import React, { useState, useEffect, Fragment } from "react";
import { FaEye } from "react-icons/fa";
import { Dialog, Transition } from "@headlessui/react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../../context/AuthContext";
import { API_ENDPOINTS } from "../../config/apiConfig";

const OrderList = () => {
    const { user } = useAuth();
    const navigate = useNavigate();
    const [orders, setOrders] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [filterStatus, setFilterStatus] = useState("");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedOrder, setSelectedOrder] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // Lấy danh sách tất cả đơn hàng từ API admin
    const fetchOrders = async () => {
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để truy cập!");
            navigate("/login");
            return;
        }
        setLoading(true);
        setError(null);
        console.log("Fetching all orders with token:", user.token);
        console.log("API endpoint:", API_ENDPOINTS.ORDERS_ADMIN_ALL);
        try {
            const response = await axios.get(API_ENDPOINTS.ORDERS_ADMIN_ALL, {
                headers: { Authorization: `Bearer ${user.token}` },
            });
            const data = Array.isArray(response.data) ? response.data : [];
            setOrders(data);
            console.log("All orders fetched:", data);
        } catch (err) {
            const errorMessage = err.response?.data?.message || err.message || "Không thể tải danh sách đơn hàng. Vui lòng thử lại.";
            setError(errorMessage);
            console.error("Fetch orders error:", err.response || err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchOrders();
    }, [user, navigate]);

    // Lọc đơn hàng
    const filteredOrders = orders.filter((order) => {
        const matchesSearch =
            String(order.orderCode).includes(searchTerm) ||
            order.userFullName?.toLowerCase().includes(searchTerm.toLowerCase());
        const matchesStatus = filterStatus
            ? order.orderStatusName?.toLowerCase() === filterStatus.toLowerCase()
            : true;
        return matchesSearch && matchesStatus;
    });

    // Mở modal chi tiết đơn hàng
    const openDetailsModal = async (order) => {
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để xem chi tiết!");
            navigate("/login");
            return;
        }
        setLoading(true);
        try {
            const response = await axios.get(`${API_ENDPOINTS.ORDERS_ADMIN_ALL}/${order.orderId}`, {
                headers: { Authorization: `Bearer ${user.token}` },
            });
            setSelectedOrder(response.data);
            console.log("Order details:", response.data);
        } catch (err) {
            setError(
                err.response?.data?.message || "Không thể tải chi tiết đơn hàng. Vui lòng thử lại."
            );
            console.error("Fetch order details error:", err.response || err);
        } finally {
            setLoading(false);
            setIsModalOpen(true);
        }
    };

    // Đóng modal
    const closeModal = () => {
        setIsModalOpen(false);
        setSelectedOrder(null);
    };

    // Cập nhật trạng thái đơn hàng
    const updateOrderStatus = async (orderId, newStatus) => {
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để cập nhật trạng thái!");
            navigate("/login");
            return;
        }
        setLoading(true);
        try {
            const response = await axios.patch(
                `${API_ENDPOINTS.ORDERS_ADMIN_ALL}/${orderId}/status`,
                { newStatus },
                { headers: { Authorization: `Bearer ${user.token}` } }
            );
            setOrders(
                orders.map((order) =>
                    order.orderId === orderId ? response.data : order
                )
            );
            console.log(`Order ${orderId} updated to ${newStatus}`);
        } catch (err) {
            const errorMessage = err.response?.data?.message || "Không thể cập nhật trạng thái. Vui lòng thử lại.";
            setError(errorMessage);
            console.error("Update status error:", err.response || err);
        } finally {
            setLoading(false);
        }
    };

    // Hủy đơn hàng
    const cancelOrder = async (orderId) => {
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để hủy đơn hàng!");
            navigate("/login");
            return;
        }
        if (!window.confirm("Bạn có chắc muốn hủy đơn hàng này?")) {
            return;
        }
        setLoading(true);
        try {
            const response = await axios.patch(
                `${API_ENDPOINTS.ORDERS_ADMIN_ALL}/${orderId}/cancel`,
                {},
                { headers: { Authorization: `Bearer ${user.token}` } }
            );
            setOrders(
                orders.map((order) =>
                    order.orderId === orderId ? response.data : order
                )
            );
            console.log(`Order ${orderId} cancelled`);
        } catch (err) {
            setError(
                err.response?.data?.message || "Không thể hủy đơn hàng. Vui lòng thử lại."
            );
            console.error("Cancel order error:", err.response || err);
        } finally {
            setLoading(false);
        }
    };

    // Định dạng tiền tệ
    const formatCurrency = (amount) => {
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
        }).format(amount || 0);
    };

    const orderStatuses = ["PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"];

    return (
        <div>
            <h2 className="text-2xl font-bold mb-6">Quản lý Đơn hàng</h2>

            {error && (
                <div className="alert alert-error mb-4">
                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        fill="none"
                        viewBox="0 0 24 24"
                        className="w-6 h-6 mx-2 stroke-current"
                    >
                        <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth="2"
                            d="M12 9v2m0 4h.01M12 2a10 10 0 100 20 10 10 0 000-20z"
                        ></path>
                    </svg>
                    <span>{error}</span>
                </div>
            )}

            <div className="mb-4 flex flex-col md:flex-row gap-4">
                <input
                    type="text"
                    placeholder="Tìm kiếm đơn hàng (mã hoặc khách hàng)..."
                    className="input input-bordered w-full md:w-1/3"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
                <select
                    className="select select-bordered w-full md:w-1/4"
                    value={filterStatus}
                    onChange={(e) => setFilterStatus(e.target.value)}
                >
                    <option value="">Tất cả trạng thái</option>
                    {orderStatuses.map((status) => (
                        <option key={status} value={status}>
                            {status === "PENDING"
                                ? "Chờ xử lý"
                                : status === "PROCESSING"
                                    ? "Đang xử lý"
                                    : status === "SHIPPED"
                                        ? "Đã giao hàng"
                                        : status === "DELIVERED"
                                            ? "Đã nhận"
                                            : "Đã hủy"}
                        </option>
                    ))}
                </select>
            </div>

            <div className="overflow-x-auto">
                {loading ? (
                    <div className="flex justify-center">
                        <span className="loading loading-spinner loading-lg"></span>
                    </div>
                ) : filteredOrders.length === 0 ? (
                    <div className="text-center py-4">
                        {error ? error : "Không tìm thấy đơn hàng."}
                    </div>
                ) : (
                    <table className="table w-full">
                        <thead>
                            <tr>
                                <th>Mã đơn hàng</th>
                                <th>Khách hàng</th>
                                <th>Tổng tiền</th>
                                <th>Trạng thái</th>
                                <th>Ngày đặt hàng</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredOrders.map((order) => (
                                <tr key={order.orderId}>
                                    <td>{order.orderCode || "N/A"}</td>
                                    <td>{order.userFullName || "Khách vãng lai"}</td>
                                    <td>{formatCurrency(order.finalAmount)}</td>
                                    <td>
                                        <select
                                            className="select select-bordered select-sm"
                                            value={order.orderStatusName || ""}
                                            onChange={(e) =>
                                                updateOrderStatus(order.orderId, e.target.value)
                                            }
                                            disabled={loading || order.orderStatusName === "CANCELLED"}
                                        >
                                            {orderStatuses.map((status) => (
                                                <option key={status} value={status}>
                                                    {status === "PENDING"
                                                        ? "Chờ xử lý"
                                                        : status === "PROCESSING"
                                                            ? "Đang xử lý"
                                                            : status === "SHIPPED"
                                                                ? "Đã giao hàng"
                                                                : status === "DELIVERED"
                                                                    ? "Đã nhận"
                                                                    : "Đã hủy"}
                                                </option>
                                            ))}
                                        </select>
                                    </td>
                                    <td>
                                        {order.orderDate
                                            ? new Date(order.orderDate).toLocaleString("vi-VN")
                                            : "N/A"}
                                    </td>
                                    <td>
                                        <button
                                            className="btn btn-ghost btn-sm mr-2"
                                            onClick={() => openDetailsModal(order)}
                                            disabled={loading}
                                        >
                                            <FaEye />
                                        </button>
                                        {order.isCancellable && (
                                            <button
                                                className="btn btn-error btn-sm"
                                                onClick={() => cancelOrder(order.orderId)}
                                                disabled={loading}
                                            >
                                                Hủy
                                            </button>
                                        )}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}
            </div>

            <Transition appear show={isModalOpen} as={Fragment}>
                <Dialog as="div" className="relative z-10" onClose={closeModal}>
                    <Transition.Child
                        as={Fragment}
                        enter="ease-out duration-300"
                        enterFrom="opacity-0"
                        enterTo="opacity-100"
                        leave="ease-in duration-200"
                        leaveFrom="opacity-100"
                        leaveTo="opacity-0"
                    >
                        <div className="fixed inset-0 bg-black bg-opacity-25" />
                    </Transition.Child>

                    <div className="fixed inset-0 overflow-y-auto">
                        <div className="flex min-h-full items-center justify-center p-4">
                            <Transition.Child
                                as={Fragment}
                                enter="ease-out duration-300"
                                enterFrom="opacity-0 scale-95"
                                enterTo="opacity-100 scale-100"
                                leave="ease-in duration-200"
                                leaveFrom="opacity-100 scale-100"
                                leaveTo="opacity-0 scale-95"
                            >
                                <Dialog.Panel className="w-full max-w-2xl transform overflow-hidden rounded-2xl bg-base-100 p-6 text-left align-middle shadow-xl transition-all">
                                    <Dialog.Title as="h3" className="text-lg font-medium leading-6">
                                        Chi tiết Đơn hàng #{selectedOrder?.orderCode || "N/A"}
                                    </Dialog.Title>
                                    {selectedOrder ? (
                                        <div className="mt-4">
                                            <p>
                                                <strong>Khách hàng:</strong>{" "}
                                                {selectedOrder.userFullName || "Khách vãng lai"}
                                            </p>
                                            <p>
                                                <strong>Tổng tiền:</strong>{" "}
                                                {formatCurrency(selectedOrder.finalAmount)}
                                            </p>
                                            <p>
                                                <strong>Trạng thái:</strong>{" "}
                                                {selectedOrder.orderStatusName === "PENDING"
                                                    ? "Chờ xử lý"
                                                    : selectedOrder.orderStatusName === "PROCESSING"
                                                        ? "Đang xử lý"
                                                        : selectedOrder.orderStatusName === "SHIPPED"
                                                            ? "Đã giao hàng"
                                                            : selectedOrder.orderStatusName === "DELIVERED"
                                                                ? "Đã nhận"
                                                                : "Đã hủy"}
                                            </p>
                                            <p>
                                                <strong>Ngày đặt hàng:</strong>{" "}
                                                {selectedOrder.orderDate
                                                    ? new Date(selectedOrder.orderDate).toLocaleString("vi-VN")
                                                    : "N/A"}
                                            </p>
                                            <p>
                                                <strong>Địa chỉ giao hàng:</strong>{" "}
                                                {selectedOrder.shippingAddress || "Chưa cung cấp"}
                                            </p>
                                            <p>
                                                <strong>Phương thức thanh toán:</strong>{" "}
                                                {selectedOrder.paymentTypeName || "Chưa có"}
                                            </p>
                                            <p>
                                                <strong>Phí vận chuyển:</strong>{" "}
                                                {formatCurrency(selectedOrder.shippingFee)}
                                            </p>
                                            <p>
                                                <strong>Giảm giá:</strong>{" "}
                                                {formatCurrency(selectedOrder.discountAmount)}
                                            </p>
                                            {selectedOrder.appliedPromotionCode && (
                                                <p>
                                                    <strong>Mã khuyến mãi:</strong>{" "}
                                                    {selectedOrder.appliedPromotionCode}
                                                </p>
                                            )}
                                            {selectedOrder.notes && (
                                                <p>
                                                    <strong>Ghi chú:</strong> {selectedOrder.notes}
                                                </p>
                                            )}

                                            <h4 className="mt-4 font-semibold">Sản phẩm trong đơn hàng</h4>
                                            <div className="overflow-x-auto">
                                                <table className="table w-full mt-2">
                                                    <thead>
                                                        <tr>
                                                            <th>Sản phẩm</th>
                                                            <th>Số lượng</th>
                                                            <th>Giá</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        {selectedOrder.orderItems?.length > 0 ? (
                                                            selectedOrder.orderItems.map((item, index) => (
                                                                <tr key={index}>
                                                                    <td>{item.productName || "N/A"}</td>
                                                                    <td>{item.quantity || 0}</td>
                                                                    <td>
                                                                        {formatCurrency(item.priceAtPurchase)}
                                                                    </td>
                                                                </tr>
                                                            ))
                                                        ) : (
                                                            <tr>
                                                                <td colSpan="3" className="text-center">
                                                                    Không có sản phẩm.
                                                                </td>
                                                            </tr>
                                                        )}
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                    ) : (
                                        <p className="mt-4">Không có dữ liệu chi tiết đơn hàng.</p>
                                    )}
                                    <div className="mt-4">
                                        <button
                                            type="button"
                                            className="btn btn-ghost"
                                            onClick={closeModal}
                                            disabled={loading}
                                        >
                                            Đóng
                                        </button>
                                    </div>
                                </Dialog.Panel>
                            </Transition.Child>
                        </div>
                    </div>
                </Dialog>
            </Transition>
        </div>
    );
};

export default OrderList;