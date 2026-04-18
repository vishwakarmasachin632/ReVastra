import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import DashboardLayout from '../../layouts/DashboardLayout';
import SectionHeader from '../../components/SectionHeader';
import StatCard from '../../components/StatCard';
import Loader from '../../components/Loader';
import EmptyState from '../../components/EmptyState';
import OrderCard from '../../components/OrderCard';
import {
  notificationApi,
  orderApi,
  paymentApi,
  rewardApi,
  workerDirectoryApi
} from '../../api/services';
import { useAuth } from '../../context/AuthContext';
import { apiError } from '../../api/client';
import { formatCurrency } from '../../utils/helpers';
import { usePageFeedback } from '../../utils/usePageFeedback';

export default function ConsumerDashboard() {
  const { token, profile } = useAuth();
  const { toast, showToast, clearToast } = usePageFeedback();
  const [loading, setLoading] = useState(true);
  const [data, setData] = useState({
    workers: [],
    orders: [],
    wallet: null,
    rewards: null,
    notifications: []
  });

  useEffect(() => {
    const load = async () => {
      try {
        const [workers, orders, wallet, rewards, notifications] = await Promise.all([
          workerDirectoryApi.list(token),
          orderApi.list(token),
          paymentApi.wallet(token),
          rewardApi.me(token),
          notificationApi.list(token)
        ]);

        setData({ workers, orders, wallet, rewards, notifications });
      } catch (err) {
        showToast(apiError(err, 'Unable to load dashboard'), 'error');
      } finally {
        setLoading(false);
      }
    };

    load();
  }, [token]);

  const activeOrders = useMemo(
    () =>
      data.orders.filter(
        (order) =>
          !['COMPLETED', 'CANCELLED'].includes(String(order.status || '').toUpperCase())
      ).length,
    [data.orders]
  );

  const completedOrders = useMemo(
    () =>
      data.orders.filter(
        (order) => String(order.status || '').toUpperCase() === 'COMPLETED'
      ).length,
    [data.orders]
  );

  const latestNotification = data.notifications[0];

  if (loading) {
    return (
      <DashboardLayout
        title="Consumer Dashboard"
        subtitle="Your services, orders and rewards in one place."
        toast={toast}
        clearToast={clearToast}
      >
        <Loader text="Loading your dashboard..." />
      </DashboardLayout>
    );
  }

  return (
    <DashboardLayout
      title={`Welcome back, ${profile?.name || 'User'}`}
      subtitle="Book, track, pay and shop from one clean dashboard."
      toast={toast}
      clearToast={clearToast}
    >
      <section className="consumer-hero glass-card">
        <div className="consumer-hero-copy">
          <p className="eyebrow">Consumer workspace</p>
          <h3>Your clothing care, simplified.</h3>
          <p>
            Book services, track orders, manage balance and explore upcycled products.
          </p>

          <div className="consumer-hero-actions">
            <Link className="primary-btn" to="/consumer/services">
              Book service
            </Link>
            <Link className="ghost-btn" to="/consumer/orders">
              View orders
            </Link>
            <Link className="ghost-btn" to="/consumer/products">
              Shop store
            </Link>
          </div>
        </div>

        <div className="consumer-hero-panel">
          <div className="hero-mini-stat">
            <span>Active orders</span>
            <strong>{activeOrders}</strong>
          </div>
          <div className="hero-mini-stat">
            <span>Wallet</span>
            <strong>{formatCurrency(data.wallet?.balance)}</strong>
          </div>
          <div className="hero-mini-stat">
            <span>Rewards</span>
            <strong>{data.rewards?.currentBalance ?? 0} pts</strong>
          </div>
          <div className="hero-mini-stat">
            <span>Workers</span>
            <strong>{data.workers.length}</strong>
          </div>
        </div>
      </section>

      <section className="stats-grid">
        <StatCard
          title="Verified Workers"
          value={data.workers.length}
          hint="Available for booking"
          tone="success"
        />
        <StatCard
          title="Total Orders"
          value={data.orders.length}
          hint="Across all services"
          tone="info"
        />
        <StatCard
          title="Wallet Balance"
          value={formatCurrency(data.wallet?.balance)}
          hint="Ready to use"
          tone="warning"
        />
        <StatCard
          title="Reward Points"
          value={data.rewards?.currentBalance ?? 0}
          hint="Earn from activity"
        />
      </section>

      <section className="consumer-feature-grid">
        <div className="glass-card consumer-feature-card feature-primary">
          <p className="eyebrow">Quick actions</p>
          <h4>Start what you need in one tap.</h4>

          <div className="consumer-action-grid">
            <Link className="consumer-action-tile" to="/consumer/services">
              <span className="consumer-action-kicker">Service</span>
              <strong>Book laundry</strong>
              <small>Pickup, wash, fold and more</small>
            </Link>

            <Link className="consumer-action-tile" to="/consumer/workers">
              <span className="consumer-action-kicker">Workers</span>
              <strong>Find workers</strong>
              <small>Browse verified profiles</small>
            </Link>

            <Link className="consumer-action-tile" to="/consumer/donations">
              <span className="consumer-action-kicker">Donate</span>
              <strong>Give clothes</strong>
              <small>Earn rewards on donations</small>
            </Link>

            <Link className="consumer-action-tile" to="/consumer/payments">
              <span className="consumer-action-kicker">Payments</span>
              <strong>Manage wallet</strong>
              <small>Top up and track payments</small>
            </Link>
          </div>
        </div>

        <div className="consumer-side-stack">
          <div className="glass-card consumer-info-card">
            <span className="consumer-info-label">Latest update</span>
            <strong>
              {latestNotification?.title || latestNotification?.type || 'No new alerts'}
            </strong>
            <p>
              {latestNotification?.message ||
                'Your latest notifications will appear here.'}
            </p>
          </div>

          <div className="glass-card consumer-info-card">
            <span className="consumer-info-label">Completed orders</span>
            <strong>{completedOrders}</strong>
            <p>Completed successfully from your dashboard.</p>
          </div>
        </div>
      </section>

      <section className="glass-card">
        <SectionHeader
          eyebrow="Recent orders"
          title="Track recent activity"
          subtitle="Your latest bookings and purchases."
        />
        <div className="card-grid two-col">
          {data.orders.slice(0, 4).map((order) => (
            <OrderCard key={order.id} order={order} />
          ))}
          {!data.orders.length ? (
            <EmptyState
              title="No orders yet"
              text="Book your first service to see activity here."
            />
          ) : null}
        </div>
      </section>

      <section className="glass-card">
        <SectionHeader
          eyebrow="Updates"
          title="Notifications"
          subtitle="Recent confirmations and status updates."
        />
        <div className="list-stack consumer-notification-list">
          {data.notifications.slice(0, 5).map((item) => (
            <div key={item.id} className="list-item consumer-notification-item">
              <div>
                <strong>{item.title || item.type || 'Notification'}</strong>
                <p>{item.message}</p>
              </div>
            </div>
          ))}
          {!data.notifications.length ? (
            <EmptyState
              title="No notifications"
              text="Updates will appear here as your activity grows."
            />
          ) : null}
        </div>
      </section>
    </DashboardLayout>
  );
}