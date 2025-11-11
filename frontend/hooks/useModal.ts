import { useState, useCallback } from 'react';

/**
 * Custom hook for modal state management
 * 
 * Best Practice: Simplifies modal open/close logic with a reusable hook,
 * avoiding repetitive useState/handler code in components
 * 
 * @param initialState - Initial open state (default: false)
 * @returns Object with modal state and handlers
 * 
 * @example
 * const { isOpen, open, close, toggle } = useModal();
 * 
 * return (
 *   <>
 *     <button onClick={open}>Open Modal</button>
 *     <Modal isOpen={isOpen} onClose={close}>
 *       Content
 *     </Modal>
 *   </>
 * );
 */
export function useModal(initialState: boolean = false) {
  const [isOpen, setIsOpen] = useState(initialState);

  const open = useCallback(() => {
    setIsOpen(true);
  }, []);

  const close = useCallback(() => {
    setIsOpen(false);
  }, []);

  const toggle = useCallback(() => {
    setIsOpen((prev) => !prev);
  }, []);

  return {
    isOpen,
    open,
    close,
    toggle,
  };
}

