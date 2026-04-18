import { useEffect, useMemo, useState } from 'react';
import DashboardLayout from '../../layouts/DashboardLayout';
import SectionHeader from '../../components/SectionHeader';
import Loader from '../../components/Loader';
import EmptyState from '../../components/EmptyState';
import Badge from '../../components/Badge';
import { orderApi, paymentApi, rewardApi } from '../../api/services';
import { useAuth } from '../../context/AuthContext';
import { apiError } from '../../api/client';
import { formatCurrency, formatDate } from '../../utils/helpers';
import { usePageFeedback } from '../../utils/usePageFeedback';

export default function PaymentsPage() {
  const { token } = useAuth();
  const { toast, showToast, clearToast } = usePageFeedback();

  const [loading, setLoading] = useState(true);
  const [wallet, setWallet] = useState(null);
  const [rewards, setRewards] = useState(null);
  const [payments, setPayments] = useState([]);
  const [orders, setOrders] = useState([]);
  const [topUp, setTopUp] = useState(500);

  const [paymentForm, setPaymentForm] = useState({
    orderId: '',
    method: 'CASH',
    rewardPointsToRedeem: 0,
    useWallet: false
  });

  const load = async () => {
    try {
      const [walletData, rewardData, paymentData, orderData] = await Promise.all([
        paymentApi.wallet(token),
        rewardApi.me(token),
        paymentApi.list(token),
        orderApi.list(token)
      ]);

      setWallet(walletData);
      setRewards(rewardData);
      setPayments(Array.isArray(paymentData) ? paymentData : []);

      const unpaidOrders = Array.isArray(orderData)
        ? orderData.filter(
            (order) =>
              String(order.paymentStatus || 'PENDING').toUpperCase() !== 'PAID'
          )
        : [];

      setOrders(unpaidOrders);
    } catch (err) {
      showToast(apiError(err, 'Unable to load payment data'), 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, [token]);

  const selectedOrder = useMemo(
    () => orders.find((order) => Number(order.id) === Number(paymentForm.orderId)),
    [orders, paymentForm.orderId]
  );

  const walletApplied = paymentForm.useWallet
    ? Math.min(
        Number(wallet?.balance || 0),
        Number(selectedOrder?.totalAmount || 0)
      )
    : 0;

  const rewardApplied = Math.min(
    Number(paymentForm.rewardPointsToRedeem || 0),
    Number(rewards?.currentBalance || 0)
  );

  const estimatedFinal = Math.max(
    Number(selectedOrder?.totalAmount || 0) - walletApplied - rewardApplied,
    0
  );

  const handleTopUp = async (e) => {
    e.preventDefault();
    try {
      await paymentApi.topup(token, Number(topUp));
      showToast('Wallet topped up successfully', 'success');
      load();
    } catch (err) {
      showToast(apiError(err, 'Wallet top-up failed'), 'error');
    }
  };

  const handlePay = async (e) => {
    e.preventDefault();
    try {
      await paymentApi.pay(token, {
        ...paymentForm,
        orderId: Number(paymentForm.orderId),
        rewardPointsToRedeem: Number(paymentForm.rewardPointsToRedeem)
      });

      showToast('Payment processed successfully', 'success');
      setPaymentForm({
        orderId: '',
        method: 'CASH',
        rewardPointsToRedeem: 0,
        useWallet: false
      });
      load();
    } catch (err) {
      showToast(apiError(err, 'Payment failed'), 'error');
    }
  };

  return (
    <DashboardLayout
      title="Payments"
      subtitle="Track balance, apply rewards and complete payments."
      toast={toast}
      clearToast={clearToast}
    >
      {loading ? (
        <Loader text="Loading payments..." />
      ) : (
        <>
          <section className="stats-grid">
            <div className="stat-card success">
              <p>Wallet</p>
              <h3>{formatCurrency(wallet?.balance)}</h3>
              <span>Available balance</span>
            </div>

            <div className="stat-card">
              <p>Rewards</p>
              <h3>{rewards?.currentBalance ?? 0}</h3>
              <span>Available points</span>
            </div>

            <div className="stat-card info">
              <p>Payments</p>
              <h3>{payments.length}</h3>
              <span>Completed transactions</span>
            </div>

            <div className="stat-card warning">
              <p>Payable Orders</p>
              <h3>{orders.length}</h3>
              <span>Ready for checkout</span>
            </div>
          </section>

          <section className="card-grid two-col split-grid">
            <div className="glass-card">
              <SectionHeader
                eyebrow="Wallet"
                title="Add balance"
                subtitle="Top up before checkout."
              />

              <form className="stack-form" onSubmit={handleTopUp}>
                <label>
                  Amount
                  <input
                    type="number"
                    min="1"
                    value={topUp}
                    onChange={(e) => setTopUp(e.target.value)}
                  />
                </label>
                <button className="primary-btn">Top Up Wallet</button>
              </form>
            </div>

            <div className="glass-card">
              <SectionHeader
                eyebrow="Checkout"
                title="Pay for an order"
                subtitle="Choose an order and review the final amount."
              />

              <form className="stack-form" onSubmit={handlePay}>
                <label>
                  Order
                  <select
                    value={paymentForm.orderId}
                    onChange={(e) =>
                      setPaymentForm((prev) => ({
                        ...prev,
                        orderId: e.target.value
                      }))
                    }
                    required
                  >
                    <option value="">Select order</option>
                    {orders.map((order) => (
                      <option key={order.id} value={order.id}>
                        #{order.id} — {order.serviceType} — {formatCurrency(order.totalAmount)}
                      </option>
                    ))}
                  </select>
                </label>

                <label>
                  Method
                  <select
                    value={paymentForm.method}
                    onChange={(e) =>
                      setPaymentForm((prev) => ({
                        ...prev,
                        method: e.target.value
                      }))
                    }
                  >
                    <option>CASH</option>
                    <option>UPI</option>
                    <option>NET_BANKING</option>
                    <option>WALLET</option>
                    <option>CARD</option>
                  </select>
                </label>

                <label>
                  Reward Points
                  <input
                    type="number"
                    min="0"
                    max={rewards?.currentBalance ?? 0}
                    value={paymentForm.rewardPointsToRedeem}
                    onChange={(e) =>
                      setPaymentForm((prev) => ({
                        ...prev,
                        rewardPointsToRedeem: e.target.value
                      }))
                    }
                  />
                </label>

                <label className="checkbox-row">
                  <input
                    type="checkbox"
                    checked={paymentForm.useWallet}
                    onChange={(e) =>
                      setPaymentForm((prev) => ({
                        ...prev,
                        useWallet: e.target.checked
                      }))
                    }
                  />
                  <span>Use wallet balance</span>
                </label>

                <div className="payment-preview">
                  <div className="payment-summary-line">
                    <span>Order Amount</span>
                    <strong>{formatCurrency(selectedOrder?.totalAmount)}</strong>
                  </div>
                  <div className="payment-summary-line">
                    <span>Wallet Applied</span>
                    <strong>- {formatCurrency(walletApplied)}</strong>
                  </div>
                  <div className="payment-summary-line">
                    <span>Rewards Applied</span>
                    <strong>- {rewardApplied}</strong>
                  </div>
                  <div className="payment-summary-line">
                    <span>Estimated Final</span>
                    <strong>{formatCurrency(estimatedFinal)}</strong>
                  </div>
                </div>

                <button className="primary-btn">Pay Now</button>
              </form>
            </div>
          </section>

          <section className="glass-card">
            <SectionHeader
              eyebrow="Transactions"
              title="Payment history"
              subtitle="Your recent payment activity."
            />

            <div className="list-stack">
              {payments.map((payment) => (
                <div className="list-item" key={payment.id}>
                  <div>
                    <strong>Order #{payment.orderId}</strong>
                    <p>{formatDate(payment.createdAt)} • Ref: {payment.transactionRef}</p>
                  </div>
                  <div className="aligned-right">
                    <strong>{formatCurrency(payment.finalAmountPaid)}</strong>
                    <Badge>{payment.status}</Badge>
                  </div>
                </div>
              ))}
              {!payments.length ? (
                <EmptyState
                  title="No payments yet"
                  text="Your payment activity will appear here."
                />
              ) : null}
            </div>
          </section>
        </>
      )}
    </DashboardLayout>
  );
}