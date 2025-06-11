import React, { useState } from "react";
import { Link } from "react-router-dom";
import { getAllProducts } from "../../../services/productService";
import { formatCurrency } from "../../../utils/currencyFormatter";
import Rating from "../../Other/Rating";
import {
  Input,
  Dropdown,
  Menu,
  Spin,
  List,
  Avatar,
  Typography,
  Empty,
} from "antd";
import { SearchOutlined, CloseCircleOutlined } from "@ant-design/icons";

const { Text } = Typography;

const Search: React.FC = () => {
  const [searchValue, setSearchValue] = useState<string>("");
  const [products, setProducts] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [showDropdown, setShowDropdown] = useState<boolean>(false);

  const handleChange = async (value: string) => {
    setSearchValue(value);

    if (!value.trim()) {
      setShowDropdown(false);
      setProducts([]);
      return;
    }

    setLoading(true);
    setShowDropdown(true);

    try {
      const response = await getAllProducts(value, null, null, 1, 10);
      setProducts(response.payload.content);
    } catch (error) {
      console.error("Lỗi tải sản phẩm:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleClearSearch = () => {
    setSearchValue("");
    setShowDropdown(false);
    setProducts([]);
  };

  const menu = (
    <Menu style={{ width: "400px", maxHeight: "400px", overflowY: "auto" }}>
      {loading ? (
        <Spin size="large" style={{ display: "block", textAlign: "center", padding: "10px" }} />
      ) : products.length > 0 ? (
        <List
          itemLayout="horizontal"
          dataSource={products}
          renderItem={(product) => {
            const defaultImage = product.images.find((img: any) => img.isDefault)?.imageUrl;
            const discountPrice =
              product.discount && product.discount > 0
                ? product.price - (product.price * product.discount) / 100
                : product.price;

            return (
              <List.Item>
                <Link
                  to={`/product-details/${product.productId}`}
                  onClick={handleClearSearch}
                  style={{ width: "100%", display: "flex", alignItems: "center", padding: "10px", borderRadius: "8px", transition: "0.3s", background: "#f9f9f9" }}
                  onMouseEnter={(e) => (e.currentTarget.style.background = "#f0f0f0")}
                  onMouseLeave={(e) => (e.currentTarget.style.background = "#f9f9f9")}
                >
                  <Avatar shape="square" size={60} src={defaultImage} />
                  <div style={{ marginLeft: "15px", flex: 1 }}>
                    <Text strong style={{ color: "#80001C", fontSize: "16px" }}>
                      {product.productName}
                    </Text>
                    <div>
                      {product.discount > 0 ? (
                        <>
                          <Text delete style={{ color: "gray", marginRight: "8px" }}>
                            {formatCurrency(product.price)}
                          </Text>
                          <Text strong style={{ color: "red" }}>
                            {formatCurrency(discountPrice)}
                          </Text>
                        </>
                      ) : (
                        <Text strong>{formatCurrency(product.price)}</Text>
                      )}
                    </div>
                  </div>
                </Link>
              </List.Item>
            );
          }}
        />
      ) : (
        <Empty description="Không tìm thấy sản phẩm nào" style={{ padding: "20px" }} />
      )}
    </Menu>
  );

  return (
    <Dropdown overlay={menu} visible={showDropdown} placement="bottomLeft">
      <Input
        value={searchValue}
        placeholder="Tìm kiếm sản phẩm..."
        onChange={(e) => handleChange(e.target.value)}
        onPressEnter={(e) => handleChange(e.currentTarget.value)}
        suffix={
          searchValue && (
            <CloseCircleOutlined
              onClick={handleClearSearch}
              style={{ color: "#80001C", cursor: "pointer" }}
            />
          )
        }
        prefix={<SearchOutlined style={{ color: "#80001C", fontSize: "18px" }} />}
        style={{
          width: 600,
          height: 48,
          fontSize: "16px",
          borderRadius: "30px",
          borderColor: "#80001C",
          color: "#80001C",
          boxShadow: "0px 4px 8px rgba(128, 0, 28, 0.1)",
        }}
      />
    </Dropdown>
  );
};

export default Search;
