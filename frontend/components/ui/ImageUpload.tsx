import { useRef } from 'react';
import { useImagePreview } from '@/hooks';
import { useTranslation } from 'react-i18next';

/**
 * Image Upload Component with Preview
 * 
 * Best Practice: Encapsulates image upload logic with validation and preview
 */

interface ImageUploadProps {
  onImageChange: (file: File | null) => void;
  currentImageUrl?: string;
  maxSizeMB?: number;
  allowedTypes?: string[];
  label?: string;
  className?: string;
}

export function ImageUpload({
  onImageChange,
  currentImageUrl,
  maxSizeMB = 5,
  allowedTypes,
  label = 'Upload Image',
  className = '',
}: ImageUploadProps) {
  const fileInputRef = useRef<HTMLInputElement>(null);
  const { preview, file, error, loading, handleFileChange, reset } = useImagePreview({
    maxSizeMB,
    allowedTypes,
    initialPreview: currentImageUrl,
  });
  const { t } = useTranslation();
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    handleFileChange(e);
    const selectedFile = e.target.files?.[0];
    onImageChange(selectedFile || null);
  };

  const handleRemove = () => {
    reset();
    onImageChange(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const handleClick = () => {
    fileInputRef.current?.click();
  };

  return (
    <div className={`image-upload ${className}`}>
      {label && <label className="image-upload-label">{label}</label>}

      {/* Preview */}
      {preview && (
        <div className="image-preview">
          <img src={preview} alt="Preview" className="preview-image" />
          <button
            onClick={handleRemove}
            className="btn btn-danger btn-small"
            type="button"
          >
            Remove
          </button>
        </div>
      )}

      {/* Upload Button */}
      {!preview && (
        <div className="image-upload-area" onClick={handleClick}>
          <input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            onChange={handleChange}
            className="image-upload-input"
            style={{ display: 'none' }}
          />
          <div className="upload-placeholder">
            <span className="upload-icon">📷</span>
            <p className="upload-text">
              {loading ? 'Loading...' : 'Click to upload or drag and drop'}
            </p>
            <p className="upload-hint">
              {t("common.maxSize")}: {maxSizeMB}MB
            </p>
          </div>
        </div>
      )}

      {/* Error Message */}
      {error && <p className="image-upload-error">{error}</p>}

      {/* File Info */}
      {file && (
        <p className="image-upload-info">
          {file.name} ({(file.size / 1024).toFixed(2)} KB)
        </p>
      )}
    </div>
  );
}

