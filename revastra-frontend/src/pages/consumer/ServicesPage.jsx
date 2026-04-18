import { Link } from 'react-router-dom';
import DashboardLayout from '../../layouts/DashboardLayout';
import SectionHeader from '../../components/SectionHeader';
import { serviceOptions } from '../../utils/helpers';
import { usePageFeedback } from '../../utils/usePageFeedback';

const routeMap = {
  WASH_AND_FOLD: '/consumer/workers?service=laundry',
  STITCHING: '/consumer/workers?service=stitching',
  IRONING: '/consumer/workers?service=ironing',
  DONATION: '/consumer/donations',
  UPCYCLE: '/consumer/products'
};

const tagMap = {
  WASH_AND_FOLD: ['Pickup', 'Doorstep', 'Quick'],
  STITCHING: ['Tailoring', 'Verified', 'Support'],
  IRONING: ['Pressed', 'Fast', 'Easy'],
  DONATION: ['Impact', 'Reward', 'Pickup'],
  UPCYCLE: ['Store', 'Unique', 'Eco']
};

const descMap = {
  WASH_AND_FOLD: 'Pickup, wash and delivery.',
  STITCHING: 'Stitching and alteration by verified workers.',
  IRONING: 'Neatly pressed clothes, ready to wear.',
  DONATION: 'Donate unused clothes and earn rewards.',
  UPCYCLE: 'Shop products made from reused materials.'
};

const imageMap = {
  WASH_AND_FOLD: '/assets/services/laundry-service.webp',
  STITCHING: '/assets/services/stitching-service.webp',
  IRONING: '/assets/services/ironing-service.webp',
  DONATION: '/assets/services/donation-service.webp',
  UPCYCLE: '/assets/services/upcycle-service.webp'
};

export default function ServicesPage() {
  const { toast, clearToast } = usePageFeedback();

  return (
    <DashboardLayout
      title="Services"
      subtitle="Choose what you need and continue in a clean flow."
      toast={toast}
      clearToast={clearToast}
    >
      <section className="glass-card">
        <SectionHeader
          eyebrow="Available Services"
          title="Services for everyday clothing care"
          subtitle="Book the service you need in a few taps."
        />

        <div className="card-grid two-col services-visual-grid">
          {serviceOptions().map((service) => {
            const imageUrl = imageMap[service.value];

            return (
              <div key={service.title} className="service-card-shell">
                <div
                  className="service-tile service-tile-visual"
                  style={{
                    backgroundImage: `url("${imageUrl}")`,
                    backgroundSize: 'cover',
                    backgroundPosition: 'center',
                    backgroundRepeat: 'no-repeat',
                    backgroundColor: '#dfe7e4'
                  }}
                >
                  <div className="service-visual-overlay" />

                  <div className="service-visual-content">
                    <h3>{service.title}</h3>
                    <p>{descMap[service.value] || service.description}</p>

                    <div className="service-tag-row">
                      {(tagMap[service.value] || []).map((tag) => (
                        <span key={tag} className="service-tag service-tag-light">
                          {tag}
                        </span>
                      ))}
                    </div>

                    <div className="button-wrap">
                      <Link
                        className="primary-btn"
                        to={routeMap[service.value] || '/consumer'}
                      >
                        Open Module
                      </Link>

                      {service.value !== 'DONATION' ? (
                        <Link
                          className="ghost-btn ghost-btn-light"
                          to="/consumer/orders"
                        >
                          Track Orders
                        </Link>
                      ) : (
                        <Link
                          className="ghost-btn ghost-btn-light"
                          to="/consumer/payments"
                        >
                          Rewards
                        </Link>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </section>
    </DashboardLayout>
  );
}