import React from "react";
import { FaSignOutAlt, FaSun, FaMoon, FaHome } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../../context/AuthContext";

const StaffHeader = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [theme, setTheme] = React.useState("dark");

    const toggleTheme = () => {
        const newTheme = theme === "light" ? "dark" : "light";
        setTheme(newTheme);
        document.documentElement.setAttribute("data-theme", newTheme);
    };

    const handleLogout = () => {
        logout();
        localStorage.removeItem("token");
        navigate("/login");
    };

    const handleHome = () => {
        navigate("/");
    };

    return (
        <div className="bg-green-100 shadow-md p-4 flex justify-between items-center">
            <h1 className="text-xl font-semibold text-green-800">Pharmacy Staff</h1>
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
                <span className="hidden md:block text-green-700">
                    {user?.email || "Staff User"}
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

export default StaffHeader;