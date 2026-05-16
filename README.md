校园二手物品交易平台

一个基于 Java SE + MySQL + JDBC 的控制台应用程序，实现学生注册/登录、商品发布、多条件搜索、购买/退货、收藏、浏览历史、操作日志等完整的二手交易功能。数据持久化使用 MySQL 数据库。

功能特性

学生管理
- 注册新学生（学号唯一，密码加密存储）
- 登录（学号 + 密码）
- 修改个人信息（姓名、电话、密码）
- 充值余额
- 注销账号（级联删除所有关联数据）
- 管理员可重置任意学生密码

商品管理
- 发布商品（标题、描述、价格、分类）
- 查看我的商品（支持分页、上架/下架操作）
- 多条件搜索商品（标题模糊、分类、价格区间、状态）
- 排序（价格升序/降序、时间倒序）
- 分页查询（每页5条，支持翻页）
- 查看商品详情（自动记录浏览历史）

交易流程
- 购买商品（事务保证：扣款、加款、状态变更、生成交易记录）
- 退货（原路退回，更新交易状态）
- 查看买入记录（可退货）
- 查看卖出记录（仅查看）

收藏与浏览
- 收藏/取消收藏（禁止重复收藏）
- 查看收藏列表（支持查看详情、购买、取消收藏）
- 浏览历史（自动记录，去重保留最新）
- 浏览历史分页查看（每页10条）

操作日志
- 关键操作自动记录（注册、登录、发布、购买、退货、充值等）
- 日志不可篡改（只提供插入操作）
- 普通学生查看自己的日志（分页）
- 管理员查看所有日志（分页）或按学生ID筛选

安全特性
- 密码加密存储（SHA-256 + 随机盐）
- 防 SQL 注入（全部使用 PreparedStatement）
- 事务保证购买/退货数据一致性
- 外键级联删除保证数据完整性

技术栈
Java 17+ (核心开发语言)
MySQL 8.0 (数据库)
JDBC 原生 (数据库连接)
控制台 (用户交互界面)

项目结构
src/
└── com.campus.market
├── entity/          实体类
├── dao/             数据访问接口
│   └── impl/        DAO实现类
├── service/         业务逻辑接口
│   └── impl/        Service实现类
├── ui/              控制台界面
├── util/            工具类
└── exception/       自定义异常
├── schema.sql           数据库建表脚本
└── README.txt

数据库设计

学生表 (student)
- 主要字段：id, student_id, name, phone, balance, password, salt, is_admin, create_time

商品表 (product)
- 主要字段：id, seller_id, title, description, price, category, status, publish_time

浏览历史表 (browse_history)
- 主要字段：id, student_id, product_id, browse_time

收藏表 (favorite)
- 主要字段：id, student_id, product_id, fav_time

交易记录表 (transaction)
- 主要字段：id, product_id, buyer_id, seller_id, amount, status, trade_time

操作日志表 (operation_log)
- 主要字段：id, operator_id, action, detail, log_time

所有外键均设置了 ON DELETE CASCADE，删除学生时会自动删除其发布的商品、浏览历史、收藏、交易记录。

运行步骤

1. 创建数据库
   执行 schema.sql 脚本，创建数据库 market_db 及全部表。

2. 修改数据库配置
   修改 DBUtil.java 或外部 db.properties 文件中的数据库连接信息：
   db.url=jdbc:mysql://localhost:3306/market_db?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
   db.user=root
   db.password=你的MySQL密码
   注意：URL 中必须包含 characterEncoding=utf8，避免中文乱码。

3. 运行程序
   方式一：在 IDE 中运行 MainMenu.java
   方式二：打包成 JAR 运行
   java -Dfile.encoding=UTF-8 -jar CampusMarket.jar

管理员权限设置

系统没有预设默认管理员。首次使用需要手动创建管理员：
1. 运行程序，注册一个普通学生账号（例如学号 admin，密码 admin123）。
2. 登录 MySQL，执行以下 SQL（将 '你的学号' 替换为实际学号）：
   UPDATE student SET is_admin = 1 WHERE student_id = '你的学号';
3. 重新登录，该账号即拥有管理员权限（可查看所有操作日志、重置任意学生密码）。

核心优化

- 密码加密：SHA-256 + 随机盐，数据库不存储明文密码
- 分页查询：LIMIT + OFFSET，减少内存占用，提升响应速度
- JDBC 模板：JdbcTemplate 封装，消除重复代码，统一异常处理
- 统一异常处理：BusinessException / SystemException，友好提示 + 系统日志分离

注意事项

- 密码以哈希值存储，登录时验证哈希是否匹配。
- 删除学生时会级联删除其发布的所有商品及相关记录。
- 购买/退货操作使用事务保证数据一致性。
- 日志不可篡改（只提供插入操作，无更新删除）。
- 运行 JAR 时请确保 db.properties 与 JAR 在同一目录，且数据库服务已启动。
- 如遇中文乱码，请检查运行命令是否包含 -Dfile.encoding=UTF-8，以及数据库连接 URL 是否包含 characterEncoding=utf8。
