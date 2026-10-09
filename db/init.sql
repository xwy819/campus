-- =====================================================
-- 校园二手交易平台 数据库初始化脚本
-- 库名：campus_market  字符集：utf8mb4  引擎：InnoDB
-- =====================================================

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS campus_market DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
USE campus_market;

-- 2. 管理员表
CREATE TABLE admin (
  id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  username VARCHAR(50) NOT NULL COMMENT '用户名',
  password VARCHAR(100) NOT NULL COMMENT '密码',
  name VARCHAR(50) NOT NULL COMMENT '姓名',
  phone VARCHAR(20) DEFAULT NULL COMMENT '电话',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 3. 用户表（学生/教师）
CREATE TABLE user (
  id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  phone VARCHAR(20) NOT NULL COMMENT '手机号',
  password VARCHAR(100) NOT NULL COMMENT '密码',
  nickname VARCHAR(50) NOT NULL COMMENT '昵称',
  avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  role TINYINT NOT NULL DEFAULT 0 COMMENT '角色:0学生1教师',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0冻结1正常',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 4. 商品分类表
CREATE TABLE category (
  id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  name VARCHAR(50) NOT NULL COMMENT '分类名称',
  sort INT DEFAULT 0 COMMENT '排序权重',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 5. 商品表
CREATE TABLE product (
  id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  user_id INT NOT NULL COMMENT '发布者ID',
  category_id INT DEFAULT NULL COMMENT '分类ID',
  title VARCHAR(100) NOT NULL COMMENT '商品标题',
  description TEXT COMMENT '商品描述',
  price DECIMAL(10,2) NOT NULL COMMENT '售价',
  condition_level TINYINT NOT NULL COMMENT '新旧程度1-4',
  image_url VARCHAR(255) DEFAULT NULL COMMENT '图片URL',
  status TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0待审1在售2已售3下架',
  view_count INT NOT NULL DEFAULT 0 COMMENT '浏览量',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_title_user (title, user_id),
  FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
  FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 6. 订单表
CREATE TABLE `order` (
  id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  product_id INT NOT NULL COMMENT '商品ID',
  buyer_id INT NOT NULL COMMENT '买家ID',
  seller_id INT NOT NULL COMMENT '卖家ID',
  status TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0待确认1已确认2完成3取消',
  final_price DECIMAL(10,2) NOT NULL COMMENT '成交价格',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
  FOREIGN KEY (buyer_id) REFERENCES user(id) ON DELETE CASCADE,
  FOREIGN KEY (seller_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 7. 站内消息表（order_id 可空，用于无订单的私信场景）
CREATE TABLE message (
  id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  order_id INT DEFAULT NULL COMMENT '关联订单ID（可空，私信时为空）',
  sender_id INT NOT NULL COMMENT '发送者ID',
  receiver_id INT NOT NULL COMMENT '接收者ID',
  content MEDIUMTEXT NOT NULL COMMENT '消息内容',
  is_read TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读:0未读1已读',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (order_id) REFERENCES `order`(id) ON DELETE CASCADE,
  FOREIGN KEY (sender_id) REFERENCES user(id) ON DELETE CASCADE,
  FOREIGN KEY (receiver_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内消息表';

-- 8. 商品收藏表
CREATE TABLE favorite (
  id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  user_id INT NOT NULL COMMENT '收藏用户ID',
  product_id INT NOT NULL COMMENT '商品ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  UNIQUE KEY uk_user_product (user_id, product_id),
  FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品收藏表';

-- 9. 公告表
CREATE TABLE notice (
  id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  admin_id INT NOT NULL COMMENT '发布管理员ID',
  title VARCHAR(100) NOT NULL COMMENT '公告标题',
  content TEXT COMMENT '公告正文',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (admin_id) REFERENCES admin(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 10. 操作日志表
CREATE TABLE operation_log (
  id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  user_id INT DEFAULT NULL COMMENT '操作人ID',
  operation VARCHAR(200) NOT NULL COMMENT '操作描述',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- =====================================================
-- 演示数据
-- =====================================================
INSERT INTO admin (username, password, name, phone) VALUES
('admin', '123456', '系统管理员', '13800000000');

INSERT INTO user (phone, password, nickname, role) VALUES
('13900000001', '123456', '张同学', 0),
('13900000002', '123456', '李老师', 1),
('13900000003', '123456', '王同学', 0),
('13900000004', '123456', '刘老师', 1);

INSERT INTO category (name, sort) VALUES
('教材', 1), ('电子产品', 2), ('生活用品', 3), ('其他', 4);

INSERT INTO product (user_id, category_id, title, description, price, condition_level, image_url, status) VALUES
(1, 1, '高等数学第七版', '九成新，无笔记', 15.00, 2, '/img/1.png', 1),
(2, 2, '罗技无线鼠标', '功能正常，外观微划痕', 35.00, 3, '/img/2.png', 1),
(1, 1, '大学英语综合教程', '全新未拆封', 25.00, 1, '/img/3.png', 0),
(3, 3, '宿舍小台灯', '可充电，柔光护眼', 12.00, 2, '/img/4.png', 1),
(2, 1, '线性代数教材', '有少量笔记', 10.00, 3, '/img/5.png', 1);

INSERT INTO `order` (product_id, buyer_id, seller_id, status, final_price) VALUES
(2, 1, 2, 1, 35.00),
(4, 2, 3, 2, 12.00);

INSERT INTO notice (admin_id, title, content) VALUES
(1, '平台交易须知', '请同学们诚信交易，当面验货后再付款。'),
(1, '二手教材专场', '开学季教材专场已上线，欢迎选购。');
