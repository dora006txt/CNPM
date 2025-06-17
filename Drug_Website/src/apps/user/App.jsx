import { BrowserRouter, Routes, Route } from "react-router-dom";
import UserRoutes from "../../routes/UserRoutes";
import AdminRoutes from "../../routes/AdminRoutes";
import StaffRoutes from "../../routes/StaffRoutes"; // Thêm StaffRoutes
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { CartProvider } from "../../context/CartContext";
import { AuthProvider } from "../../context/AuthContext";
import ConsultationButton from "../../pages/user/ConsultationButton";

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <CartProvider>
          <ToastContainer
            position="top-center"
            autoClose={3000}
            hideProgressBar={false}
            closeOnClick
            pauseOnHover
            draggable
            theme="colored"
          />
          <Routes>
            <Route path="/*" element={<UserRoutes />} />
            <Route path="/admin/*" element={<AdminRoutes />} />
            <Route path="/staff/*" element={<StaffRoutes />} /> {/* Thêm route cho Staff */}
            <Route path="*" element={<div>404 Not Found</div>} />
          </Routes>
          <ConsultationButton />
        </CartProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}