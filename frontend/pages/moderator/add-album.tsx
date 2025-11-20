import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { Layout } from '@/components/layout';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { selectIsAuthenticated, selectCurrentUser, fetchArtistByIdAsync, createAlbumAsync } from '@/store/slices';
import { CreateAlbumFormData } from '@/types/forms';
import { imageRepository } from '@/repositories';

export default function AddAlbumPage() {
  const router = useRouter();
  const { artistId: artistIdParam } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  const [artistId, setArtistId] = useState<number | null>(null);
  const [artistName, setArtistName] = useState<string>('');
  const [title, setTitle] = useState('');
  const [genre, setGenre] = useState('');
  const [releaseDate, setReleaseDate] = useState('');
  const [imageFile, setImageFile] = useState<File | null>(null);
  const [imagePreview, setImagePreview] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<{ title?: string; genre?: string; releaseDate?: string; artist?: string }>({});

  // Redirect if not authenticated or not moderator
  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/landing');
    } else if (currentUser && !currentUser.moderator) {
      router.push('/');
    }
  }, [isAuthenticated, currentUser, router]);

  // Load artist info if artistId is provided
  useEffect(() => {
    if (artistIdParam) {
      const id = parseInt(artistIdParam as string);
      dispatch(fetchArtistByIdAsync(id))
        .unwrap()
        .then((artist) => {
          setArtistId(id);
          setArtistName(artist.data.name);
        })
        .catch((error) => {
          console.error('Failed to fetch artist:', error);
          setErrors({ artist: 'Failed to load artist information' });
        });
    }
  }, [artistIdParam, dispatch]);

  // Handle image file selection
  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setImageFile(file);
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagePreview(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  // Handle form submission
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrors({});

    // Basic validation
    const newErrors: { title?: string; genre?: string; releaseDate?: string; artist?: string } = {};
    if (!artistId) {
      newErrors.artist = 'Artist is required';
    }
    if (!title.trim()) {
      newErrors.title = 'Title is required';
    }
    if (!genre.trim()) {
      newErrors.genre = 'Genre is required';
    }
    if (!releaseDate) {
      newErrors.releaseDate = 'Release date is required';
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    try {
      setLoading(true);

      // Upload image and get ID
      let albumImageId: number | undefined;
      if (imageFile) {
        try {
          albumImageId = await imageRepository.uploadImage(imageFile);
        } catch (error) {
          console.error('Failed to upload image:', error);
          setErrors({ title: 'Failed to upload image. Please try again.' });
          return;
        }
      }

      const albumData: CreateAlbumFormData = {
        title: title.trim(),
        genre: genre.trim(),
        release_date: releaseDate,
        artist_id: artistId!,
        album_image_id: albumImageId,
      };

      const newAlbum = await dispatch(createAlbumAsync(albumData)).unwrap();
      router.push(`/albums/${newAlbum.data?.id}`);
    } catch (error) {
      console.error('Failed to create album:', error);
      setErrors({ title: 'Failed to create album. Please try again.' });
    } finally {
      setLoading(false);
    }
  };

  if (!isAuthenticated || (currentUser && !currentUser.moderator)) {
    return null; // Will redirect in useEffect
  }

  return (
    <Layout title="Add Album - Musicboxd">
      <div className="mod-form-container">
        <h1 className="mod-form-title">Add Album</h1>

        {artistName && (
          <p className="mod-form-subtitle">
            for artist: <strong>{artistName}</strong>
          </p>
        )}

        {errors.artist && <div className="error" style={{ color: 'red', marginBottom: '1rem' }}>{errors.artist}</div>}

        <form onSubmit={handleSubmit}>
          <div className="mod-form">
            {/* Image Preview */}
            <img
              id="imagePreview"
              src={imagePreview || '/images/default-album.png'}
              alt="Album preview"
              className="album-cover mod-editable-image"
              onClick={() => document.getElementById('albumImageInput')?.click()}
            />
            <input
              id="albumImageInput"
              type="file"
              accept=".jpg,.jpeg,.png"
              style={{ display: 'none' }}
              onChange={handleImageChange}
            />

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

              {/* Genre */}
              <div>
                <label className="mod-label">
                  Genre:
                  {errors.genre && <span className="error" style={{ color: 'red' }}> {errors.genre}</span>}
                  <input
                    type="text"
                    value={genre}
                    onChange={(e) => setGenre(e.target.value)}
                    className="mod-input"
                    required
                  />
                </label>
              </div>

              {/* Release Date */}
              <div>
                <label className="mod-label">
                  Release Date:
                  {errors.releaseDate && <span className="error" style={{ color: 'red' }}> {errors.releaseDate}</span>}
                  <input
                    type="date"
                    value={releaseDate}
                    onChange={(e) => setReleaseDate(e.target.value)}
                    className="mod-input"
                    required
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
            <button type="submit" className="btn btn-primary" disabled={loading || !artistId}>
              {loading ? 'Creating...' : 'Create Album'}
            </button>
          </div>
        </form>
      </div>
    </Layout>
  );
}

