import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Toast from '../components/Toast';

const menus = {
  USER: [
    { to: '/consumer', label: 'Dashboard', short: 'Home' },
    { to: '/consumer/services', label: 'Services', short: 'Book' },
    { to: '/consumer/workers', label: 'Workers', short: 'Find' },
    { to: '/consumer/orders', label: 'Orders', short: 'Track' },
    { to: '/consumer/payments', label: 'Payments', short: 'Wallet' },
    { to: '/consumer/donations', label: 'Donations', short: 'Give' },
    { to: '/consumer/products', label: 'Upcycle Store', short: 'Shop' }
  ],
  WORKER: [
    { to: '/worker', label: 'Dashboard', short: 'Home' },
    { to: '/worker/orders', label: 'Orders', short: 'Track' },
    { to: '/worker/products', label: 'Products', short: 'Store' }
  ],
  ADMIN: [
    { to: '/admin', label: 'Dashboard', short: 'Home' },
    { to: '/admin/workers', label: 'Workers Approval', short: 'Review' },
    { to: '/admin/overview', label: 'Overview', short: 'Stats' }
  ]
};

export default function DashboardLayout({ title, subtitle, children, toast, clearToast }) {
  const location = useLocation();
  const navigate = useNavigate();
  const { profile, logout } = useAuth();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const menuItems = menus[profile?.role] || [];

  return (
    <div className="app-shell premium-dashboard-shell">
      <aside className="sidebar glass-card premium-sidebar">
        <div>
          <div className="brand-wrap premium-brand-wrap">
            <img
              src="/assets/revastra-logo.png"
              alt="ReVastra"
              className="brand-mark premium-brand-mark"
            />
            <div>
              <h1>ReVastra</h1>
              <p>Smart clothing care</p>
            </div>
          </div>

          <div className="profile-mini premium-profile-card">
            <img
              src="/assets/dummy-profile.jpg"
              alt="Profile"
              className="profile-avatar-image"
            />
            <div className="profile-copy">
              <strong>{profile?.name || 'User'}</strong>
              <span className="profile-role-pill">
                {profile?.role === 'USER' ? 'Consumer' : profile?.role}
              </span>
              <small>{profile?.email}</small>
            </div>
          </div>

          <p className="sidebar-group-title">Menu</p>

          <nav className="nav-links premium-nav-links">
            {menuItems.map((item) => (
              <Link
                key={item.to}
                to={item.to}
                className={location.pathname === item.to ? 'active' : ''}
              >
                <span className="nav-main-copy">
                  <strong>{item.label}</strong>
                  <small>{item.short}</small>
                </span>
                <span className="nav-arrow">›</span>
              </Link>
            ))}
          </nav>
        </div>

        <button className="ghost-btn danger-outline sidebar-logout-btn" onClick={handleLogout}>
          Logout
        </button>
      </aside>

      <main className="content-area premium-content-area">
        <header className="page-top glass-card premium-page-top">
          <div className="page-top-copy">
            <p className="eyebrow">Workspace</p>
            <h2>{title}</h2>
            <p className="section-subtitle">{subtitle}</p>
          </div>

          <div className="hero-chip-wrap premium-chip-wrap">
            <span className="hero-chip">Fast actions</span>
            <span className="hero-chip">Clean flow</span>
            <span className="hero-chip">Eco lifestyle</span>
          </div>
        </header>

        {children}
      </main>

      <Toast toast={toast} onClose={clearToast} />
    </div>
  );
}