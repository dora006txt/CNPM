import React, { useEffect } from "react";
import { Routes, Route, Navigate, useLocation } from "react-router-dom";
import StaffLayout from "../components/staff/layout/StaffLayout"; // Dùng StaffLayout
import Dashboard from "../pages/staff/Dashboard";
import InventoryList from "../pages/staff/InventoryList";
import PrescriptionList from "../pages/staff/PrescriptionList";
import ConsultationList from "../pages/staff/ConsultationList";
import ProductList from "../pages/staff/ProductList";
import OrderList from "../pages/staff/OrderList";
import { useAuth } from "../context/AuthContext";

const StaffRoutes = () => {
    const { user } = useAuth();
    const location = useLocation();

    console.log("StaffRoutes - User:", user);

    // Nếu user chưa tải (null), chờ 1 chút trước khi redirect
    useEffect(() => {
        if (!user && localStorage.getItem("token")) {
            console.log("User is null but token exists, waiting for auth...");
            const timer = setTimeout(() => {
                if (!user) {
                    console.log("No user after wait, redirecting to /login");
                    navigate("/login", { state: { from: location.pathname } });
                }
            }, 1000); // Chờ 1 giây
            return () => clearTimeout(timer);
        }
    }, [user, location.pathname]);

    if (!user && !localStorage.getItem("token")) {
        console.log("No user and no token, redirecting to /login");
        return <Navigate to="/login" state={{ from: location.pathname }} />;
    }

    if (!user) {
        console.log("User not loaded yet, showing loading state...");
        return <div>Loading...</div>;
    }

    const isCustomer = user.roleId === 1;
    const isAdmin = user.roleId === 2;
    const isStaff = user.roleId === 3;

    console.log("Roles check:", { isCustomer, isAdmin, isStaff });

    if (isCustomer) {
        console.log("Customer detected, redirecting to /");
        return <Navigate to="/" replace />;
    }

    if (isAdmin) {
        console.log("Admin detected, redirecting to /admin");
        return <Navigate to="/admin" replace />;
    }

    if (!isStaff) {
        console.log("Not a staff, redirecting to /");
        return <Navigate to="/" replace />;
    }

    return (
        <Routes>
            <Route element={<StaffLayout />}>
                <Route index element={<Dashboard />} />
                <Route path="inventory" element={<InventoryList />} />
                <Route path="prescriptions" element={<PrescriptionList />} />
                <Route path="consultations" element={<ConsultationList />} />
                <Route path="products" element={<ProductList />} />
                <Route path="orders" element={<OrderList />} />
                <Route path="*" element={<div>404 Not Found - Staff</div>} />
            </Route>
        </Routes>
    );
};

export default StaffRoutes;