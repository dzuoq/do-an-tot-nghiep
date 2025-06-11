import React, { useState, useEffect } from "react";
import { useDispatch } from "react-redux";
import { SortByCategory } from "../../../../redux/actions/productActions";
import { GetTitle, IsLoading } from "../../../../redux/actions/primaryActions";
import { getAllCategories } from "../../../../services/categoryService";
import { List, Card, Spin, Typography, Avatar } from "antd";
import { CheckOutlined, LoadingOutlined } from "@ant-design/icons";

const { Title } = Typography;

interface CategoriesProps {
  setSelectedCategory: (categoryId: number | null) => void;
}

const Categories: React.FC<CategoriesProps> = ({ setSelectedCategory }) => {
  const dispatch = useDispatch();
  const [categories, setCategories] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | null>(
    null
  );

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await getAllCategories();
        setCategories(response.content.reverse());
      } catch (error) {
        console.error("Error fetching categories", error);
      } finally {
        setLoading(false);
      }
    };

    fetchCategories();
  }, []);

  return (
    <Card
      title={<Title level={5} style={{ margin: 0, color: "#80001C" }}>Danh Mục</Title>}
      bordered={false}
      style={{
        width: "100%",
        background: "#fff",
        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
        borderRadius: "8px",
        padding: "10px",
      }}
    >
      {loading ? (
        <Spin
          indicator={<LoadingOutlined style={{ fontSize: 24, color: "#80001C" }} />}
          style={{ display: "flex", justifyContent: "center", marginTop: 10 }}
        />
      ) : (
        <List
          dataSource={[{ categoryId: null, categoryName: "Tất cả sản phẩm" }, ...categories]}
          renderItem={(category) => (
            <List.Item
              onClick={() => {
                if (selectedCategoryId === category.categoryId) return;
                setSelectedCategory(category.categoryId);
                setSelectedCategoryId(category.categoryId);
                dispatch(GetTitle(category.categoryName));
                dispatch(IsLoading(true));

                if (category.categoryId !== null) {
                  dispatch(SortByCategory(category.categoryName));
                }
              }}
              style={{
                display: "flex",
                alignItems: "center",
                padding: "8px 12px",
                borderRadius: "6px",
                transition: "all 0.3s ease-in-out",
                cursor: "pointer",
                background: selectedCategoryId === category.categoryId ? "#80001C" : "#fff",
                color: selectedCategoryId === category.categoryId ? "#fff" : "#333",
                fontSize: "14px",
                fontWeight: selectedCategoryId === category.categoryId ? "bold" : "normal",
                boxShadow:
                  selectedCategoryId === category.categoryId
                    ? "0 2px 6px rgba(0, 0, 0, 0.15)"
                    : "none",
              }}
            >
              {category.categoryId !== null && (
                <Avatar
                  src={category.image}
                  size={32}
                  shape="square"
                  style={{
                    marginRight: "10px",
                    border: "1px solid #ddd",
                    transition: "border-color 0.3s",
                    borderColor: selectedCategoryId === category.categoryId ? "#fff" : "#ddd",
                  }}
                />
              )}
              <span style={{ flex: 1 }}>{category.categoryName}</span>
              {selectedCategoryId === category.categoryId && (
                <CheckOutlined style={{ fontSize: 14, color: "#fff" }} />
              )}
            </List.Item>
          )}
        />
      )}
    </Card>
  );
};

export default Categories;
