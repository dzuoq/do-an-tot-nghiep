import React, { useState } from "react";
import { Modal, Form, Input, Spin, message, Upload, Typography } from "antd";
import { UploadOutlined } from "@ant-design/icons";
import { createCategory } from "services/categoryService";

const { Text } = Typography;

const PRIMARY_COLOR = "#24a772";

const CreateCategoryModal = ({ visible, onCancel, refreshCategories }) => {
  const [form] = Form.useForm();
  const [imageFile, setImageFile] = useState(null);
  const [imagePreviewUrl, setImagePreviewUrl] = useState("https://via.placeholder.com/150");
  const [loading, setLoading] = useState(false);

  const handleFinish = async (values) => {
    if (!imageFile) return;

    if (imageFile.size > 500 * 1024) {
      form.setFields([{ name: "image", errors: ["Kích thước ảnh không vượt quá 500KB."] }]);
      return;
    }

    setLoading(true);
    try {
      await createCategory(values.categoryName, values.description, imageFile);
      refreshCategories();
      form.resetFields();
      setImageFile(null);
      setImagePreviewUrl("https://via.placeholder.com/150");
      onCancel();
      message.success("Thêm danh mục thành công");
    } catch (error) {
      message.error("Thêm danh mục thất bại");
    } finally {
      setLoading(false);
    }
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    const isImage = file && (file.type === "image/jpeg" || file.type === "image/png");

    if (!isImage) {
      form.setFields([{ name: "image", errors: ["Vui lòng chọn ảnh JPG hoặc PNG."] }]);
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
      title={<span style={{ color: PRIMARY_COLOR }}>Thêm Mới Danh Mục</span>}
      okText="Thêm"
      cancelText="Hủy"
      onCancel={onCancel}
      onOk={() => form.submit()}
      okButtonProps={{ style: { backgroundColor: PRIMARY_COLOR, borderColor: PRIMARY_COLOR } }}
    >
      <Form form={form} layout="vertical" onFinish={handleFinish}>
        <Form.Item
          name="categoryName"
          label="Tên Danh Mục"
          rules={[{ required: true, message: "Vui lòng nhập tên danh mục!" }]}
        >
          <Input placeholder="Nhập tên danh mục" />
        </Form.Item>

        <Form.Item
          name="description"
          label="Mô Tả"
          rules={[{ required: true, message: "Vui lòng nhập mô tả!" }]}
        >
          <Input.TextArea rows={3} placeholder="Nhập mô tả chi tiết..." />
        </Form.Item>

        <Form.Item
          name="image"
          label="Tải Ảnh"
          rules={[{ required: true, message: "Vui lòng chọn hình ảnh!" }]}
        >
          <Input
            type="file"
            onChange={handleImageChange}
            accept=".jpg,.jpeg,.png"
            style={{ border: `1px dashed ${PRIMARY_COLOR}`, padding: "8px" }}
          />
        </Form.Item>

        <div style={{ textAlign: "center", marginTop: 10 }}>
          <Text>Xem trước ảnh:</Text>
          <img
            src={imagePreviewUrl}
            alt="Preview"
            style={{
              display: "block",
              margin: "10px auto",
              maxWidth: "60%",
              borderRadius: "12px",
              boxShadow: "0 0 5px rgba(0,0,0,0.1)",
            }}
          />
        </div>
      </Form>

      {loading && <Spin style={{ display: "block", margin: "16px auto" }} />}
    </Modal>
  );
};

export default CreateCategoryModal;
