import {
  Table,
  Popconfirm,
  Pagination,
  Input,
  message,
  Spin,
  Card,
  Switch,
  Row,
  Col,
  Tooltip,
  Button,
} from "antd";
import {
  EditOutlined,
  DeleteOutlined,
  PlusOutlined,
} from "@ant-design/icons";
import React, { useEffect, useState, useCallback } from "react";
import {
  getAllVouchers,
  deleteVoucher,
  changeVoucherStatus,
} from "services/voucherService";
import { debounce } from "lodash";
import moment from "moment";
import CreateVoucherModal from "./CreateVoucherModal";
import EditVoucherModal from "./EditVoucherModal";

export default function VoucherManagement() {
  const [vouchers, setVouchers] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [editVoucherData, setEditVoucherData] = useState(null);
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const limit = 5;
  const PRIMARY_COLOR = "#24a772";

  const fetchVouchers = useCallback(
    async (search = searchTerm, page = currentPage) => {
      setLoading(true);
      try {
        const data = await getAllVouchers(page, limit, search);
        setVouchers(data.content);
        setTotalPages(data.totalPages);
      } catch (error) {
        message.error("Lỗi khi lấy voucher.");
      } finally {
        setLoading(false);
      }
    },
    [currentPage, limit]
  );

  const debouncedFetchVouchers = useCallback(
    debounce((value) => {
      setCurrentPage(1);
      fetchVouchers(value, 1);
    }, 800),
    [fetchVouchers]
  );

  useEffect(() => {
    fetchVouchers(searchTerm, currentPage);
  }, [fetchVouchers, currentPage]);

  const confirmDeleteVoucher = async (voucherId) => {
    try {
      await deleteVoucher(voucherId);
      message.success("Đã xóa voucher.");
      fetchVouchers();
    } catch (error) {
      message.error("Lỗi khi xóa voucher.");
    }
  };

  const handleChangeStatus = async (voucherId) => {
    try {
      await changeVoucherStatus(voucherId);
      message.success("Đã thay đổi trạng thái voucher.");
      fetchVouchers();
    } catch (error) {
      message.error("Lỗi khi thay đổi trạng thái voucher.");
    }
  };

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  const handleEditClick = (record) => {
    setEditVoucherData(record);
    setIsEditOpen(true);
  };

  const columns = [
    {
      title: "Mã Voucher",
      dataIndex: "code",
      key: "code",
    },
    {
      title: "Giảm Giá (%)",
      dataIndex: "discount",
      key: "discount",
    },
    {
      title: "Ngày Hết Hạn",
      dataIndex: "expirationDate",
      key: "expirationDate",
      render: (text) => moment(text).format("DD/MM/YYYY"),
    },
    {
      title: "Đã Sử Dụng",
      dataIndex: "isUsed",
      key: "isUsed",
      render: (text, record) => (
        <Switch
          checked={text}
          onChange={() => handleChangeStatus(record.voucherId)}
        />
      ),
    },
    {
      title: "Hành Động",
      key: "actions",
      align: "center",
      render: (text, record) => (
        <Row justify="center" gutter={8}>
          <Tooltip title="Sửa">
            <Button
              type="text"
              icon={<EditOutlined style={{ color: PRIMARY_COLOR }} />}
              onClick={() => handleEditClick(record)}
            />
          </Tooltip>
          <Tooltip title="Xóa">
            <Popconfirm
              title="Bạn có chắc muốn xóa voucher này?"
              onConfirm={() => confirmDeleteVoucher(record.voucherId)}
              okText="Có"
              cancelText="Không"
            >
              <Button
                type="text"
                icon={<DeleteOutlined style={{ color: "red" }} />}
              />
            </Popconfirm>
          </Tooltip>
        </Row>
      ),
    },
  ];

  return (
    <Card>
      <Row justify="space-between" align="middle" style={{ marginBottom: 24 }}>
        <Col span={18}>
          <Input
            placeholder="Tìm kiếm voucher..."
            allowClear
            value={searchTerm}
            onChange={(e) => {
              setSearchTerm(e.target.value);
              debouncedFetchVouchers(e.target.value);
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
            dataSource={vouchers}
            pagination={false}
            rowKey={(record) => record.voucherId}
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

      <CreateVoucherModal
        visible={isCreateOpen}
        onCancel={() => setIsCreateOpen(false)}
        refreshVouchers={fetchVouchers}
      />
      <EditVoucherModal
        visible={isEditOpen}
        voucherData={editVoucherData}
        refreshVouchers={fetchVouchers}
        onCancel={() => setIsEditOpen(false)}
      />
    </Card>
  );
}