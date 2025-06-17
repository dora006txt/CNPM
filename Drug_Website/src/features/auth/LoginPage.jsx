import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import axios from "axios";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import { useAuth } from "../../context/AuthContext";
import { API_ENDPOINTS } from "../../config/apiConfig";
import { jwtDecode } from "jwt-decode";

const LoginPage = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [formData, setFormData] = useState({
    phoneNumber: "",
    password: "",
    rememberMe: false,
  });
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === "checkbox" ? checked : value,
    });
  };

  const toggleShowPassword = () => setShowPassword(!showPassword);

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

    try {
      const response = await axios.post(
        API_ENDPOINTS.LOGIN,
        {
          phoneNumber: formData.phoneNumber,
          password: formData.password,
        },
        {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        }
      );

      const data = response.data;
      console.log("API response:", data);

      if (data && data.token) {
        // Giải mã token để lấy thông tin cơ bản
        const decodedToken = jwtDecode(data.token);
        console.log("Decoded token:", decodedToken);

        // Kiểm tra thời gian token
        const currentTime = Math.floor(Date.now() / 1000);
        if (decodedToken.exp < currentTime) {
          setError("Token đã hết hạn. Vui lòng đăng nhập lại!");
          setLoading(false);
          return;
        }
        if (decodedToken.iat > currentTime + 300) {
          console.warn("Token được tạo trong tương lai:", new Date(decodedToken.iat * 1000).toLocaleString());
          setError("Lỗi hệ thống: Token không hợp lệ do thời gian tạo!");
          setLoading(false);
          return;
        }

        // Lưu token
        localStorage.setItem("token", data.token);
        console.log("Token expiration time:", new Date(decodedToken.exp * 1000).toLocaleString());
        console.log("Time left until expiration:", Math.floor((decodedToken.exp - currentTime) / 60), "minutes");

        // Chuẩn bị dữ liệu user
        const userData = {
          phoneNumber: decodedToken.sub || data.phoneNumber || formData.phoneNumber,
          userId: data.userId || decodedToken.userId || null,
          fullName: data.fullName || null,
          email: data.email || null,
          roles: data.roles || [],
          token: data.token,
        };

        // Gọi login từ AuthContext
        const loginSuccess = login(userData);
        if (loginSuccess) {
          setSuccess(data.message || "Đăng nhập thành công!");
          setTimeout(() => {
            navigate(userData.roles.includes("ADMIN") ? "/admin" : "/", { replace: true });
          }, 2000);
        } else {
          setError("Đăng nhập thất bại. Vui lòng kiểm tra thông tin người dùng!");
        }
      } else {
        setError(data.message || "Đăng nhập thất bại. Không nhận được token!");
      }
    } catch (err) {
      console.error("Login error:", {
        status: err.response?.status,
        data: err.response?.data,
        message: err.response?.data?.message,
        requestData: { phoneNumber: formData.phoneNumber },
      });
      if (err.response) {
        if (err.response.status === 401) {
          setError(err.response.data?.message || "Sai số điện thoại hoặc mật khẩu!");
        } else if (err.response.status === 403) {
          setError("Truy cập bị từ chối. Vui lòng kiểm tra quyền truy cập!");
        } else if (err.response.status === 400) {
          setError(err.response.data?.message || "Dữ liệu đăng nhập không hợp lệ!");
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
        <h1 className="text-3xl font-bold mb-6 text-blue-600 dark:text-blue-400 text-center">Đăng nhập</h1>
        <form onSubmit={handleSubmit}>
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
          <div className="mb-4 flex items-center">
            <input
              type="checkbox"
              name="rememberMe"
              checked={formData.rememberMe}
              onChange={handleInputChange}
              className="mr-2"
            />
            <label className="text-gray-700 dark:text-gray-300">Ghi nhớ đăng nhập</label>
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
              "Đăng nhập"
            )}
          </button>
        </form>
        <div className="text-center mt-4">
          <p className="text-gray-700 dark:text-gray-300">
            Chưa có tài khoản?{" "}
            <Link to="/register" className="text-blue-600 dark:text-blue-400 hover:underline">
              Đăng ký
            </Link>
          </p>
          <p className="text-gray-700 dark:text-gray-300 mt-2">
            Quên mật khẩu?{" "}
            <Link to="/forgot-password" className="text-blue-600 dark:text-blue-400 hover:underline">
              Khôi phục
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

export default LoginPage;