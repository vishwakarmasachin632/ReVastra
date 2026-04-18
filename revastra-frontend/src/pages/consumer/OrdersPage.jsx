import { useEffect, useState } from 'react';
import DashboardLayout from '../../layouts/DashboardLayout';
import SectionHeader from '../../components/SectionHeader';
import OrderCard from '../../components/OrderCard';
import Loader from '../../components/Loader';
import EmptyState from '../../components/EmptyState';
import { orderApi } from '../../api/services';
import { useAuth } from '../../context/AuthContext';
import { apiError } from '../../api/client';
import { usePageFeedback } from '../../utils/usePageFeedback';

export default function OrdersPage() {
  const { token } = useAuth();
  const { toast, showToast, clearToast } = usePageFeedback();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    orderApi.list(token)
      .then(setOrders)
      .catch((err) => showToast(apiError(err, 'Unable to fetch orders'), 'error'))
      .finally(() => setLoading(false));
  }, [token]);

  return (
    <DashboardLayout title="My Orders" subtitle="Track all service, store and tailoring orders" toast={toast} clearToast={clearToast}>
      <section className="glass-card">
        <SectionHeader eyebrow="Orders" title="Order history and status" subtitle="Statuses come directly from order service lifecycle." />
        {loading ? <Loader text="Loading orders..." /> : <div className="card-grid two-col">{orders.map((order) => <OrderCard key={order.id} order={order} />)}{!orders.length ? <EmptyState title="No orders yet" text="Orders created from bookings or product purchases will appear here." /> : null}</div>}
      </section>
    </DashboardLayout>
  );
}
