import React, { useState } from "react";
import { Card, Button, Tooltip, Typography, Tag, Divider, notification } from "antd";
import { FiBarChart2 } from "react-icons/fi";
import { BsHeart, BsBag } from "react-icons/bs";
import { useDispatch, useSelector } from "react-redux";
import {
  AddToCart,
  MakeIsInCartTrue,
} from "../../../redux/actions/cartActions";
import {
  AddToWishlist,
  MakeIsInWishlistTrueInWishlist,
} from "../../../redux/actions/wishlistActions";
import {
  AddToCompare,
  MakeIsInCompareTrueInCompare,
} from "../../../redux/actions/compareActions";
import { RootState } from "../../../redux/reducers/index";
import Rating from "../../Other/Rating";
import { formatCurrency } from "../../../utils/currencyFormatter";

const { Title, Text } = Typography;

interface Variation {
  variationId: number;
  attributeName: string;
  attributeValue: string;
  price: number;
  quantity: number;
}

const ProductInfo: React.FC<any> = ({ product }) => {
  const dispatch = useDispatch();
  const [selectedVariation, setSelectedVariation] = useState<Variation | null>(null);

  const cart = useSelector((state: RootState) => state.cart.cart);
  const wishlist = useSelector((state: RootState) => state.wishlist.wishlist);
  const compare = useSelector((state: RootState) => state.compare.compare);

  const isInCart = cart.some((item: { productId: any; }) => item.productId === product.productId);
  const isInWishlist = wishlist.some((item: { productId: any; }) => item.productId === product.productId);
  const isInCompare = compare.some((item: { productId: any; }) => item.productId === product.productId);

  const handleVariationChange = (variation: Variation) => {
    setSelectedVariation(variation);
  };

  const displayedPrice = selectedVariation?.price
    ? selectedVariation.price * (1 - product.discount / 100)
    : product.price * (1 - product.discount / 100);

  const handleAddToCart = () => {
    if (product.variations.length > 0 && !selectedVariation) {
      notification.error({
        message: "Vui lòng chọn một biến thể!",
        description: "Bạn cần chọn một kích thước trước khi thêm vào giỏ hàng.",
      });
      return;
    }

    dispatch(
      AddToCart({
        ...product,
        selectedAttribute: selectedVariation?.attributeValue || "Mặc định",
        selectedPrice: selectedVariation?.price || product.price,
      })
    );
    dispatch(MakeIsInCartTrue(product.productId));

    notification.success({
      message: "Đã thêm vào giỏ hàng",
      description: `"${product.productName}" đã được thêm vào giỏ hàng với lựa chọn "${selectedVariation?.attributeValue || "Mặc định"}".`,
    });
  };

  return (
    <Card bordered={false}>
      {/* ===== Tiêu đề & Xếp hạng ===== */}
      <Title level={4} style={{ color: "#333", marginBottom: "10px" }}>
        {product.productName}
      </Title>
      <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
        <Rating value={product.avgRating} />
        <Text type="secondary">({product.reviewCount} đánh giá)</Text>
      </div>

      <Divider />

      {/* ===== Giá sản phẩm ===== */}
      <div>
        {product.discount > 0 && (
          <Text delete style={{ fontSize: "14px", color: "#999", marginRight: "10px" }}>
            {formatCurrency(selectedVariation?.price ?? product.price)}
          </Text>
        )}
        <Text strong style={{ fontSize: "20px", color: "#80001C" }}>
          {formatCurrency(displayedPrice)}
        </Text>
      </div>

      <Divider />

      {/* ===== Biến thể sản phẩm ===== */}
      {product.variations.length > 0 && (
        <div>
          <Text strong>Lựa chọn:</Text>
          <div
            style={{
              display: "flex",
              flexWrap: "wrap",
              gap: "8px",
              marginTop: "10px",
            }}
          >
            {product.variations.map((variation: Variation) => (
              <Button
                key={variation.variationId}
                type={selectedVariation?.variationId === variation.variationId ? "primary" : "default"}
                style={{
                  backgroundColor:
                    selectedVariation?.variationId === variation.variationId
                      ? "#80001C"
                      : "#f0f0f0",
                  borderColor:
                    selectedVariation?.variationId === variation.variationId
                      ? "#80001C"
                      : "#d9d9d9",
                  color:
                    selectedVariation?.variationId === variation.variationId
                      ? "#fff"
                      : "#333",
                  fontWeight: "bold",
                  transition: "all 0.3s ease-in-out",
                }}
                onClick={() => handleVariationChange(variation)}
              >
                {variation.attributeValue}
              </Button>
            ))}
          </div>
        </div>
      )}

      <Divider />

      {/* ===== Nút thêm vào giỏ hàng & Wishlist & So sánh ===== */}
      <div style={{ display: "flex", gap: "10px", flexWrap: "wrap" }}>
        <Button
          type="primary"
          icon={<BsBag />}
          size="large"
          block
          style={{
            backgroundColor: "#80001C",
            borderColor: "#80001C",
            fontWeight: "bold",
          }}
          onClick={handleAddToCart}
        >
          Thêm vào giỏ hàng
        </Button>

        <Tooltip title={isInWishlist ? "Đã thêm vào Wishlist" : "Thêm vào Wishlist"}>
          <Button
            shape="circle"
            icon={<BsHeart />}
            size="large"
            disabled={isInWishlist}
            style={{
              borderColor: isInWishlist ? "#ccc" : "#80001C",
              color: isInWishlist ? "#ccc" : "#80001C",
            }}
            onClick={() => {
              dispatch(AddToWishlist(product));
              dispatch(MakeIsInWishlistTrueInWishlist(product.productId));
              notification.success({
                message: "Đã thêm vào Wishlist",
                description: `"${product.productName}" đã thêm vào Wishlist.`,
              });
            }}
          />
        </Tooltip>

        <Tooltip title={isInCompare ? "Đã thêm vào so sánh" : "Thêm vào so sánh"}>
          <Button
            shape="circle"
            icon={<FiBarChart2 />}
            size="large"
            disabled={isInCompare}
            style={{
              borderColor: isInCompare ? "#ccc" : "#80001C",
              color: isInCompare ? "#ccc" : "#80001C",
            }}
            onClick={() => {
              dispatch(AddToCompare(product));
              dispatch(MakeIsInCompareTrueInCompare(product.productId));
              notification.success({
                message: "Đã thêm vào so sánh",
                description: `"${product.productName}" đã thêm vào so sánh.`,
              });
            }}
          />
        </Tooltip>
      </div>

      <Divider />

      {/* ===== SKU, Danh mục, Thương hiệu ===== */}
      <div>
        <Text strong>SKU: </Text>
        <Text>{product.productId}</Text>
      </div>
      <div>
        <Text strong>Danh mục: </Text>
        <Tag color="#80001C" style={{ color: "white" }}>
          {product.category?.categoryName}
        </Tag>
      </div>
      <div>
        <Text strong>Thương hiệu: </Text>
        <Text>{product.brand?.brandName}</Text>
      </div>
    </Card>
  );
};

export default ProductInfo;
