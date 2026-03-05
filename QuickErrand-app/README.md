# QuickErrand 小程序端

基于 uni-app x 开发的跑腿小程序，支持微信小程序、App 等多端运行。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| uni-app x | - | 跨平台框架 |
| Vue | 3.x | 前端框架 |
| UTS | - | TypeScript超集 |
| uni-pay-x | - | 支付模块 |

## 项目结构

```
QuickErrand-app/
├── common/                     # 公共模块
│   ├── api/                    # API接口
│   │   ├── user.uts            # 用户接口
│   │   ├── order.uts           # 订单接口
│   │   ├── runner.uts          # 跑腿员接口
│   │   ├── address.uts         # 地址接口
│   │   ├── chat.uts            # 聊天接口
│   │   ├── message.uts         # 消息接口
│   │   ├── announcement.uts    # 公告接口
│   │   └── banner.uts          # 轮播图接口
│   ├── config/                 # 配置
│   │   └── map.uts             # 地图配置
│   └── utils/                  # 工具函数
│       ├── request.uts         # 请求封装
│       └── common.uts          # 公共工具
├── components/                 # 公共组件
│   ├── NavBar/                 # 导航栏组件
│   ├── Empty/                  # 空状态组件
│   ├── Loading/                # 加载组件
│   └── RunnerTabBar.uvue       # 跑腿员底部导航
├── pages/                      # 主包页面
│   ├── home/
│   │   └── index.uvue          # 首页
│   ├── order/
│   │   └── list.uvue           # 订单列表
│   ├── service/
│   │   └── index.uvue          # 服务页
│   ├── mine/
│   │   └── mine.uvue           # 我的
│   ├── runner/                 # 跑腿员页面
│   │   ├── home.uvue           # 跑腿员首页
│   │   ├── profile.uvue        # 跑腿员中心
│   │   ├── auth-apply.uvue     # 认证申请
│   │   ├── auth-status.uvue    # 认证状态
│   │   ├── pending-orders.uvue # 待接订单
│   │   ├── active-orders.uvue  # 进行中订单
│   │   ├── earnings.uvue       # 我的收益
│   │   ├── earnings-details.uvue # 收益明细
│   │   ├── withdrawal.uvue     # 提现
│   │   ├── withdrawal-records.uvue # 提现记录
│   │   ├── my-reviews.uvue     # 用户评价
│   │   ├── edit-profile.uvue   # 编辑资料
│   │   └── confirm-receive.uvue # 确认收货
│   └── common/                 # 公共页面
│       ├── privacy.uvue        # 隐私政策
│       ├── user-agreement.uvue # 用户协议
│       └── webview.uvue        # 网页容器
├── subpkg/                     # 分包页面
│   ├── login.uvue              # 登录
│   ├── register.uvue           # 注册
│   ├── register-type.uvue      # 选择注册类型
│   ├── forgot-password.uvue    # 忘记密码
│   ├── edit-profile.uvue       # 编辑资料
│   ├── address-list.uvue       # 地址列表
│   ├── address-edit.uvue       # 编辑地址
│   ├── map-choose.uvue         # 地图选点
│   ├── order-create.uvue       # 发布订单
│   ├── order-detail.uvue       # 订单详情
│   ├── order-evaluate.uvue     # 订单评价
│   ├── my-reviews.uvue         # 我的评价
│   ├── message-list.uvue       # 消息列表
│   ├── chat-list.uvue          # 聊天消息列表
│   ├── chat-session.uvue       # 聊天会话
│   ├── contact-conversations.uvue # 订单聊天
│   └── runner-profile.uvue     # 跑腿员信息
├── store/                      # 状态管理
│   ├── index.uts               # Store入口
│   ├── user.uts                # 用户状态
│   ├── order.uts               # 订单状态
│   ├── address.uts             # 地址状态
│   └── message.uts             # 消息状态
├── static/                     # 静态资源
│   ├── icons/                  # 图标
│   ├── iconfont/               # 字体图标
│   └── fonts/                  # 字体文件
├── uni_modules/                # uni-app插件
│   ├── uni-pay-x/              # 支付模块
│   ├── uni-icons/              # 图标组件
│   ├── uni-popup/              # 弹窗组件
│   ├── uni-nav-bar/            # 导航栏
│   ├── uni-easyinput/          # 输入框
│   ├── uni-forms/              # 表单
│   ├── uni-load-more/          # 加载更多
│   └── ...                     # 其他组件
├── wxcomponents/               # 微信小程序组件
│   └── vant/                   # Vant组件
├── App.uvue                    # 应用入口
├── main.uts                    # 主入口
├── pages.json                  # 页面配置
├── manifest.json               # 应用配置
├── env.js                      # 环境配置
└── uni.scss                    # 全局样式
```

## 功能模块

### 用户端功能

#### 首页
- 服务类型选择（代买、代送、代取、代办、代寄）
- Banner 轮播展示
- 公告通知展示
- 快捷入口

#### 订单功能
- 发布订单：选择订单类型、填写取送地址、物品描述、费用预估
- 订单列表：全部/待支付/待接单/服务中/已完成/已取消
- 订单详情：订单信息、配送进度、费用明细
- 订单评价：服务质量评分、服务态度评分、评价内容、图片上传
- 订单取消

#### 地址管理
- 地址列表
- 新增/编辑地址
- 设置默认地址
- 地图选点

#### 个人中心
- 用户信息展示
- 我的订单
- 我的评价
- 消息通知
- 聊天消息
- 地址管理
- 账号设置

#### 登录注册
- 账号密码登录
- 微信登录
- 账号注册
- 忘记密码

### 跑腿员端功能

#### 跑腿员首页
- 今日收益统计
- 服务统计（完成订单数、好评率）
- 快捷操作入口
- 待办提醒

#### 订单管理
- 待接订单：订单列表、筛选、接单
- 进行中订单：取货确认、配送完成
- 订单详情

#### 收益管理
- 收益统计（今日收益、总收益、可提现金额）
- 收益明细
- 提现申请
- 提现记录

#### 跑腿员认证
- 认证申请（身份信息、身份证照片）
- 认证状态查看

#### 个人中心
- 跑腿员信息
- 用户评价
- 编辑资料
- 服务设置

### 聊天功能

- 订单关联聊天会话
- 实时消息收发（WebSocket）
- 未读消息提醒
- 消息列表管理

## 页面路由

### 主包页面 (pages)

| 路径 | 说明 |
|------|------|
| /pages/home/index | 首页 |
| /pages/service/index | 服务页 |
| /pages/order/list | 订单列表 |
| /pages/mine/mine | 我的 |
| /pages/runner/home | 跑腿员首页 |
| /pages/runner/profile | 跑腿员中心 |
| /pages/runner/auth-apply | 认证申请 |
| /pages/runner/auth-status | 认证状态 |
| /pages/runner/pending-orders | 待接订单 |
| /pages/runner/active-orders | 进行中订单 |
| /pages/runner/earnings | 我的收益 |
| /pages/runner/withdrawal | 提现 |
| /pages/runner/my-reviews | 用户评价 |

### 分包页面 (subpkg)

| 路径 | 说明 |
|------|------|
| /subpkg/login | 登录 |
| /subpkg/register | 注册 |
| /subpkg/register-type | 选择注册类型 |
| /subpkg/forgot-password | 忘记密码 |
| /subpkg/edit-profile | 编辑资料 |
| /subpkg/address-list | 地址列表 |
| /subpkg/address-edit | 编辑地址 |
| /subpkg/map-choose | 地图选点 |
| /subpkg/order-create | 发布订单 |
| /subpkg/order-detail | 订单详情 |
| /subpkg/order-evaluate | 订单评价 |
| /subpkg/my-reviews | 我的评价 |
| /subpkg/message-list | 消息列表 |
| /subpkg/chat-list | 聊天消息列表 |
| /subpkg/chat-session | 聊天会话 |

## 运行说明

### 1. 环境准备

- 安装 HBuilderX（推荐最新版）
- 安装微信开发者工具
- 配置微信小程序 AppID

### 2. 配置项目

1. 使用 HBuilderX 打开项目目录
2. 修改 `manifest.json` 中的小程序 AppID
3. 修改 `env.js` 中的 API 地址：
```javascript
export const API_BASE_URL = 'http://localhost:8088/api'
```

### 3. 运行项目

#### 微信小程序
1. 点击 HBuilderX 菜单：运行 -> 运行到小程序模拟器 -> 微信开发者工具
2. 首次运行需要配置微信开发者工具路径

#### App
1. 点击 HBuilderX 菜单：运行 -> 运行到手机或模拟器

### 4. 发布项目

#### 微信小程序
1. 点击 HBuilderX 菜单：发行 -> 小程序-微信
2. 在微信开发者工具中上传代码
3. 在微信公众平台提交审核

## 配置说明

### API 地址配置

在 `env.js` 中配置：
```javascript
export const API_BASE_URL = 'http://localhost:8088/api'
```

### 地图配置

在 `common/config/map.uts` 中配置地图服务商：
```typescript
export const MAP_CONFIG = {
  provider: 'tencent',  // 腾讯地图
  key: 'your_map_key'
}
```

### 分包配置

在 `pages.json` 中配置分包：
```json
{
  "optimization": {
    "subPackages": true
  },
  "subPackages": [
    {
      "root": "subpkg",
      "pages": [...]
    },
    {
      "root": "pages/runner",
      "pages": [...]
    }
  ]
}
```

## 注意事项

- 项目基于 uni-app x 开发，需要 HBuilderX 3.99+ 版本
- 微信小程序需要在微信公众平台配置合法域名
- WebSocket 连接需要在微信公众平台配置 socket 合法域名
- 地图功能需要申请对应的地图服务商 Key

## 作者

周政

## 指导教师

吴换霞（副教授）
