import React, { useEffect, useState } from "react";
import { Modal, Form, Input, Spin, Typography, message } from "antd";
import { updateBrand } from "services/brandService";

const { Text } = Typography;
const PRIMARY_COLOR = "#24a772";

const EditBrandModal = ({ visible, brandData, onCancel, refreshBrands }) => {
  const [form] = Form.useForm();
  const [imageFile, setImageFile] = useState(null);
  const [imagePreviewUrl, setImagePreviewUrl] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (brandData) {
      form.setFieldsValue({
        brandName: brandData.brandName,
        description: brandData.description,
      });
      setImagePreviewUrl(brandData.image);
    }
  }, [brandData, form]);

  const handleFinish = async (values) => {
    setLoading(true);

    if (!imageFile && !imagePreviewUrl) {
      form.setFields([{ name: "image", errors: ["Vui lòng chọn hình ảnh."] }]);
      setLoading(false);
      return;
    }

    if (imageFile && imageFile.size > 500 * 1024) {
      form.setFields([{ name: "image", errors: ["Ảnh không vượt quá 500KB."] }]);
      setLoading(false);
      return;
    }

    try {
      await updateBrand(
        brandData.brandId,
        values.brandName,
        values.description,
        imageFile
      );
      message.success("Cập nhật thương hiệu thành công.");
      refreshBrands();
      form.resetFields();
      setImageFile(null);
      setImagePreviewUrl(null);
      onCancel();
    } catch (error) {
      message.error("Lỗi khi cập nhật thương hiệu.");
    } finally {
      setLoading(false);
    }
  };

  const handleImageChange = (event) => {
    const file = event.target.files[0];
    const isImage = file && (file.type === "image/jpeg" || file.type === "image/png");

    if (!isImage) {
      form.setFields([{ name: "image", errors: ["Chỉ chấp nhận ảnh JPG hoặc PNG."] }]);
      setImageFile(null);
      return;
    }

    if (file.size > 500 * 1024) {
      form.setFields([{ name: "image", errors: ["Ảnh không vượt quá 500KB."] }]);
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
      title={<span style={{ color: PRIMARY_COLOR }}>Chỉnh Sửa Thương Hiệu</span>}
      okText="Lưu"
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
          <Input.TextArea rows={3} placeholder="Mô tả thương hiệu..." />
        </Form.Item>

        <Form.Item label="Tải Hình Ảnh" required>
          <Input
            type="file"
            onChange={handleImageChange}
            accept=".jpg,.jpeg,.png"
            style={{ border: `1px dashed ${PRIMARY_COLOR}`, padding: 8 }}
          />
          <Form.Item name="image" style={{ display: "none" }}>
            <Input />
          </Form.Item>
        </Form.Item>

        {imagePreviewUrl && (
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
        )}
      </Form>

      {loading && <Spin style={{ display: "block", margin: "16px auto" }} />}
    </Modal>
  );
};

export default EditBrandModal;
