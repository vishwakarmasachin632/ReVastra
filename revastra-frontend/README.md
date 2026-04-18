# ReVastra Frontend

Modern React + Vite frontend for your ReVastra microservice backend.

## What is included

- Role based login flow for `USER`, `WORKER`, `ADMIN`
- Consumer workspace
  - Dashboard
  - Verified workers list
  - Laundry booking form
  - Orders page
  - Payments and wallet page
  - Donations and reward points page
  - Upcycle products page
- Worker workspace
  - Pending approval page for unverified workers
  - Dashboard
  - Order status management
  - Product creation page
- Admin workspace
  - Dashboard
  - Pending worker verification
  - Overview page with backend enhancement notes
- Logout button on all dashboards
- JWT token persistence using localStorage
- API Gateway integration via `VITE_API_BASE_URL`

## Backend mapping used

This frontend is aligned to these gateway routes:

- `POST /api/users/register`
- `POST /api/users/login`
- `GET /api/users/profile`
- `GET /api/workers`
- `GET /api/admin/workers/pending`
- `PUT /api/admin/workers/{id}/approve`
- `POST /api/laundry/book`
- `GET /api/laundry`
- `GET /api/orders`
- `PUT /api/orders/{id}/status`
- `GET /api/payment/wallet`
- `POST /api/payment/wallet/topup`
- `POST /api/payment/pay`
- `GET /api/payment/my-payments`
- `POST /api/recycling/donate`
- `GET /api/recycling/my-donations`
- `GET /api/recycling/points`
- `GET /api/rewards/me`
- `GET /api/notifications/me`
- `GET /api/upcycle/products`
- `POST /api/upcycle/products`
- `POST /api/upcycle/purchase`

## Important backend note

Your current backend exposes only some admin features directly. These are still missing for a truly full admin dashboard:

- all users listing
- all workers listing
- all orders listing for admin
- total revenue summary for admin
- worker-specific assigned consumer orders

Because of that, the frontend has smart fallback cards where data is not yet available.

## Setup

```bash
npm install
cp .env.example .env
npm run dev
```

## Build

```bash
npm run build
```

## Default API base URL

```env
VITE_API_BASE_URL=http://localhost:8080
```

Make sure your API Gateway is running on port `8080`.

## Suggested future backend additions

For a stronger frontend, add:

- `GET /api/admin/users`
- `GET /api/admin/orders`
- `GET /api/admin/workers/all`
- `GET /api/admin/payments/summary`
- `GET /api/worker/orders`

## Tech stack

- React
- Vite
- React Router DOM
- Axios
- Custom CSS
