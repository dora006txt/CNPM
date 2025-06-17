// src/pages/ProductDetailPage.jsx
import React, { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import axios from "axios";
import { useCart } from "../../context/CartContext";
import RecentlyViewed from "../../components/user/common/RecentlyViewed";


const ProductDetailPage = () => {
    const { id } = useParams(); // id là productId
    const [product, setProduct] = useState(null);
    const [branches, setBranches] = useState([]);
    const [selectedBranchId, setSelectedBranchId] = useState(null);
    const [quantity, setQuantity] = useState(1);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { addToCart, error: cartError } = useCart();
    const navigate = useNavigate();

    useEffect(() => {
        const fetchProductAndBranches = async () => {
            try {
                setLoading(true);
                const token = localStorage.getItem("token");
                if (!token) {
                    throw new Error("Vui lòng đăng nhập để xem chi tiết sản phẩm!");
                }

                const authHeader = token.startsWith("Bearer ") ? token : `Bearer ${token}`;

                // Lấy thông tin sản phẩm
                const productResponse = await axios.get(`http://localhost:8080/api/products/${id}`, {
                    headers: { Authorization: authHeader },
                });
                setProduct(productResponse.data);

                // Lấy danh sách chi nhánh có sản phẩm
                const branchesResponse = await axios.get(
                    `http://localhost:8080/api/v1/inventory/product/${id}`,
                    {
                        headers: { Authorization: authHeader },
                    }
                );
                setBranches(branchesResponse.data || []);

                // Tự động chọn chi nhánh đầu tiên nếu có
                if (branchesResponse.data.length > 0) {
                    setSelectedBranchId(branchesResponse.data[0].branchId);
                }
            } catch (err) {
                console.error("Error fetching data:", err.response?.data || err.message);
                if (err.message.includes("Vui lòng đăng nhập") || err.response?.status === 403) {
                    alert("Vui lòng đăng nhập để xem chi tiết sản phẩm!");
                    localStorage.removeItem("token");
                    navigate("/login");
                } else {
                    setError(
                        err.response?.data?.message ||
                        err.message ||
                        "Lỗi khi tải dữ liệu. Vui lòng kiểm tra token hoặc liên hệ admin."
                    );
                }
            } finally {
                setLoading(false);
            }
        };
        fetchProductAndBranches();
    }, [id, navigate]);

    useEffect(() => {
        if (product?.id) {
            const viewed = JSON.parse(localStorage.getItem("recentlyViewed") || "[]");

            // Đưa sản phẩm hiện tại lên đầu danh sách, không trùng lặp
            const newList = [product.id, ...viewed.filter(id => id !== product.id)];

            // Giới hạn chỉ lưu tối đa 10 sản phẩm
            localStorage.setItem("recentlyViewed", JSON.stringify(newList.slice(0, 10)));
        }
    }, [product?.id]);

    const handleAddToCart = async () => {
        if (!selectedBranchId) {
            alert("Vui lòng chọn chi nhánh!");
            return;
        }
        const branch = branches.find((b) => b.branchId === selectedBranchId);
        if (!branch || quantity > branch.quantityInStock) {
            alert("Số lượng vượt quá tồn kho tại chi nhánh này!");
            return;
        }
        try {
            await addToCart(product.id, quantity, selectedBranchId);
            alert("Đã thêm vào giỏ hàng thành công!");
        } catch (err) {
            console.error("Add to cart failed:", err.message, cartError);
            if (cartError && cartError.includes("Phiên đăng nhập không hợp lệ")) {
                alert(cartError);
                localStorage.removeItem("token");
                navigate("/login");
            } else if (cartError && cartError.includes("Sản phẩm không tìm thấy")) {
                setError(cartError);
            } else if (cartError && cartError.includes("Số lượng vượt quá tồn kho")) {
                setError(cartError);
            } else {
                setError("Lỗi khi thêm vào giỏ hàng: " + (err.message || cartError || "Không xác định"));
            }
        }
    };

    const handleConsult = () => {
        navigate("/consultation");
    };

    if (loading) {
        return <div className="container mx-auto p-4 bg-white text-black">Đang tải...</div>;
    }

    if (error) {
        return (
            <div className="container mx-auto p-4 bg-white text-black">
                <h1 className="text-2xl font-bold mb-4">Chi tiết sản phẩm</h1>
                <p className="text-red-500">{error}</p>
                <Link to="/products" className="btn btn-primary mt-4">
                    Quay lại danh sách sản phẩm
                </Link>
            </div>
        );
    }

    if (!product) {
        return (
            <div className="container mx-auto p-4 bg-white text-black">
                <h1 className="text-2xl font-bold mb-4">Chi tiết sản phẩm</h1>
                <p>Sản phẩm không tồn tại</p>
                <Link to="/products" className="btn btn-primary mt-4">
                    Quay lại danh sách sản phẩm
                </Link>
            </div>
        );
    }

    return (
        <div className="container mx-auto p-4 bg-white text-black">
            <Link to="/products" className="btn btn-ghost mb-4">
                Quay lại danh sách sản phẩm
            </Link>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                    <img
                        src={product.imageUrl || "https://placehold.co/400x400"}
                        alt={product.name}
                        className="w-full h-auto rounded-lg shadow-md"
                    />
                </div>
                <div>
                    <h1 className="text-3xl font-bold mb-2">{product.name}</h1>
                    <p className="text-xl text-gray-600 mb-4">
                        {branches.length > 0 ? (
                            branches[0].discountPrice ? (
                                <>
                                    <span className="line-through">
                                        {branches[0].price.toLocaleString("vi-VN")}đ
                                    </span>{" "}
                                    <span className="text-red-500">
                                        {branches[0].discountPrice.toLocaleString("vi-VN")}đ
                                    </span>
                                </>
                            ) : (
                                <span>{branches[0].price.toLocaleString("vi-VN")}đ</span>
                            )
                        ) : (
                            "Chưa có giá"
                        )}
                    </p>
                    <p className="mb-2">
                        <strong>Hộp:</strong> 1 {product.unit || "chai"}
                    </p>
                    <p className="mb-2">
                        <strong>Đóng gói:</strong> {product.packaging || "Không có thông tin"}
                    </p>
                    <p className="mb-2">
                        <strong>Nhà sản xuất:</strong> {product.manufacturer?.name || "Chưa có"}
                    </p>
                    <p className="mb-2">
                        <strong>Danh mục:</strong> {product.category?.name || "Chưa có"}
                    </p>
                    <p className="mb-2">
                        <strong>Yêu cầu đơn thuốc:</strong>{" "}
                        {product.isPrescriptionRequired ? "Có" : "Không"}
                    </p>
                    <p className="mb-2">
                        <strong>Mô tả:</strong> {product.description || "Không có mô tả"}
                    </p>
                    <p className="mb-2">
                        <strong>Thành phần:</strong> {product.ingredients || "Không có thông tin"}
                    </p>
                    <p className="mb-2">
                        <strong>Hướng dẫn sử dụng:</strong>{" "}
                        {product.usageInstructions || "Không có thông tin"}
                    </p>
                    <p className="mb-2">
                        <strong>Chống chỉ định:</strong>{" "}
                        {product.contraindications || "Không có thông tin"}
                    </p>
                    <p className="mb-2">
                        <strong>Tác dụng phụ:</strong> {product.sideEffects || "Không có thông tin"}
                    </p>
                    <p className="mb-2">
                        <strong>Điều kiện bảo quản:</strong>{" "}
                        {product.storageConditions || "Không có thông tin"}
                    </p>
                    <p className="mb-2">
                        <strong>Xuất xứ:</strong> {product.manufacturer?.country?.countryName || "Không có thông tin"}
                    </p>

                    <div className="mb-4">
                        <label className="block text-sm font-medium mb-1">Chọn chi nhánh:</label>
                        {branches.length > 0 ? (
                            <select
                                className="select select-bordered w-full max-w-xs"
                                value={selectedBranchId || ""}
                                onChange={(e) => setSelectedBranchId(Number(e.target.value))}
                            >
                                {branches.map((branch) => (
                                    <option key={branch.branchId} value={branch.branchId}>
                                        {branch.branchName} (Tồn kho: {branch.quantityInStock})
                                    </option>
                                ))}
                            </select>
                        ) : (
                            <p className="text-red-500">Sản phẩm không có sẵn tại chi nhánh nào!</p>
                        )}
                    </div>

                    <div className="flex items-center mb-4">
                        <label className="mr-2">Số lượng:</label>
                        <input
                            type="number"
                            min="1"
                            max={branches.find((b) => b.branchId === selectedBranchId)?.quantityInStock || 100}
                            value={quantity}
                            onChange={(e) =>
                                setQuantity(
                                    Math.min(
                                        Math.max(1, Number(e.target.value)),
                                        branches.find((b) => b.branchId === selectedBranchId)?.quantityInStock || 100
                                    )
                                )
                            }
                            className="input input-bordered input-sm w-20 text-black"
                            disabled={!selectedBranchId}
                        />
                    </div>

                    <div className="space-x-4">
                        {!product.isPrescriptionRequired && (
                            <button
                                onClick={handleAddToCart}
                                className="btn btn-primary"
                                disabled={!selectedBranchId || loading}
                            >
                                Chọn mua
                            </button>
                        )}
                        {product.isPrescriptionRequired && (
                            <button
                                onClick={handleConsult}
                                className="btn btn-success"
                                disabled={loading}
                            >
                                Tư vấn ngay
                            </button>
                        )}
                        <Link to="/pharmacy-locator" className="btn btn-ghost">
                            Tìm nhà thuốc
                        </Link>
                    </div>
                </div>
            </div>

            <RecentlyViewed />
        </div>
    );
};

export default ProductDetailPage;