import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import ProductCard from "../../components/user/ProductCard";

const CategoriesPage = () => {
    const { slug } = useParams();
    const [products, setProducts] = useState([]);
    const [inventoryData, setInventoryData] = useState([]); // Thêm state cho inventoryData
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [categoryId, setCategoryId] = useState(null);
    const [sortOption, setSortOption] = useState("default");
    const [priceRange, setPriceRange] = useState({ min: 0, max: Infinity });
    const [selectedBrand, setSelectedBrand] = useState("");
    const [selectedManufacturer, setSelectedManufacturer] = useState("");
    const [brands, setBrands] = useState([]);
    const [manufacturers, setManufacturers] = useState([]);

    useEffect(() => {
        const fetchCategoryAndProducts = async () => {
            try {
                setLoading(true);
                const token = localStorage.getItem("token");
                const authHeader = token && (token.startsWith("Bearer ") ? token : `Bearer ${token}`);

                // Lấy categoryId từ slug
                const categoriesResponse = await axios.get("http://localhost:8080/api/categories");
                const category = categoriesResponse.data.find((cat) => cat.slug === slug);
                if (!category) {
                    throw new Error("Danh mục không tồn tại.");
                }
                setCategoryId(category.id);

                // Lấy danh sách sản phẩm
                const productsResponse = await axios.get("http://localhost:8080/api/products");
                let filteredProducts = productsResponse.data.filter(
                    (product) => product.category && product.category.id === category.id
                );

                // Lấy danh sách thương hiệu và nhà sản xuất độc nhất
                const uniqueBrands = [...new Set(productsResponse.data.map((p) => p.brand?.name).filter(Boolean))];
                const uniqueManufacturers = [...new Set(productsResponse.data.map((p) => p.manufacturer?.name).filter(Boolean))];
                setBrands(uniqueBrands);
                setManufacturers(uniqueManufacturers);

                // Lấy dữ liệu tồn kho
                const inventoryResponse = await axios.get("http://localhost:8080/api/v1/inventory", {
                    headers: authHeader ? { Authorization: authHeader } : {},
                });
                console.log("Inventory data (Categories):", JSON.stringify(inventoryResponse.data, null, 2)); // Debug
                setInventoryData(Array.isArray(inventoryResponse.data) ? inventoryResponse.data : []);

                // Áp dụng bộ lọc và sắp xếp
                filteredProducts = applyFiltersAndSort(filteredProducts);
                setProducts(filteredProducts);
            } catch (err) {
                setError(err.message || "Không thể tải sản phẩm hoặc danh mục. Vui lòng thử lại.");
                console.error("Fetch error:", err);
            } finally {
                setLoading(false);
            }
        };
        fetchCategoryAndProducts();
    }, [slug, sortOption, priceRange, selectedBrand, selectedManufacturer]);

    const applyFiltersAndSort = (productsList) => {
        let result = [...productsList];

        // Lọc theo giá
        if (priceRange.max !== Infinity) {
            result = result.filter((product) => {
                const productInventory = inventoryData.filter((item) => item.productId === product.id) || [];
                const prices = productInventory.map((item) => item.price).filter((price) => price !== null);
                const minPrice = prices.length > 0 ? Math.min(...prices) : 0;
                return minPrice >= priceRange.min && minPrice <= priceRange.max;
            });
        }

        // Lọc theo thương hiệu
        if (selectedBrand) {
            result = result.filter((product) => product.brand?.name === selectedBrand);
        }

        // Lọc theo nhà sản xuất
        if (selectedManufacturer) {
            result = result.filter((product) => product.manufacturer?.name === selectedManufacturer);
        }

        // Sắp xếp
        if (sortOption === "lowToHigh") {
            result.sort((a, b) => {
                const aInventory = inventoryData.filter((item) => item.productId === a.id) || [];
                const bInventory = inventoryData.filter((item) => item.productId === b.id) || [];
                const aPrice = aInventory.length > 0 ? Math.min(...aInventory.map((item) => item.price)) : 0;
                const bPrice = bInventory.length > 0 ? Math.min(...bInventory.map((item) => item.price)) : 0;
                return aPrice - bPrice;
            });
        } else if (sortOption === "highToLow") {
            result.sort((a, b) => {
                const aInventory = inventoryData.filter((item) => item.productId === a.id) || [];
                const bInventory = inventoryData.filter((item) => item.productId === b.id) || [];
                const aPrice = aInventory.length > 0 ? Math.min(...aInventory.map((item) => item.price)) : 0;
                const bPrice = bInventory.length > 0 ? Math.min(...bInventory.map((item) => item.price)) : 0;
                return bPrice - aPrice;
            });
        }

        return result;
    };

    // Hàm tính giá thấp nhất và giá giảm thấp nhất
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
            productInventory,
        };
    };

    if (loading) {
        return <div className="p-4">Đang tải...</div>;
    }

    if (error) {
        return <div className="p-4 text-red-500">{error}</div>;
    }

    return (
        <div className="container mx-auto p-4">
            <div className="mb-4 text-black pt-5 pb-5">
                <div className="flex space-x-4 items-center">
                    <select
                        value={sortOption}
                        onChange={(e) => setSortOption(e.target.value)}
                        className="border rounded px-2 py-1"
                    >
                        <option value="default">Sắp xếp theo</option>
                        <option value="bestSellers">Bán chạy</option>
                        <option value="lowToHigh">Giá thấp</option>
                        <option value="highToLow">Giá cao</option>
                    </select>
                    <div className="flex space-x-2">
                        <label>Giá:</label>
                        <select
                            value={priceRange.max === Infinity ? "all" : `${priceRange.min}-${priceRange.max}`}
                            onChange={(e) => {
                                const [min, max] = e.target.value === "all" ? [0, Infinity] : e.target.value.split("-").map(Number);
                                setPriceRange({ min, max });
                            }}
                            className="border rounded px-2 py-1"
                        >
                            <option value="all">Tất cả</option>
                            <option value="0-100000">Dưới 100,000đ</option>
                            <option value="100000-300000">100,000đ - 300,000đ</option>
                            <option value="300000-500000">300,000đ - 500,000đ</option>
                            <option value="500000-Infinity">Trên 500,000đ</option>
                        </select>
                    </div>
                    <div className="flex space-x-2">
                        <label>Thương hiệu:</label>
                        <select
                            value={selectedBrand}
                            onChange={(e) => setSelectedBrand(e.target.value)}
                            className="border rounded px-2 py-1"
                        >
                            <option value="">Tất cả</option>
                            {brands.map((brand) => (
                                <option key={brand} value={brand}>{brand}</option>
                            ))}
                        </select>
                    </div>
                    <div className="flex space-x-2">
                        <label>Nhà sản xuất:</label>
                        <select
                            value={selectedManufacturer}
                            onChange={(e) => setSelectedManufacturer(e.target.value)}
                            className="border rounded px-2 py-1"
                        >
                            <option value="">Tất cả</option>
                            {manufacturers.map((manufacturer) => (
                                <option key={manufacturer} value={manufacturer}>{manufacturer}</option>
                            ))}
                        </select>
                    </div>
                </div>
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
                {products.length > 0 ? (
                    products.map((product) => {
                        const { minPrice, minDiscountPrice, productInventory } = getPriceAndInventory(product.id);
                        const unit = product.unit || product.packaging?.split(" ")[0] || "sản phẩm";
                        const packagingDetail = product.packaging ? ` (${product.packaging})` : "";

                        return (
                            <ProductCard
                                key={product.id}
                                product={product}
                                minPrice={minPrice}
                                minDiscountPrice={minDiscountPrice}
                                unit={unit}
                                packagingDetail={packagingDetail}
                                inventoryData={productInventory} // Truyền inventoryData đã lọc
                            />
                        );
                    })
                ) : (
                    <p>Không có sản phẩm nào trong danh mục này.</p>
                )}
            </div>
        </div>
    );
};

export default CategoriesPage;