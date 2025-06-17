import React from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import { useCart } from "../../context/CartContext";

const ProductCard = ({ product, minPrice, minDiscountPrice, unit, packagingDetail, inventoryData }) => {
    const navigate = useNavigate();
    const { addToCart } = useCart();

    const discountAmount = minDiscountPrice && minPrice > minDiscountPrice
        ? (minPrice - minDiscountPrice).toLocaleString("vi-VN")
        : null;

    // Xử lý khi nhấn "Chọn mua" (thêm vào giỏ hàng)
    const handleAddToCart = async () => {
        try {
            console.log("Product data:", product); // Debug
            console.log("Inventory data:", inventoryData); // Debug

            // Lấy inventoryId từ inventoryData (giả định là mảng)
            const inventoryItem = inventoryData && inventoryData.length > 0 ? inventoryData[0] : null;
            if (!inventoryItem || !inventoryItem.inventoryId) {
                throw new Error("Không tìm thấy thông tin kho hàng. Vui lòng thử lại sau.");
            }

            const inventoryId = inventoryItem.inventoryId;
            await addToCart(inventoryId, 1); // Gửi inventoryId, quantity = 1
            alert("Đã thêm vào giỏ hàng thành công!");
        } catch (err) {
            console.error("Add to cart failed:", err.message);
            alert("Lỗi khi thêm vào giỏ hàng: " + (err.message || "Không xác định"));
        }
    };

    // Xử lý khi click vào hình ảnh (chuyển hướng đến ProductDetailPage)
    const handleImageClick = () => {
        navigate(`/products/${product.id}`);
    };

    return (
        <div
            className="card bg-white shadow-md rounded-lg overflow-hidden relative w-full transition-transform hover:scale-105"
            style={{ maxWidth: "220px" }}
        >
            {discountAmount && (
                <div className="absolute top-2 right-2 bg-red-500 text-white text-xs font-bold rounded-full w-10 h-10 flex items-center justify-center">
                    -{discountAmount}đ
                </div>
            )}
            <figure className="w-full h-40 cursor-pointer" onClick={handleImageClick}>
                <img
                    src={product.imageUrl || "https://placehold.co/150x150"}
                    alt={product.name || "Sản phẩm"}
                    className="w-full h-full object-cover"
                    loading="lazy"
                    onError={(e) => (e.target.src = "https://placehold.co/150x150")}
                />
            </figure>
            <div className="card-body p-3">
                <h3 className="text-sm font-bold text-black line-clamp-2">
                    {product.name || "Không có tên"}
                    {packagingDetail}
                </h3>
                <p className="text-xs text-gray-600 line-clamp-1 mt-1">
                    {product.manufacturer?.name || "N/A"}
                </p>
                <p className="text-base font-bold text-black mt-1">
                    {minPrice ? (
                        minDiscountPrice ? (
                            <span>
                                <span className="line-through text-sm text-gray-600">
                                    {minPrice.toLocaleString("vi-VN")}đ
                                </span>{" "}
                                <span className="text-base text-red-600 font-bold">
                                    {minDiscountPrice.toLocaleString("vi-VN")}đ
                                </span>{" "}
                                <span className="text-sm text-black">
                                    / {unit}
                                </span>
                            </span>
                        ) : (
                            <span>
                                {minPrice.toLocaleString("vi-VN")}đ{" "}
                                <span className="text-sm">
                                    / {unit}
                                </span>
                            </span>
                        )
                    ) : (
                        <span className="text-gray-500 text-sm">
                            Liên hệ để biết giá
                        </span>
                    )}
                </p>
                {product.isPrescriptionRequired ? (
                    <Link
                        to={`/products/${product.id}`}
                        className="btn btn-primary btn-sm mt-2 w-full text-white bg-blue-600 hover:bg-blue-700 rounded-md text-sm py-2"
                    >
                        Xem chi tiết
                    </Link>
                ) : (
                    <button
                        onClick={handleAddToCart}
                        className="btn btn-primary btn-sm mt-2 w [sửa lỗi] w-full text-white bg-blue-600 hover:bg-blue-700 rounded-md text-sm py-2"
                    >
                        Chọn mua
                    </button>
                )}
            </div>
        </div>
    );
};

export default ProductCard;