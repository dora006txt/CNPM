import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import axios from "axios";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import { API_ENDPOINTS } from "../../config/apiConfig";
import { useAuth } from "../../context/AuthContext";

const ChangePasswordPage = () => {
    const navigate = useNavigate();
    const { user } = useAuth();
    const [formData, setFormData] = useState({
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
    });
    const [showCurrentPassword, setShowCurrentPassword] = useState(false);
    const [showNewPassword, setShowNewPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [loading, setLoading] = useState(false);

    const handleInputChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const toggleShowCurrentPassword = () => setShowCurrentPassword(!showCurrentPassword);
    const toggleShowNewPassword = () => setShowNewPassword(!showNewPassword);
    const toggleShowConfirmPassword = () => setShowConfirmPassword(!showConfirmPassword);

    const validateForm = () => {
        // Kiểm tra mật khẩu hiện tại: Không trống
        if (!formData.currentPassword.trim()) {
            setError("Vui lòng nhập mật khẩu hiện tại!");
            return false;
        }
        // Kiểm tra mật khẩu mới: Tối thiểu 6 ký tự
        const passwordRegex = /^.{6,}$/;
        if (!passwordRegex.test(formData.newPassword)) {
            setError("Mật khẩu mới phải có ít nhất 6 ký tự!");
            return false;
        }
        // Kiểm tra xác nhận mật khẩu
        if (formData.newPassword !== formData.confirmPassword) {
            setError("Mật khẩu xác nhận không khớp!");
            return false;
        }
        return true;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError("");
        setSuccess("");

        const token = localStorage.getItem("token");
        if (!token) {
            setError("Vui lòng đăng nhập để đổi mật khẩu!");
            setLoading(false);
            navigate("/login");
            return;
        }

        if (!user?.userId) {
            setError("Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại!");
            setLoading(false);
            navigate("/login");
            return;
        }

        if (!validateForm()) {
            setLoading(false);
            return;
        }

        try {
            const response = await axios.post(
                API_ENDPOINTS.CHANGE_PASSWORD(user.userId),
                formData,
                {
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${token}`,
                    },
                }
            );
            console.log("Change password response:", response.data);
            setSuccess(response.data.message || "Đổi mật khẩu thành công!");
            setTimeout(() => navigate("/"), 2000);
        } catch (err) {
            console.error("Change password error:", {
                status: err.response?.status,
                data: err.response?.data,
                message: err.response?.data?.message,
                requestData: formData,
            });
            if (err.response) {
                if (err.response.status === 400) {
                    setError(err.response.data?.message || "Dữ liệu không hợp lệ! Vui lòng kiểm tra mật khẩu hiện tại.");
                } else if (err.response.status === 401) {
                    setError("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!");
                    localStorage.removeItem("token");
                    navigate("/login");
                } else if (err.response.status === 403) {
                    setError("Truy cập bị từ chối. Vui lòng kiểm tra quyền!");
                } else {
                    setError(err.response.data?.message || "Đã có lỗi xảy ra, vui lòng thử lại!");
                }
            } else if (err.code === "ERR_NETWORK") {
                setError("Không thể kết nối đến server. Vui lòng kiểm tra lại kết nối!");
            } else {
                setError("Đã có lỗi xảy ra, vui lòng thử lại!");
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container mx-auto px-4 py-6 min-h-screen flex items-center justify-center">
            <div className="bg-white dark:bg-gray-800 p-8 rounded-lg shadow-lg max-w-md w-full">
                <h1 className="text-3xl font-bold mb-6 text-blue-600 dark:text-blue-400 text-center">Đổi mật khẩu</h1>
                <form onSubmit={handleSubmit}>
                    <div className="mb-4 relative">
                        <label className="block text-gray-700 dark:text-gray-300 mb-2">Mật khẩu hiện tại</label>
                        <input
                            type={showCurrentPassword ? "text" : "password"}
                            name="currentPassword"
                            value={formData.currentPassword}
                            onChange={handleInputChange}
                            className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500"
                            required
                        />
                        <button
                            type="button"
                            onClick={toggleShowCurrentPassword}
                            className="absolute right-3 top-10 text-gray-500 dark:text-gray-300"
                        >
                            {showCurrentPassword ? <FaEyeSlash /> : <FaEye />}
                        </button>
                    </div>
                    <div className="mb-4 relative">
                        <label className="block text-gray-700 dark:text-gray-300 mb-2">Mật khẩu mới</label>
                        <input
                            type={showNewPassword ? "text" : "password"}
                            name="newPassword"
                            value={formData.newPassword}
                            onChange={handleInputChange}
                            className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500"
                            required
                        />
                        <button
                            type="button"
                            onClick={toggleShowNewPassword}
                            className="absolute right-3 top-10 text-gray-500 dark:text-gray-300"
                        >
                            {showNewPassword ? <FaEyeSlash /> : <FaEye />}
                        </button>
                    </div>
                    <div className="mb-4 relative">
                        <label className="block text-gray-700 dark:text-gray-300 mb-2">Xác nhận mật khẩu mới</label>
                        <input
                            type={showConfirmPassword ? "text" : "password"}
                            name="confirmPassword"
                            value={formData.confirmPassword}
                            onChange={handleInputChange}
                            className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500"
                            required
                        />
                        <button
                            type="button"
                            onClick={toggleShowConfirmPassword}
                            className="absolute right-3 top-10 text-gray-500 dark:text-gray-300"
                        >
                            {showConfirmPassword ? <FaEyeSlash /> : <FaEye />}
                        </button>
                    </div>
                    {error && (
                        <div className="alert alert-error mb-4 bg-red-100 text-red-700 p-3 rounded-lg">
                            <span>{error}</span>
                        </div>
                    )}
                    {success && (
                        <div className="alert alert-success mb-4 bg-green-100 text-green-700 p-3 rounded-lg">
                            <span>{success}</span>
                        </div>
                    )}
                    <button
                        type="submit"
                        className="btn btn-primary w-full bg-blue-600 hover:bg-blue-700 text-white py-2 rounded-lg flex items-center justify-center transition duration-300"
                        disabled={loading}
                    >
                        {loading ? (
                            <span className="flex items-center">
                                <svg className="animate-spin h-5 w-5 mr-2 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
                                </svg>
                                Đang xử lý...
                            </span>
                        ) : (
                            "Đổi mật khẩu"
                        )}
                    </button>
                </form>
                <div className="text-center mt-4">
                    <p className="text-gray-700 dark:text-gray-300">
                        <Link to="/" className="text-blue-600 dark:text-blue-400 hover:underline">
                            Trở về trang chủ
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default ChangePasswordPage;