import React, { createContext, useContext, useState, useEffect } from "react";
import axios from "axios";
import { API_ENDPOINTS } from "../config/apiConfig";
import { useNavigate } from "react-router-dom";

const CartContext = createContext();

export const CartProvider = ({ children }) => {
    const [carts, setCarts] = useState([]);
    const [selectedCartId, setSelectedCartId] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const getToken = () => {
        const token = localStorage.getItem("token");
        if (!token) {
            setError("Vui lòng đăng nhập để tiếp tục");
            navigate("/login");
            throw new Error("Không có token xác thực");
        }
        return token.startsWith("Bearer ") ? token : `Bearer ${token}`;
    };

    const fetchCarts = async () => {
        if (loading) return;
        try {
            setLoading(true);
            const token = getToken();
            const response = await axios.get(API_ENDPOINTS.CART, {
                headers: { Authorization: token },
            });
            setCarts(response.data || []);
            setError(null);
        } catch (err) {
            console.error("Fetch carts error:", err.response?.data || err.message);
            if (err.response?.status === 401) {
                setError("Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.");
                localStorage.removeItem("token");
                navigate("/login");
            } else {
                setError(err.response?.data?.message || "Lỗi khi lấy giỏ hàng");
            }
            setCarts([]);
        } finally {
            setLoading(false);
        }
    };

    const addToCart = async (inventoryId, quantity) => {
        try {
            setLoading(true);
            const token = getToken();
            console.log("Sending addToCart request:", { inventoryId, quantity }); // Debug
            const response = await axios.post(
                API_ENDPOINTS.CART_ITEMS,
                { inventoryId, quantity },
                { headers: { Authorization: token, "Content-Type": "application/json" } }
            );
            console.log("addToCart response:", response.data); // Debug
            await fetchCarts();
            setSelectedCartId(response.data.cartId);
            setError(null);
            return response.data;
        } catch (err) {
            console.error("Add to cart error:", err.response?.data || err.message);
            const errorMessage = err.response?.data?.message || err.message;
            if (err.response?.status === 401 || err.response?.status === 403) {
                setError("Phiên đăng nhập không hợp lệ hoặc bạn không có quyền. Vui lòng đăng nhập lại.");
                localStorage.removeItem("token");
                navigate("/login");
            } else if (err.response?.status === 404) {
                setError("Sản phẩm không tìm thấy trong chi nhánh này.");
            } else if (err.response?.status === 409) {
                setError("Số lượng vượt quá tồn kho.");
            } else {
                setError(`Lỗi khi thêm vào giỏ hàng: ${errorMessage}`);
            }
            throw new Error(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    const removeCartItem = async (cartItemId) => {
        try {
            setLoading(true);
            const token = getToken();
            await axios.delete(`${API_ENDPOINTS.CART_ITEMS}/${cartItemId}`, {
                headers: { Authorization: token },
            });
            await fetchCarts();
            setError(null);
        } catch (err) {
            console.error("Remove cart item error:", err.response?.data || err.message);
            if (err.response?.status === 401) {
                setError("Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.");
                navigate("/login");
            } else {
                setError(err.response?.data?.message || "Lỗi khi xóa sản phẩm");
            }
            throw err;
        } finally {
            setLoading(false);
        }
    };

    const updateCartItem = async (cartItemId, quantity) => {
        try {
            setLoading(true);
            const token = getToken();
            const response = await axios.put(
                `${API_ENDPOINTS.CART_ITEMS}/${cartItemId}`,
                { quantity },
                { headers: { Authorization: token, "Content-Type": "application/json" } }
            );
            await fetchCarts();
            setError(null);
        } catch (err) {
            console.error("Update cart item error:", err.response?.data || err.message);
            if (err.response?.status === 401) {
                setError("Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.");
                navigate("/login");
            } else if (err.response?.status === 409) {
                setError("Số lượng vượt quá tồn kho.");
            } else {
                setError(err.response?.data?.message || "Lỗi khi cập nhật số lượng");
            }
            throw err;
        } finally {
            setLoading(false);
        }
    };

    const clearCart = async () => {
        try {
            setLoading(true);
            const token = getToken();
            const currentCart = carts.find((cart) => cart.cartId === selectedCartId);
            if (currentCart && currentCart.items.length > 0) {
                // Xóa từng cartItem
                await Promise.all(
                    currentCart.items.map((item) =>
                        axios.delete(`${API_ENDPOINTS.CART_ITEMS}/${item.cartItemId}`, {
                            headers: { Authorization: token },
                        })
                    )
                );
            }
            await fetchCarts(); // Làm mới giỏ hàng từ backend
            setError(null);
        } catch (err) {
            console.error("Clear cart error:", err.response?.data || err.message);
            if (err.response?.status === 401 || err.response?.status === 403) {
                setError("Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.");
                localStorage.removeItem("token");
                navigate("/login");
            } else {
                setError(err.response?.data?.message || "Lỗi khi xóa giỏ hàng");
                await fetchCarts(); // Làm mới nếu xóa thất bại
            }
            throw err;
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (localStorage.getItem("token")) {
            fetchCarts();
        }
    }, []);

    useEffect(() => {
        if (carts.length > 0 && !selectedCartId) {
            setSelectedCartId(carts[0].cartId);
        }
    }, [carts, selectedCartId]);

    return (
        <CartContext.Provider
            value={{
                carts,
                selectedCartId,
                setSelectedCartId,
                loading,
                error,
                fetchCarts,
                addToCart,
                removeCartItem,
                updateCartItem,
                clearCart,
            }}
        >
            {children}
        </CartContext.Provider>
    );
};

export const useCart = () => useContext(CartContext);