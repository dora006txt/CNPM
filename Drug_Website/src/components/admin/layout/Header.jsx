import React from "react";
import { FaSignOutAlt, FaSun, FaMoon, FaHome } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../../context/AuthContext";

const Header = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [theme, setTheme] = React.useState("dark");

    const toggleTheme = () => {
        const newTheme = theme === "light" ? "dark" : "light";
        setTheme(newTheme);
        document.documentElement.setAttribute("data-theme", newTheme);
    };

    const handleLogout = () => {
        logout(); // Cập nhật trạng thái user = null trong AuthContext
        localStorage.removeItem("token"); // Xóa token khỏi localStorage
        navigate("/login"); // Chuyển hướng về trang đăng nhập
    };

    const handleHome = () => {
        navigate("/"); // Chuyển hướng về trang chủ
    };

    return (
        <div className="bg-base-100 shadow-md p-4 flex justify-between items-center">
            <h1 className="text-xl font-semibold">Pharmacy Admin</h1>
            <div className="flex items-center gap-4">
                <button
                    className="btn btn-ghost btn-circle"
                    onClick={handleHome}
                    title="Home"
                >
                    <FaHome size={20} />
                </button>
                <button
                    className="btn btn-ghost btn-circle"
                    onClick={toggleTheme}
                    title={theme === "light" ? "Switch to Dark Theme" : "Switch to Light Theme"}
                >
                    {theme === "light" ? <FaMoon size={20} /> : <FaSun size={20} />}
                </button>
                <span className="hidden md:block">
                    {user?.email || "Admin User"}
                </span>
                <button
                    className="btn btn-ghost btn-circle"
                    onClick={handleLogout}
                    title="Logout"
                >
                    <FaSignOutAlt size={20} />
                </button>
            </div>
        </div>
    );
};

export default Header;