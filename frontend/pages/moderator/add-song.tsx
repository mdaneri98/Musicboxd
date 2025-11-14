import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { Layout } from '@/components/layout';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { selectIsAuthenticated, selectCurrentUser, fetchAlbumByIdAsync, selectCurrentAlbum, createSongAsync } from '@/store/slices';
import { Album } from '@/types/models';
import { CreateSongFormData } from '@/types/forms';

export default function AddSongPage() {
  const router = useRouter();
  const { albumId: albumIdParam } = router.query;
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  const album = useAppSelector(selectCurrentAlbum) as Album;
  const dispatch = useAppDispatch();
  const [title, setTitle] = useState('');
  const [duration, setDuration] = useState('');
  const [trackNumber, setTrackNumber] = useState('');
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<{ title?: string; duration?: string; trackNumber?: string; album?: string }>({});

  // Redirect if not authenticated or not moderator
  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/landing');
    } else if (currentUser && !currentUser.moderator) {
      router.push('/');
    }
  }, [isAuthenticated, currentUser, router]);

  // Load album info if albumId is provided
  useEffect(() => {
    const fetchAlbum = async () => {
      if (albumIdParam) {
        try {
          const id = parseInt(albumIdParam as string);
          await dispatch(fetchAlbumByIdAsync(id)).unwrap();
        } catch (error) {
          console.error('Failed to fetch album:', error);
          setErrors({ album: 'Failed to load album information' });
        }
      }
    };

    fetchAlbum();
  }, [albumIdParam]);

  // Parse duration string (MM:SS) to seconds
  const parseDurationToSeconds = (durationStr: string): number => {
    const parts = durationStr.split(':');
    if (parts.length === 2) {
      const minutes = parseInt(parts[0], 10);
      const seconds = parseInt(parts[1], 10);
      return minutes * 60 + seconds;
    }
    return 0;
  };

  // Handle form submission
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrors({});

    // Basic validation
    const newErrors: { title?: string; duration?: string; trackNumber?: string; album?: string } = {};
    if (!album) {
      newErrors.album = 'Album is required';
    }
    if (!title.trim()) {
      newErrors.title = 'Title is required';
    }
    if (!duration.trim()) {
      newErrors.duration = 'Duration is required';
    } else if (!/^\d+:\d{2}$/.test(duration)) {
      newErrors.duration = 'Duration must be in format MM:SS';
    }
    if (trackNumber && isNaN(parseInt(trackNumber))) {
      newErrors.trackNumber = 'Track number must be a number';
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    try {
      setLoading(true);

      // Create song
      const songData = {
        title: title.trim(),
        duration: parseDurationToSeconds(duration),
        trackNumber: trackNumber ? parseInt(trackNumber) : undefined,
        albumId: album.id,
      } as CreateSongFormData;

      const newSong = await dispatch(createSongAsync(songData as CreateSongFormData)).unwrap();

      // Redirect to song page
      router.push(`/songs/${newSong.data?.id}`);
    } catch (error) {
      console.error('Failed to create song:', error);
      setErrors({ title: 'Failed to create song. Please try again.' });
    } finally {
      setLoading(false);
    }
  };

  if (!isAuthenticated || (currentUser && !currentUser.moderator)) {
    return null; // Will redirect in useEffect
  }

  return (
    <Layout title="Add Song - Musicboxd">
      <div className="mod-form-container">
        <h1 className="mod-form-title">Add Song</h1>

        {album.title && (
          <p className="mod-form-subtitle">
            for album: <strong>{album.title}</strong>
          </p>
        )}

        {errors.album && <div className="error" style={{ color: 'red', marginBottom: '1rem' }}>{errors.album}</div>}

        <form onSubmit={handleSubmit}>
          <div className="mod-form">
            <div className="mod-entity-details">
              {/* Title */}
              <div>
                <label className="mod-label">
                  Title:
                  {errors.title && <span className="error" style={{ color: 'red' }}> {errors.title}</span>}
                  <input
                    type="text"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    className="mod-input"
                    required
                  />
                </label>
              </div>

              {/* Duration */}
              <div>
                <label className="mod-label">
                  Duration (MM:SS):
                  {errors.duration && <span className="error" style={{ color: 'red' }}> {errors.duration}</span>}
                  <input
                    type="text"
                    value={duration}
                    onChange={(e) => setDuration(e.target.value)}
                    className="mod-input"
                    placeholder="3:45"
                    required
                  />
                </label>
              </div>

              {/* Track Number */}
              <div>
                <label className="mod-label">
                  Track Number (optional):
                  {errors.trackNumber && <span className="error" style={{ color: 'red' }}> {errors.trackNumber}</span>}
                  <input
                    type="number"
                    value={trackNumber}
                    onChange={(e) => setTrackNumber(e.target.value)}
                    className="mod-input"
                    min="1"
                  />
                </label>
              </div>
            </div>
          </div>

          <div className="form-actions">
            {/* Cancel Button */}
            <button
              type="button"
              onClick={() => router.push('/moderator')}
              className="btn btn-secondary"
              disabled={loading}
            >
              Cancel
            </button>

            {/* Submit Button */}
            <button type="submit" className="btn btn-primary" disabled={loading || !album.id}>
              {loading ? 'Creating...' : 'Create Song'}
            </button>
          </div>
        </form>
      </div>
    </Layout>
  );
}

