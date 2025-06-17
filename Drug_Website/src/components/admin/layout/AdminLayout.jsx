import React from "react";
import { Outlet } from "react-router-dom";
import Header from "./Header";
import Sidebar from "./SideBar";

const AdminLayout = () => {
    return (
        <div className="flex min-h-screen">
            {/* Sidebar */}
            <Sidebar />

            {/* Main Content */}
            <div className="flex-1 flex flex-col">
                {/* Header */}
                <Header />

                {/* Content Area */}
                <main className="flex-1 p-6 bg-base-100">
                    <Outlet />
                </main>
            </div>
        </div>
    );
};

export default AdminLayout;