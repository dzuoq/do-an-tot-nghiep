import {
  Table,
  Button,
  Popconfirm,
  Pagination,
  Input,
  Spin,
  Card,
  Select,
  Avatar,
  Tooltip,
  Space,
} from "antd";
import React, { useEffect, useState, useCallback } from "react";
import {
  EditOutlined,
  DeleteOutlined,
  PlusOutlined,
  PictureOutlined,
} from "@ant-design/icons";
import { getAllProducts, deleteProduct } from "services/productService";
import { getAllCategories } from "services/categoryService";
import { getAllBrands } from "services/brandService";
import { debounce } from "lodash";
import { formatCurrency } from "utils/formatCurrency";
import CreateProductModal from "./CreateProductModal";
import EditProductModal from "./EditProductModal";
import ProductImageModal from "./ProductImageModal";

const { Option } = Select;
const PRIMARY_COLOR = "#24a772";

export default function ProductManagement() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [brands, setBrands] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [selectedBrand, setSelectedBrand] = useState(null);
  const [editProductData, setEditProductData] = useState(null);
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isImageModalOpen, setIsImageModalOpen] = useState(false);
  const [productId, setProductId] = useState("");
  const limit = 5;

  const fetchProducts = useCallback(
    async (
      search = searchTerm,
      page = currentPage,
      categoryId = selectedCategory,
      brandId = selectedBrand
    ) => {
      setLoading(true);
      try {
        const data = await getAllProducts(search, categoryId, brandId, page, limit);
        setProducts(data.content);
        setTotalPages(data.totalPages);
      } finally {
        setLoading(false);
      }
    },
    [currentPage, limit, selectedCategory, selectedBrand]
  );

  const fetchCategoriesAndBrands = async () => {
    const [categoryData, brandData] = await Promise.all([
      getAllCategories(),
      getAllBrands(),
    ]);
    setCategories(categoryData.content);
    setBrands(brandData.content);
  };

  const debouncedFetchProducts = useCallback(
    debounce((value) => {
      setCurrentPage(1);
      fetchProducts(value, 1);
    }, 800),
    [fetchProducts]
  );

  useEffect(() => {
    fetchProducts(searchTerm, currentPage);
  }, [fetchProducts, currentPage]);

  useEffect(() => {
    fetchCategoriesAndBrands();
  }, []);

  const handleCategoryChange = (value) => {
    setSelectedCategory(value);
    setCurrentPage(1);
    fetchProducts(searchTerm, 1, value, selectedBrand);
  };

  const handleBrandChange = (value) => {
    setSelectedBrand(value);
    setCurrentPage(1);
    fetchProducts(searchTerm, 1, selectedCategory, value);
  };

  const confirmDeleteProduct = async (productId) => {
    try {
      await deleteProduct(productId);
      fetchProducts();
    } catch { }
  };

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  const handleEditClick = (record) => {
    setEditProductData(record);
    setIsEditOpen(true);
  };

  const handleEditImages = async (productId) => {
    setProductId(productId);
    setIsImageModalOpen(true);
  };

  const columns = [
    {
      title: "Tên Sản Phẩm",
      dataIndex: "productName",
      key: "productName",
    },
    {
      title: "Giá",
      dataIndex: "price",
      key: "price",
      render: (text) => formatCurrency(text),
    },
    {
      title: "Giảm Giá (%)",
      dataIndex: "discount",
      key: "discount",
      render: (text) => (text ? `${text}%` : "0%"),
    },
    {
      title: "Giá Sau Giảm",
      key: "discountedPrice",
      render: (_, record) => {
        const discounted = record.discount
          ? record.price - (record.price * record.discount) / 100
          : record.price;
        return formatCurrency(discounted);
      },
    },
    {
      title: "Danh Mục",
      dataIndex: ["category", "categoryName"],
      key: "categoryName",
    },
    {
      title: "Thương Hiệu",
      dataIndex: ["brand", "brandName"],
      key: "brandName",
    },
    {
      title: "Hình Ảnh",
      dataIndex: "images",
      key: "images",
      render: (images) => (
        <div style={{ display: "flex", alignItems: "center" }}>
          {images.slice(0, 3).map((img) => (
            <Avatar key={img.imageId} src={img.imageUrl} size={40} style={{ marginRight: 5 }} />
          ))}
          {images.length > 3 && <span>+{images.length - 3}</span>}
        </div>
      ),
    },
    {
      title: "Số Lượng",
      dataIndex: "stock",
      key: "stock",
    },
    {
      title: "Hành Động",
      key: "actions",
      align: "center",
      render: (_, record) => (
        <Space>
          <Tooltip title="Sửa">
            <EditOutlined
              onClick={() => handleEditClick(record)}
              style={{ color: PRIMARY_COLOR, fontSize: 16, cursor: "pointer" }}
            />
          </Tooltip>
          <Tooltip title="Hình Ảnh">
            <PictureOutlined
              onClick={() => handleEditImages(record.productId)}
              style={{ fontSize: 16, cursor: "pointer" }}
            />
          </Tooltip>
          <Popconfirm
            title="Bạn có chắc muốn xóa sản phẩm này?"
            onConfirm={() => confirmDeleteProduct(record.productId)}
            okText="Có"
            cancelText="Không"
          >
            <Tooltip title="Xóa">
              <DeleteOutlined style={{ color: "red", fontSize: 16, cursor: "pointer" }} />
            </Tooltip>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <Card>
      <div style={{ display: "flex", gap: 10, marginBottom: 20 }}>
        <Input
          placeholder="Tìm kiếm sản phẩm..."
          allowClear
          value={searchTerm}
          onChange={(e) => {
            setSearchTerm(e.target.value);
            debouncedFetchProducts(e.target.value);
          }}
          style={{ flex: 2 }}
        />
        <Select
          placeholder="Danh Mục"
          onChange={handleCategoryChange}
          style={{ flex: 1 }}
          allowClear
        >
          {categories.map((cat) => (
            <Option key={cat.categoryId} value={cat.categoryId}>
              {cat.categoryName}
            </Option>
          ))}
        </Select>
        <Select
          placeholder="Thương Hiệu"
          onChange={handleBrandChange}
          style={{ flex: 1 }}
          allowClear
        >
          {brands.map((brand) => (
            <Option key={brand.brandId} value={brand.brandId}>
              {brand.brandName}
            </Option>
          ))}
        </Select>
        <Tooltip title="Thêm Mới">
          <Button
            type="primary"
            icon={<PlusOutlined />}
            style={{ backgroundColor: PRIMARY_COLOR, borderColor: PRIMARY_COLOR }}
            onClick={() => setIsCreateOpen(true)}
          />
        </Tooltip>
      </div>

      {loading ? (
        <Spin size="large" style={{ display: "block", textAlign: "center", marginTop: 32 }} />
      ) : (
        <>
          <Table
            columns={columns}
            dataSource={products}
            pagination={false}
            rowKey={(record) => record.productId}
            bordered
            size="middle"
          />
          <Pagination
            current={currentPage}
            total={totalPages * limit}
            pageSize={limit}
            onChange={handlePageChange}
            style={{ marginTop: 24, textAlign: "center" }}
          />
        </>
      )}

      <CreateProductModal
        visible={isCreateOpen}
        onCancel={() => setIsCreateOpen(false)}
        refreshProducts={fetchProducts}
        categories={categories}
        brands={brands}
      />

      <EditProductModal
        visible={isEditOpen}
        productData={editProductData}
        refreshProducts={fetchProducts}
        onCancel={() => setIsEditOpen(false)}
        categories={categories}
        brands={brands}
      />

      <ProductImageModal
        visible={isImageModalOpen}
        onClose={() => setIsImageModalOpen(false)}
        productId={productId}
      />
    </Card>
  );
}
