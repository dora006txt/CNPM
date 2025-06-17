import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import axios from "axios";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import { API_ENDPOINTS } from "../../config/apiConfig";

const RegisterPage = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    phoneNumber: "",
    password: "",
    confirmPassword: "",
    fullName: "",
    email: "",
  });
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const toggleShowPassword = () => setShowPassword(!showPassword);
  const toggleShowConfirmPassword = () => setShowConfirmPassword(!showConfirmPassword);

  const validateForm = () => {
    // Kiểm tra số điện thoại: Bắt đầu bằng 0, 10 chữ số
    const phoneRegex = /^0[0-9]{9}$/;
    if (!phoneRegex.test(formData.phoneNumber)) {
      setError("Số điện thoại phải bắt đầu bằng 0 và có đúng 10 chữ số!");
      return false;
    }
    // Kiểm tra mật khẩu: Tối thiểu 6 ký tự
    const passwordRegex = /^.{6,}$/;
    if (!passwordRegex.test(formData.password)) {
      setError("Mật khẩu phải có ít nhất 6 ký tự!");
      return false;
    }
    // Kiểm tra xác nhận mật khẩu
    if (formData.password !== formData.confirmPassword) {
      setError("Mật khẩu xác nhận không khớp!");
      return false;
    }
    // Kiểm tra email: Bắt buộc, định dạng hợp lệ
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      setError("Email không hợp lệ!");
      return false;
    }
    // Kiểm tra họ tên: Không trống, tối thiểu 2 ký tự
    if (!formData.fullName.trim() || formData.fullName.length < 2) {
      setError("Họ tên phải có ít nhất 2 ký tự và không được để trống!");
      return false;
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setSuccess("");

    if (!validateForm()) {
      setLoading(false);
      return;
    }

    const requestData = {
      phoneNumber: formData.phoneNumber,
      password: formData.password,
      fullName: formData.fullName,
      email: formData.email,
    };

    try {
      const response = await axios.post(API_ENDPOINTS.REGISTER, requestData, {
        headers: {
          "Content-Type": "application/json",
        },
        withCredentials: true,
      });
      console.log("Register response:", response.data);
      setSuccess(response.data.message || "Đăng ký thành công! Vui lòng đăng nhập.");
      setTimeout(() => {
        navigate("/login");
      }, 2000);
    } catch (err) {
      console.error("Register error:", {
        status: err.response?.status,
        data: err.response?.data,
        message: err.response?.data?.message,
        requestData,
      });
      if (err.response) {
        if (err.response.status === 400) {
          setError(err.response.data?.message || "Dữ liệu không hợp lệ! Vui lòng kiểm tra lại thông tin (số điện thoại hoặc email có thể đã được sử dụng).");
        } else if (err.response.status === 403) {
          setError("Truy cập bị từ chối (403). Kiểm tra quyền truy cập hoặc CORS.");
        } else {
          setError(err.response.data?.message || "Đã có lỗi xảy ra, vui lòng thử lại!");
        }
      } else if (err.code === "ERR_NETWORK") {
        setError("Không thể kết nối đến server. Vui lòng kiểm tra lại kết nối.");
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
        <h1 className="text-3xl font-bold mb-6 text-blue-600 dark:text-blue-400 text-center">Đăng ký</h1>
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label className="block text-gray-700 dark:text-gray-300 mb-2">Họ tên</label>
            <input
              type="text"
              name="fullName"
              value={formData.fullName}
              onChange={handleInputChange}
              className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>
          <div className="mb-4">
            <label className="block text-gray-700 dark:text-gray-300 mb-2">Số điện thoại</label>
            <input
              type="tel"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleInputChange}
              className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>
          <div className="mb-4">
            <label className="block text-gray-700 dark:text-gray-300 mb-2">Email</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleInputChange}
              className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>
          <div className="mb-4 relative">
            <label className="block text-gray-700 dark:text-gray-300 mb-2">Mật khẩu</label>
            <input
              type={showPassword ? "text" : "password"}
              name="password"
              value={formData.password}
              onChange={handleInputChange}
              className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
            <button
              type="button"
              onClick={toggleShowPassword}
              className="absolute right-3 top-10 text-gray-500 dark:text-gray-300"
            >
              {showPassword ? <FaEyeSlash /> : <FaEye />}
            </button>
          </div>
          <div className="mb-4 relative">
            <label className="block text-gray-700 dark:text-gray-300 mb-2">Xác nhận mật khẩu</label>
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
              "Đăng ký"
            )}
          </button>
        </form>
        <div className="text-center mt-4">
          <p className="text-gray-700 dark:text-gray-300">
            Đã có tài khoản?{" "}
            <Link to="/login" className="text-blue-600 dark:text-blue-400 hover:underline">
              Đăng nhập
            </Link>
          </p>
          <p className="text-gray-700 dark:text-gray-300 mt-2">
            <Link to="/" className="text-blue-600 dark:text-blue-400 hover:underline">
              Trở về trang chủ
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;