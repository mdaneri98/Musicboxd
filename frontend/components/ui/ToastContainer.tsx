/**
 * Toast Container Component
 * Displays toast notifications from Redux state
 */

import { useEffect } from 'react';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { selectToasts, removeToast, Toast } from '@/store/slices/uiSlice';

const DEFAULT_DURATION = 5000; // 5 seconds

const ToastItem = ({ toast, onClose }: { toast: Toast; onClose: () => void }) => {
  useEffect(() => {
    const duration = toast.duration ?? DEFAULT_DURATION;
    if (duration > 0) {
      const timer = setTimeout(onClose, duration);
      return () => clearTimeout(timer);
    }
  }, [toast.duration, onClose]);

  const iconMap = {
    success: 'fas fa-check-circle',
    error: 'fas fa-exclamation-circle',
    warning: 'fas fa-exclamation-triangle',
    info: 'fas fa-info-circle',
  };

  return (
    <div className={`toast toast-${toast.type}`} role="alert" aria-live="assertive">
      <div className="toast-content">
        <i className={iconMap[toast.type]} aria-hidden="true" />
        <span className="toast-message">{toast.message}</span>
      </div>
      <button 
        className="toast-close" 
        onClick={onClose}
        aria-label="Close notification"
      >
        <i className="fas fa-times" />
      </button>
    </div>
  );
};

const ToastContainer = () => {
  const dispatch = useAppDispatch();
  const toasts = useAppSelector(selectToasts);

  const handleClose = (id: string) => {
    dispatch(removeToast(id));
  };

  if (toasts.length === 0) return null;

  return (
    <div className="toast-container" aria-live="polite" aria-atomic="true">
      {toasts.map((toast) => (
        <ToastItem
          key={toast.id}
          toast={toast}
          onClose={() => handleClose(toast.id)}
        />
      ))}
    </div>
  );
};

export default ToastContainer;
