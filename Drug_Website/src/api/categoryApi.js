// src/api/categoryApi.js
import api from "./index";

export const getCategories = async () => {
    try {
        const response = await api.get("/api/categories");
        return response.data;
    } catch (error) {
        throw new Error("Không thể lấy danh sách danh mục.");
    }
};