export default function Toast({ toast, onClose }) {
  if (!toast?.message) return null;
  return (
    <div className={`toast ${toast.type || 'info'}`}>
      <span>{toast.message}</span>
      <button onClick={onClose}>×</button>
    </div>
  );
}
