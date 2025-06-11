import React, { useState, useEffect } from "react";
import { useDispatch } from "react-redux";
import { SortByBrand } from "../../../../redux/actions/productActions";
import { GetTitle, IsLoading } from "../../../../redux/actions/primaryActions";
import { getAllBrands } from "../../../../services/brandService";
import { List, Card, Spin, Typography, Avatar } from "antd";
import { CheckOutlined, LoadingOutlined } from "@ant-design/icons";

const { Title } = Typography;

interface BrandsProps {
  setSelectedBrand: (brandId: number | null) => void;
}

const Brands: React.FC<BrandsProps> = ({ setSelectedBrand }) => {
  const dispatch = useDispatch();
  const [brands, setBrands] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [selectedBrandId, setSelectedBrandId] = useState<number | null>(null);

  useEffect(() => {
    const fetchBrands = async () => {
      try {
        const response = await getAllBrands();
        setBrands(response.content);
      } catch (error) {
        console.error("Error fetching brands", error);
      } finally {
        setLoading(false);
      }
    };

    fetchBrands();
  }, []);

  return (
    <Card
      title={
        <Title level={5} style={{ margin: 0, color: "#80001C" }}>
          Thương Hiệu
        </Title>
      }
      bordered={false}
      style={{
        width: "100%",
        background: "#fff",
        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
        borderRadius: "8px",
        padding: "10px",
        marginTop: "10px",
      }}
    >
      {loading ? (
        <Spin
          indicator={
            <LoadingOutlined style={{ fontSize: 24, color: "#80001C" }} />
          }
          style={{ display: "flex", justifyContent: "center", marginTop: 10 }}
        />
      ) : (
        <List
          dataSource={[
            { brandId: null, brandName: "Tất cả thương hiệu" },
            ...brands,
          ]}
          renderItem={(brand) => (
            <List.Item
              onClick={() => {
                if (selectedBrandId === brand.brandId) return;
                setSelectedBrand(brand.brandId);
                setSelectedBrandId(brand.brandId);
                dispatch(GetTitle(brand.brandName));
                dispatch(IsLoading(true));

                if (brand.brandId !== null) {
                  dispatch(SortByBrand(brand.brandName));
                }
              }}
              style={{
                display: "flex",
                alignItems: "center",
                padding: "8px 12px",
                borderRadius: "6px",
                transition: "all 0.3s ease-in-out",
                cursor: "pointer",
                background:
                  selectedBrandId === brand.brandId ? "#80001C" : "#fff",
                color: selectedBrandId === brand.brandId ? "#fff" : "#333",
                fontSize: "14px",
                fontWeight:
                  selectedBrandId === brand.brandId ? "bold" : "normal",
                boxShadow:
                  selectedBrandId === brand.brandId
                    ? "0 2px 6px rgba(0, 0, 0, 0.15)"
                    : "none",
              }}
            >
              {brand.brandId !== null && (
                <Avatar
                  src={brand.image}
                  size={32}
                  shape="square"
                  style={{
                    marginRight: "10px",
                    border: "1px solid #ddd",
                    transition: "border-color 0.3s",
                    borderColor:
                      selectedBrandId === brand.brandId ? "#fff" : "#ddd",
                  }}
                />
              )}
              <span style={{ flex: 1 }}>{brand.brandName}</span>
              {selectedBrandId === brand.brandId && (
                <CheckOutlined style={{ fontSize: 14, color: "#fff" }} />
              )}
            </List.Item>
          )}
        />
      )}
    </Card>
  );
};

export default Brands;
