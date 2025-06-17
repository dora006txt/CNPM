import React, { useState, useEffect } from "react";
import { FaEdit, FaTrash, FaPlus } from "react-icons/fa";
import { Dialog, Transition } from "@headlessui/react";
import { Fragment } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { API_ENDPOINTS } from "../../config/apiConfig";

const StaffList = () => {
    const { user } = useAuth();
    const navigate = useNavigate();
    const [staff, setStaff] = useState([]);
    const [branches, setBranches] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [filterStatus, setFilterStatus] = useState("");
    const [filterBranch, setFilterBranch] = useState("");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const [currentStaff, setCurrentStaff] = useState(null);
    const [formData, setFormData] = useState({
        phoneNumber: "",
        password: "",
        email: "",
        userId: "",
        fullName: "",
        title: "",
        specialty: "",
        workplaceInfo: "",
        branchId: "",
        profileImageUrl: null,
        isAvailableForConsultation: true,
        isActive: true,
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // Lấy danh sách chi nhánh
    const fetchBranches = async () => {
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để truy cập!");
            navigate("/login");
            return;
        }
        setLoading(true);
        setError(null);
        try {
            const response = await axios.get(API_ENDPOINTS.BRANCHES, {
                headers: { Authorization: `Bearer ${user.token}` },
            });
            setBranches(Array.isArray(response.data) ? response.data : []);
        } catch (err) {
            setError(
                err.response?.data?.message || "Không thể tải danh sách chi nhánh. Vui lòng thử lại."
            );
            console.error("Fetch branches error:", err.response || err);
            setBranches([]);
        } finally {
            setLoading(false);
        }
    };

    // Lấy danh sách nhân viên
    const fetchStaff = async () => {
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để truy cập!");
            navigate("/login");
            return;
        }
        setLoading(true);
        setError(null);
        try {
            const response = await axios.get(API_ENDPOINTS.STAFF, {
                headers: { Authorization: `Bearer ${user.token}` },
            });
            setStaff(Array.isArray(response.data) ? response.data : []);
        } catch (err) {
            setError(
                err.response?.data?.message || "Không thể tải danh sách nhân viên. Vui lòng thử lại."
            );
            console.error("Fetch staff error:", err.response || err);
            setStaff([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchBranches();
        fetchStaff();
    }, [user, navigate]);

    // Lọc nhân viên
    const filteredStaff = Array.isArray(staff)
        ? staff.filter((item) => {
            const matchesSearch =
                item.fullName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                item.userEmail?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                item.userPhoneNumber?.toLowerCase().includes(searchTerm.toLowerCase());
            const matchesStatus = filterStatus
                ? (filterStatus === "active" && item.isActive) ||
                (filterStatus === "inactive" && !item.isActive)
                : true;
            const matchesBranch = filterBranch
                ? item.branchId === parseInt(filterBranch)
                : true;
            return matchesSearch && matchesStatus && matchesBranch;
        })
        : [];

    // Mở modal
    const openModal = (staff = null) => {
        if (staff) {
            setIsEditMode(true);
            setCurrentStaff(staff);
            setFormData({
                phoneNumber: staff.userPhoneNumber || "",
                password: "",
                email: staff.userEmail || "",
                userId: staff.userId?.toString() || "",
                fullName: staff.fullName || "",
                title: staff.title || "",
                specialty: staff.specialty || "",
                workplaceInfo: staff.workplaceInfo || "",
                branchId: staff.branchId?.toString() || "",
                profileImageUrl: staff.profileImageUrl || null,
                isAvailableForConsultation: staff.isAvailableForConsultation ?? true,
                isActive: staff.isActive ?? true,
            });
        } else {
            setIsEditMode(false);
            setCurrentStaff(null);
            setFormData({
                phoneNumber: "",
                password: "",
                email: "",
                userId: "",
                fullName: "",
                title: "",
                specialty: "",
                workplaceInfo: "",
                branchId: "",
                profileImageUrl: null,
                isAvailableForConsultation: true,
                isActive: true,
            });
        }
        setIsModalOpen(true);
    };

    // Đóng modal
    const closeModal = () => {
        setIsModalOpen(false);
        setCurrentStaff(null);
        setIsEditMode(false);
        setError(null);
    };

    // Xử lý submit form
    const handleSubmit = async (e) => {
        e.preventDefault();
        console.log("handleSubmit called with formData:", formData); // Log để debug
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để thực hiện hành động này!");
            navigate("/login");
            return;
        }
        setLoading(true);
        setError(null);

        try {
            // Validation
            if (!isEditMode) {
                if (!formData.fullName) {
                    setError("Họ tên là bắt buộc!");
                    setLoading(false);
                    return;
                }
                if (!formData.title) {
                    setError("Chức danh là bắt buộc!");
                    setLoading(false);
                    return;
                }
                if (!formData.branchId) {
                    setError("Vui lòng chọn chi nhánh!");
                    setLoading(false);
                    return;
                }
                if (!formData.userId) {
                    if (!formData.phoneNumber) {
                        setError("Số điện thoại là bắt buộc khi không có userId!");
                        setLoading(false);
                        return;
                    }
                    if (!formData.email) {
                        setError("Email là bắt buộc khi không có userId!");
                        setLoading(false);
                        return;
                    }
                    if (!formData.password) {
                        setError("Mật khẩu là bắt buộc khi không có userId!");
                        setLoading(false);
                        return;
                    }
                    if (!/^\d{10}$/.test(formData.phoneNumber)) {
                        setError("Số điện thoại phải là 10 chữ số!");
                        setLoading(false);
                        return;
                    }
                    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
                        setError("Email không hợp lệ!");
                        setLoading(false);
                        return;
                    }
                    if (formData.password.length < 6) {
                        setError("Mật khẩu phải có ít nhất 6 ký tự!");
                        setLoading(false);
                        return;
                    }
                }
            } else {
                if (!formData.title) {
                    setError("Chức danh là bắt buộc!");
                    setLoading(false);
                    return;
                }
            }

            const headers = { Authorization: `Bearer ${user.token}` };

            if (isEditMode) {
                const staffData = {
                    title: formData.title,
                    specialty: formData.specialty,
                    workplaceInfo: formData.workplaceInfo,
                    branchId: parseInt(formData.branchId),
                    isAvailableForConsultation: formData.isAvailableForConsultation,
                    isActive: formData.isActive,
                };
                console.log("PUT payload:", staffData);
                const response = await axios.put(
                    `${API_ENDPOINTS.STAFF}/${currentStaff.staffId}`,
                    staffData,
                    { headers }
                );
                setStaff(
                    staff.map((item) =>
                        item.staffId === currentStaff.staffId ? response.data : item
                    )
                );
            } else {
                const staffData = formData.userId
                    ? {
                        userId: parseInt(formData.userId),
                        branchId: parseInt(formData.branchId),
                        fullName: formData.fullName,
                        title: formData.title,
                        specialty: formData.specialty,
                        workplaceInfo: formData.workplaceInfo,
                        profileImageUrl: formData.profileImageUrl,
                        isAvailableForConsultation: formData.isAvailableForConsultation,
                        isActive: formData.isActive,
                    }
                    : {
                        phoneNumber: formData.phoneNumber,
                        password: formData.password,
                        email: formData.email,
                        branchId: parseInt(formData.branchId),
                        fullName: formData.fullName,
                        title: formData.title,
                        specialty: formData.specialty,
                        workplaceInfo: formData.workplaceInfo,
                        profileImageUrl: formData.profileImageUrl,
                        isAvailableForConsultation: formData.isAvailableForConsultation,
                        isActive: formData.isActive,
                    };
                console.log("POST payload:", staffData);
                const response = await axios.post(API_ENDPOINTS.STAFF, staffData, { headers });
                setStaff([...staff, response.data]);
            }
            closeModal();
        } catch (err) {
            setError(
                err.response?.data?.message || "Có lỗi xảy ra khi lưu thông tin nhân viên."
            );
            console.error("Submit staff error:", err.response || err);
        } finally {
            setLoading(false);
            console.log("Loading set to false");
        }
    };

    // Xóa nhân viên
    const handleDeleteStaff = async (staffId) => {
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để thực hiện hành động này!");
            navigate("/login");
            return;
        }
        if (window.confirm("Bạn có chắc chắn muốn xóa nhân viên này?")) {
            setLoading(true);
            setError(null);
            try {
                await axios.delete(`${API_ENDPOINTS.STAFF}/${staffId}`, {
                    headers: { Authorization: `Bearer ${user.token}` },
                });
                setStaff(staff.filter((item) => item.staffId !== staffId));
            } catch (err) {
                setError(
                    err.response?.data?.message || "Có lỗi xảy ra khi xóa nhân viên. Vui lòng thử lại."
                );
                console.error("Delete staff error:", err.response || err);
            } finally {
                setLoading(false);
            }
        }
    };

    return (
        <div>
            <h2 className="text-2xl font-bold mb-6">Quản lý Nhân viên</h2>

            {error && (
                <div className="alert alert-error mb-4">
                    <div className="flex-1">
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            fill="none"
                            viewBox="0 24"
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
                    placeholder="Tìm kiếm nhân viên (tên, email, số điện thoại)..."
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
                    <option value="inactive">Không hoạt động</option>
                </select>
                <select
                    className="select select-bordered w-full md:w-1/4"
                    value={filterBranch}
                    onChange={(e) => setFilterBranch(e.target.value)}
                >
                    <option value="">Tất cả chi nhánh</option>
                    {branches.map((branch) => (
                        <option key={branch.branchId} value={branch.branchId}>
                            {branch.name}
                        </option>
                    ))}
                </select>
                <button
                    className="btn btn-primary w-full md:w-auto"
                    onClick={() => openModal()}
                    disabled={loading}
                >
                    <FaPlus className="mr-2" /> Thêm Nhân viên
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
                                <th>Họ tên</th>
                                <th>Email</th>
                                <th>Số điện thoại</th>
                                <th>Chức danh</th>
                                <th>Chuyên môn</th>
                                <th>Chi nhánh</th>
                                <th>Nơi làm việc</th>
                                <th>Có thể tư vấn</th>
                                <th>Trạng thái</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredStaff.length === 0 ? (
                                <tr>
                                    <td colSpan="10" className="text-center">
                                        Không tìm thấy nhân viên.
                                    </td>
                                </tr>
                            ) : (
                                filteredStaff.map((item) => (
                                    <tr key={item.staffId}>
                                        <td>{item.fullName}</td>
                                        <td>{item.userEmail}</td>
                                        <td>{item.userPhoneNumber}</td>
                                        <td>{item.title}</td>
                                        <td>{item.specialty || "N/A"}</td>
                                        <td>{item.branchName}</td>
                                        <td>{item.workplaceInfo || "N/A"}</td>
                                        <td>{item.isAvailableForConsultation ? "Có" : "Không"}</td>
                                        <td>
                                            <span
                                                className={`badge ${item.isActive ? "badge-success" : "badge-warning"
                                                    }`}
                                            >
                                                {item.isActive ? "Hoạt động" : "Không hoạt động"}
                                            </span>
                                        </td>
                                        <td>
                                            <button
                                                className="btn btn-ghost btn-sm mr-2"
                                                onClick={() => openModal(item)}
                                                disabled={loading}
                                            >
                                                <FaEdit />
                                            </button>
                                            <button
                                                className="btn btn-ghost btn-sm"
                                                onClick={() => handleDeleteStaff(item.staffId)}
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
                                        {isEditMode ? "Chỉnh sửa Nhân viên" : "Thêm Nhân viên"}
                                    </Dialog.Title>
                                    <form onSubmit={handleSubmit} className="mt-4">
                                        {!isEditMode && (
                                            <>
                                                <div className="mb-4">
                                                    <label className="block text-sm font-medium">
                                                        User ID (nếu gắn với user hiện có)
                                                    </label>
                                                    <input
                                                        type="number"
                                                        className="input input-bordered w-full mt-1"
                                                        value={formData.userId}
                                                        onChange={(e) =>
                                                            setFormData({
                                                                ...formData,
                                                                userId: e.target.value,
                                                                phoneNumber: e.target.value ? "" : formData.phoneNumber,
                                                                email: e.target.value ? "" : formData.email,
                                                                password: e.target.value ? "" : formData.password,
                                                            })
                                                        }
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
                                                        value={formData.phoneNumber}
                                                        onChange={(e) =>
                                                            setFormData({
                                                                ...formData,
                                                                phoneNumber: e.target.value,
                                                            })
                                                        }
                                                        disabled={loading || formData.userId}
                                                        required={!formData.userId}
                                                    />
                                                </div>
                                                <div className="mb-4">
                                                    <label className="block text-sm font-medium">
                                                        Email
                                                    </label>
                                                    <input
                                                        type="email"
                                                        className="input input-bordered w-full mt-1"
                                                        value={formData.email}
                                                        onChange={(e) =>
                                                            setFormData({
                                                                ...formData,
                                                                email: e.target.value,
                                                            })
                                                        }
                                                        disabled={loading || formData.userId}
                                                        required={!formData.userId}
                                                    />
                                                </div>
                                                <div className="mb-4">
                                                    <label className="block text-sm font-medium">
                                                        Mật khẩu
                                                    </label>
                                                    <input
                                                        type="password"
                                                        className="input input-bordered w-full mt-1"
                                                        value={formData.password}
                                                        onChange={(e) =>
                                                            setFormData({
                                                                ...formData,
                                                                password: e.target.value,
                                                            })
                                                        }
                                                        disabled={loading || formData.userId}
                                                        required={!formData.userId}
                                                    />
                                                </div>
                                            </>
                                        )}
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Họ tên
                                            </label>
                                            <input
                                                type="text"
                                                className="input input-bordered w-full mt-1"
                                                value={formData.fullName}
                                                onChange={(e) =>
                                                    setFormData({
                                                        ...formData,
                                                        fullName: e.target.value,
                                                    })
                                                }
                                                required
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Chức danh
                                            </label>
                                            <input
                                                type="text"
                                                className="input input-bordered w-full mt-1"
                                                value={formData.title}
                                                onChange={(e) =>
                                                    setFormData({
                                                        ...formData,
                                                        title: e.target.value,
                                                    })
                                                }
                                                required
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Chuyên môn
                                            </label>
                                            <input
                                                type="text"
                                                className="input input-bordered w-full mt-1"
                                                value={formData.specialty}
                                                onChange={(e) =>
                                                    setFormData({
                                                        ...formData,
                                                        specialty: e.target.value,
                                                    })
                                                }
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Nơi làm việc
                                            </label>
                                            <input
                                                type="text"
                                                className="input input-bordered w-full mt-1"
                                                value={formData.workplaceInfo}
                                                onChange={(e) =>
                                                    setFormData({
                                                        ...formData,
                                                        workplaceInfo: e.target.value,
                                                    })
                                                }
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Chi nhánh
                                            </label>
                                            <select
                                                className="select select-bordered w-full mt-1"
                                                value={formData.branchId}
                                                onChange={(e) =>
                                                    setFormData({
                                                        ...formData,
                                                        branchId: e.target.value,
                                                    })
                                                }
                                                required
                                                disabled={loading}
                                            >
                                                <option value="">Chọn chi nhánh</option>
                                                {branches.map((branch) => (
                                                    <option key={branch.branchId} value={branch.branchId}>
                                                        {branch.name}
                                                    </option>
                                                ))}
                                            </select>
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Có thể tư vấn
                                            </label>
                                            <input
                                                type="checkbox"
                                                className="toggle mt-1"
                                                checked={formData.isAvailableForConsultation}
                                                onChange={(e) =>
                                                    setFormData({
                                                        ...formData,
                                                        isAvailableForConsultation: e.target.checked,
                                                    })
                                                }
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
                                                checked={formData.isActive}
                                                onChange={(e) =>
                                                    setFormData({
                                                        ...formData,
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
                                                onClick={closeModal}
                                                disabled={loading}
                                            >
                                                Hủy
                                            </button>
                                            <button
                                                type="submit"
                                                className="btn btn-primary relative"
                                                disabled={loading}
                                            >
                                                {loading && (
                                                    <span className="loading loading-spinner mr-2"></span>
                                                )}
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

export default StaffList;