// src/pages/admin/ConsultationList.jsx
import React, { useState } from "react";
import { FaUserPlus, FaCheck, FaTimes } from "react-icons/fa";
import { Dialog, Transition } from "@headlessui/react";
import { Fragment } from "react";

const ConsultationList = () => {
    // Dữ liệu tĩnh cho consultation requests
    const staticConsultations = [
        {
            id: 1,
            user: { id: 1, fullName: "Cao Thi Thu", email: "thuthi732@gmail.com" },
            branch: { id: 1, name: "Chi nhánh Hà Nội" },
            staff: null,
            requestType: "phone",
            userMessage: "Tôi cần tư vấn về thuốc hạ sốt cho trẻ em.",
            status: "pending",
            assignedStaff: null,
            requestTime: "2025-04-20T10:00:00",
            lastUpdated: "2025-04-20T10:00:00",
        },
        {
            id: 2,
            user: { id: 2, fullName: "Do Van Tu", email: "0l3vantuu7l0@gmail.com" },
            branch: { id: 2, name: "Chi nhánh TP.HCM" },
            staff: null,
            requestType: "message",
            userMessage: "Tôi bị dị ứng, nên dùng thuốc gì?",
            status: "assigned",
            assignedStaff: { id: 1, fullName: "Nguyen Van A" },
            requestTime: "2025-04-21T15:00:00",
            lastUpdated: "2025-04-22T09:00:00",
        },
    ];

    const staffList = [
        { id: 1, fullName: "Nguyen Van A" },
        { id: 2, fullName: "Tran Thi B" },
    ];

    const [consultations, setConsultations] = useState(staticConsultations);
    const [searchTerm, setSearchTerm] = useState("");
    const [filterStatus, setFilterStatus] = useState("");
    const [isAssignModalOpen, setIsAssignModalOpen] = useState(false);
    const [currentConsultation, setCurrentConsultation] = useState(null);
    const [selectedStaff, setSelectedStaff] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const filteredConsultations = consultations.filter((consultation) => {
        const matchesSearch = consultation.user.fullName
            .toLowerCase()
            .includes(searchTerm.toLowerCase());
        const matchesStatus = filterStatus
            ? consultation.status === filterStatus
            : true;
        return matchesSearch && matchesStatus;
    });

    console.log("Filtered Consultations:", filteredConsultations);

    const openAssignModal = (consultation) => {
        setCurrentConsultation(consultation);
        setSelectedStaff("");
        setIsAssignModalOpen(true);
    };

    const closeAssignModal = () => {
        setIsAssignModalOpen(false);
        setCurrentConsultation(null);
    };

    const handleAssign = (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const staff = staffList.find((s) => s.id === parseInt(selectedStaff));
            setConsultations(
                consultations.map((consultation) =>
                    consultation.id === currentConsultation.id
                        ? {
                            ...consultation,
                            status: "assigned",
                            assignedStaff: staff,
                            lastUpdated: new Date().toISOString(),
                        }
                        : consultation
                )
            );
            closeAssignModal();
        } catch (err) {
            setError("Có lỗi xảy ra khi phân công nhân viên.");
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleUpdateStatus = (consultationId, newStatus) => {
        setLoading(true);
        try {
            setConsultations(
                consultations.map((consultation) =>
                    consultation.id === consultationId
                        ? {
                            ...consultation,
                            status: newStatus,
                            lastUpdated: new Date().toISOString(),
                        }
                        : consultation
                )
            );
        } catch (err) {
            setError("Có lỗi xảy ra khi cập nhật trạng thái.");
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <h2 className="text-2xl font-bold mb-6">Quản lý Tư vấn</h2>

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
                    <option value="pending">Chờ xử lý</option>
                    <option value="assigned">Đã phân công</option>
                    <option value="in_progress">Đang tiến hành</option>
                    <option value="completed">Hoàn thành</option>
                    <option value="cancelled">Đã hủy</option>
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
                                <th>Chi nhánh</th>
                                <th>Loại yêu cầu</th>
                                <th>Tin nhắn</th>
                                <th>Trạng thái</th>
                                <th>Nhân viên phụ trách</th>
                                <th>Thời gian yêu cầu</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredConsultations.length === 0 ? (
                                <tr>
                                    <td colSpan="9" className="text-center">
                                        Không tìm thấy yêu cầu tư vấn.
                                    </td>
                                </tr>
                            ) : (
                                filteredConsultations.map((consultation) => (
                                    <tr key={consultation.id}>
                                        <td>{consultation.user.fullName}</td>
                                        <td>{consultation.user.email}</td>
                                        <td>{consultation.branch.name}</td>
                                        <td>{consultation.requestType === "phone" ? "Điện thoại" : "Tin nhắn"}</td>
                                        <td>{consultation.userMessage}</td>
                                        <td>
                                            <span
                                                className={`badge ${consultation.status === "completed"
                                                        ? "badge-success"
                                                        : consultation.status === "cancelled"
                                                            ? "badge-error"
                                                            : consultation.status === "pending"
                                                                ? "badge-warning"
                                                                : "badge-info"
                                                    }`}
                                            >
                                                {consultation.status === "pending"
                                                    ? "Chờ xử lý"
                                                    : consultation.status === "assigned"
                                                        ? "Đã phân công"
                                                        : consultation.status === "in_progress"
                                                            ? "Đang tiến hành"
                                                            : consultation.status === "completed"
                                                                ? "Hoàn thành"
                                                                : "Đã hủy"}
                                            </span>
                                        </td>
                                        <td>
                                            {consultation.assignedStaff
                                                ? consultation.assignedStaff.fullName
                                                : "Chưa phân công"}
                                        </td>
                                        <td>{new Date(consultation.requestTime).toLocaleDateString()}</td>
                                        <td>
                                            {consultation.status === "pending" && (
                                                <button
                                                    className="btn btn-ghost btn-sm"
                                                    onClick={() => openAssignModal(consultation)}
                                                    disabled={loading}
                                                >
                                                    <FaUserPlus />
                                                </button>
                                            )}
                                            {consultation.status === "assigned" && (
                                                <>
                                                    <button
                                                        className="btn btn-ghost btn-sm mr-2"
                                                        onClick={() =>
                                                            handleUpdateStatus(consultation.id, "in_progress")
                                                        }
                                                        disabled={loading}
                                                    >
                                                        <FaCheck className="text-blue-500" />
                                                    </button>
                                                    <button
                                                        className="btn btn-ghost btn-sm"
                                                        onClick={() =>
                                                            handleUpdateStatus(consultation.id, "cancelled")
                                                        }
                                                        disabled={loading}
                                                    >
                                                        <FaTimes className="text-red-500" />
                                                    </button>
                                                </>
                                            )}
                                            {consultation.status === "in_progress" && (
                                                <>
                                                    <button
                                                        className="btn btn-ghost btn-sm mr-2"
                                                        onClick={() =>
                                                            handleUpdateStatus(consultation.id, "completed")
                                                        }
                                                        disabled={loading}
                                                    >
                                                        <FaCheck className="text-green-500" />
                                                    </button>
                                                    <button
                                                        className="btn btn-ghost btn-sm"
                                                        onClick={() =>
                                                            handleUpdateStatus(consultation.id, "cancelled")
                                                        }
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

            <Transition appear show={isAssignModalOpen} as={Fragment}>
                <Dialog as="div" className="relative z-10" onClose={closeAssignModal}>
                    <Transition.Child
                        as={Fragment}
                        enter="ease-out duration-300"
                        enterFrom="opacity-0"
                        enterTo="opacity-100"
                        leave="ease-in duration-200"
                        leaveFrom="opacity-100"
                        leaveTo="opacity-0"
                    >
                        <div className="fixed inset-0 bg-black bg-opacity-25" />
                    </Transition.Child>

                    <div className="fixed inset-0 overflow-y-auto">
                        <div className="flex min-h-full items-center justify-center p-4">
                            <Transition.Child
                                as={Fragment}
                                enter="ease-out duration-300"
                                enterFrom="opacity-0 scale-95"
                                enterTo="opacity-100 scale-100"
                                leave="ease-in duration-200"
                                leaveFrom="opacity-100 scale-100"
                                leaveTo="opacity-0 scale-95"
                            >
                                <Dialog.Panel className="w-full max-w-md transform overflow-hidden rounded-2xl bg-base-100 p-6 text-left align-middle shadow-xl transition-all">
                                    <Dialog.Title as="h3" className="text-lg font-medium leading-6">
                                        Phân công Nhân viên
                                    </Dialog.Title>
                                    <form onSubmit={handleAssign} className="mt-4">
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Người dùng: {currentConsultation?.user.fullName}
                                            </label>
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Yêu cầu: {currentConsultation?.userMessage}
                                            </label>
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Chọn nhân viên
                                            </label>
                                            <select
                                                className="select select-bordered w-full mt-1"
                                                value={selectedStaff}
                                                onChange={(e) => setSelectedStaff(e.target.value)}
                                                required
                                                disabled={loading}
                                            >
                                                <option value="">Chọn nhân viên</option>
                                                {staffList.map((staff) => (
                                                    <option key={staff.id} value={staff.id}>
                                                        {staff.fullName}
                                                    </option>
                                                ))}
                                            </select>
                                        </div>
                                        <div className="mt-4">
                                            <button
                                                type="submit"
                                                className="btn btn-primary w-full"
                                                disabled={loading}
                                            >
                                                Phân công
                                            </button>
                                        </div>
                                    </form>
                                </Dialog.Panel>
                            </Transition.Child>
                        </div>
                    </div>
                </Dialog>
            </Transition>
        </div>
    );
};

export default ConsultationList;