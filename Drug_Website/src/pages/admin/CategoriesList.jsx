import React, { useState, useEffect } from "react";
import { FaEdit, FaTrash, FaPlus } from "react-icons/fa";
import { Dialog, Transition } from "@headlessui/react";
import { Fragment } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { API_ENDPOINTS } from "../../config/apiConfig";

const CategoriesList = () => {
    const { user } = useAuth();
    const navigate = useNavigate();
    const [categories, setCategories] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [filterStatus, setFilterStatus] = useState("");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const [currentCategory, setCurrentCategory] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const initialFormData = {
        name: "",
        description: "",
        status: "ACTIVE",
    };

    const [formData, setFormData] = useState(initialFormData);

    // Lấy danh sách danh mục từ API
    const fetchCategories = async () => {
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để truy cập!");
            setLoading(false);
            navigate("/login");
            return;
        }
        setLoading(true);
        setError("");
        try {
            const response = await axios.get(API_ENDPOINTS.CATEGORIES, {
                headers: { Authorization: `Bearer ${user.token}` },
            });
            setCategories(response.data || []);
        } catch (err) {
            setError(
                err.response?.data?.message || "Không thể tải danh sách danh mục. Vui lòng thử lại."
            );
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCategories();
    }, [user, navigate]);

    // Lọc danh mục
    const filteredCategories = categories.filter((category) => {
        const matchesSearch = category.name
            .toLowerCase()
            .includes(searchTerm.toLowerCase());
        const categoryStatus = category.status ? category.status.toLowerCase() : "inactive"; // Thêm kiểm tra status
        const matchesStatus = filterStatus
            ? categoryStatus === filterStatus.toLowerCase()
            : true;
        return matchesSearch && matchesStatus;
    });

    // Mở modal thêm
    const openAddModal = () => {
        setIsEditMode(false);
        setFormData(initialFormData);
        setIsModalOpen(true);
    };

    // Mở modal sửa
    const openEditModal = (category) => {
        setIsEditMode(true);
        setCurrentCategory(category);
        setFormData({
            name: category.name,
            description: category.description || "",
            status: category.status || "ACTIVE",
        });
        setIsModalOpen(true);
    };

    // Đóng modal
    const closeModal = () => {
        setIsModalOpen(false);
        setCurrentCategory(null);
    };

    // Xử lý submit (thêm/sửa)
    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để thực hiện hành động này!");
            navigate("/login");
            return;
        }
        setLoading(true);
        try {
            const payload = formData;
            if (isEditMode) {
                const response = await axios.put(
                    `${API_ENDPOINTS.CATEGORIES}/${currentCategory.id}`,
                    payload,
                    { headers: { Authorization: `Bearer ${user.token}` } }
                );
                setCategories(
                    categories.map((c) => (c.id === currentCategory.id ? response.data : c))
                );
            } else {
                const response = await axios.post(API_ENDPOINTS.CATEGORIES, payload, {
                    headers: { Authorization: `Bearer ${user.token}` },
                });
                setCategories([...categories, response.data]);
            }
            closeModal();
        } catch (err) {
            setError(
                err.response?.data?.message || "Có lỗi xảy ra khi lưu danh mục. Vui lòng thử lại."
            );
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    // Xóa danh mục
    const handleDelete = async (categoryId) => {
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để thực hiện hành động này!");
            navigate("/login");
            return;
        }
        if (window.confirm("Bạn có chắc chắn muốn xóa danh mục này?")) {
            setLoading(true);
            try {
                await axios.delete(`${API_ENDPOINTS.CATEGORIES}/${categoryId}`, {
                    headers: { Authorization: `Bearer ${user.token}` },
                });
                setCategories(categories.filter((c) => c.id !== categoryId));
            } catch (err) {
                setError(
                    err.response?.data?.message || "Có lỗi xảy ra khi xóa danh mục. Vui lòng thử lại."
                );
                console.error(err);
            } finally {
                setLoading(false);
            }
        }
    };

    return (
        <div>
            <h2 className="text-2xl font-bold mb-6">Quản lý Danh Mục</h2>

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
                    placeholder="Tìm kiếm danh mục..."
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
                    <option value="active">Hoạt động</option>
                    <option value="inactive">Ngừng hoạt động</option>
                </select>
                <button
                    className="btn btn-primary"
                    onClick={openAddModal}
                    disabled={loading}
                >
                    <FaPlus className="mr-2" /> Thêm Danh Mục
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
                                <th>Mô tả</th>
                                <th>Trạng thái</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredCategories.length === 0 ? (
                                <tr>
                                    <td colSpan="4" className="text-center">
                                        Không tìm thấy danh mục.
                                    </td>
                                </tr>
                            ) : (
                                filteredCategories.map((category) => (
                                    <tr key={category.id}>
                                        <td>{category.name}</td>
                                        <td>{category.description || "Không có mô tả"}</td>
                                        <td>
                                            <span
                                                className={`badge ${(category.status ? category.status.toLowerCase() : "inactive") === "active"
                                                    ? "badge-success"
                                                    : "badge-error"
                                                    }`}
                                            >
                                                {(category.status ? category.status.toLowerCase() : "inactive") === "active" ? "Hoạt động" : "Ngừng hoạt động"}
                                            </span>
                                        </td>
                                        <td>
                                            <button
                                                className="btn btn-ghost btn-sm mr-2"
                                                onClick={() => openEditModal(category)}
                                                disabled={loading}
                                            >
                                                <FaEdit />
                                            </button>
                                            <button
                                                className="btn btn-ghost btn-sm"
                                                onClick={() => handleDelete(category.id)}
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
                                        {isEditMode ? "Sửa Danh Mục" : "Thêm Danh Mục"}
                                    </Dialog.Title>
                                    <form onSubmit={handleSubmit} className="mt-4">
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Tên danh mục</label>
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
                                                <option value="INACTIVE">Ngừng hoạt động</option>
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

export default CategoriesList;