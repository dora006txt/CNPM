import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { useCart } from "../../context/CartContext";
import CartItem from "../../components/user/Cart/CartItem";
import CartSummary from "../../components/user/Cart/CartSummary";

const CartPage = () => {
    const cartContext = useCart();

    if (!cartContext) {
        return (
            <div className="container mx-auto p-6 text-center">
                <h1 className="text-2xl font-bold mb-4 text-black">Giỏ hàng</h1>
                <div className="alert alert-error shadow-lg mb-6">
                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        fill="none"
                        viewBox="0 0 24 24"
                        className="w-6 h-6 stroke-current"
                    >
                        <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth="2"
                            d="M12 9v2m0 4h.01M12 2a10 10 0 100 20 10 10 0 000-20z"
                        />
                    </svg>
                    <span className="ml-2">Lỗi: Không thể tải giỏ hàng. Vui lòng thử lại.</span>
                </div>
                <Link to="/" className="btn btn-primary bg-purple-600 text-white hover:bg-purple-700">
                    Tiếp tục mua sắm
                </Link>
            </div>
        );
    }

    const { carts, selectedCartId, setSelectedCartId, loading, error, addToCart, fetchCarts } = cartContext;
    const [newItem, setNewItem] = useState({ inventoryId: "", quantity: "" });
    const [addError, setAddError] = useState(null);

    useEffect(() => {
        console.log("Fetching carts on mount");
        fetchCarts();
    }, []);

    useEffect(() => {
        // Debug: In dữ liệu carts và selectedCartId
        console.log("Carts:", JSON.stringify(carts, null, 2));
        console.log("Selected Cart ID:", selectedCartId);

        // Chuẩn hóa carts thành mảng nếu nó là đối tượng
        const cartsArray = Array.isArray(carts) ? carts : carts && carts.cartId ? [carts] : [];

        // Tự động chọn giỏ hàng
        if (cartsArray.length > 0 && !selectedCartId) {
            const cartWithItems = cartsArray.find((cart) => cart.items && cart.items.length > 0);
            if (cartWithItems) {
                setSelectedCartId(cartWithItems.cartId);
                console.log("Auto-selected cart with items:", cartWithItems.cartId);
            } else {
                setSelectedCartId(cartsArray[0].cartId);
                console.log("Auto-selected first cart:", cartsArray[0].cartId);
            }
        }
    }, [carts, selectedCartId, setSelectedCartId]);

    const handleAddToCart = async (e) => {
        e.preventDefault();
        setAddError(null);
        const { inventoryId, quantity } = newItem;
        if (!inventoryId || !quantity || quantity <= 0) {
            setAddError("Vui lòng nhập đầy đủ thông tin và số lượng phải lớn hơn 0.");
            return;
        }
        try {
            await addToCart(Number(inventoryId), Number(quantity));
            setNewItem({ inventoryId: "", quantity: "" });
            await fetchCarts();
            console.log("Added to cart successfully");
        } catch (err) {
            setAddError(err.message || "Lỗi khi thêm sản phẩm.");
            console.error("Add to cart error:", err);
        }
    };

    if (loading) {
        return (
            <div className="container mx-auto p-6 text-center">
                <h1 className="text-2xl font-bold mb-6 text-black">Giỏ hàng</h1>
                <div className="flex justify-center">
                    <span className="loading loading-spinner loading-lg text-purple-600"></span>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="container mx-auto p-6 text-center">
                <h1 className="text-2xl font-bold mb-4 text-black">Giỏ hàng</h1>
                <div className="alert alert-error shadow-lg mb-6">
                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        fill="none"
                        viewBox="0 0 24 24"
                        className="w-6 h-6 stroke-current"
                    >
                        <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth="2"
                            d="M12 9v2m0 4h.01M12 2a10 10 0 100 20 10 10 0 000-20z"
                        />
                    </svg>
                    <span className="ml-2">{error}</span>
                </div>
                <Link to="/" className="btn btn-primary bg-purple-600 text-white hover:bg-purple-700">
                    Tiếp tục mua sắm
                </Link>
            </div>
        );
    }

    // Chuẩn hóa carts thành mảng
    const cartsArray = Array.isArray(carts) ? carts : carts && carts.cartId ? [carts] : [];

    if (!cartsArray.length) {
        return (
            <div className="container mx-auto p-6 text-center">
                <h1 className="text-2xl font-bold mb-6 text-black">Giỏ hàng</h1>
                <p className="mb-6 text-gray-600">Giỏ hàng của bạn đang trống.</p>
                <Link to="/" className="btn btn-primary bg-purple-600 text-white hover:bg-purple-700">
                    Tiếp tục mua sắm
                </Link>
            </div>
        );
    }

    const currentCart = cartsArray.find((cart) => cart.cartId === selectedCartId) || { items: [] };
    console.log("Current Cart:", JSON.stringify(currentCart, null, 2));

    return (
        <div className="container mx-auto p-6">
            <h1 className="text-2xl font-bold mb-6 text-black">Giỏ hàng</h1>



            {currentCart.items.length === 0 ? (
                <div className="text-center mb-6 text-gray-600">
                    <p>Giỏ hàng hiện tại trống. Vui lòng thêm sản phẩm.</p>
                </div>
            ) : (
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    <div className="lg:col-span-2">
                        {currentCart.items.map((item) => (
                            <CartItem key={item.cartItemId} item={item} />
                        ))}
                    </div>
                    <div className="lg:col-span-1">
                        <CartSummary cart={currentCart} />
                    </div>
                </div>
            )}
        </div>
    );
};

export default CartPage;