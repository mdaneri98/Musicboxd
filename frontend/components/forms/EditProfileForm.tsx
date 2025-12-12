/**
 * EditProfileForm Component
 * User profile edit form (from users/edit_profile.jsp)
 */

import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { editProfileSchema } from '@/utils/validationSchemas';
import { EditProfileFormData } from '@/types';
import { useTranslation } from 'react-i18next';

interface EditProfileFormProps {
  onSubmit: (data: EditProfileFormData) => void;
  error?: string;
  isLoading?: boolean;
  initialValues?: Partial<EditProfileFormData>;
  imagePreview?: string;
  onImageChange?: (file: File | null) => void;
}

const EditProfileForm = ({
  onSubmit,
  error,
  isLoading,
  initialValues,
  imagePreview,
  onImageChange,
}: EditProfileFormProps) => {
  const { t } = useTranslation();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<EditProfileFormData>({
    resolver: yupResolver(editProfileSchema) as any,
    defaultValues: initialValues,
  });

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null;
    if (onImageChange) {
      onImageChange(file);
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <div className="mod-form">
        {/* Profile Picture with Preview */}
        <div style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
          <input
            type="file"
            id="userImageInput"
            accept=".jpg,.jpeg,.png"
            className="hidden-input"
            onChange={handleFileChange}
            style={{ display: 'none' }}
          />
          <img
            id="imagePreview"
            src={imagePreview || '/assets/default-avatar.png'}
            className="entity-image mod-editable-image"
            style={{ cursor: 'pointer' }}
            onClick={() => document.getElementById('userImageInput')?.click()}
            alt="Profile Image"
          />
        </div>

        <div className="mod-entity-details">
          <div>
            <label className="mod-label">{t('profile.username')}</label>
            <input
              type="text"
              {...register('username')}
              className="form-control"
              disabled
              readOnly
            />
          </div>

          <div>
            <label className="mod-label">{t('profile.name')}</label>
            {errors.name && (
              <p className="form-error">{errors.name.message}</p>
            )}
            <input
              type="text"
              {...register('name')}
              className="form-control"
            />
          </div>

          <div>
            <label className="mod-label">{t('profile.bio')}</label>
            {errors.bio && <p className="form-error">{errors.bio.message}</p>}
            <textarea
              {...register('bio')}
              rows={4}
              className="form-control"
            />
          </div>
        </div>
      </div>

      {error && (
        <div className="form-error" style={{ marginBottom: '1rem' }}>
          {error}
        </div>
      )}

      <div className="form-actions">
        <button
          type="submit"
          className="btn btn-primary"
          disabled={isLoading}
        >
          {isLoading ? t('profile.updating') : t('profile.update')}
        </button>
      </div>
    </form>
  );
};

export default EditProfileForm;

