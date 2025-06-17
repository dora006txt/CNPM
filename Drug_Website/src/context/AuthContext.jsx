import React, { createContext, useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";
import { toast } from "react-toastify";

const AuthContext = createContext();

const AuthProvider = ({ children }) => {
    const navigate = useNavigate();
    const location = useLocation();
    const [user, setUser] = useState(null);
    const [sessionTimeout, setSessionTimeout] = useState(null);
    const [hasShownToast, setHasShownToast] = useState(false);

    useEffect(() => {
        const token = localStorage.getItem("token");
        const roleId = localStorage.getItem("role_id");
        const timeout = localStorage.getItem("sessionTimeout");
        console.log("Auth useEffect triggered on mount:", { token, roleId, timeout });

        if (token && roleId) {
            setUser({ token, roleId: parseInt(roleId) });
            axios
                .get("http://localhost:8080/api/users/me", {
                    headers: { Authorization: `Bearer ${token}` },
                })
                .then((response) => {
                    console.log("User profile fetched:", response.data);
                    setUser({ ...response.data, token, roleId: parseInt(roleId) });
                })
                .catch((err) => {
                    console.error("Token validation failed:", err.response?.status, err.response?.data);
                });
        } else {
            setUser(null);
            console.log("No token or roleId found, user set to null");
        }

        if (timeout && parseInt(localStorage.getItem("role_id")) === 1) {
            setSessionTimeout(parseInt(timeout));
        }
    }, []);

    useEffect(() => {
        if (!sessionTimeout || !user || user.roleId !== 1 || hasShownToast) return;

        let interval;
        const checkSessionTimeout = () => {
            const currentTime = Date.now();
            if (currentTime >= sessionTimeout) {
                console.log("Phiên đăng nhập đã hết hạn (khách hàng)");
                toast.error("Đã hết hạn phiên đăng nhập, vui lòng đăng nhập lại", {
                    autoClose: 3000,
                    hideProgressBar: false,
                    closeOnClick: true,
                    pauseOnHover: true,
                    draggable: true,
                });
                setHasShownToast(true);
                clearInterval(interval);
                setTimeout(() => {
                    logout(false);
                }, 3000);
            }
        };

        interval = setInterval(checkSessionTimeout, 1000);
        checkSessionTimeout();

        return () => clearInterval(interval);
    }, [sessionTimeout, user, hasShownToast]);

    const login = (userData) => {
        const { token, roles } = userData;
        if (!token || !roles || !Array.isArray(roles) || roles.length === 0) {
            console.error("Invalid login data:", userData);
            return false;
        }

        const role = roles[0].toUpperCase();
        const roleId = role === "ADMIN" ? 2 : role === "STAFF" ? 3 : 1;
        localStorage.setItem("token", token);
        localStorage.setItem("role_id", roleId);

        if (roleId === 1) {
            const timeout = Date.now() + 30 * 60 * 1000;
            localStorage.setItem("sessionTimeout", timeout.toString());
            setSessionTimeout(timeout);
            setHasShownToast(false);
        } else {
            localStorage.removeItem("sessionTimeout");
            setSessionTimeout(null);
            setHasShownToast(false);
        }

        setUser({ ...userData, roleId });

        const from = location.state?.from || "/";
        setTimeout(() => {
            if (roleId === 1) {
                navigate(from, { replace: true });
            } else if (roleId === 2) {
                navigate("/admin", { replace: true });
            } else if (roleId === 3) {
                navigate("/staff", { replace: true }); // Điều hướng STAFF về /staff
            }
        }, 100);

        console.log("Login successful, user:", { roleId, from });
        return true;
    };

    const logout = (redirectHome = false) => {
        localStorage.removeItem("token");
        localStorage.removeItem("role_id");
        localStorage.removeItem("sessionTimeout");
        localStorage.removeItem("encryptedCredentials");
        setUser(null);
        setSessionTimeout(null);
        setHasShownToast(false);
        if (redirectHome) {
            console.log("Đăng xuất chủ động, chuyển hướng về trang chủ");
            navigate("/", { replace: true });
        } else {
            console.log("Đăng xuất, ở lại trang hiện tại:", location.pathname);
        }
    };

    const isAdminOrStaff = () => user && (user.roleId === 2 || user.roleId === 3);

    return (
        <AuthContext.Provider value={{ user, login, logout, setSessionTimeout, isAdminOrStaff }}>
            {children}
        </AuthContext.Provider>
    );
};

export { AuthContext, AuthProvider };
export const useAuth = () => React.useContext(AuthContext);