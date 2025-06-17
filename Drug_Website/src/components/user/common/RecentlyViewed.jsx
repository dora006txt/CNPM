import React, { useEffect, useState } from "react";
import axios from "axios";
import ProductCard from "../ProductCard"; // Đường dẫn tùy theo cấu trúc thư mục

const RecentlyViewed = () => {
    const [products, setProducts] = useState([]);

    useEffect(() => {
        const viewedIds = JSON.parse(localStorage.getItem("recentlyViewed") || "[]");

        if (viewedIds.length > 0) {
            axios.get("http://localhost:8080/api/products")
                .then((res) => {
                    const allProducts = res.data;

                    const filtered = viewedIds
                        .map((id) => allProducts.find((p) => p.id === id))
                        .filter(Boolean);

                    setProducts(filtered);
                })
                .catch((err) => console.error("Failed to load products:", err));
        }
    }, []);

    if (products.length === 0) return null;

    return (
        <div className="mt-15">
            <h2 className="text- text-black font-semibold mb-4">Sản phẩm vừa xem</h2>
            <div className="flex flex-wrap gap-4">
                {products.map((product) => (
                    <ProductCard
                        key={product.id}
                        product={product}
                        minPrice={product.minPrice || 10000} // Tạm giá giả định nếu chưa có
                        minDiscountPrice={product.minDiscountPrice || null}
                        unit={product.unit}
                        packagingDetail={` (${product.packaging})`}
                        inventoryData={[]} // Nếu cần kiểm tra tồn kho
                    />
                ))}
            </div>
        </div>
    );
};

export default RecentlyViewed;
