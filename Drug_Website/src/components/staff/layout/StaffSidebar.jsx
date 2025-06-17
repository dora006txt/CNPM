import React, { useState } from "react";
import { NavLink } from "react-router-dom";
import { FaHome, FaBox, FaShoppingCart, FaBoxOpen, FaFileMedical, FaComments, FaBars } from "react-icons/fa";

const StaffSidebar = () => {
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
                className={`w-64 bg-green-700 min-h-screen p-4 fixed top-0 left-0 transition-transform duration-300 z-10 ${isOpen ? "translate-x-0" : "-translate-x-full"
                    } md:translate-x-0 md:static`}
            >
                <h2 className="text-2xl font-bold mb-6 text-white">Staff Dashboard</h2>
                <ul className="menu menu-vertical gap-2">
                    <li>
                        <NavLink
                            to="/staff"
                            end
                            className={({ isActive }) =>
                                isActive
                                    ? "bg-green-500 text-white font-semibold rounded-lg"
                                    : "hover:bg-green-500 hover:text-white rounded-lg text-white"
                            }
                            onClick={() => setIsOpen(false)}
                        >
                            <FaHome className="mr-2" /> Dashboard
                        </NavLink>
                    </li>
                    <li>
                        <NavLink
                            to="/staff/products"
                            className={({ isActive }) =>
                                isActive
                                    ? "bg-green-500 text-white font-semibold rounded-lg"
                                    : "hover:bg-green-500 hover:text-white rounded-lg text-white"
                            }
                            onClick={() => setIsOpen(false)}
                        >
                            <FaBox className="mr-2" /> Sản phẩm
                        </NavLink>
                    </li>
                    <li>
                        <NavLink
                            to="/staff/orders"
                            className={({ isActive }) =>
                                isActive
                                    ? "bg-green-500 text-white font-semibold rounded-lg"
                                    : "hover:bg-green-500 hover:text-white rounded-lg text-white"
                            }
                            onClick={() => setIsOpen(false)}
                        >
                            <FaShoppingCart className="mr-2" /> Đơn hàng
                        </NavLink>
                    </li>
                    <li>
                        <NavLink
                            to="/staff/inventory"
                            className={({ isActive }) =>
                                isActive
                                    ? "bg-green-500 text-white font-semibold rounded-lg"
                                    : "hover:bg-green-500 hover:text-white rounded-lg text-white"
                            }
                            onClick={() => setIsOpen(false)}
                        >
                            <FaBoxOpen className="mr-2" /> Kho hàng
                        </NavLink>
                    </li>
                    <li>
                        <NavLink
                            to="/staff/prescriptions"
                            className={({ isActive }) =>
                                isActive
                                    ? "bg-green-500 text-white font-semibold rounded-lg"
                                    : "hover:bg-green-500 hover:text-white rounded-lg text-white"
                            }
                            onClick={() => setIsOpen(false)}
                        >
                            <FaFileMedical className="mr-2" /> Đơn thuốc
                        </NavLink>
                    </li>
                    <li>
                        <NavLink
                            to="/staff/consultations"
                            className={({ isActive }) =>
                                isActive
                                    ? "bg-green-500 text-white font-semibold rounded-lg"
                                    : "hover:bg-green-500 hover:text-white rounded-lg text-white"
                            }
                            onClick={() => setIsOpen(false)}
                        >
                            <FaComments className="mr-2" /> Tư vấn
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

export default StaffSidebar;