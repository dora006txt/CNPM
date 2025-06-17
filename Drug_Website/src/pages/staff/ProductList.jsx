import React, { useState, useEffect } from "react";
import { FaEdit, FaTrash, FaPlus } from "react-icons/fa";
import { Dialog, Transition } from "@headlessui/react";
import { Fragment } from "react";
import api from "../../api/axios";
import { useAuth } from "../../context/AuthContext";

const ProductList = () => {
    const { user } = useAuth();
    const [products, setProducts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [brands, setBrands] = useState([]);
    const [manufacturers, setManufacturers] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [filterCategory, setFilterCategory] = useState("");
    const [filterStatus, setFilterStatus] = useState("");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const [currentProduct, setCurrentProduct] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // Khởi tạo formData với các trường rỗng, dùng cho cả thêm và chỉnh sửa
    const initialFormData = {
        name: "",
        sku: "",
        description: "",
        ingredients: "",
        usageInstructions: "",
        contraindications: "",
        sideEffects: "",
        storageConditions: "",
        packaging: "",
        unit: "",
        imageUrl: "",
        category: "",
        brandId: "",
        manufacturerId: "",
        isPrescriptionRequired: false,
        status: "ACTIVE",
    };

    const [formData, setFormData] = useState(initialFormData);

    // Lấy dữ liệu sản phẩm, danh mục, thương hiệu, nhà sản xuất từ API
    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            try {
                const [productsResponse, categoriesResponse, brandsResponse, manufacturersResponse] = await Promise.all([
                    api.get("/api/products"),
                    api.get("/api/categories"),
                    api.get("/api/brands"),
                    api.get("/api/manufacturers"),
                ]);
                console.log("Products data:", productsResponse.data);
                console.log("Categories data:", categoriesResponse.data);
                console.log("Brands data:", brandsResponse.data);
                console.log("Manufacturers data:", manufacturersResponse.data);
                setProducts(productsResponse.data || []);
                setCategories(categoriesResponse.data || []);
                setBrands(brandsResponse.data || []);
                setManufacturers(manufacturersResponse.data || []);
            } catch (err) {
                setError(
                    err.response?.data?.message || "Không thể tải dữ liệu. Vui lòng thử lại."
                );
                console.error("Fetch error:", err);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    // Lọc sản phẩm dựa trên tìm kiếm, danh mục, trạng thái
    const filteredProducts = products.filter((product) => {
        const matchesSearch = product.name
            .toLowerCase()
            .includes(searchTerm.toLowerCase());

        const categoryName = product.category?.name;
        const matchesCategory = filterCategory
            ? categoryName === filterCategory
            : true;

        const productStatus = product.status.toLowerCase();
        const matchesStatus = filterStatus
            ? productStatus === filterStatus.toLowerCase()
            : true;

        return matchesSearch && matchesCategory && matchesStatus;
    });

    console.log("Filtered Products:", filteredProducts);

    // Mở form thêm sản phẩm với các trường rỗng
    const openAddModal = () => {
        setIsEditMode(false);
        setFormData(initialFormData);
        setIsModalOpen(true);
    };

    // Mở form chỉnh sửa sản phẩm với dữ liệu hiện có
    const openEditModal = (product) => {
        setIsEditMode(true);
        setCurrentProduct(product);
        setFormData({
            name: product.name || "",
            sku: product.sku || "",
            description: product.description || "",
            ingredients: product.ingredients || "",
            usageInstructions: product.usageInstructions || "",
            contraindications: product.contraindications || "",
            sideEffects: product.sideEffects || "",
            storageConditions: product.storageConditions || "",
            packaging: product.packaging || "",
            unit: product.unit || "",
            imageUrl: product.imageUrl || "",
            category: product.category?.name || "",
            brandId: product.brand?.id?.toString() || "",
            manufacturerId: product.manufacturer?.id?.toString() || "",
            isPrescriptionRequired: product.isPrescriptionRequired || false,
            status: product.status || "ACTIVE",
        });
        setIsModalOpen(true);
    };

    // Đóng form
    const closeModal = () => {
        setIsModalOpen(false);
        setCurrentProduct(null);
    };

    // Xử lý submit form (thêm hoặc chỉnh sửa)
    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để thực hiện hành động này!");
            return;
        }
        setLoading(true);
        try {
            if (!formData.name || !formData.category || !formData.brandId || !formData.manufacturerId) {
                setError("Vui lòng nhập tên sản phẩm, chọn danh mục, thương hiệu và nhà sản xuất!");
                setLoading(false);
                return;
            }
            const categoryId = categories.find((cat) => cat.name === formData.category)?.id;
            if (!categoryId) {
                setError("Danh mục không hợp lệ!");
                setLoading(false);
                return;
            }
            const payload = {
                name: formData.name,
                sku: formData.sku,
                description: formData.description,
                ingredients: formData.ingredients,
                usageInstructions: formData.usageInstructions,
                contraindications: formData.contraindications,
                sideEffects: formData.sideEffects,
                storageConditions: formData.storageConditions,
                packaging: formData.packaging,
                unit: formData.unit,
                imageUrl: formData.imageUrl,
                categoryId,
                brandId: parseInt(formData.brandId),
                manufacturerId: parseInt(formData.manufacturerId),
                isPrescriptionRequired: formData.isPrescriptionRequired,
                status: formData.status,
            };
            console.log("Payload sent:", payload);
            const token = user.token.startsWith("Bearer ") ? user.token : `Bearer ${user.token}`;
            if (isEditMode) {
                const response = await api.put(`/api/products/${currentProduct.id}`, payload, {
                    headers: { Authorization: token },
                });
                setProducts(
                    products.map((p) => (p.id === currentProduct.id ? response.data : p))
                );
            } else {
                const response = await api.post("/api/products", payload, {
                    headers: { Authorization: token },
                });
                setProducts([...products, response.data]);
            }
            closeModal();
        } catch (err) {
            console.error("Submit error:", {
                message: err.message,
                response: err.response?.data,
                status: err.response?.status,
                headers: err.response?.headers,
            });
            setError(
                err.response?.status === 403
                    ? `Bạn không có quyền thực hiện hành động này. Tài khoản hiện tại: email=${user?.email}, isAdmin=${user?.isAdmin}, isStaff=${user?.isStaff}. Vui lòng kiểm tra tài khoản hoặc token.`
                    : err.response?.data?.message || "Có lỗi xảy ra khi lưu sản phẩm. Vui lòng thử lại."
            );
        } finally {
            setLoading(false);
        }
    };

    // Xử lý xóa sản phẩm
    const handleDelete = async (productId) => {
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để thực hiện hành động này!");
            return;
        }
        if (window.confirm("Bạn có chắc chắn muốn xóa sản phẩm này?")) {
            setLoading(true);
            try {
                const token = user.token.startsWith("Bearer ") ? user.token : `Bearer ${user.token}`;
                await api.delete(`/api/products/${productId}`, {
                    headers: { Authorization: token },
                });
                setProducts(products.filter((p) => p.id !== productId));
            } catch (err) {
                console.error("Delete error:", err);
                setError(
                    err.response?.status === 403
                        ? `Bạn không có quyền xóa sản phẩm. Tài khoản hiện tại: email=${user?.email}, isAdmin=${user?.isAdmin}, isStaff=${user?.isStaff}. Vui lòng kiểm tra tài khoản hoặc token.`
                        : err.response?.data?.message || "Có lỗi xảy ra khi xóa sản phẩm. Vui lòng thử lại."
                );
            } finally {
                setLoading(false);
            }
        }
    };

    return (
        <div>
            <h2 className="text-2xl font-bold mb-6">Quản lý Sản phẩm</h2>

            {error && (
                <div className="alert alert-error mb-4">
                    <div className="flex-1">
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
                        <label>{error}</label>
                    </div>
                </div>
            )}

            <div className="mb-4 flex flex-col md:flex-row gap-4">
                <input
                    type="text"
                    placeholder="Tìm kiếm sản phẩm..."
                    className="input input-bordered w-full md:w-1/3"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
                <select
                    className="select select-bordered w-full md:w-1/4"
                    value={filterCategory}
                    onChange={(e) => setFilterCategory(e.target.value)}
                >
                    <option value="">Tất cả danh mục</option>
                    {categories.map((cat) => (
                        <option key={cat.id} value={cat.name}>
                            {cat.name}
                        </option>
                    ))}
                </select>
                <select
                    className="select select-bordered w-full md:w-1/4"
                    value={filterStatus}
                    onChange={(e) => setFilterStatus(e.target.value)}
                >
                    <option value="">Tất cả trạng thái</option>
                    <option value="ACTIVE">Hoạt động</option>
                    <option value="DISCONTINUED">Ngừng kinh doanh</option>
                </select>
                <button
                    className="btn btn-primary"
                    onClick={openAddModal}
                    disabled={loading}
                >
                    <FaPlus className="mr-2" /> Thêm Sản phẩm
                </button>
            </div>

            <div className="overflow-x-auto">
                {loading ? (
                    <div className="flex justify-center">
                        <span className="loading loading-spinner loading-lg"></span>
                    </div>
                ) : (
                    <table className="table w-full">
                        <thead>
                            <tr>
                                <th>Tên</th>
                                <th>SKU</th>
                                <th>Danh mục</th>
                                <th>Thương hiệu</th>
                                <th>Yêu cầu đơn thuốc</th>
                                <th>Trạng thái</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredProducts.length === 0 ? (
                                <tr>
                                    <td colSpan="7" className="text-center">
                                        Không tìm thấy sản phẩm.
                                    </td>
                                </tr>
                            ) : (
                                filteredProducts.map((product) => (
                                    <tr key={product.id}>
                                        <td>{product.name}</td>
                                        <td>{product.sku || "N/A"}</td>
                                        <td>{product.category?.name || "Không xác định"}</td>
                                        <td>{product.brand?.name || "N/A"}</td>
                                        <td>
                                            {product.isPrescriptionRequired ? (
                                                <span className="badge badge-error">Có</span>
                                            ) : (
                                                <span className="badge badge-success">Không</span>
                                            )}
                                        </td>
                                        <td>
                                            <span
                                                className={`badge ${product.status === "ACTIVE"
                                                    ? "badge-success"
                                                    : "badge-error"
                                                    }`}
                                            >
                                                {product.status === "ACTIVE"
                                                    ? "Hoạt động"
                                                    : "Ngừng kinh doanh"}
                                            </span>
                                        </td>
                                        <td>
                                            <button
                                                className="btn btn-ghost btn-sm mr-2"
                                                onClick={() => openEditModal(product)}
                                                disabled={loading}
                                            >
                                                <FaEdit />
                                            </button>
                                            <button
                                                className="btn btn-ghost btn-sm"
                                                onClick={() => handleDelete(product.id)}
                                                disabled={loading}
                                            >
                                                <FaTrash />
                                            </button>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                )}
            </div>

            {/* Form dùng chung cho cả thêm và chỉnh sửa sản phẩm */}
            <Transition appear show={isModalOpen} as={Fragment}>
                <Dialog as="div" className="relative z-10" onClose={closeModal}>
                    <Transition.Child
                        as={Fragment}
                        enter="ease-out duration-300"
                        enterFrom="opacity-0"
                        enterTo="opacity-100"
                        leave="ease-in duration-200"
                        leaveFrom="opacity-100"
                        leaveTo="opacity-0"
                    >
                        <div className="fixed inset-0 bg-black bg-opacity-25" />
                    </Transition.Child>

                    <div className="fixed inset-0 overflow-y-auto">
                        <div className="flex min-h-full items-center justify-center p-4">
                            <Transition.Child
                                as={Fragment}
                                enter="ease-out duration-300"
                                enterFrom="opacity-0 scale-95"
                                enterTo="opacity-100 scale-100"
                                leave="ease-in duration-200"
                                leaveFrom="opacity-100 scale-100"
                                leaveTo="opacity-0 scale-95"
                            >
                                <Dialog.Panel className="w-full max-w-md transform overflow-hidden rounded-2xl bg-base-100 p-6 text-left align-middle shadow-xl transition-all">
                                    <Dialog.Title as="h3" className="text-lg font-medium leading-6">
                                        {isEditMode ? "Sửa Sản phẩm" : "Thêm Sản phẩm"}
                                    </Dialog.Title>
                                    <form onSubmit={handleSubmit} className="mt-4">
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Tên sản phẩm
                                            </label>
                                            <input
                                                type="text"
                                                className="input input-bordered w-full mt-1"
                                                value={formData.name}
                                                onChange={(e) =>
                                                    setFormData({ ...formData, name: e.target.value })
                                                }
                                                required
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">SKU</label>
                                            <input
                                                type="text"
                                                className="input input-bordered w-full mt-1"
                                                value={formData.sku}
                                                onChange={(e) =>
                                                    setFormData({ ...formData, sku: e.target.value })
                                                }
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Mô tả</label>
                                            <textarea
                                                className="textarea textarea-bordered w-full mt-1"
                                                value={formData.description}
                                                onChange={(e) =>
                                                    setFormData({ ...formData, description: e.target.value })
                                                }
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Thành phần</label>
                                            <input
                                                type="text"
                                                className="input input-bordered w-full mt-1"
                                                value={formData.ingredients}
                                                onChange={(e) =>
                                                    setFormData({ ...formData, ingredients: e.target.value })
                                                }
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Hướng dẫn sử dụng</label>
                                            <textarea
                                                className="textarea textarea-bordered w-full mt-1"
                                                value={formData.usageInstructions}
                                                onChange={(e) =>
                                                    setFormData({ ...formData, usageInstructions: e.target.value })
                                                }
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Chống chỉ định</label>
                                            <textarea
                                                className="textarea textarea-bordered w-full mt-1"
                                                value={formData.contraindications}
                                                onChange={(e) =>
                                                    setFormData({ ...formData, contraindications: e.target.value })
                                                }
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Tác dụng phụ</label>
                                            <textarea
                                                className="textarea textarea-bordered w-full mt-1"
                                                value={formData.sideEffects}
                                                onChange={(e) =>
                                                    setFormData({ ...formData, sideEffects: e.target.value })
                                                }
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Điều kiện bảo quản</label>
                                            <input
                                                type="text"
                                                className="input input-bordered w-full mt-1"
                                                value={formData.storageConditions}
                                                onChange={(e) =>
                                                    setFormData({ ...formData, storageConditions: e.target.value })
                                                }
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Đóng gói</label>
                                            <input
                                                type="text"
                                                className="input input-bordered w-full mt-1"
                                                value={formData.packaging}
                                                onChange={(e) =>
                                                    setFormData({ ...formData, packaging: e.target.value })
                                                }
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Đơn vị</label>
                                            <input
                                                type="text"
                                                className="input input-bordered w-full mt-1"
                                                value={formData.unit}
                                                onChange={(e) =>
                                                    setFormData({ ...formData, unit: e.target.value })
                                                }
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">URL hình ảnh</label>
                                            <input
                                                type="text"
                                                className="input input-bordered w-full mt-1"
                                                value={formData.imageUrl}
                                                onChange={(e) =>
                                                    setFormData({ ...formData, imageUrl: e.target.value })
                                                }
                                                disabled={loading}
                                            />
                                        </div>
                                        {/* Dropdown danh mục, hiển thị tất cả danh mục từ API */}
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Danh mục</label>
                                            <select
                                                className="select select-bordered w-full mt-1"
                                                value={formData.category}
                                                onChange={(e) =>
                                                    setFormData({ ...formData, category: e.target.value })
                                                }
                                                required
                                                disabled={loading}
                                            >
                                                <option value="">Chọn danh mục</option>
                                                {categories.map((cat) => (
                                                    <option key={cat.id} value={cat.name}>
                                                        {cat.name}
                                                    </option>
                                                ))}
                                            </select>
                                        </div>
                                        {/* Dropdown thương hiệu, hiển thị tất cả thương hiệu từ API */}
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Thương hiệu</label>
                                            <select
                                                className="select select-bordered w-full mt-1"
                                                value={formData.brandId}
                                                onChange={(e) =>
                                                    setFormData({ ...formData, brandId: e.target.value })
                                                }
                                                required
                                                disabled={loading}
                                            >
                                                <option value="">Chọn thương hiệu</option>
                                                {brands.map((brand) => (
                                                    <option key={brand.id} value={brand.id}>
                                                        {brand.name}
                                                    </option>
                                                ))}
                                            </select>
                                        </div>
                                        {/* Dropdown nhà sản xuất, hiển thị tất cả nhà sản xuất từ API */}
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Nhà sản xuất</label>
                                            <select
                                                className="select select-bordered w-full mt-1"
                                                value={formData.manufacturerId}
                                                onChange={(e) =>
                                                    setFormData({ ...formData, manufacturerId: e.target.value })
                                                }
                                                required
                                                disabled={loading}
                                            >
                                                <option value="">Chọn nhà sản xuất</option>
                                                {manufacturers.map((manufacturer) => (
                                                    <option key={manufacturer.id} value={manufacturer.id}>
                                                        {manufacturer.name}
                                                    </option>
                                                ))}
                                            </select>
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Yêu cầu đơn thuốc</label>
                                            <input
                                                type="checkbox"
                                                className="toggle mt-1"
                                                checked={formData.isPrescriptionRequired}
                                                onChange={(e) =>
                                                    setFormData({
                                                        ...formData,
                                                        isPrescriptionRequired: e.target.checked,
                                                    })
                                                }
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Trạng thái</label>
                                            <select
                                                className="select select-bordered w-full mt-1"
                                                value={formData.status}
                                                onChange={(e) =>
                                                    setFormData({ ...formData, status: e.target.value })
                                                }
                                                disabled={loading}
                                            >
                                                <option value="ACTIVE">Hoạt động</option>
                                                <option value="DISCONTINUED">Ngừng kinh doanh</option>
                                            </select>
                                        </div>
                                        <div className="mt-4">
                                            <button
                                                type="submit"
                                                className="btn btn-primary w-full"
                                                disabled={loading}
                                            >
                                                {isEditMode ? "Cập nhật" : "Thêm"}
                                            </button>
                                        </div>
                                    </form>
                                </Dialog.Panel>
                            </Transition.Child>
                        </div>
                    </div>
                </Dialog>
            </Transition>
        </div>
    );
};

export default ProductList;