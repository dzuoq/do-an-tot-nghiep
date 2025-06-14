import React from "react";
import Slider from "./Slider";
import Img1 from "../../../assets/img/home-banner/other/img1.jpeg";
import Img2 from "../../../assets/img/home-banner/other/img2.jpeg";
import { Link } from "react-router-dom";
import { IBannerRightDataTypes } from "../../../types/types";

const Banner: React.FC = () => {
  const BannerRightData: IBannerRightDataTypes[] = [
    { id: 1, img: "https://donghohungthinh.com/wp-content/uploads/2024/01/dong-ho-casio-g-shock.jpg" },
    {
      id: 2,
      img: "https://www.g-central.com/wp-content/uploads/2024/08/G-SHOCK-FL-SERIES.jpg",
    },
  ];

  return (
    <section id="home-banner">
      <div className="container">
        <div className="home-banner-content">
          <div className="banner-slider-wrapper banner-left">
            <Slider />
          </div>
          <div className="banner-right-imgs">
            {BannerRightData.map((item) => (
              <div key={item.id} className="banner-img-wrapper">
                <Link to="/shop">
                  <img
                    src={item.img}
                    alt="banner-img"
                    style={{
                      width: "390px",
                      height: "193px",
                      objectFit: "cover",
                    }}
                  />
                </Link>
              </div>
            ))}
          </div>
        </div>
      </div>
    </section>
  );
};

export default Banner;
