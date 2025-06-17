import { useState, useEffect } from "react";

const Carousel = () => {
  const [currentSlide, setCurrentSlide] = useState(1);
  const totalSlides = 3;

  // Tự động chuyển slide sau 5 giây
  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentSlide((prev) => (prev === totalSlides ? 1 : prev + 1));
    }, 5000); // 5000 ms = 5 giây

    // Cleanup interval khi component unmount
    return () => clearInterval(interval);
  }, []);

  // Hàm chuyển slide
  const goToPrevious = () => {
    setCurrentSlide((prev) => (prev === 1 ? totalSlides : prev - 1));
  };

  const goToNext = () => {
    setCurrentSlide((prev) => (prev === totalSlides ? 1 : prev + 1));
  };

  return (
    <div className="carousel w-full mb-10 mt-5">
      <div
        id="slide1"
        className={`carousel-item relative w-full ${currentSlide === 1 ? "block" : "hidden"
          }`}
      >
        <img
          src="https://cdn.nhathuoclongchau.com.vn/unsafe/2560x0/filters:quality(90)/https://cms-prod.s3-sgn09.fptcloud.com/Full_Desktop_1440x256_1b65560449.png"
          className="w-full"
        />
        <div className="absolute left-5 right-5 top-1/2 flex -translate-y-1/2 transform justify-between">
          <button onClick={goToPrevious} className="btn btn-circle">
            ❮
          </button>
          <button onClick={goToNext} className="btn btn-circle">
            ❯
          </button>
        </div>
      </div>

      <div
        id="slide2"
        className={`carousel-item relative w-full ${currentSlide === 2 ? "block" : "hidden"
          }`}
      >
        <img
          src="https://cdn.nhathuoclongchau.com.vn/unsafe/2560x0/filters:quality(90)/https://cms-prod.s3-sgn09.fptcloud.com/310325_Banner_Home_a9e828a451.png"
          className="w-full"
        />
        <div className="absolute left-5 right-5 top-1/2 flex -translate-y-1/2 transform justify-between">
          <button onClick={goToPrevious} className="btn btn-circle">
            ❮
          </button>
          <button onClick={goToNext} className="btn btn-circle">
            ❯
          </button>
        </div>
      </div>

      <div
        id="slide3"
        className={`carousel-item relative w-full ${currentSlide === 3 ? "block" : "hidden"
          }`}
      >
        <img
          src="https://cdn.nhathuoclongchau.com.vn/unsafe/2560x0/filters:quality(90)/https://s3-sgn09.fptcloud.com/cms-prod/Banner_full_width_D_1440x256_f03f032601.png"
          className="w-full"
        />
        <div className="absolute left-5 right-5 top-1/2 flex -translate-y-1/2 transform justify-between">
          <button onClick={goToPrevious} className="btn btn-circle">
            ❮
          </button>
          <button onClick={goToNext} className="btn btn-circle">
            ❯
          </button>
        </div>
      </div>
    </div>
  );
};

export default Carousel;
