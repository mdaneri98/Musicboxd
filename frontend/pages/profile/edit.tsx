import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { useTranslation } from 'react-i18next';
import { Layout } from '@/components/layout';
import { EditProfileForm } from '@/components/forms';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { selectIsAuthenticated, selectCurrentUser, getCurrentUserAsync, updateUserAsync } from '@/store/slices';
import type { EditProfileFormData } from '@/types';
import { imageRepository } from '@/repositories';

const EditProfilePage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | undefined>();
  const [imagePreview, setImagePreview] = useState<string | undefined>();
  const [imageFile, setImageFile] = useState<File | null>(null);

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

      // Upload image and get ID if a new image was selected
      let profilePictureId: number | undefined;
      if (imageFile) {
        try {
          profilePictureId = await imageRepository.uploadImage(imageFile);
        } catch (error) {
          console.error('Failed to upload image:', error);
          setError('Failed to upload image. Please try again.');
          setLoading(false);
          return;
        }
      }

      // Prepare updated user data (excluding username as it's not changeable)
      const userData: Partial<EditProfileFormData> = {
        name: data.name,
        bio: data.bio,
        profilePicture: profilePictureId,
      };

      // Update user profile
      await dispatch(updateUserAsync({ id: currentUser.id, userData })).unwrap();

      // Refresh current user data
      await dispatch(getCurrentUserAsync()).unwrap();

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
      setImageFile(file);
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
          <h1 className="mod-form-title">{t('profile.editProfile')}</h1>

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
              {t('profile.discardChanges')}
            </Link>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default EditProfilePage;

