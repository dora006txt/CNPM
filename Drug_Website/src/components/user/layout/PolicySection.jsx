// src/components/layout/PolicySection.jsx
import React from "react";
import { Link } from "react-router-dom";
import { FaShieldAlt, FaBox, FaCheck, FaTruck } from "react-icons/fa";

const PolicySection = () => {
    return (
        <section className="bg-[#D1E5F4] py-4">
            <div className="container mx-auto px-4 flex flex-col md:flex-row justify-between items-center text-center md:text-left">
                {/* Ô 1: Thuốc chính hãng */}
                <div className="flex flex-col items-center mb-4 md:mb-0 md:mr-4">
                    <FaShieldAlt className="text-blue-600 text-2xl mb-2" />
                    <p className="text-sm font-medium text-black">
                        Thuốc chính hãng <br /> đảm bảo chất lượng
                    </p>
                </div>

                {/* Ô 2: Đổi trả trong 30 ngày */}
                <div className="flex flex-col items-center mb-4 md:mb-0 md:mr-4">
                    <FaBox className="text-blue-600 text-2xl mb-2" />
                    <p className="text-sm font-medium text-black">
                        Đổi trả trong 30 ngày <br /> khi chưa mở mua hàng
                    </p>
                </div>

                {/* Ô 3: Cam kết 100% */}
                <div className="flex flex-col items-center mb-4 md:mb-0 md:mr-4">
                    <FaCheck className="text-blue-600 text-2xl mb-2" />
                    <p className="text-sm font-medium text-black">
                        Cam kết 100% <br /> chat tư vấn dược sĩ
                    </p>
                </div>

                {/* Ô 4: Miễn phí vận chuyển */}
                <div className="flex flex-col items-center mb-4 md:mb-0">
                    <FaTruck className="text-blue-600 text-2xl mb-2" />
                    <p className="text-sm font-medium text-black">
                        Miễn phí vận chuyển <br /> theo chỉ định bác sĩ
                    </p>
                </div>
            </div>

            {/* Phần liên kết nhà thuốc */}
            <div className="bg-blue-600 py-3 mt-4">
                <div className="container mx-auto px-4 flex flex-col md:flex-row justify-between items-center text-white">
                    <Link
                        to="/pharmacy-locator"
                        className="text-sm font-medium hover:underline mb-2 md:mb-0"
                    >
                        <span className="mr-2">📍</span> Xem hệ thống 2080 nhà thuốc trên toàn quốc
                    </Link>
                    <Link
                        to="/pharmacy-locator"
                        className="btn btn-ghost bg-white text-blue-600 hover:bg-gray-200 rounded-full px-4 py-2"
                    >
                        Xem danh sách nhà thuốc
                    </Link>
                </div>
            </div>
        </section>
    );
};

export default PolicySection;