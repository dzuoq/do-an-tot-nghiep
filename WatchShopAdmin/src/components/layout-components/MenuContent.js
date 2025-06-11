import React from "react";
import { Link } from "react-router-dom";
import { Menu, Grid } from "antd";
import IntlMessage from "../util-components/IntlMessage";
import Icon from "../util-components/Icon";
import navigationConfig from "configs/NavigationConfig";
import { connect } from "react-redux";
import { SIDE_NAV_LIGHT, NAV_TYPE_SIDE } from "constants/ThemeConstant";
import utils from "utils";
import { onMobileNavToggle } from "redux/actions/Theme";

const { SubMenu } = Menu;
const { useBreakpoint } = Grid;

// 🌿 Màu chính và style item chọn
const PRIMARY_COLOR = "#24a772";
const ACTIVE_STYLE = {
  backgroundColor: "#e6f7ec",
  color: PRIMARY_COLOR,
  borderRadius: 8,
  fontWeight: 600,
};

const setLocale = (isLocaleOn, localeKey) =>
  isLocaleOn ? <IntlMessage id={localeKey} /> : localeKey.toString();

const setDefaultOpen = (key) => {
  let keyList = [];
  let keyString = "";
  if (key) {
    const arr = key.split("-");
    for (let index = 0; index < arr.length; index++) {
      const elm = arr[index];
      index === 0 ? (keyString = elm) : (keyString = `${keyString}-${elm}`);
      keyList.push(keyString);
    }
  }
  return keyList;
};

// 💡 Style chung cho item
const itemStyle = {
  display: "flex",
  alignItems: "center",
  gap: 10,
  padding: "12px 20px",
  borderRadius: 10,
  margin: "10px 12px", // Tăng margin để tạo khoảng cách giữa các item
  fontSize: 15,
  fontWeight: 500,
  transition: "all 0.3s",
};


// ✨ Style hover
const getHoverStyle = (isActive) =>
  isActive
    ? ACTIVE_STYLE
    : {
        ...itemStyle,
        cursor: "pointer",
        color: "#333",
      };

const SideNavContent = (props) => {
  const { sideNavTheme, routeInfo, localization, onMobileNavToggle } = props;
  const isMobile = !utils.getBreakPoint(useBreakpoint()).includes("lg");
  const closeMobileNav = () => isMobile && onMobileNavToggle(false);

  return (
    <div
      style={{
        background: "#fff",
        height: "100%",
        overflowY: "auto",
        boxShadow: "2px 0 8px rgba(0,0,0,0.05)",
        paddingTop: 16,
      }}
    >
      <Menu
        mode="inline"
        selectedKeys={[routeInfo?.key]}
        defaultOpenKeys={setDefaultOpen(routeInfo?.key)}
        style={{ borderRight: 0, backgroundColor: "transparent" }}
      >
        {navigationConfig.map((menu) =>
          menu.submenu.length > 0 ? (
            <SubMenu
              key={menu.key}
              icon={menu.icon && <Icon type={menu.icon} />}
              title={<span>{setLocale(localization, menu.title)}</span>}
              style={{ paddingLeft: 8 }}
            >
              {menu.submenu.map((sub) =>
                sub.submenu?.length > 0 ? (
                  <SubMenu
                    key={sub.key}
                    icon={sub.icon && <Icon type={sub.icon} />}
                    title={setLocale(localization, sub.title)}
                  >
                    {sub.submenu.map((item) => (
                      <Menu.Item
                        key={item.key}
                        style={
                          routeInfo?.key === item.key
                            ? { ...itemStyle, ...ACTIVE_STYLE }
                            : itemStyle
                        }
                      >
                        {item.icon && <Icon type={item.icon} />}
                        <span>{setLocale(localization, item.title)}</span>
                        <Link to={item.path} onClick={closeMobileNav} />
                      </Menu.Item>
                    ))}
                  </SubMenu>
                ) : (
                  <Menu.Item
                    key={sub.key}
                    style={
                      routeInfo?.key === sub.key
                        ? { ...itemStyle, ...ACTIVE_STYLE }
                        : itemStyle
                    }
                  >
                    {sub.icon && <Icon type={sub.icon} />}
                    <span>{setLocale(localization, sub.title)}</span>
                    <Link to={sub.path} onClick={closeMobileNav} />
                  </Menu.Item>
                )
              )}
            </SubMenu>
          ) : (
            <Menu.Item
              key={menu.key}
              style={
                routeInfo?.key === menu.key
                  ? { ...itemStyle, ...ACTIVE_STYLE }
                  : itemStyle
              }
            >
              {menu.icon && <Icon type={menu.icon} />}
              <span>{setLocale(localization, menu.title)}</span>
              {menu.path && <Link to={menu.path} onClick={closeMobileNav} />}
            </Menu.Item>
          )
        )}
      </Menu>
    </div>
  );
};

const TopNavContent = (props) => {
  const { topNavColor, localization, routeInfo } = props;
  return (
    <Menu mode="horizontal" style={{ backgroundColor: "#fff", paddingLeft: 24 }}>
      {navigationConfig.map((menu) =>
        menu.submenu.length > 0 ? (
          <SubMenu
            key={menu.key}
            popupClassName="top-nav-menu"
            title={
              <span>
                {menu.icon && <Icon type={menu.icon} />}
                <span style={{ paddingLeft: 8 }}>{setLocale(localization, menu.title)}</span>
              </span>
            }
          >
            {menu.submenu.map((sub) =>
              sub.submenu?.length > 0 ? (
                <SubMenu
                  key={sub.key}
                  title={<span>{setLocale(localization, sub.title)}</span>}
                >
                  {sub.submenu.map((item) => (
                    <Menu.Item
                      key={item.key}
                      style={
                        routeInfo?.key === item.key
                          ? { ...itemStyle, ...ACTIVE_STYLE }
                          : itemStyle
                      }
                    >
                      <span>{setLocale(localization, item.title)}</span>
                      <Link to={item.path} />
                    </Menu.Item>
                  ))}
                </SubMenu>
              ) : (
                <Menu.Item
                  key={sub.key}
                  style={
                    routeInfo?.key === sub.key
                      ? { ...itemStyle, ...ACTIVE_STYLE }
                      : itemStyle
                  }
                >
                  {sub.icon && <Icon type={sub.icon} />}
                  <span>{setLocale(localization, sub.title)}</span>
                  <Link to={sub.path} />
                </Menu.Item>
              )
            )}
          </SubMenu>
        ) : (
          <Menu.Item
            key={menu.key}
            style={
              routeInfo?.key === menu.key
                ? { ...itemStyle, ...ACTIVE_STYLE }
                : itemStyle
            }
          >
            {menu.icon && <Icon type={menu.icon} />}
            <span>{setLocale(localization, menu.title)}</span>
            {menu.path && <Link to={menu.path} />}
          </Menu.Item>
        )
      )}
    </Menu>
  );
};

const MenuContent = (props) => {
  return props.type === NAV_TYPE_SIDE ? (
    <SideNavContent {...props} />
  ) : (
    <TopNavContent {...props} />
  );
};

const mapStateToProps = ({ theme }) => {
  const { sideNavTheme, topNavColor } = theme;
  return { sideNavTheme, topNavColor };
};

export default connect(mapStateToProps, { onMobileNavToggle })(MenuContent);
