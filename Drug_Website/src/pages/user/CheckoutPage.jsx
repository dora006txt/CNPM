import React, { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import axios from "axios";
import { useCart } from "../../context/CartContext";
import { useAuth } from "../../context/AuthContext";
import { API_ENDPOINTS } from "../../config/apiConfig";

const CheckoutPage = () => {
    const { user, logout } = useAuth(); // Thêm logout từ useAuth
    const { carts, selectedCartId, fetchCarts } = useCart();
    const navigate = useNavigate();
    const [paymentTypes, setPaymentTypes] = useState([]);
    const [shippingMethods, setShippingMethods] = useState([]);
    const [formData, setFormData] = useState({
        shippingAddress: "",
        paymentTypeId: "",
        shippingMethodId: "",
        notes: "",
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [isDataLoaded, setIsDataLoaded] = useState(false);

    // Lấy danh sách phương thức thanh toán
    const fetchPaymentTypes = async () => {
        try {
            const response = await axios.get("http://localhost:8080/api/payment-types", {
                headers: { Authorization: `Bearer ${user?.token}` },
            });
            setPaymentTypes(Array.isArray(response.data) ? response.data : []);
        } catch (err) {
            setError("Không thể tải danh sách phương thức thanh toán. Vui lòng thử lại.");
            console.error("Fetch payment types error:", err.response || err);
        }
    };

    // Lấy danh sách phương thức vận chuyển
    const fetchShippingMethods = async () => {
        try {
            const response = await axios.get("http://localhost:8080/api/shipping-methods/active", {
                headers: { Authorization: `Bearer ${user?.token}` },
            });
            setShippingMethods(Array.isArray(response.data) ? response.data : []);
        } catch (err) {
            setError("Không thể tải danh sách phương thức vận chuyển. Vui lòng thử lại.");
            console.error("Fetch shipping methods error:", err.response || err);
        }
    };

    useEffect(() => {
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để tiếp tục!");
            navigate("/login");
            return;
        }
        if (!isDataLoaded) {
            fetchPaymentTypes();
            fetchShippingMethods();
            fetchCarts();
            setIsDataLoaded(true);
        }
    }, [user, navigate, fetchCarts, isDataLoaded]);

    // Chuẩn hóa carts thành mảng và lấy currentCart
    const cartsArray = Array.isArray(carts) ? carts : carts && carts.cartId ? [carts] : [];
    const currentCart = cartsArray.find((cart) => cart.cartId === selectedCartId) || { items: [] };

    // Tính tổng tiền
    const calculateTotal = () => {
        const subtotal = currentCart.items.reduce(
            (sum, item) => sum + item.quantity * item.price,
            0
        );
        const shippingFee = shippingMethods.find(
            (method) => method.methodId === Number(formData.shippingMethodId)
        )?.baseCost || 0;
        return subtotal + shippingFee;
    };

    const handleCheckout = async (e) => {
        e.preventDefault();
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để đặt hàng!");
            return;
        }
        if (!formData.shippingAddress || !formData.paymentTypeId || !formData.shippingMethodId) {
            setError("Vui lòng điền đầy đủ thông tin!");
            return;
        }
        if (currentCart.items.length === 0) {
            setError("Giỏ hàng của bạn đang trống!");
            return;
        }
        if (!selectedCartId) {
            setError("Không tìm thấy giỏ hàng được chọn!");
            return;
        }
        if (user.roleId !== 1) {
            setError("Chỉ người dùng CUSTOMER có quyền đặt hàng!");
            return;
        }

        setLoading(true);
        setError(null);
        try {
            // Kiểm tra token
            const userResponse = await axios.get("http://localhost:8080/api/users/me", {
                headers: { Authorization: `Bearer ${user.token}` },
            });
            console.log("User profile:", userResponse.data);

            const orderData = {
                shippingAddress: formData.shippingAddress,
                paymentTypeId: Number(formData.paymentTypeId),
                shippingMethodId: Number(formData.shippingMethodId),
                notes: formData.notes,
                // promotionCode: formData.promotionCode || "", // Thêm nếu cần
            };
            console.log("Order Data:", JSON.stringify(orderData, null, 2));
            console.log("Token:", user.token);

            const response = await axios.post(
                "http://localhost:8080/api/v1/orders/from-cart",
                orderData,
                { headers: { Authorization: `Bearer ${user.token}` } }
            );
            console.log("Checkout response:", response.data);

            alert("Đặt hàng thành công!");
            navigate("/orders");
        } catch (err) {
            console.error("Checkout error:", {
                status: err.response?.status,
                data: err.response?.data,
                headers: err.response?.headers,
                config: err.response?.config,
            });
            const errorMessage = err.response?.data?.message || "Không thể đặt hàng. Vui lòng thử lại.";
            setError(`Lỗi: ${errorMessage}`);
        } finally {
            setLoading(false);
        }
    };

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
        }).format(amount);
    };

    if (!user) {
        return (
            <div className="container mx-auto px-4 py-6 bg-white">
                <h1 className="text-2xl font-bold mb-4 text-blue-600">Thanh toán</h1>
                <div className="alert alert-error mb-6">
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
                    <span className="text-black">Vui lòng đăng nhập để tiếp tục!</span>
                </div>
                <Link to="/login" className="btn btn-primary mt-4">
                    Đăng nhập
                </Link>
            </div>
        );
    }

    if (currentCart.items.length === 0) {
        return (
            <div className="container mx-auto px-4 py-6 bg-white">
                <h1 className="text-2xl font-bold mb-4 text-blue-600">Thanh toán</h1>
                <div className="text-center mb-6">
                    <p className="text-black">Giỏ hàng của bạn đang trống.</p>
                </div>
                <Link to="/cart" className="btn btn-primary mt-4">
                    Quay lại giỏ hàng
                </Link>
            </div>
        );
    }

    return (
        <div className="container mx-auto px-4 py-6 bg-white min-h-screen">
            <h1 className="text-2xl font-bold mb-4 text-blue-600">Thanh toán</h1>

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
                    <span className="text-black">{error}</span>
                </div>
            )}

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="md:col-span-2">
                    <div className="p-4 border rounded-lg bg-gray-50">
                        <h2 className="text-xl font-semibold mb-4 text-black">Thông tin đặt hàng</h2>
                        <form onSubmit={handleCheckout} className="grid grid-cols-1 gap-4">
                            <div className="mb-4">
                                <label className="block text-black mb-2">Địa chỉ giao hàng</label>
                                <input
                                    type="text"
                                    value={formData.shippingAddress}
                                    onChange={(e) => setFormData({ ...formData, shippingAddress: e.target.value })}
                                    className="w-full px-3 py-2 border rounded-lg bg-white text-black focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    required
                                />
                            </div>
                            <div className="mb-4">
                                <label className="block text-black mb-2">Phương thức thanh toán</label>
                                <select
                                    value={formData.paymentTypeId}
                                    onChange={(e) => setFormData({ ...formData, paymentTypeId: e.target.value })}
                                    className="select select-bordered w-full bg-white text-black"
                                    required
                                >
                                    <option value="" disabled>Chọn phương thức thanh toán</option>
                                    {paymentTypes.map((type) => (
                                        <option key={type.paymentTypeId} value={type.paymentTypeId}>
                                            {type.typeName} - {type.description}
                                        </option>
                                    ))}
                                </select>
                            </div>
                            <div className="mb-4">
                                <label className="block text-black mb-2">Phương thức vận chuyển</label>
                                <select
                                    value={formData.shippingMethodId}
                                    onChange={(e) => setFormData({ ...formData, shippingMethodId: e.target.value })}
                                    className="select select-bordered w-full bg-white text-black"
                                    required
                                >
                                    <option value="" disabled>Chọn phương thức vận chuyển</option>
                                    {shippingMethods.map((method) => (
                                        <option key={method.methodId} value={method.methodId}>
                                            {method.name} - {formatCurrency(method.baseCost)}
                                        </option>
                                    ))}
                                </select>
                            </div>
                            <div className="mb-4">
                                <label className="block text-black mb-2">Ghi chú</label>
                                <textarea
                                    value={formData.notes}
                                    onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
                                    className="w-full px-3 py-2 border rounded-lg bg-white text-black focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    rows="3"
                                />
                            </div>
                            <button
                                type="submit"
                                className="btn btn-primary mt-2"
                                disabled={loading}
                            >
                                {loading ? "Đang xử lý..." : "Đặt hàng"}
                            </button>
                        </form>
                    </div>
                </div>

                <div>
                    <div className="p-4 border rounded-lg bg-gray-50">
                        <h3 className="text-lg font-semibold mb-4 text-black">Tóm tắt đơn hàng</h3>
                        <ul className="mt-2">
                            {currentCart.items.map((item) => (
                                <li key={item.cartItemId} className="mb-2">
                                    <p className="text-black">
                                        {item.productName} x {item.quantity} - {formatCurrency(item.quantity * item.price)}
                                    </p>
                                </li>
                            ))}
                        </ul>
                        <div className="mt-4 border-t pt-4">
                            <p className="text-black">
                                <strong>Phí vận chuyển:</strong>{" "}
                                {formatCurrency(
                                    shippingMethods.find(
                                        (method) => method.methodId === Number(formData.shippingMethodId)
                                    )?.baseCost || 0
                                )}
                            </p>
                            <p className="text-black">
                                <strong>Tổng cộng:</strong> {formatCurrency(calculateTotal())}
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CheckoutPage;