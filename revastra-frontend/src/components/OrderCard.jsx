import Badge from './Badge';
import { formatCurrency, formatDate } from '../utils/helpers';

const formatDateTime = (value) => {
  if (!value) return '—';
  try {
    return new Date(value).toLocaleString();
  } catch {
    return value;
  }
};

export default function OrderCard({ order, children }) {
  return (
    <div className="glass-card order-card">
      {/* Header */}
      <div className="order-head">
        <div>
          <h3>{order.serviceType || 'SERVICE'}</h3>
          <p>{formatDate(order.createdAt)}</p>
        </div>

        <Badge>{order.status}</Badge>
      </div>

      {/* Main Info */}
      <div className="order-grid">
        <div>
          <span>Amount</span>
          <strong>{formatCurrency(order.totalAmount)}</strong>
        </div>

        <div>
          <span>Customer</span>
          <strong>{order.consumerName || '—'}</strong>
        </div>

        <div>
          <span>Phone</span>
          <strong>{order.consumerPhone || '—'}</strong>
        </div>
      </div>

      {/* Payment Info */}
      <div className="sub-grid-two muted-grid">
        <div>
          <span>Payment Status</span>
          <strong>{order.paymentStatus || 'PENDING'}</strong>
        </div>

        <div>
          <span>Paid At</span>
          <strong>{formatDateTime(order.paymentTime)}</strong>
        </div>
      </div>

      {/* Completion Info */}
      {order.completedAt ? (
        <div className="sub-grid-two muted-grid">
          <div>
            <span>Completed At</span>
            <strong>{formatDateTime(order.completedAt)}</strong>
          </div>
        </div>
      ) : null}

      {/* Buttons (status update etc.) */}
      {children}
    </div>
  );
}