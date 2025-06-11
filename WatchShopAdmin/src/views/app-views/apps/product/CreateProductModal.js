import React, { useState } from "react";
import {
  Modal,
  Form,
  Input,
  Spin,
  Switch,
  Tabs,
  Upload,
  notification,
  Button,
  Select,
  Row,
  Col,
  Typography,
} from "antd";
import { createProduct } from "services/productService";
import { PlusOutlined, MinusCircleOutlined } from "@ant-design/icons";
import { Editor } from "@tinymce/tinymce-react";

const { TabPane } = Tabs;
const { Option } = Select;
const { Text } = Typography;
const PRIMARY_COLOR = "#24a772";

const CreateProductModal = ({
  visible,
  onCancel,
  refreshProducts,
  categories,
  brands,
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [images, setImages] = useState([]);
  const [variations, setVariations] = useState([]);
  const [content, setContent] = useState("");
  const [contentError, setContentError] = useState("");
  const [isNewProduct, setIsNewProduct] = useState(false);
  const [isSale, setIsSale] = useState(false);
  const [isSpecial, setIsSpecial] = useState(false);

  const handleFinish = async (values) => {
    if (!content) {
      setContentError("Vui lòng nhập mô tả sản phẩm!");
      notification.error({ message: "Thiếu mô tả", description: contentError });
      return;
    }

    if (variations.some((v) => !v.attributeName || !v.attributeValue || !v.price || !v.quantity)) {
      notification.error({
        message: "Lỗi biến thể",
        description: "Vui lòng nhập đầy đủ thông tin cho tất cả biến thể!",
      });
      return;
    }

    setLoading(true);
    try {
      const formData = new FormData();
      formData.append("productName", values.productName);
      formData.append("price", values.price);
      formData.append("description", content);
      formData.append("discount", values.discount ?? "");
      formData.append("badge", values.badge ?? "");
      formData.append("stock", values.stock);
      formData.append("isNewProduct", isNewProduct);
      formData.append("isSale", isSale);
      formData.append("isSpecial", isSpecial);
      formData.append("categoryId", values.categoryId);
      formData.append("brandId", values.brandId);

      images.forEach((img) => formData.append("images", img));

      if (variations.length > 0) {
        formData.append("variations", JSON.stringify(variations));
      }

      await createProduct(formData);
      refreshProducts();
      form.resetFields();
      setImages([]);
      setContent("");
      setContentError("");
      setIsNewProduct(false);
      setIsSale(false);
      setIsSpecial(false);
      setVariations([]);
      onCancel();
      notification.success({ message: "Thành công", description: "Thêm sản phẩm thành công" });
    } catch {
      notification.error({ message: "Thất bại", description: "Thêm sản phẩm thất bại" });
    } finally {
      setLoading(false);
    }
  };

  const handleImageChange = (fileList) => {
    const newImages = fileList.map((file) => file.originFileObj);
    setImages(newImages);
  };

  const removeImage = (index) => {
    const newImages = images.filter((_, i) => i !== index);
    setImages(newImages);
  };

  const addVariation = () => {
    setVariations([...variations, { attributeName: "", attributeValue: "", price: "", quantity: "" }]);
  };

  const removeVariation = (index) => {
    setVariations(variations.filter((_, i) => i !== index));
  };

  const handleVariationChange = (index, field, value) => {
    const updated = [...variations];
    updated[index][field] = value;
    setVariations(updated);
  };

  return (
    <Modal
      open={visible}
      title={<span style={{ color: PRIMARY_COLOR }}>Thêm Sản Phẩm Mới</span>}
      okText="Thêm"
      cancelText="Hủy"
      onCancel={onCancel}
      onOk={() => form.submit()}
      okButtonProps={{
        style: { backgroundColor: PRIMARY_COLOR, borderColor: PRIMARY_COLOR },
      }}
      width={850}
    >
      <Spin spinning={loading}>
        <Tabs defaultActiveKey="1">
          <TabPane tab="Thông Tin" key="1">
            <Form form={form} layout="vertical" onFinish={handleFinish}>
              <Form.Item name="productName" label="Tên Sản Phẩm" rules={[{ required: true, message: "Nhập tên sản phẩm" }]}>
                <Input placeholder="Nhập tên..." />
              </Form.Item>

              <Form.Item label="Mô Tả" required>
                <Editor
                  apiKey="igjpx91ezhzid8fokbcr4lo6ptz5ak4icvy0f9b6auggb44g"
                  value={content}
                  onEditorChange={setContent}
                  init={{
                    height: 300,
                    menubar: false,
                    plugins: "lists link image preview code",
                    toolbar: "undo redo | bold italic | bullist numlist | link image",
                  }}
                />
                {contentError && <Text type="danger">{contentError}</Text>}
              </Form.Item>

              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item name="price" label="Giá" rules={[{ required: true, message: "Nhập giá" }]}>
                    <Input type="number" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item name="discount" label="Giảm Giá">
                    <Input type="number" />
                  </Form.Item>
                </Col>
              </Row>

              <Form.Item name="stock" label="Số Lượng" rules={[{ required: true, message: "Nhập số lượng" }]}>
                <Input type="number" />
              </Form.Item>

              <Form.Item name="badge" label="Nhãn">
                <Input />
              </Form.Item>

              <Row gutter={16}>
                <Col span={8}>
                  <Form.Item label="Mới" valuePropName="checked">
                    <Switch checked={isNewProduct} onChange={setIsNewProduct} />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item label="Sale" valuePropName="checked">
                    <Switch checked={isSale} onChange={setIsSale} />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item label="Đặc Biệt" valuePropName="checked">
                    <Switch checked={isSpecial} onChange={setIsSpecial} />
                  </Form.Item>
                </Col>
              </Row>

              <Form.Item name="categoryId" label="Danh Mục" rules={[{ required: true, message: "Chọn danh mục" }]}>
                <Select placeholder="Chọn danh mục">
                  {categories.map((cat) => (
                    <Option key={cat.categoryId} value={cat.categoryId}>
                      {cat.categoryName}
                    </Option>
                  ))}
                </Select>
              </Form.Item>

              <Form.Item name="brandId" label="Thương Hiệu" rules={[{ required: true, message: "Chọn thương hiệu" }]}>
                <Select placeholder="Chọn thương hiệu">
                  {brands.map((b) => (
                    <Option key={b.brandId} value={b.brandId}>
                      {b.brandName}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Form>
          </TabPane>

          <TabPane tab="Hình Ảnh" key="2">
            <Upload
              multiple
              beforeUpload={(file) => {
                setImages((prev) => [...prev, file]);
                return false;
              }}
              showUploadList={false}
            >
              <Button icon={<PlusOutlined />}>Tải lên</Button>
            </Upload>
            <div style={{ marginTop: 16 }}>
              {images.map((img, i) => (
                <div key={i} style={{ display: "inline-block", marginRight: 10 }}>
                  <img src={URL.createObjectURL(img)} alt="" style={{ width: 100, height: 100 }} />
                  <Button type="link" danger onClick={() => removeImage(i)}>
                    Xóa
                  </Button>
                </div>
              ))}
            </div>
          </TabPane>

          <TabPane tab="Lựa Chọn" key="3">
            <Button type="dashed" onClick={addVariation} style={{ marginBottom: 16 }}>
              <PlusOutlined /> Thêm biến thể
            </Button>
            {variations.map((v, i) => (
              <Row key={i} gutter={16} style={{ marginBottom: 10 }}>
                <Col span={5}>
                  <Input
                    placeholder="Tên thuộc tính"
                    value={v.attributeName}
                    onChange={(e) => handleVariationChange(i, "attributeName", e.target.value)}
                  />
                </Col>
                <Col span={5}>
                  <Input
                    placeholder="Giá trị"
                    value={v.attributeValue}
                    onChange={(e) => handleVariationChange(i, "attributeValue", e.target.value)}
                  />
                </Col>
                <Col span={5}>
                  <Input
                    placeholder="Giá"
                    type="number"
                    value={v.price}
                    onChange={(e) => handleVariationChange(i, "price", e.target.value)}
                  />
                </Col>
                <Col span={5}>
                  <Input
                    placeholder="Số lượng"
                    type="number"
                    value={v.quantity}
                    onChange={(e) => handleVariationChange(i, "quantity", e.target.value)}
                  />
                </Col>
                <Col span={2}>
                  <MinusCircleOutlined
                    onClick={() => removeVariation(i)}
                    style={{ fontSize: 18, color: "red", marginTop: 6 }}
                  />
                </Col>
              </Row>
            ))}
          </TabPane>
        </Tabs>
      </Spin>
    </Modal>
  );
};

export default CreateProductModal;
