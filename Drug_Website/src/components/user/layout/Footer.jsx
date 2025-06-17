// src/components/layout/Footer.jsx
import React from "react";
import { Link } from "react-router-dom";
import { FaFacebook, FaInstagram, FaTwitter } from "react-icons/fa";

const Footer = () => {
  return (
    <footer className="bg-white text-black py-6 border-t border-gray-200">
      <div className="container mx-auto px-4">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          <div>
            <h3 className="text-lg font-bold mb-3 text-blue-600">Nhà Thuốc Online</h3>
            <p className="text-sm">Địa chỉ: 123 Đường Sức Khỏe, TP. Hà Nội</p>
            <p className="text-sm">Email: support@nhathuoconline.vn</p>
            <p className="text-sm">Hotline: 1900-1234</p>
            <p className="text-sm mt-2">
              <span className="font-semibold">Giấy phép:</span> Đăng ký kinh doanh số 123456789
            </p>
          </div>

          <div>
            <h3 className="text-lg font-bold mb-3 text-black">Hỗ trợ khách hàng</h3>
            <ul className="space-y-2">
              <li>
                <Link to="/return-policy" className="text-sm hover:text-blue-600">
                  Chính sách đổi trả
                </Link>
              </li>
              <li>
                <Link to="/payment-guide" className="text-sm hover:text-blue-600">
                  Hướng dẫn thanh toán
                </Link>
              </li>
              <li>
                <Link to="/shipping-guide" className="text-sm hover:text-blue-600">
                  Hướng dẫn giao hàng
                </Link>
              </li>
              <li>
                <Link to="/faq" className="text-sm hover:text-blue-600">
                  Câu hỏi thường gặp
                </Link>
              </li>
            </ul>
          </div>

          <div>
            <h3 className="text-lg font-bold mb-3 text-black">Thông tin</h3>
            <ul className="space-y-2">
              <li>
                <Link to="/about-us" className="text-sm hover:text-blue-600">
                  Về chúng tôi
                </Link>
              </li>
              <li>
                <Link to="/pharmacy-locator" className="text-sm hover:text-blue-600">
                  Hệ thống nhà thuốc
                </Link>
              </li>
              <li>
                <Link to="/news" className="text-sm hover:text-blue-600">
                  Tin tức
                </Link>
              </li>
              <li>
                <Link to="/careers" className="text-sm hover:text-blue-600">
                  Tuyển dụng
                </Link>
              </li>
            </ul>
          </div>

          <div>
            <h3 className="text-lg font-bold mb-3 text-black">Theo dõi chúng tôi</h3>
            <div className="flex space-x-4">
              <a
                href="https://facebook.com"
                target="_blank"
                rel="noopener noreferrer"
                className="text-blue-600 hover:text-blue-800"
              >
                <FaFacebook size={24} />
              </a>
              <a
                href="https://instagram.com"
                target="_blank"
                rel="noopener noreferrer"
                className="text-pink-600 hover:text-pink-800"
              >
                <FaInstagram size={24} />
              </a>
              <a
                href="https://twitter.com"
                target="_blank"
                rel="noopener noreferrer"
                className="text-blue-400 hover:text-blue-600"
              >
                <FaTwitter size={24} />
              </a>
            </div>
          </div>
        </div>

        <div className="text-center mt-6 border-t border-gray-200 pt-4">
          <p className="text-sm">
            © 2025 Công ty Cổ phần Dược phẩm Online. All rights reserved.
          </p>
          <p className="text-sm mt-1">
            Được chứng nhận bởi{" "}
            <a
              href="http://online.gov.vn"
              target="_blank"
              rel="noopener noreferrer"
              className="text-blue-600 hover:underline"
            >
              Bộ Công Thương
            </a>
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;