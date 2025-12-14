import { useState, useEffect, useRef, useCallback } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { Layout } from '@/components/layout';
import { LoadingSpinner, ConfirmationModal } from '@/components/ui';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import {
  selectIsAuthenticated,
  selectCurrentUser,
  fetchArtistByIdAsync,
  fetchArtistAlbumsAsync,
  fetchAlbumSongsAsync,
  createArtistAsync,
  updateArtistAsync,
  deleteArtistAsync,
  deleteAlbumAsync,
  deleteSongAsync,
  selectCurrentArtist,
  selectArtistAlbums,
  selectLoadingArtist,
  selectLoadingAlbums,
  clearCurrentArtist,
  clearCurrentAlbum,
  showError,
} from '@/store/slices';
import { imageRepository } from '@/repositories';
import { validateMusicEditorForm } from '@/utils/validationSchemas';
import type { ModArtistFormData, ModAlbumFormData, ModSongFormData } from '@/types/forms';

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
  const dispatch = useAppDispatch();
  
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);
  const currentArtist = useAppSelector(selectCurrentArtist);
  const artistAlbums = useAppSelector(selectArtistAlbums);
  const loadingArtist = useAppSelector(selectLoadingArtist);
  const loadingAlbums = useAppSelector(selectLoadingAlbums);

  const routerReady = router.isReady;
  const { artistId, albumId, songId } = router.query;
  const isEditMode = routerReady && !!artistId;
  const artistIdNum = routerReady && artistId ? parseInt(artistId as string) : null;
  const focusAlbumId = routerReady && albumId ? parseInt(albumId as string) : null;
  const focusSongId = routerReady && songId ? parseInt(songId as string) : null;
  const currentArtistIdRef = useRef<number | null>(null);
  const dataProcessedForArtistRef = useRef<number | null>(null);
  const songsLoadingStartedForRef = useRef<number | null>(null);
  const [albumsFetchedForArtist, setAlbumsFetchedForArtist] = useState<number | null>(null);
  const initialScrollDoneRef = useRef<boolean>(false);
  const [loadingSongs, setLoadingSongs] = useState(false);
  const [albumSongsMap, setAlbumSongsMap] = useState<Record<number, ModSongFormData[]>>({});

  const [formData, setFormData] = useState<ModArtistFormData>({
    name: '',
    bio: '',
    albums: [],
  });

  const [loading, setLoading] = useState(false);
  const [dataLoaded, setDataLoaded] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});
  
  // Confirmation modal state
  const [deleteAlbumModal, setDeleteAlbumModal] = useState<{ isOpen: boolean; albumIndex: number | null }>({
    isOpen: false,
    albumIndex: null,
  });
  const [deleteSongModal, setDeleteSongModal] = useState<{ isOpen: boolean; albumIndex: number | null; songIndex: number | null }>({
    isOpen: false,
    albumIndex: null,
    songIndex: null,
  });
  const [deleteArtistModal, setDeleteArtistModal] = useState(false);
  const [deletingArtist, setDeletingArtist] = useState(false);

  // Redirect if not authenticated or not moderator
  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/landing');
    } else if (currentUser && !currentUser.moderator) {
      router.push('/');
    }
  }, [isAuthenticated, currentUser, router]);

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      dispatch(clearCurrentArtist());
      dispatch(clearCurrentAlbum());
    };
  }, [dispatch]);

  // Load artist and albums data using Redux thunks
  useEffect(() => {
    if (!routerReady) return;
    
    currentArtistIdRef.current = artistIdNum;
    dataProcessedForArtistRef.current = null;
    songsLoadingStartedForRef.current = null;
    initialScrollDoneRef.current = false;
    setAlbumsFetchedForArtist(null);
    
    setFormData({
      name: '',
      bio: '',
      albums: [],
    });
    setErrors({});
    setDataLoaded(false);
    setAlbumSongsMap({});
    setLoadingSongs(false);

    if (!artistIdNum || !isEditMode) {
      setDataLoaded(true);
      return;
    }
    
    dispatch(clearCurrentArtist());
    
    dispatch(fetchArtistByIdAsync(artistIdNum));
    dispatch(fetchArtistAlbumsAsync({ artistId: artistIdNum, page: 1, size: 100 }))
      .then(() => {
        if (currentArtistIdRef.current === artistIdNum) {
          setAlbumsFetchedForArtist(artistIdNum);
        }
      });
  }, [routerReady, artistIdNum, isEditMode, dispatch]);

  useEffect(() => {
    const loadAlbumSongs = async () => {
      if (!routerReady || !isEditMode || !artistIdNum) return;
      
      if (loadingArtist || !currentArtist) return;
      
      if (currentArtist.id !== artistIdNum) return;
      
      if (loadingAlbums) return;
      
      if (albumsFetchedForArtist !== artistIdNum) return;
      
      if (songsLoadingStartedForRef.current === artistIdNum) return;
      
      if (currentArtistIdRef.current !== artistIdNum) return;
      
      const albumsForCurrentArtist = artistAlbums.filter(album => album.artist_id === artistIdNum);
      
      if (albumsForCurrentArtist.length === 0) {
        songsLoadingStartedForRef.current = artistIdNum;
        return;
      }

      songsLoadingStartedForRef.current = artistIdNum;
      setLoadingSongs(true);
      const songsMap: Record<number, ModSongFormData[]> = {};

      for (const album of albumsForCurrentArtist) {
        try {
          const result = await dispatch(fetchAlbumSongsAsync({ albumId: album.id, page: 1, size: 100 })).unwrap();
          
          if (currentArtistIdRef.current !== artistIdNum) return;

          const songs: ModSongFormData[] = result.items.map((songRes: any) => ({
            id: songRes.data.id,
            title: songRes.data.title,
            duration: formatDurationForDisplay(songRes.data.duration),
            trackNumber: songRes.data.track_number,
            albumId: songRes.data.album_id,
            deleted: false,
            _tempId: generateTempId(),
            _isCollapsed: true,
          }));
          
          songsMap[album.id] = songs;
        } catch (error) {
          console.error(`Failed to load songs for album ${album.id}:`, error);
          songsMap[album.id] = [];
        }
      }

      if (currentArtistIdRef.current !== artistIdNum) return;

      setAlbumSongsMap(songsMap);
      setLoadingSongs(false);
    };

    loadAlbumSongs();
  }, [routerReady, artistAlbums, artistIdNum, isEditMode, dispatch, loadingAlbums, loadingArtist, currentArtist, albumsFetchedForArtist]);

  useEffect(() => {
    if (!routerReady || !isEditMode || !artistIdNum) return;

    if (loadingArtist || !currentArtist) return;
    
    if (currentArtist.id !== artistIdNum) return;
    
    if (loadingAlbums) return;
    
    if (albumsFetchedForArtist !== artistIdNum) return;
    
    if (loadingSongs) return;
    
    if (dataProcessedForArtistRef.current === artistIdNum) return;
    
    const albumsForCurrentArtist = artistAlbums.filter(album => album.artist_id === artistIdNum);
    
    if (albumsForCurrentArtist.length > 0 && songsLoadingStartedForRef.current !== artistIdNum) {
      return;
    }
    
    const allAlbumsHaveSongs = albumsForCurrentArtist.every(album => albumSongsMap[album.id] !== undefined);
    if (!allAlbumsHaveSongs && albumsForCurrentArtist.length > 0) return;

    const albumsWithSongs: ModAlbumFormData[] = albumsForCurrentArtist.map(album => {
      const shouldExpand = focusAlbumId === album.id;
      const songs = albumSongsMap[album.id] || [];

      return {
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
      };
    });

    setFormData({
      id: currentArtist.id,
      name: currentArtist.name,
      bio: currentArtist.bio || '',
      artistImgId: currentArtist.image_id,
      albums: albumsWithSongs,
      _imagePreview: currentArtist.image_id ? imageRepository.getImageUrl(currentArtist.image_id) : '',
    });
    
    dataProcessedForArtistRef.current = artistIdNum;
    setDataLoaded(true);
  }, [routerReady, currentArtist, artistAlbums, albumSongsMap, loadingArtist, loadingAlbums, loadingSongs, isEditMode, artistIdNum, focusAlbumId, albumsFetchedForArtist]);

  useEffect(() => {
    if (!dataLoaded || formData.albums.length === 0) return;
    if (initialScrollDoneRef.current) return;
    
    let elementId: string | null = null;
    
    if (focusSongId && focusAlbumId) {
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
      initialScrollDoneRef.current = true;
      setTimeout(() => {
        const element = document.getElementById(elementId!);
        if (element) {
          element.scrollIntoView({ behavior: 'smooth', block: 'center' });
          element.classList.add('highlight-focus');
          setTimeout(() => element.classList.remove('highlight-focus'), 2000);
        }
      }, 300);
    }
  }, [dataLoaded, formData.albums, focusAlbumId, focusSongId]);

  // Artist image handling
  const handleArtistImageChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
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
  }, []);

  // Album management
  const addAlbum = useCallback(() => {
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
  }, []);

  const updateAlbum = useCallback((index: number, updates: Partial<ModAlbumFormData>) => {
    setFormData(prev => ({
      ...prev,
      albums: prev.albums.map((album, i) => 
        i === index ? { ...album, ...updates } : album
      ),
    }));
  }, []);

  const handleRemoveAlbumClick = useCallback((index: number) => {
    setDeleteAlbumModal({ isOpen: true, albumIndex: index });
  }, []);

  const confirmRemoveAlbum = useCallback(() => {
    if (deleteAlbumModal.albumIndex !== null) {
      setFormData(prev => ({
        ...prev,
        albums: prev.albums.map((album, i) =>
          i === deleteAlbumModal.albumIndex ? { ...album, deleted: true } : album
        ),
      }));
    }
    setDeleteAlbumModal({ isOpen: false, albumIndex: null });
  }, [deleteAlbumModal.albumIndex]);

  const toggleAlbumCollapse = useCallback((index: number) => {
    setFormData(prev => ({
      ...prev,
      albums: prev.albums.map((album, i) => 
        i === index ? { ...album, _isCollapsed: !album._isCollapsed } : album
      ),
    }));
  }, []);

  // Album image handling
  const handleAlbumImageChange = useCallback((albumIndex: number, e: React.ChangeEvent<HTMLInputElement>) => {
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
  }, [updateAlbum]);

  // Song management
  const addSong = useCallback((albumIndex: number) => {
    setFormData(prev => {
      const album = prev.albums[albumIndex];
      const newSong: ModSongFormData = {
        title: '',
        duration: '',
        trackNumber: album.songs.filter(s => !s.deleted).length + 1,
        deleted: false,
        _tempId: generateTempId(),
        _isCollapsed: false,
      };
      return {
        ...prev,
        albums: prev.albums.map((a, i) => 
          i === albumIndex ? { ...a, songs: [...a.songs, newSong] } : a
        ),
      };
    });
  }, []);

  const updateSong = useCallback((albumIndex: number, songIndex: number, updates: Partial<ModSongFormData>) => {
    setFormData(prev => ({
      ...prev,
      albums: prev.albums.map((album, aIdx) =>
        aIdx === albumIndex
          ? {
              ...album,
              songs: album.songs.map((song, sIdx) =>
                sIdx === songIndex ? { ...song, ...updates } : song
              ),
            }
          : album
      ),
    }));
  }, []);

  const handleRemoveSongClick = useCallback((albumIndex: number, songIndex: number) => {
    setDeleteSongModal({ isOpen: true, albumIndex, songIndex });
  }, []);

  const confirmRemoveSong = useCallback(() => {
    if (deleteSongModal.albumIndex !== null && deleteSongModal.songIndex !== null) {
      const { albumIndex, songIndex } = deleteSongModal;
      setFormData(prev => ({
        ...prev,
        albums: prev.albums.map((album, aIdx) =>
          aIdx === albumIndex
            ? {
                ...album,
                songs: album.songs.map((song, sIdx) =>
                  sIdx === songIndex ? { ...song, deleted: true } : song
                ),
              }
            : album
        ),
      }));
    }
    setDeleteSongModal({ isOpen: false, albumIndex: null, songIndex: null });
  }, [deleteSongModal]);

  const handleDeleteArtistClick = useCallback(() => {
    setDeleteArtistModal(true);
  }, []);

  const confirmDeleteArtist = useCallback(async () => {
    if (!formData.id) return;
    
    setDeletingArtist(true);
    try {
      await dispatch(deleteArtistAsync(formData.id)).unwrap();
      setDeleteArtistModal(false);
      router.push('/music');
    } catch (error) {
      console.error('Failed to delete artist:', error);
      dispatch(showError(t('moderator.failedToDeleteArtist')));
    } finally {
      setDeletingArtist(false);
    }
  }, [formData.id, dispatch, router, t]);

  // Validation using Yup schemas
  const validateForm = useCallback(async (): Promise<boolean> => {
    const validationErrors = await validateMusicEditorForm(formData, t);
    setErrors(validationErrors);
    return Object.keys(validationErrors).length === 0;
  }, [formData, t]);

  // Form submission using Redux thunks
  const handleSubmit = useCallback(async (e: React.FormEvent) => {
    e.preventDefault();

    const isValid = await validateForm();
    if (!isValid) {
      return;
    }

    setLoading(true);
    try {
      // First, delete songs marked as deleted (only existing ones with ID)
      for (const album of formData.albums) {
        for (const song of album.songs) {
          if (song.deleted && song.id) {
            await dispatch(deleteSongAsync(song.id)).unwrap();
          }
        }
      }

      // Then, delete albums marked as deleted (only existing ones with ID)
      for (const album of formData.albums) {
        if (album.deleted && album.id) {
          await dispatch(deleteAlbumAsync(album.id)).unwrap();
        }
      }

      // Upload artist image if new
      let artistImgId = formData.artistImgId;
      if (formData._imageFile) {
        artistImgId = await imageRepository.uploadImage(formData._imageFile);
      }

      // Upload album images (only for non-deleted albums)
      const albumsWithImages = await Promise.all(
        formData.albums
          .filter(album => !album.deleted)
          .map(async (album) => {
            let albumImageId = album.albumImageId;
            if (album._imageFile) {
              albumImageId = await imageRepository.uploadImage(album._imageFile);
            }
            return { ...album, albumImageId };
          })
      );

      // Prepare payload matching ModArtistForm structure (excluding deleted items)
      const payload = {
        id: formData.id,
        name: formData.name.trim(),
        bio: formData.bio?.trim() || null,
        artistImgId,
        deleted: false,
        albums: albumsWithImages.map(album => ({
          id: album.id,
          title: album.title.trim(),
          genre: album.genre?.trim() || null,
          releaseDate: album.releaseDate || null,
          albumImageId: album.albumImageId,
          artistId: formData.id,
          deleted: false,
          songs: album.songs
            .filter(song => !song.deleted)
            .map(song => ({
              id: song.id,
              title: song.title.trim(),
              duration: song.duration,
              trackNumber: song.trackNumber,
              albumId: album.id,
              deleted: false,
            })),
        })),
      };

      // Use Redux thunks for API calls
      if (isEditMode && formData.id) {
        await dispatch(updateArtistAsync({ id: formData.id, artistData: payload as any })).unwrap();
        router.push(`/artists/${formData.id}`);
      } else {
        const result = await dispatch(createArtistAsync(payload as any)).unwrap();
        router.push(`/artists/${result.data.id}`);
      }
    } catch (error: any) {
      console.error('Failed to save artist:', error);
      dispatch(showError(t('moderator.failedToSaveArtist')));
      setErrors({ general: t('moderator.failedToSaveArtist') });
    } finally {
      setLoading(false);
    }
  }, [formData, isEditMode, validateForm, dispatch, router, t]);

  if (!isAuthenticated || (currentUser && !currentUser.moderator)) {
    return null;
  }

  // Determine if we're still loading (include waiting for router)
  const isLoading = !routerReady || loadingArtist || loadingAlbums || loadingSongs || (isEditMode && !dataLoaded);

  // Show spinner while loading data in edit mode
  if (isLoading) {
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
                  src={formData._imagePreview || '/assets/image-placeholder.png'}
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
                          src={album._imagePreview || '/assets/image-placeholder.png'}
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
                          onClick={(e) => { e.stopPropagation(); handleRemoveAlbumClick(albumIndex); }}
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
                              src={album._imagePreview || '/assets/image-placeholder.png'}
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
                                    onClick={() => handleRemoveSongClick(albumIndex, songIndex)}
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
              onClick={() => router.back()}
              disabled={loading || deletingArtist}
            >
              {t('common.cancel')}
            </button>
            {isEditMode && formData.id && (
              <button
                type="button"
                className="btn btn-danger"
                onClick={handleDeleteArtistClick}
                disabled={loading || deletingArtist}
              >
                {deletingArtist ? t('common.loading') : t('moderator.deleteArtist')}
              </button>
            )}
            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading || deletingArtist}
            >
              {loading ? t('common.loading') : isEditMode ? t('moderator.saveChanges') : t('moderator.createArtist')}
            </button>
          </div>
        </form>
      </div>

      {/* Delete Album Confirmation Modal */}
      <ConfirmationModal
        isOpen={deleteAlbumModal.isOpen}
        message={t('moderator.confirmRemoveAlbum')}
        confirmText={t('common.delete')}
        cancelText={t('common.cancel')}
        onConfirm={confirmRemoveAlbum}
        onCancel={() => setDeleteAlbumModal({ isOpen: false, albumIndex: null })}
      />

      {/* Delete Song Confirmation Modal */}
      <ConfirmationModal
        isOpen={deleteSongModal.isOpen}
        message={t('moderator.confirmRemoveSong')}
        confirmText={t('common.delete')}
        cancelText={t('common.cancel')}
        onConfirm={confirmRemoveSong}
        onCancel={() => setDeleteSongModal({ isOpen: false, albumIndex: null, songIndex: null })}
      />

      {/* Delete Artist Confirmation Modal */}
      <ConfirmationModal
        isOpen={deleteArtistModal}
        message={t('moderator.confirmDeleteArtist')}
        confirmText={t('common.delete')}
        cancelText={t('common.cancel')}
        onConfirm={confirmDeleteArtist}
        onCancel={() => setDeleteArtistModal(false)}
      />
    </Layout>
  );
}
