/**
 * ConfirmationModal Component
 * Reusable confirmation dialog (from components/confirmation-window.jsp)
 */

interface ConfirmationModalProps {
  isOpen: boolean;
  message: string;
  onConfirm: () => void;
  onCancel: () => void;
  confirmText?: string;
  cancelText?: string;
}

const ConfirmationModal: React.FC<ConfirmationModalProps> = ({
  isOpen,
  message,
  onConfirm,
  onCancel,
  confirmText = 'Yes',
  cancelText = 'No',
}) => {
  if (!isOpen) return null;

  return (
    <>
      <div
        className="modal-overlay"
        onClick={onCancel}
        style={{ display: 'block' }}
      />
      <div className="confirmation-modal" style={{ display: 'block' }}>
        <div className="modal-content">
          <p className="modal-message">{message}</p>
          <div className="modal-actions">
            <button onClick={onCancel} className="btn btn-secondary modal-btn">
              {cancelText}
            </button>
            <button onClick={onConfirm} className="btn btn-danger modal-btn">
              {confirmText}
            </button>
          </div>
        </div>
      </div>
    </>
  );
};

export default ConfirmationModal;

