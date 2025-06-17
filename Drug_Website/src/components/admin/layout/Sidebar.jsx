import React, { useState } from "react";
import { NavLink } from "react-router-dom";
import { FaHome, FaBox, FaShoppingCart, FaUsers, FaStore, FaBars, FaTags, FaStar, FaBoxOpen, FaFileMedical, FaComments, FaUserMd, FaBuilding, FaChartLine, FaListAlt } from "react-icons/fa";

const Sidebar = () => {
    const [isOpen, setIsOpen] = useState(false);

    return (
        <>
            <button
                className="md:hidden fixed top-4 left-4 z-20 btn btn-ghost"
                onClick={() => setIsOpen(!isOpen)}
            >
                <FaBars size={24} />
            </button>

            <div
                className={`w-64 bg-base-200 min-h-screen p-4 fixed top-0 left-0 transition-transform duration-300 z-10 ${isOpen ? "translate-x-0" : "-translate-x-full"
                    } md:translate-x-0 md:static`}
            >
                <h2 className="text-2xl font-bold mb-6 text-primary">Admin Dashboard</h2>
                <ul className="menu menu-vertical gap-2">
                    <li>
                        <NavLink
                            to="/admin"
                            end
                            className={({ isActive }) =>
                                isActive
                                    ? "bg-primary text-white font-semibold rounded-lg"
                                    : "hover:bg-primary hover:text-white rounded-lg"
                            }
                            onClick={() => setIsOpen(false)}
                        >
                            <FaHome className="mr-2" /> Dashboard
                        </NavLink>
                    </li>
                    <li>
                        <NavLink
                            to="/admin/products"
                            className={({ isActive }) =>
                                isActive
                                    ? "bg-primary text-white font-semibold rounded-lg"
                                    : "hover:bg-primary hover:text-white rounded-lg"
                            }
                            onClick={() => setIsOpen(false)}
                        >
                            <FaBox className="mr-2" /> Sản phẩm
                        </NavLink>
                    </li>
                    <li>
                        <NavLink
                            to="/admin/orders"
                            className={({ isActive }) =>
                                isActive
                                    ? "bg-primary text-white font-semibold rounded-lg"
                                    : "hover:bg-primary hover:text-white rounded-lg"
                            }
                            onClick={() => setIsOpen(false)}
                        >
                            <FaShoppingCart className="mr-2" /> Đơn hàng
                        </NavLink>
                    </li>
                    <li>
                        <NavLink
                            to="/admin/users"
                            className={({ isActive }) =>
                                isActive
                                    ? "bg-primary text-white font-semibold rounded-lg"
                                    : "hover:bg-primary hover:text-white rounded-lg"
                            }
                            onClick={() => setIsOpen(false)}
                        >
                            <FaUsers className="mr-2" /> Người dùng
                        </NavLink>
                    </li>
                    <li>
                        <NavLink
                            to="/admin/branches"
                            className={({ isActive }) =>
                                isActive
                                    ? "bg-primary text-white font-semibold rounded-lg"
                                    : "hover:bg-primary hover:text-white rounded-lg"
                            }
                            onClick={() => setIsOpen(false)}
                        >
                            <FaStore className="mr-2" /> Chi nhánh
                        </NavLink>
                    </li>
                    <li>
                        <NavLink
                            to="/admin/staff"
                            className={({ isActive }) =>
                                isActive
                                    ? "bg-primary text-white font-semibold rounded-lg"
                                    : "hover:bg-primary hover:text-white rounded-lg"
                            }
                            onClick={() => setIsOpen(false)}
                        >
                            <FaUserMd className="mr-2" /> Nhân viên
                        </NavLink>
                    </li>
                    <li>
                        <NavLink
                            to="/admin/promotions"
                            className={({ isActive }) =>
                                isActive
                                    ? "bg-primary text-white font-semibold rounded-lg"
                                    : "hover:bg-primary hover:text-white rounded-lg"
                            }
                            onClick={() => setIsOpen(false)}
                        >
                            <FaTags className="mr-2" /> Khuyến mãi
                        </NavLink>
                    </li>
                    <li>
                        <NavLink
                            to="/admin/reviews"
                            className={({ isActive }) =>
                                isActive
                                    ? "bg-primary text-white font-semibold rounded-lg"
                                    : "hover:bg-primary hover:text-white rounded-lg"
                            }
                            onClick={() => setIsOpen(false)}
                        >
                            <FaStar className="mr-2" /> Đánh giá
                        </NavLink>
                    </li>
                    <li>
                        <NavLink
                            to="/admin/inventory"
                            className={({ isActive }) =>
                                isActive
                                    ? "bg-primary text-white font-semibold rounded-lg"
                                    : "hover:bg-primary hover:text-white rounded-lg"
                            }
                            onClick={() => setIsOpen(false)}
                        >
                            <FaBoxOpen className="mr-2" /> Kho hàng
                        </NavLink>
                    </li>
                    <li>
                        <NavLink
                            to="/admin/prescriptions"
                            className={({ isActive }) =>
                                isActive
                                    ? "bg-primary text-white font-semibold rounded-lg"
                                    : "hover:bg-primary hover:text-white rounded-lg"
                            }
                            onClick={() => setIsOpen(false)}
                        >
                            <FaFileMedical className="mr-2" /> Đơn thuốc
                        </NavLink>
                    </li>
                    <li>
                        <NavLink
                            to="/admin/consultations"
                            className={({ isActive }) =>
                                isActive
                                    ? "bg-primary text-white font-semibold rounded-lg"
                                    : "hover:bg-primary hover:text-white rounded-lg"
                            }
                            onClick={() => setIsOpen(false)}
                        >
                            <FaComments className="mr-2" /> Tư vấn
                        </NavLink>
                    </li>
                    <li>
                        <NavLink
                            to="/admin/manufacturers-brands"
                            className={({ isActive }) =>
                                isActive
                                    ? "bg-primary text-white font-semibold rounded-lg"
                                    : "hover:bg-primary hover:text-white rounded-lg"
                            }
                            onClick={() => setIsOpen(false)}
                        >
                            <FaBuilding className="mr-2" /> Nhà sản xuất & Thương hiệu
                        </NavLink>
                    </li>
                    <li>
                        <NavLink
                            to="/admin/revenue-report"
                            className={({ isActive }) =>
                                isActive
                                    ? "bg-primary text-white font-semibold rounded-lg"
                                    : "hover:bg-primary hover:text-white rounded-lg"
                            }
                            onClick={() => setIsOpen(false)}
                        >
                            <FaChartLine className="mr-2" /> Báo cáo Doanh thu
                        </NavLink>
                    </li>
                    <li>
                        <NavLink
                            to="/admin/categories"
                            className={({ isActive }) =>
                                isActive
                                    ? "bg-primary text-white font-semibold rounded-lg"
                                    : "hover:bg-primary hover:text-white rounded-lg"
                            }
                            onClick={() => setIsOpen(false)}
                        >
                            <FaListAlt className="mr-2" /> Danh Mục
                        </NavLink>
                    </li>
                    <li>
                        <NavLink
                            to="/admin/banners"
                            className={({ isActive }) =>
                                isActive
                                    ? "bg-primary text-white font-semibold rounded-lg"
                                    : "hover:bg-primary hover:text-white rounded-lg"
                            }
                            onClick={() => setIsOpen(false)}
                        >
                            <FaListAlt className="mr-2" /> Bảng Quảng Cáo
                        </NavLink>
                    </li>
                </ul>
            </div>

            {isOpen && (
                <div
                    className="fixed inset-0 bg-black bg-opacity-50 md:hidden z-0"
                    onClick={() => setIsOpen(false)}
                />
            )}
        </>
    );
};

export default Sidebar;