import { useEffect, useState } from 'react';
import DashboardLayout from '../../layouts/DashboardLayout';
import SectionHeader from '../../components/SectionHeader';
import EmptyState from '../../components/EmptyState';
import Loader from '../../components/Loader';
import Badge from '../../components/Badge';
import { adminApi } from '../../api/services';
import { useAuth } from '../../context/AuthContext';
import { apiError } from '../../api/client';
import { formatDate } from '../../utils/helpers';
import { usePageFeedback } from '../../utils/usePageFeedback';

export default function AdminWorkersPage() {
  const { token } = useAuth();
  const { toast, showToast, clearToast } = usePageFeedback();
  const [loading, setLoading] = useState(true);
  const [workers, setWorkers] = useState([]);

  const load = async () => {
    try {
      setLoading(true);

      const [users, profiles] = await Promise.all([
        adminApi.getPendingWorkers(token),
        adminApi.getAllWorkerProfiles(token)
      ]);

      const mergedWorkers = users.map((user) => {
        const profile = profiles.find((p) => p.userId === user.id);

        return {
          ...user,
          workerProfileId: profile?.id || null,
          workerName: profile?.workerName || user.name,
          skills: profile?.skills || 'Not added',
          bio: profile?.bio || 'Not added',
          otpVerified: profile?.otpVerified || false,
          adminApproved: profile?.adminApproved || false,
          verifiedBadge: profile?.verifiedBadge || false,
          rating: profile?.rating ?? 0
        };
      });

      setWorkers(mergedWorkers);
    } catch (err) {
      showToast(apiError(err, 'Unable to fetch workers'), 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (token) {
      load();
    }
  }, [token]);

  const handleApproval = async (userId, verified) => {
    try {
      await adminApi.approveWorker(token, userId, verified);
      showToast(`Worker ${verified ? 'approved' : 'updated'} successfully`, 'success');
      load();
    } catch (err) {
      showToast(apiError(err, 'Approval action failed'), 'error');
    }
  };

  return (
    <DashboardLayout
      title="Worker Verification"
      subtitle="Approve or hold worker accounts from admin panel"
      toast={toast}
      clearToast={clearToast}
    >
      <section className="glass-card">
        <SectionHeader
          eyebrow="Pending Queue"
          title="Verification actions"
          
        />

        {loading ? (
          <Loader text="Loading worker queue..." />
        ) : (
          <div className="list-stack">
            {workers.map((worker) => (
              <div className="list-item" key={worker.id}>
                <div style={{ flex: 1 }}>
                  <strong>{worker.workerName || worker.name}</strong>
                  <p>{worker.email} • {worker.phone}</p>
                  <small>Joined {formatDate(worker.createdAt)}</small>

                  <div style={{ marginTop: '12px', lineHeight: '1.8' }}>
                    <p><strong>Skills:</strong> {worker.skills}</p>
                    <p><strong>Bio:</strong> {worker.bio}</p>
                    <p><strong>OTP Verified:</strong> {worker.otpVerified ? 'Yes' : 'No'}</p>
                    <p><strong>Admin Approved:</strong> {worker.adminApproved ? 'Yes' : 'No'}</p>
                    <p><strong>Verified Badge:</strong> {worker.verifiedBadge ? 'Yes' : 'No'}</p>
                    <p><strong>Rating:</strong> {worker.rating}</p>
                  </div>
                </div>

                <div className="button-wrap">
                  <Badge>{worker.verified ? 'Verified' : 'Pending'}</Badge>
                  <button
                    className="primary-btn"
                    onClick={() => handleApproval(worker.id, true)}
                  >
                    Approve
                  </button>
                  <button
                    className="ghost-btn"
                    onClick={() => handleApproval(worker.id, false)}
                  >
                    Mark Pending
                  </button>
                </div>
              </div>
            ))}

            {!workers.length ? (
              <EmptyState
                title="No workers waiting"
                text="Approval queue is empty right now."
              />
            ) : null}
          </div>
        )}
      </section>
    </DashboardLayout>
  );
}