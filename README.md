# CineNova 智慧票务平台（毕业设计·独立全栈开发）

> 基于 Spring Boot + Vue3 的前后端分离电影票预订系统，支持用户购票、影院管理、后台运营三大模块

## 项目简介

**CineNova 智慧票务平台**是一个完整的电影票预订系统，采用 B/S 架构前后端分离设计。系统涵盖影片浏览、选座购票、在线支付、订单管理、评论互动、数据统计等核心业务流程，面向普通用户、影院管理员、系统管理员三种角色提供差异化功能体验。

## 技术栈

| 分类 | 技术 | 版本 |
| :--- | :--- | :--- |
| **前端框架** | Vue | 3.x (Beta) |
| **构建工具** | Vite | 6.x |
| **路由管理** | Vue Router | 4.x |
| **状态管理** | Pinia | 3.x |
| **UI 组件** | Element Plus | 2.x |
| **HTTP 客户端** | Axios | 1.x |
| **二维码生成** | QRCodeJS | - |
| **后端框架** | Spring Boot | 2.7.x |
| **ORM** | Spring Data JPA (Hibernate) | - |
| **数据库** | MySQL | 8.x+ |
| **支付集成** | Alipay SDK | 4.x |
| **代码简化** | Lombok | - |

## 项目结构

```
test/
├── movie-ticket-frontend/    # 前端项目
│   ├── src/
│   │   ├── views/
│   │   │   ├── user/          # 用户端页面
│   │   │   ├── admin/         # 管理端页面
│   │   │   └── cinema-admin/  # 影院端页面
│   │   ├── api/               # API 接口封装
│   │   ├── stores/            # Pinia 状态管理
│   │   └── router/            # 路由配置
│   └── package.json
└── movie-ticket-backend/     # 后端项目
    ├── src/main/java/
    │   ├── controller/        # REST API 控制层
    │   ├── service/           # 业务逻辑层
    │   ├── repository/        # 数据访问层
    │   └── entity/            # 实体类
    └── pom.xml
```

## 核心功能

### 用户端
- 首页影片轮播展示
- 影片列表筛选与搜索
- 影片详情与评论互动
- 选座购票流程
- 支付宝在线支付
- 订单管理（查看、取消、退款）
- 个人中心与收藏功能

### 影院管理端
- 放映厅管理
- 影片排期管理
- 订单查询
- 影院设置

### 系统管理端
- 控制台数据统计
- 影片管理（增删改查）
- 影院管理
- 轮播图管理
- 用户管理
- 订单管理
- 评论管理

## 功能截图

> 截图请放置在 `screenshots/` 目录下，以下为占位符

![首页](./screenshots/home.png)

![选座购票](./screenshots/seat-selection.png)

![后台仪表盘](./screenshots/dashboard.png)

![订单管理](./screenshots/order-management.png)

## 本地运行

### 前置条件

- JDK 11+
- Node.js 20+ / npm
- MySQL 8.0+

### 1. 数据库配置

创建数据库并执行初始化脚本：

```sql
CREATE DATABASE movie_ticket CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE movie_ticket;
```

执行初始化脚本：
```bash
mysql -u username -p movie_ticket < movie-ticket-backend/init_database.sql
```

### 2. 后端启动

修改后端数据库配置：
- 文件路径：`movie-ticket-backend/src/main/resources/application.properties`
- 修改数据库连接信息（用户名、密码）

启动后端服务：

```bash
cd movie-ticket-backend
mvn spring-boot:run
```

后端服务默认运行在 `http://localhost:8080`

### 3. 前端启动

```bash
cd movie-ticket-frontend
npm install
npm run dev
```

前端服务默认运行在 `http://localhost:5173`

### 4. 访问系统

打开浏览器访问 `http://localhost:5173`

### 默认账号

| 角色 | 用户名 | 密码 |
| :--- | :--- | :--- |
| 普通用户 | user | 123456 |
| 系统管理员 | admin | 123456 |
| 影院管理员 | cinema | 123456 |

## 支付配置

本项目使用支付宝沙箱环境进行支付测试：

1. 在支付宝开放平台创建沙箱应用
2. 获取 AppID、私钥、公钥
3. 修改配置文件：`movie-ticket-backend/src/main/java/com/example/movieticket/config/AliPayConfig.java`
4. 配置支付回调地址

## API 接口

前端 API 封装位于 [api.js](file:///d:/谭佳佳/Desktop/school/毕业论文-设计/test/movie-ticket-frontend/src/api/api.js)，包含以下模块：
- `movieAPI` - 影片相关接口
- `orderAPI` - 订单相关接口
- `cinemaAPI` - 影院相关接口
- `commentAPI` - 评论相关接口
- `dashboardAPI` - 数据统计接口
- `alipayAPI` - 支付宝支付接口
- 等共 11 个 API 模块，80+ 接口

## 开发说明

### 前端开发规范

- 使用 Vue 3 Composition API
- 路由守卫控制页面访问权限
- Pinia 管理全局状态
- Element Plus 组件库构建界面
- ESLint + Prettier 代码格式化

### 后端开发规范

- Spring Boot 分层架构（Controller → Service → Repository）
- Spring Data JPA 数据访问
- RESTful API 设计规范
- 统一响应格式 `{code, message, data}`

## License

MIT License

---

📌 注：本项目为毕业设计，仅展示技术实现，不涉及商业用途。
