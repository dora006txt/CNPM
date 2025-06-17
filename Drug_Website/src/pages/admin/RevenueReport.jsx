// src/pages/admin/RevenueReport.jsx
import React, { useState, useEffect, Fragment } from "react";
import { Dialog, Transition } from "@headlessui/react";
import { FaFilter } from "react-icons/fa";
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";
import {
    LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer
} from "recharts";

const RevenueReport = () => {
    const [revenueData, setRevenueData] = useState([]);
    const [totalRevenue, setTotalRevenue] = useState(0);
    const [startDate, setStartDate] = useState("");
    const [endDate, setEndDate] = useState("");
    const [timeRange, setTimeRange] = useState("all");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [isFilterModalOpen, setIsFilterModalOpen] = useState(false);

    // Tạo dữ liệu mẫu (50 đơn hàng)
    useEffect(() => {
        const branches = ["Chi nhánh A", "Chi nhánh B", "Chi nhánh C"];
        const sampleData = Array.from({ length: 50 }, (_, i) => {
            const randomAmount = Math.floor(Math.random() * 10_000_000 + 1_000_000);
            const date = new Date();
            date.setDate(date.getDate() - i);
            return {
                id: i + 1,
                user: { fullName: `Khách ${i + 1}` },
                branch: { name: branches[i % branches.length] },
                totalAmount: randomAmount,
                status: i % 3 === 0 ? "completed" : "pending",
                createdAt: date.toISOString(),
                orderItems: [
                    {
                        id: i * 100 + 1,
                        product: { name: "Sản phẩm A" },
                        quantity: 2,
                        price: randomAmount / 2
                    },
                    {
                        id: i * 100 + 2,
                        product: { name: "Sản phẩm B" },
                        quantity: 1,
                        price: randomAmount / 2
                    }
                ]
            };
        });
        setRevenueData(sampleData);
        const total = sampleData.reduce((sum, item) => sum + item.totalAmount, 0);
        setTotalRevenue(total);
    }, []);

    const openFilterModal = () => setIsFilterModalOpen(true);
    const closeFilterModal = () => setIsFilterModalOpen(false);
    const handleFilterSubmit = (e) => {
        e.preventDefault();
        closeFilterModal();
    };

    const exportToExcel = () => {
        const worksheet = XLSX.utils.json_to_sheet(revenueData.map(order => ({
            ID: order.id,
            "Khách hàng": order.user?.fullName,
            "Chi nhánh": order.branch?.name,
            "Tổng tiền": order.totalAmount,
            "Trạng thái": order.status,
            "Ngày đặt hàng": new Date(order.createdAt).toLocaleDateString("vi-VN")
        })));
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, "DoanhThu");
        const excelBuffer = XLSX.write(workbook, { bookType: "xlsx", type: "array" });
        saveAs(new Blob([excelBuffer], { type: "application/octet-stream" }), "doanhthu.xlsx");
    };

    const revenueByBranch = {};
    revenueData.forEach(order => {
        const branch = order.branch?.name || "Không rõ";
        revenueByBranch[branch] = (revenueByBranch[branch] || 0) + (order.totalAmount || 0);
    });
    const branchList = Object.entries(revenueByBranch).map(([branch, amount]) => ({ branch, amount }));

    const chartData = revenueData.map(order => ({
        label: new Date(order.createdAt).toLocaleDateString("vi-VN"),
        totalAmount: order.totalAmount
    })).reverse();

    return (
        <div className="p-6">
            <h2 className="text-2xl font-bold mb-6">Báo cáo Doanh thu</h2>

            <button onClick={exportToExcel} className="btn btn-accent mb-4">Xuất Excel</button>

            <div className="mb-4 flex flex-col md:flex-row gap-4 items-center">
                <select
                    className="select select-bordered w-full md:w-1/4"
                    value={timeRange}
                    onChange={(e) => {
                        setTimeRange(e.target.value);
                        setStartDate("");
                        setEndDate("");
                    }}
                >
                    <option value="all">Tất cả</option>
                    <option value="today">Hôm nay</option>
                    <option value="week">Tuần này</option>
                    <option value="month">Tháng này</option>
                    <option value="quarter">Quý này</option>
                    <option value="year">Năm nay</option>
                </select>
                <button className="btn btn-primary" onClick={openFilterModal}>
                    <FaFilter className="mr-2" /> Bộ lọc nâng cao
                </button>
            </div>

            <div className="card bg-base-200 shadow-xl mb-6">
                <div className="card-body">
                    <h3 className="card-title">Tổng Doanh thu</h3>
                    <p className="text-3xl font-bold">{totalRevenue.toLocaleString()} VNĐ</p>
                </div>
            </div>

            <div className="h-96 mb-8">
                <ResponsiveContainer width="100%" height="100%">
                    <LineChart data={chartData}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="label" />
                        <YAxis />
                        <Tooltip />
                        <Line type="monotone" dataKey="totalAmount" stroke="#8884d8" strokeWidth={2} />
                    </LineChart>
                </ResponsiveContainer>
            </div>

            <h3 className="text-xl font-semibold mb-2">Doanh thu theo chi nhánh</h3>
            <table className="table w-full mb-6">
                <thead><tr><th>Chi nhánh</th><th>Tổng doanh thu</th></tr></thead>
                <tbody>
                    {branchList.map((b, idx) => (
                        <tr key={idx}>
                            <td>{b.branch}</td>
                            <td>{b.amount.toLocaleString()} VNĐ</td>
                        </tr>
                    ))}
                </tbody>
            </table>

            <div className="overflow-x-auto">
                <table className="table w-full">
                    <thead>
                        <tr>
                            <th>ID Đơn hàng</th>
                            <th>Khách hàng</th>
                            <th>Chi nhánh</th>
                            <th>Tổng tiền</th>
                            <th>Trạng thái</th>
                            <th>Ngày đặt hàng</th>
                            <th>Chi tiết</th>
                        </tr>
                    </thead>
                    <tbody>
                        {revenueData.length === 0 ? (
                            <tr>
                                <td colSpan="7" className="text-center">Không tìm thấy dữ liệu doanh thu.</td>
                            </tr>
                        ) : (
                            revenueData.map((order) => (
                                <tr key={order.id}>
                                    <td>{order.id}</td>
                                    <td>{order.user?.fullName || "N/A"}</td>
                                    <td>{order.branch?.name || "N/A"}</td>
                                    <td>{(order.totalAmount || 0).toLocaleString()} VNĐ</td>
                                    <td>
                                        <span className={`badge ${order.status === "completed" ? "badge-success" : "badge-warning"}`}>
                                            {order.status === "completed" ? "Hoàn thành" : "Đang xử lý"}
                                        </span>
                                    </td>
                                    <td>{new Date(order.createdAt).toLocaleDateString("vi-VN")}</td>
                                    <td>
                                        <details className="dropdown">
                                            <summary className="btn btn-ghost btn-sm">Xem chi tiết</summary>
                                            <ul className="dropdown-content menu p-4 shadow bg-base-200 rounded-box w-52">
                                                {order.orderItems?.map((item) => (
                                                    <li key={item.id}>
                                                        {item.product?.name} x {item.quantity} - {(item.quantity * item.price).toLocaleString()} VNĐ
                                                    </li>
                                                ))}
                                            </ul>
                                        </details>
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>

            <Transition appear show={isFilterModalOpen} as={Fragment}>
                <Dialog as="div" className="relative z-10" onClose={closeFilterModal}>
                    <Transition.Child as={Fragment} enter="ease-out duration-300" enterFrom="opacity-0" enterTo="opacity-100" leave="ease-in duration-200" leaveFrom="opacity-100" leaveTo="opacity-0">
                        <div className="fixed inset-0 bg-black bg-opacity-25" />
                    </Transition.Child>
                    <div className="fixed inset-0 overflow-y-auto">
                        <div className="flex min-h-full items-center justify-center p-4">
                            <Transition.Child as={Fragment} enter="ease-out duration-300" enterFrom="opacity-0 scale-95" enterTo="opacity-100 scale-100" leave="ease-in duration-200" leaveFrom="opacity-100 scale-100" leaveTo="opacity-0 scale-95">
                                <Dialog.Panel className="w-full max-w-md transform overflow-hidden rounded-2xl bg-base-100 p-6 text-left align-middle shadow-xl transition-all">
                                    <Dialog.Title as="h3" className="text-lg font-medium leading-6">Bộ lọc Doanh thu</Dialog.Title>
                                    <form onSubmit={handleFilterSubmit} className="mt-4">
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Từ ngày</label>
                                            <input type="date" className="input input-bordered w-full mt-1" value={startDate} onChange={(e) => setStartDate(e.target.value)} disabled={loading} />
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">Đến ngày</label>
                                            <input type="date" className="input input-bordered w-full mt-1" value={endDate} onChange={(e) => setEndDate(e.target.value)} disabled={loading} />
                                        </div>
                                        <div className="mt-4">
                                            <button type="submit" className="btn btn-primary w-full" disabled={loading}>Áp dụng Bộ lọc</button>
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

export default RevenueReport;
