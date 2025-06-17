import React, { useEffect, useState } from "react";
import axios from "axios";
import { Swiper, SwiperSlide } from "swiper/react";
import { Autoplay, Navigation, Pagination } from "swiper/modules";
import "swiper/css";
import "swiper/css/navigation";
import "swiper/css/pagination";

const ActiveBanners = () => {
    const [banners, setBanners] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchBanners = async () => {
            setLoading(true);
            try {
                const res = await axios.get("http://localhost:8080/api/banners/active");
                setBanners(res.data || []);
            } catch (err) {
                setError("Lỗi khi tải banner.");
            } finally {
                setLoading(false);
            }
        };
        fetchBanners();
    }, []);

    if (loading) return <div>Loading banners...</div>;
    if (error) return <div className="text-red-500">{error}</div>;

    // Tách banner to và nhỏ (ví dụ: banner đầu tiên là to)
    const [mainBanner, ...smallBanners] = banners;

    return (
        <div className="p-4 bg-[#f4f6fb]">
            {/* Banner chính */}
            <div className="mb-6">
                <Swiper
                    modules={[Autoplay, Pagination, Navigation]}
                    autoplay={{ delay: 3000 }}
                    pagination={{ clickable: true }}
                    navigation
                    loop
                >
                    {mainBanner && (
                        <SwiperSlide key={mainBanner.id}>
                            <a href={mainBanner.targetUrl} target="_blank" rel="noopener noreferrer">
                                <img
                                    src={mainBanner.imageUrl}
                                    alt={mainBanner.name}
                                    className="w-full h-[300px] object-cover rounded-xl"
                                />
                            </a>
                        </SwiperSlide>
                    )}
                </Swiper>
            </div>

            {/* Banner phụ */}
            <Swiper
                modules={[Navigation]}
                slidesPerView={3}
                spaceBetween={20}
                navigation
                breakpoints={{
                    640: { slidesPerView: 1 },
                    768: { slidesPerView: 2 },
                    1024: { slidesPerView: 3 },
                }}
            >
                {smallBanners.map((banner) => (
                    <SwiperSlide key={banner.id}>
                        <a href={banner.targetUrl} target="_blank" rel="noopener noreferrer">
                            <img
                                src={banner.imageUrl}
                                alt={banner.name}
                                className="w-full h-40 object-cover rounded-xl"
                            />
                        </a>
                    </SwiperSlide>
                ))}
            </Swiper>
        </div>
    );
};

export default ActiveBanners;
