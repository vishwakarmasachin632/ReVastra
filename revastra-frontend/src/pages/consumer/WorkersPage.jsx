import { useEffect, useMemo, useRef, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import DashboardLayout from '../../layouts/DashboardLayout';
import SectionHeader from '../../components/SectionHeader';
import WorkerCard from '../../components/WorkerCard';
import Loader from '../../components/Loader';
import EmptyState from '../../components/EmptyState';
import { laundryApi, workerDirectoryApi } from '../../api/services';
import { useAuth } from '../../context/AuthContext';
import { apiError } from '../../api/client';
import { usePageFeedback } from '../../utils/usePageFeedback';

const serviceTypeMap = {
  laundry: 'WASH_AND_FOLD',
  ironing: 'IRONING',
  drycleaning: 'DRY_CLEAN',
  stitching: 'STITCHING',
  alteration: 'ALTERATION'
};

const allServiceOptions = [
  { label: 'WASH_AND_FOLD', value: 'WASH_AND_FOLD' },
  { label: 'DRY_CLEAN', value: 'DRY_CLEAN' },
  { label: 'IRONING', value: 'IRONING' },
  { label: 'STITCHING', value: 'STITCHING' },
  { label: 'ALTERATION', value: 'ALTERATION' }
];

const pickupTimeOptions = [
  { label: '09:00 AM', value: 'SLOT_9_AM' },
  { label: '01:00 PM', value: 'SLOT_1_PM' },
  { label: '03:00 PM', value: 'SLOT_3_PM' }
];

function withTimeout(promise, ms = 8000) {
  return Promise.race([
    promise,
    new Promise((_, reject) =>
      setTimeout(() => reject(new Error('Workers request timed out')), ms)
    )
  ]);
}

export default function WorkersPage() {
  const { token, profile } = useAuth();
  const { toast, showToast, clearToast } = usePageFeedback();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const bookingRef = useRef(null);

  const selectedService = searchParams.get('service') || '';
  const selectedLaundryType = serviceTypeMap[selectedService] || 'WASH_AND_FOLD';

  const [allWorkers, setAllWorkers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedWorker, setSelectedWorker] = useState(null);

  const [booking, setBooking] = useState({
    workerId: '',
    workerName: '',
    laundryType: selectedLaundryType,
    itemCount: 1,
    estimatedPrice: 0,
    pickupAddress: profile?.address || '',
    deliveryAddress: profile?.address || '',
    pickupDate: '',
    pickupTimeSlot: 'SLOT_9_AM'
  });

  useEffect(() => {
    setBooking((prev) => ({
      ...prev,
      laundryType: selectedLaundryType
    }));
  }, [selectedLaundryType]);

  useEffect(() => {
    let active = true;

    const loadWorkers = async () => {
      setLoading(true);

      try {
        const data = await withTimeout(workerDirectoryApi.list(token), 8000);

        if (!active) return;

        const workersArray = Array.isArray(data)
          ? data
          : Array.isArray(data?.data)
          ? data.data
          : [];

        setAllWorkers(workersArray);
      } catch (err) {
        if (!active) return;
        setAllWorkers([]);
        showToast(apiError(err, 'Unable to load workers'), 'error');
      } finally {
        if (active) setLoading(false);
      }
    };

    if (token) {
      loadWorkers();
    } else {
      setAllWorkers([]);
      setLoading(false);
    }

    return () => {
      active = false;
    };
  }, [token]);

  const filteredWorkers = useMemo(() => {
    if (!selectedService) return allWorkers;

    return allWorkers.filter((worker) => {
      switch (selectedService) {
        case 'laundry':
          return Number(worker?.laundryPricePerItem || 0) > 0;
        case 'ironing':
          return Number(worker?.ironingPricePerItem || 0) > 0;
        case 'drycleaning':
          return Number(worker?.dryCleaningPricePerItem || 0) > 0;
        case 'stitching':
          return Number(worker?.stitchingPricePerItem || 0) > 0;
        case 'alteration':
          return Number(worker?.alterationPricePerItem || 0) > 0;
        default:
          return true;
      }
    });
  }, [allWorkers, selectedService]);

  const getWorkerDisplayName = (worker) =>
    worker?.name ||
    worker?.workerName ||
    worker?.fullName ||
    worker?.username ||
    'Selected Worker';

  const getSelectedWorkerPrice = (worker, laundryTypeValue) => {
    switch (laundryTypeValue) {
      case 'WASH_AND_FOLD':
        return Number(worker?.laundryPricePerItem || 0);
      case 'IRONING':
        return Number(worker?.ironingPricePerItem || 0);
      case 'DRY_CLEAN':
        return Number(worker?.dryCleaningPricePerItem || 0);
      case 'STITCHING':
        return Number(worker?.stitchingPricePerItem || 0);
      case 'ALTERATION':
        return Number(worker?.alterationPricePerItem || 0);
      default:
        return 0;
    }
  };

  const getWorkerServiceOptions = (worker) => {
    if (!worker) return allServiceOptions;

    const options = [];

    if (Number(worker?.laundryPricePerItem || 0) > 0) {
      options.push({ label: 'WASH_AND_FOLD', value: 'WASH_AND_FOLD' });
    }
    if (Number(worker?.dryCleaningPricePerItem || 0) > 0) {
      options.push({ label: 'DRY_CLEAN', value: 'DRY_CLEAN' });
    }
    if (Number(worker?.ironingPricePerItem || 0) > 0) {
      options.push({ label: 'IRONING', value: 'IRONING' });
    }
    if (Number(worker?.stitchingPricePerItem || 0) > 0) {
      options.push({ label: 'STITCHING', value: 'STITCHING' });
    }
    if (Number(worker?.alterationPricePerItem || 0) > 0) {
      options.push({ label: 'ALTERATION', value: 'ALTERATION' });
    }

    return options.length ? options : allServiceOptions;
  };

  const availableServiceOptions = useMemo(
    () => getWorkerServiceOptions(selectedWorker),
    [selectedWorker]
  );

  const onSelect = (worker) => {
    const workerOptions = getWorkerServiceOptions(worker);

    let nextLaundryType = booking.laundryType;
    const isCurrentTypeAvailable = workerOptions.some(
      (option) => option.value === booking.laundryType
    );

    if (!isCurrentTypeAvailable) {
      nextLaundryType = workerOptions.length ? workerOptions[0].value : selectedLaundryType;
    }

    const selectedPrice = getSelectedWorkerPrice(worker, nextLaundryType);

    setSelectedWorker(worker);
    setBooking((prev) => ({
      ...prev,
      workerId: worker.userId || worker.id || '',
      workerName: getWorkerDisplayName(worker),
      laundryType: nextLaundryType,
      estimatedPrice: selectedPrice
    }));

    setTimeout(() => {
      bookingRef.current?.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
      });
    }, 120);
  };

  const handleServiceChange = (value) => {
    const updatedPrice = selectedWorker
      ? getSelectedWorkerPrice(selectedWorker, value)
      : 0;

    setBooking((prev) => ({
      ...prev,
      laundryType: value,
      estimatedPrice: updatedPrice
    }));
  };

  const computedTotal = useMemo(() => {
    const qty = Number(booking.itemCount || 0);
    const price = Number(booking.estimatedPrice || 0);
    return qty * price;
  }, [booking.itemCount, booking.estimatedPrice]);

  const estimatedDeliveryDate = useMemo(() => {
    if (!booking.pickupDate) return '';

    const date = new Date(`${booking.pickupDate}T00:00:00`);
    date.setDate(date.getDate() + 2);

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }, [booking.pickupDate]);

  const onBook = async (e) => {
    e.preventDefault();

    if (!booking.workerId) {
      showToast('Please select a worker first', 'error');
      return;
    }

    if (!booking.estimatedPrice || Number(booking.estimatedPrice) <= 0) {
      showToast('Selected worker price is missing for this service', 'error');
      return;
    }

    if (!booking.pickupDate) {
      showToast('Please select pickup date', 'error');
      return;
    }

    try {
      await laundryApi.book(token, {
        workerId: Number(booking.workerId),
        laundryType: booking.laundryType,
        itemCount: Number(booking.itemCount),
        estimatedPrice: Number(booking.estimatedPrice),
        pickupAddress: booking.pickupAddress,
        deliveryAddress: booking.deliveryAddress,
        pickupDate: booking.pickupDate,
        pickupTimeSlot: booking.pickupTimeSlot
      });

      showToast('Booking created successfully', 'success');

      setTimeout(() => {
        navigate('/consumer/payments');
      }, 700);
    } catch (err) {
      showToast(apiError(err, 'Booking failed'), 'error');
    }
  };

  return (
    <DashboardLayout
      title="Workers"
      subtitle="Choose a verified worker and book with confidence."
      toast={toast}
      clearToast={clearToast}
    >
      <section className="glass-card">
        <SectionHeader
          eyebrow="Verified workers"
          title="Choose the right worker"
          subtitle={
            selectedService
              ? `Showing workers for ${selectedService}`
              : 'Compare skills, pricing and experience.'
          }
        />

        {loading ? (
          <Loader text="Fetching workers..." />
        ) : filteredWorkers.length ? (
          <div className="card-grid two-col worker-grid">
            {filteredWorkers.map((worker) => (
              <WorkerCard
                key={worker.id || worker.userId || worker.username || Math.random()}
                worker={worker}
                onSelect={onSelect}
                selectedService={selectedService}
              />
            ))}
          </div>
        ) : (
          <EmptyState
            title="No workers found"
            text="No matching workers are available right now."
          />
        )}
      </section>

      <section className="glass-card" ref={bookingRef}>
        <SectionHeader
          eyebrow="Quick booking"
          title="Book from the worker list"
          subtitle="Select a worker, choose service and confirm pickup."
        />

        <form className="grid-form" onSubmit={onBook}>
          <label>
            Selected Worker
            <input
              value={booking.workerName}
              readOnly
              placeholder="Select a worker from the list"
              required
            />
          </label>

          <label>
            Service Type
            <select
              value={booking.laundryType}
              onChange={(e) => handleServiceChange(e.target.value)}
              disabled={!selectedWorker}
            >
              {!selectedWorker ? <option value="">Select worker first</option> : null}
              {availableServiceOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </label>

          <label>
            Total Items
            <input
              type="number"
              min="1"
              value={booking.itemCount}
              onChange={(e) =>
                setBooking((prev) => ({
                  ...prev,
                  itemCount: e.target.value
                }))
              }
              required
            />
          </label>

          <label>
            Total Amount
            <input value={computedTotal} readOnly />
          </label>

          <label>
            Pickup Date
            <input
              type="date"
              value={booking.pickupDate}
              onChange={(e) =>
                setBooking((prev) => ({
                  ...prev,
                  pickupDate: e.target.value
                }))
              }
              required
            />
          </label>

          <label>
            Pickup Slot
            <select
              value={booking.pickupTimeSlot}
              onChange={(e) =>
                setBooking((prev) => ({
                  ...prev,
                  pickupTimeSlot: e.target.value
                }))
              }
            >
              {pickupTimeOptions.map((slot) => (
                <option key={slot.value} value={slot.value}>
                  {slot.label}
                </option>
              ))}
            </select>
          </label>

          <label>
            Pickup Address
            <input
              value={booking.pickupAddress}
              onChange={(e) =>
                setBooking((prev) => ({
                  ...prev,
                  pickupAddress: e.target.value
                }))
              }
              required
            />
          </label>

          <label>
            Delivery Address
            <input
              value={booking.deliveryAddress}
              onChange={(e) =>
                setBooking((prev) => ({
                  ...prev,
                  deliveryAddress: e.target.value
                }))
              }
              required
            />
          </label>

          <label>
            Estimated Delivery
            <input value={estimatedDeliveryDate} readOnly />
          </label>

          <label>
            Price per Item
            <input value={booking.estimatedPrice} readOnly />
          </label>

          <div className="full-span">
            <button className="primary-btn">Book Now</button>
          </div>
        </form>
      </section>
    </DashboardLayout>
  );
}