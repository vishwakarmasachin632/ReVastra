import { apiClient, tryRequests, withAuth } from './client';

const auth = (token) => withAuth(token);

const normalizeWorker = (worker = {}) => ({
  ...worker,
  id: worker.id,
  userId: worker.userId ?? worker.id,
  name: worker.name || worker.workerName,
  verified: Boolean(worker.verified ?? worker.verifiedBadge ?? worker.adminApproved),
  phone: worker.phone || 'Not available',
  skills: worker.skills || 'Not added',
  bio: worker.bio || '',
  experienceYears: worker.experienceYears ?? null,
  rating: worker.rating ?? 0,
  reviewCount: worker.reviewCount ?? 0,
  reviews: Array.isArray(worker.reviews) ? worker.reviews : [],
  laundryPricePerItem: worker.laundryPricePerItem ?? null,
  ironingPricePerItem: worker.ironingPricePerItem ?? null,
  dryCleaningPricePerItem: worker.dryCleaningPricePerItem ?? null,
  stitchingPricePerItem: worker.stitchingPricePerItem ?? null,
  alterationPricePerItem: worker.alterationPricePerItem ?? null
});

const normalizeOrder = (order = {}) => ({
  ...order,
  id: order.id,
  serviceType: String(order.serviceType || '').toUpperCase(),
  status: String(order.status || '').toUpperCase(),
  totalAmount: order.totalAmount ?? 0,
  consumerName: order.consumerName || 'N/A',
  consumerPhone: order.consumerPhone || 'N/A',
  paymentStatus: order.paymentStatus || 'PENDING',
  paymentTime: order.paymentTime || null,
  createdAt: order.createdAt || null,
  completedAt: order.completedAt || null
});

export const authApi = {
  register: async (payload) => (await apiClient.post('/api/users/register', payload)).data,
  login: async (payload) => (await apiClient.post('/api/users/login', payload)).data,
  getProfile: async (token) => (await apiClient.get('/api/users/profile', auth(token))).data,
  updateProfile: async (token, payload) =>
    (await apiClient.put('/api/users/profile', payload, auth(token))).data
};

export const adminApi = {
  getPendingWorkers: async (token) =>
    (await apiClient.get('/api/admin/workers/pending', auth(token))).data,

  approveWorker: async (token, userId, verified) =>
    (await apiClient.put(`/api/admin/workers/${userId}/approve`, { verified }, auth(token))).data,

  getAllWorkerProfiles: async (token) =>
    tryRequests([
      () => apiClient.get('/api/workers/admin/all', auth(token))
    ]),

  getUserStats: async (token) =>
    tryRequests([
      () => apiClient.get('/api/admin/stats/users', auth(token))
    ]),

  getConsumers: async (token) =>
    tryRequests([
      () => apiClient.get('/api/admin/consumers', auth(token)),
      () => apiClient.get('/api/admin/users', auth(token))
    ]),

  getWorkers: async (token) =>
    tryRequests([
      () => apiClient.get('/api/admin/workers', auth(token)),
      () => apiClient.get('/api/admin/users', auth(token))
    ]),

  getOrders: async (token) => {
    const items = await tryRequests([
      () => apiClient.get('/api/orders/admin/all', auth(token)),
      () => apiClient.get('/api/orders/admin', auth(token))
    ]);
    return (Array.isArray(items) ? items : []).map(normalizeOrder);
  },

  getOrderStats: async (token) =>
    tryRequests([
      () => apiClient.get('/api/orders/admin/stats', auth(token))
    ]),

  getPaymentSummary: async (token) =>
    tryRequests([
      () => apiClient.get('/api/payment/admin/summary', auth(token))
    ]),

  getPayments: async (token) =>
    tryRequests([
      () => apiClient.get('/api/payment/admin/all', auth(token))
    ])
};

export const workerDirectoryApi = {
  list: async (token) => {
    const data = await tryRequests([
      () => apiClient.get('/api/workers', auth(token)),
      () => apiClient.get('/api/users/workers/verified', auth(token)),
      () => apiClient.get('/api/workers', auth(token))
    ]);
    return (Array.isArray(data) ? data : []).map(normalizeWorker);
  },

  listByService: async (token, serviceType) => {
    const data = await tryRequests([
      () => apiClient.get(`/api/workers/service/${serviceType}`, auth(token))
    ]);
    return (Array.isArray(data) ? data : []).map(normalizeWorker);
  },

  getById: async (token, workerId) => {
    const data = await tryRequests([
      () => apiClient.get(`/api/workers/${workerId}`, auth(token)),
      () => apiClient.get(`/api/users/workers/${workerId}`, auth(token)),
      () => apiClient.get(`/api/workers/${workerId}`, auth(token))
    ]);
    return normalizeWorker(data);
  }
};

export const laundryApi = {
  book: async (token, payload) =>
    (await apiClient.post('/api/laundry/book', payload, auth(token))).data,

  list: async (token) =>
    (await apiClient.get('/api/laundry', auth(token))).data,

  cancel: async (token, bookingId) =>
    (await apiClient.put(`/api/laundry/${bookingId}/cancel`, {}, auth(token))).data
};

export const orderApi = {
  create: async (token, payload) =>
    (await apiClient.post('/api/orders', payload, auth(token))).data,

  list: async (token) => {
    const { data } = await apiClient.get('/api/orders', auth(token));
    return (Array.isArray(data) ? data : []).map(normalizeOrder);
  },

  listForWorker: async (token) => {
    const data = await tryRequests([
      () => apiClient.get('/api/orders/worker/my-orders', auth(token)),
      () => apiClient.get('/api/orders/worker', auth(token)),
      () => apiClient.get('/api/orders/worker/me', auth(token))
    ]);
    return (Array.isArray(data) ? data : []).map(normalizeOrder);
  },

  listForAdmin: async (token) => {
    const data = await tryRequests([
      () => apiClient.get('/api/orders/admin/all', auth(token)),
      () => apiClient.get('/api/orders/admin', auth(token))
    ]);
    return (Array.isArray(data) ? data : []).map(normalizeOrder);
  },

  getStats: async (token) =>
    tryRequests([
      () => apiClient.get('/api/orders/admin/stats', auth(token))
    ]),

  getById: async (token, orderId) =>
    (await apiClient.get(`/api/orders/${orderId}`, auth(token))).data,

  updateStatus: async (token, orderId, status) =>
    (await apiClient.put(`/api/orders/${orderId}/status`, { status }, auth(token))).data
};

export const paymentApi = {
  pay: async (token, payload) =>
    (await apiClient.post('/api/payment/pay', payload, auth(token))).data,

  wallet: async (token) =>
    (await apiClient.get('/api/payment/wallet', auth(token))).data,

  topup: async (token, amount) =>
    (await apiClient.post('/api/payment/wallet/topup', { amount }, auth(token))).data,

  list: async (token) =>
    (await apiClient.get('/api/payment/my-payments', auth(token))).data,

  summary: async (token) =>
    tryRequests([
      () => apiClient.get('/api/payment/admin/summary', auth(token))
    ]),

  all: async (token) =>
    tryRequests([
      () => apiClient.get('/api/payment/admin/all', auth(token))
    ])
};

export const rewardApi = {
  me: async (token) =>
    (await apiClient.get('/api/rewards/me', auth(token))).data
};

export const recyclingApi = {
  donate: async (token, payload) =>
    (await apiClient.post('/api/recycling/donate', payload, auth(token))).data,

  list: async (token) =>
    (await apiClient.get('/api/recycling/my-donations', auth(token))).data,

  points: async (token) =>
    (await apiClient.get('/api/recycling/points', auth(token))).data
};

export const upcycleApi = {
  registerWorkerProfile: async (token, payload) =>
    (await apiClient.post('/api/workers/register', payload, auth(token))).data,

  sendOtp: async (token, workerProfileId) =>
    (await apiClient.post('/api/workers/send-otp', { workerProfileId }, auth(token))).data,

  verifyOtp: async (token, payload) =>
    (await apiClient.post('/api/workers/verify-otp', payload, auth(token))).data,

  workerProfiles: async (token) =>
    (await apiClient.get('/api/workers', auth(token))).data,

  getWorkerProfile: async (token, workerProfileId) =>
    (await apiClient.get(`/api/workers/${workerProfileId}`, auth(token))).data,

  getMyWorkerProfile: async (token) =>
    tryRequests([
      () => apiClient.get('/api/workers/me', auth(token))
    ]),

  addProduct: async (token, payload) =>
    (await apiClient.post('/api/upcycle/products', payload, auth(token))).data,

  listProducts: async (token) =>
    (await apiClient.get('/api/upcycle/products', auth(token))).data,

  addReview: async (token, payload) =>
    (await apiClient.post('/api/workers/reviews', payload, auth(token))).data,

  getReviews: async (token, workerProfileId) =>
    (await apiClient.get(`/api/workers/reviews/${workerProfileId}`, auth(token))).data,

  purchaseProduct: async (token, productId) =>
    (await apiClient.post('/api/upcycle/purchase', { productId }, auth(token))).data
};

export const notificationApi = {
  list: async (token) =>
    (await apiClient.get('/api/notifications/me', auth(token))).data,

  updateStatus: async (token, notificationId, status) =>
    (await apiClient.put(`/api/notifications/${notificationId}/status`, { status }, auth(token))).data
};