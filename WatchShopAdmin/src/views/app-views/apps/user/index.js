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
import { getAllUsers, deleteUser } from "services/userService";
import { debounce } from "lodash";
import CreateUserModal from "./CreateUserModal";
import EditUserModal from "./EditUserModal";

export default function UserManagement() {
  const [users, setUsers] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [editUserData, setEditUserData] = useState(null);
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const limit = 5;

  const PRIMARY_COLOR = "#24a772";

  const fetchUsers = useCallback(async (search = searchTerm, page = currentPage) => {
    setLoading(true);
    try {
      const data = await getAllUsers(page, limit, search);
      setUsers(data.content);
      setTotalPages(data.totalPages);
    } catch (error) {
      message.error("Lỗi khi lấy người dùng.");
    } finally {
      setLoading(false);
    }
  }, [currentPage, limit]);

  const debouncedFetchUsers = useCallback(
    debounce((value) => {
      setCurrentPage(1);
      fetchUsers(value, 1);
    }, 800),
    [fetchUsers]
  );

  useEffect(() => {
    fetchUsers(searchTerm, currentPage);
  }, [fetchUsers, currentPage]);

  const confirmDeleteUser = async (userId) => {
    try {
      await deleteUser(userId);
      message.success("Đã xóa người dùng.");
      fetchUsers();
    } catch (error) {
      message.error("Lỗi khi xóa người dùng.");
    }
  };

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  const handleEditClick = (record) => {
    setEditUserData(record);
    setIsEditOpen(true);
  };

  const columns = [
    {
      title: "Tên Người Dùng",
      dataIndex: "fullName",
      key: "fullName",
    },
    {
      title: "Ảnh Đại Diện",
      dataIndex: "avatar",
      key: "avatar",
      render: (text) => (
        <img
          src={text || "https://via.placeholder.com/50"}
          alt="User"
          width={50}
          height={50}
          style={{ borderRadius: "10%" }}
        />
      ),
    },
    {
      title: "Email",
      dataIndex: "email",
      key: "email",
    },
    {
      title: "Số Điện Thoại",
      dataIndex: "phoneNumber",
      key: "phoneNumber",
    },
    {
      title: "Vai Trò",
      dataIndex: "role",
      key: "role",
      render: (text) => {
        switch (text) {
          case "ADMIN":
            return "Quản trị viên";
          case "STAFF":
            return "Nhân viên";
          case "CUSTOMER":
            return "Khách hàng";
          default:
            return text;
        }
      },
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
              title="Bạn có chắc muốn xóa người dùng này?"
              onConfirm={() => confirmDeleteUser(record.userId)}
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
            placeholder="Tìm kiếm người dùng..."
            allowClear
            value={searchTerm}
            onChange={(e) => {
              setSearchTerm(e.target.value);
              debouncedFetchUsers(e.target.value);
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
            dataSource={users}
            pagination={false}
            rowKey={(record) => record.userId}
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

      <CreateUserModal
        visible={isCreateOpen}
        onCancel={() => setIsCreateOpen(false)}
        refreshUsers={fetchUsers}
      />
      <EditUserModal
        visible={isEditOpen}
        userData={editUserData}
        refreshUsers={fetchUsers}
        onCancel={() => setIsEditOpen(false)}
      />
    </Card>
  );
}
