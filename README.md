## QuickErrand 项目说明

QuickErrand 是一个跑腿小程序整体解决方案，包含 **微信/移动端小程序前端**、**管理后台前端** 以及 **Spring Boot 后端服务**，实现从用户下单、跑腿员接单到订单评价的完整闭环。

同级目录下的 `quick_errand.sql` 为项目数据库初始化脚本。

目录结构概览：

```text
QuickErrand/
├── quick_errand.sql          # 数据库初始化脚本
├── QuickErrand-backend/      # 后端服务（Spring Boot）
├── QuickErrand-admin/        # 管理后台前端（Vue3 + Element Plus）
├── QuickErrand-app/          # 小程序前端（uni-app x，微信小程序等）
└── uploads/                  # 文件上传目录
```

---

## 一、整体技术栈

- **后端**：Spring Boot 2.7.18、MyBatis Plus 3.5.3.1、MySQL 8.0、Redis、JWT、Swagger 3.0、WebSocket
- **管理后台**：Vue 3.4、Vite 5、Element Plus 2.5、Vue Router 4、Pinia 2、Axios、ECharts、WangEditor
- **小程序端**：uni-app x（Vue 3 + UTS），目标平台为微信小程序 / App-UVUE 等
- **即时通讯 / 实时模块**：基于 Spring WebSocket + Redis 支持跑腿订单的实时聊天

数据库结构请参考根目录的 `quick_errand.sql`。

---

## 二、各子项目说明

### 1. QuickErrand-backend（后端服务）

- **路径**：`QuickErrand-backend`
- **技术栈**：
  - Spring Boot 2.7.18
  - MyBatis Plus 3.5.3.1
  - MySQL 8.0
  - Redis（缓存与会话管理）
  - JWT + Spring Security（认证与权限控制）
  - Swagger 3.0（接口文档）
  - WebSocket（实时聊天与消息推送）
  - Hutool 5.8.23（工具库）
  - FastJSON 2.0.43（JSON处理）
- **主要功能模块**：
  - **用户模块**：用户/跑腿员登录注册、身份认证、微信登录、个人信息管理
  - **订单模块**：订单创建、报价、接单、配送、完成、取消全流程、费用自动计算
  - **跑腿员模块**：跑腿员认证申请与审核、信用等级管理、服务范围设置
  - **地址模块**：用户地址管理、地图选点
  - **评价模块**：订单评价（服务质量/服务态度评分、文字内容、图片）
  - **聊天模块**：基于 WebSocket 的即时聊天、订单关联会话
  - **消息模块**：系统消息、订单消息推送
  - **收益模块**：跑腿员收益统计、收益明细、定时结算
  - **提现模块**：提现申请、审核、到账
  - **公告模块**：公告发布与管理
  - **轮播图模块**：首页/个人中心轮播图管理
  - **统计模块**：数据看板、统计分析
  - **文件模块**：文件上传与管理
- **快速启动**：
  1. 在 MySQL 中执行 `quick_errand.sql` 初始化数据库。
  2. 修改 `QuickErrand-backend/src/main/resources/application.yml` 中的数据库与 Redis 配置。
  3. 在后端目录执行：
     ```bash
     mvn spring-boot:run
     ```
  4. 启动成功后可通过浏览器访问接口文档：
     - `http://localhost:8088/api/swagger-ui/`

更多后端细节可参考 `QuickErrand-backend/README.md`。

---

### 2. QuickErrand-admin（管理后台）

- **路径**：`QuickErrand-admin`
- **技术栈**：
  - Vue 3.4 + Vite 5
  - Element Plus 2.5
  - Vue Router 4、Pinia 2
  - Axios、Sass
  - ECharts 5.4（数据可视化）
  - WangEditor（富文本编辑）
- **主要功能**：
  - **数据看板**：订单统计、用户统计、收益统计
  - **用户管理**：用户列表、状态管理
  - **订单管理**：订单列表、订单详情、状态管理
  - **订单类型管理**：订单类型配置（代买、代送、代取、代办、代寄等）
  - **认证审核**：跑腿员认证申请审核
  - **评价管理**：用户评价列表与查看
  - **信用等级管理**：跑腿员信用等级配置
  - **公告管理**：公告发布与管理
  - **轮播图管理**：首页/个人中心轮播图配置
  - **提现审核管理**：跑腿员提现申请审核
  - **收益结算管理**：收益记录查看与管理
  - **消息管理**：系统消息管理
  - **个人中心**：管理员个人信息管理
- **开发运行**：
  ```bash
  cd QuickErrand-admin
  npm install
  npm run dev
  ```
- **接口代理**：`vite.config.js` 中将 `/api` 代理到后端（默认 `http://localhost:8088`），确保后台与后端端口配置一致。

更多说明见 `QuickErrand-admin/README.md`。

---

### 3. QuickErrand-app（小程序 / 移动端）

- **路径**：`QuickErrand-app`
- **技术栈**：
  - uni-app x（基于 Vue 3 + UTS）
  - HBuilderX / 微信开发者工具
  - uni-pay-x（支付模块）
- **用户端功能**：
  - **首页**：服务类型选择、Banner 轮播、公告展示
  - **发布订单**：选择订单类型、填写取送地址、物品描述、费用计算、地图选点
  - **订单管理**：订单列表、订单详情、订单状态跟踪、订单评价
  - **地址管理**：地址列表、新增/编辑地址、设置默认地址、地图选点
  - **个人中心**：用户信息、我的评价、消息通知、聊天消息
  - **登录注册**：账号密码登录、微信登录、注册、忘记密码
- **跑腿员端功能**：
  - **跑腿员首页**：收益统计、服务统计、快捷操作入口
  - **待接订单**：订单列表、接单操作
  - **进行中订单**：取货确认、配送完成
  - **收益管理**：收益统计、收益明细、提现申请、提现记录
  - **跑腿员认证**：认证申请、认证状态查看
  - **用户评价**：查看用户对自己的评价
- **聊天功能**：
  - 订单关联聊天会话
  - 实时消息收发（WebSocket）
  - 未读消息提醒
- **运行说明**：
  1. 使用 HBuilderX 打开 `QuickErrand-app` 目录。
  2. 根据 `env.js` / 接口配置，确认 API 基础地址指向本地后端（如 `http://localhost:8088/api`）。
  3. 选择运行到微信小程序 / 模拟器进行调试。

---

## 三、数据库设计

### 核心数据表

| 表名 | 说明 |
|------|------|
| `t_user` | 用户表（普通用户、跑腿员、管理员） |
| `t_runner_info` | 跑腿员信息表（认证信息、信用等级、服务统计） |
| `t_order` | 订单表（订单全流程信息） |
| `t_order_type` | 订单类型表（代买、代送、代取、代办、代寄） |
| `t_address` | 用户地址表 |
| `t_evaluation` | 评价表（服务质量、服务态度评分） |
| `t_chat_message` | 聊天消息表 |
| `t_chat_order_rel` | 订单-聊天绑定表 |
| `t_message` | 消息通知表 |
| `t_announcement` | 公告表 |
| `t_banner` | 轮播图表 |
| `t_earnings_record` | 收益记录表 |
| `t_withdrawal_record` | 提现记录表 |

### 数据库配置

- **数据库名**：`quick_errand`
- **字符集**：`utf8mb4`
- **MySQL 版本**：8.0+

---

## 四、部署提示

- **数据库脚本**：`quick_errand.sql`
  - 包含核心业务表的建表和部分初始化数据。
  - 请根据实际环境修改数据库名称、字符集及连接账号密码。
- **后端部署**：
  - 部署在独立服务器或云主机（JDK 1.8+）
  - 结合 Nginx 做反向代理
  - Redis 部署用于缓存和会话管理
  - 配置文件上传路径 `file.upload.path`
- **管理后台部署**：
  - 构建为静态资源后部署至 Nginx
  - `npm run build` 生成 dist 目录
- **小程序端部署**：
  - 在微信小程序后台配置合法域名
  - 上传代码审核发布

---

## 五、开发规范与约定

- **接口风格**：统一使用 RESTful 风格，响应体使用统一的 `Result` 包装。
- **认证方式**：前后端统一使用 JWT，在 `Authorization` 头中传递 `Bearer <token>`。
- **代码规范**：
  - Java 端参考阿里巴巴 Java 开发手册。
  - 前端采用 ESLint/Prettier（如有配置）统一格式。
  - Git 提交建议遵循 `feat / fix / docs / style / refactor / test / chore` 等前缀。

---

## 六、快速体验（本地一键概要流程）

1. **导入数据库**：在 MySQL 中执行 `quick_errand.sql`。
2. **启动 Redis**：确保本地 Redis 服务运行中。
3. **启动后端**：进入 `QuickErrand-backend`，配置好 `application.yml` 后执行 `mvn spring-boot:run`。
4. **启动管理后台**：进入 `QuickErrand-admin`，执行 `npm install && npm run dev`。
5. **启动小程序端**：用 HBuilderX 打开 `QuickErrand-app`，运行到微信开发者工具，并确保 API 地址指向本地后端。

按照以上步骤即可在本地完整体验 QuickErrand 跑腿系统的用户端、跑腿员端与管理后台功能。

---

## 七、默认账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | admin | admin123 |