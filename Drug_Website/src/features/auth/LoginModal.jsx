// src/components/ui/LoginModal.jsx
import React from "react";

const LoginModal = ({ isOpen, onClose }) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center backdrop-blur-sm bg-black/50">
      <div className="bg-primary dark:bg-gray-900 rounded-xl shadow-xl p-6 w-full max-w-sm relative">
        <button
          onClick={onClose}
          className="absolute top-3 right-3 text-gray-500 hover:text-white"
        >
          ✖
        </button>

        <h2 className="text-xl font-semibold text-center mb-4 text-white">
          Drug Web Login
        </h2>

        <form className="space-y-4">
          <div>
            <label className="text-white block text-sm mb-1">Email</label>
            <input
              type="email"
              className="w-full px-3 py-2 rounded-md bg-gray-800 text-white border border-gray-700 focus:outline-none focus:ring focus:ring-blue-500"
              placeholder="example@email.com"
            />
          </div>
          <div>
            <label className="text-white block text-sm mb-1">Mật khẩu</label>
            <input
              type="password"
              className="w-full px-3 py-2 rounded-md bg-gray-800 text-white border border-gray-700 focus:outline-none focus:ring focus:ring-blue-500"
              placeholder="●●●●●●"
            />
          </div>
          <button
            type="submit"
            className="btn w-full bg-indigo-500 hover:bg-indigo-600 text-white font-semibold rounded py-2"
          >
            Đăng nhập
          </button>
        </form>

        <p className="mt-4 text-center text-sm text-white">
          Chưa có tài khoản? '<a href="/register ">Đăng ký</a>
        </p>
      </div>
      +{" "}
    </div>
  );
};

export default LoginModal;
