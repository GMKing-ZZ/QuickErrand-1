# QuickErrand Backend
## 📖 项目简介

QuickErrand 后端服务是基于 Spring Boot 2.7.18 开发的 RESTful API 服务，为跑腿小程序提供完整的后端支持。项目采用前后端分离架构，提供用户管理、订单管理、跑腿员管理、实时聊天等核心功能，支持 JWT 认证、WebSocket 实时通讯、Redis 缓存等特性。

## ✨ 功能特性

### 👤 用户模块

#### 用户认证
- 用户注册（手机号注册）
- 用户登录（账号密码登录）
- 微信一键登录
- JWT Token 认证
- 密码加密存储

#### 用户管理
- 用户信息管理
- 密码修改
- 找回密码
- 用户状态管理

### 📦 订单模块

#### 订单创建
- 订单创建
- 费用自动计算（基础费用 + 距离费用）
- 订单类型选择
- 地图选点支持

#### 订单流程
- 待支付 → 待接单 → 服务中 → 已完成
- 订单取消
- 订单超时处理

#### 订单管理
- 订单列表查询
- 订单详情查看
- 订单状态更新
- 订单评价

### 🏃 跑腿员模块

#### 跑腿员认证
- 认证申请
- 认证审核（管理端）
- 认证状态查询
- 身份证照片上传

#### 跑腿员管理
- 跑腿员信息管理
- 信用等级管理
- 服务范围设置
- 服务统计

#### 接单管理
- 待接订单列表
- 一键接单
- 进行中订单管理
- 订单完成确认

### 💬 聊天模块

#### 实时聊天
- 基于 WebSocket 的实时聊天
- 订单关联会话
- 消息已读状态
- 未读消息统计
- 消息历史记录

### 💰 收益模块

#### 收益管理
- 收益统计
- 收益明细
- 定时结算
- 收益提现

#### 提现管理
- 提现申请
- 提现审核
- 提现到账
- 提现记录

### 📢 内容管理模块

#### 公告管理
- 公告发布
- 公告列表
- 公告状态管理

#### 轮播图管理
- 轮播图上传
- 轮播图列表
- 排序管理

### 📊 统计模块

#### 数据统计
- 订单统计
- 用户统计
- 收益统计
- 跑腿员统计

### 🔔 消息模块

#### 消息推送
- 系统消息
- 订单消息
- 实时推送

## 🏗️ 技术架构

### 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.18 | 基础框架 |
| MyBatis Plus | 3.5.3.1 | ORM 框架 |
| MySQL | 8.0 | 关系型数据库 |
| Redis | 6.0+ | 缓存数据库 |
| JWT | 0.11.5 | 身份认证 |
| Spring Security | - | 权限控制 |
| Swagger | 3.0 | API 文档 |
| WebSocket | - | 实时通讯 |
| Hutool | 5.8.23 | 工具库 |
| FastJSON | 2.0.43 | JSON 处理 |
| Lombok | - | 代码简化 |

### 架构设计

```
┌─────────────────────────────────────────────────────────┐
│                      Controller 层                       │
│          (接收请求、参数校验、返回响应)                     │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│                       Service 层                         │
│          (业务逻辑处理、事务管理)                          │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│                       Mapper 层                          │
│          (数据访问、SQL 执行)                             │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│                     数据库层                              │
│              MySQL + Redis                               │
└─────────────────────────────────────────────────────────┘
```

### 核心特性

- **RESTful API**：统一的接口设计规范
- **JWT 认证**：无状态的身份认证机制
- **权限控制**：基于 Spring Security 的权限管理
- **缓存支持**：Redis 缓存提升性能
- **实时通讯**：WebSocket 支持实时聊天
- **统一异常处理**：全局异常捕获和处理
- **API 文档**：Swagger 自动生成接口文档
- **日志管理**：完善的日志记录体系

## 📁 项目结构

```
QuickErrand-backend/
├── src/main/
│   ├── java/com/quickerrand/
│   │   ├── QuickErrandApplication.java    # 启动类
│   │   │
│   │   ├── common/                        # 公共类
│   │   │   └── Result.java                # 统一响应封装
│   │   │
│   │   ├── config/                        # 配置类
│   │   │   ├── MyBatisPlusConfig.java     # MyBatis Plus 配置
│   │   │   ├── RedisCacheConfig.java      # Redis 缓存配置
│   │   │   ├── SecurityConfig.java        # Security 安全配置
│   │   │   ├── SwaggerConfig.java         # Swagger 配置
│   │   │   ├── WebConfig.java             # Web 配置
│   │   │   └── WebSocketConfig.java       # WebSocket 配置
│   │   │
│   │   ├── controller/                    # 控制器
│   │   │   ├── AuthController.java        # 认证控制器
│   │   │   ├── UserController.java        # 用户控制器
│   │   │   ├── OrderController.java       # 订单控制器
│   │   │   ├── RunnerAuthController.java  # 跑腿员认证
│   │   │   ├── RunnerOrderController.java # 跑腿员订单
│   │   │   ├── RunnerEarningsController.java # 跑腿员收益
│   │   │   ├── AddressController.java     # 地址控制器
│   │   │   ├── ChatController.java        # 聊天控制器
│   │   │   ├── MessageController.java     # 消息控制器
│   │   │   ├── AnnouncementController.java # 公告控制器
│   │   │   ├── BannerController.java      # 轮播图控制器
│   │   │   ├── FileController.java        # 文件上传控制器
│   │   │   └── Admin*Controller.java      # 管理后台接口
│   │   │
│   │   ├── service/                       # 服务层
│   │   │   └── impl/                      # 服务实现
│   │   │
│   │   ├── mapper/                        # 数据访问层
│   │   │
│   │   ├── entity/                        # 实体类
│   │   │   ├── User.java                  # 用户实体
│   │   │   ├── Order.java                 # 订单实体
│   │   │   ├── RunnerInfo.java            # 跑腿员信息
│   │   │   ├── Address.java               # 地址实体
│   │   │   ├── Evaluation.java            # 评价实体
│   │   │   ├── ChatMessage.java           # 聊天消息
│   │   │   ├── Announcement.java          # 公告实体
│   │   │   ├── Banner.java                # 轮播图实体
│   │   │   ├── EarningsRecord.java        # 收益记录
│   │   │   └── WithdrawalRecord.java      # 提现记录
│   │   │
│   │   ├── dto/                           # 数据传输对象
│   │   │   ├── AddressDTO.java
│   │   │   ├── AdminLoginDTO.java
│   │   │   ├── CalculateFeeDTO.java
│   │   │   ├── CreateOrderDTO.java
│   │   │   ├── CreateUserDTO.java
│   │   │   ├── LoginDTO.java
│   │   │   ├── OrderQueryDTO.java
│   │   │   ├── RegisterDTO.java
│   │   │   ├── UserQueryDTO.java
│   │   │   └── WxLoginDTO.java
│   │   │
│   │   ├── vo/                            # 视图对象
│   │   │   ├── AddressVO.java
│   │   │   ├── AdminLoginVO.java
│   │   │   ├── ChatContactVO.java
│   │   │   └── ...
│   │   │
│   │   ├── security/                      # 安全相关
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   └── JwtAuthenticationEntryPoint.java
│   │   │
│   │   ├── websocket/                     # WebSocket 处理
│   │   │   ├── ChatWebSocketHandler.java  # 聊天处理
│   │   │   ├── MessagePushHandler.java    # 消息推送
│   │   │   └── OrderPushHandler.java      # 订单推送
│   │   │
│   │   ├── task/                          # 定时任务
│   │   │   └── EarningsSettlementScheduler.java
│   │   │
│   │   ├── exception/                     # 异常处理
│   │   │   ├── BusinessException.java
│   │   │   └── GlobalExceptionHandler.java
│   │   │
│   │   └── utils/                         # 工具类
│   │       ├── JwtUtils.java              # JWT 工具
│   │       ├── RedisUtils.java            # Redis 工具
│   │       ├── SecurityUtils.java         # 安全工具
│   │       ├── PasswordUtils.java         # 密码工具
│   │       └── CodeUtils.java             # 验证码工具
│   │
│   └── resources/
│       ├── application.yml                # 主配置文件
│       ├── application-dev.yml            # 开发环境配置
│       ├── application-prod.yml           # 生产环境配置
│       ├── logback-spring.xml             # 日志配置
│       └── db/                            # 数据库脚本
│
├── logs/                                  # 日志目录
└── pom.xml                                # Maven 配置
```

## 🚀 快速开始

### 环境要求

- **JDK**: 11+
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Redis**: 6.0+

### 安装步骤

#### 1. 克隆项目

```bash
git clone https://github.com/yourusername/QuickErrand.git
cd QuickErrand/QuickErrand-backend
```

#### 2. 数据库配置

##### 创建数据库

```sql
CREATE DATABASE quick_errand 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

##### 导入数据

```bash
mysql -u root -p quick_errand < ../quick_errand.sql
```

#### 3. 修改配置文件

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/quick_errand?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  redis:
    host: localhost
    port: 6379
    password:
    database: 0
    timeout: 5000ms

server:
  port: 8088

jwt:
  secret: quickerrand-secret-key-2026
  expiration: 604800

file:
  upload:
    path: /path/to/uploads
    url-prefix: http://localhost:8088/api

quickerrand:
  fee:
    base-price: 5.00
    price-per-km: 2.00
    platform-rate: 0.10
```

#### 4. 启动 Redis

```bash
redis-server
```

#### 5. 启动项目

```bash
mvn spring-boot:run
```

或使用 IDE 运行 `QuickErrandApplication.java`

#### 6. 访问 API 文档

启动成功后访问：`http://localhost:8088/api/swagger-ui/`

## 📡 API 接口

### 认证接口 `/api/auth`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /login | 用户登录 | ❌ |
| POST | /register | 用户注册 | ❌ |
| POST | /wx-login | 微信登录 | ❌ |
| POST | /logout | 退出登录 | ✅ |

### 用户接口 `/api/user`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /info | 获取用户信息 | ✅ |
| PUT | /info | 更新用户信息 | ✅ |
| PUT | /password | 修改密码 | ✅ |

### 订单接口 `/api/order`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /create | 创建订单 | ✅ |
| POST | /calculate-fee | 计算费用 | ✅ |
| GET | /list | 订单列表 | ✅ |
| GET | /{id} | 订单详情 | ✅ |
| POST | /{id}/cancel | 取消订单 | ✅ |
| POST | /{id}/evaluate | 评价订单 | ✅ |

### 跑腿员接口 `/api/runner`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /auth/apply | 认证申请 | ✅ |
| GET | /auth/status | 认证状态 | ✅ |
| GET | /orders/pending | 待接订单 | ✅ |
| POST | /orders/{id}/accept | 接单 | ✅ |
| GET | /orders/active | 进行中订单 | ✅ |
| GET | /earnings | 收益统计 | ✅ |

### 管理后台接口 `/api/admin`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /login | 管理员登录 | ❌ |
| GET | /dashboard | 数据看板 | ✅ |
| GET | /users | 用户列表 | ✅ |
| GET | /orders | 订单列表 | ✅ |
| GET | /statistics | 统计数据 | ✅ |

### 地址接口 `/api/address`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /list | 地址列表 | ✅ |
| POST | /save | 保存地址 | ✅ |
| DELETE | /{id} | 删除地址 | ✅ |
| PUT | /default/{id} | 设置默认地址 | ✅ |

### 聊天接口 `/api/chat`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /contacts | 联系人列表 | ✅ |
| GET | /messages | 消息列表 | ✅ |
| POST | /send | 发送消息 | ✅ |

### 公告接口 `/api/announcement`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /list | 公告列表 | ❌ |
| GET | /{id} | 公告详情 | ❌ |

### 轮播图接口 `/api/banner`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | /list | 轮播图列表 | ❌ |

## 🔧 配置说明

### 服务端口

默认端口：8088，可在 `application.yml` 中修改：

```yaml
server:
  port: 8088
```

### JWT 配置

```yaml
jwt:
  secret: quickerrand-secret-key-2026    # JWT 密钥
  expiration: 604800                      # 过期时间（7天）
```

### 文件上传配置

```yaml
file:
  upload:
    path: e:/QuickErrand/uploads          # 上传路径
    url-prefix: http://localhost:8088/api # URL 前缀
```

### 订单费用配置

```yaml
quickerrand:
  fee:
    base-price: 5.00        # 基础费用
    price-per-km: 2.00      # 每公里费用
    platform-rate: 0.10     # 平台抽成比例
```

### Redis 配置

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password:
    database: 0
    timeout: 5000ms
    lettuce:
      pool:
        max-active: 8
        max-wait: -1ms
        max-idle: 8
        min-idle: 0
```

### MyBatis Plus 配置

```yaml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

## 🌐 部署指南

### 开发环境

```bash
mvn spring-boot:run
```

### 生产环境

#### 1. 打包项目

```bash
mvn clean package -Dmaven.test.skip=true
```

#### 2. 运行 JAR 包

```bash
java -jar target/quickerrand-backend-1.0.0.jar
```

#### 3. 后台运行

```bash
nohup java -jar target/quickerrand-backend-1.0.0.jar > app.log 2>&1 &
```

### Docker 部署

#### 1. 构建 Docker 镜像

```bash
docker build -t quickerrand-backend:1.0.0 .
```

#### 2. 运行容器

```bash
docker run -d \
  --name quickerrand-backend \
  -p 8088:8088 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -v /path/to/uploads:/uploads \
  quickerrand-backend:1.0.0
```

### Nginx 反向代理

```nginx
server {
    listen 80;
    server_name api.your-domain.com;
    
    location /api {
        proxy_pass http://localhost:8088;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
    
    location /ws {
        proxy_pass http://localhost:8088;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
    }
}
```
## 🐛 常见问题

### 1. 数据库连接失败

**问题**：启动时报数据库连接错误

**解决**：
- 检查 MySQL 服务是否启动
- 检查数据库连接配置
- 检查数据库用户名和密码
- 检查数据库是否存在

### 2. Redis 连接失败

**问题**：启动时报 Redis 连接错误

**解决**：
- 检查 Redis 服务是否启动
- 检查 Redis 连接配置
- 检查 Redis 密码配置

### 3. JWT Token 无效

**问题**：接口返回 401 未授权

**解决**：
- 检查 Token 是否过期
- 检查 Token 格式是否正确
- 检查 JWT 密钥配置

### 4. 文件上传失败

**问题**：文件上传返回错误

**解决**：
- 检查上传路径是否存在
- 检查上传路径权限
- 检查文件大小限制

### 5. WebSocket 连接失败

**问题**：WebSocket 无法连接

**解决**：
- 检查 WebSocket 配置
- 检查 Nginx 代理配置
- 检查防火墙设置

## 👥 作者

**周政**

指导教师：**吴换霞**（副教授）