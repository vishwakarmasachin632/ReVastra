export function routeForUser(profile) {
  if (!profile) return '/login';
  if (profile.role === 'ADMIN') return '/admin';
  if (profile.role === 'WORKER') return profile.verified ? '/worker' : '/worker/pending';
  return '/consumer';
}

export function formatCurrency(value) {
  const amount = Number(value || 0);
  return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 2 }).format(amount);
}

export function formatDate(value) {
  if (!value) return '—';
  return new Date(value).toLocaleString('en-IN', {
    dateStyle: 'medium',
    timeStyle: 'short'
  });
}

export function statusTone(status = '') {
  const v = String(status).toUpperCase();
  if (['SUCCESS', 'COMPLETED', 'APPROVED', 'VERIFIED', 'PAID', 'DELIVERED'].includes(v)) return 'success';
  if (['PENDING', 'IN_PROGRESS', 'PROCESSING', 'SCHEDULED'].includes(v)) return 'warning';
  return 'danger';
}

export function serviceOptions() {
  return [
    { title: 'Laundry Care', value: 'WASH_AND_FOLD', description: 'Book pickup, wash, dry and doorstep delivery.' },
    { title: 'Stitch & Alter', value: 'STITCHING', description: 'Choose verified workers for stitching and alteration.' },
    { title: 'Iron Service', value: 'IRONING', description: 'Schedule quick ironing support from available workers.' },
    { title: 'Donation & Recycling', value: 'DONATION', description: 'Donate unused clothes and earn reward points.' },
    { title: 'Upcycle Store', value: 'UPCYCLE', description: 'Shop creative products built from repurposed clothing.' }
  ];
}
