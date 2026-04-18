import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { apiError } from '../../api/client';
import { routeForUser } from '../../utils/helpers';

export default function LoginPage() {
  const navigate = useNavigate();
  const { login, loading } = useAuth();
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');

  const onChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const profile = await login(form.email, form.password);
      navigate(routeForUser(profile));
    } catch (err) {
      setError(apiError(err, 'Login failed'));
    }
  };

  return (
    <div className="auth-shell auth-shell-premium">
      <section className="auth-showcase glass-card auth-showcase-login">
        <div className="auth-showcase-overlay" />

        <div className="auth-showcase-content">
          <div className="brand-lockup">
            <img
              src="/assets/revastra-logo.png"
              alt="ReVastra"
              className="brand-logo-lg"
            />
            <div>
              <p className="eyebrow">Sustainable fashion care</p>
              <h1 className="brand-title">ReVastra</h1>
            </div>
          </div>

          <div className="showcase-copy">
            <h2>Fresh care for clothes, crafted with style.</h2>
            <p>Laundry, upcycle, donation and worker services in one elegant space.</p>
          </div>

          <div className="showcase-chips">
            <span>Eco-friendly</span>
            <span>Pickup ready</span>
            <span>Upcycle store</span>
          </div>
        </div>
      </section>

      <form className="auth-card auth-card-premium glass-card" onSubmit={onSubmit}>
        <div className="auth-card-inner">
          <p className="eyebrow">Welcome back</p>
          <h2>Sign in</h2>
          <p className="auth-subtext">Clean, simple, and ready.</p>

          <label>
            Email
            <input
              type="email"
              name="email"
              value={form.email}
              onChange={onChange}
              placeholder="Enter your email"
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
              placeholder="Enter your password"
              autoComplete="current-password"
              required
            />
          </label>

          {error ? <div className="alert error">{error}</div> : null}

          <button className="primary-btn auth-submit-btn" disabled={loading}>
            {loading ? 'Signing in...' : 'Login'}
          </button>

          <p className="muted-text auth-switch-text">
            New here? <Link to="/register">Create account</Link>
          </p>
        </div>
      </form>
    </div>
  );
}