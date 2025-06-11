import {
  Table,
  Button,
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
} from "antd";
import {
  EditOutlined,
  DeleteOutlined,
  PlusOutlined,
} from "@ant-design/icons";
import React, { useEffect, useState, useCallback } from "react";
import { getAllBrands, deleteBrand } from "services/brandService";
import { debounce } from "lodash";
import CreateBrandModal from "./CreateBrandModal";
import EditBrandModal from "./EditBrandModal";

const PRIMARY_COLOR = "#24a772";

export default function BrandManagement() {
  const [brands, setBrands] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [editBrandData, setEditBrandData] = useState(null);
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const limit = 5;

  const fetchBrands = useCallback(
    async (search = searchTerm, page = currentPage) => {
      setLoading(true);
      try {
        const data = await getAllBrands(page, limit, search);
        setBrands(data.content);
        setTotalPages(data.totalPages);
      } catch (error) {
        message.error("Lỗi khi lấy thương hiệu.");
      } finally {
        setLoading(false);
      }
    },
    [currentPage, limit]
  );

  const debouncedFetchBrands = useCallback(
    debounce((value) => {
      setCurrentPage(1);
      fetchBrands(value, 1);
    }, 800),
    [fetchBrands]
  );

  useEffect(() => {
    fetchBrands(searchTerm, currentPage);
  }, [fetchBrands, currentPage]);

  const confirmDeleteBrand = async (brandId) => {
    try {
      await deleteBrand(brandId);
      message.success("Đã xóa thương hiệu.");
      fetchBrands();
    } catch (error) {
      message.error("Lỗi khi xóa thương hiệu.");
    }
  };

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  const handleEditClick = (record) => {
    setEditBrandData(record);
    setIsEditOpen(true);
  };

  const columns = [
    {
      title: "Tên Thương Hiệu",
      dataIndex: "brandName",
      key: "brandName",
    },
    {
      title: "Hình Ảnh",
      dataIndex: "image",
      key: "image",
      render: (text) => (
        <img
          src={text || "https://via.placeholder.com/50"}
          alt="Thương hiệu"
          width={80}
          height={80}
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
              title="Bạn có chắc muốn xóa thương hiệu này?"
              onConfirm={() => confirmDeleteBrand(record.brandId)}
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
            placeholder="Tìm kiếm thương hiệu..."
            allowClear
            value={searchTerm}
            onChange={(e) => {
              setSearchTerm(e.target.value);
              debouncedFetchBrands(e.target.value);
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
            dataSource={brands}
            pagination={false}
            rowKey={(record) => record.brandId}
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

      <CreateBrandModal
        visible={isCreateOpen}
        onCancel={() => setIsCreateOpen(false)}
        refreshBrands={fetchBrands}
      />

      <EditBrandModal
        visible={isEditOpen}
        brandData={editBrandData}
        refreshBrands={fetchBrands}
        onCancel={() => setIsEditOpen(false)}
      />
    </Card>
  );
}
