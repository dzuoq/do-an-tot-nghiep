import React, { useState } from "react";
import { Link } from "react-router-dom";
import { Table, Button, Tooltip, Avatar, Typography, Modal, Tag } from "antd";
import { HiArrowNarrowLeft } from "react-icons/hi";
import { BsBag, BsTrash } from "react-icons/bs";
import { useSelector, useDispatch } from "react-redux";
import { RootState } from "../../redux/reducers";
import { AddToCart, MakeIsInCartTrue } from "../../redux/actions/cartActions";
import { RemoveFromWishlist } from "../../redux/actions/wishlistActions";
import { MakeIsInWishlistFalse } from "../../redux/actions/productActions";
import { toast } from "react-toastify";
import { formatCurrency } from "../../utils/currencyFormatter";
import ImgSlider from "../ProductDetails/PrimaryInfo/ImgSlider";
import ProductInfo from "../ProductDetails/PrimaryInfo/ProductInfo";

const { Text, Title } = Typography;

const WishlistSection: React.FC = () => {
  const [showModal, setShowModal] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState<any>(null);
  const wishlist = useSelector((state: RootState) => state.wishlist.wishlist);
  const cart = useSelector((state: RootState) => state.cart.cart);
  const dispatch = useDispatch();

  const handleClose = () => setShowModal(false);
  const handleShow = (product: any) => {
    setSelectedProduct(product);
    setShowModal(true);
  };

  const handleRemove = (productId: number, productName: string) => {
    dispatch(RemoveFromWishlist(productId));
    dispatch(MakeIsInWishlistFalse(productId));
    toast.error(`"${productName}" đã được xóa khỏi danh sách yêu thích.`);
  };

  const handleAddToCart = (product: any) => {
    if (product.variations.length > 0) {
      handleShow(product);
    } else {
      dispatch(AddToCart(product));
      dispatch(MakeIsInCartTrue(product.productId));
      toast.success(`"${product.productName}" đã thêm vào giỏ hàng.`);
    }
  };

  const columns = [
    {
      title: "Hình ảnh",
      dataIndex: "images",
      key: "images",
      render: (images: any[]) => {
        const defaultImage = images.find((img) => img.isDefault)?.imageUrl || "/placeholder-image.jpg";
        return <Avatar shape="square" size={64} src={defaultImage} />;
      },
    },
    {
      title: "Tên sản phẩm",
      dataIndex: "productName",
      key: "productName",
      render: (text: string, record: any) => (
        <Link to={`/product-details/${record.productId}`} style={{ color: "#80001C", fontWeight: "bold" }}>
          {text}
        </Link>
      ),
    },
    {
      title: "Giá",
      dataIndex: "price",
      key: "price",
      render: (price: number, record: any) => {
        const discountPrice = price - (price * record.discount) / 100;
        return (
          <div>
            {record.discount ? (
              <>
                <Text delete style={{ color: "gray", marginRight: "8px" }}>
                  {formatCurrency(price)}
                </Text>
                <Text strong style={{ color: "red" }}>
                  {formatCurrency(discountPrice)}
                </Text>
              </>
            ) : (
              <Text strong>{formatCurrency(price)}</Text>
            )}
          </div>
        );
      },
    },
    {
      title: "Danh mục",
      dataIndex: "category",
      key: "category",
      render: (category: { categoryName: string }) => <Tag color="#80001C">{category.categoryName}</Tag>,
    },
    {
      title: "Hành động",
      key: "action",
      render: (_: any, record: any) => {
        const isInCart = cart.some((cartProduct: any) => cartProduct.productId === record.productId);
        return (
          <div style={{ display: "flex", gap: "8px" }}>
            <Tooltip title={isInCart ? "Đã thêm vào giỏ hàng" : "Thêm vào giỏ hàng"}>
              <Button
                type="primary"
                icon={<BsBag />}
                disabled={isInCart}
                style={{
                  backgroundColor: isInCart ? "#ccc" : "#80001C",
                  borderColor: "#80001C",
                }}
                onClick={() => handleAddToCart(record)}
              >
                {isInCart ? "Đã thêm" : "Thêm vào giỏ"}
              </Button>
            </Tooltip>
            <Tooltip title="Xóa khỏi yêu thích">
              <Button danger icon={<BsTrash />} onClick={() => handleRemove(record.productId, record.productName)} />
            </Tooltip>
          </div>
        );
      },
    },
  ];

  return (
    <section id="wishlist">
      <div className="container">
        <div className="row">
          <div className="col-12 text-center">
            <Title level={2} style={{ color: "#80001C" }}>Danh sách yêu thích</Title>
          </div>
        </div>

        {wishlist.length > 0 ? (
          <Table columns={columns} dataSource={wishlist} pagination={false} style={{ marginTop: "20px" }} rowKey="productId" />
        ) : (
          <div style={{ textAlign: "center", marginTop: "50px" }}>
            <Text strong style={{ fontSize: "18px", color: "#80001C" }}>
              Danh sách yêu thích của bạn hiện đang trống.
            </Text>
            <div style={{ marginTop: "20px" }}>
              <Link to="/shop">
                <Button type="primary" icon={<HiArrowNarrowLeft />} style={{ backgroundColor: "#80001C", borderColor: "#80001C" }}>
                  Quay lại mua sắm
                </Button>
              </Link>
            </div>
          </div>
        )}
      </div>

      {/* ======= Modal để hiển thị thông tin sản phẩm có variations ======= */}
      <Modal open={showModal} onCancel={handleClose} footer={null} centered width={800}>
        <div style={{ padding: "20px" }}>
          <div className="row">
            <div className="col-lg-6">
              <ImgSlider product={selectedProduct} />
            </div>
            <div className="col-lg-6">
              <ProductInfo product={selectedProduct} />
            </div>
          </div>
        </div>
      </Modal>
    </section>
  );
};

export default WishlistSection;
