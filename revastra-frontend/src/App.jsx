import { Navigate, Route, Routes } from 'react-router-dom';
import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';
import ProtectedRoute from './components/ProtectedRoute';
import ConsumerDashboard from './pages/consumer/ConsumerDashboard';
import ServicesPage from './pages/consumer/ServicesPage';
import WorkersPage from './pages/consumer/WorkersPage';
import OrdersPage from './pages/consumer/OrdersPage';
import PaymentsPage from './pages/consumer/PaymentsPage';
import DonationsPage from './pages/consumer/DonationsPage';
import ProductsPage from './pages/consumer/ProductsPage';
import WorkerPendingPage from './pages/worker/WorkerPendingPage';
import WorkerDashboard from './pages/worker/WorkerDashboard';
import WorkerOrdersPage from './pages/worker/WorkerOrdersPage';
import WorkerProductsPage from './pages/worker/WorkerProductsPage';
import AdminDashboard from './pages/admin/AdminDashboard';
import AdminWorkersPage from './pages/admin/AdminWorkersPage';
import AdminOverviewPage from './pages/admin/AdminOverviewPage';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      <Route
        path="/consumer"
        element={
          <ProtectedRoute allowedRoles={["USER"]}>
            <ConsumerDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/consumer/services"
        element={
          <ProtectedRoute allowedRoles={["USER"]}>
            <ServicesPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/consumer/workers"
        element={
          <ProtectedRoute allowedRoles={["USER"]}>
            <WorkersPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/consumer/orders"
        element={
          <ProtectedRoute allowedRoles={["USER"]}>
            <OrdersPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/consumer/payments"
        element={
          <ProtectedRoute allowedRoles={["USER"]}>
            <PaymentsPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/consumer/donations"
        element={
          <ProtectedRoute allowedRoles={["USER"]}>
            <DonationsPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/consumer/products"
        element={
          <ProtectedRoute allowedRoles={["USER"]}>
            <ProductsPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/worker/pending"
        element={
          <ProtectedRoute allowedRoles={["WORKER"]}>
            <WorkerPendingPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/worker"
        element={
          <ProtectedRoute allowedRoles={["WORKER"]} requireVerified>
            <WorkerDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/worker/orders"
        element={
          <ProtectedRoute allowedRoles={["WORKER"]} requireVerified>
            <WorkerOrdersPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/worker/products"
        element={
          <ProtectedRoute allowedRoles={["WORKER"]} requireVerified>
            <WorkerProductsPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin"
        element={
          <ProtectedRoute allowedRoles={["ADMIN"]}>
            <AdminDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/workers"
        element={
          <ProtectedRoute allowedRoles={["ADMIN"]}>
            <AdminWorkersPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/overview"
        element={
          <ProtectedRoute allowedRoles={["ADMIN"]}>
            <AdminOverviewPage />
          </ProtectedRoute>
        }
      />

      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}
