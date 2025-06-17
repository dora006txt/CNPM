import React, { useState, useEffect } from "react";
import { FaEdit } from "react-icons/fa";
import { Dialog, Transition } from "@headlessui/react";
import { Fragment } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { API_ENDPOINTS } from "../../config/apiConfig";

const InventoryList = () => {
    const { user } = useAuth();
    const navigate = useNavigate();
    const [inventory, setInventory] = useState([]);
    const [branches, setBranches] = useState([]);
    const [products, setProducts] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [filterBranch, setFilterBranch] = useState("");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [currentInventory, setCurrentInventory] = useState(null);
    const [formData, setFormData] = useState({ quantityOnHand: 0 });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // Kiểm tra quyền: Chỉ Admin (role_id = 2) được phép chỉnh sửa
    const isAdmin = user?.roleId === 2;

    const fetchInventory = async () => {
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để truy cập!");
            navigate("/login");
            return;
        }
        setLoading(true);
        setError(null);
        try {
            const response = await axios.get(API_ENDPOINTS.INVENTORY, {
                headers: { Authorization: `Bearer ${user.token}` },
            });
            console.log("Inventory data:", response.data);
            setInventory(response.data || []);
        } catch (err) {
            setError(
                err.response?.data?.message || "Không thể tải danh sách tồn kho. Vui lòng thử lại."
            );
            console.error("Fetch inventory error:", err);
        } finally {
            setLoading(false);
        }
    };

    const fetchBranches = async () => {
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để truy cập!");
            navigate("/login");
            return;
        }
        try {
            const response = await axios.get(API_ENDPOINTS.BRANCHES, {
                headers: { Authorization: `Bearer ${user.token}` },
            });
            console.log("Branches data:", response.data);
            setBranches(response.data || []);
        } catch (err) {
            setError(
                err.response?.data?.message || "Không thể tải danh sách chi nhánh. Vui lòng thử lại."
            );
            console.error("Fetch branches error:", err);
        }
    };

    const fetchProducts = async () => {
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để truy cập!");
            navigate("/login");
            return;
        }
        try {
            const response = await axios.get(API_ENDPOINTS.PRODUCTS, {
                headers: { Authorization: `Bearer ${user.token}` },
            });
            console.log("Products data:", response.data);
            setProducts(response.data || []);
        } catch (err) {
            setError(
                err.response?.data?.message ||
                (err.response?.status === 403
                    ? "Truy cập bị từ chối (403). Vui lòng kiểm tra token hoặc quyền truy cập sản phẩm."
                    : "Không thể tải danh sách sản phẩm. Vui lòng thử lại.")
            );
            console.error("Fetch products error:", err);
        }
    };

    useEffect(() => {
        fetchInventory();
        fetchBranches();
        fetchProducts();
    }, [user, navigate]);

    const getBranchName = (branchId) => {
        const branch = branches.find((b) => b.branchId === branchId);
        console.log("Branch found for branchId", branchId, ":", branch);
        return branch ? (branch.name || "Không xác định") : "Không xác định";
    };

    const getProductName = (productId) => {
        const product = products.find((p) => p.id === productId || p.product_id === productId);
        console.log("Product found for productId", productId, ":", product);
        return product ? (product.product_name || product.name || "Không xác định") : "Không xác định";
    };

    const filteredInventory = inventory.filter((item) => {
        if (!item || !item.productId || !item.branchId) return false;
        const productName = getProductName(item.productId);
        const matchesSearch = productName.toLowerCase().includes(searchTerm.toLowerCase());
        const matchesBranch = filterBranch ? item.branchId === parseInt(filterBranch) : true;
        return matchesSearch && matchesBranch;
    });

    const openEditModal = (item) => {
        if (!isAdmin) return; // Chỉ Admin được phép chỉnh sửa
        if (!item || !item.productId || !item.branchId) {
            setError("Dữ liệu sản phẩm không hợp lệ.");
            return;
        }
        setCurrentInventory(item);
        setFormData({ quantityOnHand: item.quantityOnHand || 0 });
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setCurrentInventory(null);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!isAdmin) {
            setError("Bạn không có quyền thực hiện hành động này!");
            return;
        }
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để thực hiện hành động này!");
            navigate("/login");
            return;
        }
        if (!currentInventory) {
            setError("Không có bản ghi nào được chọn để cập nhật.");
            return;
        }
        setLoading(true);
        try {
            const updatedInventory = {
                branchId: currentInventory.branchId,
                productId: currentInventory.productId,
                quantityOnHand: parseInt(formData.quantityOnHand),
                price: currentInventory.price,
                discountPrice: currentInventory.discountPrice,
                expiryDate: currentInventory.expiryDate,
                batchNumber: currentInventory.batchNumber,
                locationInStore: currentInventory.locationInStore,
            };
            const response = await axios.put(
                `${API_ENDPOINTS.INVENTORY}/${currentInventory.inventoryId}`,
                updatedInventory,
                { headers: { Authorization: `Bearer ${user.token}` } }
            );
            setInventory(
                inventory.map((item) =>
                    item.inventoryId === currentInventory.inventoryId
                        ? { ...item, quantityOnHand: response.data.quantityOnHand, lastUpdated: response.data.lastUpdated }
                        : item
                )
            );
            closeModal();
        } catch (err) {
            setError(
                err.response?.data?.message || "Có lỗi xảy ra khi cập nhật tồn kho. Vui lòng thử lại."
            );
            console.error("Update inventory error:", err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <h2 className="text-2xl font-bold mb-6">Quản lý Kho</h2>

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
                    value={filterBranch}
                    onChange={(e) => setFilterBranch(e.target.value)}
                >
                    <option value="">Tất cả chi nhánh</option>
                    {branches.map((branch) => (
                        <option key={branch.branchId} value={branch.branchId}>
                            {branch.name || "Không xác định"}
                        </option>
                    ))}
                </select>
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
                                <th>Sản phẩm</th>
                                <th>Chi nhánh</th>
                                <th>Số lượng tồn</th>
                                <th>Giá bán</th>
                                <th>Giá giảm</th>
                                <th>Hạn sử dụng</th>
                                <th>Số lô</th>
                                <th>Vị trí</th>
                                <th>Ngày cập nhật</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredInventory.length === 0 ? (
                                <tr>
                                    <td colSpan="10" className="text-center">
                                        Không tìm thấy sản phẩm trong kho.
                                    </td>
                                </tr>
                            ) : (
                                filteredInventory.map((item) => (
                                    <tr key={item.inventoryId}>
                                        <td>{getProductName(item.productId)}</td>
                                        <td>{getBranchName(item.branchId)}</td>
                                        <td>{item.quantityOnHand}</td>
                                        <td>{item.price.toLocaleString()} VNĐ</td>
                                        <td>
                                            {item.discountPrice
                                                ? `${item.discountPrice.toLocaleString()} VNĐ`
                                                : "N/A"}
                                        </td>
                                        <td>{item.expiryDate}</td>
                                        <td>{item.batchNumber}</td>
                                        <td>{item.locationInStore}</td>
                                        <td>{new Date(item.lastUpdated).toLocaleDateString()}</td>
                                        <td>
                                            {isAdmin && (
                                                <button
                                                    className="btn btn-ghost btn-sm"
                                                    onClick={() => openEditModal(item)}
                                                    disabled={loading}
                                                >
                                                    <FaEdit />
                                                </button>
                                            )}
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                )}
            </div>

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
                                        Cập nhật Số lượng Tồn kho
                                    </Dialog.Title>
                                    <form onSubmit={handleSubmit} className="mt-4">
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Sản phẩm: {currentInventory && getProductName(currentInventory.productId)}
                                            </label>
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Chi nhánh: {currentInventory && getBranchName(currentInventory.branchId)}
                                            </label>
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Số lượng tồn
                                            </label>
                                            <input
                                                type="number"
                                                className="input input-bordered w-full mt-1"
                                                value={formData.quantityOnHand}
                                                onChange={(e) =>
                                                    setFormData({
                                                        ...formData,
                                                        quantityOnHand: parseInt(e.target.value),
                                                    })
                                                }
                                                required
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mt-4">
                                            <button
                                                type="submit"
                                                className="btn btn-primary w-full"
                                                disabled={loading}
                                            >
                                                Cập nhật
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

export default InventoryList;