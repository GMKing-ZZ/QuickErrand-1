# QuickErrand - 跑腿小程序整体解决方案

<div align="center">

![Version](https://img.shields.io/badge/版本-1.9.8-blue.svg)
![License](https://img.shields.io/badge/许可证-MIT-green.svg)
![Platform](https://img.shields.io/badge/平台-微信小程序%20%7C%20App%20%7C%20H5-lightgrey.svg)

**一个完整的跑腿服务平台，包含用户端、跑腿员端和管理后台**

[在线演示](#) | [快速开始](#快速开始) | [功能特性](#功能特性) | [技术架构](#技术架构)

</div>

---

## 📖 项目简介

QuickErrand 是一个功能完善的跑腿小程序整体解决方案，采用前后端分离架构，实现了从用户下单、跑腿员接单到订单评价的完整业务闭环。项目包含三个独立子系统：

- **QuickErrand-app**：基于 uni-app x 的多端小程序（支持微信小程序、App、H5等）
- **QuickErrand-admin**：基于 Vue 3 的管理后台系统
- **QuickErrand-backend**：基于 Spring Boot 的后端服务

## ✨ 功能特性

### 用户端功能

#### 🏠 首页模块
- 服务类型快速选择（代买、代送、代取、代办、代寄）
- Banner 轮播展示
- 平台公告通知
- 智能搜索服务

#### 📦 订单管理
- 订单发布：支持多种订单类型、地图选点、费用预估
- 订单列表：多状态筛选、下拉刷新、上拉加载
- 订单详情：实时状态跟踪、费用明细、操作按钮
- 订单评价：星级评分、文字评价、图片上传

#### 📍 地址管理
- 地址列表管理
- 新增/编辑地址
- 地图选点功能
- 默认地址设置

#### 👤 个人中心
- 用户信息管理
- 我的订单
- 我的评价
- 消息通知
- 聊天消息

#### 🔐 认证授权
- 账号密码登录
- 微信一键登录
- 手机号注册
- 找回密码

### 跑腿员端功能

#### 🏃 跑腿员首页
- 收益统计（今日、本月、累计）
- 服务统计（完成订单、好评率、信用等级）
- 快捷操作入口
- 待办提醒

#### 📋 订单管理
- 待接订单：智能筛选、一键接单
- 进行中订单：取货确认、配送完成
- 订单历史：多维度筛选排序

#### 💰 收益管理
- 收益统计与明细
- 提现申请
- 提现记录查询

#### 🎖️ 认证中心
- 跑腿员认证申请
- 认证状态查询
- 资料编辑

### 管理后台功能

#### 📊 数据看板
- 订单统计（今日订单、待接单、进行中）
- 用户统计（总用户、今日新增）
- 收益统计（今日收益、总收益）
- 跑腿员统计

#### 👥 用户管理
- 用户列表查询
- 用户状态管理
- 用户详情查看

#### 📝 订单管理
- 订单列表查询
- 订单详情查看
- 订单状态管理

#### ⚙️ 系统管理
- 订单类型配置
- 跑腿员认证审核
- 信用等级管理
- 公告管理
- 轮播图管理
- 提现审核
- 收益结算
- 消息管理

## 🏗️ 技术架构

### 整体架构

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   用户端/跑腿员端  │     │    管理后台      │     │    后端服务      │
│  (uni-app x)    │     │  (Vue 3 + Vite) │     │ (Spring Boot)   │
└────────┬────────┘     └────────┬────────┘     └────────┬────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌────────────┴────────────┐
                    │                         │
            ┌───────▼────────┐       ┌───────▼────────┐
            │     MySQL      │       │     Redis      │
            │   (数据库)      │       │   (缓存)       │
            └────────────────┘       └────────────────┘
```

### 技术栈详情

#### 小程序端 (QuickErrand-app)

| 技术 | 版本 | 说明 |
|------|------|------|
| uni-app x | - | 跨平台开发框架 |
| Vue | 3.x | 渐进式 JavaScript 框架 |
| UTS | - | TypeScript 超集 |
| uni-pay-x | - | 统一支付模块 |
| 高德地图 | - | 地图定位服务 |

**支持平台**：
- 微信小程序
- Android App
- iOS App
- H5 网页
- 鸿蒙应用

#### 管理后台 (QuickErrand-admin)

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4.15 | 前端框架 |
| Element Plus | 2.5.4 | UI 组件库 |
| Vite | 5.0.11 | 构建工具 |
| Vue Router | 4.2.5 | 路由管理 |
| Pinia | 2.1.7 | 状态管理 |
| Axios | 1.6.5 | HTTP 客户端 |
| ECharts | 5.4.3 | 数据可视化 |
| WangEditor | 5.1.23 | 富文本编辑器 |

#### 后端服务 (QuickErrand-backend)

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

## 📁 项目结构

```
QuickErrand/
├── QuickErrand-app/              # 小程序端
│   ├── pages/                    # 主包页面
│   │   ├── home/                 # 首页
│   │   ├── order/                # 订单
│   │   ├── service/              # 服务
│   │   ├── mine/                 # 个人中心
│   │   └── runner/               # 跑腿员页面
│   ├── subpkg/                   # 分包页面
│   ├── common/                   # 公共模块
│   │   ├── api/                  # API 接口
│   │   └── utils/                # 工具函数
│   ├── components/               # 公共组件
│   ├── store/                    # 状态管理
│   ├── static/                   # 静态资源
│   ├── uni_modules/              # uni-app 插件
│   ├── manifest.json             # 应用配置
│   └── pages.json                # 页面配置
│
├── QuickErrand-admin/            # 管理后台
│   ├── src/
│   │   ├── api/                  # API 接口
│   │   ├── views/                # 页面组件
│   │   ├── components/           # 公共组件
│   │   ├── router/               # 路由配置
│   │   ├── store/                # 状态管理
│   │   ├── utils/                # 工具函数
│   │   └── styles/               # 样式文件
│   ├── vite.config.js            # Vite 配置
│   └── package.json              # 依赖配置
│
├── QuickErrand-backend/          # 后端服务
│   ├── src/main/
│   │   ├── java/com/quickerrand/
│   │   │   ├── controller/       # 控制器
│   │   │   ├── service/          # 服务层
│   │   │   ├── mapper/           # 数据访问
│   │   │   ├── entity/           # 实体类
│   │   │   ├── dto/              # 数据传输对象
│   │   │   ├── vo/               # 视图对象
│   │   │   ├── config/           # 配置类
│   │   │   ├── security/         # 安全模块
│   │   │   ├── websocket/        # WebSocket
│   │   │   └── utils/            # 工具类
│   │   └── resources/
│   │       ├── application.yml   # 配置文件
│   │       └── db/               # 数据库脚本
│   └── pom.xml                   # Maven 配置
│
├── quick_errand.sql              # 数据库初始化脚本
└── README.md                     # 项目说明文档
```

## 🚀 快速开始

### 环境要求

- **JDK**: 11+
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Node.js**: 16+
- **HBuilderX**: 3.99+

### 安装步骤

#### 1. 克隆项目

```bash
git clone https://github.com/yourusername/QuickErrand.git
cd QuickErrand
```

#### 2. 初始化数据库

```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE quick_errand CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入数据
mysql -u root -p quick_errand < quick_errand.sql
```

#### 3. 启动后端服务

```bash
cd QuickErrand-backend

# 修改配置文件
# 编辑 src/main/resources/application.yml
# 配置数据库和 Redis 连接信息

# 启动服务
mvn spring-boot:run
```

后端服务将在 `http://localhost:8088` 启动

API 文档地址：`http://localhost:8088/api/swagger-ui/`

#### 4. 启动管理后台

```bash
cd QuickErrand-admin

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

管理后台将在 `http://localhost:3000` 启动

#### 5. 启动小程序

1. 使用 HBuilderX 打开 `QuickErrand-app` 目录
2. 修改 `manifest.json` 中的小程序 AppID
3. 配置 API 地址（修改 `common/utils/request.uts` 中的 BASE_URL）
4. 运行到微信开发者工具或模拟器

### 默认账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | admin | admin123 |

**⚠️ 注意**: 请在生产环境中修改默认密码！

## 📊 数据库设计

### 核心数据表

| 表名 | 说明 |
|------|------|
| `t_user` | 用户表（普通用户、跑腿员、管理员） |
| `t_runner_info` | 跑腿员信息表 |
| `t_order` | 订单表 |
| `t_order_type` | 订单类型表 |
| `t_address` | 用户地址表 |
| `t_evaluation` | 评价表 |
| `t_chat_message` | 聊天消息表 |
| `t_chat_order_rel` | 订单-聊天绑定表 |
| `t_message` | 消息通知表 |
| `t_announcement` | 公告表 |
| `t_banner` | 轮播图表 |
| `t_earnings_record` | 收益记录表 |
| `t_withdrawal_record` | 提现记录表 |

## 🔧 配置说明

### 后端配置

#### application.yml

```yaml
server:
  port: 8088

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/quick_errand
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379

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

### 小程序配置

#### manifest.json

```json
{
  "name": "QuickErrand-app",
  "versionName": "1.9.8",
  "mp-weixin": {
    "appid": "your_appid",
    "setting": {
      "urlCheck": false
    }
  }
}
```

## 🌐 部署指南

### 生产环境部署

#### 后端部署

```bash
# 打包
mvn clean package

# 运行
java -jar target/quickerrand-backend-1.0.0.jar
```

#### 管理后台部署

```bash
# 构建
npm run build

# 将 dist 目录部署到 Nginx
```

#### 小程序部署

1. 在微信开发者工具中上传代码
2. 在微信公众平台提交审核
3. 审核通过后发布

### 云开发部署

项目已支持腾讯云开发 CloudBase 部署，详见 [QuickErrand-app/DEPLOYMENT.md](./QuickErrand-app/DEPLOYMENT.md)

## 📝 开发规范

### 代码规范

- **Java**: 遵循阿里巴巴 Java 开发手册
- **前端**: 使用 ESLint + Prettier 统一格式
- **Git 提交**: 使用约定式提交（Conventional Commits）

### 提交规范

- `feat`: 新功能
- `fix`: 修复 Bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建/工具变动

### 接口规范

- RESTful API 设计
- 统一响应格式：`Result<T>`
- JWT 认证：`Authorization: Bearer <token>`

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'feat: Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 👥 作者

**周政**

指导教师：**吴换霞**（副教授）

## 🙏 致谢

感谢以下开源项目：

- [uni-app x](https://uniapp.dcloud.net.cn/)
- [Vue.js](https://vuejs.org/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Element Plus](https://element-plus.org/)

## 📞 联系方式

如有问题或建议，欢迎通过以下方式联系：

- 提交 Issue
- 发送邮件

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给一个 Star ⭐**

</div>
