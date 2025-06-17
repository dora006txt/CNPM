import React, { useState } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import { API_ENDPOINTS } from "../../config/apiConfig";

const ForgotPasswordPage = () => {
    const [formData, setFormData] = useState({
        email: "",
    });
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState("");

    const handleInputChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError("");
        setSuccess("");

        try {
            const response = await axios.post(API_ENDPOINTS.FORGOT_PASSWORD, formData, {
                headers: {
                    "Content-Type": "application/json",
                },
            });

            const data = response.data;
            setSuccess(data.message || "Nếu tài khoản với email này tồn tại, email đặt lại mật khẩu đã được gửi.");
        } catch (err) {
            if (err.response) {
                setError(err.response.data?.error || "Gửi yêu cầu khôi phục thất bại!");
            } else if (err.code === "ERR_NETWORK") {
                setError("Không thể kết nối đến server. Vui lòng thử lại!");
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
                <h1 className="text-3xl font-bold mb-6 text-blue-600 dark:text-blue-400 text-center">Quên mật khẩu</h1>
                <form onSubmit={handleSubmit}>
                    <label className="text-red-500">Vui lòng điền Email để khôi phục lại mật khẩu nhé !</label>
                    <div className="mb-4">
                        <label className="block text-gray-700 dark:text-gray-300 mb-2 mt-5">Email</label>
                        <input
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleInputChange}
                            className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500"
                            required
                        />
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
                        className="btn btn-primary w-full bg-blue-300 hover:bg-blue-500 text-black py-2 rounded-lg flex items-center justify-center transition duration-300"
                        disabled={loading}
                    >
                        {loading ? (
                            <span className="flex items-center">
                                <svg className="animate-spin h-5 w-5 mr-2 text-black" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
                                </svg>
                                Đang xử lý...
                            </span>
                        ) : (
                            "Gửi yêu cầu"
                        )}
                    </button>
                </form>
                <div className="text-center mt-4">
                    <p className="text-gray-700 dark:text-gray-300">
                        Quay lại?{" "}
                        <Link to="/login" className="text-blue-600 dark:text-blue-400 hover:underline">
                            Đăng nhập
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default ForgotPasswordPage;