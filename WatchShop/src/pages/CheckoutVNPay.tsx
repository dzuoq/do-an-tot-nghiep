import React, { useEffect } from "react";
import { useLocation, useHistory } from "react-router-dom";
import { toast } from "react-toastify";
import { handleVNPayPaymentReturn } from "../services/vnpayService"; // Import hàm xử lý VNPay
import { createOrder } from "../services/orderService";
import { useSelector, useDispatch } from "react-redux";
import { RootState } from "../redux/reducers/index";
import { ActionType } from "../redux/actions/actionTypes";
import { markVoucherAsUsed } from "../services/voucherService";

const CheckoutVNPay: React.FC = () => {
  const location = useLocation();
  const history = useHistory();
  const cartState = useSelector((state: RootState) => state.cart);
  const cart = cartState.cart;
  const dispatch = useDispatch();

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search); // Lấy các tham số trả về từ URL
    const requestParams = Object.fromEntries(queryParams.entries());

    // Xử lý kết quả trả về từ VNPay
    handleVNPayPaymentReturn(requestParams)
      .then((result) => {
        if (result === "Giao dịch thành công!") {
          const orderCode = queryParams.get("vnp_OrderInfo") || "";

          // Gọi hàm handlePaymentSubmit và điều hướng tới trang success
          handlePaymentSubmit("VNPay", orderCode);
        } else {
          toast.error(result); // Nếu thất bại, thông báo lỗi
          history.push("/"); // Điều hướng về trang chủ
        }
      })
      .catch((error) => {
        console.error("Error handling VNPay payment return:", error);
        toast.error("Lỗi xử lý thanh toán VNPay.");
        history.push("/"); // Điều hướng về trang chủ nếu lỗi
      });
  }, [location, history]);

  const handlePaymentSubmit = async (method: string, orderCode: string) => {
    try {
      // Lấy selectedAddressBookId từ localStorage và chuyển đổi sang number
      const selectedAddressBookId = localStorage.getItem(
        "selectedAddressBookId"
      );

      if (!selectedAddressBookId) {
        throw new Error("Không tìm thấy địa chỉ giao hàng.");
      }

      const addressBookId = Number(selectedAddressBookId); // Chuyển đổi sang kiểu number

      if (isNaN(addressBookId)) {
        throw new Error("ID địa chỉ không hợp lệ.");
      }

      // Lấy thông tin voucher từ localStorage
      const storedVoucher = localStorage.getItem("voucher");
      let voucherId: string | null = null;
      let discount: number = 0;
      if (storedVoucher) {
        const voucher = JSON.parse(storedVoucher);
        voucherId = voucher.voucherId; // Lấy voucherId từ localStorage
        discount = voucher.discount;
      }

      // Tạo đơn hàng
      const response = await createOrder(
        cart,
        method,
        orderCode,
        addressBookId,
        discount
      );
      toast.success("Đặt hàng thành công");

      // Nếu có voucherId, gọi API để đánh dấu mã giảm giá đã được sử dụng
      if (voucherId) {
        await markVoucherAsUsed(voucherId);
        toast.success("Mã giảm giá đã được sử dụng");

        // Xóa voucher khỏi localStorage sau khi sử dụng
        localStorage.removeItem("voucher");
      }

      // Dispatch action để xóa giỏ hàng
      dispatch({ type: ActionType.CLEAR_CART });

      // Xóa selectedAddressBookId khỏi localStorage sau khi đặt hàng thành công
      localStorage.removeItem("selectedAddressBookId");

      // Chuyển hướng đến trang checkout success
      history.push({
        pathname: "/checkoutsuccess",
        state: { orderCode }, // Gửi orderCode qua location.state
      });
    } catch (error: any) {
      console.error(error?.response?.data?.message);
      toast.error(error?.response?.data?.message);
    }
  };

  return <div>Đang xử lý thanh toán...</div>; // Hiển thị trong khi xử lý kết quả
};

export default CheckoutVNPay;
