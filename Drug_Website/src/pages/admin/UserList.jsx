import React, { useState, useEffect } from "react";
import { FaPlus } from "react-icons/fa";
import { Dialog, Transition } from "@headlessui/react";
import { Fragment } from "react";
import axios from "axios";
import { useAuth } from "../../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { API_ENDPOINTS } from "../../config/apiConfig";

const UserList = () => {
    const { user } = useAuth();
    const navigate = useNavigate();
    const [users, setUsers] = useState([]);
    const [currentUser, setCurrentUser] = useState(null);
    const [searchTerm, setSearchTerm] = useState("");
    const [filterRole, setFilterRole] = useState("");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [newUser, setNewUser] = useState({
        username: "",
        password: "",
        email: "",
        phoneNumber: "",
        fullName: "",
        dateOfBirth: "",
        gender: "",
        address: "",
        roleId: 1, // Mặc định là Customer
        isActive: true,
    });

    // Lấy thông tin người dùng hiện tại
    const fetchCurrentUser = async () => {
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để truy cập!");
            navigate("/login");
            return;
        }
        try {
            const response = await axios.get(API_ENDPOINTS.USER_PROFILE, {
                headers: { Authorization: `Bearer ${user.token}` },
            });
            setCurrentUser(response.data);
        } catch (err) {
            setError(
                err.response?.data?.message || "Không thể tải thông tin người dùng. Vui lòng thử lại."
            );
            console.error("Fetch current user error:", err.response || err);
        }
    };

    // Lấy danh sách người dùng
    const fetchUsers = async () => {
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để truy cập!");
            navigate("/login");
            return;
        }
        setLoading(true);
        setError(null);
        try {
            const response = await axios.get(API_ENDPOINTS.USERS, {
                headers: { Authorization: `Bearer ${user.token}` },
            });
            setUsers(Array.isArray(response.data) ? response.data : []);
        } catch (err) {
            setError(
                err.response?.data?.message ||
                (err.response?.status === 403
                    ? "Truy cập bị từ chối (403). Vui lòng kiểm tra token hoặc quyền truy cập."
                    : "Không thể tải danh sách người dùng. Vui lòng thử lại.")
            );
            console.error("Fetch users error:", err.response || err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCurrentUser();
        fetchUsers();
    }, [user, navigate]);

    // Lọc người dùng
    const filteredUsers = Array.isArray(users)
        ? users.filter((item) => {
            const matchesSearch =
                item.username?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                item.email?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                item.phoneNumber?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                item.fullName?.toLowerCase().includes(searchTerm.toLowerCase());
            const matchesRole = filterRole
                ? item.roleId === parseInt(filterRole)
                : true;
            return matchesSearch && matchesRole;
        })
        : [];

    // Mở modal thêm người dùng
    const openModal = () => {
        setNewUser({
            username: "",
            password: "",
            email: "",
            phoneNumber: "",
            fullName: "",
            dateOfBirth: "",
            gender: "",
            address: "",
            roleId: 1,
            isActive: true,
        });
        setIsModalOpen(true);
    };

    // Đóng modal
    const closeModal = () => {
        setIsModalOpen(false);
    };

    // Thêm người dùng
    const handleAddUser = async (e) => {
        e.preventDefault();
        if (!user || !user.token) {
            setError("Vui lòng đăng nhập để thực hiện hành động này!");
            navigate("/login");
            return;
        }
        setLoading(true);
        setError(null);
        try {
            // Validation
            if (!newUser.username && !newUser.phoneNumber) {
                setError("Vui lòng điền tên đăng nhập hoặc số điện thoại!");
                setLoading(false);
                return;
            }
            if (!newUser.email) {
                setError("Vui lòng điền email!");
                setLoading(false);
                return;
            }
            if (!newUser.password) {
                setError("Vui lòng điền mật khẩu!");
                setLoading(false);
                return;
            }
            if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(newUser.email)) {
                setError("Email không hợp lệ!");
                setLoading(false);
                return;
            }
            if (newUser.password.length < 6) {
                setError("Mật khẩu phải có ít nhất 6 ký tự!");
                setLoading(false);
                return;
            }
            if (newUser.phoneNumber && !/^\d{10}$/.test(newUser.phoneNumber)) {
                setError("Số điện thoại phải là 10 chữ số!");
                setLoading(false);
                return;
            }

            const payload = {
                username: newUser.username || newUser.phoneNumber, // Sử dụng phoneNumber nếu username trống
                password: newUser.password,
                email: newUser.email,
                phoneNumber: newUser.phoneNumber || null,
                fullName: newUser.fullName || null,
                dateOfBirth: newUser.dateOfBirth || null,
                gender: newUser.gender || null,
                address: newUser.address || null,
                roleId: newUser.roleId,
                isActive: newUser.isActive,
            };

            await axios.post(API_ENDPOINTS.REGISTER, payload, {
                headers: { "Content-Type": "application/json" },
            });
            fetchUsers(); // Làm mới danh sách
            closeModal();
        } catch (err) {
            setError(
                err.response?.data?.message || "Có lỗi xảy ra khi thêm người dùng. Vui lòng thử lại."
            );
            console.error("Add user error:", err.response || err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <h2 className="text-2xl font-bold mb-6">Quản lý Người dùng</h2>

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

            {currentUser && (
                <div className="mb-4">
                    <p className="text-sm">
                        Đang đăng nhập với: <strong>{currentUser.email}</strong> (Vai trò: {currentUser.roleId === 1 ? "Customer" : currentUser.roleId === 2 ? "Admin" : "Nhân viên"})
                    </p>
                </div>
            )}

            <div className="mb-4 flex flex-col md:flex-row gap-4">
                <input
                    type="text"
                    placeholder="Tìm kiếm người dùng (tên, email, số điện thoại)..."
                    className="input input-bordered w-full md:w-1/3"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
                <select
                    className="select select-bordered w-full md:w-1/4"
                    value={filterRole}
                    onChange={(e) => setFilterRole(e.target.value)}
                >
                    <option value="">Tất cả vai trò</option>
                    <option value="1">Customer</option>
                    <option value="2">Admin</option>
                    <option value="3">Nhân viên</option>
                </select>
                <button
                    className="btn btn-primary w-full md:w-auto"
                    onClick={openModal}
                    disabled={loading}
                >
                    <FaPlus className="mr-2" /> Thêm Người dùng
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
                                <th>ID</th>
                                <th>Họ tên</th>
                                <th>Email</th>
                                <th>Số điện thoại</th>
                                <th>Vai trò</th>
                                <th>Trạng thái</th>
                                <th>Địa chỉ</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredUsers.length === 0 ? (
                                <tr>
                                    <td colSpan="7" className="text-center">
                                        Không tìm thấy người dùng.
                                    </td>
                                </tr>
                            ) : (
                                filteredUsers.map((user) => (
                                    <tr key={user.id}>
                                        <td>{user.id}</td>
                                        <td>{user.fullName || "N/A"}</td>
                                        <td>{user.email}</td>
                                        <td>{user.phoneNumber || "N/A"}</td>
                                        <td>
                                            {user.roles.includes("ADMIN") ? "Admin" : user.roles.includes("STAFF") ? "Nhân viên" : "Customer"}
                                        </td>
                                        <td>
                                            <span className={`badge ${user.isActive ? "badge-success" : "badge-error"}`}>
                                                {user.isActive ? "Hoạt động" : "Không hoạt động"}
                                            </span>
                                        </td>
                                        <td>{user.address || "N/A"}</td>
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
                                        Thêm Người dùng Mới
                                    </Dialog.Title>
                                    <form onSubmit={handleAddUser} className="mt-4">
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Tên đăng nhập
                                            </label>
                                            <input
                                                type="text"
                                                className="input input-bordered w-full mt-1"
                                                value={newUser.username}
                                                onChange={(e) =>
                                                    setNewUser({ ...newUser, username: e.target.value })
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
                                                value={newUser.phoneNumber}
                                                onChange={(e) =>
                                                    setNewUser({ ...newUser, phoneNumber: e.target.value })
                                                }
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Họ và tên
                                            </label>
                                            <input
                                                type="text"
                                                className="input input-bordered w-full mt-1"
                                                value={newUser.fullName}
                                                onChange={(e) =>
                                                    setNewUser({ ...newUser, fullName: e.target.value })
                                                }
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Email
                                            </label>
                                            <input
                                                type="email"
                                                className="input input-bordered w-full mt-1"
                                                value={newUser.email}
                                                onChange={(e) =>
                                                    setNewUser({ ...newUser, email: e.target.value })
                                                }
                                                required
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Mật khẩu
                                            </label>
                                            <input
                                                type="password"
                                                className="input input-bordered w-full mt-1"
                                                value={newUser.password}
                                                onChange={(e) =>
                                                    setNewUser({ ...newUser, password: e.target.value })
                                                }
                                                required
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Ngày sinh
                                            </label>
                                            <input
                                                type="date"
                                                className="input input-bordered w-full mt-1"
                                                value={newUser.dateOfBirth}
                                                onChange={(e) =>
                                                    setNewUser({ ...newUser, dateOfBirth: e.target.value })
                                                }
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Giới tính
                                            </label>
                                            <select
                                                className="select select-bordered w-full mt-1"
                                                value={newUser.gender}
                                                onChange={(e) =>
                                                    setNewUser({ ...newUser, gender: e.target.value })
                                                }
                                                disabled={loading}
                                            >
                                                <option value="">Chọn giới tính</option>
                                                <option value="male">Nam</option>
                                                <option value="female">Nữ</option>
                                                <option value="other">Khác</option>
                                            </select>
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Địa chỉ
                                            </label>
                                            <input
                                                type="text"
                                                className="input input-bordered w-full mt-1"
                                                value={newUser.address}
                                                onChange={(e) =>
                                                    setNewUser({ ...newUser, address: e.target.value })
                                                }
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Vai trò
                                            </label>
                                            <select
                                                className="select select-bordered w-full mt-1"
                                                value={newUser.roleId}
                                                onChange={(e) =>
                                                    setNewUser({ ...newUser, roleId: parseInt(e.target.value) })
                                                }
                                                disabled={loading}
                                            >
                                                <option value={1}>Customer</option>
                                                <option value={2}>Admin</option>
                                                <option value={3}>Nhân viên</option>
                                            </select>
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Trạng thái
                                            </label>
                                            <input
                                                type="checkbox"
                                                className="toggle mt-1"
                                                checked={newUser.isActive}
                                                onChange={(e) =>
                                                    setNewUser({ ...newUser, isActive: e.target.checked })
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
                                                Thêm
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

export default UserList;