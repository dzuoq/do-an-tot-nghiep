import React, { useState } from "react";
import { Modal, Form, Input, Spin, Typography } from "antd";
import { createBrand } from "services/brandService";

const { Text } = Typography;
const PRIMARY_COLOR = "#24a772";

const CreateBrandModal = ({ visible, onCancel, refreshBrands }) => {
  const [form] = Form.useForm();
  const [imageFile, setImageFile] = useState(null);
  const [imagePreviewUrl, setImagePreviewUrl] = useState("https://via.placeholder.com/150");
  const [loading, setLoading] = useState(false);

  const handleFinish = async (values) => {
    if (!imageFile) return;

    if (imageFile.size > 500 * 1024) {
      form.setFields([
        {
          name: "image",
          errors: ["Kích thước file hình ảnh không được vượt quá 500KB."],
        },
      ]);
      return;
    }

    setLoading(true);
    try {
      await createBrand(values.brandName, values.description, imageFile);
      refreshBrands();
      form.resetFields();
      setImageFile(null);
      setImagePreviewUrl("https://via.placeholder.com/150");
      onCancel();
    } catch (error) {
      console.error("Lỗi khi thêm thương hiệu:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleImageChange = (event) => {
    const file = event.target.files[0];
    const isImage = file && (file.type === "image/jpeg" || file.type === "image/png");

    if (!isImage) {
      form.setFields([
        {
          name: "image",
          errors: ["Vui lòng chọn ảnh JPG hoặc PNG."],
        },
      ]);
      setImageFile(null);
      return;
    }

    setImageFile(file);
    const reader = new FileReader();
    reader.onload = () => setImagePreviewUrl(reader.result);
    reader.readAsDataURL(file);
  };

  return (
    <Modal
      open={visible}
      title={<span style={{ color: PRIMARY_COLOR }}>Thêm Mới Thương Hiệu</span>}
      okText="Thêm"
      cancelText="Hủy"
      onCancel={onCancel}
      onOk={() => form.submit()}
      okButtonProps={{
        style: { backgroundColor: PRIMARY_COLOR, borderColor: PRIMARY_COLOR },
      }}
    >
      <Form form={form} layout="vertical" onFinish={handleFinish}>
        <Form.Item
          name="brandName"
          label="Tên Thương Hiệu"
          rules={[{ required: true, message: "Vui lòng nhập tên thương hiệu!" }]}
        >
          <Input placeholder="Nhập tên thương hiệu" />
        </Form.Item>

        <Form.Item
          name="description"
          label="Mô Tả"
          rules={[{ required: true, message: "Vui lòng nhập mô tả!" }]}
        >
          <Input.TextArea rows={3} placeholder="Mô tả chi tiết..." />
        </Form.Item>

        <Form.Item
          name="image"
          label="Tải Hình Ảnh"
          rules={[{ required: true, message: "Vui lòng chọn hình ảnh!" }]}
        >
          <Input
            type="file"
            onChange={handleImageChange}
            accept=".jpg,.jpeg,.png"
            style={{
              border: `1px dashed ${PRIMARY_COLOR}`,
              padding: 8,
            }}
          />
        </Form.Item>

        <div style={{ textAlign: "center", marginTop: 10 }}>
          <img
            src={imagePreviewUrl}
            alt="Preview"
            style={{
              marginTop: 10,
              maxWidth: "60%",
              borderRadius: 12,
              boxShadow: "0 0 6px rgba(0,0,0,0.1)",
            }}
          />
        </div>
      </Form>

      {loading && <Spin style={{ display: "block", margin: "16px auto" }} />}
    </Modal>
  );
};

export default CreateBrandModal;
