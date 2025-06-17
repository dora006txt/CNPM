
const Carousel = () => {


  return (
    <div className="flex">

      <div className="carousel w-full mb-10 mr-2">
        <div
          id="slide1"
          className={`carousel-item relative w-full`}
        >
          <img
            src="https://cdn.nhathuoclongchau.com.vn/unsafe/2560x0/filters:quality(90)/https://cms-prod.s3-sgn09.fptcloud.com/Full_Desktop_1440x256_1b65560449.png"
            className="w-full"
          />
          <div className="absolute left-5 right-5 top-1/2 flex -translate-y-1/2 transform justify-between">
            ❮          </div>
        </div>
      </div>
      <div className="carousel w-full mb-10 ml-2">

        <div
          id="slide3"
          className={`carousel-item relative w-full ml-5`}
        >
          <img
            src="https://cdn.nhathuoclongchau.com.vn/unsafe/2560x0/filters:quality(90)/https://s3-sgn09.fptcloud.com/cms-prod/Banner_full_width_D_1440x256_f03f032601.png"
            className="w-full"
          />
          <div className="absolute left-5 right-5 top-1/2 flex -translate-y-1/2 transform justify-between">
          </div>
        </div>
      </div>
    </div>
  );
};

export default Carousel;
