// src/pages/admin/ManufacturersBrandsList.jsx
import React, { useState } from "react";
import { FaEdit, FaPlus } from "react-icons/fa";
import { Dialog, Transition } from "@headlessui/react";
import { Fragment } from "react";

const ManufacturersBrandsList = () => {
    // Dữ liệu tĩnh cho manufacturers (dựa trên bảng Manufacturers)
    const staticManufacturers = [
        {
            id: 1,
            name: "Pfizer Inc.",
            country: "USA",
            website: "https://www.pfizer.com",
            contactEmail: "contact@pfizer.com",
            contactPhone: "+1-800-879-3477",
            createdAt: "2025-04-20T10:00:00",
            updatedAt: "2025-04-20T10:00:00",
        },
        {
            id: 2,
            name: "Vinpharma",
            country: "Vietnam",
            website: "https://www.vinpharma.vn",
            contactEmail: "info@vinpharma.vn",
            contactPhone: "+84-24-1234-5678",
            createdAt: "2025-04-21T15:00:00",
            updatedAt: "2025-04-21T15:00:00",
        },
        // Thêm 10 nhà sản xuất mới
        {
            id: 3,
            name: "Novartis AG",
            country: "Switzerland",
            website: "https://www.novartis.com",
            contactEmail: "support@novartis.com",
            contactPhone: "+41-61-324-1111",
            createdAt: "2025-05-20T12:40:00",
            updatedAt: "2025-05-20T12:40:00",
        },
        {
            id: 4,
            name: "HauGiang Pharm",
            country: "Vietnam",
            website: "https://www.haugiangpharm.vn",
            contactEmail: "contact@haugiangpharm.vn",
            contactPhone: "+84-29-1234-5678",
            createdAt: "2025-05-20T12:40:00",
            updatedAt: "2025-05-20T12:40:00",
        },
        {
            id: 5,
            name: "Roche Holding AG",
            country: "Switzerland",
            website: "https://www.roche.com",
            contactEmail: "info@roche.com",
            contactPhone: "+41-61-688-1111",
            createdAt: "2025-05-20T12:40:00",
            updatedAt: "2025-05-20T12:40:00",
        },
        {
            id: 6,
            name: "Sanofi S.A.",
            country: "France",
            website: "https://www.sanofi.com",
            contactEmail: "support@sanofi.com",
            contactPhone: "+33-1-53-77-4000",
            createdAt: "2025-05-20T12:40:00",
            updatedAt: "2025-05-20T12:40:00",
        },
        {
            id: 7,
            name: "Domesco Medical",
            country: "Vietnam",
            website: "https://www.domesco.vn",
            contactEmail: "info@domesco.vn",
            contactPhone: "+84-27-1234-5678",
            createdAt: "2025-05-20T12:40:00",
            updatedAt: "2025-05-20T12:40:00",
        },
        {
            id: 8,
            name: "Johnson & Johnson",
            country: "USA",
            website: "https://www.jnj.com",
            contactEmail: "contact@jnj.com",
            contactPhone: "+1-732-524-0400",
            createdAt: "2025-05-20T12:40:00",
            updatedAt: "2025-05-20T12:40:00",
        },
        {
            id: 9,
            name: "Traphaco JSC",
            country: "Vietnam",
            website: "https://www.traphaco.vn",
            contactEmail: "support@traphaco.vn",
            contactPhone: "+84-24-5678-1234",
            createdAt: "2025-05-20T12:40:00",
            updatedAt: "2025-05-20T12:40:00",
        },
        {
            id: 10,
            name: "Merck & Co.",
            country: "USA",
            website: "https://www.merck.com",
            contactEmail: "info@merck.com",
            contactPhone: "+1-908-740-4000",
            createdAt: "2025-05-20T12:40:00",
            updatedAt: "2025-05-20T12:40:00",
        },
        {
            id: 11,
            name: "AstraZeneca PLC",
            country: "UK",
            website: "https://www.astrazeneca.com",
            contactEmail: "support@astrazeneca.com",
            contactPhone: "+44-20-3749-5000",
            createdAt: "2025-05-20T12:40:00",
            updatedAt: "2025-05-20T12:40:00",
        },
        {
            id: 12,
            name: "Imexpharm",
            country: "Vietnam",
            website: "https://www.imexpharm.vn",
            contactEmail: "contact@imexpharm.vn",
            contactPhone: "+84-28-1234-5678",
            createdAt: "2025-05-20T12:40:00",
            updatedAt: "2025-05-20T12:40:00",
        },
    ];

    // Dữ liệu tĩnh cho brands (dựa trên bảng Brands)
    const staticBrands = [
        {
            id: 1,
            manufacturerId: 1,
            manufacturerName: "Pfizer Inc.",
            name: "Pfizer",
            description: "Thương hiệu dược phẩm hàng đầu thế giới.",
            logoUrl: "https://example.com/brands/pfizer-logo.jpg",
            createdAt: "2025-04-20T10:00:00",
            updatedAt: "2025-04-20T10:00:00",
        },
        {
            id: 2,
            manufacturerId: 2,
            manufacturerName: "Vinpharma",
            name: "Vinpharma",
            description: "Thương hiệu dược phẩm Việt Nam uy tín.",
            logoUrl: "https://example.com/brands/vinpharma-logo.jpg",
            createdAt: "2025-04-21T15:00:00",
            updatedAt: "2025-04-21T15:00:00",
        },
        // Thêm 10 thương hiệu mới
        {
            id: 3,
            manufacturerId: 3,
            manufacturerName: "Novartis AG",
            name: "Novartis",
            description: "Thương hiệu dược phẩm toàn cầu từ Thụy Sĩ.",
            logoUrl: "https://example.com/brands/novartis-logo.jpg",
            createdAt: "2025-05-20T12:40:00",
            updatedAt: "2025-05-20T12:40:00",
        },
        {
            id: 4,
            manufacturerId: 4,
            manufacturerName: "HauGiang Pharm",
            name: "HauGiang",
            description: "Thương hiệu dược phẩm Việt Nam chất lượng.",
            logoUrl: "https://example.com/brands/haugiang-logo.jpg",
            createdAt: "2025-05-20T12:40:00",
            updatedAt: "2025-05-20T12:40:00",
        },
        {
            id: 5,
            manufacturerId: 5,
            manufacturerName: "Roche Holding AG",
            name: "Roche",
            description: "Thương hiệu dược phẩm chuyên về ung thư.",
            logoUrl: "https://example.com/brands/roche-logo.jpg",
            createdAt: "2025-05-20T12:40:00",
            updatedAt: "2025-05-20T12:40:00",
        },
        {
            id: 6,
            manufacturerId: 6,
            manufacturerName: "Sanofi S.A.",
            name: "Sanofi",
            description: "Thương hiệu dược phẩm Pháp nổi tiếng.",
            logoUrl: "https://example.com/brands/sanofi-logo.jpg",
            createdAt: "2025-05-20T12:40:00",
            updatedAt: "2025-05-20T12:40:00",
        },
        {
            id: 7,
            manufacturerId: 7,
            manufacturerName: "Domesco Medical",
            name: "Domesco",
            description: "Thương hiệu dược phẩm Việt Nam uy tín.",
            logoUrl: "https://example.com/brands/domesco-logo.jpg",
            createdAt: "2025-05-20T12:40:00",
            updatedAt: "2025-05-20T12:40:00",
        },
        {
            id: 8,
            manufacturerId: 8,
            manufacturerName: "Johnson & Johnson",
            name: "J&J",
            description: "Thương hiệu dược phẩm và chăm sóc sức khỏe.",
            logoUrl: "https://example.com/brands/jnj-logo.jpg",
            createdAt: "2025-05-20T12:40:00",
            updatedAt: "2025-05-20T12:40:00",
        },
        {
            id: 9,
            manufacturerId: 9,
            manufacturerName: "Traphaco JSC",
            name: "Traphaco",
            description: "Thương hiệu dược phẩm truyền thống Việt Nam.",
            logoUrl: "https://example.com/brands/traphaco-logo.jpg",
            createdAt: "2025-05-20T12:40:00",
            updatedAt: "2025-05-20T12:40:00",
        },
        {
            id: 10,
            manufacturerId: 10,
            manufacturerName: "Merck & Co.",
            name: "Merck",
            description: "Thương hiệu dược phẩm hàng đầu tại Mỹ.",
            logoUrl: "https://example.com/brands/merck-logo.jpg",
            createdAt: "2025-05-20T12:40:00",
            updatedAt: "2025-05-20T12:40:00",
        },
        {
            id: 11,
            manufacturerId: 11,
            manufacturerName: "AstraZeneca PLC",
            name: "AstraZeneca",
            description: "Thương hiệu dược phẩm Anh Quốc.",
            logoUrl: "https://example.com/brands/astrazeneca-logo.jpg",
            createdAt: "2025-05-20T12:40:00",
            updatedAt: "2025-05-20T12:40:00",
        },
        {
            id: 12,
            manufacturerId: 12,
            manufacturerName: "Imexpharm",
            name: "Imexpharm",
            description: "Thương hiệu dược phẩm Việt Nam chuyên nghiệp.",
            logoUrl: "https://example.com/brands/imexpharm-logo.jpg",
            createdAt: "2025-05-20T12:40:00",
            updatedAt: "2025-05-20T12:40:00",
        },
    ];

    const [manufacturers, setManufacturers] = useState(staticManufacturers);
    const [brands, setBrands] = useState(staticBrands);
    const [activeTab, setActiveTab] = useState("manufacturers");
    const [searchTerm, setSearchTerm] = useState("");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const [currentItem, setCurrentItem] = useState(null);
    const [formData, setFormData] = useState({
        name: "",
        country: "",
        website: "",
        contactEmail: "",
        contactPhone: "",
        manufacturerId: "",
        description: "",
        logoUrl: "",
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const filteredManufacturers = manufacturers.filter((item) =>
        item.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const filteredBrands = brands.filter((item) =>
        item.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    console.log("Filtered Manufacturers:", filteredManufacturers);
    console.log("Filtered Brands:", filteredBrands);

    const openModal = (item = null, type) => {
        if (item) {
            setIsEditMode(true);
            setCurrentItem(item);
            if (type === "manufacturer") {
                setFormData({
                    name: item.name,
                    country: item.country,
                    website: item.website,
                    contactEmail: item.contactEmail,
                    contactPhone: item.contactPhone,
                    manufacturerId: "",
                    description: "",
                    logoUrl: "",
                });
            } else {
                setFormData({
                    name: item.name,
                    country: "",
                    website: "",
                    contactEmail: "",
                    contactPhone: "",
                    manufacturerId: item.manufacturerId,
                    description: item.description,
                    logoUrl: item.logoUrl,
                });
            }
        } else {
            setIsEditMode(false);
            setFormData({
                name: "",
                country: "",
                website: "",
                contactEmail: "",
                contactPhone: "",
                manufacturerId: "",
                description: "",
                logoUrl: "",
            });
        }
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setCurrentItem(null);
        setIsEditMode(false);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            if (activeTab === "manufacturers") {
                if (isEditMode) {
                    // Cập nhật nhà sản xuất
                    setManufacturers(
                        manufacturers.map((item) =>
                            item.id === currentItem.id
                                ? {
                                    ...item,
                                    name: formData.name,
                                    country: formData.country,
                                    website: formData.website,
                                    contactEmail: formData.contactEmail,
                                    contactPhone: formData.contactPhone,
                                    updatedAt: new Date().toISOString(),
                                }
                                : item
                        )
                    );
                } else {
                    // Thêm nhà sản xuất mới
                    const newManufacturer = {
                        id: manufacturers.length + 1,
                        name: formData.name,
                        country: formData.country,
                        website: formData.website,
                        contactEmail: formData.contactEmail,
                        contactPhone: formData.contactPhone,
                        createdAt: new Date().toISOString(),
                        updatedAt: new Date().toISOString(),
                    };
                    setManufacturers([...manufacturers, newManufacturer]);
                }
            } else {
                if (isEditMode) {
                    // Cập nhật thương hiệu
                    setBrands(
                        brands.map((item) =>
                            item.id === currentItem.id
                                ? {
                                    ...item,
                                    name: formData.name,
                                    manufacturerId: parseInt(formData.manufacturerId),
                                    manufacturerName: manufacturers.find(
                                        (m) => m.id === parseInt(formData.manufacturerId)
                                    ).name,
                                    description: formData.description,
                                    logoUrl: formData.logoUrl,
                                    updatedAt: new Date().toISOString(),
                                }
                                : item
                        )
                    );
                } else {
                    // Thêm thương hiệu mới
                    const newBrand = {
                        id: brands.length + 1,
                        manufacturerId: parseInt(formData.manufacturerId),
                        manufacturerName: manufacturers.find(
                            (m) => m.id === parseInt(formData.manufacturerId)
                        ).name,
                        name: formData.name,
                        description: formData.description,
                        logoUrl: formData.logoUrl,
                        createdAt: new Date().toISOString(),
                        updatedAt: new Date().toISOString(),
                    };
                    setBrands([...brands, newBrand]);
                }
            }
            closeModal();
        } catch (err) {
            setError("Có lỗi xảy ra khi lưu thông tin.");
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <h2 className="text-2xl font-bold mb-6">Quản lý Nhà sản xuất và Thương hiệu</h2>

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

            <div className="tabs mb-4">
                <a
                    className={`tab tab-lifted ${activeTab === "manufacturers" ? "tab-active" : ""}`}
                    onClick={() => setActiveTab("manufacturers")}
                >
                    Nhà sản xuất
                </a>
                <a
                    className={`tab tab-lifted ${activeTab === "brands" ? "tab-active" : ""}`}
                    onClick={() => setActiveTab("brands")}
                >
                    Thương hiệu
                </a>
            </div>

            <div className="mb-4 flex flex-col md:flex-row gap-4">
                <input
                    type="text"
                    placeholder={`Tìm kiếm ${activeTab === "manufacturers" ? "nhà sản xuất" : "thương hiệu"}...`}
                    className="input input-bordered w-full md:w-1/3"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
                <button
                    className="btn btn-primary w-full md:w-auto"
                    onClick={() => openModal(null, activeTab === "manufacturers" ? "manufacturer" : "brand")}
                >
                    <FaPlus /> {activeTab === "manufacturers" ? "Thêm nhà sản xuất" : "Thêm thương hiệu"}
                </button>
            </div>

            <div className="overflow-x-auto">
                {loading ? (
                    <div className="flex justify-center">
                        <span className="loading loading-spinner loading-lg"></span>
                    </div>
                ) : activeTab === "manufacturers" ? (
                    <table className="table w-full">
                        <thead>
                            <tr>
                                <th>Tên nhà sản xuất</th>
                                <th>Quốc gia</th>
                                <th>Website</th>
                                <th>Email liên hệ</th>
                                <th>Số điện thoại</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredManufacturers.length === 0 ? (
                                <tr>
                                    <td colSpan="6" className="text-center">
                                        Không tìm thấy nhà sản xuất.
                                    </td>
                                </tr>
                            ) : (
                                filteredManufacturers.map((item) => (
                                    <tr key={item.id}>
                                        <td>{item.name}</td>
                                        <td>{item.country}</td>
                                        <td>
                                            <a
                                                href={item.website}
                                                target="_blank"
                                                rel="noopener noreferrer"
                                                className="text-blue-500 underline"
                                            >
                                                {item.website}
                                            </a>
                                        </td>
                                        <td>{item.contactEmail}</td>
                                        <td>{item.contactPhone}</td>
                                        <td>
                                            <button
                                                className="btn btn-ghost btn-sm"
                                                onClick={() => openModal(item, "manufacturer")}
                                                disabled={loading}
                                            >
                                                <FaEdit />
                                            </button>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                ) : (
                    <table className="table w-full">
                        <thead>
                            <tr>
                                <th>Tên thương hiệu</th>
                                <th>Nhà sản xuất</th>
                                <th>Mô tả</th>
                                <th>Logo</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredBrands.length === 0 ? (
                                <tr>
                                    <td colSpan="5" className="text-center">
                                        Không tìm thấy thương hiệu.
                                    </td>
                                </tr>
                            ) : (
                                filteredBrands.map((item) => (
                                    <tr key={item.id}>
                                        <td>{item.name}</td>
                                        <td>{item.manufacturerName}</td>
                                        <td>{item.description}</td>
                                        <td>
                                            {item.logoUrl ? (
                                                <a
                                                    href={item.logoUrl}
                                                    target="_blank"
                                                    rel="noopener noreferrer"
                                                    className="text-blue-500 underline"
                                                >
                                                    Xem logo
                                                </a>
                                            ) : (
                                                "N/A"
                                            )}
                                        </td>
                                        <td>
                                            <button
                                                className="btn btn-ghost btn-sm"
                                                onClick={() => openModal(item, "brand")}
                                                disabled={loading}
                                            >
                                                <FaEdit />
                                            </button>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                )}
            </div>

            <Transition appear show={isModalOpen} as={Fragment}>
                <Dialog as="div" className="relative z-10" onClose={closeModal}>
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
                                        {isEditMode
                                            ? activeTab === "manufacturers"
                                                ? "Chỉnh sửa Nhà sản xuất"
                                                : "Chỉnh sửa Thương hiệu"
                                            : activeTab === "manufacturers"
                                                ? "Thêm Nhà sản xuất"
                                                : "Thêm Thương hiệu"}
                                    </Dialog.Title>
                                    <form onSubmit={handleSubmit} className="mt-4">
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium">
                                                Tên {activeTab === "manufacturers" ? "nhà sản xuất" : "thương hiệu"}
                                            </label>
                                            <input
                                                type="text"
                                                className="input input-bordered w-full mt-1"
                                                value={formData.name}
                                                onChange={(e) =>
                                                    setFormData({
                                                        ...formData,
                                                        name: e.target.value,
                                                    })
                                                }
                                                required
                                                disabled={loading}
                                            />
                                        </div>
                                        {activeTab === "manufacturers" ? (
                                            <>
                                                <div className="mb-4">
                                                    <label className="block text-sm font-medium">
                                                        Quốc gia
                                                    </label>
                                                    <input
                                                        type="text"
                                                        className="input input-bordered w-full mt-1"
                                                        value={formData.country}
                                                        onChange={(e) =>
                                                            setFormData({
                                                                ...formData,
                                                                country: e.target.value,
                                                            })
                                                        }
                                                        required
                                                        disabled={loading}
                                                    />
                                                </div>
                                                <div className="mb-4">
                                                    <label className="block text-sm font-medium">
                                                        Website
                                                    </label>
                                                    <input
                                                        type="url"
                                                        className="input input-bordered w-full mt-1"
                                                        value={formData.website}
                                                        onChange={(e) =>
                                                            setFormData({
                                                                ...formData,
                                                                website: e.target.value,
                                                            })
                                                        }
                                                        disabled={loading}
                                                    />
                                                </div>
                                                <div className="mb-4">
                                                    <label className="block text-sm font-medium">
                                                        Email liên hệ
                                                    </label>
                                                    <input
                                                        type="email"
                                                        className="input input-bordered w-full mt-1"
                                                        value={formData.contactEmail}
                                                        onChange={(e) =>
                                                            setFormData({
                                                                ...formData,
                                                                contactEmail: e.target.value,
                                                            })
                                                        }
                                                        disabled={loading}
                                                    />
                                                </div>
                                                <div className="mb-4">
                                                    <label className="block text-sm font-medium">
                                                        Số điện thoại
                                                    </label>
                                                    <input
                                                        type="text"
                                                        className="input input-bordered w-full mt-1"
                                                        value={formData.contactPhone}
                                                        onChange={(e) =>
                                                            setFormData({
                                                                ...formData,
                                                                contactPhone: e.target.value,
                                                            })
                                                        }
                                                        disabled={loading}
                                                    />
                                                </div>
                                            </>
                                        ) : (
                                            <>
                                                <div className="mb-4">
                                                    <label className="block text-sm font-medium">
                                                        Nhà sản xuất
                                                    </label>
                                                    <select
                                                        className="select select-bordered w-full mt-1"
                                                        value={formData.manufacturerId}
                                                        onChange={(e) =>
                                                            setFormData({
                                                                ...formData,
                                                                manufacturerId: e.target.value,
                                                            })
                                                        }
                                                        required
                                                        disabled={loading}
                                                    >
                                                        <option value="">Chọn nhà sản xuất</option>
                                                        {manufacturers.map((manufacturer) => (
                                                            <option key={manufacturer.id} value={manufacturer.id}>
                                                                {manufacturer.name}
                                                            </option>
                                                        ))}
                                                    </select>
                                                </div>
                                                <div className="mb-4">
                                                    <label className="block text-sm font-medium">
                                                        Mô tả
                                                    </label>
                                                    <textarea
                                                        className="textarea textarea-bordered w-full mt-1"
                                                        value={formData.description}
                                                        onChange={(e) =>
                                                            setFormData({
                                                                ...formData,
                                                                description: e.target.value,
                                                            })
                                                        }
                                                        disabled={loading}
                                                    />
                                                </div>
                                                <div className="mb-4">
                                                    <label className="block text-sm font-medium">
                                                        URL Logo
                                                    </label>
                                                    <input
                                                        type="url"
                                                        className="input input-bordered w-full mt-1"
                                                        value={formData.logoUrl}
                                                        onChange={(e) =>
                                                            setFormData({
                                                                ...formData,
                                                                logoUrl: e.target.value,
                                                            })
                                                        }
                                                        disabled={loading}
                                                    />
                                                </div>
                                            </>
                                        )}
                                        <div className="mt-4">
                                            <button
                                                type="submit"
                                                className="btn btn-primary w-full"
                                                disabled={loading}
                                            >
                                                {isEditMode ? "Cập nhật" : "Thêm"}
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

export default ManufacturersBrandsList;