# 电影票务系统后端

## 项目结构

```
movie-ticket-backend/
├── src/
│   ├── main/
│   │   ├── java/com/example/movieticket/
│   │   │   ├── config/          # 配置类
│   │   │   ├── controller/      # 控制器
│   │   │   ├── entity/          # 实体类
│   │   │   ├── repository/      # 数据访问层
│   │   │   ├── service/         # 服务层
│   │   │   └── MovieTicketApplication.java  # 主应用类
│   │   └── resources/
│   │       └── application.properties  # 配置文件
│   └── test/                    # 测试代码
├── pom.xml                      # Maven依赖配置
└── README.md                    # 项目说明
```

## 技术栈

- Spring Boot 3.2.0
- Spring Web
- Spring Data JPA
- MySQL
- Lombok

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库配置

1. 创建数据库 `movie_ticket`
2. 修改 `src/main/resources/application.properties` 中的数据库连接信息

### 3. 运行项目

```bash
# 编译项目
mvn clean package

# 运行项目
mvn spring-boot:run
```

### 4. 接口说明

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/banners` | GET | 获取首页轮播图数据 |
| `/api/movies/hot` | GET | 获取正在热映的电影列表 |
| `/api/movies/coming` | GET | 获取即将上映的电影列表 |
| `/api/cinemas/hot` | GET | 获取热门影院列表 |

### 5. 数据初始化

项目启动时会自动初始化以下数据：
- 3个轮播图
- 6部电影（5部上映中，1部即将上映）
- 4家热门影院

数据来源于前端的mockData.js文件，确保前后端数据一致。