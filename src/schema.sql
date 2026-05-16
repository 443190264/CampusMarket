-- 校园二手物品交易平台数据库 Schema
-- 含默认管理员账号

-- 创建数据库
CREATE DATABASE IF NOT EXISTS market_db;
USE market_db;

-- 删除已有表（顺序重要：先删子表，再删父表）
DROP TABLE IF EXISTS transaction;
DROP TABLE IF EXISTS browse_history;
DROP TABLE IF EXISTS favorite;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS operation_log;


-- 1. 学生表
CREATE TABLE student (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '学生ID，自增主键',
    student_id VARCHAR(50) UNIQUE NOT NULL COMMENT '学号，唯一',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(15) COMMENT '电话',
    balance DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '账户余额',
    password VARCHAR(100) NOT NULL COMMENT '密码哈希值（SHA-256）',
    salt VARCHAR(100) NOT NULL COMMENT '随机盐（Base64编码）',
    is_admin TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否管理员：0=普通学生，1=管理员',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';


-- 2. 商品表
CREATE TABLE product (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    seller_id INT NOT NULL COMMENT '发布者ID，关联student.id',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    description TEXT COMMENT '描述',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    category VARCHAR(30) COMMENT '分类',
    status ENUM('ON_SALE', 'OFF_SALE', 'SOLD') NOT NULL DEFAULT 'ON_SALE' COMMENT '状态',
    publish_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    FOREIGN KEY (seller_id) REFERENCES student(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';


-- 3. 浏览历史表
CREATE TABLE browse_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    product_id INT NOT NULL,
    browse_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_student_product (student_id, product_id),
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浏览历史表';


-- 4. 收藏表
CREATE TABLE favorite (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    product_id INT NOT NULL,
    fav_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_student_product (student_id, product_id),
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';


-- 5. 交易记录表
CREATE TABLE transaction (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    buyer_id INT NOT NULL,
    seller_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status ENUM('COMPLETED', 'RETURNED') NOT NULL DEFAULT 'COMPLETED',
    trade_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    FOREIGN KEY (buyer_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (seller_id) REFERENCES student(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易记录表';


-- 6. 操作日志表
CREATE TABLE operation_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    operator_id INT COMMENT '操作学生ID',
    action VARCHAR(100) NOT NULL,
    detail TEXT,
    log_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';


-- 索引（提升查询性能）
CREATE INDEX idx_product_status ON product(status);
CREATE INDEX idx_product_category ON product(category);
CREATE INDEX idx_product_publish_time ON product(publish_time);
CREATE INDEX idx_transaction_buyer ON transaction(buyer_id);
CREATE INDEX idx_transaction_seller ON transaction(seller_id);
CREATE INDEX idx_transaction_trade_time ON transaction(trade_time);
CREATE INDEX idx_browse_time ON browse_history(browse_time);
CREATE INDEX idx_fav_time ON favorite(fav_time);
CREATE INDEX idx_log_operator ON operation_log(operator_id);
CREATE INDEX idx_log_time ON operation_log(log_time);


