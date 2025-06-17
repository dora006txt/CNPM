// src/components/ProductListPage.jsx
import React, { useState, useEffect } from "react";
import axios from "axios";
import ProductCard from "./ProductCard";

const ProductListPage = () => {
  const [products, setProducts] = useState([]);
  const [inventoryData, setInventoryData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

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
        const productsResponse = await axios.get("http://localhost:8080/api/products", {
          headers: { Authorization: authHeader },
        });
        setProducts(productsResponse.data);

        // Lấy dữ liệu tồn kho
        const inventoryResponse = await axios.get("http://localhost:8080/api/v1/inventory", {
          headers: { Authorization: authHeader },
        });
        setInventoryData(inventoryResponse.data);
      } catch (err) {
        setError(err.message || "Lỗi khi tải dữ liệu.");
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  if (loading) return <div>Đang tải...</div>;
  if (error) return <div>{error}</div>;

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4">Danh sách sản phẩm</h1>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {products.map((product) => (
          <ProductCard key={product.id} product={product} inventoryData={inventoryData} />
        ))}
      </div>
    </div>
  );
};

export default ProductListPage;