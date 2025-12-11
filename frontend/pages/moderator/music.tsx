import { useState, useEffect, useRef } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { Layout } from '@/components/layout';
import { LoadingSpinner } from '@/components/ui';
import { useAppSelector } from '@/store/hooks';
import {
  selectIsAuthenticated,
  selectCurrentUser,
} from '@/store/slices';
import { imageRepository, artistRepository, albumRepository } from '@/repositories';
import type { ModArtistFormData, ModAlbumFormData, ModSongFormData } from '@/types/forms';
import type { Song, Album, Artist, HALResource } from '@/types';

// Generate unique temporary IDs for new items
const generateTempId = () => `temp_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;

// Convert duration string (MM:SS or HH:MM:SS) to display format
const formatDurationForDisplay = (duration: string): string => {
  if (!duration) return '';
  // If already in MM:SS format, return as is
  if (/^\d{1,2}:\d{2}$/.test(duration)) return duration;
  // If in seconds, convert to MM:SS
  const seconds = parseInt(duration);
  if (!isNaN(seconds)) {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }
  return duration;
};

export default function MusicEditorPage() {
  const { t } = useTranslation();
  const router = useRouter();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  const { artistId, albumId, songId } = router.query;
  const isEditMode = !!artistId;
  const focusAlbumId = albumId ? parseInt(albumId as string) : null;
  const focusSongId = songId ? parseInt(songId as string) : null;

  // Track current artistId to prevent race conditions
  const currentArtistIdRef = useRef<string | null>(null);

  // Form state
  const [formData, setFormData] = useState<ModArtistFormData>({
    name: '',
    bio: '',
    albums: [],
  });

  const [loading, setLoading] = useState(false);
  const [loadingData, setLoadingData] = useState(false);
  const [dataLoaded, setDataLoaded] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  // Redirect if not authenticated or not moderator
  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/landing');
    } else if (currentUser && !currentUser.moderator) {
      router.push('/');
    }
  }, [isAuthenticated, currentUser, router]);

  // Load all artist data in a single effect - no Redux dependency
  useEffect(() => {
    const loadArtistData = async () => {
      // Reset state when artistId changes
      const currentId = artistId as string | undefined;
      currentArtistIdRef.current = currentId || null;
      
      // Reset form for new artist or create mode
      setFormData({
        name: '',
        bio: '',
        albums: [],
      });
      setErrors({});
      setDataLoaded(false);

      if (!currentId || !isEditMode) {
        // Create mode - no data to load
        setDataLoaded(true);
        return;
      }

      setLoadingData(true);
      try {
        const artistIdNum = parseInt(currentId);
        
        // Fetch artist directly from repository (not Redux)
        const artistResponse = await artistRepository.getArtistById(artistIdNum);
        const artist: Artist = artistResponse.data;
        
        // Check if artistId changed during fetch (race condition prevention)
        if (currentArtistIdRef.current !== currentId) {
          return; // Abort - a new artistId was requested
        }
        
        // Fetch albums directly from repository
        const albumsResponse = await artistRepository.getArtistAlbums(artistIdNum, 1, 100);
        const albums: Album[] = albumsResponse.items.map((item: HALResource<Album>) => item.data);
        
        // Check again for race condition
        if (currentArtistIdRef.current !== currentId) {
          return;
        }

        // Now fetch songs for each album
        const albumsWithSongs: ModAlbumFormData[] = [];
        
        for (const album of albums) {
          let songs: ModSongFormData[] = [];
          
          try {
            const songsData = await albumRepository.getAlbumSongs(album.id, 1, 100);
            const songItems = songsData.items || [];
            songs = songItems.map((songRes: HALResource<Song>) => ({
              id: songRes.data.id,
              title: songRes.data.title,
              duration: formatDurationForDisplay(songRes.data.duration),
              trackNumber: songRes.data.track_number,
              albumId: songRes.data.album_id,
              deleted: false,
              _tempId: generateTempId(),
              _isCollapsed: true,
            }));
          } catch (songError) {
            console.error(`Failed to load songs for album ${album.id}:`, songError);
          }

          // Check for race condition after each album fetch
          if (currentArtistIdRef.current !== currentId) {
            return;
          }

          const shouldExpand = focusAlbumId === album.id;
          
          albumsWithSongs.push({
            id: album.id,
            title: album.title,
            genre: album.genre || '',
            releaseDate: album.release_date ? new Date(album.release_date).toISOString().split('T')[0] : '',
            albumImageId: album.image_id,
            artistId: album.artist_id,
            songs,
            deleted: false,
            _tempId: generateTempId(),
            _isCollapsed: !shouldExpand,
            _imagePreview: album.image_id ? imageRepository.getImageUrl(album.image_id) : '',
          });
        }

        // Final race condition check before setting state
        if (currentArtistIdRef.current !== currentId) {
          return;
        }

        // Set form data only when ALL data is ready
        setFormData({
          id: artist.id,
          name: artist.name,
          bio: artist.bio || '',
          artistImgId: artist.image_id,
          albums: albumsWithSongs,
          _imagePreview: artist.image_id ? imageRepository.getImageUrl(artist.image_id) : '',
        });
        setDataLoaded(true);
      } catch (error) {
        console.error('Failed to load artist data:', error);
        if (currentArtistIdRef.current === currentId) {
          setErrors({ general: t('moderator.failedToLoadArtist') });
        }
      } finally {
        if (currentArtistIdRef.current === currentId) {
          setLoadingData(false);
        }
      }
    };

    loadArtistData();
  }, [artistId, isEditMode, focusAlbumId, t]);

  // Scroll to focused album or song after data loads
  useEffect(() => {
    if (!loadingData && formData.albums.length > 0) {
      let elementId: string | null = null;
      
      if (focusSongId && focusAlbumId) {
        // Find the album index and song index
        const albumIndex = formData.albums.findIndex(a => a.id === focusAlbumId);
        if (albumIndex !== -1) {
          const songIndex = formData.albums[albumIndex].songs.findIndex(s => s.id === focusSongId);
          if (songIndex !== -1) {
            elementId = `song_${albumIndex}_${songIndex}`;
          }
        }
      } else if (focusAlbumId) {
        const albumIndex = formData.albums.findIndex(a => a.id === focusAlbumId);
        if (albumIndex !== -1) {
          elementId = `album_${albumIndex}`;
        }
      }
      
      if (elementId) {
        setTimeout(() => {
          const element = document.getElementById(elementId!);
          if (element) {
            element.scrollIntoView({ behavior: 'smooth', block: 'center' });
            element.classList.add('highlight-focus');
            setTimeout(() => element.classList.remove('highlight-focus'), 2000);
          }
        }, 300);
      }
    }
  }, [loadingData, formData.albums, focusAlbumId, focusSongId]);

  // Artist image handling
  const handleArtistImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setFormData(prev => ({
          ...prev,
          _imageFile: file,
          _imagePreview: reader.result as string,
        }));
      };
      reader.readAsDataURL(file);
    }
  };

  // Album management
  const addAlbum = () => {
    const newAlbum: ModAlbumFormData = {
      title: '',
      genre: '',
      releaseDate: '',
      songs: [],
      deleted: false,
      _tempId: generateTempId(),
      _isCollapsed: false,
    };
    setFormData(prev => ({
      ...prev,
      albums: [...prev.albums, newAlbum],
    }));
  };

  const updateAlbum = (index: number, updates: Partial<ModAlbumFormData>) => {
    setFormData(prev => ({
      ...prev,
      albums: prev.albums.map((album, i) => 
        i === index ? { ...album, ...updates } : album
      ),
    }));
  };

  const removeAlbum = (index: number) => {
    setFormData(prev => ({
      ...prev,
      albums: prev.albums.map((album, i) =>
        i === index ? { ...album, deleted: true } : album
      ),
    }));
  };

  const toggleAlbumCollapse = (index: number) => {
    updateAlbum(index, { _isCollapsed: !formData.albums[index]._isCollapsed });
  };

  // Album image handling
  const handleAlbumImageChange = (albumIndex: number, e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        updateAlbum(albumIndex, {
          _imageFile: file,
          _imagePreview: reader.result as string,
        });
      };
      reader.readAsDataURL(file);
    }
  };

  // Song management
  const addSong = (albumIndex: number) => {
    const album = formData.albums[albumIndex];
    const newSong: ModSongFormData = {
      title: '',
      duration: '',
      trackNumber: album.songs.filter(s => !s.deleted).length + 1,
      deleted: false,
      _tempId: generateTempId(),
      _isCollapsed: false,
    };
    updateAlbum(albumIndex, {
      songs: [...album.songs, newSong],
    });
  };

  const updateSong = (albumIndex: number, songIndex: number, updates: Partial<ModSongFormData>) => {
    const album = formData.albums[albumIndex];
    updateAlbum(albumIndex, {
      songs: album.songs.map((song, i) =>
        i === songIndex ? { ...song, ...updates } : song
      ),
    });
  };

  const removeSong = (albumIndex: number, songIndex: number) => {
    const album = formData.albums[albumIndex];
    updateAlbum(albumIndex, {
      songs: album.songs.map((song, i) =>
        i === songIndex ? { ...song, deleted: true } : song
      ),
    });
  };

  // Validation
  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.name.trim()) {
      newErrors.name = t('moderator.validation.nameRequired');
    } else if (formData.name.length < 2 || formData.name.length > 50) {
      newErrors.name = t('moderator.validation.nameLength');
    }

    if (formData.bio && formData.bio.length > 2048) {
      newErrors.bio = t('moderator.validation.bioLength');
    }

    // Validate albums
    formData.albums.forEach((album, albumIndex) => {
      if (album.deleted) return;

      if (!album.title.trim()) {
        newErrors[`album_${albumIndex}_title`] = t('moderator.validation.albumTitleRequired');
      } else if (album.title.length < 2 || album.title.length > 100) {
        newErrors[`album_${albumIndex}_title`] = t('moderator.validation.albumTitleLength');
      }

      // Validate songs
      album.songs.forEach((song, songIndex) => {
        if (song.deleted) return;

        if (!song.title.trim()) {
          newErrors[`album_${albumIndex}_song_${songIndex}_title`] = t('moderator.validation.songTitleRequired');
        } else if (song.title.length < 1 || song.title.length > 100) {
          newErrors[`album_${albumIndex}_song_${songIndex}_title`] = t('moderator.validation.songTitleLength');
        }

        if (!song.duration || !/^(?:(?:([0-9]{1,2}):)?([0-5]?[0-9]):)?([0-5][0-9])$/.test(song.duration)) {
          newErrors[`album_${albumIndex}_song_${songIndex}_duration`] = t('moderator.validation.songDurationFormat');
        }
      });
    });

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // Form submission
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setLoading(true);
    try {
      // Upload artist image if new
      let artistImgId = formData.artistImgId;
      if (formData._imageFile) {
        artistImgId = await imageRepository.uploadImage(formData._imageFile);
      }

      // Upload album images
      const albumsWithImages = await Promise.all(
        formData.albums.map(async (album) => {
          let albumImageId = album.albumImageId;
          if (album._imageFile) {
            albumImageId = await imageRepository.uploadImage(album._imageFile);
          }
          return { ...album, albumImageId };
        })
      );

      // Prepare payload matching ModArtistForm structure
      const payload = {
        id: formData.id,
        name: formData.name.trim(),
        bio: formData.bio?.trim() || null,
        artistImgId,
        deleted: false,
        albums: albumsWithImages
          .filter(album => !album.deleted || album.id) // Include deleted albums only if they have an ID (existing)
          .map(album => ({
            id: album.id,
            title: album.title.trim(),
            genre: album.genre?.trim() || null,
            releaseDate: album.releaseDate || null,
            albumImageId: album.albumImageId,
            artistId: formData.id,
            deleted: album.deleted || false,
            songs: album.songs
              .filter(song => !song.deleted || song.id) // Include deleted songs only if they have an ID
              .map(song => ({
                id: song.id,
                title: song.title.trim(),
                duration: song.duration,
                trackNumber: song.trackNumber,
                albumId: album.id,
                deleted: song.deleted || false,
              })),
          })),
      };

      // Make API call
      if (isEditMode && formData.id) {
        await artistRepository.updateArtist(formData.id, payload as any);
        router.push(`/artists/${formData.id}`);
      } else {
        const newArtist = await artistRepository.createArtist(payload as any);
        router.push(`/artists/${newArtist.data.id}`);
      }
    } catch (error) {
      console.error('Failed to save artist:', error);
      setErrors({ general: t('moderator.failedToSaveArtist') });
    } finally {
      setLoading(false);
    }
  };

  if (!isAuthenticated || (currentUser && !currentUser.moderator)) {
    return null;
  }

  // Show spinner while loading data in edit mode
  if (loadingData || (isEditMode && !dataLoaded)) {
    return (
      <Layout title={t('moderator.musicEditor')}>
        <div className="content-wrapper">
          <LoadingSpinner />
        </div>
      </Layout>
    );
  }

  const visibleAlbums = formData.albums.filter(album => !album.deleted);

  return (
    <Layout title={isEditMode ? t('moderator.editArtist') : t('moderator.addArtist')}>
      <div className="music-editor-container">
        <h1 className="music-editor-title">
          {isEditMode ? t('moderator.editArtist') : t('moderator.addArtist')}
        </h1>

        {errors.general && (
          <div className="alert alert-danger">{errors.general}</div>
        )}

        <form onSubmit={handleSubmit}>
          {/* Artist Section */}
          <section className="music-editor-section artist-section">
            <div className="section-header">
              <h2>{t('moderator.artistInfo')}</h2>
            </div>

            <div className="artist-form-content">
              {/* Artist Image */}
              <div className="image-upload-container">
                <img
                  src={formData._imagePreview || '/images/default-artist.png'}
                  alt={t('moderator.artistImage')}
                  className="entity-image mod-editable-image"
                  onClick={() => document.getElementById('artistImageInput')?.click()}
                />
                <input
                  id="artistImageInput"
                  type="file"
                  accept=".jpg,.jpeg,.png"
                  className="hidden-input"
                  onChange={handleArtistImageChange}
                />
                <span className="image-upload-hint">{t('moderator.clickToUploadImage')}</span>
              </div>

              {/* Artist Fields */}
              <div className="entity-fields">
                <div className="form-group">
                  <label htmlFor="artistName" className="form-label">
                    {t('moderator.artistName')} *
                  </label>
                  <input
                    id="artistName"
                    type="text"
                    className={`form-input ${errors.name ? 'input-error' : ''}`}
                    value={formData.name}
                    onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
                    placeholder={t('moderator.enterArtistName')}
                  />
                  {errors.name && <span className="error-message">{errors.name}</span>}
                </div>

                <div className="form-group">
                  <label htmlFor="artistBio" className="form-label">
                    {t('moderator.bio')}
                  </label>
                  <textarea
                    id="artistBio"
                    className={`form-input form-textarea ${errors.bio ? 'input-error' : ''}`}
                    value={formData.bio}
                    onChange={(e) => setFormData(prev => ({ ...prev, bio: e.target.value }))}
                    placeholder={t('moderator.enterBio')}
                    rows={4}
                  />
                  {errors.bio && <span className="error-message">{errors.bio}</span>}
                </div>
              </div>
            </div>
          </section>

          {/* Albums Section */}
          <section className="music-editor-section albums-section">
            <div className="section-header">
              <h2>{t('moderator.albums')} ({visibleAlbums.length})</h2>
              <button
                type="button"
                className="btn btn-secondary btn-add"
                onClick={addAlbum}
              >
                <i className="fa-solid fa-plus"></i>
                {t('moderator.addAlbum')}
              </button>
            </div>

            <div className="albums-list">
              {formData.albums.map((album, albumIndex) => {
                if (album.deleted) return null;
                const visibleSongs = album.songs.filter(s => !s.deleted);

                return (
                  <div key={album._tempId || album.id} id={`album_${albumIndex}`} className="album-card">
                    {/* Album Header */}
                    <div className="album-header" onClick={() => toggleAlbumCollapse(albumIndex)}>
                      <div className="album-header-info">
                        <img
                          src={album._imagePreview || '/images/default-album.png'}
                          alt={album.title || t('moderator.newAlbum')}
                          className="album-thumbnail"
                        />
                        <div className="album-header-text">
                          <h3 className="album-title">
                            {album.title || t('moderator.newAlbum')}
                          </h3>
                          <span className="album-song-count">
                            {visibleSongs.length} {t('moderator.songs')}
                          </span>
                        </div>
                      </div>
                      <div className="album-header-actions">
                        <button
                          type="button"
                          className="btn-icon btn-delete"
                          onClick={(e) => { e.stopPropagation(); removeAlbum(albumIndex); }}
                          title={t('moderator.removeAlbum')}
                        >
                          <i className="fa-solid fa-trash"></i>
                        </button>
                        <i className={`fa-solid fa-chevron-${album._isCollapsed ? 'down' : 'up'}`}></i>
                      </div>
                    </div>

                    {/* Album Content (collapsible) */}
                    {!album._isCollapsed && (
                      <div className="album-content">
                        <div className="album-form-content">
                          {/* Album Image */}
                          <div className="image-upload-container image-upload-small">
                            <img
                              src={album._imagePreview || '/images/default-album.png'}
                              alt={t('moderator.albumImage')}
                              className="sub-element-image-preview mod-editable-image"
                              onClick={() => document.getElementById(`albumImageInput_${albumIndex}`)?.click()}
                            />
                            <input
                              id={`albumImageInput_${albumIndex}`}
                              type="file"
                              accept=".jpg,.jpeg,.png"
                              className="hidden-input"
                              onChange={(e) => handleAlbumImageChange(albumIndex, e)}
                            />
                          </div>

                          {/* Album Fields */}
                          <div className="entity-fields">
                            <div className="form-row">
                              <div className="form-group">
                                <label className="form-label">
                                  {t('moderator.albumTitle')} *
                                </label>
                                <input
                                  type="text"
                                  className={`form-input ${errors[`album_${albumIndex}_title`] ? 'input-error' : ''}`}
                                  value={album.title}
                                  onChange={(e) => updateAlbum(albumIndex, { title: e.target.value })}
                                  placeholder={t('moderator.enterAlbumTitle')}
                                />
                                {errors[`album_${albumIndex}_title`] && (
                                  <span className="error-message">{errors[`album_${albumIndex}_title`]}</span>
                                )}
                              </div>

                              <div className="form-group">
                                <label className="form-label">
                                  {t('moderator.genre')}
                                </label>
                                <input
                                  type="text"
                                  className="form-input"
                                  value={album.genre || ''}
                                  onChange={(e) => updateAlbum(albumIndex, { genre: e.target.value })}
                                  placeholder={t('moderator.enterGenre')}
                                />
                              </div>
                            </div>

                            <div className="form-group">
                              <label className="form-label">
                                {t('moderator.releaseDate')}
                              </label>
                              <input
                                type="date"
                                className="form-input"
                                value={album.releaseDate || ''}
                                onChange={(e) => updateAlbum(albumIndex, { releaseDate: e.target.value })}
                              />
                            </div>
                          </div>
                        </div>

                        {/* Songs Section */}
                        <div className="songs-section">
                          <div className="songs-header">
                            <h4>{t('moderator.songs')} ({visibleSongs.length})</h4>
                            <button
                              type="button"
                              className="btn btn-secondary btn-sm btn-add"
                              onClick={() => addSong(albumIndex)}
                            >
                              <i className="fa-solid fa-plus"></i>
                              {t('moderator.addSong')}
                            </button>
                          </div>

                          <div className="songs-list">
                            {album.songs.map((song, songIndex) => {
                              if (song.deleted) return null;

                              return (
                                <div key={song._tempId || song.id} id={`song_${albumIndex}_${songIndex}`} className="song-item">
                                  <div className="song-number">
                                    <input
                                      type="number"
                                      className="form-input song-track-input"
                                      value={song.trackNumber || ''}
                                      onChange={(e) => updateSong(albumIndex, songIndex, { trackNumber: parseInt(e.target.value) || undefined })}
                                      min="1"
                                      max="500"
                                      placeholder="#"
                                    />
                                  </div>

                                  <div className="song-fields">
                                    <div className="form-group">
                                      <input
                                        type="text"
                                        className={`form-input ${errors[`album_${albumIndex}_song_${songIndex}_title`] ? 'input-error' : ''}`}
                                        value={song.title}
                                        onChange={(e) => updateSong(albumIndex, songIndex, { title: e.target.value })}
                                        placeholder={t('moderator.songTitle')}
                                      />
                                      {errors[`album_${albumIndex}_song_${songIndex}_title`] && (
                                        <span className="error-message">{errors[`album_${albumIndex}_song_${songIndex}_title`]}</span>
                                      )}
                                    </div>

                                    <div className="form-group song-duration-group">
                                      <input
                                        type="text"
                                        className={`form-input ${errors[`album_${albumIndex}_song_${songIndex}_duration`] ? 'input-error' : ''}`}
                                        value={song.duration}
                                        onChange={(e) => updateSong(albumIndex, songIndex, { duration: e.target.value })}
                                        placeholder="MM:SS"
                                      />
                                      {errors[`album_${albumIndex}_song_${songIndex}_duration`] && (
                                        <span className="error-message">{errors[`album_${albumIndex}_song_${songIndex}_duration`]}</span>
                                      )}
                                    </div>
                                  </div>

                                  <button
                                    type="button"
                                    className="btn-icon btn-delete"
                                    onClick={() => removeSong(albumIndex, songIndex)}
                                    title={t('moderator.removeSong')}
                                  >
                                    <i className="fa-solid fa-times"></i>
                                  </button>
                                </div>
                              );
                            })}

                            {visibleSongs.length === 0 && (
                              <p className="no-songs-message">{t('moderator.noSongsYet')}</p>
                            )}
                          </div>
                        </div>
                      </div>
                    )}
                  </div>
                );
              })}

              {visibleAlbums.length === 0 && (
                <div className="no-albums-message">
                  <p>{t('moderator.noAlbumsYet')}</p>
                  <p className="hint">{t('moderator.clickAddAlbumHint')}</p>
                </div>
              )}
            </div>
          </section>

          {/* Form Actions */}
          <div className="form-actions">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => router.push('/moderator')}
              disabled={loading}
            >
              {t('common.cancel')}
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading}
            >
              {loading ? t('common.loading') : isEditMode ? t('moderator.saveChanges') : t('moderator.createArtist')}
            </button>
          </div>
        </form>
      </div>
    </Layout>
  );
}
