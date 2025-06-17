import React, { useState } from "react";
import { useCart } from "../../../context/CartContext";

const CartItem = ({ item }) => {
    console.log("CartItem item:", JSON.stringify(item, null, 2)); // Debug

    const { removeCartItem, updateCartItem } = useCart();
    const [quantity, setQuantity] = useState(item.quantity);
    const [localError, setLocalError] = useState(null);

    const handleQuantityChange = async (e) => {
        const newQuantity = parseInt(e.target.value);
        if (newQuantity >= 0 && newQuantity <= 100) {
            setQuantity(newQuantity);
            setLocalError(null);
            try {
                await updateCartItem(item.cartItemId, newQuantity);
            } catch (err) {
                setQuantity(item.quantity);
                setLocalError(err.message || "Không thể cập nhật số lượng.");
                console.error("Error updating quantity:", err);
            }
        } else {
            setLocalError("Số lượng phải từ 0 đến 100.");
        }
    };

    const handleRemove = async () => {
        try {
            await removeCartItem(item.cartItemId);
            setLocalError(null);
        } catch (err) {
            setLocalError("Không thể xóa sản phẩm.");
            console.error("Error removing item:", err);
        }
    };

    const totalItemPrice = (item.price * quantity).toLocaleString("vi-VN");

    return (
        <div className="flex items-center border-b py-4 text-black">
            <img
                src={item.productImageUrl || "https://placehold.co/100x100"}
                alt={item.productName}
                className="w-20 h-20 object-cover rounded mr-4"
                onError={(e) => { e.target.src = "https://placehold.co/100x100"; }}
            />
            <div className="flex-1">
                <h3 className="text-lg font-semibold">{item.productName}</h3>
                <p className="text-gray-600">
                    Giá: {item.price.toLocaleString("vi-VN")} VND / {item.unit}
                </p>
                <div className="flex items-center mt-2">
                    <label className="mr-2">Số lượng:</label>
                    <input
                        type="number"
                        min="0"
                        max="100"
                        value={quantity}
                        onChange={handleQuantityChange}
                        className="input input-bordered input-sm w-16 text-black bg-white"
                    />
                </div>
                <p className="text-gray-800 font-semibold">
                    Tổng: {totalItemPrice} VND
                </p>
                <p className="text-gray-500 text-sm">
                    Thêm vào lúc: {new Date(item.addedAt).toLocaleString("vi-VN")}
                </p>
                {localError && <p className="text-red-500 mt-2">{localError}</p>}
            </div>
            <button onClick={handleRemove} className="btn btn-ghost btn-circle">
                <span className="text-red-500">✖</span>
            </button>
        </div>
    );
};

export default CartItem;