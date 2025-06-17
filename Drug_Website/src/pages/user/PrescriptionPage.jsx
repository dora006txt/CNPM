// src/pages/user/PrescriptionPage.jsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

const PrescriptionPage = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        fullName: "",
        phone: "",
        email: "",
        prescriptionImage: null,
    });
    const [previewImage, setPreviewImage] = useState(null);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setFormData({ ...formData, prescriptionImage: file });
            setPreviewImage(URL.createObjectURL(file));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const submissionData = new FormData();
            submissionData.append("fullName", formData.fullName);
            submissionData.append("phone", formData.phone);
            submissionData.append("email", formData.email);
            submissionData.append("prescriptionImage", formData.prescriptionImage);

            // Gửi dữ liệu lên BE (thay thế bằng API thực tế)
            console.log("Đơn thuốc:", Object.fromEntries(submissionData));
            // await axios.post("http://localhost:3001/api/prescriptions", submissionData);

            alert("Gửi đơn thuốc thành công!");
            navigate("/");
        } catch (error) {
            console.error("Lỗi khi gửi đơn thuốc:", error);
            alert("Đã có lỗi xảy ra, vui lòng thử lại!");
        }
    };

    return (
        <div className="min-h-screen bg-white dark:bg-gray-900">
            <div className="container mx-auto px-4 py-6">
                <h1 className="text-3xl font-bold mb-6 text-blue-600 dark:text-blue-400">Gửi đơn thuốc</h1>
                <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md">
                    <form onSubmit={handleSubmit}>
                        <div className="mb-4">
                            <label className="block text-gray-700 dark:text-gray-300 mb-2">Họ và tên</label>
                            <input
                                type="text"
                                name="fullName"
                                value={formData.fullName}
                                onChange={handleInputChange}
                                className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200"
                                required
                            />
                        </div>
                        <div className="mb-4">
                            <label className="block text-gray-700 dark:text-gray-300 mb-2">Số điện thoại</label>
                            <input
                                type="tel"
                                name="phone"
                                value={formData.phone}
                                onChange={handleInputChange}
                                className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200"
                                required
                            />
                        </div>
                        <div className="mb-4">
                            <label className="block text-gray-700 dark:text-gray-300 mb-2">Email</label>
                            <input
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleInputChange}
                                className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200"
                                required
                            />
                        </div>
                        <div className="mb-4">
                            <label className="block text-gray-700 dark:text-gray-300 mb-2">Hình ảnh đơn thuốc</label>
                            <input
                                type="file"
                                accept="image/*"
                                onChange={handleFileChange}
                                className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200"
                                required
                            />
                            {previewImage && (
                                <div className="mt-4">
                                    <img src={previewImage} alt="Preview" className="w-48 h-48 object-cover rounded-lg" />
                                </div>
                            )}
                        </div>
                        <button
                            type="submit"
                            className="btn btn-primary w-full bg-white hover:bg-blue-700 text-white"
                        >
                            Gửi đơn thuốc
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default PrescriptionPage;