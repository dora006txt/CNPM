// src/pages/admin/PrescriptionList.jsx
import React, { useState } from "react";
import { FaCheck, FaTimes } from "react-icons/fa";

const PrescriptionList = () => {
    // Dữ liệu tĩnh cho prescriptions (dựa trên bảng Prescriptions)
    const staticPrescriptions = [
        {
            id: 1,
            user: { id: 1, fullName: "Cao Thi Thu", email: "thuthi732@gmail.com" },
            orderId: null,
            imageUrl: "https://example.com/prescriptions/prescription1.jpg",
            issueDate: "2025-04-20",
            doctorName: "BS. Nguyễn Văn A",
            clinicAddress: "Bệnh viện Bạch Mai, Hà Nội",
            diagnosis: "Cảm cúm thông thường",
            status: "pending_verification",
            verifiedByStaffId: null,
            verificationNotes: null,
            uploadedAt: "2025-04-20T10:00:00",
            updatedAt: "2025-04-20T10:00:00",
        },
        {
            id: 2,
            user: { id: 2, fullName: "Do Van Tu", email: "0l3vantuu7l0@gmail.com" },
            orderId: null,
            imageUrl: "https://example.com/prescriptions/prescription2.jpg",
            issueDate: "2025-04-21",
            doctorName: "BS. Trần Thị B",
            clinicAddress: "Bệnh viện Chợ Rẫy, TP.HCM",
            diagnosis: "Viêm họng cấp",
            status: "verified",
            verifiedByStaffId: 1,
            verificationNotes: "Đơn thuốc hợp lệ",
            uploadedAt: "2025-04-21T15:00:00",
            updatedAt: "2025-04-22T09:00:00",
        },
    ];

    const [prescriptions, setPrescriptions] = useState(staticPrescriptions);
    const [searchTerm, setSearchTerm] = useState("");
    const [filterStatus, setFilterStatus] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const filteredPrescriptions = prescriptions.filter((prescription) => {
        const matchesSearch = prescription.user.fullName
            .toLowerCase()
            .includes(searchTerm.toLowerCase());
        const matchesStatus = filterStatus
            ? prescription.status === filterStatus
            : true;
        return matchesSearch && matchesStatus;
    });

    console.log("Filtered Prescriptions:", filteredPrescriptions);

    const handleVerify = (prescriptionId, newStatus) => {
        setLoading(true);
        try {
            setPrescriptions(
                prescriptions.map((prescription) =>
                    prescription.id === prescriptionId
                        ? {
                            ...prescription,
                            status: newStatus,
                            verifiedByStaffId: 1, // Giả lập admin đang xác minh
                            verificationNotes:
                                newStatus === "verified"
                                    ? "Đơn thuốc hợp lệ"
                                    : "Đơn thuốc không hợp lệ",
                            updatedAt: new Date().toISOString(),
                        }
                        : prescription
                )
            );
        } catch (err) {
            setError("Có lỗi xảy ra khi xác minh đơn thuốc.");
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <h2 className="text-2xl font-bold mb-6">Quản lý Đơn thuốc</h2>

            {error && (
                <div className="alert alert-error mb-4">
                    <div className="flex-1">
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            fill="none"
                            viewBox="0 0 24 24"
                            className="w-6 h-6 mx-2 stroke-current"
                        >
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth="2"
                                d="M12 9v2m0 4h.01M12 2a10 10 0 100 20 10 10 0 000-20z"
                            ></path>
                        </svg>
                        <label>{error}</label>
                    </div>
                </div>
            )}

            <div className="mb-4 flex flex-col md:flex-row gap-4">
                <input
                    type="text"
                    placeholder="Tìm kiếm người dùng..."
                    className="input input-bordered w-full md:w-1/3"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
                <select
                    className="select select-bordered w-full md:w-1/4"
                    value={filterStatus}
                    onChange={(e) => setFilterStatus(e.target.value)}
                >
                    <option value="">Tất cả trạng thái</option>
                    <option value="pending_verification">Chờ xác minh</option>
                    <option value="verified">Đã xác minh</option>
                    <option value="rejected">Bị từ chối</option>
                    <option value="used">Đã sử dụng</option>
                </select>
            </div>

            <div className="overflow-x-auto">
                {loading ? (
                    <div className="flex justify-center">
                        <span className="loading loading-spinner loading-lg"></span>
                    </div>
                ) : (
                    <table className="table w-full">
                        <thead>
                            <tr>
                                <th>Người dùng</th>
                                <th>Email</th>
                                <th>Ảnh đơn thuốc</th>
                                <th>Ngày kê đơn</th>
                                <th>Bác sĩ</th>
                                <th>Chẩn đoán</th>
                                <th>Trạng thái</th>
                                <th>Ngày tải lên</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredPrescriptions.length === 0 ? (
                                <tr>
                                    <td colSpan="9" className="text-center">
                                        Không tìm thấy đơn thuốc.
                                    </td>
                                </tr>
                            ) : (
                                filteredPrescriptions.map((prescription) => (
                                    <tr key={prescription.id}>
                                        <td>{prescription.user.fullName}</td>
                                        <td>{prescription.user.email}</td>
                                        <td>
                                            <a
                                                href={prescription.imageUrl}
                                                target="_blank"
                                                rel="noopener noreferrer"
                                                className="text-blue-500 underline"
                                            >
                                                Xem ảnh
                                            </a>
                                        </td>
                                        <td>{prescription.issueDate}</td>
                                        <td>{prescription.doctorName}</td>
                                        <td>{prescription.diagnosis}</td>
                                        <td>
                                            <span
                                                className={`badge ${prescription.status === "verified"
                                                        ? "badge-success"
                                                        : prescription.status === "rejected"
                                                            ? "badge-error"
                                                            : "badge-warning"
                                                    }`}
                                            >
                                                {prescription.status === "pending_verification"
                                                    ? "Chờ xác minh"
                                                    : prescription.status === "verified"
                                                        ? "Đã xác minh"
                                                        : prescription.status === "rejected"
                                                            ? "Bị từ chối"
                                                            : "Đã sử dụng"}
                                            </span>
                                        </td>
                                        <td>{new Date(prescription.uploadedAt).toLocaleDateString()}</td>
                                        <td>
                                            {prescription.status === "pending_verification" && (
                                                <>
                                                    <button
                                                        className="btn btn-ghost btn-sm mr-2"
                                                        onClick={() => handleVerify(prescription.id, "verified")}
                                                        disabled={loading}
                                                    >
                                                        <FaCheck className="text-green-500" />
                                                    </button>
                                                    <button
                                                        className="btn btn-ghost btn-sm"
                                                        onClick={() => handleVerify(prescription.id, "rejected")}
                                                        disabled={loading}
                                                    >
                                                        <FaTimes className="text-red-500" />
                                                    </button>
                                                </>
                                            )}
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
};

export default PrescriptionList;