import React from "react";
import BedImg from "../../../assets/img/home-ads/fabric-bed.jpeg";
import IphoneImg from "../../../assets/img/home-ads/iphonex.jpeg";
import { Link } from "react-router-dom";

const HomeAds2: React.FC = () => {
  return (
    <section id="ads-2">
      <div className="container">
        <div className="row">
          <div className="col-lg-8">
            {/* ======= Bed img ======= */}
            <div className="bed-img">
              <Link to="/shop">
                <img
                  src={"https://media.casio-mea.com/mageplaza/blog/post/b/l/blog-post-main-banner1_1.png"}
                  alt="bed"
                  style={{
                    width: "856px",
                    height: "193px",
                    objectFit: "cover",
                  }}
                />
              </Link>
            </div>
          </div>
          <div className="col-lg-4">
            {/* ======= Iphone img ======= */}
            <div className="iphone-img">
              <Link to="/shop">
                <img
                  src={"https://gshock.casio.com/content/casio/locales/us/en/brands/gshock/products/mt-g/mtg-b2000xmg/_jcr_content/root/responsivegrid/container_1689197747/teaser.casiocoreimg.jpeg/1659453063287/mtgb2000xmg-logo-1920x816.jpeg"}
                  alt="iphonex"
                  style={{
                    width: "416px",
                    height: "193px",
                    objectFit: "cover",
                  }}
                />
              </Link>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default HomeAds2;
