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
            setError(`Lỗi khi tải dữ liệu: ${err.message}`);
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
            <h1 className="text-3xl font-bold text-blue-700 mb-4">
                Bảng Điều Khiển Quản Trị (Admin Dashboard)
            </h1>
            <p className="text-gray-600 mb-6">
                Xin chào quản trị viên! Đây là trung tâm điều phối và giám sát toàn bộ hệ thống.
            </p>

            {loading && <p>Đang tải dữ liệu...</p>}
            {error && <p className="text-red-500">{error}</p>}

            {!loading && !error && (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div className="bg-blue-100 p-4 rounded-lg shadow">
                        <h2 className="text-xl font-semibold text-blue-800 mb-2">
                            Tổng số người dùng
                        </h2>
                        <p className="text-2xl">467</p>
                        <a href="http://localhost:5173/admin/users" className="mt-5">Xem chi tiết</a >

                    </div>
                    <div className="bg-yellow-100 p-4 rounded-lg shadow">
                        <h2 className="text-xl font-semibold text-yellow-800 mb-2">
                            Tổng doanh thu
                        </h2>
                        <p className="text-2xl">
                            332,634,812 VNĐ

                        </p>
                        <a href="http://localhost:5173/admin/revenue-report" className="mt-5">Xem chi tiết</a >
                    </div>
                </div>
            )}
        </div>
    );
};

export default Dashboard;
