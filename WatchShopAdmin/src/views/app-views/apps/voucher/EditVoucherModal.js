import React, { useEffect, useState } from "react";
import {
  Modal,
  Form,
  Input,
  Spin,
  message,
  notification,
  DatePicker,
  Switch,
} from "antd";
import moment from "moment";
import { updateVoucher } from "services/voucherService";

const EditVoucherModal = ({ visible, voucherData, onCancel, refreshVouchers }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const PRIMARY_COLOR = "#24a772";

  useEffect(() => {
    if (voucherData) {
      form.setFieldsValue({
        code: voucherData.code,
        discount: voucherData.discount,
        expirationDate: moment(voucherData.expirationDate),
        isUsed: voucherData.isUsed,
      });
    }
  }, [voucherData, form]);

  const handleFinish = async (values) => {
    setLoading(true);
    const expirationDate = moment(values.expirationDate).toISOString();
    const voucherUpdateData = {
      code: values.code,
      discount: values.discount,
      expirationDate,
      isUsed: values.isUsed,
    };

    try {
      await updateVoucher(voucherData.voucherId, voucherUpdateData);
      message.success("Chỉnh sửa voucher thành công");
      refreshVouchers();
      form.resetFields();
      onCancel();
    } catch (error) {
      notification.error({
        message: "Chỉnh sửa voucher thất bại",
        description: "Có lỗi xảy ra khi cập nhật voucher.",
        placement: "topRight",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      open={visible}
      title={<span style={{ color: PRIMARY_COLOR }}>Chỉnh Sửa Voucher</span>}
      okText="Lưu"
      cancelText="Hủy"
      onCancel={onCancel}
      onOk={() => form.submit()}
      okButtonProps={{ style: { backgroundColor: PRIMARY_COLOR, borderColor: PRIMARY_COLOR } }}
      width={500}
    >
      <Form form={form} layout="vertical" onFinish={handleFinish}>
        <Form.Item
          name="code"
          label="Mã Voucher"
          rules={[{ required: true, message: "Vui lòng nhập mã voucher!" }]}
        >
          <Input placeholder="Nhập mã voucher" />
        </Form.Item>
        <Form.Item
          name="discount"
          label="Giảm Giá (%)"
          rules={[{ required: true, message: "Vui lòng nhập giảm giá!" }]}
        >
          <Input type="number" min={0} max={100} placeholder="Nhập % giảm giá" />
        </Form.Item>
        <Form.Item
          name="expirationDate"
          label="Ngày Hết Hạn"
          rules={[{ required: true, message: "Vui lòng chọn ngày hết hạn!" }]}
        >
          <DatePicker style={{ width: "100%" }} format="DD/MM/YYYY" />
        </Form.Item>
        <Form.Item label="Đã Sử Dụng">
          <Form.Item name="isUsed" valuePropName="checked" noStyle>
            <Switch />
          </Form.Item>
        </Form.Item>
      </Form>
      {loading && <Spin style={{ display: "block", margin: "20px auto" }} />}
    </Modal>
  );
};

export default EditVoucherModal;
