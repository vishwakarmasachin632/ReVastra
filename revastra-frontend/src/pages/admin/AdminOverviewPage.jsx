import { useEffect, useState } from 'react';
import DashboardLayout from '../../layouts/DashboardLayout';
import SectionHeader from '../../components/SectionHeader';
import EmptyState from '../../components/EmptyState';
import Loader from '../../components/Loader';
import Badge from '../../components/Badge';
import { adminApi } from '../../api/services';
import { useAuth } from '../../context/AuthContext';
import { apiError } from '../../api/client';
import { formatCurrency, formatDate } from '../../utils/helpers';
import { usePageFeedback } from '../../utils/usePageFeedback';

export default function AdminOverviewPage() {
  const { token } = useAuth();
  const { toast, showToast, clearToast } = usePageFeedback();
  const [loading, setLoading] = useState(true);
  const [data, setData] = useState({ consumers: [], workers: [], orders: [], payments: [] });

  useEffect(() => {
    Promise.allSettled([
      adminApi.getConsumers(token),
      adminApi.getWorkers(token),
      adminApi.getOrders(token),
      adminApi.getPayments(token)
    ]).then(([consumers, workers, orders, payments]) => {
      setData({
        consumers: consumers.status === 'fulfilled' ? consumers.value : [],
        workers: workers.status === 'fulfilled' ? workers.value : [],
        orders: orders.status === 'fulfilled' ? orders.value : [],
        payments: payments.status === 'fulfilled' ? payments.value : []
      });
      [consumers, workers, orders, payments].forEach((item) => {
        if (item.status === 'rejected') showToast(apiError(item.reason, 'Some admin listing APIs are unavailable'), 'error');
      });
    }).finally(() => setLoading(false));
  }, [token]);

  return (
    <DashboardLayout title="Admin Overview" subtitle="Users, workers, orders and payments in one place" toast={toast} clearToast={clearToast}>
      {loading ? <Loader text="Loading overview..." /> : (
        <section className="card-grid two-col split-grid">
          <div className="glass-card">
            <SectionHeader eyebrow="Consumers" title="Registered customers" />
            <div className="list-stack limited-list">
              {data.consumers.slice(0, 8).map((item) => <div className="list-item" key={item.id}><div><strong>{item.name}</strong><p>{item.email}</p></div><Badge>{item.role}</Badge></div>)}
              {!data.consumers.length ? <EmptyState title="Consumer list unavailable" text="Add admin consumer listing endpoint if this remains empty." /> : null}
            </div>
          </div>
          <div className="glass-card">
            <SectionHeader eyebrow="Workers" title="Worker accounts" />
            <div className="list-stack limited-list">
              {data.workers.slice(0, 8).map((item) => <div className="list-item" key={item.id}><div><strong>{item.name || item.workerName}</strong><p>{item.email || item.skills}</p></div><Badge>{item.verified || item.verifiedBadge ? 'Verified' : 'Pending'}</Badge></div>)}
              {!data.workers.length ? <EmptyState title="Worker list unavailable" text="Worker listing API will populate here once exposed." /> : null}
            </div>
          </div>
          <div className="glass-card">
            <SectionHeader eyebrow="Orders" title="Platform orders" />
            <div className="list-stack limited-list">
              {data.orders.slice(0, 8).map((order) => <div className="list-item" key={order.id}><div><strong>#{order.id} — {order.serviceType}</strong><p>{formatDate(order.createdAt)}</p></div><div className="aligned-right"><strong>{formatCurrency(order.totalAmount)}</strong><Badge>{order.status}</Badge></div></div>)}
              {!data.orders.length ? <EmptyState title="Order list unavailable" text="Admin order API will populate this section." /> : null}
            </div>
          </div>
          <div className="glass-card">
            <SectionHeader eyebrow="Payments" title="Recent transactions" />
            <div className="list-stack limited-list">
              {data.payments.slice(0, 8).map((payment) => <div className="list-item" key={payment.id}><div><strong>Order #{payment.orderId}</strong><p>{payment.transactionRef}</p></div><div className="aligned-right"><strong>{formatCurrency(payment.finalAmountPaid || payment.originalAmount)}</strong><Badge>{payment.status}</Badge></div></div>)}
              {!data.payments.length ? <EmptyState title="Payment list unavailable" text="Admin payment API will populate this section." /> : null}
            </div>
          </div>
        </section>
      )}
    </DashboardLayout>
  );
}
