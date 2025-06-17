import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import { API_ENDPOINTS } from "../../config/apiConfig";

const PaymentPage = () => {
    const { state } = useLocation();
    const { orderId, totalPrice } = state || {};
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handlePayment = async () => {
        try {
            setLoading(true);
            const token = localStorage.getItem("token");
            if (!token) {
                throw new Error("Vui lòng đăng nhập để thanh toán!");
            }

            const paymentData = {
                orderId,
                amount: totalPrice,
                paymentTypeId: 2,
                status: "COMPLETED",
            };
            const paymentResponse = await axios.post(API_ENDPOINTS.PAYMENTS, paymentData, {
                headers: { Authorization: `Bearer ${token}` },
            });
            console.log("Payment successful:", paymentResponse.data);
            alert("Thanh toán thành công!");
            navigate("/orders");
        } catch (err) {
            console.error("Payment failed:", err);
            if (err.response?.status === 403) {
                alert("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!");
                localStorage.removeItem("token");
                navigate("/login");
            } else {
                setError("Lỗi khi thanh toán: " + (err.response?.data?.message || err.message || "Không xác định"));
            }
        } finally {
            setLoading(false);
        }
    };

    if (!orderId || !totalPrice) {
        return (
            <div className="container mx-auto p-4">
                <h1 className="text-2xl font-bold mb-4">Thanh toán</h1>
                <p className="text-red-500">Không tìm thấy thông tin đơn hàng!</p>
            </div>
        );
    }

    return (
        <div className="container mx-auto p-4">
            <h1 className="text-2xl font-bold mb-4">Thanh toán</h1>
            {error && <p className="text-red-500 mb-4">{error}</p>}
            <div className="bg-gray-100 p-4 rounded-lg shadow-md">
                <p className="mb-2">
                    <strong>Mã đơn hàng:</strong> {orderId}
                </p>
                <p className="mb-4">
                    <strong>Tổng tiền:</strong> {totalPrice.toLocaleString("vi-VN")} VND
                </p>
                <button
                    onClick={handlePayment}
                    className="btn btn-success"
                    disabled={loading}
                >
                    {loading ? "Đang xử lý..." : "Xác nhận thanh toán"}
                </button>
            </div>
        </div>
    );
};

export default PaymentPage;