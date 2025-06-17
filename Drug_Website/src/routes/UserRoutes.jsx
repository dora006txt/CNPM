import React from "react";
import { Routes, Route } from "react-router-dom";
import UserLayout from "../components/user/layout/UserLayout";
import HomePage from "../pages/user/HomePage";
import CartPage from "../pages/user/CartPage";
import ProductListPage from "../components/user/ProductListPage";
import CheckoutPage from "../pages/user/CheckoutPage";
import PrescriptionPage from "../pages/user/PrescriptionPage";
import LoginPage from "../features/auth/LoginPage";
import RegisterPage from "../features/auth/RegisterPage";
import ConsultationPage from "../pages/user/ConsultationPage";
import ProfilePage from "../pages/user/ProfilePage";
import ProductDetailPage from "../pages/user/ProductDetailPage";
import OrderHistoryPage from "../pages/user/OrderHistoryPage";
import ProtectedRoute from "../routes/ProtectedRoutes";
import ForgotPasswordPage from "../features/auth/ForgotPasswordPage";
import ChangePasswordPage from "../features/auth/ChangePasswordPage";
import PharmacyLocator from "../pages/user/PharmacyLocator";
import CategoriesPage from "../pages/user/CategoriesPage";

const UserRoutes = () => {
  return (
    <Routes>
      <Route element={<UserLayout />}>
        <Route index element={<HomePage />} />
        <Route path="products" element={<ProductListPage />} />
        <Route path="products/:id" element={<ProductDetailPage />} />
        <Route
          path="cart"
          element={
            <ProtectedRoute>
              <CartPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="checkout"
          element={
            <ProtectedRoute>
              <CheckoutPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="prescription"
          element={
            <ProtectedRoute>
              <PrescriptionPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="consultation"
          element={
            <ProtectedRoute>
              <ConsultationPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="profile"
          element={
            <ProtectedRoute>
              <ProfilePage />
            </ProtectedRoute>
          }
        />
        <Route
          path="order/:orderId"
          element={
            <ProtectedRoute>
              <OrderHistoryPage />
            </ProtectedRoute>
          }
        />
        <Route path="pharmacy-locator" element={<PharmacyLocator />} />
        <Route path="/categories/:slug" element={<CategoriesPage />} />
        <Route path="*" element={<div>404 Not Found - User</div>} />
      </Route>
      <Route path="login" element={<LoginPage />} />
      <Route path="register" element={<RegisterPage />} />
      <Route path="forgot-password" element={<ForgotPasswordPage />} />
      <Route path="change-password" element={<ChangePasswordPage />} />
    </Routes>
  );
};

export default UserRoutes;