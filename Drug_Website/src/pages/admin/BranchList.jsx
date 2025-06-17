import React, { useState, useEffect } from "react";
import { FaEdit, FaTrash, FaPlus } from "react-icons/fa";
import { Dialog, Transition } from "@headlessui/react";
import { Fragment } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { API_ENDPOINTS } from "../../config/apiConfig";

const BranchList = () => {
    const { user } = useAuth();
    const navigate = useNavigate();
    const [branches, setBranches] = useState([]); // Khởi tạo là mảng rỗng
    const [searchTerm, setSearchTerm] = useState("");
    const [isBranchModalOpen, setIsBranchModalOpen] = useState(false);
    const [isEditBranchMode, setIsEditBranchMode] = useState(false);
    const [currentBranch, setCurrentBranch] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    // Khởi tạo formData cho chi nhánh
    const [branchFormData, setBranchFormData] = useState({
        name: "",
        address: "",
        phoneNumber: "",
        latitude: "",
        longitude: "",
        operatingHours: "",
        isActive: true,
    });

    // Lấy danh sách chi nhánh từ API
    const fetchBranches = async () => {
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để truy cập!");
            navigate("/login");
            return;
        }
        setLoading(true);
        setError("");
        try {
            const response = await axios.get(API_ENDPOINTS.BRANCHES, {
                headers: { Authorization: `Bearer ${user.token}` },
            });
            // Chuẩn hóa response.data thành mảng
            setBranches(Array.isArray(response.data) ? response.data : []);
        } catch (err) {
            setError(
                err.response?.data?.message || "Không thể tải danh sách chi nhánh. Vui lòng thử lại."
            );
            console.error("Fetch branches error:", err.response || err);
            setBranches([]); // Đặt lại thành mảng rỗng nếu lỗi
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchBranches();
    }, [user, navigate]);

    // Lọc chi nhánh theo tìm kiếm
    const filteredBranches = Array.isArray(branches)
        ? branches.filter((branch) =>
            branch.name?.toLowerCase().includes(searchTerm.toLowerCase())
        )
        : [];

    // Mở modal thêm/sửa chi nhánh
    const openBranchModal = (branch = null) => {
        setIsEditBranchMode(!!branch);
        setCurrentBranch(branch);
        setBranchFormData(
            branch
                ? {
                    name: branch.name || "",
                    address: branch.address || "",
                    phoneNumber: branch.phoneNumber || "",
                    latitude: branch.latitude?.toString() || "",
                    longitude: branch.longitude?.toString() || "",
                    operatingHours: branch.operatingHours || "",
                    isActive: branch.isActive ?? true,
                }
                : {
                    name: "",
                    address: "",
                    phoneNumber: "",
                    latitude: "",
                    longitude: "",
                    operatingHours: "",
                    isActive: true,
                }
        );
        setIsBranchModalOpen(true);
    };

    // Đóng modal
    const closeBranchModal = () => {
        setIsBranchModalOpen(false);
        setCurrentBranch(null);
    };

    // Xử lý submit form chi nhánh
    const handleBranchSubmit = async (e) => {
        e.preventDefault();
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để thực hiện hành động này!");
            navigate("/login");
            return;
        }
        setLoading(true);
        setError("");
        try {
            // Kiểm tra các trường bắt buộc
            if (
                !branchFormData.name ||
                !branchFormData.address ||
                !branchFormData.phoneNumber ||
                !branchFormData.latitude ||
                !branchFormData.longitude ||
                !branchFormData.operatingHours
            ) {
                setError("Vui lòng điền đầy đủ các trường bắt buộc!");
                setLoading(false);
                return;
            }

            // Tạo payload khớp với API
            const payload = {
                name: branchFormData.name,
                address: branchFormData.address,
                phoneNumber: branchFormData.phoneNumber,
                latitude: parseFloat(branchFormData.latitude),
                longitude: parseFloat(branchFormData.longitude),
                operatingHours: branchFormData.operatingHours,
                isActive: branchFormData.isActive,
            };

            console.log("Payload sent:", payload);

            const headers = { Authorization: `Bearer ${user.token}` };

            if (isEditBranchMode) {
                const response = await axios.put(
                    `${API_ENDPOINTS.BRANCHES}/${currentBranch.branchId}`,
                    payload,
                    { headers }
                );
                setBranches(
                    branches.map((b) =>
                        b.branchId === currentBranch.branchId ? response.data : b
                    )
                );
            } else {
                const response = await axios.post(API_ENDPOINTS.BRANCHES, payload, {
                    headers,
                });
                setBranches([...branches, response.data]);
            }
            closeBranchModal();
        } catch (err) {
            setError(
                err.response?.data?.message || "Có lỗi xảy ra khi lưu chi nhánh. Vui lòng thử lại."
            );
            console.error("Submit branch error:", err.response || err);
        } finally {
            setLoading(false);
        }
    };

    // Xóa chi nhánh
    const handleDeleteBranch = async (branchId) => {
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để thực hiện hành động này!");
            navigate("/login");
            return;
        }
        if (window.confirm("Bạn có chắc chắn muốn xóa chi nhánh này?")) {
            setLoading(true);
            setError("");
            try {
                await axios.delete(`${API_ENDPOINTS.BRANCHES}/${branchId}`, {
                    headers: { Authorization: `Bearer ${user.token}` },
                });
                setBranches(branches.filter((b) => b.branchId !== branchId));
            } catch (err) {
                setError(
                    err.response?.data?.message || "Có lỗi xảy ra khi xóa chi nhánh. Vui lòng thử lại."
                );
                console.error("Delete branch error:", err.response || err);
            } finally {
                setLoading(false);
            }
        }
    };

    return (
        <div>
            <h2 className="text-2xl font-bold mb-6">Quản lý Chi nhánh</h2>

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
                    placeholder="Tìm kiếm chi nhánh..."
                    className="input input-bordered w-full md:w-1/3"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
                <button
                    className="btn btn-primary"
                    onClick={() => openBranchModal()}
                    disabled={loading}
                >
                    <FaPlus className="mr-2" /> Thêm Chi nhánh
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
                                <th>Tên chi nhánh</th>
                                <th>Địa chỉ</th>
                                <th>Số điện thoại</th>
                                <th>Giờ hoạt động</th>
                                <th>Trạng thái</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredBranches.length === 0 ? (
                                <tr>
                                    <td colSpan="6" className="text-center">
                                        Không tìm thấy chi nhánh.
                                    </td>
                                </tr>
                            ) : (
                                filteredBranches.map((branch, index) => (
                                    <tr key={branch.branchId || index}>
                                        <td>{branch.name}</td>
                                        <td>{branch.address}</td>
                                        <td>{branch.phoneNumber}</td>
                                        <td>{branch.operatingHours}</td>
                                        <td>
                                            <span
                                                className={`badge ${branch.isActive
                                                    ? "badge-success"
                                                    : "badge-warning"
                                                    }`}
                                            >
                                                {branch.isActive
                                                    ? "Hoạt động"
                                                    : "Không hoạt động"}
                                            </span>
                                        </td>
                                        <td>
                                            <button
                                                className="btn btn-ghost btn-sm mr-2"
                                                onClick={() => openBranchModal(branch)}
                                                disabled={loading}
                                            >
                                                <FaEdit />
                                            </button>
                                            <button
                                                className="btn btn-ghost btn-sm"
                                                onClick={() =>
                                                    handleDeleteBranch(branch.branchId)
                                                }
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

            {/* Modal thêm/sửa chi nhánh */}
            <Transition appear show={isBranchModalOpen} as={Fragment}>
                <Dialog as="div" className="relative z-10" onClose={closeBranchModal}>
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
                                        {isEditBranchMode ? "Sửa Chi nhánh" : "Thêm Chi nhánh"}
                                    </Dialog.Title>
                                    <form onSubmit={handleBranchSubmit} className="mt-4">
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Tên chi nhánh
                                            </label>
                                            <input
                                                type="text"
                                                className="input input-bordered w-full mt-1"
                                                value={branchFormData.name}
                                                onChange={(e) =>
                                                    setBranchFormData({
                                                        ...branchFormData,
                                                        name: e.target.value,
                                                    })
                                                }
                                                required
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Địa chỉ</label>
                                            <textarea
                                                className="textarea textarea-bordered w-full mt-1"
                                                value={branchFormData.address}
                                                onChange={(e) =>
                                                    setBranchFormData({
                                                        ...branchFormData,
                                                        address: e.target.value,
                                                    })
                                                }
                                                required
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Số điện thoại
                                            </label>
                                            <input
                                                type="text"
                                                className="input input-bordered w-full mt-1"
                                                value={branchFormData.phoneNumber}
                                                onChange={(e) =>
                                                    setBranchFormData({
                                                        ...branchFormData,
                                                        phoneNumber: e.target.value,
                                                    })
                                                }
                                                required
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Vĩ độ</label>
                                            <input
                                                type="number"
                                                step="any"
                                                className="input input-bordered w-full mt-1"
                                                value={branchFormData.latitude}
                                                onChange={(e) =>
                                                    setBranchFormData({
                                                        ...branchFormData,
                                                        latitude: e.target.value,
                                                    })
                                                }
                                                required
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Kinh độ</label>
                                            <input
                                                type="number"
                                                step="any"
                                                className="input input-bordered w-full mt-1"
                                                value={branchFormData.longitude}
                                                onChange={(e) =>
                                                    setBranchFormData({
                                                        ...branchFormData,
                                                        longitude: e.target.value,
                                                    })
                                                }
                                                required
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Giờ hoạt động
                                            </label>
                                            <input
                                                type="text"
                                                className="input input-bordered w-full mt-1"
                                                value={branchFormData.operatingHours}
                                                onChange={(e) =>
                                                    setBranchFormData({
                                                        ...branchFormData,
                                                        operatingHours: e.target.value,
                                                    })
                                                }
                                                required
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Trạng thái
                                            </label>
                                            <input
                                                type="checkbox"
                                                className="toggle mt-1"
                                                checked={branchFormData.isActive}
                                                onChange={(e) =>
                                                    setBranchFormData({
                                                        ...branchFormData,
                                                        isActive: e.target.checked,
                                                    })
                                                }
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mt-4 flex justify-end gap-2">
                                            <button
                                                type="button"
                                                className="btn btn-ghost"
                                                onClick={closeBranchModal}
                                                disabled={loading}
                                            >
                                                Hủy
                                            </button>
                                            <button
                                                type="submit"
                                                className="btn btn-primary"
                                                disabled={loading}
                                            >
                                                {isEditBranchMode ? "Cập nhật" : "Thêm"}
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

export default BranchList;