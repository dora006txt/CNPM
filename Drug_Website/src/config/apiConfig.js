const API_BASE_URL = "http://localhost:8080";
export const API_ENDPOINTS = {
    LOGIN: `${API_BASE_URL}/api/auth/login`,
    REGISTER: `${API_BASE_URL}/api/auth/register`,
    CHANGE_PASSWORD: (userId) => `${API_BASE_URL}/api/auth/change-password?userId=${userId}`,
    CONSULTATION: `${API_BASE_URL}/api/consultations`,
    PROFILE: `${API_BASE_URL}/api/profile`,
    PRODUCTS: `${API_BASE_URL}/api/products`,
    INVENTORY: `${API_BASE_URL}/api/v1/inventory`,
    ORDERS: `${API_BASE_URL}/api/v1/orders`,
    ORDER_DETAIL: (orderId) => `${API_BASE_URL}/api/v1/orders/${orderId}`,
    ORDER_CANCEL: (orderId) => `${API_BASE_URL}/api/v1/orders/${orderId}/cancel`,
    ORDERS_ADMIN_ALL: `${API_BASE_URL}/api/v1/orders/admin/all`, // Cập nhật ở đây
    CATEGORIES: `${API_BASE_URL}/api/categories`,
    BRANCHES: `${API_BASE_URL}/api/v1/branches`,
    STAFF: `${API_BASE_URL}/api/admin/staff`,
    USERS: `${API_BASE_URL}/api/admin/users`,
    USER_PROFILE: `${API_BASE_URL}/api/users/me`,
    PROMOTIONS: `${API_BASE_URL}/api/admin/promotions`,
    CART: `${API_BASE_URL}/api/v1/cart`,
    CART_ITEMS: `${API_BASE_URL}/api/v1/cart/items`,
    PAYMENTS: `${API_BASE_URL}/api/v1/payments`,
};