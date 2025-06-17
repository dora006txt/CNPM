import React, { useState, useEffect } from "react";
import axios from "axios";
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import L from "leaflet";

// Fix Leaflet marker icon issue
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
    iconRetinaUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
    iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
    shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
});

const PharmacyLocator = () => {
    const [branches, setBranches] = useState([]);
    const [filteredBranches, setFilteredBranches] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Fetch branches from API
    useEffect(() => {
        const fetchBranches = async () => {
            try {
                setLoading(true);
                const response = await axios.get("http://localhost:8080/api/v1/branches");
                const activeBranches = response.data.filter((branch) => branch.isActive);
                setBranches(activeBranches);
                setFilteredBranches(activeBranches);
            } catch (err) {
                setError(err.message || "Không thể tải danh sách chi nhánh. Vui lòng thử lại.");
                console.error("Fetch branches error:", err);
            } finally {
                setLoading(false);
            }
        };
        fetchBranches();
    }, []);

    // Handle search
    useEffect(() => {
        const filtered = branches.filter(
            (branch) =>
                branch.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                branch.address.toLowerCase().includes(searchTerm.toLowerCase())
        );
        setFilteredBranches(filtered);
    }, [searchTerm, branches]);

    if (loading) {
        return (
            <div className="bg-white text-black p-4">
                <h1 className="text-2xl font-bold mb-6">Tìm nhà thuốc</h1>
                <div className="flex justify-center">
                    <span className="loading loading-spinner loading-lg"></span>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="bg-white text-black p-4">
                <h1 className="text-2xl font-bold mb-6">Tìm nhà thuốc</h1>
                <div className="alert alert-error mb-6">
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
                    <span>{error}</span>
                </div>
            </div>
        );
    }

    return (
        <div className="bg-white text-black p-4">
            <h1 className="text-2xl font-bold mb-6">Tìm nhà thuốc</h1>

            {/* Search bar */}
            <div className="mb-6">
                <input
                    type="text"
                    placeholder="Tìm kiếm nhà thuốc theo tên hoặc địa chỉ..."
                    className="input input-bordered w-full max-w-md bg-white text-black border-2 border-gray-300"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
            </div>

            {/* Map */}
            <div className="mb-6">
                <MapContainer
                    center={[10.79, 106.70]} // Center map around TP.HCM
                    zoom={12}
                    style={{ height: "400px", width: "100%", borderRadius: "8px" }}
                >
                    <TileLayer
                        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    />
                    {filteredBranches.map((branch) => (
                        <Marker key={branch.branchId} position={[branch.latitude, branch.longitude]}>
                            <Popup>
                                <div>
                                    <h3 className="font-bold">{branch.name}</h3>
                                    <p>{branch.address}</p>
                                    <p>Giờ mở cửa: {branch.operatingHours}</p>
                                    <p>SĐT: {branch.phoneNumber}</p>
                                </div>
                            </Popup>
                        </Marker>
                    ))}
                </MapContainer>
            </div>

            {/* Branch list */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {filteredBranches.length > 0 ? (
                    filteredBranches.map((branch) => (
                        <div
                            key={branch.branchId}
                            className="p-4 border rounded-lg bg-white hover:bg-gray-300 transition"
                        >
                            <h3 className="text-lg font-semibold mb-2">{branch.name}</h3>
                            <p className="mb-1"><strong>Địa chỉ:</strong> {branch.address}</p>
                            <p className="mb-1"><strong>SĐT:</strong> {branch.phoneNumber}</p>
                            <p className="mb-1"><strong>Giờ mở cửa:</strong> {branch.operatingHours}</p>
                            <button
                                onClick={() => window.open(`https://www.google.com/maps?q=${branch.latitude},${branch.longitude}`, "_blank")}
                                className="btn bg-blue-300 hover:bg-blue-500 text-black mt-2"
                            >
                                Xem trên Google Maps
                            </button>
                        </div>
                    ))
                ) : (
                    <p className="text-center col-span-full">Không tìm thấy nhà thuốc nào.</p>
                )}
            </div>
        </div>
    );
};

export default PharmacyLocator;