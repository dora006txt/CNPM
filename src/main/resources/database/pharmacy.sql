-- Xóa database cũ nếu tồn tại;
-- DROP DATABASE IF EXISTS pharmacy_db;
-- CREATE DATABASE pharmacy_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE pharmacy_db;

-- =============================================
-- Bảng cấu hình & danh mục chung
-- =============================================

-- Bảng: Vai trò người dùng (Roles)
CREATE TABLE Roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Tên vai trò (Customer, Admin, Doctor, BranchManager)',
    description TEXT NULL COMMENT 'Mô tả vai trò'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Lưu trữ các vai trò người dùng trong hệ thống';

-- Bảng: Quốc gia (Countries) - Học hỏi từ ERD
CREATE TABLE Countries (
    country_id INT AUTO_INCREMENT PRIMARY KEY,
    country_code CHAR(2) NOT NULL UNIQUE COMMENT 'Mã quốc gia ISO 3166-1 Alpha-2',
    country_name VARCHAR(100) NOT NULL UNIQUE COMMENT 'Tên quốc gia'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Danh mục các quốc gia';

-- Bảng: Trạng thái đơn hàng (Order_Statuses) - Học hỏi từ ERD
CREATE TABLE Order_Statuses (
    status_id INT AUTO_INCREMENT PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Tên trạng thái (pending_confirmation, pending_doctor_consultation, processing, ready_for_pickup, shipping, delivered, cancelled, returned)',
    description TEXT NULL COMMENT 'Mô tả trạng thái',
    is_final_state BOOLEAN DEFAULT FALSE COMMENT 'Đánh dấu trạng thái cuối cùng (không thể chuyển tiếp)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Danh mục các trạng thái đơn hàng';

-- Bảng: Phương thức vận chuyển (Shipping_Methods) - Học hỏi từ ERD
CREATE TABLE Shipping_Methods (
    method_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT 'Tên phương thức vận chuyển (Giao hàng nhanh, Giao hàng tiết kiệm, Nhận tại cửa hàng)',
    description TEXT NULL,
    base_cost DECIMAL(10, 2) DEFAULT 0.00 COMMENT 'Chi phí cơ bản',
    is_active BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Danh mục các phương thức vận chuyển';

-- Bảng: Loại thanh toán (Payment_Types) - Học hỏi từ ERD
CREATE TABLE Payment_Types (
    payment_type_id INT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Tên loại thanh toán (COD, Bank Transfer, Credit Card, E-Wallet)',
    description TEXT NULL,
    is_active BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Danh mục các loại hình thanh toán được hỗ trợ';

-- Bảng: Danh mục sản phẩm (Categories)
CREATE TABLE Categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL COMMENT 'Tên danh mục',
    description TEXT NULL,
    parent_category_id INT NULL COMMENT 'Danh mục cha (cho cấu trúc đa cấp)',
    slug VARCHAR(160) NOT NULL UNIQUE COMMENT 'URL-friendly identifier',
    image_url VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_category_id) REFERENCES Categories(category_id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Danh mục sản phẩm (Thuốc, TPCN, Thiết bị y tế...)';

-- Bảng: Thương hiệu (Brands)
CREATE TABLE Brands (
    brand_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE COMMENT 'Tên thương hiệu',
    logo_url VARCHAR(255) NULL,
    description TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Thông tin thương hiệu sản phẩm';

-- Bảng: Nhà sản xuất (Manufacturers)
CREATE TABLE Manufacturers (
    manufacturer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL COMMENT 'Tên nhà sản xuất',
    country_id INT NULL COMMENT 'Quốc gia sản xuất',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (country_id) REFERENCES Countries(country_id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Thông tin nhà sản xuất';

-- =============================================
-- Bảng Quản lý Người dùng & Nhân viên
-- =============================================

-- Bảng: Người dùng / Tài khoản (Users)
CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    phone_number VARCHAR(20) NOT NULL UNIQUE COMMENT 'SĐT dùng đăng nhập/đăng ký',
    password_hash VARCHAR(255) NOT NULL COMMENT 'Mật khẩu đã mã hóa',
    full_name VARCHAR(100) NULL,
    email VARCHAR(100) NULL UNIQUE,
    default_address_id INT NULL, -- FK sẽ được thêm sau khi tạo bảng Addresses
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Tài khoản người dùng hệ thống';

-- Bảng: Phân quyền Người dùng (User_Roles)
CREATE TABLE User_Roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (role_id) REFERENCES Roles(role_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Liên kết Người dùng và Vai trò (Many-to-Many)';

-- Bảng: Địa chỉ Người dùng (Addresses)
CREATE TABLE Addresses (
    address_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    recipient_name VARCHAR(100) NOT NULL COMMENT 'Tên người nhận',
    recipient_phone VARCHAR(20) NOT NULL COMMENT 'SĐT người nhận',
    street_address VARCHAR(255) NOT NULL COMMENT 'Địa chỉ cụ thể (Số nhà, đường)',
    ward VARCHAR(100) NULL COMMENT 'Phường/Xã',
    district VARCHAR(100) NOT NULL COMMENT 'Quận/Huyện',
    city VARCHAR(100) NOT NULL COMMENT 'Tỉnh/Thành phố',
    country_id INT NOT NULL,
    -- postal_code VARCHAR(20) NULL, -- Cân nhắc nếu cần mã bưu điện
    is_default BOOLEAN DEFAULT FALSE COMMENT 'Là địa chỉ mặc định?',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (country_id) REFERENCES Countries(country_id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Địa chỉ của người dùng';

-- Thêm Foreign Key cho default_address_id trong bảng Users
ALTER TABLE Users
ADD CONSTRAINT fk_user_default_address
FOREIGN KEY (default_address_id) REFERENCES Addresses(address_id) ON DELETE SET NULL ON UPDATE CASCADE;

-- Bảng: Chi nhánh / Nhà thuốc (Branches)
CREATE TABLE Branches (
    branch_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL COMMENT 'Tên chi nhánh',
    address VARCHAR(255) NOT NULL COMMENT 'Địa chỉ chi nhánh',
    phone_number VARCHAR(20) NULL COMMENT 'SĐT hotline chi nhánh',
    latitude DECIMAL(10, 8) NULL,
    longitude DECIMAL(11, 8) NULL,
    operating_hours VARCHAR(255) NULL COMMENT 'Giờ hoạt động',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Danh sách các chi nhánh nhà thuốc';

-- Bảng: Nhân viên / Bác sĩ (Staff)
CREATE TABLE Staff (
    staff_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE NULL COMMENT 'Liên kết tài khoản đăng nhập (nếu có)',
    branch_id INT NOT NULL COMMENT 'Chi nhánh chính',
    full_name VARCHAR(100) NOT NULL,
    title VARCHAR(100) NULL COMMENT 'Chức hiệu (Bác sĩ, Dược sĩ...)',
    specialty VARCHAR(150) NULL COMMENT 'Chuyên khoa',
    workplace_info VARCHAR(255) NULL COMMENT 'Thông tin nơi làm việc khác',
    profile_image_url VARCHAR(255) NULL,
    is_available_for_consultation BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (branch_id) REFERENCES Branches(branch_id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Thông tin nhân viên, bác sĩ, dược sĩ tại chi nhánh';

-- Bảng: Phương thức thanh toán đã lưu của người dùng (User_Payment_Methods) - Học hỏi từ ERD
CREATE TABLE User_Payment_Methods (
    user_payment_method_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    payment_type_id INT NOT NULL,
    provider VARCHAR(100) NULL COMMENT 'Nhà cung cấp (VD: Visa, Mastercard, MoMo)',
    account_number VARCHAR(50) NULL COMMENT 'Số tài khoản/Số thẻ (che dấu)',
    expiry_date DATE NULL,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (payment_type_id) REFERENCES Payment_Types(payment_type_id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Lưu các phương thức thanh toán người dùng đã thêm';

-- =============================================
-- Bảng Quản lý Sản phẩm & Tồn kho
-- =============================================

-- Bảng: Sản phẩm (Products) - Thông tin chung
CREATE TABLE Products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    sku VARCHAR(100) UNIQUE NULL COMMENT 'Mã SKU chung (nếu có)',
    description TEXT NULL,
    ingredients TEXT NULL COMMENT 'Thành phần',
    usage_instructions TEXT NULL COMMENT 'Hướng dẫn sử dụng',
    contraindications TEXT NULL COMMENT 'Chống chỉ định',
    side_effects TEXT NULL COMMENT 'Tác dụng phụ',
    storage_conditions VARCHAR(255) NULL COMMENT 'Điều kiện bảo quản',
    packaging VARCHAR(150) NULL COMMENT 'Quy cách đóng gói',
    unit VARCHAR(50) NULL COMMENT 'Đơn vị tính (Viên, Hộp, Lọ...)',
    image_url VARCHAR(255) NULL COMMENT 'URL ảnh đại diện',
    category_id INT NULL,
    brand_id INT NULL,
    manufacturer_id INT NULL,
    is_prescription_required BOOLEAN DEFAULT FALSE COMMENT 'Yêu cầu đơn thuốc?',
    status ENUM('active', 'inactive', 'discontinued') DEFAULT 'active',
    slug VARCHAR(220) NOT NULL UNIQUE,
    average_rating DECIMAL(3, 2) DEFAULT 0.00,
    review_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES Categories(category_id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (brand_id) REFERENCES Brands(brand_id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (manufacturer_id) REFERENCES Manufacturers(manufacturer_id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_product_name (name),
    INDEX idx_product_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Thông tin chung về sản phẩm';

-- Bảng: Ảnh chi tiết Sản phẩm (Product_Images)
CREATE TABLE Product_Images (
    image_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    alt_text VARCHAR(200) NULL,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Lưu nhiều ảnh cho một sản phẩm';

-- Bảng: Tồn kho theo Chi nhánh (Branch_Inventory)
CREATE TABLE Branch_Inventory (
    inventory_id INT AUTO_INCREMENT PRIMARY KEY,
    branch_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity_on_hand INT DEFAULT 0,
    price DECIMAL(12, 2) NOT NULL COMMENT 'Giá bán tại chi nhánh',
    discount_price DECIMAL(12, 2) NULL COMMENT 'Giá khuyến mãi tại chi nhánh',
    expiry_date DATE NULL COMMENT 'Hạn sử dụng (quan trọng!)',
    batch_number VARCHAR(100) NULL COMMENT 'Số lô sản xuất (nếu quản lý theo lô)',
    location_in_store VARCHAR(100) NULL COMMENT 'Vị trí trong kho/cửa hàng',
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (branch_id) REFERENCES Branches(branch_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE ON UPDATE CASCADE,
    -- Đảm bảo mỗi sản phẩm/lô/hsd tại một chi nhánh là duy nhất
    UNIQUE KEY uk_branch_product_batch (branch_id, product_id, batch_number, expiry_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Quản lý tồn kho, giá, HSD theo từng chi nhánh';

-- =============================================
-- Bảng Quản lý Giỏ hàng & Đơn hàng
-- =============================================

-- Bảng: Giỏ hàng (Shopping_Carts) - Học hỏi từ ERD
CREATE TABLE Shopping_Carts (
    cart_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE NOT NULL COMMENT 'Mỗi user chỉ có 1 giỏ hàng active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Lưu thông tin giỏ hàng của người dùng';

-- Bảng: Chi tiết Giỏ hàng (Shopping_Cart_Items) - Học hỏi từ ERD
CREATE TABLE Shopping_Cart_Items (
    cart_item_id INT AUTO_INCREMENT PRIMARY KEY,
    cart_id INT NOT NULL,
    inventory_id INT NOT NULL COMMENT 'Liên kết tới sản phẩm cụ thể trong kho chi nhánh',
    quantity INT NOT NULL DEFAULT 1,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cart_id) REFERENCES Shopping_Carts(cart_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (inventory_id) REFERENCES Branch_Inventory(inventory_id) ON DELETE CASCADE ON UPDATE CASCADE, -- Nếu item kho bị xóa, xóa khỏi giỏ
    UNIQUE KEY uk_cart_inventory (cart_id, inventory_id) -- Mỗi item kho chỉ xuất hiện 1 lần trong giỏ
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Các sản phẩm trong giỏ hàng';

-- Bảng: Đơn hàng (Orders)
CREATE TABLE Orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    order_code VARCHAR(20) NOT NULL UNIQUE COMMENT 'Mã đơn hàng thân thiện (VD: NT-10001)',
    user_id INT NOT NULL,
    branch_id INT NOT NULL COMMENT 'Chi nhánh xử lý đơn hàng',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    shipping_address_id INT NOT NULL COMMENT 'Địa chỉ giao hàng',
    billing_address_id INT NULL COMMENT 'Địa chỉ thanh toán (nếu khác)',
    shipping_method_id INT NULL,
    order_status_id INT NOT NULL,
    subtotal_amount DECIMAL(14, 2) NOT NULL DEFAULT 0.00 COMMENT 'Tổng tiền hàng',
    shipping_fee DECIMAL(10, 2) DEFAULT 0.00,
    discount_amount DECIMAL(12, 2) DEFAULT 0.00 COMMENT 'Số tiền giảm giá (từ promotion)',
    final_amount DECIMAL(14, 2) NOT NULL DEFAULT 0.00 COMMENT 'Tổng tiền cuối cùng',
    payment_type_id INT NULL,
    payment_status ENUM('pending', 'paid', 'failed', 'refunded') DEFAULT 'pending',
    notes TEXT NULL COMMENT 'Ghi chú của khách hàng',
    requires_consultation BOOLEAN DEFAULT FALSE COMMENT 'Đơn hàng này có cần BS tư vấn ko?',
    assigned_staff_id INT NULL COMMENT 'Nhân viên/BS được gán tư vấn/chuẩn bị đơn',
    consultation_status ENUM('not_required', 'pending', 'completed', 'skipped') DEFAULT 'not_required',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (branch_id) REFERENCES Branches(branch_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (shipping_address_id) REFERENCES Addresses(address_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (billing_address_id) REFERENCES Addresses(address_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (shipping_method_id) REFERENCES Shipping_Methods(method_id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (order_status_id) REFERENCES Order_Statuses(status_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (payment_type_id) REFERENCES Payment_Types(payment_type_id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (assigned_staff_id) REFERENCES Staff(staff_id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_order_user (user_id),
    INDEX idx_order_branch (branch_id),
    INDEX idx_order_status (order_status_id),
    INDEX idx_order_code (order_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Thông tin các đơn đặt hàng';

-- Bảng: Chi tiết Đơn hàng (Order_Items)
CREATE TABLE Order_Items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL COMMENT 'Lưu product_id để tiện truy vấn thông tin gốc',
    inventory_id INT NOT NULL COMMENT 'Liên kết tới item tồn kho cụ thể đã bán',
    quantity INT NOT NULL,
    price_at_purchase DECIMAL(12, 2) NOT NULL COMMENT 'Giá tại thời điểm mua',
    subtotal DECIMAL(14, 2) NOT NULL COMMENT 'Thành tiền (quantity * price)',
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE RESTRICT ON UPDATE CASCADE, -- Không cho xóa product nếu có trong order item
    FOREIGN KEY (inventory_id) REFERENCES Branch_Inventory(inventory_id) ON DELETE RESTRICT ON UPDATE CASCADE -- Không cho xóa inventory nếu đã bán
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Các sản phẩm trong một đơn hàng';

-- =============================================
-- Bảng Quản lý Đơn thuốc & Tư vấn
-- =============================================

-- Bảng: Đơn thuốc (Prescriptions) - Quan trọng cho thuốc kê đơn
CREATE TABLE Prescriptions (
    prescription_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL COMMENT 'Người dùng upload đơn',
    order_id INT NULL UNIQUE COMMENT 'Gắn với đơn hàng cụ thể (nếu quy trình yêu cầu)',
    image_url VARCHAR(255) NOT NULL COMMENT 'URL ảnh đơn thuốc',
    issue_date DATE NULL COMMENT 'Ngày kê đơn',
    doctor_name VARCHAR(100) NULL,
    clinic_address VARCHAR(255) NULL,
    diagnosis TEXT NULL COMMENT 'Chẩn đoán (nếu có)',
    status ENUM('pending_verification', 'verified', 'rejected', 'used') DEFAULT 'pending_verification',
    verified_by_staff_id INT NULL COMMENT 'Nhân viên/Dược sĩ xác minh',
    verification_notes TEXT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (verified_by_staff_id) REFERENCES Staff(staff_id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Lưu thông tin đơn thuốc khách hàng tải lên';

-- Bảng: Chi tiết Đơn thuốc (Prescription_Items)
CREATE TABLE Prescription_Items (
     prescription_item_id INT AUTO_INCREMENT PRIMARY KEY,
     prescription_id INT NOT NULL,
     product_name_on_rx VARCHAR(200) NOT NULL COMMENT 'Tên thuốc ghi trên đơn',
     product_id INT NULL COMMENT 'Map với sản phẩm trong hệ thống (nếu được)',
     dosage VARCHAR(100) NULL COMMENT 'Liều lượng',
     frequency VARCHAR(100) NULL COMMENT 'Tần suất dùng',
     duration VARCHAR(100) NULL COMMENT 'Thời gian dùng',
     quantity_prescribed VARCHAR(50) NULL COMMENT 'Số lượng kê đơn (có thể là text)',
     notes TEXT NULL,
     FOREIGN KEY (prescription_id) REFERENCES Prescriptions(prescription_id) ON DELETE CASCADE ON UPDATE CASCADE,
     FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Các thuốc được kê trong một đơn';


-- Bảng: Yêu cầu Tư vấn (Consultation_Requests)
CREATE TABLE Consultation_Requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL COMMENT 'Người yêu cầu',
    branch_id INT NULL COMMENT 'Chi nhánh được yêu cầu (nếu có)',
    staff_id INT NULL COMMENT 'Bác sĩ cụ thể được yêu cầu (nếu có)',
    request_type ENUM('phone', 'message') NOT NULL,
    user_message TEXT NULL COMMENT 'Nội dung yêu cầu',
    status ENUM('pending', 'assigned', 'in_progress', 'completed', 'cancelled') DEFAULT 'pending',
    assigned_staff_id INT NULL COMMENT 'Bác sĩ/Nhân viên được gán',
    request_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (branch_id) REFERENCES Branches(branch_id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (staff_id) REFERENCES Staff(staff_id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (assigned_staff_id) REFERENCES Staff(staff_id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Lưu các yêu cầu tư vấn từ người dùng';

-- Bảng: Tin nhắn Tư vấn (Messages)
CREATE TABLE Messages (
    message_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    consultation_request_id INT NULL,
    order_id INT NULL COMMENT 'Tin nhắn liên quan đến tư vấn đơn hàng',
    sender_user_id INT NOT NULL COMMENT 'FK tới Users (là khách hoặc nhân viên)',
    receiver_user_id INT NOT NULL COMMENT 'FK tới Users (là khách hoặc nhân viên)',
    content TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    FOREIGN KEY (consultation_request_id) REFERENCES Consultation_Requests(request_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (sender_user_id) REFERENCES Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (receiver_user_id) REFERENCES Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_message_consultation (consultation_request_id),
    INDEX idx_message_order (order_id),
    INDEX idx_message_sender_receiver (sender_user_id, receiver_user_id),
    INDEX idx_message_sent_at (sent_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Lưu trữ lịch sử tin nhắn trao đổi';

-- =============================================
-- Bảng Quản lý Đánh giá & Khuyến mãi
-- =============================================

-- Bảng: Đánh giá Sản phẩm (Reviews)
CREATE TABLE Reviews (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    user_id INT NOT NULL,
    order_item_id INT UNIQUE NULL COMMENT 'Liên kết tới item đã mua để xác thực',
    branch_id INT NULL COMMENT 'Đánh giá có thể liên quan tới chi nhánh',
    rating TINYINT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT NULL,
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('pending_approval', 'approved', 'rejected') DEFAULT 'pending_approval',
    approved_by_user_id INT NULL COMMENT 'FK tới Users (Admin/Manager)',
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (order_item_id) REFERENCES Order_Items(order_item_id) ON DELETE SET NULL ON UPDATE CASCADE, -- Giữ lại review nếu order item bị xóa? Hoặc CASCADE?
    FOREIGN KEY (branch_id) REFERENCES Branches(branch_id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (approved_by_user_id) REFERENCES Users(user_id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_review_product (product_id),
    INDEX idx_review_user (user_id),
    INDEX idx_review_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Đánh giá của khách hàng về sản phẩm';

-- Bảng: Khuyến mãi (Promotions)
CREATE TABLE Promotions (
    promotion_id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE NULL COMMENT 'Mã giảm giá (nếu là coupon)',
    name VARCHAR(150) NOT NULL COMMENT 'Tên chương trình KM',
    description TEXT NULL,
    discount_type ENUM('percentage', 'fixed_amount') NOT NULL,
    discount_value DECIMAL(10, 2) NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    min_order_value DECIMAL(12, 2) NULL COMMENT 'Giá trị đơn hàng tối thiểu',
    usage_limit_per_customer INT NULL COMMENT 'Giới hạn dùng/khách',
    total_usage_limit INT NULL COMMENT 'Tổng lượt dùng tối đa',
    total_used_count INT DEFAULT 0 COMMENT 'Số lượt đã dùng',
    applicable_scope ENUM('all', 'specific_categories', 'specific_products', 'specific_branches') DEFAULT 'all',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_promo_code (code),
    INDEX idx_promo_dates (start_date, end_date),
    INDEX idx_promo_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Thông tin các chương trình khuyến mãi';

-- Bảng liên kết: Khuyến mãi áp dụng cho Danh mục (Promotion_Categories) - Học hỏi từ ERD
CREATE TABLE Promotion_Categories (
    promotion_id INT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (promotion_id, category_id),
    FOREIGN KEY (promotion_id) REFERENCES Promotions(promotion_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (category_id) REFERENCES Categories(category_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Link KM với danh mục áp dụng';

-- Bảng liên kết: Khuyến mãi áp dụng cho Sản phẩm (Promotion_Products)
CREATE TABLE Promotion_Products (
    promotion_id INT NOT NULL,
    product_id INT NOT NULL,
    PRIMARY KEY (promotion_id, product_id),
    FOREIGN KEY (promotion_id) REFERENCES Promotions(promotion_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Link KM với sản phẩm áp dụng';

-- Bảng liên kết: Khuyến mãi áp dụng cho Chi nhánh (Promotion_Branches)
CREATE TABLE Promotion_Branches (
    promotion_id INT NOT NULL,
    branch_id INT NOT NULL,
    PRIMARY KEY (promotion_id, branch_id),
    FOREIGN KEY (promotion_id) REFERENCES Promotions(promotion_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (branch_id) REFERENCES Branches(branch_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Link KM với chi nhánh áp dụng';

-- Bảng: Lịch sử sử dụng Khuyến mãi (Promotion_Usage)
CREATE TABLE Promotion_Usage (
     usage_id BIGINT AUTO_INCREMENT PRIMARY KEY,
     promotion_id INT NOT NULL,
     order_id INT NOT NULL,
     user_id INT NOT NULL,
     usage_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     discount_applied DECIMAL(12, 2) NOT NULL,
     FOREIGN KEY (promotion_id) REFERENCES Promotions(promotion_id) ON DELETE RESTRICT ON UPDATE CASCADE, -- Giữ lại lịch sử nếu KM bị xóa?
     FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE ON UPDATE CASCADE,
     FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
     INDEX idx_promo_usage_user (user_id, promotion_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Theo dõi việc áp dụng KM vào đơn hàng';
