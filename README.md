# 酒店客房管理系统

Hotel Room Management System

## 项目简介

酒店客房管理系统是一个面向中小型酒店的综合性管理平台，覆盖前台接待、客房预订、入住管理、退房结算、客房状态维护、营收统计等核心业务流程，实现酒店日常运营的数字化、规范化管理。

## 技术栈

| 项目     | 版本/工具              |
| -------- | ---------------------- |
| JDK      | Java 25                |
| 数据库   | MySQL Community 8.0.46 |
| 构建工具 | Maven 3.9+             |
| 框架     | Spring Boot 3.x        |
| 持久层   | MyBatis 3.x            |
| UI 框架  | Java Swing / JavaFX    |
| 连接池   | HikariCP               |

## 功能模块

- 前台接待：客房状态查询、预订管理、入住/退房办理
- 客房管理：房间状态维护、清洁调度、维修管理
- 营收统计：财务报表、营业额分析、入住率统计

## 设计目标

| 目标     | 实现策略                                          |
| -------- | ------------------------------------------------- |
| 可维护性 | 分层架构、单一职责、面向接口编程、统一异常处理    |
| 可扩展性 | 开闭原则、策略模式、工厂模式、配置文件驱动        |
| 可复用性 | 通用 DAO 基类、统一工具类、组件化 UI              |
| 可测试性 | 依赖注入、业务与 UI 分离、Service 层独立单元测试  |
| 可靠性   | 事务管理、异常回滚、乐观锁、数据校验              |
| 安全性   | SQL 注入防护、权限校验、敏感数据加密、日志审计    |
| 高效性   | 连接池复用、二级缓存、索引优化、延迟加载、批量操作 |
| 可移植性 | JDBC 标准、Spring Boot 跨平台、配置外部化         |

## 环境要求

| 软件       | 最低版本       | 说明                         |
| ---------- | -------------- | ---------------------------- |
| JDK        | 17             | 推荐 Eclipse Temurin 17      |
| MySQL      | 8.0+           | 社区版即可，需开启服务       |
| Maven      | 3.8+           | 构建与依赖管理               |
| 操作系统   | Windows/macOS/Linux | 跨平台 Swing 桌面应用    |

## 数据库配置

### 1. 创建数据库

```sql
mysql -u root -p
CREATE DATABASE hotel_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 执行初始化脚本

按以下顺序执行 `src/main/resources/sql/` 目录下的脚本：

```bash
# 顺序很重要，不可颠倒
mysql -u root -p hotel_db < src/main/resources/sql/schema.sql
mysql -u root -p hotel_db < src/main/resources/sql/procedures.sql
mysql -u root -p hotel_db < src/main/resources/sql/triggers.sql
mysql -u root -p hotel_db < src/main/resources/sql/views.sql
mysql -u root -p hotel_db < src/main/resources/sql/init_data.sql
```

| 脚本             | 内容               |
| ---------------- | ------------------ |
| `schema.sql`     | 10 张数据表结构    |
| `procedures.sql` | 5 个存储过程       |
| `triggers.sql`   | 7 个触发器         |
| `views.sql`      | 5 个视图           |
| `init_data.sql`  | 初始化角色/用户/房间等数据 |

### 3. 修改数据库连接

编辑 `src/main/resources/application.properties`，将默认密码占位符替换为你的 MySQL 密码：

```properties
# 修改这一行
spring.datasource.password=your_password  →  spring.datasource.password=你的真实密码
```

## 快速开始

```bash
# 1. 克隆仓库
git clone https://github.com/Vesperi174/-Hotel-Room-Management-System-.git
cd Hotel-Room-Management-System

# 2. 编译打包（跳过测试）
mvn clean package -DskipTests

# 3. 运行
java -Dfile.encoding=UTF-8 -Djava.awt.headless=false -jar target/hotel-room-management-1.0.0.jar

# 或者直接使用 Maven 运行
mvn spring-boot:run
```

## 默认账号

| 用户名 | 密码   | 角色       |
| ------ | ------ | ---------- |
| admin  | admin123 | 系统管理员 |

## 项目结构

```
src/main/java/com/hotel/
├── HotelApplication.java          # 启动入口
├── config/                         # Spring 配置类
├── common/
│   ├── enums/                      # 状态枚举
│   ├── exception/                  # 自定义异常
│   └── util/                       # 工具类（密码加密、日期等）
├── model/
│   ├── entity/                     # 9 个实体类
│   ├── dto/                        # 请求 DTO
│   └── vo/                         # 视图 VO
├── dao/                            # 10 个 DAO 接口
│   └── impl/                       # MyBatis 实现
├── service/                        # 7 个 Service 接口
│   └── impl/                       # 业务逻辑实现
├── controller/                     # 8 个 Controller
├── view/                           # 11 个 Swing 界面
└── pattern/                        # 13 种设计模式
    ├── template/                   # 模板方法（JdbcTemplate）
    ├── observer/                   # 观察者（房间状态通知）
    ├── command/                    # 命令（入住/退房/清洁）
    ├── facade/                     # 外观（统一服务入口）
    ├── proxy/                      # 代理（权限控制）
    ├── decorator/                  # 装饰器（房价优惠）
    └── strategy/                   # 策略（定价策略）
```

## 许可证

本项目基于 MIT License 开源，详见 [LICENSE](LICENSE) 文件。