import React, { useEffect, useState } from "react";
import {
  Modal,
  Avatar,
  Table,
  Switch,
  Button,
  Spin,
  Popconfirm,
  Upload,
  notification,
  Typography,
} from "antd";
import { PlusOutlined } from "@ant-design/icons";
import {
  changeIsDefault,
  deleteProductImages,
  getProductImages,
  createProductImage,
} from "services/productImageService";

const { Text } = Typography;
const PRIMARY_COLOR = "#24a772";

const ProductImageModal = ({ visible, onClose, productId }) => {
  const [images, setImages] = useState([]);
  const [loading, setLoading] = useState(false);
  const [uploading, setUploading] = useState(false);

  useEffect(() => {
    if (visible && productId) {
      fetchProductImages();
    }
  }, [visible, productId]);

  const fetchProductImages = async () => {
    setLoading(true);
    try {
      const response = await getProductImages(productId);
      setImages(response);
    } catch {
      notification.error({
        message: "Lỗi",
        description: "Không thể tải danh sách hình ảnh.",
      });
    } finally {
      setLoading(false);
    }
  };

  const handleToggleDefault = async (imageId) => {
    try {
      await changeIsDefault(imageId, true);
      notification.success({
        message: "Thành công",
        description: "Đã đặt ảnh làm mặc định.",
      });
      fetchProductImages();
    } catch {
      notification.error({
        message: "Lỗi",
        description: "Không thể cập nhật trạng thái mặc định.",
      });
    }
  };

  const handleDeleteImage = async (imageId) => {
    try {
      await deleteProductImages(imageId);
      notification.success({
        message: "Đã xóa",
        description: "Hình ảnh đã được xóa thành công.",
      });
      fetchProductImages();
    } catch {
      notification.error({
        message: "Lỗi",
        description: "Không thể xóa hình ảnh.",
      });
    }
  };

  const handleUpload = async (file) => {
    setUploading(true);
    const formData = new FormData();
    formData.append("image", file);
    try {
      await createProductImage(productId, formData);
      notification.success({
        message: "Thành công",
        description: "Tải ảnh lên thành công.",
      });
      fetchProductImages();
    } catch {
      notification.error({
        message: "Lỗi",
        description: "Tải ảnh lên thất bại.",
      });
    } finally {
      setUploading(false);
    }
  };

  const columns = [
    {
      title: "Hình Ảnh",
      dataIndex: "imageUrl",
      key: "imageUrl",
      render: (text) => <Avatar src={text} size={80} />,
    },
    {
      title: "Mặc Định",
      dataIndex: "isDefault",
      key: "isDefault",
      render: (isDefault, record) => (
        <Switch
          checked={isDefault}
          onChange={() => handleToggleDefault(record.imageId)}
        />
      ),
    },
    {
      title: "Hành Động",
      key: "actions",
      render: (_, record) => (
        <Popconfirm
          title="Xác nhận xóa hình ảnh này?"
          onConfirm={() => handleDeleteImage(record.imageId)}
          okText="Có"
          cancelText="Không"
        >
          <Button type="link" danger>
            Xóa
          </Button>
        </Popconfirm>
      ),
    },
  ];

  return (
    <Modal
      open={visible}
      title={<span style={{ color: PRIMARY_COLOR }}>Hình Ảnh Sản Phẩm</span>}
      onCancel={onClose}
      footer={null}
      width={800}
    >
      {loading ? (
        <div style={{ textAlign: "center", margin: "20px 0" }}>
          <Spin size="large" />
        </div>
      ) : (
        <>
          <Table
            dataSource={images}
            columns={columns}
            rowKey="imageId"
            pagination={false}
            bordered
            size="middle"
          />

          <Upload
            customRequest={({ file }) => handleUpload(file)}
            showUploadList={false}
          >
            <Button
              icon={<PlusOutlined />}
              type="dashed"
              loading={uploading}
              style={{
                marginTop: 24,
                borderColor: PRIMARY_COLOR,
                color: PRIMARY_COLOR,
              }}
            >
              Thêm Ảnh Mới
            </Button>
          </Upload>
        </>
      )}
    </Modal>
  );
};

export default ProductImageModal;
