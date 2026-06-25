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

## 前置条件

运行本项目前，请确保以下软件已安装并正确配置：

| 软件 | 最低版本 | 说明 |
| --- | --- | --- |
| **JDK** | 17+ | 推荐 [Eclipse Temurin](https://adoptium.net/download/) 或 Oracle JDK。需配置 `JAVA_HOME` 环境变量。 |
| **MySQL** | 8.0+ | 社区版即可。**必须启动 MySQL 服务**，确保 `mysql` 命令行可用。 |
| **Maven** | 3.8+ | [Apache Maven](https://maven.apache.org/download.cgi) 构建工具。需配置 `MAVEN_HOME` 或确保 `mvn` 在 PATH 中。 |
| **操作系统** | Windows/macOS/Linux | 跨平台 Swing 桌面应用，Windows 用户推荐直接双击 `setup.bat`。 |

> 以上三项为**必须预先安装**的前置条件，`setup.bat` 脚本不会自动安装它们，仅做检测。脚本会自动完成数据库建表、初始化数据和项目编译打包。

## 快速开始（一键配置）

### Windows 用户

```cmd
双击 setup.bat 即可一键配置
```

脚本将自动完成以下步骤：
1. 检测 Java 环境（JDK 17+）
2. 检测 MySQL 并引导创建数据库
3. 自动执行所有 SQL 初始化脚本
4. 使用 Maven 编译打包项目
5. 生成 `start.bat` 启动脚本

**配置完成后，双击 `start.bat` 即可启动系统。**

### 手动配置（可选）

<details>
<summary>点击展开手动配置步骤</summary>

## 数据库配置

### 1. 创建数据库

```sql
mysql -u root -p
CREATE DATABASE hotel_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 执行初始化脚本

```bash
mysql -u root -p hotel_db < src/main/resources/sql/schema.sql
mysql -u root -p hotel_db < src/main/resources/sql/views.sql
mysql -u root -p hotel_db < src/main/resources/sql/triggers.sql
mysql -u root -p hotel_db < src/main/resources/sql/procedures.sql
mysql -u root -p hotel_db < src/main/resources/sql/init_data.sql
```

| 脚本             | 内容               |
| ---------------- | ------------------ |
| `schema.sql`     | 10 张数据表结构    |
| `views.sql`      | 5 个视图           |
| `triggers.sql`   | 7 个触发器         |
| `procedures.sql` | 5 个存储过程       |
| `init_data.sql`  | 初始化角色/用户/房间等数据 |

### 3. 修改数据库连接

编辑 `src/main/resources/application.properties`，将默认密码替换为你的 MySQL 密码：

```properties
spring.datasource.password=your_password
```

## 编译运行

```bash
mvn clean package -DskipTests
java -Dfile.encoding=UTF-8 -Djava.awt.headless=false -jar target/hotel-room-management-1.0.0.jar
```

</details>

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