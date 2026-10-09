# 校园二手交易平台

校园二手商品交易平台，支持商品发布、浏览、收藏、下单、站内私信、地址管理、站内公告等功能，同时提供独立的后台管理系统。

## 技术栈

| 层级 | 技术 |
|------|------|
| 用户端前端 | Vue 3 + Vite + Vue Router |
| 管理后台前端 | Vue 3 + Element Plus |
| 后端 | Spring Boot + MyBatis + MySQL |
| 构建 | Maven (后端) / npm (前端) |

## 项目结构

```
campusmarket/
├── frontend/              # 用户端前端
│   ├── src/
│   │   ├── views/         # 页面组件 (首页/分类/详情/发布/消息/个人中心等)
│   │   ├── router/        # 路由配置
│   │   ├── store/         # 状态管理
│   │   └── utils/         # 请求封装
│   └── public/image/      # 静态图片资源
├── admin/                 # 后台管理前端
│   └── src/
│       ├── views/         # 管理页面 (商品/用户/订单/分类/公告)
│       ├── router/        # 路由配置
│       └── utils/         # 请求封装
├── backend/               # 后端
│   └── src/main/
│       ├── java/com/campus/
│       │   ├── controller/   # 接口层
│       │   ├── service/      # 业务逻辑
│       │   ├── mapper/      # 数据访问
│       │   ├── entity/      # 实体类
│       │   ├── config/       # 拦截器/跨域配置
│       │   └── common/      # 通用工具 (Result/PageResult/AuthContext)
│       └── resources/
│           ├── application.yml
│           └── mapper/      # MyBatis XML
├── db/                    # 数据库脚本
│   ├── init.sql           # 建表+初始数据
│   └── insert_test_products.sql  # 测试商品数据
└── pom.xml                # Maven 父配置
```

## 功能模块

### 用户端

- 首页商品展示 + 轮播图 + 搜索
- 分类筛选 (教材/电子产品/生活用品/其他)
- 商品详情 + 相似推荐 + 收藏
- 商品发布 (含交易方式选择：当面交易/校内送货上门)
- 订单管理 (下单/确认/完成/取消)
- 站内私信 (会话列表 + 气泡式聊天)
- 站内公告
- 个人中心 (头像/昵称/简介/性别/生日/爱好)
- 地址管理 (增删改查 + 默认地址)
- 修改密码

### 管理后台

- 数据看板 (商品数/用户数/订单数/交易额统计图表)
- 商品管理 (审核/上下架/删除/分类筛选)
- 用户管理 (编辑/冻结解冻/删除)
- 订单管理 (状态流转/分页查询)
- 分类管理 (增删改查)
- 公告管理 (发布/编辑/删除)

## 快速开始

### 1. 数据库

```bash
# 创建数据库并执行建表脚本
mysql -u root -p -e "CREATE DATABASE campus_market DEFAULT CHARSET utf8mb4"
mysql -u root -p campus_market < db/init.sql
mysql -u root -p campus_market < db/insert_test_products.sql  # 可选：插入测试商品
```

### 2. 后端

```bash
cd backend
# 修改 application.yml 中的数据库密码
# Windows 使用 mvnw.cmd，Linux/Mac 使用 ./mvnw
mvnw.cmd spring-boot:run
```

后端启动在 `http://localhost:8080/api`

### 3. 用户端前端

```bash
cd frontend
npm install
npm run dev
```

前端启动在 `http://localhost:5173`

### 4. 管理后台前端

```bash
cd admin
npm install
npm run dev
```

管理后台启动在 `http://localhost:5174`

### 5. 测试账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | admin | 123456 |
| 用户 | 见 init.sql 中的测试用户 | 123456 |

## API 规范

- 所有接口前缀 `/api`
- 需登录的接口在请求头携带 `token`
- `/admin/` 路径仅管理员可访问
- 交易方式使用位掩码：1=当面交易，2=校内送货上门，3=两者

## 部署

```bash
# 前端构建
cd frontend && npm run build
cd admin && npm run build

# 后端打包
cd backend && mvnw.cmd clean package -DskipTests

# 将 frontend/dist 部署到 Nginx，admin/dist 部署到另一个路径
# 后端 jar 包运行：java -jar backend/target/*.jar
```

## License

MIT
