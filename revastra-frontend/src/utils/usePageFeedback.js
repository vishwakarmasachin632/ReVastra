import { useState } from 'react';

export function usePageFeedback() {
  const [toast, setToast] = useState(null);
  const showToast = (message, type = 'info') => setToast({ message, type });
  const clearToast = () => setToast(null);
  return { toast, showToast, clearToast };
}
