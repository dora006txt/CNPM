import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FaShoppingCart, FaUser, FaSearch } from "react-icons/fa";
import { useAuth } from "../../../context/AuthContext";
import axios from "axios";

const Navbar = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const { user } = useAuth();
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState("");
  const [categories, setCategories] = useState([]);
  const [loadingCategories, setLoadingCategories] = useState(true);
  const [cartCount, setCartCount] = useState(0);

  // Fetch categories
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        setLoadingCategories(true);
        const response = await axios.get("http://localhost:8080/api/categories");
        setCategories(Array.isArray(response.data) ? response.data : []);
      } catch (err) {
        console.error("API Error fetching categories:", err.message);
        setCategories([]);
      } finally {
        setLoadingCategories(false);
      }
    };
    fetchCategories();
  }, []);

  // Fetch cart count
  useEffect(() => {
    const fetchCart = async () => {
      if (!user) return;
      try {
        const token = localStorage.getItem("token");
        if (!token) {
          console.warn("No token found in localStorage");
          return;
        }

        const response = await axios.get("http://localhost:8080/api/v1/cart", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        console.log("Cart API response:", response.data);

        const cart = response.data;
        let totalItems = 0;

        if (cart && typeof cart === "object" && cart.items && Array.isArray(cart.items)) {
          if (cart.userId === user.id) {
            totalItems = cart.items.reduce((sum, item) => sum + item.quantity, 0);
          }
        } else {
          console.warn("No valid cart data found:", cart);
        }

        setCartCount(totalItems);
      } catch (err) {
        if (err.response && err.response.status === 204) {
          console.log("No cart found for user (204 No Content)");
          setCartCount(0);
        } else {
          console.error("API Error fetching cart:", err.response ? err.response.data : err.message);
        }
      }
    };

    fetchCart();
  }, [user]);

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/products?query=${encodeURIComponent(searchQuery)}`);
      setSearchQuery("");
    }
  };

  return (
    <nav className="bg-blue-600 text-white shadow-lg">
      <div className="container mx-auto px-4 py-3 flex items-center justify-between">
        <Link to="/" className="text-xl md:text-2xl font-bold flex items-center space-x-2">
          <span>Nhà Thuốc Hàng Châu</span>
        </Link>

        <form onSubmit={handleSearch} className="flex-1 mx-4 max-w-2xl hidden md:flex">
          <div className="relative w-full">
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Tìm kiếm sản phẩm, vấn đề sức khỏe..."
              className="w-full text-black bg-white rounded-full py-2 px-4 pr-10 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            <button
              type="submit"
              className="absolute right-2 top-1/2 transform -translate-y-1/2 text-gray-600 hover:text-blue-600"
            >
              <FaSearch size={20} />
            </button>
          </div>
        </form>

        <div className="flex items-center space-x-4">
          {user ? (
            <Link
              to="/profile"
              className="flex items-center space-x-1 text-white hover:text-gray-200 transition duration-200 pr-3"
            >
              <FaUser size={18} /> <span>Hồ sơ</span>
            </Link>
          ) : (
            <Link
              to="/login"
              className="flex items-center space-x-1 text-white hover:text-gray-200 transition duration-200"
            >
              <FaUser size={18} /> <span>Đăng nhập</span>
            </Link>
          )}
          <Link
            to="/cart"
            className="relative flex items-center gap-2 bg-blue-600 text-white rounded-full px-4 py-2 hover:bg-blue-700 transition duration-200"
          >
            <div className="relative">
              <FaShoppingCart size={20} />
              {cartCount > 0 && (
                <span className="absolute -top-2 -right-2 bg-orange-400 text-white text-xs font-bold px-1.5 py-0.5 rounded-full">
                  {cartCount}
                </span>
              )}
            </div>
            <span className="font-medium">Giỏ hàng</span>
          </Link>

          <button
            className="md:hidden btn btn-ghost text-white hover:bg-blue-700 rounded-full"
            onClick={() => setIsMenuOpen(!isMenuOpen)}
          >
            <FaSearch size={20} />
          </button>
        </div>
      </div>

      {/* Categories */}
      <div className="bg-white text-black py-2 border-b border-gray-200">
        <div className="container mx-auto px-4 flex items-center justify-center space-x-6 overflow-x-auto">
          {loadingCategories ? (
            <span className="loading loading-spinner loading-sm text-black"></span>
          ) : categories.length > 0 ? (
            categories.map((category) => (
              <Link
                key={category.id}
                to={`/categories/${category.slug}`}
                className="px-2 py-1 hover:text-blue-600 rounded transition duration-200 whitespace-nowrap"
              >
                {category.name || "Không có tên"}
              </Link>
            ))
          ) : (
            <p className="text-blue-600">Không có danh mục nào</p>
          )}
          <Link
            to="/pharmacy-locator"
            className="px-2 py-1 hover:text-blue-600 rounded transition duration-200 whitespace-nowrap"
          >
            Hệ thống nhà thuốc
          </Link>
        </div>
      </div>

      {/* Mobile Menu */}
      {isMenuOpen && (
        <div className="md:hidden bg-white text-blue-600 py-4 border-t border-gray-200">
          <ul className="menu menu-compact px-4 space-y-2">
            {loadingCategories ? (
              <li><span className="loading loading-spinner loading-sm"></span></li>
            ) : categories.length > 0 ? (
              categories.map((category) => (
                <li key={category.id}>
                  <Link
                    to={`/categories/${category.slug}`}
                    className="hover:bg-blue-100 rounded px-2 py-1"
                    onClick={() => setIsMenuOpen(false)}
                  >
                    {category.name || "Không có tên"}
                  </Link>
                </li>
              ))
            ) : (
              <li><p>Không có danh mục nào</p></li>
            )}
            <li>
              <Link
                to="/pharmacy-locator"
                className="hover:bg-blue-100 rounded px-2 py-1"
                onClick={() => setIsMenuOpen(false)}
              >
                Hệ thống nhà thuốc
              </Link>
            </li>
            <li>
              <Link
                to="/cart"
                className="hover:bg-blue-100 rounded px-2 py-1"
                onClick={() => setIsMenuOpen(false)}
              >
                Giỏ hàng
              </Link>
            </li>
            <li>
              <form onSubmit={handleSearch} className="relative">
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  placeholder="Tìm kiếm sản phẩm, vấn đề sức khỏe..."
                  className="input input-bordered input-sm w-full text-black bg-white pr-10 rounded-full"
                />
                <button type="submit" className="absolute right-3 top-2 text-gray-500">
                  <FaSearch />
                </button>
              </form>
            </li>
          </ul>
        </div>
      )}
    </nav>
  );
};

export default Navbar;