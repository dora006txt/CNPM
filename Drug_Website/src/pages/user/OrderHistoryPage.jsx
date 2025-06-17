// src/pages/OrderHistoryPage.jsx
import React, { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import axios from "axios";
import { API_ENDPOINTS } from "../../config/apiConfig";

const OrderHistoryPage = () => {
    const { orderId } = useParams();
    const [order, setOrder] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        fetchOrder();
    }, [orderId]);

    const fetchOrder = async () => {
        try {
            setLoading(true);
            const token = localStorage.getItem("token");
            if (!token) {
                setError("Vui lòng đăng nhập để xem chi tiết đơn hàng!");
                navigate("/login");
                return;
            }
            const response = await axios.get(API_ENDPOINTS.ORDER_DETAIL(orderId), {
                headers: { Authorization: `Bearer ${token}` },
            });
            setOrder(response.data);
        } catch (err) {
            setError(
                err.response?.data?.message ||
                `Lỗi khi tải chi tiết đơn hàng: ${err.message}`
            );
        } finally {
            setLoading(false);
        }
    };

    const handleCancelOrder = async () => {
        if (!window.confirm("Bạn có chắc muốn hủy đơn hàng này?")) return;
        try {
            setLoading(true);
            const token = localStorage.getItem("token");
            await axios.patch(API_ENDPOINTS.ORDER_CANCEL(orderId), null, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setOrder((prev) => ({ ...prev, orderStatusName: "CANCELLED", cancelledAt: new Date().toISOString() }));
            setError("");
        } catch (err) {
            setError(
                err.response?.data?.message ||
                `Lỗi khi hủy đơn hàng: ${err.message}`
            );
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <div className="container mx-auto p-4">Đang tải...</div>;
    }

    if (error) {
        return (
            <div className="container mx-auto p-4">
                <h1 className="text-2xl font-bold mb-4">Chi tiết đơn hàng</h1>
                <p className="text-red-500">{error}</p>
                <Link to="/checkout" className="btn btn-primary mt-4">
                    Quay lại thanh toán
                </Link>
            </div>
        );
    }

    if (!order) {
        return (
            <div className="container mx-auto p-4">
                <h1 className="text-2xl font-bold mb-4">Chi tiết đơn hàng</h1>
                <p>Đơn hàng không tồn tại</p>
                <Link to="/checkout" className="btn btn-primary mt-4">
                    Quay lại thanh toán
                </Link>
            </div>
        );
    }

    return (
        <div className="container mx-auto p-4 bg-white text-gray-900 dark:bg-gray-900 dark:text-white">
            <h1 className="text-2xl font-bold mb-4 text-blue-600 dark:text-blue-400">Chi tiết đơn hàng #{order.orderCode}</h1>
            <p><strong>Trạng thái:</strong> {order.orderStatusName}</p>
            <p><strong>Ngày đặt:</strong> {new Date(order.orderDate).toLocaleString("vi-VN")}</p>
            <p><strong>Tổng tiền:</strong> {order.finalAmount.toLocaleString("vi-VN")} VND</p>
            <p><strong>Địa chỉ giao hàng:</strong> {order.shippingAddress}</p>
            <p><strong>Ghi chú:</strong> {order.notes || "Không có"}</p>
            <h3 className="text-lg font-semibold mt-4">Sản phẩm</h3>
            <ul className="mt-2">
                {order.orderItems.map((item) => (
                    <li key={item.orderItemId} className="mb-2">
                        {item.productName} - {item.quantity} x {item.priceAtPurchase.toLocaleString("vi-VN")} VND = {item.subtotal.toLocaleString("vi-VN")} VND
                    </li>
                ))}
            </ul>
            {order.isCancellable && order.orderStatusName === "PENDING" && (
                <button
                    onClick={handleCancelOrder}
                    className="mt-4 bg-red-600 text-white py-2 px-4 rounded-lg hover:bg-red-700 disabled:opacity-50"
                    disabled={loading}
                >
                    {loading ? "Đang xử lý..." : "Hủy đơn hàng"}
                </button>
            )}
            <Link to="/checkout" className="mt-4 inline-block bg-white text-white py-2 px-4 rounded-lg hover:bg-blue-700">
                Quay lại thanh toán
            </Link>
        </div>
    );
};

export default OrderHistoryPage;