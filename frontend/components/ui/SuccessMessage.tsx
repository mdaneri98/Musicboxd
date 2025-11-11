import { useEffect, useState } from 'react';

/**
 * Success Message Component
 * 
 * Best Practice: Temporary success notification with auto-dismiss
 */

interface SuccessMessageProps {
  message: string | null | undefined;
  duration?: number;
  onClose?: () => void;
  className?: string;
}

export function SuccessMessage({
  message,
  duration = 5000,
  onClose,
  className = '',
}: SuccessMessageProps) {
  const [isVisible, setIsVisible] = useState(!!message);

  useEffect(() => {
    if (message) {
      setIsVisible(true);

      // Auto-dismiss after duration
      const timer = setTimeout(() => {
        setIsVisible(false);
        onClose?.();
      }, duration);

      return () => clearTimeout(timer);
    }
  }, [message, duration, onClose]);

  if (!message || !isVisible) return null;

  return (
    <div
      className={`success-message ${className}`}
      role="status"
      aria-live="polite"
    >
      <div className="success-icon">✓</div>
      <p className="success-text">{message}</p>
      <button
        onClick={() => {
          setIsVisible(false);
          onClose?.();
        }}
        className="success-close"
        type="button"
        aria-label="Close"
      >
        ×
      </button>
    </div>
  );
}

