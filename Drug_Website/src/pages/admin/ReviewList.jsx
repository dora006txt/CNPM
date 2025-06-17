// src/pages/admin/ReviewList.jsx
import React, { useState } from "react";
import { FaTrash } from "react-icons/fa";

const ReviewList = () => {
    // Dữ liệu tĩnh cho reviews
    const staticReviews = [
        {
            id: 1,
            product: { id: 1, name: "Updated Paracetamol 500mg" },
            user: { id: 1, username: "user1" },
            rating: 4,
            comment: "Sản phẩm rất hiệu quả, giảm đau nhanh.",
            createdAt: "2025-04-20T10:00:00",
        },
        {
            id: 2,
            product: { id: 4, name: "Ibuprofen 400mg" },
            user: { id: 2, username: "user2" },
            rating: 3,
            comment: "Hiệu quả trung bình, hơi buồn nôn.",
            createdAt: "2025-04-21T15:30:00",
        },
        {
            id: 3,
            product: { id: 6, name: "Povidone Iodine 10%" },
            user: { id: 3, username: "user3" },
            rating: 5,
            comment: "Sát khuẩn tốt, rất hài lòng!",
            createdAt: "2025-04-22T09:00:00",
        },
    ];

    const [reviews, setReviews] = useState(staticReviews);
    const [searchTerm, setSearchTerm] = useState("");
    const [filterRating, setFilterRating] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const filteredReviews = reviews.filter((review) => {
        const matchesSearch = review.product.name
            .toLowerCase()
            .includes(searchTerm.toLowerCase());
        const matchesRating = filterRating
            ? review.rating === parseInt(filterRating)
            : true;
        return matchesSearch && matchesRating;
    });

    console.log("Filtered Reviews:", filteredReviews);

    const handleDelete = (reviewId) => {
        if (window.confirm("Bạn có chắc chắn muốn xóa đánh giá này?")) {
            setLoading(true);
            try {
                setReviews(reviews.filter((r) => r.id !== reviewId));
            } catch (err) {
                setError("Có lỗi xảy ra khi xóa đánh giá.");
                console.error(err);
            } finally {
                setLoading(false);
            }
        }
    };

    return (
        <div>
            <h2 className="text-2xl font-bold mb-6">Quản lý Đánh giá</h2>

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
                    placeholder="Tìm kiếm sản phẩm..."
                    className="input input-bordered w-full md:w-1/3"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
                <select
                    className="select select-bordered w-full md:w-1/4"
                    value={filterRating}
                    onChange={(e) => setFilterRating(e.target.value)}
                >
                    <option value="">Tất cả số sao</option>
                    <option value="1">1 sao</option>
                    <option value="2">2 sao</option>
                    <option value="3">3 sao</option>
                    <option value="4">4 sao</option>
                    <option value="5">5 sao</option>
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
                                <th>Sản phẩm</th>
                                <th>Người dùng</th>
                                <th>Số sao</th>
                                <th>Nội dung</th>
                                <th>Ngày tạo</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredReviews.length === 0 ? (
                                <tr>
                                    <td colSpan="6" className="text-center">
                                        Không tìm thấy đánh giá.
                                    </td>
                                </tr>
                            ) : (
                                filteredReviews.map((review) => (
                                    <tr key={review.id}>
                                        <td>{review.product.name}</td>
                                        <td>{review.user.username}</td>
                                        <td>{review.rating} ⭐</td>
                                        <td>{review.comment}</td>
                                        <td>{new Date(review.createdAt).toLocaleDateString()}</td>
                                        <td>
                                            <button
                                                className="btn btn-ghost btn-sm"
                                                onClick={() => handleDelete(review.id)}
                                                disabled={loading}
                                            >
                                                <FaTrash />
                                            </button>
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

export default ReviewList;