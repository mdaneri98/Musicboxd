/**
 * Loading Spinner Component
 * 
 * Best Practice: Reusable loading indicator with consistent styling
 */

interface LoadingSpinnerProps {
  size?: 'small' | 'medium' | 'large';
  message?: string;
  fullScreen?: boolean;
  inline?: boolean;
  centered?: boolean;
}

export function LoadingSpinner({
  size = 'medium',
  message,
  fullScreen = false,
  inline = false,
  centered = false,
}: LoadingSpinnerProps) {
  const sizeClasses = {
    small: 'spinner-small',
    medium: '',
    large: 'spinner-large',
  };

  const classNames = [
    'loading-spinner',
    sizeClasses[size],
    inline ? 'inline' : '',
  ].filter(Boolean).join(' ');

  const spinner = (
    <div className={classNames}>
      <div className="spinner" aria-hidden="true"></div>
      {message && <p className="loading-message">{message}</p>}
    </div>
  );

  if (fullScreen) {
    return (
      <div className="loading-container" role="status" aria-live="polite">
        {spinner}
      </div>
    );
  }

  if (centered) {
    return (
      <div className="loading-center" role="status" aria-live="polite">
        {spinner}
      </div>
    );
  }

  return (
    <div role="status" aria-live="polite">
      {spinner}
    </div>
  );
}

