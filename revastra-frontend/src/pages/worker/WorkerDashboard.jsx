import { useEffect, useState } from 'react';
import DashboardLayout from '../../layouts/DashboardLayout';
import StatCard from '../../components/StatCard';
import SectionHeader from '../../components/SectionHeader';
import OrderCard from '../../components/OrderCard';
import EmptyState from '../../components/EmptyState';
import Loader from '../../components/Loader';
import { notificationApi, orderApi, upcycleApi } from '../../api/services';
import { useAuth } from '../../context/AuthContext';
import { apiError } from '../../api/client';
import { usePageFeedback } from '../../utils/usePageFeedback';

export default function WorkerDashboard() {
  const { token, profile } = useAuth();
  const { toast, showToast, clearToast } = usePageFeedback();
  const [loading, setLoading] = useState(true);
  const [orders, setOrders] = useState([]);
  const [products, setProducts] = useState([]);
  const [notifications, setNotifications] = useState([]);

  useEffect(() => {
    Promise.all([orderApi.listForWorker(token), upcycleApi.listProducts(token), notificationApi.list(token)])
      .then(([o, p, n]) => {
        setOrders(o);
        setProducts(p);
        setNotifications(n);
      })
      .catch((err) => showToast(apiError(err, 'Unable to load worker dashboard'), 'error'))
      .finally(() => setLoading(false));
  }, [token]);

  return (
    <DashboardLayout title={`Worker Console for ${profile?.name}`} subtitle="Verified worker view for managing assigned work, statuses and product listings" toast={toast} clearToast={clearToast}>
      {loading ? <Loader text="Loading worker workspace..." /> : (
        <>
          <section className="stats-grid">
            <StatCard title="Assigned Orders" value={orders.length} hint="Worker-specific endpoint is used when available" />
            <StatCard title="My Products" value={products.length} hint="Public items currently listed" tone="warning" />
            <StatCard title="Notifications" value={notifications.length} hint="Status alerts and updates" tone="success" />
            <StatCard title="Verified" value={profile?.verified ? 'Yes' : 'No'} hint="Role unlock is based on admin approval" />
          </section>
          <section className="glass-card">
            <SectionHeader eyebrow="Orders" title="Manage lifecycle" subtitle="Use Orders page to mark pending jobs as confirmed, in progress or completed." />
            <div className="card-grid two-col">
              {orders.slice(0, 4).map((order) => <OrderCard key={order.id} order={order} />)}
              {!orders.length ? <EmptyState title="No work assigned yet" text="Once assigned, worker orders will appear here with consumer details when backend includes them." /> : null}
            </div>
          </section>
        </>
      )}
    </DashboardLayout>
  );
}
