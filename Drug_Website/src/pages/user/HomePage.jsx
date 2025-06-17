import React, { useState, useEffect } from "react";
import { getProducts } from "../../api/productApi";
import { getCategories } from "../../api/categoryApi";
import axios from "axios";
import ActiveBanners from "../../components/user/layout/banner/ActiveBanner";
import ProductCard from "../../components/user/ProductCard";
import RecentlyViewed from "../../components/user/common/RecentlyViewed";
import BannerSlider from "../../components/user/layout/banner/BannerSlider";

const HomePage = () => {
    const [products, setProducts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [inventoryData, setInventoryData] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const pageSize = 10; // Thay đổi pageSize thành 10

    useEffect(() => {
        const fetchData = async () => {
            try {
                setLoading(true);
                const token = localStorage.getItem("token");
                if (!token) {
                    throw new Error("Vui lòng đăng nhập!");
                }
                const authHeader = token.startsWith("Bearer ") ? token : `Bearer ${token}`;

                // Lấy danh sách sản phẩm
                const fetchedProducts = await getProducts({ page: currentPage, size: pageSize });
                console.log("Fetched Products:", JSON.stringify(fetchedProducts, null, 2));
                if (!Array.isArray(fetchedProducts)) {
                    throw new Error("Dữ liệu sản phẩm không phải là mảng.");
                }
                setProducts(fetchedProducts);
                setTotalPages(Math.ceil(fetchedProducts.length / pageSize) || 1);

                // Lấy danh mục
                const fetchedCategories = await getCategories();
                if (!Array.isArray(fetchedCategories)) {
                    throw new Error("Dữ liệu danh mục không phải là mảng.");
                }
                setCategories(fetchedCategories);

                // Lấy dữ liệu tồn kho
                const inventoryResponse = await axios.get("http://localhost:8080/api/v1/inventory", {
                    headers: { Authorization: authHeader },
                });
                console.log("Inventory data:", JSON.stringify(inventoryResponse.data, null, 2)); // Debug
                setInventoryData(Array.isArray(inventoryResponse.data) ? inventoryResponse.data : []);
            } catch (err) {
                setError(err.message);
                console.error("API Error:", err.message);
                setProducts([]);
                setCategories([]);
                setInventoryData([]);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [currentPage]);

    const handlePageChange = (page) => {
        if (page >= 1 && page <= totalPages) {
            setCurrentPage(page);
            window.scrollTo({ top: 0, behavior: "smooth" });
        }
    };

    // Hàm tính giá thấp nhất và giá giảm thấp nhất, đồng thời lấy inventoryData cho sản phẩm
    const getPriceAndInventory = (productId) => {
        const productInventory = inventoryData.filter((item) => item.productId === productId) || [];
        if (!productInventory.length) {
            return { minPrice: null, minDiscountPrice: null, productInventory: [] };
        }

        const prices = productInventory.map((item) => item.price);
        const discountPrices = productInventory
            .map((item) => item.discountPrice)
            .filter((price) => price !== null);

        return {
            minPrice: Math.min(...prices),
            minDiscountPrice: discountPrices.length > 0 ? Math.min(...discountPrices) : null,
            productInventory, // Trả về inventoryData đã lọc
        };
    };

    return (
        <div className="container mx-auto px-4 py-6 text-black">
            {/* Banner chính */}
            <section className="mb-20">
                <BannerSlider />
            </section>
            {/* <section className="mb-6">
                <ActiveBanners />
            </section> */}
            {/* Sản phẩm */}
            {loading ? (
                <div className="flex justify-center mb-6">
                    <span className="loading loading-spinner loading-lg"></span>
                </div>
            ) : error ? (
                <div className="alert alert-error mb-6 bg-red-100 text-red-800 border-red-400">
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
            ) : products.length > 0 ? (
                <>
                    <h2 className="text-2xl font-bold mb-4 text-black">Sản phẩm bán chạy nhất</h2>
                    <div className="grid grid-cols-2 md:grid-cols-5 gap-4 mb-6">
                        {products.slice(0, 10).map((product) => { // Giới hạn 10 sản phẩm
                            const { minPrice, minDiscountPrice, productInventory } = getPriceAndInventory(product.id);
                            const unit = product.packaging || "Hộp";
                            const packagingDetail = product.packaging ? ` (${product.packaging})` : "";

                            return (
                                <ProductCard
                                    key={product.id || product.name}
                                    product={product}
                                    minPrice={minPrice}
                                    minDiscountPrice={minDiscountPrice}
                                    unit={unit}
                                    packagingDetail={packagingDetail}
                                    inventoryData={productInventory} // Truyền inventoryData đã lọc
                                />
                            );
                        })}
                    </div>
                    {totalPages > 1 && (
                        <div className="flex justify-center mt-6">
                            <button
                                onClick={() => handlePageChange(currentPage - 1)}
                                disabled={currentPage === 1}
                                className="px-4 py-2 mx-1 bg-gray-200 rounded-lg disabled:opacity-50 text-black"
                            >
                                Trước
                            </button>
                            <span className="px-4 py-2 mx-1 text-black">
                                Trang {currentPage} / {totalPages}
                            </span>
                            <button
                                onClick={() => handlePageChange(currentPage + 1)}
                                disabled={currentPage === totalPages}
                                className="px-4 py-2 mx-1 bg-gray-200 rounded-lg disabled:opacity-50 text-black"
                            >
                                Sau
                            </button>
                        </div>
                    )}
                </>
            ) : (
                <div className="text-center mb-6 text-black">
                    <p>Không có sản phẩm nào để hiển thị.</p>
                </div>
            )}
            <RecentlyViewed />
        </div>
    );
};

export default HomePage;