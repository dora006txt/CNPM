import React, { useEffect, useState } from "react";
import axios from "axios";
import { Swiper, SwiperSlide } from "swiper/react";
import { Autoplay, Navigation, Pagination } from "swiper/modules";
import "swiper/css";
import "swiper/css/navigation";
import "swiper/css/pagination";

const BannerSlider = () => {
    const [banners, setBanners] = useState([]);

    useEffect(() => {
        const fetchBanners = async () => {
            try {
                const res = await axios.get("http://localhost:8080/api/banners/active");
                setBanners(res.data || []);
            } catch (err) {
                console.error("Lỗi khi tải banner:", err);
            }
        };
        fetchBanners();
    }, []);

    return (
        <div className="w-full max-w-screen-xl mx-auto mt-4 rounded-xl overflow-hidden">
            <Swiper
                modules={[Autoplay, Navigation, Pagination]}
                autoplay={{ delay: 5000 }}
                pagination={{ clickable: true }}
                navigation
                loop
                className="rounded-xl"
            >
                {banners.map((banner) => (
                    <SwiperSlide key={banner.id}>
                        <a href={banner.targetUrl} target="_blank" rel="noopener noreferrer">
                            <img
                                src={banner.imageUrl}
                                alt={banner.name}
                                className="w-full h-[300px] object-cover"
                            />
                        </a>
                    </SwiperSlide>
                ))}
            </Swiper>
        </div>
    );
};

export default BannerSlider;
