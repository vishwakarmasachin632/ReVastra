import { useEffect, useState } from 'react';
import DashboardLayout from '../../layouts/DashboardLayout';
import SectionHeader from '../../components/SectionHeader';
import Loader from '../../components/Loader';
import EmptyState from '../../components/EmptyState';
import { upcycleApi } from '../../api/services';
import { useAuth } from '../../context/AuthContext';
import { apiError } from '../../api/client';
import { formatCurrency, formatDate } from '../../utils/helpers';
import { usePageFeedback } from '../../utils/usePageFeedback';

export default function WorkerProductsPage() {
  const { token } = useAuth();
  const { toast, showToast, clearToast } = usePageFeedback();
  const [loading, setLoading] = useState(true);
  const [products, setProducts] = useState([]);
  const [form, setForm] = useState({ title: '', description: '', price: 499 });

  const load = async () => {
    try {
      const data = await upcycleApi.listProducts(token);
      setProducts(data);
    } catch (err) {
      showToast(apiError(err, 'Unable to fetch products'), 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, [token]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await upcycleApi.addProduct(token, { ...form, price: Number(form.price) });
      showToast('Product added successfully', 'success');
      setForm({ title: '', description: '', price: 499 });
      load();
    } catch (err) {
      showToast(apiError(err, 'Unable to add product'), 'error');
    }
  };

  return (
    <DashboardLayout title="Worker Products" subtitle="Add upcycled products and view the public listing" toast={toast} clearToast={clearToast}>
      <section className="card-grid two-col split-grid">
        <div className="glass-card">
          <SectionHeader eyebrow="Product Form" title="Add a new upcycled item" />
          <form className="stack-form" onSubmit={handleSubmit}>
            <label>Title<input value={form.title} onChange={(e) => setForm((prev) => ({ ...prev, title: e.target.value }))} required /></label>
            <label>Description<textarea value={form.description} onChange={(e) => setForm((prev) => ({ ...prev, description: e.target.value }))} required /></label>
            <label>Price<input type="number" min="1" value={form.price} onChange={(e) => setForm((prev) => ({ ...prev, price: e.target.value }))} required /></label>
            <button className="primary-btn">Add Product</button>
          </form>
        </div>
        <div className="glass-card">
          <SectionHeader eyebrow="Public Listing" title="Current products" subtitle="What consumers can browse and purchase." />
          {loading ? <Loader text="Loading products..." /> : <div className="list-stack">{products.map((product) => <div className="list-item" key={product.id}><div><strong>{product.title}</strong><p>{product.description}</p><small>{formatDate(product.createdAt)}</small></div><strong>{formatCurrency(product.price)}</strong></div>)}{!products.length ? <EmptyState title="No products listed" text="Create your first upcycled product from the form." /> : null}</div>}
        </div>
      </section>
    </DashboardLayout>
  );
}
