/**
 * Error Message Component
 * 
 * Best Practice: Consistent error display with proper accessibility
 */

interface ErrorMessageProps {
  message: string | null | undefined;
  title?: string;
  onRetry?: () => void;
  className?: string;
}

export function ErrorMessage({
  message,
  title = 'Error',
  onRetry,
  className = '',
}: ErrorMessageProps) {
  if (!message) return null;

  return (
    <div className={`error-message ${className}`} role="alert" aria-live="polite">
      <div className="error-icon">⚠️</div>
      <div className="error-content">
        {title && <h3 className="error-title">{title}</h3>}
        <p className="error-text">{message}</p>
        {onRetry && (
          <button
            onClick={onRetry}
            className="btn btn-secondary btn-small"
            type="button"
          >
            Try Again
          </button>
        )}
      </div>
    </div>
  );
}

