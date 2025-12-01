import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { Layout } from '@/components/layout';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { selectIsAuthenticated, selectCurrentUser, fetchAlbumByIdAsync, selectCurrentAlbum, createSongAsync } from '@/store/slices';
import { Album } from '@/types/models';
import { CreateSongFormData } from '@/types/forms';
import { songSchema } from '@/utils/validationSchemas';

// Form data type for validation (duration as string MM:SS)
interface SongFormData {
  title: string;
  duration: string;
  trackNumber?: number;
  albumId: number;
}

export default function AddSongPage() {
  const router = useRouter();
  const { albumId: albumIdParam } = router.query;
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  const album = useAppSelector(selectCurrentAlbum) as Album;
  const dispatch = useAppDispatch();

  const {
    register,
    handleSubmit,
    formState: { errors },
    setError,
    setValue,
  } = useForm<SongFormData>({
    resolver: yupResolver(songSchema) as any,
  });

  const [loading, setLoading] = useState(false);
  const [albumError, setAlbumError] = useState<string>('');

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
          setValue('albumId', id);
        } catch (error) {
          console.error('Failed to fetch album:', error);
          setAlbumError('Failed to load album information');
        }
      }
    };

    fetchAlbum();
  }, [albumIdParam, dispatch, setValue]);


  // Handle form submission
  const onSubmit = async (data: SongFormData) => {
    if (!album) {
      setAlbumError('Album is required');
      return;
    }

    try {
      setLoading(true);

      const songData: CreateSongFormData = {
        title: data.title.trim(),
        duration: data.duration,
        track_number: data.trackNumber,
        album_id: album.id,
      };

      const newSong = await dispatch(createSongAsync(songData)).unwrap();
      router.push(`/songs/${newSong.data?.id}`);
    } catch (error) {
      console.error('Failed to create song:', error);
      setError('title', { type: 'manual', message: 'Failed to create song. Please try again.' });
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

        {album?.title && (
          <p className="mod-form-subtitle">
            for album: <strong>{album.title}</strong>
          </p>
        )}

        {albumError && <div className="error" style={{ color: 'red', marginBottom: '1rem' }}>{albumError}</div>}

        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="mod-form">
            <div className="mod-entity-details">
              {/* Title */}
              <div>
                <label className="mod-label">
                  Title:
                  {errors.title && <span className="error" style={{ color: 'red' }}> {errors.title.message}</span>}
                  <input
                    type="text"
                    {...register('title')}
                    className="mod-input"
                  />
                </label>
              </div>

              {/* Duration */}
              <div>
                <label className="mod-label">
                  Duration (MM:SS):
                  {errors.duration && <span className="error" style={{ color: 'red' }}> {errors.duration.message}</span>}
                  <input
                    type="text"
                    {...register('duration')}
                    className="mod-input"
                    placeholder="3:45"
                  />
                </label>
              </div>

              {/* Track Number */}
              <div>
                <label className="mod-label">
                  Track Number (optional):
                  {errors.trackNumber && <span className="error" style={{ color: 'red' }}> {errors.trackNumber.message}</span>}
                  <input
                    type="number"
                    {...register('trackNumber')}
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
            <button type="submit" className="btn btn-primary" disabled={loading || !album?.id}>
              {loading ? 'Creating...' : 'Create Song'}
            </button>
          </div>
        </form>
      </div>
    </Layout>
  );
}

