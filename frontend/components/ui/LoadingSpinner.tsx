/**
 * Loading Spinner Component
 * 
 * Best Practice: Reusable loading indicator with consistent styling
 */

interface LoadingSpinnerProps {
  size?: 'small' | 'medium' | 'large';
  message?: string;
  fullScreen?: boolean;
}

export function LoadingSpinner({
  size = 'medium',
  message,
  fullScreen = false,
}: LoadingSpinnerProps) {
  const sizeClasses = {
    small: 'spinner-small',
    medium: '',
    large: 'spinner-large',
  };

  const spinner = (
    <div className={`loading-spinner ${sizeClasses[size]}`}>
      <div className="spinner"></div>
      {message && <p className="loading-message">{message}</p>}
    </div>
  );

  if (fullScreen) {
    return (
      <div className="loading-container">
        {spinner}
      </div>
    );
  }

  return spinner;
}

