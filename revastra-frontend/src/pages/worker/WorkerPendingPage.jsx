import { useRef, useState } from 'react';
import DashboardLayout from '../../layouts/DashboardLayout';
import { useAuth } from '../../context/AuthContext';
import { usePageFeedback } from '../../utils/usePageFeedback';
import { apiError } from '../../api/client';
import { upcycleApi } from '../../api/services';

export default function WorkerPendingPage() {
  const { profile, token } = useAuth();
  const { toast, showToast, clearToast } = usePageFeedback();

  const topRef = useRef(null);
  const otpSectionRef = useRef(null);

  const [registration, setRegistration] = useState({
    skills: '',
    bio: '',
    experienceYears: '',
    laundryPricePerItem: '',
    ironingPricePerItem: '',
    dryCleaningPricePerItem: '',
    stitchingPricePerItem: '',
    alterationPricePerItem: ''
  });

  const [workerProfileId, setWorkerProfileId] = useState('');
  const [otp, setOtp] = useState('');
  const [busy, setBusy] = useState(false);

  const handleRegistrationChange = (e) => {
    const { name, value } = e.target;
    setRegistration((prev) => ({
      ...prev,
      [name]: value
    }));
  };

  const scrollToOtpSection = () => {
    setTimeout(() => {
      otpSectionRef.current?.scrollIntoView({
        behavior: 'smooth',
        block: 'center'
      });
    }, 180);
  };

  const scrollToTopSection = () => {
    setTimeout(() => {
      topRef.current?.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
      });
    }, 220);
  };

  const handleProfileCreate = async (e) => {
    e.preventDefault();
    setBusy(true);

    try {
      const payload = {
        skills: registration.skills,
        bio: registration.bio,
        experienceYears: registration.experienceYears
          ? Number(registration.experienceYears)
          : null,
        laundryPricePerItem: registration.laundryPricePerItem
          ? Number(registration.laundryPricePerItem)
          : null,
        ironingPricePerItem: registration.ironingPricePerItem
          ? Number(registration.ironingPricePerItem)
          : null,
        dryCleaningPricePerItem: registration.dryCleaningPricePerItem
          ? Number(registration.dryCleaningPricePerItem)
          : null,
        stitchingPricePerItem: registration.stitchingPricePerItem
          ? Number(registration.stitchingPricePerItem)
          : null,
        alterationPricePerItem: registration.alterationPricePerItem
          ? Number(registration.alterationPricePerItem)
          : null
      };

      const result = await upcycleApi.registerWorkerProfile(token, payload);
      const profileId = result?.id || '';

      setWorkerProfileId(profileId);

      if (!profileId) {
        showToast('Worker profile created, but profile ID is missing.', 'error');
        return;
      }

      await upcycleApi.sendOtp(token, Number(profileId));
      showToast('OTP sent successfully', 'success');
      scrollToOtpSection();
    } catch (err) {
      showToast(apiError(err, 'Unable to create worker profile'), 'error');
    } finally {
      setBusy(false);
    }
  };

  const handleSendOtp = async () => {
    if (!workerProfileId) {
      showToast('Enter worker profile id first', 'error');
      return;
    }

    setBusy(true);
    try {
      await upcycleApi.sendOtp(token, Number(workerProfileId));
      showToast('OTP sent successfully', 'success');
      scrollToOtpSection();
    } catch (err) {
      showToast(apiError(err, 'Unable to send OTP'), 'error');
    } finally {
      setBusy(false);
    }
  };

  const handleVerifyOtp = async () => {
    if (!workerProfileId || !otp) {
      showToast('Enter worker profile id and OTP', 'error');
      return;
    }

    setBusy(true);
    try {
      await upcycleApi.verifyOtp(token, {
        workerProfileId: Number(workerProfileId),
        otp
      });

      showToast('OTP verified successfully', 'success');

      setTimeout(() => {
        showToast('Wait for Admin approval', 'success');
      }, 900);

      scrollToTopSection();
    } catch (err) {
      const message = apiError(err, 'OTP verification failed');
      const normalized = String(message || '').toLowerCase();

      if (normalized.includes('otp')) {
        showToast('Wrong OTP', 'error');
      } else {
        showToast(message, 'error');
      }
    } finally {
      setBusy(false);
    }
  };

  return (
    <DashboardLayout
      title="Worker Approval Pending"
      subtitle="This workspace opens fully after OTP verification and admin approval"
      toast={toast}
      clearToast={clearToast}
    >
      <section className="card-grid two-col split-grid" ref={topRef}>
        <div className="glass-card center-card pending-card">
          <img src="/assets/revastra-logo.png" alt="ReVastra" className="pending-logo" />
          <h2>Hello {profile?.name}</h2>
          <p>
            Your worker account is registered but not verified by admin yet. Until approval,
            worker-only pages remain blocked.
          </p>

          <div className="feature-list centered">
            <span>Create worker profile</span>
            <span>Verify OTP</span>
            <span>Wait for admin approval</span>
          </div>
        </div>

        <div className="glass-card">
          <p className="eyebrow">Setup Steps</p>
          <h2>Complete your worker onboarding</h2>

          <form className="stack-form" onSubmit={handleProfileCreate}>
            <label>
              Skills
              <input
                name="skills"
                value={registration.skills}
                onChange={handleRegistrationChange}
                placeholder="Laundry, stitching, ironing"
                required
              />
            </label>

            <label>
              Bio
              <textarea
                name="bio"
                value={registration.bio}
                onChange={handleRegistrationChange}
                placeholder="Describe your experience"
                required
              />
            </label>

            <label>
              Experience (Years)
              <input
                type="number"
                name="experienceYears"
                min="0"
                max="50"
                value={registration.experienceYears}
                onChange={handleRegistrationChange}
                placeholder="e.g. 3"
              />
            </label>

            <label>
              Laundry Price Per Item
              <input
                type="number"
                step="0.01"
                name="laundryPricePerItem"
                value={registration.laundryPricePerItem}
                onChange={handleRegistrationChange}
                placeholder="e.g. 15"
              />
            </label>

            <label>
              Ironing Price Per Item
              <input
                type="number"
                step="0.01"
                name="ironingPricePerItem"
                value={registration.ironingPricePerItem}
                onChange={handleRegistrationChange}
                placeholder="e.g. 5"
              />
            </label>

            <label>
              Dry Cleaning Price Per Item
              <input
                type="number"
                step="0.01"
                name="dryCleaningPricePerItem"
                value={registration.dryCleaningPricePerItem}
                onChange={handleRegistrationChange}
                placeholder="e.g. 25"
              />
            </label>

            <label>
              Stitching Price Per Item
              <input
                type="number"
                step="0.01"
                name="stitchingPricePerItem"
                value={registration.stitchingPricePerItem}
                onChange={handleRegistrationChange}
                placeholder="e.g. 80"
              />
            </label>

            <label>
              Alteration Price Per Item
              <input
                type="number"
                step="0.01"
                name="alterationPricePerItem"
                value={registration.alterationPricePerItem}
                onChange={handleRegistrationChange}
                placeholder="e.g. 40"
              />
            </label>

            <button className="primary-btn" disabled={busy}>
              {busy ? 'Please wait...' : 'Create Worker Profile'}
            </button>
          </form>

          <div className="stack-form top-gap" ref={otpSectionRef}>
            <label>
              Worker Profile ID
              <input
                value={workerProfileId}
                onChange={(e) => setWorkerProfileId(e.target.value)}
                placeholder="Auto-filled after profile creation"
              />
            </label>

            <div className="button-wrap">
              <button
                className="ghost-btn"
                type="button"
                onClick={handleSendOtp}
                disabled={busy}
              >
                Send OTP
              </button>
            </div>

            <label>
              OTP
              <input
                value={otp}
                onChange={(e) => setOtp(e.target.value)}
                placeholder="Enter received OTP"
              />
            </label>

            <button
              className="primary-btn"
              type="button"
              onClick={handleVerifyOtp}
              disabled={busy}
            >
              Verify OTP
            </button>
          </div>
        </div>
      </section>
    </DashboardLayout>
  );
}