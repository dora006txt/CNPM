// src/components/layout/UserLayout.jsx
import React from "react";
import { Outlet } from "react-router-dom";
import Navbar from "./Navbar";
import Footer from "./Footer";
import PolicySection from "./PolicySection";
const UserLayout = () => {
    return (
        <div className="min-h-screen flex flex-col">
            <Navbar />
            <main className="flex-grow bg-[#E6F0FA]">
                <Outlet /> {/* Nội dung của các trang sẽ được render ở đây */}
            </main>
            <PolicySection />
            <Footer />
        </div>
    );
};

export default UserLayout;