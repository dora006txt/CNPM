import React, { useState, useEffect } from "react";
import axios from "axios";
import { Link } from "react-router-dom";

const BannerList = () => {
    const [banners, setBanners] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [newBanner, setNewBanner] = useState({
        name: "",
        imageUrl: "",
        targetUrl: "",
        startDate: "",
        endDate: "",
        isActive: true,
        displayOrder: 1,
    });
    const [editingBanner, setEditingBanner] = useState(null);

    // Lấy danh sách banner
    const fetchBanners = async () => {
        setLoading(true);
        try {
            const token = localStorage.getItem("token");
            const response = await axios.get("http://localhost:8080/api/admin/banners", {
                headers: { Authorization: `Bearer ${token}` },
            });
            setBanners(response.data);
        } catch (err) {
            setError(err.response?.data?.message || "Lỗi khi tải danh sách banner.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchBanners();
    }, []);

    // Thêm banner
    const handleCreateBanner = async (e) => {
        e.preventDefault();
        setError("");
        try {
            const token = localStorage.getItem("token");
            const response = await axios.post("http://localhost:8080/api/admin/banners", newBanner, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setBanners([...banners, response.data]);
            setNewBanner({
                name: "",
                imageUrl: "",
                targetUrl: "",
                startDate: "",
                endDate: "",
                isActive: true,
                displayOrder: 1,
            });
        } catch (err) {
            setError(err.response?.data?.message || "Lỗi khi tạo banner.");
        }
    };

    // Cập nhật banner
    const handleUpdateBanner = async (e) => {
        e.preventDefault();
        setError("");
        try {
            const token = localStorage.getItem("token");
            const response = await axios.put(
                `http://localhost:8080/api/admin/banners/${editingBanner.id}`,
                {
                    name: editingBanner.name,
                    imageUrl: editingBanner.imageUrl,
                    targetUrl: editingBanner.targetUrl,
                    startDate: editingBanner.startDate,
                    endDate: editingBanner.endDate,
                    isActive: editingBanner.active,
                    displayOrder: editingBanner.displayOrder,
                },
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            );
            setBanners(banners.map((banner) => (banner.id === response.data.id ? response.data : banner)));
            setEditingBanner(null);
        } catch (err) {
            setError(err.response?.data?.message || "Lỗi khi cập nhật banner.");
        }
    };

    // Xóa banner
    const handleDeleteBanner = async (id) => {
        if (!window.confirm("Bạn có chắc muốn xóa banner này?")) return;
        try {
            const token = localStorage.getItem("token");
            await axios.delete(`http://localhost:8080/api/admin/banners/${id}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setBanners(banners.filter((banner) => banner.id !== id));
        } catch (err) {
            setError(err.response?.data?.message || "Lỗi khi xóa banner.");
        }
    };

    // Chọn banner để sửa
    const handleEditBanner = (banner) => {
        setEditingBanner({ ...banner });
    };

    // Format ngày
    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleString("vi-VN");
    };

    return (
        <div className="p-6">
            <h2 className="text-2xl font-bold mb-4 text-blue-600 dark:text-blue-400">Quản lý Banner</h2>

            {error && (
                <div className="alert alert-error mb-4 bg-red-100 text-red-700 p-3 rounded-lg">
                    <span>{error}</span>
                </div>
            )}

            {/* Form tạo banner */}
            <div className="mb-6">
                <h3 className="text-xl font-semibold mb-2">Thêm Banner Mới</h3>
                <form onSubmit={handleCreateBanner} className="bg-gray-100 dark:bg-gray-800 p-4 rounded-lg">
                    <div className="mb-4">
                        <label className="block text-gray-700 dark:text-gray-300 mb-2">Tên Banner</label>
                        <input
                            type="text"
                            value={newBanner.name}
                            onChange={(e) => setNewBanner({ ...newBanner, name: e.target.value })}
                            className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200"
                            required
                        />
                    </div>
                    <div className="mb-4">
                        <label className="block text-gray-700 dark:text-gray-300 mb-2">URL Hình Ảnh</label>
                        <input
                            type="url"
                            value={newBanner.imageUrl}
                            onChange={(e) => setNewBanner({ ...newBanner, imageUrl: e.target.value })}
                            className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200"
                            required
                        />
                    </div>
                    <div className="mb-4">
                        <label className="block text-gray-700 dark:text-gray-300 mb-2">URL Đích</label>
                        <input
                            type="url"
                            value={newBanner.targetUrl}
                            onChange={(e) => setNewBanner({ ...newBanner, targetUrl: e.target.value })}
                            className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200"
                            required
                        />
                    </div>
                    <div className="mb-4">
                        <label className="block text-gray-700 dark:text-gray-300 mb-2">Ngày Bắt Đầu</label>
                        <input
                            type="datetime-local"
                            value={newBanner.startDate}
                            onChange={(e) => setNewBanner({ ...newBanner, startDate: e.target.value })}
                            className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200"
                            required
                        />
                    </div>
                    <div className="mb-4">
                        <label className="block text-gray-700 dark:text-gray-300 mb-2">Ngày Kết Thúc</label>
                        <input
                            type="datetime-local"
                            value={newBanner.endDate}
                            onChange={(e) => setNewBanner({ ...newBanner, endDate: e.target.value })}
                            className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200"
                            required
                        />
                    </div>
                    <div className="mb-4">
                        <label className="block text-gray-700 dark:text-gray-300 mb-2">Trạng Thái</label>
                        <select
                            value={newBanner.isActive}
                            onChange={(e) => setNewBanner({ ...newBanner, isActive: e.target.value === "true" })}
                            className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200"
                        >
                            <option value="true">Kích Hoạt</option>
                            <option value="false">Không Kích Hoạt</option>
                        </select>
                    </div>
                    <div className="mb-4">
                        <label className="block text-gray-700 dark:text-gray-300 mb-2">Thứ Tự Hiển Thị</label>
                        <input
                            type="number"
                            value={newBanner.displayOrder}
                            onChange={(e) => setNewBanner({ ...newBanner, displayOrder: parseInt(e.target.value) })}
                            className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200"
                            required
                        />
                    </div>
                    <button
                        type="submit"
                        className="bg-white text-white py-2 px-4 rounded-lg hover:bg-blue-700"
                    >
                        Thêm Banner
                    </button>
                </form>
            </div>

            {/* Form sửa banner */}
            {editingBanner && (
                <div className="mb-6">
                    <h3 className="text-xl font-semibold mb-2">Sửa Banner #{editingBanner.id}</h3>
                    <form onSubmit={handleUpdateBanner} className="bg-gray-100 dark:bg-gray-800 p-4 rounded-lg">
                        <div className="mb-4">
                            <label className="block text-gray-700 dark:text-gray-300 mb-2">Tên Banner</label>
                            <input
                                type="text"
                                value={editingBanner.name}
                                onChange={(e) => setEditingBanner({ ...editingBanner, name: e.target.value })}
                                className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200"
                                required
                            />
                        </div>
                        <div className="mb-4">
                            <label className="block text-gray-700 dark:text-gray-300 mb-2">URL Hình Ảnh</label>
                            <input
                                type="url"
                                value={editingBanner.imageUrl}
                                onChange={(e) => setEditingBanner({ ...editingBanner, imageUrl: e.target.value })}
                                className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200"
                                required
                            />
                        </div>
                        <div className="mb-4">
                            <label className="block text-gray-700 dark:text-gray-300 mb-2">URL Đích</label>
                            <input
                                type="url"
                                value={editingBanner.targetUrl}
                                onChange={(e) => setEditingBanner({ ...editingBanner, targetUrl: e.target.value })}
                                className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200"
                                required
                            />
                        </div>
                        <div className="mb-4">
                            <label className="block text-gray-700 dark:text-gray-300 mb-2">Ngày Bắt Đầu</label>
                            <input
                                type="datetime-local"
                                value={editingBanner.startDate.slice(0, 16)}
                                onChange={(e) => setEditingBanner({ ...editingBanner, startDate: e.target.value })}
                                className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200"
                                required
                            />
                        </div>
                        <div className="mb-4">
                            <label className="block text-gray-700 dark:text-gray-300 mb-2">Ngày Kết Thúc</label>
                            <input
                                type="datetime-local"
                                value={editingBanner.endDate.slice(0, 16)}
                                onChange={(e) => setEditingBanner({ ...editingBanner, endDate: e.target.value })}
                                className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200"
                                required
                            />
                        </div>
                        <div className="mb-4">
                            <label className="block text-gray-700 dark:text-gray-300 mb-2">Trạng Thái</label>
                            <select
                                value={editingBanner.active}
                                onChange={(e) => setEditingBanner({ ...editingBanner, active: e.target.value === "true" })}
                                className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200"
                            >
                                <option value="true">Kích Hoạt</option>
                                <option value="false">Không Kích Hoạt</option>
                            </select>
                        </div>
                        <div className="mb-4">
                            <label className="block text-gray-700 dark:text-gray-300 mb-2">Thứ Tự Hiển Thị</label>
                            <input
                                type="number"
                                value={editingBanner.displayOrder}
                                onChange={(e) => setEditingBanner({ ...editingBanner, displayOrder: parseInt(e.target.value) })}
                                className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200"
                                required
                            />
                        </div>
                        <button
                            type="submit"
                            className="bg-white text-white py-2 px-4 rounded-lg hover:bg-blue-700 mr-2"
                        >
                            Cập Nhật
                        </button>
                        <button
                            type="button"
                            onClick={() => setEditingBanner(null)}
                            className="bg-gray-500 text-white py-2 px-4 rounded-lg hover:bg-gray-600"
                        >
                            Hủy
                        </button>
                    </form>
                </div>
            )}

            {/* Danh sách banner */}
            <div>
                <h3 className="text-xl font-semibold mb-2">Danh Sách Banner</h3>
                {loading ? (
                    <div>Loading...</div>
                ) : (
                    <table className="min-w-full border-collapse border border-gray-300 dark:border-gray-600">
                        <thead>
                            <tr className="bg-gray-200 dark:bg-gray-700">
                                <th className="border px-4 py-2 text-gray-700 dark:text-gray-200">ID</th>
                                <th className="border px-4 py-2 text-gray-700 dark:text-gray-200">Tên</th>
                                <th className="border px-4 py-2 text-gray-700 dark:text-gray-200">Hình Ảnh</th>
                                <th className="border px-4 py-2 text-gray-700 dark:text-gray-200">Ngày Bắt Đầu</th>
                                <th className="border px-4 py-2 text-gray-700 dark:text-gray-200">Ngày Kết Thúc</th>
                                <th className="border px-4 py-2 text-gray-700 dark:text-gray-200">Trạng Thái</th>
                                <th className="border px-4 py-2 text-gray-700 dark:text-gray-200">Thứ Tự</th>
                                <th className="border px-4 py-2 text-gray-700 dark:text-gray-200">Hành Động</th>
                            </tr>
                        </thead>
                        <tbody>
                            {banners.map((banner) => (
                                <tr key={banner.id} className="hover:bg-gray-100 dark:hover:bg-gray-800">
                                    <td className="border px-4 py-2 text-gray-900 dark:text-gray-200">{banner.id}</td>
                                    <td className="border px-4 py-2 text-gray-900 dark:text-gray-200">{banner.name}</td>
                                    <td className="border px-4 py-2 text-gray-900 dark:text-gray-200">
                                        <a href={banner.imageUrl} target="_blank" rel="noopener noreferrer" className="text-blue-600 dark:text-blue-400 hover:underline">
                                            Xem hình
                                        </a>
                                    </td>
                                    <td className="border px-4 py-2 text-gray-900 dark:text-gray-200">{formatDate(banner.startDate)}</td>
                                    <td className="border px-4 py-2 text-gray-900 dark:text-gray-200">{formatDate(banner.endDate)}</td>
                                    <td className="border px-4 py-2 text-gray-900 dark:text-gray-200">{banner.active ? "Kích Hoạt" : "Không Kích Hoạt"}</td>
                                    <td className="border px-4 py-2 text-gray-900 dark:text-gray-200">{banner.displayOrder}</td>
                                    <td className="border px-4 py-2 text-gray-900 dark:text-gray-200">
                                        <button
                                            onClick={() => handleEditBanner(banner)}
                                            className="bg-yellow-500 text-white py-1 px-2 rounded-lg hover:bg-yellow-600 mr-2"
                                        >
                                            Sửa
                                        </button>
                                        <button
                                            onClick={() => handleDeleteBanner(banner.id)}
                                            className="bg-red-500 text-white py-1 px-2 rounded-lg hover:bg-red-600"
                                        >
                                            Xóa
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
};

export default BannerList;