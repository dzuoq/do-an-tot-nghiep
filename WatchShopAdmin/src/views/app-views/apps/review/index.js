import React, { useEffect, useState, useCallback } from "react";
import {
  Table,
  Button,
  Pagination,
  Input,
  message,
  Spin,
  Card,
  Modal,
} from "antd";
import { getAllReviews } from "services/reviewService";
import { debounce } from "lodash";
import moment from "moment";

export default function ReviewManagement() {
  const [reviews, setReviews] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedReview, setSelectedReview] = useState(null);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const PRIMARY_COLOR = "#24a772";
  const limit = 5;

  const fetchReviews = useCallback(
    async (search = searchTerm, page = currentPage) => {
      setLoading(true);
      try {
        const data = await getAllReviews(page, limit, search);
        setReviews(data.content);
        setTotalPages(data.totalPages);
      } catch (error) {
        message.error("Lỗi khi lấy đánh giá.");
      } finally {
        setLoading(false);
      }
    },
    [currentPage, limit]
  );

  const debouncedFetchReviews = useCallback(
    debounce((value) => {
      setCurrentPage(1);
      fetchReviews(value, 1);
    }, 800),
    [fetchReviews]
  );

  useEffect(() => {
    fetchReviews(searchTerm, currentPage);
  }, [fetchReviews, currentPage]);

  const showReviewDetails = (record) => {
    setSelectedReview(record);
    setIsModalVisible(true);
  };

  const handleCancel = () => {
    setIsModalVisible(false);
    setSelectedReview(null);
  };

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  const columns = [
    {
      title: "Sản phẩm",
      dataIndex: "productName",
      key: "productName",
    },
    {
      title: "Người dùng",
      dataIndex: "fullName",
      key: "fullName",
    },
    {
      title: "Số điện thoại",
      dataIndex: "phoneNumber",
      key: "phoneNumber",
    },
    {
      title: "Đánh giá",
      dataIndex: "reviewText",
      key: "reviewText",
    },
    {
      title: "Số sao",
      dataIndex: "rating",
      key: "rating",
    },
    {
      title: "Ngày đánh giá",
      dataIndex: "reviewDate",
      key: "reviewDate",
      render: (text) => moment(text).format("DD/MM/YYYY"),
    },
    {
      title: "Hành Động",
      key: "actions",
      align: "center",
      render: (text, record) => (
        <Button
          size="small"
          style={{ backgroundColor: PRIMARY_COLOR, color: "white" }}
          onClick={() => showReviewDetails(record)}
        >
          Xem chi tiết
        </Button>
      ),
    },
  ];

  return (
    <Card>
      <div>
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            marginBottom: "20px",
          }}
        >
          <Input
            placeholder="Tìm kiếm đánh giá..."
            allowClear
            value={searchTerm}
            onChange={(e) => {
              setSearchTerm(e.target.value);
              debouncedFetchReviews(e.target.value);
            }}
            style={{ width: "100%" }}
          />
        </div>

        {loading ? (
          <div style={{ textAlign: "center", marginTop: "20px" }}>
            <Spin size="large" />
          </div>
        ) : (
          <>
            <Table
              columns={columns}
              dataSource={reviews}
              pagination={false}
              rowKey={(record) => record.reviewId}
            />

            <Pagination
              current={currentPage}
              total={totalPages * limit}
              pageSize={limit}
              onChange={handlePageChange}
              style={{ marginTop: "20px", textAlign: "center" }}
            />
          </>
        )}

        <Modal
          title={<span style={{ color: PRIMARY_COLOR }}>Chi tiết đánh giá</span>}
          open={isModalVisible}
          onCancel={handleCancel}
          footer={null}
          width={600}
        >
          {selectedReview && (
            <div style={{ lineHeight: 1.7 }}>
              <p><strong>Sản phẩm:</strong> {selectedReview.productName}</p>
              <p><strong>Người dùng:</strong> {selectedReview.fullName}</p>
              <p><strong>Số điện thoại:</strong> {selectedReview.phoneNumber}</p>
              <p><strong>Đánh giá:</strong> {selectedReview.reviewText}</p>
              <p><strong>Số sao:</strong> {selectedReview.rating}</p>
              <p><strong>Ngày đánh giá:</strong> {moment(selectedReview.reviewDate).format("DD/MM/YYYY")}</p>
            </div>
          )}
        </Modal>
      </div>
    </Card>
  );
}
