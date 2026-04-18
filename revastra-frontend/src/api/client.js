import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

export function withAuth(token) {
  return {
    headers: {
      Authorization: `Bearer ${token}`
    }
  };
}

export async function tryRequests(requestFactories = []) {
  let lastError;
  for (const requestFactory of requestFactories) {
    try {
      const response = await requestFactory();
      return response.data;
    } catch (error) {
      lastError = error;
      const status = error?.response?.status;
      if (status && ![404, 405, 500].includes(status)) {
        throw error;
      }
    }
  }
  throw lastError;
}

export function apiError(error, fallback = 'Something went wrong') {
  const payload = error?.response?.data;
  if (typeof payload === 'string') return payload;
  return payload?.message || error?.message || fallback;
}
