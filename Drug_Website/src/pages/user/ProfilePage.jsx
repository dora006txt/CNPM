import React, { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import axios from "axios";
import { FaSignOutAlt } from "react-icons/fa";
import { API_ENDPOINTS } from "../../config/apiConfig";
import { useAuth } from "../../context/AuthContext";

const ProfilePage = () => {
    const [user, setUser] = useState(null);
    const [editMode, setEditMode] = useState(false);
    const [formData, setFormData] = useState({
        fullName: "",
        email: "",
        phoneNumber: "",
        dateOfBirth: "",
        gender: "",
        address: "",
    });
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const navigate = useNavigate();
    const { logout } = useAuth();

    useEffect(() => {
        const fetchUserAndOrders = async () => {
            try {
                setLoading(true);
                const token = localStorage.getItem("token");
                if (!token) {
                    throw new Error("Vui lòng đăng nhập!");
                }

                const userResponse = await axios.get(API_ENDPOINTS.USER_PROFILE, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setUser(userResponse.data);
                setFormData({
                    fullName: userResponse.data.fullName || "",
                    email: userResponse.data.email || "",
                    phoneNumber: userResponse.data.phoneNumber || "",
                    dateOfBirth: userResponse.data.dateOfBirth || "",
                    gender: userResponse.data.gender || "",
                    address: userResponse.data.address || "",
                });

                const ordersResponse = await axios.get(API_ENDPOINTS.ORDERS, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setOrders(ordersResponse.data || []);
            } catch (err) {
                setError("Không thể tải thông tin. Vui lòng thử lại!");
                if (err.message.includes("đăng nhập")) {
                    navigate("/login");
                }
            } finally {
                setLoading(false);
            }
        };
        fetchUserAndOrders();
    }, [navigate]);

    const handleInputChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleUpdate = async (e) => {
        e.preventDefault();
        try {
            const token = localStorage.getItem("token");
            const payload = {
                fullName: formData.fullName,
                email: formData.email,
                phoneNumber: formData.phoneNumber,
                dateOfBirth: formData.dateOfBirth || null,
                gender: formData.gender || null,
                address: formData.address || null,
            };
            console.log("Update payload:", payload);
            await axios.put(API_ENDPOINTS.USER_PROFILE, payload, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setUser({ ...user, ...payload });
            setEditMode(false);
            alert("Cập nhật thông tin thành công!");
        } catch (err) {
            setError("Lỗi khi cập nhật thông tin: " + (err.response?.data?.message || err.message));
        }
    };

    const handleLogout = () => {
        logout(true); // Gọi logout với redirectHome = true
        navigate("/", { replace: true });
    };

    if (loading) return <div><span className="loading loading-spinner loading-lg"></span></div>;
    if (error) return <div><div className="alert alert-error mb-6"><span>{error}</span></div></div>;

    return (
        <div className="pr-30 pl-30 pt-10 pb-10 text-black">
            <h1 className="text-2xl font-bold mb-6">Hồ sơ người dùng</h1>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="md:col-span-2">
                    <div className="p-4 border rounded-lg">
                        {editMode ? (
                            <>
                                <h2 className="text-xl font-semibold mb-4">Chỉnh sửa thông tin</h2>
                                <form onSubmit={handleUpdate} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                    <div className="mb-4">
                                        <label className="block mb-2">Họ tên</label>
                                        <input
                                            type="text"
                                            name="fullName"
                                            value={formData.fullName}
                                            onChange={handleInputChange}
                                            className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                            required
                                        />
                                    </div>
                                    <div className="mb-4">
                                        <label className="block mb-2">Email</label>
                                        <input
                                            type="email"
                                            name="email"
                                            value={formData.email}
                                            onChange={handleInputChange}
                                            className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                            required
                                        />
                                    </div>
                                    <div className="mb-4">
                                        <label className="block mb-2">Số điện thoại</label>
                                        <input
                                            type="tel"
                                            name="phoneNumber"
                                            value={formData.phoneNumber}
                                            onChange={handleInputChange}
                                            className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                        />
                                    </div>
                                    <div className="mb-4">
                                        <label className="block mb-2">Ngày sinh</label>
                                        <input
                                            type="date"
                                            name="dateOfBirth"
                                            value={formData.dateOfBirth}
                                            onChange={handleInputChange}
                                            className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                        />
                                    </div>
                                    <div className="mb-4">
                                        <label className="block mb-2">Giới tính</label>
                                        <select
                                            name="gender"
                                            value={formData.gender}
                                            onChange={handleInputChange}
                                            className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                        >
                                            <option value="">Chọn giới tính</option>
                                            <option value="MALE">Nam</option>
                                            <option value="FEMALE">Nữ</option>
                                            <option value="OTHER">Khác</option>
                                        </select>
                                    </div>
                                    <div className="mb-4">
                                        <label className="block mb-2">Địa chỉ</label>
                                        <input
                                            type="text"
                                            name="address"
                                            value={formData.address}
                                            onChange={handleInputChange}
                                            className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                        />
                                    </div>
                                    <div className="flex space-x-4 md:col-span-2">
                                        <button type="submit" className="btn btn-primary mt-2">
                                            Lưu
                                        </button>
                                        <button
                                            type="button"
                                            onClick={() => setEditMode(false)}
                                            className="btn btn-secondary mt-2"
                                        >
                                            Hủy
                                        </button>
                                    </div>
                                </form>
                            </>
                        ) : (
                            <>
                                <h2 className="text-xl font-semibold mb-4">Thông tin cá nhân</h2>
                                <div className="mb-4">
                                    <p className="mb-2"><strong>Họ tên:</strong> {user.fullName || "Chưa có"}</p>
                                    <p className="mb-2"><strong>Email:</strong> {user.email || "Chưa có"}</p>
                                    <p className="mb-2"><strong>Số điện thoại:</strong> {user.phoneNumber || "Chưa có"}</p>
                                    <p className="mb-2"><strong>Ngày sinh:</strong> {user.dateOfBirth ? new Date(user.dateOfBirth).toLocaleDateString("vi-VN") : "Chưa có"}</p>
                                    <p className="mb-2"><strong>Giới tính:</strong> {user.gender ? (user.gender === "MALE" ? "Nam" : user.gender === "FEMALE" ? "Nữ" : "Khác") : "Chưa có"}</p>
                                    <p className="mb-4"><strong>Địa chỉ:</strong> {user.address || "Chưa có"}</p>
                                    <div className="flex space-x-4">
                                        <button
                                            onClick={() => setEditMode(true)}
                                            className="btn btn-primary mt-2"
                                        >
                                            Chỉnh sửa
                                        </button>
                                        <Link
                                            to="/change-password"
                                            className="btn btn-secondary mt-2 flex items-center"
                                        >
                                            Đổi mật khẩu
                                        </Link>
                                    </div>
                                    <button
                                        onClick={handleLogout}
                                        className="btn btn-error mt-4 flex items-center"
                                    >
                                        <FaSignOutAlt className="mr-2" /> Đăng xuất
                                    </button>
                                </div>
                            </>
                        )}
                    </div>
                </div>
                <div>
                    <div className="p-4 border rounded-lg">
                        <h3 className="text-lg font-semibold mb-4">Lịch sử đơn hàng</h3>
                        {orders.length > 0 ? (
                            <ul className="mt-2">
                                {orders.map((order) => (
                                    <li key={order.orderId} className="mb-2">
                                        <Link
                                            to={`/order/${order.orderId}`}
                                            className="text-blue-600 hover:underline"
                                        >
                                            Đơn #{order.orderCode} - {order.orderStatusName} (
                                            {new Date(order.orderDate).toLocaleString("vi-VN")})
                                        </Link>
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <p>Chưa có đơn hàng nào.</p>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ProfilePage;