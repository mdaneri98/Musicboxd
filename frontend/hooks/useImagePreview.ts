import { useState, useCallback, useEffect } from 'react';

/**
 * Custom hook for image upload with preview
 * Handles file selection, validation, and preview generation
 * 
 * Best Practice: Encapsulates complex image handling logic in a reusable hook,
 * with validation and cleanup
 * 
 * @param options - Configuration options
 * @returns Object with image state and handlers
 * 
 * @example
 * const { preview, file, error, handleFileChange, reset } = useImagePreview({
 *   maxSizeMB: 5,
 *   allowedTypes: ['image/jpeg', 'image/png'],
 * });
 */
interface UseImagePreviewOptions {
  maxSizeMB?: number;
  allowedTypes?: string[];
  initialPreview?: string;
}

export function useImagePreview(options: UseImagePreviewOptions = {}) {
  const {
    maxSizeMB = 5,
    allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'],
    initialPreview = '',
  } = options;

  const [file, setFile] = useState<File | null>(null);
  const [preview, setPreview] = useState<string>(initialPreview);
  const [error, setError] = useState<string>('');
  const [loading, setLoading] = useState(false);

  // Clean up preview URL on unmount
  useEffect(() => {
    return () => {
      if (preview && preview.startsWith('blob:')) {
        URL.revokeObjectURL(preview);
      }
    };
  }, [preview]);

  const validateFile = useCallback(
    (file: File): string | null => {
      // Check file type
      if (!allowedTypes.includes(file.type)) {
        return `Invalid file type. Allowed types: ${allowedTypes.join(', ')}`;
      }

      // Check file size
      const sizeMB = file.size / (1024 * 1024);
      if (sizeMB > maxSizeMB) {
        return `File too large. Maximum size: ${maxSizeMB}MB`;
      }

      return null;
    },
    [allowedTypes, maxSizeMB]
  );

  const handleFileChange = useCallback(
    (event: React.ChangeEvent<HTMLInputElement>) => {
      const selectedFile = event.target.files?.[0];

      if (!selectedFile) {
        return;
      }

      // Validate file
      const validationError = validateFile(selectedFile);
      if (validationError) {
        setError(validationError);
        setFile(null);
        setPreview('');
        return;
      }

      setError('');
      setFile(selectedFile);
      setLoading(true);

      // Create preview
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreview(reader.result as string);
        setLoading(false);
      };
      reader.onerror = () => {
        setError('Failed to read file');
        setLoading(false);
      };
      reader.readAsDataURL(selectedFile);
    },
    [validateFile]
  );

  const reset = useCallback(() => {
    if (preview && preview.startsWith('blob:')) {
      URL.revokeObjectURL(preview);
    }
    setFile(null);
    setPreview(initialPreview);
    setError('');
    setLoading(false);
  }, [preview, initialPreview]);

  const setPreviewUrl = useCallback((url: string) => {
    setPreview(url);
  }, []);

  return {
    file,
    preview,
    error,
    loading,
    handleFileChange,
    reset,
    setPreviewUrl,
  };
}

