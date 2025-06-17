// src/api/index.js
import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080", // Thay bằng URL backend của bạn
    timeout: 10000,
    headers: {
        "Content-Type": "application/json",
    },
});

api.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error("API Error:", error);
        return Promise.reject(error);
    }
);

export default api;