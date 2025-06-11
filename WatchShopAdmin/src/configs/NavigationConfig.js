import {
  PieChartOutlined,
  PartitionOutlined,
  TrademarkCircleOutlined,
  DatabaseOutlined,
  ShoppingCartOutlined,
  RedEnvelopeOutlined,
  CommentOutlined,
  TeamOutlined,
  WechatOutlined,
} from "@ant-design/icons";

import { APP_PREFIX_PATH } from "configs/AppConfig";

// Lấy user từ localStorage
const user = JSON.parse(localStorage.getItem("user"));

const dashBoardNavTreeForUser = [
  {
    key: "category-management",
    path: `${APP_PREFIX_PATH}/apps/category`,
    title: "sidenav.dashboard.category",
    icon: PartitionOutlined,
    breadcrumb: true,
    submenu: [],
  },
  {
    key: "brand-management",
    path: `${APP_PREFIX_PATH}/apps/brand`,
    title: "sidenav.dashboard.brand",
    icon: TrademarkCircleOutlined,
    breadcrumb: true,
    submenu: [],
  },
  {
    key: "product-management",
    path: `${APP_PREFIX_PATH}/apps/product`,
    title: "sidenav.dashboard.product",
    icon: DatabaseOutlined,
    breadcrumb: true,
    submenu: [],
  },
  {
    key: "order-management",
    path: `${APP_PREFIX_PATH}/apps/order`,
    title: "sidenav.dashboard.order",
    icon: ShoppingCartOutlined,
    breadcrumb: true,
    submenu: [],
  },
];

const dashBoardNavTreeFull = [
  {
    key: "dashboards-default",
    path: `${APP_PREFIX_PATH}/dashboards/default`,
    title: "sidenav.dashboard.default",
    icon: PieChartOutlined,
    breadcrumb: false,
    submenu: [],
  },
  ...dashBoardNavTreeForUser,
  {
    key: "voucher-management",
    path: `${APP_PREFIX_PATH}/apps/voucher`,
    title: "sidenav.dashboard.voucher",
    icon: RedEnvelopeOutlined,
    breadcrumb: true,
    submenu: [],
  },
  {
    key: "review-management",
    path: `${APP_PREFIX_PATH}/apps/review`,
    title: "sidenav.dashboard.review",
    icon: CommentOutlined,
    breadcrumb: true,
    submenu: [],
  },
  {
    key: "user-management",
    path: `${APP_PREFIX_PATH}/apps/user`,
    title: "sidenav.dashboard.user",
    icon: TeamOutlined,
    breadcrumb: true,
    submenu: [],
  },
];

const dashBoardNavTree =
  user && user.role === "STAFF" ? dashBoardNavTreeForUser : dashBoardNavTreeFull;

const navigationConfig = [...dashBoardNavTree];

export default navigationConfig;
