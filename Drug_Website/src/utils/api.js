// src/utils/api.js
import axios from 'axios';

const API_BASE_URL = "http://localhost:8080";

const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Interceptor để thêm token vào header
apiClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token'); // Giả sử token được lưu trong localStorage
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Hàm gọi API chung
export const fetchData = async (endpoint, params = {}) => {
    try {
        const response = await apiClient.get(endpoint, { params });
        return response.data;
    } catch (error) {
        console.error(`Error fetching data from ${endpoint}:`, error);
        throw error.response?.data || { message: 'Something went wrong' };
    }
};

// Hàm cụ thể cho các API thống kê
export const getRevenueByDay = (startDate, endDate) =>
    fetchData('/api/admin/statistics/revenue-by-day', { startDate, endDate });

export const getRevenueByMonth = (year, month = null) =>
    fetchData('/api/admin/statistics/revenue-by-month', { year, month });

export const getRevenueByQuarter = (year, quarter = null) =>
    fetchData('/api/admin/statistics/revenue-by-quarter', { year, quarter });

export const getRevenueByYear = (years) =>
    fetchData('/api/admin/statistics/revenue-by-year', { years });

export const getRevenueForSpecificYear = (year) =>
    fetchData('/api/admin/statistics/revenue-for-year', { year });