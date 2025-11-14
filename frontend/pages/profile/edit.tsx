import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { Layout } from '@/components/layout';
import { EditProfileForm } from '@/components/forms';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { selectIsAuthenticated, selectCurrentUser, getCurrentUserAsync, updateUserAsync } from '@/store/slices';
import type { EditProfileFormData } from '@/types';
import { imageRepository } from '@/repositories';

const EditProfilePage = () => {
  const router = useRouter();
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | undefined>();
  const [imagePreview, setImagePreview] = useState<string | undefined>();

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    if (!currentUser) {
      dispatch(getCurrentUserAsync());
    } else {
      // Set initial image preview
      if (currentUser.image_id) {
        setImagePreview(imageRepository.getImageUrl(currentUser.image_id));
      }
    }
  }, [isAuthenticated, currentUser, dispatch, router]);

  const handleSubmit = async (data: EditProfileFormData) => {
    if (!currentUser) return;

    try {
      setLoading(true);
      setError(undefined);

      // Update user profile
      await dispatch(updateUserAsync({ id: currentUser.id, userData: data }));

      // Refresh current user data
      await dispatch(getCurrentUserAsync());

      // Redirect to profile
      router.push('/profile');
    } catch (err: any) {
      console.error('Failed to update profile:', err);
      setError(err.message || 'Failed to update profile');
    } finally {
      setLoading(false);
    }
  };

  const handleImageChange = (file: File | null) => {
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        setImagePreview(e.target?.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  if (!isAuthenticated || !currentUser) {
    return null; // Will redirect
  }

  return (
    <Layout title="Musicboxd - Edit Profile">
      <div className="content-wrapper">
        <div className="mod-form-container">
          <h1 className="mod-form-title">Edit Profile</h1>

          <EditProfileForm
            onSubmit={handleSubmit}
            error={error}
            isLoading={loading}
            initialValues={{
              username: currentUser.username,
              name: currentUser.name || '',
              bio: currentUser.bio || '',
            }}
            imagePreview={imagePreview}
            onImageChange={handleImageChange}
          />

          <div className="form-actions" style={{ marginTop: '1rem' }}>
            <Link href="/profile" className="btn btn-secondary">
              Discard Changes
            </Link>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default EditProfilePage;

