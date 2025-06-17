import React from "react";
import { useNavigate } from "react-router-dom";

const CartSummary = ({ cart }) => {
    const navigate = useNavigate();

    const totalPrice = cart.items.reduce(
        (total, item) => total + item.price * item.quantity,
        0
    ).toLocaleString("vi-VN");

    // Lấy danh sách chi nhánh duy nhất từ cart.items
    const branches = [...new Set(cart.items.map(item => item.branchName))];

    const handleCheckout = () => {
        navigate("/checkout");
    };

    return (
        <div className="border p-4 rounded-lg shadow-md text-black">
            <h2 className="text-xl font-semibold mb-4">Tổng kết giỏ hàng</h2>
            <p className="text-gray-600 mb-2">
                Chi nhánh: {branches.length > 0 ? branches.join(", ") : "Không xác định"}
            </p>
            <p className="text-gray-600 mb-2">
                Số sản phẩm: {cart.items.length}
            </p>
            <p className="text-lg font-semibold mb-4">
                Tổng tiền: {totalPrice} VND
            </p>
            <button
                onClick={handleCheckout}
                className="btn btn-primary w-full"
                disabled={cart.items.length === 0}
            >
                Thanh toán
            </button>
        </div>
    );
};

export default CartSummary;