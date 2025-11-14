import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { Layout } from '@/components/layout';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { selectIsAuthenticated, selectCurrentUser, createArtistAsync } from '@/store/slices';
import { CreateArtistFormData } from '@/types/forms';

export default function AddArtistPage() {
  const router = useRouter();
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  const [name, setName] = useState('');
  const [bio, setBio] = useState('');
  const [imageFile, setImageFile] = useState<File | null>(null);
  const [imagePreview, setImagePreview] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<{ name?: string; bio?: string; image?: string }>({});

  // Redirect if not authenticated or not moderator
  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/landing');
    } else if (currentUser && !currentUser.moderator) {
      router.push('/');
    }
  }, [isAuthenticated, currentUser, router]);

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
    const newErrors: { name?: string; bio?: string; image?: string } = {};
    if (!name.trim()) {
      newErrors.name = 'Name is required';
    }
    if (!bio.trim()) {
      newErrors.bio = 'Bio is required';
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    try {
      setLoading(true);

      // Create artist
      const artistData = {
        name: name.trim(),
        bio: bio.trim(),
        artistImage: imageFile,
      } as CreateArtistFormData;

      const newArtist = await dispatch(createArtistAsync(artistData as CreateArtistFormData)).unwrap();

      // Redirect to artist page
      router.push(`/artists/${newArtist.data?.id}`);
    } catch (error) {
      console.error('Failed to create artist:', error);
      setErrors({ name: 'Failed to create artist. Please try again.' });
    } finally {
      setLoading(false);
    }
  };

    if (!isAuthenticated || (currentUser && !currentUser.moderator)) {
    return null; // Will redirect in useEffect
  }

  return (
    <Layout title="Add Artist - Musicboxd">
      <div className="mod-form-container">
        <h1 className="mod-form-title">Add Artist</h1>

        <form onSubmit={handleSubmit}>
          <div className="mod-form">
            {/* Image Preview */}
            <img
              id="imagePreview"
              src={imagePreview || '/images/default-artist.png'}
              alt="Artist preview"
              className="entity-image mod-editable-image"
              onClick={() => document.getElementById('artistImageInput')?.click()}
            />
            <input
              id="artistImageInput"
              type="file"
              accept=".jpg,.jpeg,.png"
              style={{ display: 'none' }}
              onChange={handleImageChange}
            />

            <div className="mod-entity-details">
              {/* Name */}
              <div>
                <label className="mod-label">
                  Name:
                  {errors.name && <span className="error" style={{ color: 'red' }}> {errors.name}</span>}
                  <input
                    type="text"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    className="mod-input"
                    required
                  />
                </label>
              </div>

              {/* Bio */}
              <div>
                <label className="mod-label">
                  Bio:
                  {errors.bio && <span className="error" style={{ color: 'red' }}> {errors.bio}</span>}
                  <textarea
                    value={bio}
                    onChange={(e) => setBio(e.target.value)}
                    rows={5}
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
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Creating...' : 'Create Artist'}
            </button>
          </div>
        </form>
      </div>
    </Layout>
  );
}

