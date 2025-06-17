import React, { useState, useEffect } from "react";
import { FaEdit, FaTrash, FaPlus } from "react-icons/fa";
import { Dialog, Transition } from "@headlessui/react";
import { Fragment } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { API_ENDPOINTS } from "../../config/apiConfig";

const initialFormData = {
    code: "",
    name: "",
    description: "",
    discountType: "PERCENTAGE",
    discountValue: 0,
    startDate: "",
    endDate: "",
    minOrderValue: 0,
    usageLimitPerCustomer: 1,
    totalUsageLimit: 100,
    applicableScope: "ALL",
    isActive: true,
    categoryIds: [],
    productIds: [],
    branchIds: [],
};

const PromotionList = () => {
    const { user } = useAuth();
    const navigate = useNavigate();
    const [promotions, setPromotions] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [filterStatus, setFilterStatus] = useState("");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const [currentPromotion, setCurrentPromotion] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [formData, setFormData] = useState(initialFormData);

    useEffect(() => {
        const fetchPromotions = async () => {
            if (!user || !user.token) {
                setError("Vui lòng đăng nhập để truy cập!");
                navigate("/login");
                return;
            }
            setLoading(true);
            try {
                const config = {
                    headers: { Authorization: `Bearer ${user.token}` },
                };
                console.log("Fetching promotions from:", API_ENDPOINTS.PROMOTIONS);
                console.log("Axios config:", config);
                const response = await axios.get(API_ENDPOINTS.PROMOTIONS, config);
                console.log("Promotions response:", {
                    status: response.status,
                    headers: response.headers,
                    data: response.data,
                });
                if (Array.isArray(response.data)) {
                    setPromotions(response.data);
                } else {
                    throw new Error("Dữ liệu trả về không phải là mảng hợp lệ. Kiểu: " + typeof response.data);
                }
            } catch (err) {
                const errorMsg = err.response?.data?.message ||
                    (err.response?.status === 401
                        ? "Token không hợp lệ hoặc bạn không có quyền truy cập."
                        : err.response?.status === 404
                            ? "Endpoint không tồn tại. Vui lòng kiểm tra cấu hình API."
                            : "Không thể tải dữ liệu khuyến mãi. Vui lòng thử lại. Chi tiết: " + (err.response?.status || "Không xác định"));
                setError(errorMsg);
                console.error("Fetch promotions error:", {
                    status: err.response?.status,
                    data: err.response?.data,
                    message: err.message,
                });
                setPromotions([]); // Không dùng staticPromotions, chỉ để rỗng
            } finally {
                setLoading(false);
            }
        };

        fetchPromotions();
    }, [user, navigate]);

    const filteredPromotions = Array.isArray(promotions)
        ? promotions.filter((promotion) => {
            const matchesSearch = promotion.code
                .toLowerCase()
                .includes(searchTerm.toLowerCase());
            const matchesStatus = filterStatus
                ? (filterStatus === "ACTIVE" ? promotion.isActive : !promotion.isActive)
                : true;
            return matchesSearch && matchesStatus;
        })
        : [];

    console.log("Filtered Promotions:", filteredPromotions);

    const openAddModal = () => {
        setIsEditMode(false);
        setFormData(initialFormData);
        setIsModalOpen(true);
    };

    const openEditModal = (promotion) => {
        setIsEditMode(true);
        setCurrentPromotion(promotion);
        setFormData({
            code: promotion.code,
            name: promotion.name,
            description: promotion.description || "",
            discountType: promotion.discountType,
            discountValue: promotion.discountValue,
            startDate: promotion.startDate.split("T")[0],
            endDate: promotion.endDate.split("T")[0],
            minOrderValue: promotion.minOrderValue,
            usageLimitPerCustomer: promotion.usageLimitPerCustomer,
            totalUsageLimit: promotion.totalUsageLimit,
            applicableScope: promotion.applicableScope,
            isActive: promotion.isActive,
            categoryIds: promotion.categories.map(c => c.id) || [],
            productIds: promotion.products.map(p => p.id) || [],
            branchIds: promotion.branches.map(b => b.id) || [],
        });
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setCurrentPromotion(null);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để thực hiện hành động này!");
            navigate("/login");
            return;
        }
        setLoading(true);
        try {
            const payload = {
                ...formData,
                startDate: new Date(formData.startDate).toISOString(),
                endDate: new Date(formData.endDate).toISOString(),
            };
            const headers = { Authorization: `Bearer ${user.token}` };

            if (isEditMode) {
                const response = await axios.put(
                    `${API_ENDPOINTS.PROMOTIONS}/${currentPromotion.promotionId}`,
                    payload,
                    { headers }
                );
                setPromotions(
                    promotions.map((p) =>
                        p.promotionId === currentPromotion.promotionId ? response.data : p
                    )
                );
            } else {
                const response = await axios.post(API_ENDPOINTS.PROMOTIONS, payload, {
                    headers,
                });
                setPromotions([...promotions, response.data]);
            }
            closeModal();
        } catch (err) {
            setError(
                err.response?.data?.message || "Có lỗi xảy ra khi lưu khuyến mãi. Vui lòng thử lại."
            );
            console.error("Submit promotion error:", err);
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (promotionId) => {
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để thực hiện hành động này!");
            navigate("/login");
            return;
        }
        if (window.confirm("Bạn có chắc chắn muốn xóa khuyến mãi này?")) {
            setLoading(true);
            try {
                await axios.delete(`${API_ENDPOINTS.PROMOTIONS}/${promotionId}`, {
                    headers: { Authorization: `Bearer ${user.token}` },
                });
                setPromotions(promotions.filter((p) => p.promotionId !== promotionId));
            } catch (err) {
                setError(
                    err.response?.data?.message || "Có lỗi xảy ra khi xóa khuyến mãi. Vui lòng thử lại."
                );
                console.error("Delete promotion error:", err);
            } finally {
                setLoading(false);
            }
        }
    };

    return (
        <div>
            <h2 className="text-2xl font-bold mb-6">Quản lý Khuyến mãi</h2>

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
                    placeholder="Tìm kiếm mã khuyến mãi..."
                    className="input input-bordered w-full md:w-1/3"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
                <select
                    className="select select-bordered w-full md:w-1/4"
                    value={filterStatus}
                    onChange={(e) => setFilterStatus(e.target.value)}
                >
                    <option value="">Tất cả trạng thái</option>
                    <option value="ACTIVE">Hoạt động</option>
                    <option value="INACTIVE">Không hoạt động</option>
                </select>
                <button
                    className="btn btn-primary"
                    onClick={openAddModal}
                    disabled={loading}
                >
                    <FaPlus className="mr-2" /> Thêm Khuyến mãi
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
                                <th>Mã Khuyến mãi</th>
                                <th>Tên</th>
                                <th>Giá trị giảm</th>
                                <th>Loại giảm giá</th>
                                <th>Ngày bắt đầu</th>
                                <th>Ngày kết thúc</th>
                                <th>Trạng thái</th>
                                <th>Mô tả</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredPromotions.length === 0 ? (
                                <tr>
                                    <td colSpan="9" className="text-center">
                                        Không có dữ liệu.
                                    </td>
                                </tr>
                            ) : (
                                filteredPromotions.map((promotion) => (
                                    <tr key={promotion.promotionId}>
                                        <td>{promotion.code}</td>
                                        <td>{promotion.name}</td>
                                        <td>
                                            {promotion.discountType === "PERCENTAGE"
                                                ? `${promotion.discountValue}%`
                                                : promotion.discountType === "FREE_SHIPPING"
                                                    ? "Miễn phí vận chuyển"
                                                    : promotion.discountValue}
                                        </td>
                                        <td>
                                            {promotion.discountType === "PERCENTAGE"
                                                ? "Phần trăm"
                                                : promotion.discountType === "FREE_SHIPPING"
                                                    ? "Miễn phí vận chuyển"
                                                    : "Số tiền cố định"}
                                        </td>
                                        <td>{new Date(promotion.startDate).toLocaleDateString()}</td>
                                        <td>{new Date(promotion.endDate).toLocaleDateString()}</td>
                                        <td>
                                            <span
                                                className={`badge ${promotion.isActive ? "badge-success" : "badge-warning"
                                                    }`}
                                            >
                                                {promotion.isActive ? "Hoạt động" : "Không hoạt động"}
                                            </span>
                                        </td>
                                        <td>{promotion.description || "N/A"}</td>
                                        <td>
                                            <button
                                                className="btn btn-ghost btn-sm mr-2"
                                                onClick={() => openEditModal(promotion)}
                                                disabled={loading}
                                            >
                                                <FaEdit />
                                            </button>
                                            <button
                                                className="btn btn-ghost btn-sm"
                                                onClick={() => handleDelete(promotion.promotionId)}
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
                                        {isEditMode ? "Sửa Khuyến mãi" : "Thêm Khuyến mãi"}
                                    </Dialog.Title>
                                    <form onSubmit={handleSubmit} className="mt-4">
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Mã khuyến mãi</label>
                                            <input
                                                type="text"
                                                className="input input-bordered w-full mt-1"
                                                value={formData.code}
                                                onChange={(e) =>
                                                    setFormData({ ...formData, code: e.target.value })
                                                }
                                                required
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Tên khuyến mãi</label>
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
                                            <label className="block text-sm font-medium">Giá trị giảm</label>
                                            <input
                                                type="number"
                                                className="input input-bordered w-full mt-1"
                                                value={formData.discountValue}
                                                onChange={(e) =>
                                                    setFormData({
                                                        ...formData,
                                                        discountValue: parseFloat(e.target.value),
                                                    })
                                                }
                                                required
                                                disabled={loading || formData.discountType === "FREE_SHIPPING"}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Loại giảm giá</label>
                                            <select
                                                className="select select-bordered w-full mt-1"
                                                value={formData.discountType}
                                                onChange={(e) =>
                                                    setFormData({
                                                        ...formData,
                                                        discountType: e.target.value,
                                                        discountValue:
                                                            e.target.value === "FREE_SHIPPING" ? 0 : formData.discountValue,
                                                    })
                                                }
                                                disabled={loading}
                                            >
                                                <option value="PERCENTAGE">Phần trăm</option>
                                                <option value="FIXED_AMOUNT">Số tiền cố định</option>
                                                <option value="FREE_SHIPPING">Miễn phí vận chuyển</option>
                                            </select>
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Ngày bắt đầu</label>
                                            <input
                                                type="date"
                                                className="input input-bordered w-full mt-1"
                                                value={formData.startDate}
                                                onChange={(e) =>
                                                    setFormData({ ...formData, startDate: e.target.value })
                                                }
                                                required
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Ngày kết thúc</label>
                                            <input
                                                type="date"
                                                className="input input-bordered w-full mt-1"
                                                value={formData.endDate}
                                                onChange={(e) =>
                                                    setFormData({ ...formData, endDate: e.target.value })
                                                }
                                                required
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Giá trị đơn tối thiểu</label>
                                            <input
                                                type="number"
                                                className="input input-bordered w-full mt-1"
                                                value={formData.minOrderValue}
                                                onChange={(e) =>
                                                    setFormData({
                                                        ...formData,
                                                        minOrderValue: parseFloat(e.target.value),
                                                    })
                                                }
                                                required
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Phạm vi áp dụng</label>
                                            <select
                                                className="select select-bordered w-full mt-1"
                                                value={formData.applicableScope}
                                                onChange={(e) =>
                                                    setFormData({ ...formData, applicableScope: e.target.value })
                                                }
                                                disabled={loading}
                                            >
                                                <option value="ALL">Tất cả</option>
                                                <option value="SPECIFIC_CATEGORIES">Danh mục cụ thể</option>
                                            </select>
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Trạng thái</label>
                                            <select
                                                className="select select-bordered w-full mt-1"
                                                value={formData.isActive ? "ACTIVE" : "INACTIVE"}
                                                onChange={(e) =>
                                                    setFormData({
                                                        ...formData,
                                                        isActive: e.target.value === "ACTIVE",
                                                    })
                                                }
                                                disabled={loading}
                                            >
                                                <option value="ACTIVE">Hoạt động</option>
                                                <option value="INACTIVE">Không hoạt động</option>
                                            </select>
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Mô tả</label>
                                            <textarea
                                                className="textarea textarea-bordered w-full mt-1"
                                                value={formData.description}
                                                onChange={(e) =>
                                                    setFormData({
                                                        ...formData,
                                                        description: e.target.value,
                                                    })
                                                }
                                                disabled={loading}
                                            />
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

export default PromotionList;