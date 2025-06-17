import axios from "axios";
import { useAuth } from "../context/AuthContext";

// Tạo axios instance
const api = axios.create({
    baseURL: "http://localhost:8080", // Thay đổi baseURL nếu cần
});

// Interceptor để thêm token vào tất cả request
api.interceptors.request.use(
    (config) => {
        const user = JSON.parse(localStorage.getItem("user")); // Hoặc lấy từ useAuth trong component
        const token = user?.token;
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

export default api;