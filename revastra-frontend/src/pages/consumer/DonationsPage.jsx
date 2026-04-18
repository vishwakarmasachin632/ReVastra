import { useEffect, useMemo, useState } from 'react';
import DashboardLayout from '../../layouts/DashboardLayout';
import SectionHeader from '../../components/SectionHeader';
import Loader from '../../components/Loader';
import EmptyState from '../../components/EmptyState';
import Badge from '../../components/Badge';
import { recyclingApi } from '../../api/services';
import { useAuth } from '../../context/AuthContext';
import { apiError } from '../../api/client';
import { formatDate } from '../../utils/helpers';
import { usePageFeedback } from '../../utils/usePageFeedback';

export default function DonationsPage() {
  const { token, profile } = useAuth();
  const { toast, showToast, clearToast } = usePageFeedback();

  const [loading, setLoading] = useState(true);
  const [donations, setDonations] = useState([]);
  const [points, setPoints] = useState(null);
  const [form, setForm] = useState({
    itemCount: 2,
    description: '',
    pickupAddress: profile?.address || '',
    itemType: 'Clothes'
  });

  const load = async () => {
    try {
      const [d, p] = await Promise.all([
        recyclingApi.list(token),
        recyclingApi.points(token)
      ]);
      setDonations(Array.isArray(d) ? d : []);
      setPoints(p);
    } catch (err) {
      showToast(apiError(err, 'Unable to fetch donation data'), 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, [token]);

  const handleDonate = async (e) => {
    e.preventDefault();
    try {
      await recyclingApi.donate(token, {
        ...form,
        itemCount: Number(form.itemCount)
      });
      showToast('Donation request submitted', 'success');
      setForm((prev) => ({
        ...prev,
        description: '',
        itemCount: 2
      }));
      load();
    } catch (err) {
      showToast(apiError(err, 'Donation request failed'), 'error');
    }
  };

  const totalItems = useMemo(
    () => donations.reduce((sum, item) => sum + Number(item.itemCount || 0), 0),
    [donations]
  );

  return (
    <DashboardLayout
      title="Donations"
      subtitle="Request pickup and track donation status."
      toast={toast}
      clearToast={clearToast}
    >
      {loading ? (
        <Loader text="Loading donations..." />
      ) : (
        <>
          <section className="glass-card dashboard-hero">
            <div className="dashboard-hero-main">
              <p className="eyebrow">Sustainable impact</p>
              <h3 className="dashboard-hero-title">
                Turn unused clothes into meaningful impact.
              </h3>
              <p className="dashboard-hero-copy">
                Submit a pickup request and track your donation progress.
              </p>
              <div className="hero-note">
                Reward points are added after a successful donation.
              </div>
            </div>

            <div className="dashboard-hero-side">
              <div className="quick-highlight">
                <span className="metric-label">Requests</span>
                <strong>{donations.length}</strong>
                <div className="mini-note">Submitted by you</div>
              </div>

              <div className="quick-highlight">
                <span className="metric-label">Items</span>
                <strong>{totalItems}</strong>
                <div className="mini-note">Across all donations</div>
              </div>

              <div className="quick-highlight">
                <span className="metric-label">Rewards</span>
                <strong>{points?.currentPoints ?? points?.currentBalance ?? 0}</strong>
                <div className="mini-note">Available for payments</div>
              </div>
            </div>
          </section>

          <section className="card-grid two-col split-grid">
            <div className="glass-card">
              <SectionHeader
                eyebrow="Donation form"
                title="Request pickup"
                subtitle="Fill in the details and submit."
              />

              <form className="stack-form" onSubmit={handleDonate}>
                <label>
                  Item Count
                  <input
                    type="number"
                    min="1"
                    value={form.itemCount}
                    onChange={(e) =>
                      setForm((prev) => ({
                        ...prev,
                        itemCount: e.target.value
                      }))
                    }
                  />
                </label>

                <label>
                  Item Type
                  <input
                    value={form.itemType}
                    onChange={(e) =>
                      setForm((prev) => ({
                        ...prev,
                        itemType: e.target.value
                      }))
                    }
                  />
                </label>

                <label>
                  Description
                  <textarea
                    value={form.description}
                    onChange={(e) =>
                      setForm((prev) => ({
                        ...prev,
                        description: e.target.value
                      }))
                    }
                    placeholder="Mention cloth type, condition, quantity or pickup notes"
                  />
                </label>

                <label>
                  Pickup Address
                  <input
                    value={form.pickupAddress}
                    onChange={(e) =>
                      setForm((prev) => ({
                        ...prev,
                        pickupAddress: e.target.value
                      }))
                    }
                  />
                </label>

                <button className="primary-btn">Submit Donation</button>
              </form>
            </div>

            <div className="glass-card">
              <SectionHeader
                eyebrow="Donation history"
                title="My requests"
                subtitle="Track current and past donation requests."
              />

              <div className="list-stack">
                {donations.map((item) => (
                  <div className="list-item" key={item.id}>
                    <div>
                      <div className="history-item-head">
                        <strong>{item.itemType}</strong>
                        <Badge>{item.status}</Badge>
                      </div>
                      <p>{item.description || 'No description added.'}</p>
                      <small>
                        {formatDate(item.createdAt)} • Items: {item.itemCount ?? 0}
                      </small>
                    </div>
                  </div>
                ))}

                {!donations.length ? (
                  <EmptyState
                    title="No donations yet"
                    text="Submit your first donation request from the form."
                  />
                ) : null}
              </div>
            </div>
          </section>
        </>
      )}
    </DashboardLayout>
  );
}