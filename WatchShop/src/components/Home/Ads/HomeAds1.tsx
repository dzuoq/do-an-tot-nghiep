import React from "react";
import ChairsImg from "../../../assets/img/home-ads/chairs.jpeg";
import ChargerImg from "../../../assets/img/home-ads/charger.jpeg";
import SpeakerImg from "../../../assets/img/home-ads/speaker.jpeg";
import { Link } from "react-router-dom";
import { IAdsData1 } from "../../../types/types";

const HomeAds1: React.FC = () => {
  const AdsData1: IAdsData1[] = [
    { id: 1, img: "https://i.pinimg.com/736x/b7/e1/22/b7e1228c63f11b6b65fe924cba3e32ca.jpg" },
    { id: 2, img: "https://www.casio.com/content/casio/locales/intl/en/products/_jcr_content/root/responsivegrid/image_1970305012_cop.casiocoreimg.jpeg/1744870573475/aem-banner-top-g-1920-816.jpeg" },
    { id: 3, img: "https://image.donghohaitrieu.com/wp-content/uploads/2024/12/banner-casio-xanh.jpg" },
  ];

  return (
    <section id="ads-1">
      <div className="container">
        <div className="row">
          {AdsData1.map((item) => (
            <div key={item.id} className="col-lg-4">
              <div className="ads-img">
                <Link to="/shop">
                  <img
                    src={item.img}
                    alt="ads-img"
                    style={{
                      width: "416px",
                      height: "224px",
                      objectFit: "cover",
                    }}
                  />
                </Link>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};

export default HomeAds1;
