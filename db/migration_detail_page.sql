-- =====================================================
-- 商品详情页升级 迁移脚本
-- 适用：已存在 campus_market 库的环境，无需重建库表
-- 执行：mysql -uroot -p campus_market < migration_detail_page.sql
-- =====================================================
USE campus_market;

-- 1. 商品表新增 view_count 列
ALTER TABLE product
  ADD COLUMN view_count INT NOT NULL DEFAULT 0 COMMENT '浏览量' AFTER status;

-- 2. 站内消息表 order_id 改为可空（用于私信卖家场景）
ALTER TABLE message
  MODIFY COLUMN order_id INT DEFAULT NULL COMMENT '关联订单ID（可空，私信时为空）';

-- 3. 新建商品收藏表
CREATE TABLE IF NOT EXISTS favorite (
  id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  user_id INT NOT NULL COMMENT '收藏用户ID',
  product_id INT NOT NULL COMMENT '商品ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  UNIQUE KEY uk_user_product (user_id, product_id),
  FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品收藏表';
