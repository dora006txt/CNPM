import React, { useState, useEffect } from "react";
import axios from "axios";
import { API_ENDPOINTS } from "../../config/apiConfig";

const Dashboard = () => {
    const [stats, setStats] = useState({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchStats = async () => {
        try {
            setLoading(true);
            const response = await axios.get(API_ENDPOINTS.STATS, {
                headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
            });
            setStats(response.data);
        } catch (err) {
            setError(`Error fetching stats: ${err.message}`);
            console.error("Fetch stats error:", err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchStats();
    }, []);

    return (
        <div className="p-6">
            <h1 className="text-3xl font-bold text-green-600 mb-4">
                Bảng Điều Khiển Nhân Viên (Staff Dashboard)
            </h1>
            <p className="text-gray-600">
                Chào mừng đến với giao diện dành riêng cho nhân viên!
                Bạn có thể quản lý kho, đơn thuốc và tư vấn tại đây.
            </p>
            <div className="mt-4">
                <div className="bg-green-100 p-4 rounded-lg">
                    <h2 className="text-xl font-semibold text-green-700">
                        Tình Trạng Hệ Thống
                    </h2>
                    <p>Đang hoạt động bình thường</p>
                </div>
            </div>
        </div>

    );
};

export default Dashboard;