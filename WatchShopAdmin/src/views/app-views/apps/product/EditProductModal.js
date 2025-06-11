import React, { useState, useEffect } from "react";
import {
  Modal,
  Form,
  Input,
  Spin,
  Switch,
  Tabs,
  notification,
  Button,
  Select,
  Row,
  Col,
  Typography,
} from "antd";
import { PlusOutlined, MinusCircleOutlined } from "@ant-design/icons";
import { Editor } from "@tinymce/tinymce-react";
import { updateProduct } from "services/productService";

const { TabPane } = Tabs;
const { Option } = Select;
const { Text } = Typography;
const PRIMARY_COLOR = "#24a772";

const EditProductModal = ({
  visible,
  onCancel,
  refreshProducts,
  categories,
  brands,
  productData,
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [variations, setVariations] = useState([]);
  const [content, setContent] = useState("");

  useEffect(() => {
    if (visible && productData) {
      form.setFieldsValue({
        productName: productData.productName,
        price: productData.price,
        discount: productData.discount,
        badge: productData.badge,
        stock: productData.stock,
        categoryId: productData.category.categoryId,
        brandId: productData.brand.brandId,
        isNewProduct: productData.isNewProduct,
        isSale: productData.isSale,
        isSpecial: productData.isSpecial,
      });
      setContent(productData.description);
      setVariations(productData.variations || []);
    }
  }, [visible, productData, form]);

  const handleFinish = async (values) => {
    if (!content) {
      notification.error({
        message: "Thiếu mô tả",
        description: "Vui lòng nhập mô tả sản phẩm!",
      });
      return;
    }

    if (variations.some((v) => !v.attributeName || !v.attributeValue || !v.price || !v.quantity)) {
      notification.error({
        message: "Thiếu thông tin biến thể",
        description: "Vui lòng nhập đầy đủ các biến thể!",
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
      formData.append("isNewProduct", values.isNewProduct);
      formData.append("isSale", values.isSale);
      formData.append("isSpecial", values.isSpecial);
      formData.append("categoryId", values.categoryId);
      formData.append("brandId", values.brandId);

      if (variations.length > 0) {
        formData.append("variations", JSON.stringify(variations));
      }

      await updateProduct(productData.productId, formData);
      refreshProducts();
      form.resetFields();
      setVariations([]);
      setContent("");
      onCancel();
      notification.success({
        message: "Thành công",
        description: "Cập nhật sản phẩm thành công",
      });
    } catch {
      notification.error({
        message: "Thất bại",
        description: "Cập nhật sản phẩm thất bại",
      });
    } finally {
      setLoading(false);
    }
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
      title={<span style={{ color: PRIMARY_COLOR }}>Chỉnh Sửa Sản Phẩm</span>}
      okText="Cập Nhật"
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
              <Form.Item name="productName" label="Tên Sản Phẩm" rules={[{ required: true }]}>
                <Input />
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
              </Form.Item>
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item name="price" label="Giá" rules={[{ required: true }]}>
                    <Input type="number" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item name="discount" label="Giảm Giá">
                    <Input type="number" />
                  </Form.Item>
                </Col>
              </Row>
              <Form.Item name="stock" label="Số Lượng" rules={[{ required: true }]}>
                <Input type="number" />
              </Form.Item>
              <Form.Item name="badge" label="Nhãn">
                <Input />
              </Form.Item>
              <Row gutter={16}>
                <Col span={8}>
                  <Form.Item label="Mới" name="isNewProduct" valuePropName="checked">
                    <Switch />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item label="Sale" name="isSale" valuePropName="checked">
                    <Switch />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item label="Đặc Biệt" name="isSpecial" valuePropName="checked">
                    <Switch />
                  </Form.Item>
                </Col>
              </Row>
              <Form.Item name="categoryId" label="Danh Mục" rules={[{ required: true }]}>
                <Select placeholder="Chọn danh mục">
                  {categories.map((cat) => (
                    <Option key={cat.categoryId} value={cat.categoryId}>
                      {cat.categoryName}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
              <Form.Item name="brandId" label="Thương Hiệu" rules={[{ required: true }]}>
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
          <TabPane tab="Lựa Chọn" key="2">
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

export default EditProductModal;
