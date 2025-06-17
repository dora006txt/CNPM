import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const ProtectedRoute = ({ children }) => {
    const { user } = useAuth();

    if (!user) {
        // Nếu chưa đăng nhập, chuyển hướng đến trang Đăng nhập
        return <Navigate to="/login" replace />;
    }

    // Nếu đã đăng nhập, hiển thị nội dung
    return children;
};

export default ProtectedRoute;