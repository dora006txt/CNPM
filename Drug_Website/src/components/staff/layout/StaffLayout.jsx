import React from "react";
import { Outlet } from "react-router-dom";
import StaffHeader from "./StaffHeader";
import StaffSidebar from "./StaffSidebar";

const StaffLayout = () => {
    return (
        <div className="flex min-h-screen">
            {/* Sidebar */}
            <StaffSidebar />

            {/* Main Content */}
            <div className="flex-1 flex flex-col">
                {/* Header */}
                <StaffHeader />

                {/* Content Area */}
                <main className="flex-1 p-6 bg-green-50">
                    <Outlet />
                </main>
            </div>
        </div>
    );
};

export default StaffLayout;