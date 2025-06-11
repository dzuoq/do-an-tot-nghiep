import {
  Table,
  Popconfirm,
  Pagination,
  Input,
  message,
  Spin,
  Card,
  Row,
  Col,
  Space,
  Tooltip,
  Button,
} from "antd";
import {
  EditOutlined,
  DeleteOutlined,
  PlusOutlined,
} from "@ant-design/icons";
import React, { useEffect, useState, useCallback } from "react";
import { getAllCategories, deleteCategory } from "services/categoryService";
import { debounce } from "lodash";
import CreateCategoryModal from "./CreateCategoryModal";
import EditCategoryModal from "./EditCategoryModal";

export default function CategoryManagement() {
  const [categories, setCategories] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [editCategoryData, setEditCategoryData] = useState(null);
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const limit = 5;

  const fetchCategories = useCallback(async (search = searchTerm, page = currentPage) => {
    setLoading(true);
    try {
      const data = await getAllCategories(page, limit, search);
      setCategories(data.content);
      setTotalPages(data.totalPages);
    } catch (error) {
      message.error("Lỗi khi lấy danh mục.");
    } finally {
      setLoading(false);
    }
  }, [currentPage, limit]);

  const debouncedFetchCategories = useCallback(
    debounce((value) => {
      setCurrentPage(1);
      fetchCategories(value, 1);
    }, 800),
    [fetchCategories]
  );

  useEffect(() => {
    fetchCategories(searchTerm, currentPage);
  }, [fetchCategories, currentPage]);

  const confirmDeleteCategory = async (categoryId) => {
    try {
      await deleteCategory(categoryId);
      message.success("Đã xóa danh mục.");
      fetchCategories();
    } catch (error) {
      message.error("Lỗi khi xóa danh mục.");
    }
  };

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  const handleEditClick = (record) => {
    setEditCategoryData(record);
    setIsEditOpen(true);
  };

  const PRIMARY_COLOR = "#24a772";

  const columns = [
    {
      title: "Tên Danh Mục",
      dataIndex: "categoryName",
      key: "categoryName",
    },
    {
      title: "Hình Ảnh",
      dataIndex: "image",
      key: "image",
      render: (text) => (
        <img
          src={text || "https://via.placeholder.com/50"}
          alt="Danh mục"
          width={50}
          height={50}
          style={{ borderRadius: "10%" }}
        />
      ),
    },
    {
      title: "Hành Động",
      key: "actions",
      align: "center",
      render: (text, record) => (
        <Space size="middle">
          <Tooltip title="Sửa">
            <EditOutlined
              style={{ color: PRIMARY_COLOR, fontSize: 16, cursor: "pointer" }}
              onClick={() => handleEditClick(record)}
            />
          </Tooltip>
          <Tooltip title="Xóa">
            <Popconfirm
              title="Bạn có chắc muốn xóa danh mục này?"
              onConfirm={() => confirmDeleteCategory(record.categoryId)}
              okText="Có"
              cancelText="Không"
            >
              <DeleteOutlined
                style={{ color: "red", fontSize: 16, cursor: "pointer" }}
              />
            </Popconfirm>
          </Tooltip>
        </Space>
      ),
    },
  ];

  return (
    <Card>
      <Row justify="space-between" align="middle" style={{ marginBottom: 24 }}>
        <Col span={16}>
          <Input
            placeholder="Tìm kiếm danh mục..."
            allowClear
            value={searchTerm}
            onChange={(e) => {
              setSearchTerm(e.target.value);
              debouncedFetchCategories(e.target.value);
            }}
          />
        </Col>
        <Col>
          <Tooltip title="Thêm Mới">
            <Button
              type="primary"
              icon={<PlusOutlined />}
              style={{ backgroundColor: PRIMARY_COLOR, borderColor: PRIMARY_COLOR }}
              onClick={() => setIsCreateOpen(true)}
            />
          </Tooltip>
        </Col>
      </Row>

      {loading ? (
        <Spin size="large" style={{ display: "block", textAlign: "center", marginTop: 32 }} />
      ) : (
        <>
          <Table
            columns={columns}
            dataSource={categories}
            pagination={false}
            rowKey={(record) => record.categoryId}
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

      <CreateCategoryModal
        visible={isCreateOpen}
        onCancel={() => setIsCreateOpen(false)}
        refreshCategories={fetchCategories}
      />

      <EditCategoryModal
        visible={isEditOpen}
        categoryData={editCategoryData}
        refreshCategories={fetchCategories}
        onCancel={() => setIsEditOpen(false)}
      />
    </Card>
  );
}
