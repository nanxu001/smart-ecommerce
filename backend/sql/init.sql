-- ============================================
-- 智能电商推荐系统 - 数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS ecommerce_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ecommerce_db;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    avatar VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 用户画像标签表
CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    gender VARCHAR(10),
    age_range VARCHAR(20),
    budget_level VARCHAR(20),
    preferred_categories JSON,
    tags JSON,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 商品分类表
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    icon VARCHAR(200),
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 商品表
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2),
    stock INT DEFAULT 0,
    sales INT DEFAULT 0,
    views INT DEFAULT 0,
    images JSON,
    tags JSON,
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- 用户行为记录表
CREATE TABLE IF NOT EXISTS user_behaviors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    search_query VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- 购物车表
CREATE TABLE IF NOT EXISTS cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    UNIQUE KEY uk_user_product (user_id, product_id)
);

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_no VARCHAR(50) UNIQUE NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    address VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 订单项表
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200),
    price DECIMAL(10,2),
    quantity INT,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- 收藏表
CREATE TABLE IF NOT EXISTS favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_product (user_id, product_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- 推荐记录表
CREATE TABLE IF NOT EXISTS recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    query_text TEXT,
    product_ids JSON,
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ============================================
-- 模拟数据
-- ============================================

-- 用户 (密码: 123456 → MD5: e10adc3949ba59abbe56e057f20f883e)
INSERT IGNORE INTO users (id, username, password, nickname, email, phone) VALUES
(1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', 'admin@example.com', '13800000001'),
(2, 'zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三', 'zhangsan@example.com', '13800000002'),
(3, 'lisi', 'e10adc3949ba59abbe56e057f20f883e', '李四', 'lisi@example.com', '13800000003');

-- 用户画像
INSERT IGNORE INTO user_profiles (user_id, gender, age_range, budget_level, preferred_categories, tags) VALUES
(1, 'male', '25-35', 'high', '["electronics", "books"]', '["tech-enthusiast"]'),
(2, 'female', '18-25', 'medium', '["fashion", "beauty"]', '["gift-buyer", "trendy"]'),
(3, 'male', '35-45', 'high', '["electronics", "home"]', '["family-man"]');

-- 商品分类
INSERT IGNORE INTO categories (id, name, parent_id, icon, sort_order) VALUES
(1, '电子产品', 0, '💻', 1),
(2, '手机', 1, '📱', 1),
(3, '笔记本电脑', 1, '💻', 2),
(4, '服饰', 0, '👔', 2),
(5, '男装', 4, '👔', 1),
(6, '女装', 4, '👗', 2),
(7, '美妆', 0, '💄', 3),
(8, '家居', 0, '🏠', 4),
(9, '图书', 0, '📚', 5),
(10, '礼品', 0, '🎁', 6);

-- 商品数据
INSERT IGNORE INTO products (id, category_id, name, description, price, original_price, stock, sales, views, images, tags, status) VALUES
(1, 2, '小米14 Pro 5G手机', '骁龙8Gen3处理器，徕卡光学镜头，2K屏幕，120W快充', 4999.00, 5499.00, 500, 1200, 8500, '["https://picsum.photos/seed/phone1/400/400"]', '["electronics", "phone", "gift", "tech"]', 1),
(2, 2, 'iPhone 15 Pro', 'A17 Pro芯片，钛金属设计，48MP相机系统', 8999.00, 9999.00, 300, 2500, 15000, '["https://picsum.photos/seed/phone2/400/400"]', '["electronics", "phone", "gift", "premium"]', 1),
(3, 3, 'MacBook Air M3', 'Apple M3芯片，15.3英寸Liquid Retina显示屏，18小时续航', 10499.00, 11999.00, 200, 800, 6000, '["https://picsum.photos/seed/laptop1/400/400"]', '["electronics", "laptop", "gift", "tech"]', 1),
(4, 6, '法式连衣裙', '优雅法式风格，适合约会、聚会、日常穿搭', 299.00, 399.00, 1000, 600, 4500, '["https://picsum.photos/seed/dress1/400/400"]', '["fashion", "dress", "gift", "trendy"]', 1),
(5, 6, '设计师手包', '意大利进口皮革，精致手工，送女友首选', 599.00, 799.00, 500, 450, 3200, '["https://picsum.photos/seed/bag1/400/400"]', '["fashion", "bag", "gift", "premium"]', 1),
(6, 7, 'SK-II 神仙水', '经典护肤精华，改善肤质，送礼佳品', 1590.00, 1890.00, 800, 1500, 9000, '["https://picsum.photos/seed/skincare1/400/400"]', '["beauty", "skincare", "gift", "premium"]', 1),
(7, 7, 'YSL口红礼盒', '3支经典色号，丝绒质地，精美礼盒装', 899.00, 1099.00, 600, 900, 5500, '["https://picsum.photos/seed/lipstick1/400/400"]', '["beauty", "lipstick", "gift", "luxury"]', 1),
(8, 8, '智能扫地机器人', '激光导航，自动集尘，APP远程控制', 2499.00, 2999.00, 400, 700, 4800, '["https://picsum.photos/seed/robot1/400/400"]', '["home", "smart", "tech", "gift"]', 1),
(9, 8, '北欧实木餐桌', '白蜡木材质，简约设计，4人位', 3299.00, 3999.00, 100, 150, 2000, '["https://picsum.photos/seed/table1/400/400"]', '["home", "furniture", "premium"]', 1),
(10, 9, '深入理解Java虚拟机', 'Java开发者必读经典，第3版', 89.00, 109.00, 2000, 3000, 12000, '["https://picsum.photos/seed/book1/400/400"]', '["book", "java", "tech"]', 1),
(11, 9, '算法导论', '计算机科学经典教材，算法学习必备', 128.00, 158.00, 1500, 2000, 10000, '["https://picsum.photos/seed/book2/400/400"]', '["book", "algorithm", "tech"]', 1),
(12, 10, '生日礼物礼盒', '精选礼物组合，含贺卡、精美包装', 199.00, 299.00, 800, 500, 3500, '["https://picsum.photos/seed/gift1/400/400"]', '["gift", "birthday", "budget-friendly"]', 1),
(13, 1, '无线降噪耳机', '主动降噪，40小时续航，高音质', 1299.00, 1599.00, 600, 800, 5000, '["https://picsum.photos/seed/headphone1/400/400"]', '["electronics", "audio", "gift", "tech"]', 1),
(14, 1, '智能手表', '健康监测，GPS定位，14天续航', 999.00, 1299.00, 700, 600, 4000, '["https://picsum.photos/seed/watch1/400/400"]', '["electronics", "wearable", "gift", "tech"]', 1),
(15, 5, '商务休闲衬衫', '纯棉面料，修身版型，通勤必备', 199.00, 259.00, 1200, 400, 3000, '["https://picsum.photos/seed/shirt1/400/400"]', '["fashion", "menswear", "casual"]', 1),
(16, 6, '针织开衫', '柔软舒适，春秋百搭，温柔色系', 259.00, 329.00, 900, 350, 2800, '["https://picsum.photos/seed/cardigan1/400/400"]', '["fashion", "womenswear", "casual"]', 1),
(17, 2, '小米Redmi Note 13', '高性价比，1亿像素，5000mAh大电池', 1299.00, 1499.00, 1000, 2000, 11000, '["https://picsum.photos/seed/redmi1/400/400"]', '["electronics", "phone", "budget-friendly"]', 1),
(18, 7, '香水礼盒', '经典淡香水，持久留香，送女友推荐', 459.00, 599.00, 500, 700, 4200, '["https://picsum.photos/seed/perfume1/400/400"]', '["beauty", "perfume", "gift", "luxury"]', 1),
(19, 8, '空气净化器', '除甲醛，除PM2.5，智能联动', 1899.00, 2299.00, 300, 450, 3500, '["https://picsum.photos/seed/purifier1/400/400"]', '["home", "smart", "health"]', 1),
(20, 10, '情人节礼物套装', '巧克力+鲜花+贺卡，浪漫表白', 168.00, 228.00, 500, 800, 6000, '["https://picsum.photos/seed/valentine1/400/400"]', '["gift", "valentine", "romantic", "budget-friendly"]', 1),
(21, 3, '联想ThinkPad X1', '商务旗舰，碳纤维机身，14英寸', 12999.00, 14999.00, 150, 400, 3000, '["https://picsum.photos/seed/thinkpad1/400/400"]', '["electronics", "laptop", "business", "premium"]', 1),
(22, 5, '运动夹克', '防水透气，户外运动必备', 399.00, 499.00, 800, 200, 1500, '["https://picsum.photos/seed/jacket1/400/400"]', '["fashion", "menswear", "sports"]', 1),
(23, 1, '蓝牙音箱', 'JBL音质，防水便携，户外派对', 599.00, 799.00, 600, 300, 2500, '["https://picsum.photos/seed/speaker1/400/400"]', '["electronics", "audio", "outdoor"]', 1),
(24, 7, '面膜套装', '玻尿酸补水，一周焕肤，10片装', 129.00, 169.00, 2000, 1200, 8000, '["https://picsum.photos/seed/mask1/400/400"]', '["beauty", "skincare", "budget-friendly"]', 1),
(25, 9, 'Spring Boot实战', '从入门到精通，企业级开发指南', 79.00, 99.00, 1800, 1500, 7000, '["https://picsum.photos/seed/book3/400/400"]', '["book", "java", "spring"]', 1);

-- 模拟用户行为
INSERT IGNORE INTO user_behaviors (user_id, product_id, type, created_at) VALUES
-- 用户2（女性，喜欢美妆和服饰）的行为
(2, 6, 'purchase', '2026-04-20 10:00:00'),
(2, 7, 'purchase', '2026-04-21 14:00:00'),
(2, 4, 'favorite', '2026-04-22 09:00:00'),
(2, 5, 'cart', '2026-04-22 10:00:00'),
(2, 18, 'view', '2026-04-23 11:00:00'),
(2, 20, 'view', '2026-04-23 12:00:00'),
(2, 24, 'purchase', '2026-04-24 15:00:00'),
(2, 1, 'view', '2026-04-25 16:00:00'),
-- 用户3（男性，喜欢电子产品）的行为
(3, 1, 'purchase', '2026-04-20 10:00:00'),
(3, 3, 'purchase', '2026-04-21 11:00:00'),
(3, 8, 'view', '2026-04-22 14:00:00'),
(3, 10, 'view', '2026-04-22 15:00:00'),
(3, 13, 'cart', '2026-04-23 09:00:00'),
(3, 14, 'favorite', '2026-04-23 10:00:00'),
(3, 11, 'view', '2026-04-24 11:00:00'),
(3, 17, 'view', '2026-04-25 12:00:00');

-- 收藏
INSERT IGNORE INTO favorites (user_id, product_id) VALUES
(2, 6),
(2, 7),
(2, 4),
(2, 20),
(3, 1),
(3, 3),
(3, 13),
(3, 14);

-- 购物车
INSERT IGNORE INTO cart_items (user_id, product_id, quantity) VALUES
(2, 5, 1),
(2, 12, 2),
(3, 13, 1),
(3, 21, 1);

SELECT '✅ 数据库初始化完成，模拟数据已导入！' AS message;
