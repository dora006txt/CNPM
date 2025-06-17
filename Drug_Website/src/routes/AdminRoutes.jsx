import React, { useEffect } from "react";
import { Routes, Route, Navigate, useLocation } from "react-router-dom";
import AdminLayout from "../components/admin/layout/AdminLayout";
import Dashboard from "../pages/admin/Dashboard";
import ProductList from "../pages/admin/ProductList";
import OrderList from "../pages/admin/OrderList";
import UserList from "../pages/admin/UserList";
import BranchList from "../pages/admin/BranchList";
import PromotionList from "../pages/admin/PromotionList";
import ReviewList from "../pages/admin/ReviewList";
import InventoryList from "../pages/admin/InventoryList";
import PrescriptionList from "../pages/admin/PrescriptionList";
import ConsultationList from "../pages/admin/ConsultationList";
import StaffList from "../pages/admin/StaffList";
import ManufacturersBrandsList from "../pages/admin/ManufacturersBrandsList";
import RevenueReport from "../pages/admin/RevenueReport";
import CategoriesList from "../pages/admin/CategoriesList";
import { useAuth } from "../context/AuthContext";
import BannerList from "../pages/admin/BannerList";

const AdminRoutes = () => {
    const { user } = useAuth();
    const location = useLocation();

    console.log("AdminRoutes - User:", user);

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

    if (isStaff) {
        console.log("Staff detected, redirecting to /staff");
        return <Navigate to="/staff" replace />;
    }

    if (!isAdmin) {
        console.log("Not an admin, redirecting to /");
        return <Navigate to="/" replace />;
    }

    return (
        <Routes>
            <Route element={<AdminLayout />}>
                <Route index element={<Dashboard />} />
                <Route path="products" element={<ProductList />} />
                <Route path="orders" element={<OrderList />} />
                <Route path="promotions" element={<PromotionList />} />
                <Route path="reviews" element={<ReviewList />} />
                <Route path="inventory" element={<InventoryList />} />
                <Route path="prescriptions" element={<PrescriptionList />} />
                <Route path="consultations" element={<ConsultationList />} />
                <Route path="manufacturers-brands" element={<ManufacturersBrandsList />} />
                <Route path="categories" element={<CategoriesList />} />
                <Route path="banners" element={<BannerList />} />
                <Route path="users" element={<UserList />} />
                <Route path="branches" element={<BranchList />} />
                <Route path="staff" element={<StaffList />} />
                <Route path="revenue-report" element={<RevenueReport />} />
                <Route path="*" element={<div>404 Not Found - Admin</div>} />
            </Route>
        </Routes>
    );
};

export default AdminRoutes;