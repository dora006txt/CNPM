import axios from "axios";

export const getProducts = async () => {
  try {
    const response = await axios.get("http://localhost:8080/api/products");
    console.log("API Response:", response.data); // Log để debug
    return response.data.products || response.data || []; // Hỗ trợ cả {products: [...]} và {data: [...]}
  } catch (error) {
    console.error("Error fetching products:", error);
    return mockProducts; // Sử dụng dữ liệu giả lập nếu API lỗi
  }
};

export const getProductById = async (id) => {
  try {
    const response = await axios.get(`http://localhost:8080/api/products/${id}`);
    console.log("API Response for product:", response.data); // Log để debug
    return response.data;
  } catch (error) {
    console.error(`Error fetching product ${id}:`, error);
    const product = mockProducts.find((p) => p.id === parseInt(id));
    if (!product) {
      throw new Error("Sản phẩm không tồn tại");
    }
    return product;
  }
};