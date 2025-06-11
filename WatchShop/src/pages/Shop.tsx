import React, { useState, useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import { IsLoading, ShowSidebarFilter } from "../redux/actions/primaryActions";
import { Link } from "react-router-dom";
import BrandsSection from "../components/Shop/Brands/Brands";
import Categories from "../components/Shop/FilterSide/Categories/Categories";
import Brands from "../components/Shop/FilterSide/Brands/Brands";
import ProductsSide from "../components/Shop/ProductsSide/ProductsSide";
import { RootState } from "../redux/reducers/index";
import { getAllProducts } from "../services/productService";
import { CircleLoader } from "react-spinners";

const Shop: React.FC = () => {
  const getQueryParam = (param: string) => {
    const queryParams = new URLSearchParams(window.location.search);
    return queryParams.get(param);
  };

  const categoryParam = getQueryParam("category"); // Lấy giá trị category từ URL
  const primaryState = useSelector((state: RootState) => state.primary);
  const loading = primaryState.isLoading;
  const showSideFilter = primaryState.showSidebarFilter;
  const dispatch = useDispatch();

  const [selectedCategory, setSelectedCategory] = useState<number | null>(
    categoryParam ? parseInt(categoryParam, 10) : null
  );
  const [selectedBrand, setSelectedBrand] = useState<number | null>(null);
  const [products, setProducts] = useState<any[]>([]);
  const [totalPagesNum, setTotalPagesNum] = useState<number>(1);
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [sortField, setSortField] = useState<string>("productName");
  const [sortDirection, setSortDirection] = useState<string>("asc");

  useEffect(() => {
    setSelectedCategory(categoryParam ? parseInt(categoryParam, 10) : null);
  }, []);

  useEffect(() => {
    fetchProducts();
  }, [selectedCategory, selectedBrand, currentPage, sortField, sortDirection]);

  useEffect(() => {
    setCurrentPage(1);
  }, [selectedCategory, selectedBrand, sortField, sortDirection]);

  const fetchProducts = async () => {
    try {
      dispatch(IsLoading(true));
      const response = await getAllProducts(
        "",
        selectedCategory,
        selectedBrand,
        currentPage,
        12,
        sortField,
        sortDirection
      );
      setProducts(response.payload.content);
      setTotalPagesNum(response.payload.totalPages);
    } catch (error) {
      console.error("Error fetching products", error);
    } finally {
      dispatch(IsLoading(false));
    }
  };

  return (
    <div className="shop-content">
      <div className="main">
        <section id="breadcrumb">
          <div className="container">
            <ul className="breadcrumb-content d-flex m-0 p-0">
              <li>
                <Link to="/">Trang chủ</Link>
              </li>
              <li>
                <span>Sản phẩm</span>
              </li>
            </ul>
          </div>
        </section>
        <div className="shop-content-wrapper">
          <div className="container">
            <div className="row">
              <div className="col-lg-3">
                <div className={showSideFilter ? "filter-side show-filter" : "filter-side"}>
                  <Categories setSelectedCategory={setSelectedCategory} />
                  <Brands setSelectedBrand={setSelectedBrand} />
                </div>
              </div>
              <div className="col-lg-9">
                {loading ? (
                  <div className="d-flex justify-content-center align-items-center" style={{ height: "400px" }}>
                    <CircleLoader color="#36d7b7" size={100} />
                  </div>
                ) : products.length === 0 ? (
                  <small>Không tìm thấy sản phẩm.</small>
                ) : (
                  <ProductsSide
                    products={products}
                    sortField={sortField}
                    sortDirection={sortDirection}
                    setSortField={setSortField}
                    setSortDirection={setSortDirection}
                    totalPagesNum={totalPagesNum}
                    currentPage={currentPage}
                    setCurrentPage={setCurrentPage}
                  />
                )}
              </div>
            </div>
            <div className="row" style={{ marginTop: "25px" }}>
              <div className="col-12">
                <BrandsSection />
              </div>
            </div>
          </div>
        </div>
        <div
          className={showSideFilter ? "dark-bg-color" : "d-none"}
          onClick={() => {
            dispatch(ShowSidebarFilter(false));
          }}
        ></div>
      </div>
    </div>
  );
};

export default Shop;
