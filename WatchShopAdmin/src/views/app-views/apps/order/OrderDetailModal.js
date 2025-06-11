import React, { useState, useEffect } from "react";
import { Modal, Steps, message } from "antd";
import { updateOrderStatus } from "services/orderService";
import { formatCurrency } from "utils/formatCurrency";

const { Step } = Steps;
const PRIMARY_COLOR = "#24a772";

const statusSteps = [
  "Chờ xác nhận",
  "Đã xác nhận",
  "Đã đóng gói",
  "Đang vận chuyển",
  "Đã giao hàng",
  "Đã hủy",
];

const OrderDetailModal = ({ visible, onClose, selectedOrder, fetchOrdersData }) => {
  const [currentStatus, setCurrentStatus] = useState(selectedOrder?.status);

  useEffect(() => {
    if (selectedOrder) {
      setCurrentStatus(selectedOrder.status);
    }
  }, [selectedOrder]);

  const handleStatusUpdate = async (status) => {
    try {
      await updateOrderStatus(selectedOrder.orderId, status);
      setCurrentStatus(status);
      message.success(`Đã cập nhật trạng thái thành ${status}`);
      fetchOrdersData();
    } catch {
      message.error("Lỗi khi cập nhật trạng thái đơn hàng.");
    }
  };

  const currentStatusIndex = statusSteps.indexOf(currentStatus);

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
          padding: "4px 10px",
          borderRadius: 6,
          border: `1px solid ${color}`,
          backgroundColor: `${color}10`,
          fontWeight: 500,
        }}
      >
        {status}
      </span>
    );
  };

  return (
    <Modal
      title={<span style={{ color: PRIMARY_COLOR }}>Chi tiết đơn hàng: {selectedOrder?.code}</span>}
      open={visible}
      onCancel={onClose}
      footer={null}
      width={1000}
    >
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <h2 style={{ fontSize: "1.25rem", fontWeight: 600 }}>{selectedOrder.code}</h2>
        {generateStatus(currentStatus)}
      </div>

      <p style={{ color: "#6B7280", marginBottom: 32 }}>
        {new Date(selectedOrder.date).toLocaleString("vi-VN")}
      </p>

      <Steps
        current={currentStatusIndex}
        onChange={(current) => handleStatusUpdate(statusSteps[current])}
        style={{ marginBottom: 32 }}
      >
        {statusSteps.map((status) => (
          <Step key={status} title={status} />
        ))}
      </Steps>

      <div style={{ border: "1px solid #E5E7EB", borderRadius: 12, padding: 16, marginBottom: 24 }}>
        <h3 style={{ fontWeight: 600, marginBottom: 16 }}>Sản phẩm trong đơn hàng</h3>
        {selectedOrder.orderDetails.map((item) => (
          <div
            key={item.orderDetailId}
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              marginBottom: 16,
            }}
          >
            <div style={{ display: "flex", alignItems: "center", gap: 16 }}>
              <img
                src={item.productImages.imageUrl}
                alt={item.productName}
                style={{ width: 80, height: 80, objectFit: "cover", borderRadius: 12 }}
              />
              <div>
                <p style={{ fontWeight: 600 }}>{item.productName}</p>
                <p style={{ fontSize: 14, color: "#6B7280" }}>
                  Lựa chọn: {item.variationName || "Không có"}
                </p>
              </div>
            </div>
            <div style={{ textAlign: "right" }}>
              <p style={{ color: "#6B7280" }}>x{item.productQuantity}</p>
              <p style={{ fontWeight: 600 }}>{formatCurrency(item.price)}</p>
            </div>
          </div>
        ))}
      </div>

      <div style={{ border: "1px solid #E5E7EB", borderRadius: 12, padding: 16, marginBottom: 24 }}>
        <h3 style={{ fontWeight: 600, marginBottom: 16 }}>Tóm tắt đơn hàng</h3>
        <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 8 }}>
          <span>Tổng tiền</span>
          <span>
            {formatCurrency(
              selectedOrder.totalPrice / (1 - (selectedOrder.discount || 0) / 100)
            )}
          </span>
        </div>
        <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 8 }}>
          <span>Giảm giá</span>
          <span>
            {selectedOrder.discount
              ? `- ${formatCurrency(
                  (selectedOrder.totalPrice /
                    (1 - selectedOrder.discount / 100)) *
                    (selectedOrder.discount / 100)
                )}`
              : "Không có"}
          </span>
        </div>
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            fontWeight: 600,
            fontSize: 16,
            marginTop: 8,
          }}
        >
          <span>Tổng đơn hàng</span>
          <span>{formatCurrency(selectedOrder.totalPrice)}</span>
        </div>
      </div>

      <div style={{ display: "flex", gap: 16 }}>
        <div style={{ flex: 1, border: "1px solid #E5E7EB", borderRadius: 12, padding: 16 }}>
          <h3 style={{ fontWeight: 600, marginBottom: 12 }}>Thông tin khách hàng</h3>
          <p>
            <strong>Họ tên:</strong> {selectedOrder.user.fullName}
          </p>
          <p>
            <strong>Email:</strong> {selectedOrder.user.email}
          </p>
          <p>
            <strong>Số điện thoại:</strong> {selectedOrder.user.phoneNumber || "Không có"}
          </p>
        </div>

        <div style={{ flex: 1, border: "1px solid #E5E7EB", borderRadius: 12, padding: 16 }}>
          <h3 style={{ fontWeight: 600, marginBottom: 12 }}>Thông tin người nhận</h3>
          <p>
            <strong>Người nhận:</strong> {selectedOrder.addressBook.recipientName}
          </p>
          <p>
            <strong>Email:</strong> {selectedOrder.addressBook.email}
          </p>
          <p>
            <strong>Số điện thoại:</strong>{" "}
            {selectedOrder.addressBook.phoneNumber || "Không có"}
          </p>
          <p>
            <strong>Địa chỉ:</strong>{" "}
            {`${selectedOrder.addressBook.address}, ${selectedOrder.addressBook.ward}, ${selectedOrder.addressBook.district}, ${selectedOrder.addressBook.city}`}
          </p>
        </div>
      </div>
    </Modal>
  );
};

export default OrderDetailModal;
