import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { routeForUser } from '../utils/helpers';

export default function ProtectedRoute({ children, allowedRoles = [], requireVerified = false }) {
  const { isAuthenticated, profile } = useAuth();

  if (!isAuthenticated || !profile) return <Navigate to="/login" replace />;
  if (allowedRoles.length && !allowedRoles.includes(profile.role)) return <Navigate to={routeForUser(profile)} replace />;
  if (requireVerified && !profile.verified) return <Navigate to="/worker/pending" replace />;

  return children;
}
