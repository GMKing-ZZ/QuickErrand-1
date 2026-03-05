# QuickErrand Backend

跑腿小程序后端服务 - 基于 Spring Boot 的 RESTful API 服务

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.18 | 基础框架 |
| MyBatis Plus | 3.5.3.1 | ORM框架 |
| MySQL | 8.0 | 数据库 |
| Redis | 6.0+ | 缓存与会话管理 |
| JWT | 0.11.5 | 身份认证 |
| Spring Security | - | 权限控制 |
| Swagger | 3.0 | 接口文档 |
| WebSocket | - | 实时通讯 |
| Hutool | 5.8.23 | 工具库 |
| FastJSON | 2.0.43 | JSON处理 |

## 项目结构

```
QuickErrand-backend
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── quickerrand
│   │   │           ├── QuickErrandApplication.java    # 启动类
│   │   │           ├── common/                        # 公共类
│   │   │           │   └── Result.java                # 统一响应封装
│   │   │           ├── config/                        # 配置类
│   │   │           │   ├── MyBatisPlusConfig.java     # MyBatis Plus配置
│   │   │           │   ├── RedisCacheConfig.java      # Redis缓存配置
│   │   │           │   ├── SecurityConfig.java        # Security安全配置
│   │   │           │   ├── SwaggerConfig.java         # Swagger配置
│   │   │           │   ├── WebConfig.java             # Web配置
│   │   │           │   └── WebSocketConfig.java       # WebSocket配置
│   │   │           ├── controller/                    # 控制器
│   │   │           │   ├── AuthController.java        # 认证控制器
│   │   │           │   ├── UserController.java        # 用户控制器
│   │   │           │   ├── OrderController.java       # 订单控制器
│   │   │           │   ├── RunnerAuthController.java  # 跑腿员认证
│   │   │           │   ├── RunnerOrderController.java # 跑腿员订单
│   │   │           │   ├── RunnerEarningsController.java # 跑腿员收益
│   │   │           │   ├── AddressController.java     # 地址控制器
│   │   │           │   ├── ChatController.java        # 聊天控制器
│   │   │           │   ├── MessageController.java     # 消息控制器
│   │   │           │   ├── AnnouncementController.java # 公告控制器
│   │   │           │   ├── BannerController.java      # 轮播图控制器
│   │   │           │   ├── FileController.java        # 文件上传控制器
│   │   │           │   └── Admin*Controller.java      # 管理后台接口
│   │   │           ├── service/                       # 服务层
│   │   │           │   └── impl/                      # 服务实现
│   │   │           ├── mapper/                        # 数据访问层
│   │   │           ├── entity/                        # 实体类
│   │   │           │   ├── User.java                  # 用户实体
│   │   │           │   ├── Order.java                 # 订单实体
│   │   │           │   ├── RunnerInfo.java            # 跑腿员信息
│   │   │           │   ├── Address.java               # 地址实体
│   │   │           │   ├── Evaluation.java            # 评价实体
│   │   │           │   ├── ChatMessage.java           # 聊天消息
│   │   │           │   ├── Announcement.java          # 公告实体
│   │   │           │   ├── Banner.java                # 轮播图实体
│   │   │           │   ├── EarningsRecord.java        # 收益记录
│   │   │           │   └── WithdrawalRecord.java      # 提现记录
│   │   │           ├── dto/                           # 数据传输对象
│   │   │           ├── vo/                            # 视图对象
│   │   │           ├── security/                      # 安全相关
│   │   │           │   ├── JwtAuthenticationFilter.java
│   │   │           │   └── JwtAuthenticationEntryPoint.java
│   │   │           ├── websocket/                     # WebSocket处理
│   │   │           │   ├── ChatWebSocketHandler.java  # 聊天处理
│   │   │           │   ├── MessagePushHandler.java    # 消息推送
│   │   │           │   └── OrderPushHandler.java      # 订单推送
│   │   │           ├── task/                          # 定时任务
│   │   │           │   └── EarningsSettlementScheduler.java
│   │   │           ├── exception/                     # 异常处理
│   │   │           │   ├── BusinessException.java
│   │   │           │   └── GlobalExceptionHandler.java
│   │   │           └── utils/                         # 工具类
│   │   │               ├── JwtUtils.java              # JWT工具
│   │   │               ├── RedisUtils.java            # Redis工具
│   │   │               ├── SecurityUtils.java         # 安全工具
│   │   │               └── PasswordUtils.java         # 密码工具
│   │   └── resources
│   │       ├── application.yml                        # 主配置文件
│   │       ├── application-swagger.yml                # Swagger配置
│   │       ├── logback-spring.xml                     # 日志配置
│   │       └── db/                                    # 数据库脚本
│   └── test                                           # 测试代码
├── logs/                                              # 日志目录
└── pom.xml                                            # Maven配置
```

## 功能模块

### 用户模块
- 用户注册、登录（账号密码/微信登录）
- 用户信息管理
- 密码修改、找回

### 订单模块
- 订单创建、费用自动计算
- 订单状态流转（待支付→待接单→服务中→已完成/已取消）
- 订单列表、详情查询
- 订单评价

### 跑腿员模块
- 跑腿员认证申请
- 认证审核（管理端）
- 信用等级管理
- 服务范围设置

### 聊天模块
- 基于 WebSocket 的实时聊天
- 订单关联会话
- 消息已读状态
- 未读消息统计

### 收益模块
- 收益统计
- 收益明细
- 定时结算
- 提现申请与审核

### 管理后台接口
- 数据看板统计
- 用户管理
- 订单管理
- 认证审核
- 评价管理
- 公告管理
- 轮播图管理
- 提现审核
- 收益结算

## API 接口

### 认证接口 `/api/auth`
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /login | 用户登录 |
| POST | /register | 用户注册 |
| POST | /wx-login | 微信登录 |
| POST | /logout | 退出登录 |

### 用户接口 `/api/user`
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /info | 获取用户信息 |
| PUT | /info | 更新用户信息 |
| PUT | /password | 修改密码 |

### 订单接口 `/api/order`
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /create | 创建订单 |
| POST | /calculate-fee | 计算费用 |
| GET | /list | 订单列表 |
| GET | /{id} | 订单详情 |
| POST | /{id}/cancel | 取消订单 |
| POST | /{id}/evaluate | 评价订单 |

### 跑腿员接口 `/api/runner`
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /auth/apply | 认证申请 |
| GET | /auth/status | 认证状态 |
| GET | /orders/pending | 待接订单 |
| POST | /orders/{id}/accept | 接单 |
| GET | /orders/active | 进行中订单 |
| GET | /earnings | 收益统计 |

### 管理后台接口 `/api/admin`
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /login | 管理员登录 |
| GET | /dashboard | 数据看板 |
| GET | /users | 用户列表 |
| GET | /orders | 订单列表 |
| GET | /statistics | 统计数据 |

## 快速开始

### 1. 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 2. 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE quick_errand CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 导入数据：
```bash
mysql -u root -p quick_errand < ../quick_errand.sql
```

3. 修改 `application.yml` 中的数据库配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/quick_errand?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 1234
  redis:
    host: localhost
    port: 6379
    password:
```

### 3. 启动项目

```bash
mvn spring-boot:run
```

### 4. 访问接口文档

启动成功后访问：http://localhost:8088/api/swagger-ui/

## 配置说明

### 服务端口
默认端口：8088，可在 `application.yml` 中修改：
```yaml
server:
  port: 8088
```

### JWT配置
```yaml
jwt:
  secret: quickerrand-secret-key-2026
  expiration: 604800  # 7天
```

### 文件上传配置
```yaml
file:
  upload:
    path: e:/QuickErrand/uploads
    url-prefix: http://localhost:8088/api
```

### 订单费用配置
```yaml
quickerrand:
  fee:
    base-price: 5.00        # 基础费用
    price-per-km: 2.00      # 每公里费用
    platform-rate: 0.10     # 平台抽成比例
```

## 开发规范

- 遵循阿里巴巴 Java 开发手册
- 使用 RESTful API 设计规范
- 统一使用 Result 类封装响应结果
- 所有接口需要添加 Swagger 注解
- 使用 @RestController 注解
- 统一异常处理

## 作者

周政

## 指导教师

吴换霞（副教授）
