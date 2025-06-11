import React, { useState, useEffect } from "react";
import { Button, message } from "antd";
import { MessageOutlined } from "@ant-design/icons";
import Banner from "../components/Home/Banner/Banner";
import Advantages from "../components/Home/Advantages/Advantages";
import DealOfTheDay from "../components/Home/DealOfTheDay/DealOfTheDay";
import HomeAds1 from "../components/Home/Ads/HomeAds1";
import Categories from "../components/Home/Categories/Categories";
import HomeAds2 from "../components/Home/Ads/HomeAds2";
import SuggestProduct from "../components/Home/DealOfTheDay/SuggestProduct";

const Home: React.FC = () => {
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);


  return (
    <div className="home-content">
      <div className="main">
        <Banner />
        <Advantages />
        <SuggestProduct />
        <DealOfTheDay />
        <HomeAds1 />
        <Categories />
        <HomeAds2 />
      </div>
    </div>
  );
};

// Các style cho Button
const styles = {
  chatButton: {
    position: "fixed" as "fixed",
    bottom: "50px",
    right: "90px",
    zIndex: 1000,
    boxShadow: "0 4px 8px rgba(0,0,0,0.1)",
    color: "#ffffff",
    backgroundColor: "#80001C", // Thay đổi màu nền
    width: "50px", // Tăng chiều rộng
    height: "50px", // Tăng chiều cao
    display: "flex",
    alignItems: "center",
    justifyContent: "center", // Đảm bảo icon căn giữa
  },
};

export default Home;
