import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Menu, Button, Dropdown, Typography, Spin, Avatar } from "antd";
import { GiHamburgerMenu } from "react-icons/gi";
import { VscChevronRight } from "react-icons/vsc";
import { getAllCategories } from "../../../services/categoryService"; // API gọi danh mục

const { Title } = Typography;

const Department: React.FC = () => {
  const [categories, setCategories] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const data = await getAllCategories(1, 1000);
        setCategories(data.content.reverse());
      } catch (error) {
        console.error("Lỗi tải danh mục:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchCategories();
  }, []);

  // Tạo menu danh mục sản phẩm
  const menu = (
    <Menu>
      {loading ? (
        <Spin size="small" style={{ display: "block", textAlign: "center", padding: "10px" }} />
      ) : (
        categories.map((item: any) =>
          item.submenu ? (
            <Menu.SubMenu
              key={item.categoryId}
              title={
                <span style={{ display: "flex", alignItems: "center", gap: "10px" }}>
                  <Avatar src={item.image} shape="square" size={24} />
                  {item.categoryName}
                </span>
              }
              icon={<VscChevronRight />}
            >
              {item.submenu.map((subItem: any) => (
                <Menu.Item key={subItem.categoryId}>
                  <Link to={`/shop?category=${subItem.categoryId}`}>
                    {subItem.categoryName}
                  </Link>
                </Menu.Item>
              ))}
            </Menu.SubMenu>
          ) : (
            <Menu.Item key={item.categoryId}>
              <Link to={`/shop?category=${item.categoryId}`}>
                <span style={{ display: "flex", alignItems: "center", gap: "10px" }}>
                  <Avatar src={item.image} shape="square" size={24} />
                  {item.categoryName}
                </span>
              </Link>
            </Menu.Item>
          )
        )
      )}
    </Menu>
  );

  return (
    <Dropdown overlay={menu} trigger={["click"]} placement="bottomLeft">
      <Button
        type="primary"
        icon={<GiHamburgerMenu />}
        style={{
          backgroundColor: "#80001C",
          borderColor: "#80001C",
          fontWeight: "bold",
        }}
      >
        Danh mục sản phẩm
      </Button>
    </Dropdown>
  );
};

export default Department;
