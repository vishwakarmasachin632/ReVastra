import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { apiError } from '../../api/client';

export default function RegisterPage() {
  const navigate = useNavigate();
  const { register } = useAuth();

  const [form, setForm] = useState({
    name: '',
    email: '',
    password: '',
    phone: '',
    address: '',
    role: 'USER',
  });

  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const onChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      await register(form);
      setSuccess(
        form.role === 'WORKER'
          ? 'Worker registered. Wait for admin approval before full access.'
          : 'Registration successful. Please login.'
      );

      setTimeout(() => navigate('/login'), 1200);
    } catch (err) {
      setError(apiError(err, 'Registration failed'));
    }
  };

  return (
    <div className="auth-shell auth-shell-premium register-layout">
      <section className="auth-showcase glass-card auth-showcase-register">
        <div className="auth-showcase-overlay" />

        <div className="auth-showcase-content">
          <div className="brand-lockup">
            <img
              src="/assets/revastra-logo.png"
              alt="ReVastra"
              className="brand-logo-lg"
            />
            <div>
              <p className="eyebrow">Pastel premium experience</p>
              <h1 className="brand-title">Join ReVastra</h1>
            </div>
          </div>

          <div className="showcase-copy">
            <h2>Style, care and sustainability in one platform.</h2>
            <p>Register as a consumer or worker and start your journey.</p>
          </div>

          <div className="showcase-chips">
            <span>Laundry</span>
            <span>Donation</span>
            <span>Upcycle</span>
          </div>
        </div>
      </section>

      <div className="auth-card auth-card-premium auth-card-wide glass-card">
        <div className="auth-card-inner">
          <p className="eyebrow">Create account</p>
          <h2>Get started</h2>
          <p className="auth-subtext">Quick setup. Beautiful experience.</p>

          <form className="grid-form auth-grid-premium" onSubmit={onSubmit}>
            <label>
              Full name
              <input
                name="name"
                value={form.name}
                onChange={onChange}
                placeholder="Your full name"
                autoComplete="name"
                required
              />
            </label>

            <label>
              Email
              <input
                type="email"
                name="email"
                value={form.email}
                onChange={onChange}
                placeholder="Your email"
                autoComplete="email"
                required
              />
            </label>

            <label>
              Password
              <input
                type="password"
                name="password"
                value={form.password}
                onChange={onChange}
                placeholder="Create password"
                autoComplete="new-password"
                required
              />
            </label>

            <label>
              Phone
              <input
                name="phone"
                value={form.phone}
                onChange={onChange}
                placeholder="Phone number"
                autoComplete="tel"
                required
              />
            </label>

            <label className="full-span">
              Address
              <input
                name="address"
                value={form.address}
                onChange={onChange}
                placeholder="Your address"
                autoComplete="street-address"
                required
              />
            </label>

            <label className="full-span">
              Role
              <select name="role" value={form.role} onChange={onChange}>
                <option value="USER">Consumer</option>
                <option value="WORKER">Worker</option>
              </select>
            </label>

            {error ? <div className="alert error full-span">{error}</div> : null}
            {success ? <div className="alert success full-span">{success}</div> : null}

            <button className="primary-btn auth-submit-btn full-span">
              Register
            </button>
          </form>

          <p className="muted-text auth-switch-text">
            Already have an account? <Link to="/login">Back to login</Link>
          </p>
        </div>
      </div>
    </div>
  );
}