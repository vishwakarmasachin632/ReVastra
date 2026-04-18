import { useEffect, useState } from 'react';
import DashboardLayout from '../../layouts/DashboardLayout';
import SectionHeader from '../../components/SectionHeader';
import Loader from '../../components/Loader';
import EmptyState from '../../components/EmptyState';
import { orderApi } from '../../api/services';
import { useAuth } from '../../context/AuthContext';
import { apiError } from '../../api/client';
import { usePageFeedback } from '../../utils/usePageFeedback';

const nextStatusMap = {
  PAID: 'ACCEPTED',
  ACCEPTED: 'PICKED_UP',
  PICKED_UP: 'IN_PROGRESS',
  IN_PROGRESS: 'OUT_FOR_DELIVERY',
  OUT_FOR_DELIVERY: 'COMPLETED'
};

const formatDateTime = (value) => {
  if (!value) return 'N/A';

  try {
    return new Date(value).toLocaleString();
  } catch {
    return value;
  }
};

export default function WorkerOrdersPage() {
  const { token } = useAuth();
  const { toast, showToast, clearToast } = usePageFeedback();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = async () => {
    try {
      setLoading(true);
      const data = await orderApi.listForWorker(token);
      setOrders(Array.isArray(data) ? data : []);
    } catch (err) {
      showToast(apiError(err, 'Unable to fetch worker orders'), 'error');
      setOrders([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (token) {
      load();
    }
  }, [token]);

  const handleStatus = async (orderId, status) => {
    try {
      await orderApi.updateStatus(token, orderId, status);
      showToast(`Order updated to ${status}`, 'success');
      load();
    } catch (err) {
      showToast(apiError(err, 'Status update failed'), 'error');
    }
  };

  return (
    <DashboardLayout
      title="Worker Orders"
      subtitle="Manage paid and assigned orders"
      toast={toast}
      clearToast={clearToast}
    >
      <section className="glass-card">
        <SectionHeader
          eyebrow="Worker Tasks"
          title="Orders ready for action"
          subtitle="Paid orders appear here automatically after customer payment."
        />

        {loading ? (
          <Loader text="Loading worker orders..." />
        ) : !orders.length ? (
          <EmptyState
            title="No worker orders"
            text="Paid customer orders will appear here automatically."
          />
        ) : (
          <div className="card-grid two-col">
            {orders.map((order) => {
              const nextStatus = nextStatusMap[order.status];

              return (
                <div key={order.id} className="glass-card worker-card">
                  <div className="worker-card-top">
                    <div>
                      <h3>{order.serviceType || 'SERVICE'}</h3>
                      <p>Current Status: {order.status || 'N/A'}</p>
                    </div>
                  </div>

                  <div className="worker-meta">
                    <div>
                      <span>Customer</span>
                      <strong>{order.consumerName || 'N/A'}</strong>
                    </div>

                    <div>
                      <span>Phone</span>
                      <strong>{order.consumerPhone || 'N/A'}</strong>
                    </div>

                    <div>
                      <span>Payment</span>
                      <strong>{order.paymentStatus || 'PENDING'}</strong>
                    </div>

                    <div>
                      <span>Amount</span>
                      <strong>₹{order.totalAmount ?? 0}</strong>
                    </div>

                    <div>
                      <span>Paid At</span>
                      <strong>{formatDateTime(order.paymentTime)}</strong>
                    </div>

                    <div>
                      <span>Created At</span>
                      <strong>{formatDateTime(order.createdAt)}</strong>
                    </div>
                  </div>

                  {nextStatus ? (
                    <button
                      type="button"
                      className="primary-btn"
                      onClick={() => handleStatus(order.id, nextStatus)}
                    >
                      Mark as {nextStatus}
                    </button>
                  ) : (
                    <button type="button" className="ghost-btn" disabled>
                      No further action
                    </button>
                  )}
                </div>
              );
            })}
          </div>
        )}
      </section>
    </DashboardLayout>
  );
}