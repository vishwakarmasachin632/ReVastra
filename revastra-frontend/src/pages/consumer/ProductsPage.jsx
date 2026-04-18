import { useEffect, useMemo, useState } from 'react';
import DashboardLayout from '../../layouts/DashboardLayout';
import SectionHeader from '../../components/SectionHeader';
import EmptyState from '../../components/EmptyState';
import Loader from '../../components/Loader';
import { upcycleApi } from '../../api/services';
import { useAuth } from '../../context/AuthContext';
import { apiError } from '../../api/client';
import { formatCurrency, formatDate } from '../../utils/helpers';
import { usePageFeedback } from '../../utils/usePageFeedback';

export default function ProductsPage() {
  const { token } = useAuth();
  const { toast, showToast, clearToast } = usePageFeedback();

  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = async () => {
    try {
      const data = await upcycleApi.listProducts(token);
      setProducts(Array.isArray(data) ? data : []);
    } catch (err) {
      showToast(apiError(err, 'Unable to fetch products'), 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, [token]);

  const handlePurchase = async (productId) => {
    try {
      await upcycleApi.purchaseProduct(token, productId);
      showToast('Product purchased successfully', 'success');
      load();
    } catch (err) {
      showToast(apiError(err, 'Purchase failed'), 'error');
    }
  };

  const averagePrice = useMemo(() => {
    if (!products.length) return 0;
    return Math.round(
      products.reduce((sum, item) => sum + Number(item.price || 0), 0) /
        products.length
    );
  }, [products]);

  const totalValue = useMemo(
    () => products.reduce((sum, item) => sum + Number(item.price || 0), 0),
    [products]
  );

  return (
    <DashboardLayout
      title="Upcycle Store"
      subtitle="Discover unique products made from reused materials."
      toast={toast}
      clearToast={clearToast}
    >
      <section className="glass-card dashboard-hero">
        <div className="dashboard-hero-main">
          <p className="eyebrow">Sustainable shopping</p>
          <h3 className="dashboard-hero-title">
            Shop beautifully repurposed products.
          </h3>
          <p className="dashboard-hero-copy">
            Explore one-of-a-kind pieces and support circular fashion.
          </p>
          <div className="hero-note">
            Buy a product and complete payment from the Payments page.
          </div>
        </div>

        <div className="dashboard-hero-side">
          <div className="quick-highlight">
            <span className="metric-label">Products</span>
            <strong>{products.length}</strong>
            <div className="mini-note">Available now</div>
          </div>

          <div className="quick-highlight">
            <span className="metric-label">Average Price</span>
            <strong>{formatCurrency(averagePrice)}</strong>
            <div className="mini-note">Across the catalog</div>
          </div>

          <div className="quick-highlight">
            <span className="metric-label">Store Value</span>
            <strong>{formatCurrency(totalValue)}</strong>
            <div className="mini-note">Visible inventory value</div>
          </div>
        </div>
      </section>

      <section className="glass-card">
        <SectionHeader
          eyebrow="Storefront"
          title="Available upcycled products"
          subtitle="Fresh products from verified workers."
        />

        {loading ? (
          <Loader text="Loading products..." />
        ) : (
          <div className="card-grid two-col">
            {products.map((product) => (
              <div key={product.id} className="product-card">
                <div className="product-image-placeholder">
                  <div>
                    <span className="product-pill">Upcycled</span>
                    <strong>{product.title}</strong>
                  </div>
                  <span className="product-pill">{formatCurrency(product.price)}</span>
                </div>

                <div>
                  <p className="muted-text">
                    {product.description ||
                      'Thoughtfully designed repurposed fashion product.'}
                  </p>
                </div>

                <div className="product-meta list-item compact">
                  <div>
                    <span>{product.workerName || 'Worker'}</span>
                    <small>{formatDate(product.createdAt)}</small>
                  </div>
                  <strong>{formatCurrency(product.price)}</strong>
                </div>

                <button
                  className="primary-btn"
                  onClick={() => handlePurchase(product.id)}
                >
                  Buy Product
                </button>
              </div>
            ))}

            {!products.length ? (
              <EmptyState
                title="No products available"
                text="New products will appear here."
              />
            ) : null}
          </div>
        )}
      </section>
    </DashboardLayout>
  );
}