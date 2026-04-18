import Badge from './Badge';

const formatCurrency = (value) => `₹${Number(value || 0)} / item`;

export default function WorkerCard({ worker, onSelect, extra, selectedService }) {
  const workerName =
    worker?.name ||
    worker?.workerName ||
    worker?.fullName ||
    worker?.username ||
    'Worker';

  const isVerified = worker?.verified || worker?.verifiedBadge;

  const phone =
    worker?.phone ||
    worker?.phoneNumber ||
    worker?.mobile ||
    worker?.contactNumber ||
    'Not available';

  const skills = Array.isArray(worker?.skills)
    ? worker.skills.join(', ')
    : worker?.skills || 'General service support';

  const experience = worker?.experienceYears
    ? `${worker.experienceYears} year${worker.experienceYears > 1 ? 's' : ''}`
    : worker?.experience
    ? `${worker.experience} years`
    : 'Not mentioned';

  const rating =
    typeof worker?.rating === 'number' ? worker.rating.toFixed(1) : '0.0';

  const reviewCount = worker?.reviewCount ?? 0;

  const servicePrices = [
    { key: 'laundry', label: 'Laundry', price: worker?.laundryPricePerItem },
    { key: 'ironing', label: 'Ironing', price: worker?.ironingPricePerItem },
    { key: 'drycleaning', label: 'Dry Clean', price: worker?.dryCleaningPricePerItem },
    { key: 'stitching', label: 'Stitching', price: worker?.stitchingPricePerItem },
    { key: 'alteration', label: 'Alteration', price: worker?.alterationPricePerItem }
  ];

  const availableServicePrices = servicePrices.filter(
    (service) =>
      service.price !== null &&
      service.price !== undefined &&
      service.price !== '' &&
      Number(service.price) > 0
  );

  const selectedServiceKey = (selectedService || '').toLowerCase();

  return (
    <div className="worker-card-clean">
      <div className="worker-card-clean__header">
        <div className="worker-card-clean__title-wrap">
          <h3>{workerName}</h3>
          <p>{skills}</p>
        </div>

        <Badge tone={isVerified ? 'success' : 'warning'}>
          {isVerified ? 'Verified' : 'Pending'}
        </Badge>
      </div>

      <div className="worker-card-clean__meta">
        <div className="worker-meta-box">
          <span>Phone</span>
          <strong>{phone}</strong>
        </div>

        <div className="worker-meta-box">
          <span>Experience</span>
          <strong>{experience}</strong>
        </div>

        <div className="worker-meta-box worker-meta-box--full">
          <span>Skills</span>
          <strong>{skills}</strong>
        </div>

        <div className="worker-meta-box">
          <span>Rating</span>
          <strong>{rating} / 5</strong>
        </div>

        <div className="worker-meta-box">
          <span>Reviews</span>
          <strong>{reviewCount}</strong>
        </div>
      </div>

      <div className="worker-card-clean__charges">
        <h4>Service Charges</h4>

        {availableServicePrices.length ? (
          <div className="worker-charge-list">
            {availableServicePrices.map((service) => {
              const isActive = selectedServiceKey === service.key.toLowerCase();

              return (
                <div
                  key={service.key}
                  className={`worker-charge-row ${isActive ? 'active' : ''}`}
                >
                  <span>{service.label}</span>
                  <strong>{formatCurrency(service.price)}</strong>
                </div>
              );
            })}
          </div>
        ) : (
          <div className="worker-charge-empty">No service pricing available</div>
        )}
      </div>

      <div className="worker-card-clean__footer">
        <p>{worker?.bio || 'Available through ReVastra verified marketplace.'}</p>
      </div>

      {extra}

      {onSelect ? (
        <button
          type="button"
          className="primary-btn worker-select-btn"
          onClick={() => onSelect(worker)}
        >
          Select Worker
        </button>
      ) : null}
    </div>
  );
}