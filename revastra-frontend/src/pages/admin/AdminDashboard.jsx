import { useEffect, useState } from 'react';
import DashboardLayout from '../../layouts/DashboardLayout';
import StatCard from '../../components/StatCard';
import SectionHeader from '../../components/SectionHeader';
import Loader from '../../components/Loader';
import EmptyState from '../../components/EmptyState';
import WorkerCard from '../../components/WorkerCard';
import { adminApi } from '../../api/services';
import { useAuth } from '../../context/AuthContext';
import { apiError } from '../../api/client';
import { formatCurrency } from '../../utils/helpers';
import { usePageFeedback } from '../../utils/usePageFeedback';

export default function AdminDashboard() {
  const { token, profile } = useAuth();
  const { toast, showToast, clearToast } = usePageFeedback();
  const [loading, setLoading] = useState(true);
  const [state, setState] = useState({ pendingWorkers: [], userStats: null, orderStats: null, paymentSummary: null });

  useEffect(() => {
    Promise.allSettled([
      adminApi.getPendingWorkers(token),
      adminApi.getUserStats(token),
      adminApi.getOrderStats(token),
      adminApi.getPaymentSummary(token)
    ]).then(([pending, users, orders, payments]) => {
      setState({
        pendingWorkers: pending.status === 'fulfilled' ? pending.value : [],
        userStats: users.status === 'fulfilled' ? users.value : null,
        orderStats: orders.status === 'fulfilled' ? orders.value : null,
        paymentSummary: payments.status === 'fulfilled' ? payments.value : null
      });
      [pending, users, orders, payments].forEach((item) => {
        if (item.status === 'rejected') showToast(apiError(item.reason, 'Some admin metrics are unavailable'), 'error');
      });
    }).finally(() => setLoading(false));
  }, [token]);

  return (
    <DashboardLayout title={`Admin Command Center — ${profile?.name}`} subtitle="Monitor approval pipeline, platform activity and commercial metrics" toast={toast} clearToast={clearToast}>
      {loading ? <Loader text="Loading admin dashboard..." /> : (
        <>
          <section className="stats-grid">
            <StatCard title="Pending Workers" value={state.pendingWorkers.length} hint="Needs admin decision" tone="warning" />
            <StatCard title="Consumers" value={state.userStats?.totalConsumers ?? '—'} hint="Registered consumer accounts" />
            <StatCard title="Orders" value={state.orderStats?.totalOrders ?? '—'} hint="Platform-wide order count" />
            <StatCard title="Revenue" value={state.paymentSummary ? formatCurrency(state.paymentSummary.totalEarnings) : '—'} hint="Successful earnings" tone="success" />
          </section>
          <section className="glass-card">
            <SectionHeader eyebrow="Approvals" title="Workers waiting for verification" subtitle="Approve pending worker registrations from the queue below." />
            <div className="card-grid two-col">
              {state.pendingWorkers.map((worker) => <WorkerCard key={worker.id} worker={worker} extra={<p className="muted-text">Open Worker Approval page to verify this account.</p>} />)}
              {!state.pendingWorkers.length ? <EmptyState title="No pending workers" text="All workers are already reviewed or none have registered yet." /> : null}
            </div>
          </section>
        </>
      )}
    </DashboardLayout>
  );
}
