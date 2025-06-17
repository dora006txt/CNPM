import React, { useState, useEffect } from "react";
import axios from "axios";

const CheckoutForm = ({ cart, onSubmit, loading }) => {
    const [formData, setFormData] = useState({
        fullName: "",
        address: "",
        phoneNumber: "",
        paymentMethod: "",
        shippingMethodId: "",
        notes: "",
    });
    const [paymentTypes, setPaymentTypes] = useState([]);
    const [shippingMethods, setShippingMethods] = useState([]);
    const [error, setError] = useState(null);

    // Lấy danh sách phương thức thanh toán
    const fetchPaymentTypes = async () => {
        try {
            const response = await axios.get("http://localhost:8080/api/payment-types");
            setPaymentTypes(Array.isArray(response.data) ? response.data : []);
            if (response.data.length > 0) {
                setFormData((prev) => ({ ...prev, paymentMethod: response.data[0].paymentTypeId.toString() }));
            }
        } catch (err) {
            setError("Không thể tải danh sách phương thức thanh toán. Vui lòng thử lại.");
            console.error("Fetch payment types error:", err.response || err);
        }
    };

    // Lấy danh sách phương thức vận chuyển
    const fetchShippingMethods = async () => {
        try {
            const response = await axios.get("http://localhost:8080/api/shipping-methods/active");
            setShippingMethods(Array.isArray(response.data) ? response.data : []);
            if (response.data.length > 0) {
                setFormData((prev) => ({ ...prev, shippingMethodId: response.data[0].methodId.toString() }));
            }
        } catch (err) {
            setError("Không thể tải danh sách phương thức vận chuyển. Vui lòng thử lại.");
            console.error("Fetch shipping methods error:", err.response || err);
        }
    };

    useEffect(() => {
        fetchPaymentTypes();
        fetchShippingMethods();
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!formData.fullName || !formData.address || !formData.phoneNumber || !formData.paymentMethod || !formData.shippingMethodId) {
            alert("Vui lòng điền đầy đủ thông tin!");
            return;
        }
        onSubmit(formData);
    };

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
        }).format(amount);
    };

    return (
        <form onSubmit={handleSubmit} className="bg-gray-100 dark:bg-gray-800 p-6 rounded-lg shadow-lg">
            <h2 className="text-xl font-semibold mb-4 text-blue-600 dark:text-blue-400">Thông tin giao hàng</h2>

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

            <div className="mb-4">
                <label className="block text-gray-700 dark:text-gray-300 mb-2">Họ và tên</label>
                <input
                    type="text"
                    name="fullName"
                    value={formData.fullName}
                    onChange={handleChange}
                    className="w-full px-3 py-2 border rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required
                />
            </div>
            <div className="mb-4">
                <label className="block text-gray-700 dark:text-gray-300 mb-2">Địa chỉ</label>
                <textarea
                    name="address"
                    value={formData.address}
                    onChange={handleChange}
                    className="w-full px-3 py-2 border rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    rows="3"
                    required
                />
            </div>
            <div className="mb-4">
                <label className="block text-gray-700 dark:text-gray-300 mb-2">Số điện thoại</label>
                <input
                    type="tel"
                    name="phoneNumber"
                    value={formData.phoneNumber}
                    onChange={handleChange}
                    className="w-full px-3 py-2 border rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required
                />
            </div>
            <div className="mb-4">
                <label className="block text-gray-700 dark:text-gray-300 mb-2">Phương thức thanh toán</label>
                <select
                    name="paymentMethod"
                    value={formData.paymentMethod}
                    onChange={handleChange}
                    className="w-full px-3 py-2 border rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500"
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
                <label className="block text-gray-700 dark:text-gray-300 mb-2">Phương thức vận chuyển</label>
                <select
                    name="shippingMethodId"
                    value={formData.shippingMethodId}
                    onChange={handleChange}
                    className="w-full px-3 py-2 border rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500"
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
                <label className="block text-gray-700 dark:text-gray-300 mb-2">Ghi chú</label>
                <textarea
                    name="notes"
                    value={formData.notes}
                    onChange={handleChange}
                    className="w-full px-3 py-2 border rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    rows="3"
                />
            </div>
            <button
                type="submit"
                className="w-full bg-white text-white py-2 px-4 rounded-lg hover:bg-blue-700 dark:bg-blue-700 dark:hover:bg-blue-800 disabled:opacity-50"
                disabled={loading}
            >
                {loading ? "Đang xử lý..." : "Xác nhận đặt hàng"}
            </button>
        </form>
    );
};

export default CheckoutForm;