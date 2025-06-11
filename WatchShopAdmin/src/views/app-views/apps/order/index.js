import {
  Table,
  Button,
  Pagination,
  Input,
  message,
  Spin,
  Card,
} from "antd";
import React, { useEffect, useState, useCallback } from "react";
import { getAllOrders, updateOrderStatus } from "services/orderService";
import { debounce } from "lodash";
import { formatCurrency } from "utils/formatCurrency";
import COD from "../../../../assets/img/cash-on-delivery.png";
import OrderDetailModal from "./OrderDetailModal";

const PRIMARY_COLOR = "#24a772";

const generatePaymentMethod = (method) => {
  const baseStyle = { borderRadius: "15px", width: 50, height: 50 };
  switch (method) {
    case "COD":
      return <img src={COD} alt="COD" style={baseStyle} />;
    case "VNPay":
      return (
        <img
          src="https://vinadesign.vn/uploads/images/2023/05/vnpay-logo-vinadesign-25-12-57-55.jpg"
          alt="VNPay"
          style={baseStyle}
        />
      );
    case "Paypal":
      return (
        <img
          src="https://upload.wikimedia.org/wikipedia/commons/a/a4/Paypal_2014_logo.png"
          alt="Paypal"
          style={baseStyle}
        />
      );
    default:
      return <span>{method}</span>;
  }
};

const generateStatus = (status) => {
  const colorMap = {
    "Chờ xác nhận": "#FF9900",
    "Đã xác nhận": "#0000FF",
    "Đã đóng gói": "#800080",
    "Đang vận chuyển": "#008000",
    "Đã hủy": "#FF0000",
    "Đã giao hàng": "#008080",
  };
  const color = colorMap[status] || "gray";
  return (
    <span
      style={{
        color,
        padding: "3px 8px",
        border: `1px solid ${color}`,
        borderRadius: "5px",
        backgroundColor: `${color}20`,
        textAlign: "center",
        display: "inline-block",
        fontWeight: 500,
      }}
    >
      {status}
    </span>
  );
};

export default function OrderManagement() {
  const [orders, setOrders] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const limit = 5;

  const fetchOrders = useCallback(
    async (search = searchTerm, page = currentPage) => {
      setLoading(true);
      try {
        const data = await getAllOrders(page, limit, search);
        setOrders(data.content);
        setTotalPages(data.totalPages);
      } catch {
        message.error("Không thể tải danh sách đơn hàng.");
      } finally {
        setLoading(false);
      }
    },
    [currentPage, limit]
  );

  const debouncedFetchOrders = useCallback(
    debounce((value) => {
      setCurrentPage(1);
      fetchOrders(value, 1);
    }, 800),
    [fetchOrders]
  );

  useEffect(() => {
    fetchOrders(searchTerm, currentPage);
  }, [fetchOrders, currentPage]);

  const handleViewDetail = (order) => {
    setSelectedOrder(order);
    setIsModalVisible(true);
  };

  const handleCloseModal = () => {
    setIsModalVisible(false);
    setSelectedOrder(null);
  };

  const confirmUpdateOrderStatus = async (orderId, status) => {
    try {
      await updateOrderStatus(orderId, status);
      message.success("Cập nhật trạng thái thành công");
      fetchOrders();
    } catch {
      message.error("Không thể cập nhật trạng thái.");
    }
  };

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  const columns = [
    {
      title: "Mã Đơn",
      dataIndex: "code",
      key: "code",
    },
    {
      title: "Khách Hàng",
      dataIndex: ["user", "fullName"],
      key: "user",
    },
    {
      title: "SĐT",
      dataIndex: ["user", "phoneNumber"],
      key: "phoneNumber",
    },
    {
      title: "Email",
      dataIndex: ["user", "email"],
      key: "email",
    },
    {
      title: "Ngày Đặt",
      dataIndex: "date",
      key: "date",
      render: (text) => new Date(text).toLocaleDateString("vi-VN"),
    },
    {
      title: "Thanh Toán",
      dataIndex: "paymentMethod",
      key: "paymentMethod",
      align: "center",
      render: generatePaymentMethod,
    },
    {
      title: "Tổng Tiền",
      dataIndex: "totalPrice",
      key: "totalPrice",
      render: (text) => formatCurrency(text),
    },
    {
      title: "Trạng Thái",
      dataIndex: "status",
      key: "status",
      render: generateStatus,
    },
    {
      title: "Hành Động",
      key: "actions",
      align: "center",
      render: (_, record) => (
        <Button
          size="small"
          type="primary"
          onClick={() => handleViewDetail(record)}
          style={{ backgroundColor: PRIMARY_COLOR, borderColor: PRIMARY_COLOR }}
        >
          Xem chi tiết
        </Button>
      ),
    },
  ];

  return (
    <Card>
      <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 20 }}>
        <Input
          placeholder="Tìm kiếm đơn hàng..."
          allowClear
          value={searchTerm}
          onChange={(e) => {
            setSearchTerm(e.target.value);
            debouncedFetchOrders(e.target.value);
          }}
          style={{ width: "75%" }}
        />
      </div>

      {loading ? (
        <div style={{ textAlign: "center", marginTop: 32 }}>
          <Spin size="large" />
        </div>
      ) : (
        <>
          <Table
            columns={columns}
            dataSource={orders}
            pagination={false}
            rowKey={(record) => record.orderId}
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

      {selectedOrder && (
        <OrderDetailModal
          visible={isModalVisible}
          onClose={handleCloseModal}
          selectedOrder={selectedOrder}
          fetchOrdersData={fetchOrders}
        />
      )}
    </Card>
  );
}
