-- MySQL dump 10.13  Distrib 9.2.0, for macos14.7 (arm64)
--
-- Host: localhost    Database: pharmacy_db
-- ------------------------------------------------------
-- Server version	9.2.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `banners`
--

DROP TABLE IF EXISTS `banners`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `banners` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `image_url` varchar(1024) COLLATE utf8mb4_unicode_ci NOT NULL,
  `target_url` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `display_order` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_active_banners` (`is_active`,`start_date`,`end_date`,`display_order`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `banners`
--

LOCK TABLES `banners` WRITE;
/*!40000 ALTER TABLE `banners` DISABLE KEYS */;
INSERT INTO `banners` VALUES (1,'Summer Sale Banner - Updated!','https://example.com/images/summer_sale.png','https://example.com/summer-sale-page','2025-04-30 17:00:00','2025-05-31 16:59:59',0,5,'2025-05-10 21:34:11','2025-05-10 21:41:02'),(2,'Summer Sale Banner 1','https://example.com/images/summer_sale.png','https://example.com/summer-sale-page','2025-04-30 17:00:00','2025-05-31 16:59:59',1,1,'2025-05-10 21:34:24','2025-05-10 21:41:41'),(3,'Summer Sale Banner 2','https://example.com/images/summer_sale.png','https://example.com/summer-sale-page','2025-04-30 17:00:00','2025-05-31 16:59:59',1,1,'2025-05-10 21:34:27','2025-05-10 21:41:45'),(4,'Summer Sale Banner 3','https://example.com/images/summer_sale.png','https://example.com/summer-sale-page','2024-06-30 17:00:00','2024-07-31 16:59:59',1,1,'2025-05-10 21:34:29','2025-05-10 21:34:29'),(5,'Summer Sale Banner 43','https://example.com/images/summer_sale.png','https://example.com/summer-sale-page','2024-06-30 17:00:00','2024-07-31 16:59:59',1,1,'2025-05-10 21:34:33','2025-05-10 21:34:33');
/*!40000 ALTER TABLE `banners` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Branch_Inventory`
--

DROP TABLE IF EXISTS `Branch_Inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Branch_Inventory` (
  `inventory_id` int NOT NULL AUTO_INCREMENT,
  `branch_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity_on_hand` int DEFAULT '0',
  `price` decimal(38,2) NOT NULL,
  `discount_price` decimal(38,2) DEFAULT NULL,
  `expiry_date` date DEFAULT NULL COMMENT 'Hạn sử dụng (quan trọng!)',
  `batch_number` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `location_in_store` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`inventory_id`),
  UNIQUE KEY `uk_branch_product_batch` (`branch_id`,`product_id`,`batch_number`,`expiry_date`),
  UNIQUE KEY `uq_branch_product` (`branch_id`,`product_id`,`batch_number`),
  KEY `FK9a4huhi5hv9j0hup9u1k2dn4o` (`product_id`),
  CONSTRAINT `branch_inventory_ibfk_1` FOREIGN KEY (`branch_id`) REFERENCES `Branches` (`branch_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `branch_inventory_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `Products` (`product_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Quản lý tồn kho, giá, HSD theo từng chi nhánh';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Branch_Inventory`
--

LOCK TABLES `Branch_Inventory` WRITE;
/*!40000 ALTER TABLE `Branch_Inventory` DISABLE KEYS */;
INSERT INTO `Branch_Inventory` VALUES (1,1,5,149,125000.00,115000.00,'2024-12-31','BATCH-2023-001','Kệ A1, Tầng 2','2025-04-26 17:47:17'),(2,2,6,78,75000.50,NULL,'2024-06-30','BATCH-2023-205','Kệ B3, Tầng 1','2025-04-26 17:39:49'),(3,2,7,200,120000.00,110000.00,'2025-01-15','BATCH-2023-002','Kệ trung tâm','2025-04-26 17:40:12'),(5,3,9,45,250000.00,230000.00,'2023-12-15','BATCH-2022-308','Tủ lạnh dược phẩm','2025-04-26 17:41:11'),(6,5,10,60,78000.00,75000.00,'2024-07-20','BATCH-2023-205B','Kệ B1, Tầng 1','2025-04-26 17:41:30'),(7,6,11,90,130000.00,NULL,'2024-11-30','BATCH-2023-001A','Kệ A3','2025-04-26 17:41:47'),(8,7,12,30,350000.00,320000.00,'2025-03-15','BATCH-2023-517','Tủ bảo quản đặc biệt','2025-04-26 17:42:06'),(9,8,13,25,260000.00,NULL,'2023-11-30','BATCH-2022-308B','Tủ lạnh dược phẩm','2025-04-26 17:42:31'),(10,9,14,200,45000.00,42000.00,'2024-08-31','BATCH-2023-623','Kệ C4, Tầng 1','2025-04-26 17:42:47'),(11,1,15,150,125000.00,115000.00,'2024-12-31','BATCH-2023-001','Kệ A1, Tầng 2','2025-04-26 17:50:18'),(12,1,12,150,125000.00,115000.00,'2024-12-31','BATCH-2023-001','Kệ A1, Tầng 2','2025-04-26 17:50:23'),(14,1,6,150,125000.00,115000.00,'2024-12-31','BATCH-2023-001','Kệ A1, Tầng 2','2025-04-26 17:50:34'),(17,2,8,149,125000.00,115000.00,'2024-12-31','BATCH-2023-001','Kệ A1, Tầng 2','2025-04-26 17:50:56'),(18,3,8,149,125000.00,115000.00,'2024-12-31','BATCH-2023-001','Kệ A1, Tầng 2','2025-04-26 17:51:00'),(19,4,8,140,125000.00,115000.00,'2024-12-31','BATCH-2023-001','Kệ A1, Tầng 2','2025-04-26 17:51:02');
/*!40000 ALTER TABLE `Branch_Inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Branch_Staff`
--

DROP TABLE IF EXISTS `Branch_Staff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Branch_Staff` (
  `branch_staff_id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `end_date` datetime(6) DEFAULT NULL,
  `is_primary_branch` bit(1) DEFAULT NULL,
  `start_date` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `branch_id` int NOT NULL,
  `staff_id` int NOT NULL,
  PRIMARY KEY (`branch_staff_id`),
  KEY `FKd9k2nwn8acl64itpllrevd3h3` (`branch_id`),
  KEY `FKkljjy8feekl2yx3yqk87lcjkn` (`staff_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Branch_Staff`
--

LOCK TABLES `Branch_Staff` WRITE;
/*!40000 ALTER TABLE `Branch_Staff` DISABLE KEYS */;
/*!40000 ALTER TABLE `Branch_Staff` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Branches`
--

DROP TABLE IF EXISTS `Branches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Branches` (
  `branch_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Địa chỉ chi nhánh',
  `phone_number` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `latitude` decimal(38,2) DEFAULT NULL,
  `longitude` decimal(38,2) DEFAULT NULL,
  `operating_hours` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Giờ hoạt động',
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Danh sách các chi nhánh nhà thuốc';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Branches`
--

LOCK TABLES `Branches` WRITE;
/*!40000 ALTER TABLE `Branches` DISABLE KEYS */;
INSERT INTO `Branches` VALUES (1,'Nhà thuốc Hoàng Trung','111 Đường Bạch Đằng, Quận Bình Thạnh, TP.HCM','0906677889',10.80,106.72,'6:00 - 20:00 (Thứ 2 - Chủ Nhật)',1,NULL,NULL),(2,'Nhà thuốc Hồng Phúc','456 Đường Nguyễn Huệ, Quận 1, TP.HCM','0907654321',10.77,106.70,'8:00 - 21:00 (Thứ 2 - Thứ 7)',1,NULL,NULL),(3,'Nhà thuốc Gia Hân','789 Đường Cách Mạng Tháng 8, Quận 3, TP.HCM','0901122334',10.78,106.69,'24/7',0,NULL,NULL),(4,'Nhà thuốc Phương Thảo','321 Đường Lý Thường Kiệt, Quận 10, TP.HCM','0909988776',10.77,106.67,'6:00 - 23:00 (Hàng ngày)',1,NULL,NULL),(5,'Nhà thuốc Bảo An','654 Đường 3 Tháng 2, Quận 10, TP.HCM','0905544332',10.77,106.67,'7:30 - 20:30 (Thứ 2 - Chủ Nhật)',1,NULL,NULL),(6,'Nhà thuốc Tâm Đức','555 Đường Trường Chinh, Quận Tân Bình, TP.HCM','0907894561',10.80,106.65,'7:00 - 23:00 (Thứ 2 - Chủ Nhật)',1,NULL,NULL),(7,'Nhà thuốc Hòa Bình','444 Đường Lê Quang Định, Quận Bình Thạnh, TP.HCM','0904561237',10.81,106.72,'6:30 - 22:00 (Hàng ngày)',1,NULL,NULL),(8,'Nhà thuốc An Khang','333 Đường Nguyễn Thị Minh Khai, Quận 3, TP.HCM','0901239876',10.78,106.69,'8:30 - 21:30 (Hàng ngày)',0,NULL,NULL),(9,'Nhà thuốc Thiên Phú','222 Đường Lê Văn Việt, Quận 9, TP.HCM','0909876543',10.84,106.79,'7:00 - 19:00 (Thứ 2 - Thứ 6)',1,NULL,NULL);
/*!40000 ALTER TABLE `Branches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Brands`
--

DROP TABLE IF EXISTS `Brands`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Brands` (
  `brand_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `logo_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`brand_id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `UKoce3937d2f4mpfqrycbr0l93m` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Thông tin thương hiệu sản phẩm';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Brands`
--

LOCK TABLES `Brands` WRITE;
/*!40000 ALTER TABLE `Brands` DISABLE KEYS */;
INSERT INTO `Brands` VALUES (1,'Abbott LaboratorieS',NULL,NULL,'2025-04-17 22:07:02','2025-05-12 20:23:47'),(2,'Ampharco U.S.A',NULL,'','2025-04-17 22:07:11','2025-04-17 22:07:11'),(3,'Allnature',NULL,'','2025-04-17 22:07:23','2025-04-17 22:07:23'),(5,'Balancepharm',NULL,'','2025-04-17 22:07:40','2025-04-17 22:07:40'),(6,'CZ PHARMA',NULL,'','2025-04-17 22:07:53','2025-04-17 22:07:53'),(7,'Ciliary Healthcare',NULL,'','2025-04-17 22:08:00','2025-04-17 22:08:00'),(8,'Cancer Council',NULL,'','2025-04-17 22:08:10','2025-04-17 22:08:10'),(9,'Coswell',NULL,'','2025-04-17 22:08:20','2025-04-17 22:08:20'),(10,'Dan Pharm LTD',NULL,'','2025-04-17 22:08:32','2025-04-17 22:08:32'),(11,'Do Van Tu',NULL,'','2025-04-25 00:54:02','2025-04-25 00:54:02'),(12,'Hoang Tran',NULL,'','2025-04-25 01:04:02','2025-04-25 01:04:02');
/*!40000 ALTER TABLE `Brands` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Categories`
--

DROP TABLE IF EXISTS `Categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Categories` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_category_id` int DEFAULT NULL COMMENT 'Danh mục cha (cho cấu trúc đa cấp)',
  `slug` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `image_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `slug` (`slug`),
  UNIQUE KEY `UKoul14ho7bctbefv8jywp5v3i2` (`slug`),
  KEY `FK8xuh59qh8v22aqcwyy37rkvnp` (`parent_category_id`),
  CONSTRAINT `categories_ibfk_1` FOREIGN KEY (`parent_category_id`) REFERENCES `Categories` (`category_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Danh mục sản phẩm (Thuốc, TPCN, Thiết bị y tế...)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Categories`
--

LOCK TABLES `Categories` WRITE;
/*!40000 ALTER TABLE `Categories` DISABLE KEYS */;
INSERT INTO `Categories` VALUES (3,'Pain Relievers','Fast-acting pain relief for headaches, muscle aches, and more.',NULL,'pain-relievers','http://example.com/images/pain-relief.jpg','2025-04-17 14:35:46','2025-04-17 14:35:46'),(4,'Antibiotics','Prescription antibiotics for bacterial infections.',NULL,'antibiotics','http://example.com/images/antibiotics.jpg','2025-04-17 14:35:56','2025-04-17 14:35:56'),(5,'Allergy Medicine','Relief for seasonal allergies, sneezing, and itchy eyes.',NULL,'allergy-medicine','http://example.com/images/allergy-relief.jpg','2025-04-17 14:36:04','2025-04-17 14:36:04'),(6,'Digestive Health','Medications for heartburn, indigestion, and stomach issues.',NULL,'digestive-health','http://example.com/images/digestive-medicine.jpg','2025-04-17 14:36:13','2025-04-17 14:36:13');
/*!40000 ALTER TABLE `Categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Consultation_Requests`
--

DROP TABLE IF EXISTS `Consultation_Requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Consultation_Requests` (
  `request_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT 'Người yêu cầu',
  `branch_id` int DEFAULT NULL COMMENT 'Chi nhánh được yêu cầu (nếu có)',
  `staff_id` int DEFAULT NULL COMMENT 'Bác sĩ cụ thể được yêu cầu (nếu có)',
  `request_type` enum('phone','message') COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_message` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('pending','assigned','in_progress','completed','cancelled') COLLATE utf8mb4_unicode_ci DEFAULT 'pending',
  `assigned_staff_id` int DEFAULT NULL COMMENT 'Bác sĩ/Nhân viên được gán',
  `request_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `last_updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`request_id`),
  KEY `FK9a8j1tnataob8a8wx3cber2t7` (`assigned_staff_id`),
  KEY `FK7pooiu4d11dlc0xu9dhadw79s` (`branch_id`),
  KEY `FK94b3jppmbkvb3kywma44cnvn0` (`staff_id`),
  KEY `FKoj69hspa2rcrxpror8efvjnl2` (`user_id`),
  CONSTRAINT `consultation_requests_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `consultation_requests_ibfk_2` FOREIGN KEY (`branch_id`) REFERENCES `Branches` (`branch_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `consultation_requests_ibfk_3` FOREIGN KEY (`staff_id`) REFERENCES `Staff` (`staff_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `consultation_requests_ibfk_4` FOREIGN KEY (`assigned_staff_id`) REFERENCES `Staff` (`staff_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Lưu các yêu cầu tư vấn từ người dùng';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Consultation_Requests`
--

LOCK TABLES `Consultation_Requests` WRITE;
/*!40000 ALTER TABLE `Consultation_Requests` DISABLE KEYS */;
/*!40000 ALTER TABLE `Consultation_Requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Countries`
--

DROP TABLE IF EXISTS `Countries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Countries` (
  `country_id` int NOT NULL AUTO_INCREMENT,
  `country_code` varchar(2) COLLATE utf8mb4_unicode_ci NOT NULL,
  `country_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`country_id`),
  UNIQUE KEY `country_code` (`country_code`),
  UNIQUE KEY `country_name` (`country_name`),
  UNIQUE KEY `UKc9ccge90oirf3ivfxcd2xnmnw` (`country_code`),
  UNIQUE KEY `UKlx3r8cp4g7xkaqximbtxum74r` (`country_name`),
  UNIQUE KEY `UK4y6twd7e6y4yfhs1ghu84eglm` (`country_code`),
  UNIQUE KEY `UK5y4r7j3ksmtkjsoq0ibx6arrc` (`country_name`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Danh mục các quốc gia';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Countries`
--

LOCK TABLES `Countries` WRITE;
/*!40000 ALTER TABLE `Countries` DISABLE KEYS */;
INSERT INTO `Countries` VALUES (1,'VN','Viet Nam'),(2,'US','Hoa Kỳ'),(3,'TW','Đài Loan'),(4,'TH','Thái Lan'),(5,'ES','Tây Ban Nha'),(6,'SE','Thuỵ Điển'),(7,'CH','Thuỵ Sĩ'),(8,'CA','Canada');
/*!40000 ALTER TABLE `Countries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Manufacturers`
--

DROP TABLE IF EXISTS `Manufacturers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Manufacturers` (
  `manufacturer_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `country_id` int DEFAULT NULL COMMENT 'Quốc gia sản xuất',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `description` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`manufacturer_id`),
  UNIQUE KEY `UK96mjlr8ukhao3a77g65619k9m` (`name`),
  KEY `FKkmud332tpqmpxi27isqpvqeo6` (`country_id`),
  CONSTRAINT `manufacturers_ibfk_1` FOREIGN KEY (`country_id`) REFERENCES `Countries` (`country_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Thông tin nhà sản xuất';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Manufacturers`
--

LOCK TABLES `Manufacturers` WRITE;
/*!40000 ALTER TABLE `Manufacturers` DISABLE KEYS */;
INSERT INTO `Manufacturers` VALUES (2,'Công ty Dược phẩm Traphaco',1,'2025-04-17 21:48:11','2025-04-17 21:48:11',NULL),(3,'Công ty Dược phẩm Sanofi',1,'2025-04-17 21:48:19','2025-04-17 21:48:19',NULL),(4,'Công ty Dược phẩm Imexpharm',1,'2025-04-17 21:48:28','2025-04-17 21:48:28',NULL),(5,'Công ty CP Dược Bình Định',1,'2025-04-17 21:48:35','2025-04-17 21:48:35',NULL),(6,'Công ty cổ phần Dược Pymepharco',1,'2025-04-17 21:48:46','2025-04-17 21:48:46',NULL),(7,'Công ty CP xuất nhập khẩu y tế Domesco',1,'2025-04-17 21:48:57','2025-04-17 21:48:57',NULL),(8,'Công ty Cổ phần Dược phẩm TV.Pharm',1,'2025-04-17 21:49:04','2025-04-17 21:49:04',NULL),(9,'Công ty CP Dược phẩm Hà Tây',1,'2025-04-17 21:49:12','2025-04-17 21:49:12',NULL),(10,'Công ty cổ phần Dược phẩm OPC',1,'2025-04-17 21:49:21','2025-04-17 21:49:21',NULL),(11,'Công ty cổ phần Dược phẩm OPCC',1,'2025-05-12 06:42:10','2025-05-12 06:42:10',NULL);
/*!40000 ALTER TABLE `Manufacturers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Messages`
--

DROP TABLE IF EXISTS `Messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Messages` (
  `message_id` bigint NOT NULL AUTO_INCREMENT,
  `consultation_request_id` int DEFAULT NULL,
  `order_id` int DEFAULT NULL COMMENT 'Tin nhắn liên quan đến tư vấn đơn hàng',
  `sender_user_id` int NOT NULL COMMENT 'FK tới Users (là khách hoặc nhân viên)',
  `receiver_user_id` int NOT NULL COMMENT 'FK tới Users (là khách hoặc nhân viên)',
  `content` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sent_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `read_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`message_id`),
  KEY `idx_message_consultation` (`consultation_request_id`),
  KEY `idx_message_order` (`order_id`),
  KEY `idx_message_sender_receiver` (`sender_user_id`,`receiver_user_id`),
  KEY `idx_message_sent_at` (`sent_at`),
  KEY `FK3yy0xw0mtemj8erxg6kwg2ney` (`receiver_user_id`),
  CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`consultation_request_id`) REFERENCES `Consultation_Requests` (`request_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`order_id`) REFERENCES `Orders` (`order_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `messages_ibfk_3` FOREIGN KEY (`sender_user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `messages_ibfk_4` FOREIGN KEY (`receiver_user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Lưu trữ lịch sử tin nhắn trao đổi';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Messages`
--

LOCK TABLES `Messages` WRITE;
/*!40000 ALTER TABLE `Messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `Messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Order_Items`
--

DROP TABLE IF EXISTS `Order_Items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Order_Items` (
  `order_item_id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL COMMENT 'Lưu product_id để tiện truy vấn thông tin gốc',
  `inventory_id` int NOT NULL COMMENT 'Liên kết tới item tồn kho cụ thể đã bán',
  `quantity` int NOT NULL,
  `price_at_purchase` decimal(38,2) NOT NULL,
  `subtotal` decimal(38,2) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`order_item_id`),
  KEY `FKoumaybyuekewx3ttagyr49h20` (`inventory_id`),
  KEY `FKnx2qghxfmt1gwugsh3owpeirr` (`order_id`),
  KEY `FKoqh4wvbt14k562bgga9okv868` (`product_id`),
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `Orders` (`order_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `Products` (`product_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `order_items_ibfk_3` FOREIGN KEY (`inventory_id`) REFERENCES `Branch_Inventory` (`inventory_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Các sản phẩm trong một đơn hàng';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Order_Items`
--

LOCK TABLES `Order_Items` WRITE;
/*!40000 ALTER TABLE `Order_Items` DISABLE KEYS */;
INSERT INTO `Order_Items` VALUES (1,1,6,14,1,125000.00,125000.00,'2025-05-05 01:43:59','2025-05-05 01:43:59'),(2,1,5,1,2,125000.00,250000.00,'2025-05-05 01:43:59','2025-05-05 01:43:59'),(3,2,5,1,2,125000.00,250000.00,'2025-05-05 06:31:55','2025-05-05 06:31:55'),(4,2,6,14,1,125000.00,125000.00,'2025-05-05 06:31:55','2025-05-05 06:31:55'),(5,3,5,1,2,125000.00,250000.00,'2025-05-06 18:17:44','2025-05-06 18:17:44'),(6,3,6,14,1,125000.00,125000.00,'2025-05-06 18:17:44','2025-05-06 18:17:44'),(7,4,9,5,4,250000.00,1000000.00,'2025-05-06 18:21:53','2025-05-06 18:21:53'),(8,5,8,19,6,125000.00,750000.00,'2025-05-06 18:25:28','2025-05-06 18:25:28'),(9,6,6,2,2,75000.50,150001.00,'2025-05-06 18:35:35','2025-05-06 18:35:35'),(10,7,8,19,2,125000.00,250000.00,'2025-05-06 18:37:50','2025-05-06 18:37:50'),(11,8,8,19,2,125000.00,250000.00,'2025-05-06 18:39:02','2025-05-06 18:39:02'),(12,9,8,19,2,125000.00,250000.00,'2025-05-06 18:40:45','2025-05-06 18:40:45'),(13,10,8,19,2,125000.00,250000.00,'2025-05-06 18:41:36','2025-05-06 18:41:36'),(14,11,8,19,2,125000.00,250000.00,'2025-05-06 19:14:47','2025-05-06 19:14:47'),(15,12,8,17,1,125000.00,125000.00,'2025-05-18 04:12:39','2025-05-18 04:12:39'),(16,12,5,1,1,125000.00,125000.00,'2025-05-18 04:12:39','2025-05-18 04:12:39'),(17,12,8,19,6,125000.00,750000.00,'2025-05-18 04:12:39','2025-05-18 04:12:39'),(18,12,8,18,1,125000.00,125000.00,'2025-05-18 04:12:39','2025-05-18 04:12:39');
/*!40000 ALTER TABLE `Order_Items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Order_Statuses`
--

DROP TABLE IF EXISTS `Order_Statuses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Order_Statuses` (
  `status_id` int NOT NULL AUTO_INCREMENT,
  `status_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_final_state` tinyint(1) DEFAULT '0' COMMENT 'Đánh dấu trạng thái cuối cùng (không thể chuyển tiếp)',
  PRIMARY KEY (`status_id`),
  UNIQUE KEY `status_name` (`status_name`),
  UNIQUE KEY `UK8hx04g0c3ngcm6098i3m3nll2` (`status_name`),
  UNIQUE KEY `UK5np0xb0dfrf3xgbimmn891w50` (`status_name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Danh mục các trạng thái đơn hàng';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Order_Statuses`
--

LOCK TABLES `Order_Statuses` WRITE;
/*!40000 ALTER TABLE `Order_Statuses` DISABLE KEYS */;
INSERT INTO `Order_Statuses` VALUES (1,'PENDING',NULL,0),(2,'PROCESSING',NULL,0),(3,'CANCELLED',NULL,0),(4,'RETURNED',NULL,0),(5,'DELIVERED',NULL,0),(6,'SHIPPED',NULL,0);
/*!40000 ALTER TABLE `Order_Statuses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Orders`
--

DROP TABLE IF EXISTS `Orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Orders` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `order_code` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` int NOT NULL,
  `branch_id` int DEFAULT NULL,
  `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `shipping_method_id` int DEFAULT NULL,
  `order_status_id` int NOT NULL,
  `subtotal_amount` decimal(38,2) NOT NULL,
  `shipping_fee` decimal(38,2) DEFAULT NULL,
  `discount_amount` decimal(38,2) DEFAULT NULL,
  `final_amount` decimal(38,2) NOT NULL,
  `payment_type_id` int DEFAULT NULL,
  `payment_status` enum('pending','paid','failed','refunded') COLLATE utf8mb4_unicode_ci DEFAULT 'pending',
  `notes` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `requires_consultation` tinyint(1) DEFAULT '0' COMMENT 'Đơn hàng này có cần BS tư vấn ko?',
  `assigned_staff_id` int DEFAULT NULL COMMENT 'Nhân viên/BS được gán tư vấn/chuẩn bị đơn',
  `consultation_status` enum('not_required','pending','completed','skipped') COLLATE utf8mb4_unicode_ci DEFAULT 'not_required',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `shipping_address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Địa chỉ giao hàng',
  `cancelled_at` timestamp NULL DEFAULT NULL,
  `applied_promotion_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Mã khuyến mãi đã áp dụng',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `order_code` (`order_code`),
  UNIQUE KEY `UKdhk2umg8ijjkg4njg6891trit` (`order_code`),
  UNIQUE KEY `UK4uhcaixqu5lh7gb6gqx3tqysx` (`order_code`),
  KEY `idx_order_user` (`user_id`),
  KEY `idx_order_branch` (`branch_id`),
  KEY `idx_order_status` (`order_status_id`),
  KEY `idx_order_code` (`order_code`),
  KEY `FKsnd975a78ui3bd4x74bkl4sgh` (`assigned_staff_id`),
  KEY `FK7b1t9toti56m3i1hc52qc0u0l` (`payment_type_id`),
  KEY `FKdr8ycmsgodwwhtankpa2ts7d3` (`shipping_method_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`branch_id`) REFERENCES `Branches` (`branch_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `orders_ibfk_5` FOREIGN KEY (`shipping_method_id`) REFERENCES `Shipping_Methods` (`method_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `orders_ibfk_6` FOREIGN KEY (`order_status_id`) REFERENCES `Order_Statuses` (`status_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `orders_ibfk_7` FOREIGN KEY (`payment_type_id`) REFERENCES `Payment_Types` (`payment_type_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `orders_ibfk_8` FOREIGN KEY (`assigned_staff_id`) REFERENCES `Staff` (`staff_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Thông tin các đơn đặt hàng';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Orders`
--

LOCK TABLES `Orders` WRITE;
/*!40000 ALTER TABLE `Orders` DISABLE KEYS */;
INSERT INTO `Orders` VALUES (1,'ORD-2F51511A',3,1,'2025-05-05 01:43:59',2,5,375000.00,28000.00,0.00,403000.00,2,'pending','Leave package at the front door.',0,NULL,'not_required','2025-05-05 01:43:59','2025-05-15 14:22:56',NULL,'2025-05-05 21:39:13',NULL),(2,'ORD-61026516',3,1,'2025-05-05 06:31:55',2,5,375000.00,28000.00,0.00,403000.00,2,'pending','Leave package at the front door.',0,NULL,'not_required','2025-05-05 06:31:55','2025-05-15 14:22:56','đường NK2, khu phố 3A, phường Thới Hoà, thị xã Bến Cát, tỉnh Bình Dương','2025-05-06 18:34:18',NULL),(3,'ORD-F1B4174C',3,1,'2025-05-06 18:17:44',2,3,375000.00,28000.00,0.00,403000.00,2,'pending','Order without promotion.',0,NULL,'not_required','2025-05-06 18:17:44','2025-05-06 18:34:23','123 Main St, Anytown, USA','2025-05-06 18:34:23',NULL),(4,'ORD-7C88F7FE',3,3,'2025-05-06 18:21:53',2,3,1000000.00,28000.00,100000.00,928000.00,2,'pending','Order with 10% discount.',0,NULL,'not_required','2025-05-06 18:21:53','2025-05-06 18:34:26','123 Main St, Anytown, USA','2025-05-06 18:34:26','SAVE10PERCENT'),(5,'ORD-D463B520',3,4,'2025-05-06 18:25:28',2,3,750000.00,28000.00,75000.00,703000.00,2,'pending','Order with 10% discount., subtotal too low.',0,NULL,'not_required','2025-05-06 18:25:28','2025-05-06 18:34:28','123 Main St, Anytown, USA','2025-05-06 18:34:28','SAVE10PERCENT'),(6,'ORD-55781A25',3,2,'2025-05-06 18:35:35',2,5,150001.00,28000.00,0.00,178001.00,2,'pending','Order with 10% discount., subtotal too low.',0,NULL,'not_required','2025-05-06 18:35:35','2025-05-17 03:05:10','123 Main St, Anytown, USA',NULL,NULL),(7,'ORD-C080BD49',3,4,'2025-05-06 18:37:50',2,3,250000.00,28000.00,0.00,278000.00,2,'pending','Order with 10% discount., subtotal too low.',0,NULL,'not_required','2025-05-06 18:37:50','2025-05-06 18:38:23','123 Main St, Anytown, USA','2025-05-06 18:38:23',NULL),(8,'ORD-D67DA846',3,4,'2025-05-06 18:39:02',2,3,250000.00,28000.00,0.00,278000.00,2,'pending','Order with 10% discount., subtotal too low.',0,NULL,'not_required','2025-05-06 18:39:02','2025-05-06 18:40:20','123 Main St, Anytown, USA','2025-05-06 18:40:20',NULL),(9,'ORD-52A48D3E',3,4,'2025-05-06 18:40:45',2,3,250000.00,28000.00,0.00,278000.00,2,'pending','Order with 10% discount., subtotal too low.',0,NULL,'not_required','2025-05-06 18:40:45','2025-05-06 18:41:15','123 Main St, Anytown, USA','2025-05-06 18:41:15',NULL),(10,'ORD-370A58B3',3,4,'2025-05-06 18:41:36',2,5,250000.00,28000.00,0.00,278000.00,2,'pending','Order with 10% discount., subtotal too low.',0,NULL,'not_required','2025-05-06 18:41:36','2025-05-17 03:03:14','123 Main St, Anytown, USA',NULL,NULL),(11,'ORD-A5E312D4',3,4,'2025-05-06 19:14:47',2,5,250000.00,28000.00,25000.00,253000.00,2,'pending','Order with 10% discount., subtotal too low.',0,NULL,'not_required','2025-05-06 19:14:47','2025-05-17 03:03:14','123 Main St, Anytown, USA',NULL,'SUMMER10_2'),(12,'ORD-4526E9C2',3,NULL,'2025-05-18 04:12:39',2,1,1125000.00,28000.00,112500.00,1040500.00,2,'pending',NULL,0,NULL,NULL,'2025-05-18 04:12:39','2025-05-18 04:12:39','123 Main St, Anytown, USA',NULL,'SUMMER10_2');
/*!40000 ALTER TABLE `Orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Payment_Types`
--

DROP TABLE IF EXISTS `Payment_Types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Payment_Types` (
  `payment_type_id` int NOT NULL AUTO_INCREMENT,
  `type_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`payment_type_id`),
  UNIQUE KEY `type_name` (`type_name`),
  UNIQUE KEY `UKc2v1842wjtfsc64x7foq6dl0` (`type_name`),
  UNIQUE KEY `UKenepklkybqsv9dpcf9vp2o2um` (`type_name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Danh mục các loại hình thanh toán được hỗ trợ';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Payment_Types`
--

LOCK TABLES `Payment_Types` WRITE;
/*!40000 ALTER TABLE `Payment_Types` DISABLE KEYS */;
INSERT INTO `Payment_Types` VALUES (1,'COD','Thanh toán khi nhận hàng',1),(2,'MOMO','Thanh toán bằng ví MoMo',1),(3,'BANK','Thanh toán bằng QR ngân hàng',1),(4,'BITCOINT','Thanh toán bằng tiền điện tử',0);
/*!40000 ALTER TABLE `Payment_Types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Prescription_Items`
--

DROP TABLE IF EXISTS `Prescription_Items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Prescription_Items` (
  `prescription_item_id` int NOT NULL AUTO_INCREMENT,
  `prescription_id` int NOT NULL,
  `product_name_on_rx` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `product_id` int DEFAULT NULL COMMENT 'Map với sản phẩm trong hệ thống (nếu được)',
  `dosage` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `frequency` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `duration` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `quantity_prescribed` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `notes` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`prescription_item_id`),
  KEY `FKm9duihgfgh4x8bx4khs7wav0f` (`prescription_id`),
  KEY `FKhbd9q5qt77wvngjhd4uo0vckn` (`product_id`),
  CONSTRAINT `prescription_items_ibfk_1` FOREIGN KEY (`prescription_id`) REFERENCES `Prescriptions` (`prescription_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `prescription_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `Products` (`product_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Các thuốc được kê trong một đơn';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Prescription_Items`
--

LOCK TABLES `Prescription_Items` WRITE;
/*!40000 ALTER TABLE `Prescription_Items` DISABLE KEYS */;
/*!40000 ALTER TABLE `Prescription_Items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Prescriptions`
--

DROP TABLE IF EXISTS `Prescriptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Prescriptions` (
  `prescription_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT 'Người dùng upload đơn',
  `order_id` int DEFAULT NULL COMMENT 'Gắn với đơn hàng cụ thể (nếu quy trình yêu cầu)',
  `image_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'URL ảnh đơn thuốc',
  `issue_date` date DEFAULT NULL COMMENT 'Ngày kê đơn',
  `doctor_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `clinic_address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `diagnosis` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('pending_verification','verified','rejected','used') COLLATE utf8mb4_unicode_ci DEFAULT 'pending_verification',
  `verified_by_staff_id` int DEFAULT NULL COMMENT 'Nhân viên/Dược sĩ xác minh',
  `verification_notes` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `uploaded_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`prescription_id`),
  UNIQUE KEY `order_id` (`order_id`),
  UNIQUE KEY `UKal7b39k590rx016vfqxih73ee` (`order_id`),
  UNIQUE KEY `UKr5eyy95csswjhuyp4oec41ck8` (`order_id`),
  KEY `FKmrfayvwbn2o2a5bbu3xy820r2` (`user_id`),
  KEY `FK6nj29xgnss7sau50ju217cqhp` (`verified_by_staff_id`),
  CONSTRAINT `prescriptions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `prescriptions_ibfk_2` FOREIGN KEY (`order_id`) REFERENCES `Orders` (`order_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `prescriptions_ibfk_3` FOREIGN KEY (`verified_by_staff_id`) REFERENCES `Staff` (`staff_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Lưu thông tin đơn thuốc khách hàng tải lên';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Prescriptions`
--

LOCK TABLES `Prescriptions` WRITE;
/*!40000 ALTER TABLE `Prescriptions` DISABLE KEYS */;
/*!40000 ALTER TABLE `Prescriptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Product_Images`
--

DROP TABLE IF EXISTS `Product_Images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Product_Images` (
  `image_id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `image_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `alt_text` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sort_order` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`image_id`),
  KEY `FK7daronmldn8c1tpmkgaqtcnjn` (`product_id`),
  CONSTRAINT `product_images_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `Products` (`product_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Lưu nhiều ảnh cho một sản phẩm';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Product_Images`
--

LOCK TABLES `Product_Images` WRITE;
/*!40000 ALTER TABLE `Product_Images` DISABLE KEYS */;
/*!40000 ALTER TABLE `Product_Images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Products`
--

DROP TABLE IF EXISTS `Products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Products` (
  `product_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sku` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ingredients` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usage_instructions` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contraindications` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `side_effects` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `storage_conditions` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Điều kiện bảo quản',
  `packaging` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `unit` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `image_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `category_id` int DEFAULT NULL,
  `brand_id` int DEFAULT NULL,
  `manufacturer_id` int DEFAULT NULL,
  `is_prescription_required` tinyint(1) DEFAULT '0' COMMENT 'Yêu cầu đơn thuốc?',
  `status` enum('ACTIVE','DISCONTINUED','INACTIVE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `slug` varchar(220) COLLATE utf8mb4_unicode_ci NOT NULL,
  `average_rating` decimal(3,2) DEFAULT '0.00',
  `review_count` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `slug` (`slug`),
  UNIQUE KEY `UKostq1ec3toafnjok09y9l7dox` (`slug`),
  UNIQUE KEY `UKsaappvwaebaevh709kd131sq8` (`slug`),
  UNIQUE KEY `sku` (`sku`),
  UNIQUE KEY `UKnekfpgjr4j5poacavu7amam7a` (`sku`),
  KEY `idx_product_name` (`name`),
  KEY `idx_product_slug` (`slug`),
  KEY `FKmd8vckrmi2gmivcqmoou09itp` (`brand_id`),
  KEY `FK7mwlviymhj4bl4mvr24ixj8dh` (`category_id`),
  KEY `FKcwdm61b3rtci4ckeokqf2hbwq` (`manufacturer_id`),
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `Categories` (`category_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `products_ibfk_2` FOREIGN KEY (`brand_id`) REFERENCES `Brands` (`brand_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `products_ibfk_3` FOREIGN KEY (`manufacturer_id`) REFERENCES `Manufacturers` (`manufacturer_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Thông tin chung về sản phẩm';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Products`
--

LOCK TABLES `Products` WRITE;
/*!40000 ALTER TABLE `Products` DISABLE KEYS */;
INSERT INTO `Products` VALUES (5,'Losartan 50mg','LOS-50-28TAB','Thuốc ức chế thụ thể angiotensin II, điều trị tăng huyết áp và bảo vệ thận ở bệnh nhân đái tháo đường','Losartan potassium 50mg','1 viên/ngày, uống vào buổi sáng. Có thể điều chỉnh liều theo chỉ định bác sĩ','Quá mẫn với Losartan, phụ nữ có thai','Chóng mặt, tăng kali máu, ho khan','Nhiệt độ phòng, tránh ánh sáng','Hộp 28 viên nén','viên','https://example.com/images/losartan.jpg',6,5,3,1,'DISCONTINUED','losartan-50mg',0.00,0,'2025-04-17 17:16:41','2025-04-17 17:16:41',1),(6,'Povidone Iodine 10%','PVI-10-500ML','Dung dịch sát khuẩn ngoài da, vết thương hở và chuẩn bị phẫu thuật','Povidone Iodine 10% w/v','Thấm dung dịch vào gạc vô trùng, lau nhẹ lên vùng da cần sát khuẩn. Không dùng cho vết thương sâu','Dị ứng với iod, bệnh tuyến giáp, phụ nữ có thai','Kích ứng da nhẹ, nhuộm màu da tạm thời','Đậy kín nắp, nhiệt độ dưới 25°C','Chai 500ml','chai','https://example.com/images/povidone-iodine.jpg',5,6,6,0,'ACTIVE','povidone-iodine-10',0.00,0,'2025-04-17 17:18:02','2025-04-17 17:18:02',1),(7,'Paracetamol 500mg','PAR-500-10V','Thuốc giảm đau, hạ sốt','Paracetamol 500mg','Uống 1-2 viên mỗi 4-6 giờ khi cần, không quá 8 viên/ngày','Quá mẫn với paracetamol, suy gan nặng','Hiếm khi: buồn nôn, phát ban','Nơi khô ráo, nhiệt độ dưới 30°C','Vỉ 10 viên','vỉ','https://example.com/images/paracetamol.jpg',4,2,3,0,'ACTIVE','paracetamol-500mg',0.00,0,'2025-04-25 18:49:48','2025-04-25 18:49:48',1),(8,'Amoxicillin 500mg','AMX-500-12V','Kháng sinh điều trị nhiễm khuẩn','Amoxicillin trihydrate tương đương Amoxicillin 500mg','1 viên x 3 lần/ngày, uống cách nhau 8 giờ','Dị ứng penicillin, suy gan nặng','Tiêu chảy, phát ban, buồn nôn','Nhiệt độ dưới 25°C, tránh ẩm','Vỉ 12 viên','vỉ','https://example.com/images/amoxicillin.jpg',5,NULL,5,1,'ACTIVE','amoxicillin-500mg',0.00,0,'2025-04-25 18:50:00','2025-04-25 18:50:00',1),(9,'Vitamin C 1000mg','VITC-1000-60V','Bổ sung vitamin C tăng sức đề kháng','Ascorbic acid 1000mg','1 viên/ngày, uống sau ăn','Quá mẫn với thành phần','Tiêu chảy khi dùng liều cao','Nơi khô ráo, tránh ánh sáng','Lọ 60 viên','lọ','https://example.com/images/vitamin-c.jpg',6,1,2,0,'ACTIVE','vitamin-c-1000mg',0.00,0,'2025-04-25 18:50:11','2025-04-25 18:50:11',1),(10,'Omeprazole 20mg','OMP-20-14V','Thuốc điều trị viêm loét dạ dày','Omeprazole 20mg','1 viên/ngày trước bữa ăn sáng','Quá mẫn với omeprazole','Đau đầu, chóng mặt, rối loạn tiêu hóa','Nhiệt độ dưới 25°C','Vỉ 14 viên','vỉ','https://example.com/images/omeprazole.jpg',4,3,4,1,'ACTIVE','omeprazole-20mg',0.00,0,'2025-04-25 18:50:30','2025-04-25 18:50:30',1),(11,'Dizapram 10mg','DIZ-10-30V','Thuốc chống nôn, chống say tàu xe','Dimenhydrinate 10mg','Uống 1-2 viên trước khi đi tàu xe 30 phút, có thể lặp lại sau 4-6 giờ nếu cần','Trẻ em dưới 2 tuổi, bệnh nhân glaucoma góc đóng','Buồn ngủ, khô miệng','Nhiệt độ phòng, tránh ẩm','Vỉ 30 viên','vỉ','https://example.com/images/dizapram.jpg',6,7,8,0,'ACTIVE','dizapram-10mg',0.00,0,'2025-04-25 18:52:08','2025-04-25 18:52:08',1),(12,'Băng dán vết thương loại lớn','BDV-10x6CM-10C','Băng dán vô trùng cho vết thương nhỏ','Vải không dệt, keo y tế','Làm sạch vết thương trước khi dán, thay băng hàng ngày','Dị ứng với thành phần keo dính','Kích ứng da tại chỗ','Nơi khô ráo','Hộp 10 cái','hộp','https://example.com/images/bandaid.jpg',4,9,10,0,'ACTIVE','bang-dan-vet-thuong-loai-lon',0.00,0,'2025-04-25 18:52:24','2025-04-25 18:52:24',1),(13,'Atorvastatin 20mg','ATO-20-30V','Thuốc hạ mỡ máu nhóm statin','Atorvastatin calcium 20mg','1 viên/ngày vào buổi tối','Bệnh gan hoạt động, phụ nữ có thai','Đau cơ, tăng men gan','Nhiệt độ dưới 30°C','Vỉ 30 viên','vỉ','https://example.com/images/atorvastatin.jpg',5,11,6,1,'ACTIVE','atorvastatin-20mg',0.00,0,'2025-04-25 18:52:46','2025-04-25 18:52:46',1),(14,'Dung dịch rửa mắt','DRM-15ML','Dung dịch rửa trôi dị vật, làm dịu mắt','Nước tinh khiết vô trùng, natri clorid 0.9%','Nhỏ 2-3 giọt vào mắt khi cần, có thể lặp lại nhiều lần','Dị ứng với thành phần','Cảm giác rát nhẹ thoáng qua','Nhiệt độ phòng, tránh ánh sáng','Lọ 15ml','lọ','https://example.com/images/eye-wash.jpg',4,7,10,0,'ACTIVE','dung-dich-rua-mat',0.00,0,'2025-04-25 18:53:06','2025-04-25 18:53:06',1),(15,'Metformin 500mg','MET-500-30V','Thuốc điều trị đái tháo đường type 2','Metformin hydrochloride 500mg','Uống 1-2 viên/ngày sau ăn, theo chỉ định bác sĩ','Suy thận nặng, nhiễm toan ceton','Rối loạn tiêu hóa, chán ăn','Nhiệt độ dưới 25°C','Vỉ 30 viên','vỉ','https://example.com/images/metformin.jpg',4,2,2,1,'ACTIVE','metformin-500mg',0.00,0,'2025-04-25 18:53:27','2025-04-25 18:53:27',1),(16,'Kem bôi da chứa Corticoid','KBC-0.1-15G','Kem bôi ngoài da điều trị viêm da, chàm','Betamethasone dipropionate 0.1%','Bôi lớp mỏng 1-2 lần/ngày lên vùng da tổn thương','Nhiễm trùng da, trẻ em dưới 2 tuổi','Teo da, rạn da khi dùng kéo dài','Nhiệt độ dưới 25°C','Tuýp 15g','tuýp','https://example.com/images/corticoid-cream.jpg',5,6,6,1,'ACTIVE','kem-boi-da-chua-corticoid',0.00,0,'2025-04-25 18:53:45','2025-04-25 18:53:45',1);
/*!40000 ALTER TABLE `Products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Promotion_Branches`
--

DROP TABLE IF EXISTS `Promotion_Branches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Promotion_Branches` (
  `promotion_id` int NOT NULL,
  `branch_id` int NOT NULL,
  PRIMARY KEY (`promotion_id`,`branch_id`),
  KEY `FKo8uuahbia3vdlo33n41tf57ex` (`branch_id`),
  CONSTRAINT `promotion_branches_ibfk_1` FOREIGN KEY (`promotion_id`) REFERENCES `Promotions` (`promotion_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `promotion_branches_ibfk_2` FOREIGN KEY (`branch_id`) REFERENCES `Branches` (`branch_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Link KM với chi nhánh áp dụng';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Promotion_Branches`
--

LOCK TABLES `Promotion_Branches` WRITE;
/*!40000 ALTER TABLE `Promotion_Branches` DISABLE KEYS */;
/*!40000 ALTER TABLE `Promotion_Branches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Promotion_Categories`
--

DROP TABLE IF EXISTS `Promotion_Categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Promotion_Categories` (
  `promotion_id` int NOT NULL,
  `category_id` int NOT NULL,
  PRIMARY KEY (`promotion_id`,`category_id`),
  KEY `FK3s44p8d45edhbxv58xggux5e2` (`category_id`),
  CONSTRAINT `promotion_categories_ibfk_1` FOREIGN KEY (`promotion_id`) REFERENCES `Promotions` (`promotion_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `promotion_categories_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `Categories` (`category_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Link KM với danh mục áp dụng';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Promotion_Categories`
--

LOCK TABLES `Promotion_Categories` WRITE;
/*!40000 ALTER TABLE `Promotion_Categories` DISABLE KEYS */;
/*!40000 ALTER TABLE `Promotion_Categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Promotion_Products`
--

DROP TABLE IF EXISTS `Promotion_Products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Promotion_Products` (
  `promotion_id` int NOT NULL,
  `product_id` int NOT NULL,
  PRIMARY KEY (`promotion_id`,`product_id`),
  KEY `FKqdbq5fro3pqmlvor4jtoasapp` (`product_id`),
  CONSTRAINT `promotion_products_ibfk_1` FOREIGN KEY (`promotion_id`) REFERENCES `Promotions` (`promotion_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `promotion_products_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `Products` (`product_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Link KM với sản phẩm áp dụng';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Promotion_Products`
--

LOCK TABLES `Promotion_Products` WRITE;
/*!40000 ALTER TABLE `Promotion_Products` DISABLE KEYS */;
/*!40000 ALTER TABLE `Promotion_Products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Promotion_Usage`
--

DROP TABLE IF EXISTS `Promotion_Usage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Promotion_Usage` (
  `usage_id` bigint NOT NULL AUTO_INCREMENT,
  `promotion_id` int NOT NULL,
  `order_id` int NOT NULL,
  `user_id` int NOT NULL,
  `usage_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `discount_applied` decimal(38,2) NOT NULL,
  PRIMARY KEY (`usage_id`),
  KEY `idx_promo_usage_user` (`user_id`,`promotion_id`),
  KEY `FK618cc6iym5v892hb3798x7gm5` (`order_id`),
  KEY `FKb98q84su6375xoibhe2ka4usl` (`promotion_id`),
  CONSTRAINT `promotion_usage_ibfk_1` FOREIGN KEY (`promotion_id`) REFERENCES `Promotions` (`promotion_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `promotion_usage_ibfk_2` FOREIGN KEY (`order_id`) REFERENCES `Orders` (`order_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `promotion_usage_ibfk_3` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Theo dõi việc áp dụng KM vào đơn hàng';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Promotion_Usage`
--

LOCK TABLES `Promotion_Usage` WRITE;
/*!40000 ALTER TABLE `Promotion_Usage` DISABLE KEYS */;
INSERT INTO `Promotion_Usage` VALUES (1,4,12,3,'2025-05-18 04:12:39',112500.00);
/*!40000 ALTER TABLE `Promotion_Usage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Promotions`
--

DROP TABLE IF EXISTS `Promotions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Promotions` (
  `promotion_id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `discount_value` decimal(38,2) NOT NULL,
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `min_order_value` decimal(38,2) DEFAULT NULL,
  `usage_limit_per_customer` int DEFAULT NULL COMMENT 'Giới hạn dùng/khách',
  `total_usage_limit` int DEFAULT NULL COMMENT 'Tổng lượt dùng tối đa',
  `total_used_count` int DEFAULT '0' COMMENT 'Số lượt đã dùng',
  `applicable_scope` enum('ALL','SPECIFIC_CATEGORIES','SPECIFIC_PRODUCTS','SPECIFIC_BRANCHES') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `discount_type` enum('PERCENTAGE','FIXED_AMOUNT') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`promotion_id`),
  UNIQUE KEY `code` (`code`),
  UNIQUE KEY `UKjdho73ymbyu46p2hh562dk4kk` (`code`),
  UNIQUE KEY `UK3wxh570vw4ueyqacwo6fhxd9n` (`code`),
  KEY `idx_promo_code` (`code`),
  KEY `idx_promo_dates` (`start_date`,`end_date`),
  KEY `idx_promo_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Thông tin các chương trình khuyến mãi';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Promotions`
--

LOCK TABLES `Promotions` WRITE;
/*!40000 ALTER TABLE `Promotions` DISABLE KEYS */;
INSERT INTO `Promotions` VALUES (1,'SUMMER25','Summer Sale 25%','Get 25% off on all summer collection items.',25.00,'2024-06-30 17:00:00','2024-07-31 16:59:59',50.00,1,100,0,'ALL',1,'2025-05-06 16:55:15','2025-05-07 00:30:59','PERCENTAGE'),(2,'SUMMER10','Summer Sale 10%','Get 10% off on all summer collection items.',10.00,'2024-06-30 17:00:00','2024-07-31 16:59:59',50.00,1,100,0,'ALL',0,'2025-05-06 17:08:36','2025-05-06 17:37:33','PERCENTAGE'),(3,'SUMMER30','Summer Sale 30% Off','Get 30% off on all summer collection items. Limited time!',30.00,'2024-06-30 17:00:00','2024-07-15 16:59:59',40.00,1,150,0,'SPECIFIC_CATEGORIES',1,'2025-05-06 17:22:19','2025-05-06 17:36:51','PERCENTAGE'),(4,'SUMMER10_2','Summer Sale 10%','Get 10% off on all summer collection items.',10.00,'2025-04-30 17:00:00','2025-05-31 16:59:59',50.00,1,100,2,'ALL',1,'2025-05-06 19:12:29','2025-05-06 19:12:29','PERCENTAGE');
/*!40000 ALTER TABLE `Promotions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Reviews`
--

DROP TABLE IF EXISTS `Reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Reviews` (
  `review_id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `user_id` int NOT NULL,
  `order_item_id` int DEFAULT NULL COMMENT 'Liên kết tới item đã mua để xác thực',
  `branch_id` int DEFAULT NULL COMMENT 'Đánh giá có thể liên quan tới chi nhánh',
  `rating` tinyint NOT NULL,
  `comment` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `review_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('pending_approval','approved','rejected') COLLATE utf8mb4_unicode_ci DEFAULT 'pending_approval',
  `approved_by_user_id` int DEFAULT NULL COMMENT 'FK tới Users (Admin/Manager)',
  PRIMARY KEY (`review_id`),
  UNIQUE KEY `order_item_id` (`order_item_id`),
  UNIQUE KEY `UK96f6ovfc9wn4579incehx4gra` (`order_item_id`),
  UNIQUE KEY `UKqpfqqudm4ibjiv69w0gfiupq` (`order_item_id`),
  KEY `idx_review_product` (`product_id`),
  KEY `idx_review_user` (`user_id`),
  KEY `idx_review_status` (`status`),
  KEY `FKnvet38l6p7o0gnnv3bnirmdyy` (`approved_by_user_id`),
  KEY `FK56w7k90nvpd5s1cs92ryrktjv` (`branch_id`),
  CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `Products` (`product_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `reviews_ibfk_3` FOREIGN KEY (`order_item_id`) REFERENCES `Order_Items` (`order_item_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `reviews_ibfk_4` FOREIGN KEY (`branch_id`) REFERENCES `Branches` (`branch_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `reviews_ibfk_5` FOREIGN KEY (`approved_by_user_id`) REFERENCES `Users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `reviews_chk_1` CHECK (((`rating` >= 1) and (`rating` <= 5)))
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Đánh giá của khách hàng về sản phẩm';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Reviews`
--

LOCK TABLES `Reviews` WRITE;
/*!40000 ALTER TABLE `Reviews` DISABLE KEYS */;
INSERT INTO `Reviews` VALUES (2,6,3,NULL,NULL,5,'This product is amazing! Highly recommended.','2025-05-15 08:11:38','approved',NULL),(3,5,3,NULL,NULL,5,'This product is amazing! Highly recommended.','2025-05-15 08:34:52','approved',NULL),(4,8,3,NULL,NULL,5,'This product is amazing! Highly recommended.','2025-05-16 20:07:03','approved',NULL),(5,8,3,NULL,NULL,5,'This product is amazing! Highly recommended.','2025-05-16 20:07:32','approved',NULL);
/*!40000 ALTER TABLE `Reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Roles`
--

DROP TABLE IF EXISTS `Roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Roles` (
  `role_id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_name` (`role_name`),
  UNIQUE KEY `UK716hgxp60ym1lifrdgp67xt5k` (`role_name`),
  UNIQUE KEY `UKkq0q6fk0168lj68ffgakdjsat` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Lưu trữ các vai trò người dùng trong hệ thống';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Roles`
--

LOCK TABLES `Roles` WRITE;
/*!40000 ALTER TABLE `Roles` DISABLE KEYS */;
INSERT INTO `Roles` VALUES (1,'CUSTOMER','Khách hàng đăng kí tài khoản'),(2,'ADMIN','Quản trị hệ thống'),(3,'STAFF','Nhân viên chi nhánh');
/*!40000 ALTER TABLE `Roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Shipping_Methods`
--

DROP TABLE IF EXISTS `Shipping_Methods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Shipping_Methods` (
  `method_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `base_cost` decimal(38,2) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`method_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Danh mục các phương thức vận chuyển';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Shipping_Methods`
--

LOCK TABLES `Shipping_Methods` WRITE;
/*!40000 ALTER TABLE `Shipping_Methods` DISABLE KEYS */;
INSERT INTO `Shipping_Methods` VALUES (2,'Chuyển phát nhanh','Next day delivery',28000.00,1),(3,'Chuyển phát siêu tốc','',38.00,1);
/*!40000 ALTER TABLE `Shipping_Methods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Shopping_Cart_Items`
--

DROP TABLE IF EXISTS `Shopping_Cart_Items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Shopping_Cart_Items` (
  `cart_item_id` int NOT NULL AUTO_INCREMENT,
  `cart_id` int NOT NULL,
  `inventory_id` int NOT NULL COMMENT 'Liên kết tới sản phẩm cụ thể trong kho chi nhánh',
  `quantity` int NOT NULL DEFAULT '1',
  `added_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`cart_item_id`),
  UNIQUE KEY `uk_cart_inventory` (`cart_id`,`inventory_id`),
  KEY `FKc37vqfna4vuqcvpgm6wb7warf` (`inventory_id`),
  CONSTRAINT `shopping_cart_items_ibfk_1` FOREIGN KEY (`cart_id`) REFERENCES `Shopping_Carts` (`cart_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `shopping_cart_items_ibfk_2` FOREIGN KEY (`inventory_id`) REFERENCES `Branch_Inventory` (`inventory_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Các sản phẩm trong giỏ hàng';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Shopping_Cart_Items`
--

LOCK TABLES `Shopping_Cart_Items` WRITE;
/*!40000 ALTER TABLE `Shopping_Cart_Items` DISABLE KEYS */;
INSERT INTO `Shopping_Cart_Items` VALUES (29,25,1,1,'2025-05-18 02:03:18'),(30,25,17,1,'2025-05-18 02:03:44'),(31,25,18,1,'2025-05-18 02:03:48'),(32,25,19,6,'2025-05-18 02:05:38');
/*!40000 ALTER TABLE `Shopping_Cart_Items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Shopping_Carts`
--

DROP TABLE IF EXISTS `Shopping_Carts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Shopping_Carts` (
  `cart_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT 'Mỗi user chỉ có 1 giỏ hàng active',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`cart_id`),
  UNIQUE KEY `UK6yxvquwoom284riggdkg2p300` (`user_id`),
  CONSTRAINT `shopping_carts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Lưu thông tin giỏ hàng của người dùng';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Shopping_Carts`
--

LOCK TABLES `Shopping_Carts` WRITE;
/*!40000 ALTER TABLE `Shopping_Carts` DISABLE KEYS */;
INSERT INTO `Shopping_Carts` VALUES (25,3,'2025-05-18 02:03:18','2025-05-18 02:05:38');
/*!40000 ALTER TABLE `Shopping_Carts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Staff`
--

DROP TABLE IF EXISTS `Staff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Staff` (
  `staff_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL COMMENT 'Liên kết tài khoản đăng nhập (nếu có)',
  `branch_id` int NOT NULL COMMENT 'Chi nhánh chính',
  `full_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `specialty` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `workplace_info` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Thông tin nơi làm việc khác',
  `profile_image_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_available_for_consultation` tinyint(1) DEFAULT '1',
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`staff_id`),
  UNIQUE KEY `user_id` (`user_id`),
  UNIQUE KEY `UK7qatq4kob2sr6rlp44khhj53g` (`user_id`),
  UNIQUE KEY `UK8mnih4j03n460a5cf2duc4n0x` (`user_id`),
  KEY `FKk4gcbflecxfoy21588qy984c4` (`branch_id`),
  CONSTRAINT `staff_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `staff_ibfk_2` FOREIGN KEY (`branch_id`) REFERENCES `Branches` (`branch_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Thông tin nhân viên, bác sĩ, dược sĩ tại chi nhánh';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Staff`
--

LOCK TABLES `Staff` WRITE;
/*!40000 ALTER TABLE `Staff` DISABLE KEYS */;
INSERT INTO `Staff` VALUES (1,10,1,'Branch Manager One','Branch Manager','Management','Branch 1 Office',NULL,0,1,'2025-05-05 22:57:45','2025-05-05 22:57:45'),(2,11,2,'Branch Manager Two','Branch Manager','Management','Branch 2 Office',NULL,0,1,'2025-05-05 23:02:29','2025-05-05 23:02:29'),(3,12,3,'Branch Manager Three','Branch Manager','Management','Branch 3 Office',NULL,0,1,'2025-05-05 23:02:46','2025-05-05 23:02:46'),(4,13,4,'Branch Manager Four','Branch Manager','Management','Branch 4 Office',NULL,0,1,'2025-05-05 23:02:54','2025-05-05 23:02:54'),(5,14,5,'Branch Manager Five','Branch Manager','Management','Branch 5 Office',NULL,0,0,'2025-05-05 23:03:03','2025-05-05 23:17:04'),(6,15,6,'Branch Manager Six','Branch Manager','Management','Branch 6 Office',NULL,0,1,'2025-05-05 23:03:11','2025-05-05 23:03:11'),(7,16,7,'Branch Manager Seven','Branch Manager','Management','Branch 7 Office',NULL,0,1,'2025-05-05 23:03:17','2025-05-05 23:03:17'),(8,17,8,'Branch Manager Eight','Branch Manager','Management','Branch 8 Office',NULL,0,1,'2025-05-05 23:03:25','2025-05-05 23:03:25'),(9,18,9,'Branch Manager Nine','Senior Branch Manager','Operations & Management','Branch 9 Office',NULL,1,1,'2025-05-05 23:03:33','2025-05-05 23:13:03'),(10,9,1,'Đỗ Văn Hưng','Pharmacist','Clinical Pharmacy','Consultation Room',NULL,1,1,'2025-05-05 23:10:45','2025-05-05 23:10:45');
/*!40000 ALTER TABLE `Staff` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `User_Payment_Methods`
--

DROP TABLE IF EXISTS `User_Payment_Methods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `User_Payment_Methods` (
  `user_payment_method_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `payment_type_id` int NOT NULL,
  `provider` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `account_number` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `expiry_date` date DEFAULT NULL,
  `is_default` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_payment_method_id`),
  KEY `FKhb2j9mgwl7bygh3drerpxyj0y` (`payment_type_id`),
  KEY `FKnkt175vtyeui49wg2dnj4snn9` (`user_id`),
  CONSTRAINT `user_payment_methods_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_payment_methods_ibfk_2` FOREIGN KEY (`payment_type_id`) REFERENCES `Payment_Types` (`payment_type_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Lưu các phương thức thanh toán người dùng đã thêm';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `User_Payment_Methods`
--

LOCK TABLES `User_Payment_Methods` WRITE;
/*!40000 ALTER TABLE `User_Payment_Methods` DISABLE KEYS */;
/*!40000 ALTER TABLE `User_Payment_Methods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `User_Roles`
--

DROP TABLE IF EXISTS `User_Roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `User_Roles` (
  `user_id` int NOT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FKtig6oxhv6no1ysdjwrnusajg6` (`role_id`),
  CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `Roles` (`role_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Liên kết Người dùng và Vai trò (Many-to-Many)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `User_Roles`
--

LOCK TABLES `User_Roles` WRITE;
/*!40000 ALTER TABLE `User_Roles` DISABLE KEYS */;
INSERT INTO `User_Roles` VALUES (1,1),(2,1),(3,1),(4,1),(5,1),(6,1),(7,1),(9,1),(8,2),(10,3),(11,3),(12,3),(13,3),(14,3),(15,3),(16,3),(17,3),(18,3);
/*!40000 ALTER TABLE `User_Roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Users`
--

DROP TABLE IF EXISTS `Users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `phone_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Mật khẩu đã mã hóa',
  `full_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT '1',
  `last_login` timestamp NULL DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `gender` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `phone_number` (`phone_number`),
  UNIQUE KEY `UK9q63snka3mdh91as4io72espi` (`phone_number`),
  UNIQUE KEY `UK1jtfloh2wr1clw54p61pl4qho` (`phone_number`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKncoa9bfasrql0x4nhmh1plxxy` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Tài khoản người dùng hệ thống';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Users`
--

LOCK TABLES `Users` WRITE;
/*!40000 ALTER TABLE `Users` DISABLE KEYS */;
INSERT INTO `Users` VALUES (1,'0362522421','$2a$10$W..XDUFknq457.9GXxmap.75X1pLhVW9RLVcFIH6Ur/Cv2wnJVQtW','Cao Thi Thu','thuthi732@gmail.com','2025-04-14 21:23:25','2025-04-14 21:23:25',1,'2025-05-06 18:19:34',NULL,NULL,NULL),(2,'0352446599','$2a$10$IdV37DVxKAvrDzF7eKH7kOa1L5Qql5x//w4qMUhuJ2it7t/Uehip2','Do Van Tu','0l3vantuu7l0@gmail.com','2025-04-14 21:23:58','2025-04-19 02:00:35',1,'2025-04-18 18:58:16',NULL,NULL,NULL),(3,'0987351477','$2a$10$t1MT2zPj6sf/I6Zs.PAbV.VkR2CC0d9YnHK87mBkm6KGUecf6he8S','Do Van Thanh','vanthanh732@gmail.com','2025-04-14 21:24:14','2025-05-05 08:51:16',1,'2025-05-18 03:03:48',NULL,NULL,'đường NK2, khu phố 3A, phường Thới Hoà, thị xã Bến Cát, tỉnh Bình Dương'),(4,'0989771499','$2a$10$GJIRYAv0l2JLZ/6TUbdD/e1KzL9OrC7l8iIO.r1Lp8BXWNao1Rx7y','Hoang Thi Tham','thamhoang732@gmail.com','2025-04-14 21:24:46','2025-05-06 05:45:43',1,'2025-04-22 06:47:26',NULL,NULL,'đường NK2, khu phố 3A, phường Thới Hoà, thị xã Bến Cát, tỉnh Bình Dương'),(5,'0345001645','$2a$10$01l/M4vXIT2whjjPb.JkI..aDD2ci8THiLXbTYTZLerGRHLyYxzV6','Hang Gia Thinh','thinhgia@gmail.com','2025-04-16 23:20:18','2025-04-16 23:20:18',1,NULL,NULL,NULL,NULL),(6,'0345004355','$2a$10$kULDmlQZdBsuAf4WoIO3TuZhPJcCNx2p9vQmoD83E.aaCuzHnHPLW','Nguyen Hoang Trung','trung324312@gmail.com','2025-04-16 23:33:32','2025-05-12 11:21:16',0,'2025-04-22 06:45:34',NULL,NULL,NULL),(7,'0933345345','$2a$10$9shiW/JLnI4VF4FEzYBF/uXZ/s5weqT6uOM0nPJ9BlZ6oOEG6vTRq','Do Ra DO','dorado732006@gmail.com','2025-04-18 19:20:02','2025-04-22 08:55:45',1,'2025-04-22 08:55:56',NULL,NULL,NULL),(8,'0987654321','$2a$10$aquUhUfBGo4.TLpgKgOEqeRBji54kB22fIn5v3/cjBjFvK8M/3ciG','Đỗ Văn Tú','0l3vantu7l0@gmail.com','2025-04-18 20:02:20','2025-05-12 06:40:06',1,'2025-05-18 04:14:34','2004-03-07','male','đường NK2, khu phố 3B, phường Thới Hoà, thị xã Bến Cát, tỉnh Bình Dương'),(9,'0987654322','$2a$10$2LcfxQhrnQiI1wdTcLB8g.BiXGPctArEBIXtmQw7Aq/P9JIh4nBc6','Đỗ Văn Hưng','hungvan2354@gmail.com','2025-04-22 06:55:55','2025-04-22 06:55:55',1,'2025-04-22 08:03:21',NULL,NULL,NULL),(10,'0912345678','$2a$10$MhHkjPATIFHl9A0cUR.sR.54UIoW6IMMTj87rWhazN9PXx5xkIgWS','Branch Manager One','manager.one@pharmacy.com','2025-05-05 22:57:44','2025-05-05 22:57:44',1,NULL,NULL,NULL,NULL),(11,'0912345679','$2a$10$RLF22usesVNUV53Y822ofeDObJm3EhJaYCXfrg9KyIKFT8chO/49O','Branch Manager Two','manager.two@pharmacy.com','2025-05-05 23:02:29','2025-05-05 23:02:29',1,NULL,NULL,NULL,NULL),(12,'0912345680','$2a$10$LbrO60qq9PGbslQoiry7Ve7TA/iiHectlGPv7.FMgNyRVQyny1xya','Branch Manager Three','manager.three@pharmacy.com','2025-05-05 23:02:46','2025-05-05 23:02:46',1,NULL,NULL,NULL,NULL),(13,'0912345681','$2a$10$XQ6iENNLL1sbqpHw7vTu4ev8F5eMCVjTfO5iZ2EawDGe6RXxCysxS','Branch Manager Four','manager.four@pharmacy.com','2025-05-05 23:02:54','2025-05-05 23:02:54',1,NULL,NULL,NULL,NULL),(14,'0912345682','$2a$10$O3IE4UZ5ayXkMdPymvENOOXanC3KLeqdbsqv2A2v6vZqsuBZanymK','Branch Manager Five','manager.five@pharmacy.com','2025-05-05 23:03:03','2025-05-05 23:03:03',1,NULL,NULL,NULL,NULL),(15,'0912345683','$2a$10$J2vuiaWZWplP4N6YLQUwTey7gZMerRq0BWd2I86a1nfcPv98eiZPC','Branch Manager Six','manager.six@pharmacy.com','2025-05-05 23:03:11','2025-05-05 23:03:11',1,NULL,NULL,NULL,NULL),(16,'0912345684','$2a$10$w0Hfg8NvzKA3Y6/cixIwteEcDntB2zOmvIG9krK/ARqaos8.qsHuG','Branch Manager Seven','manager.seven@pharmacy.com','2025-05-05 23:03:17','2025-05-05 23:03:17',1,NULL,NULL,NULL,NULL),(17,'0912345685','$2a$10$7i1gZgjEzRdGxmufnT63sOIx.u34kurul0fWveeIfoUFvNT3rVbB2','Branch Manager Eight','manager.eight@pharmacy.com','2025-05-05 23:03:25','2025-05-05 23:03:25',1,NULL,NULL,NULL,NULL),(18,'0912345686','$2a$10$t9TqPu0hdLrwEDhJ7BP5c.pD1Lg9zqKquRubfUB8flPVXGEDdwmz2','Branch Manager Nine','manager.nine@pharmacy.com','2025-05-05 23:03:33','2025-05-05 23:03:33',1,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `Users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-18 18:19:10
