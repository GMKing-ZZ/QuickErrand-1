# QuickErrand 小程序端

<div align="center">

![Version](https://img.shields.io/badge/版本-1.9.8-blue.svg)
![Platform](https://img.shields.io/badge/平台-微信小程序%20%7C%20App%20%7C%20H5%20%7C%20鸿蒙-lightgrey.svg)
![HBuilderX](https://img.shields.io/badge/HBuilderX-3.99+-green.svg)

**基于 uni-app x 的多端跑腿小程序**

[功能特性](#功能特性) | [快速开始](#快速开始) | [项目结构](#项目结构) | [部署指南](#部署指南)

</div>

---

## 📖 项目简介

QuickErrand 小程序端是基于 uni-app x 框架开发的多端应用，支持微信小程序、Android App、iOS App、H5 网页和鸿蒙应用等多个平台。项目采用 Vue 3 + UTS 开发，实现了用户端和跑腿员端双端功能，提供完整的跑腿服务体验。

## ✨ 功能特性

### 用户端功能

#### 🏠 首页模块
- **服务类型选择**：代买、代送、代取、代办、代寄等多种服务类型
- **Banner 轮播**：展示平台活动和公告
- **公告通知**：实时显示平台公告信息
- **智能搜索**：快速搜索服务类型和订单

#### 📦 订单管理
- **发布订单**
  - 选择订单类型
  - 填写取送地址（支持地图选点）
  - 物品描述和图片上传（最多9张）
  - 费用预估（配送费、平台服务费）
  - 订单提交和支付
- **订单列表**
  - 多状态筛选（全部、待支付、待接单、服务中、已完成、已取消）
  - 下拉刷新、上拉加载
  - 订单卡片展示
- **订单详情**
  - 实时状态跟踪
  - 收货码显示
  - 费用明细
  - 跑腿员信息
  - 操作按钮（支付、取消、联系、确认收货、评价）
- **订单评价**
  - 星级评分（服务质量、服务态度）
  - 文字评价
  - 图片上传

#### 📍 地址管理
- 地址列表展示
- 新增/编辑地址
- 地图选点功能
- 设置默认地址
- 省市区三级联动

#### 👤 个人中心
- 用户信息展示与编辑
- 我的订单
- 我的评价
- 消息通知
- 聊天消息
- 地址管理
- 修改密码
- 退出登录

#### 🔐 认证授权
- 账号密码登录
- 微信一键登录
- 手机号注册
- 忘记密码
- 用户协议与隐私政策

### 跑腿员端功能

#### 🏃 跑腿员首页
- **收益统计**
  - 今日收益
  - 本月收益
  - 累计收益
- **服务统计**
  - 完成订单量
  - 跑腿信用
  - 好评率
  - 服务时长
- **快捷操作**
  - 待接订单
  - 我的收益
  - 提现
  - 跑腿员中心
- **平台公告**

#### 📋 订单管理
- **待接订单**
  - 订单列表展示
  - 智能筛选
  - 一键接单
  - 订单详情查看
- **进行中订单**
  - 取货确认
  - 配送完成
  - 订单状态更新
- **订单历史**
  - 多维度筛选
  - 时间排序
  - 金额排序
  - 距离排序

#### 💰 收益管理
- **收益统计**
  - 今日收益
  - 本月收益
  - 可提现金额
- **收益明细**
  - 收益记录列表
  - 筛选和排序
  - 收益详情
- **提现功能**
  - 提现申请
  - 提现方式选择（银行卡、微信、支付宝）
  - 提现记录查询

#### 🎖️ 认证中心
- 跑腿员认证申请
- 身份证照片上传
- 认证状态查询
- 资料编辑

#### 👥 用户评价
- 查看用户评价
- 评分统计
- 评价内容查看

### 聊天功能

- 订单关联聊天会话
- 实时消息收发
- 未读消息提醒
- 消息列表管理

## 🏗️ 技术架构

### 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| uni-app x | - | 跨平台开发框架 |
| Vue | 3.x | 渐进式 JavaScript 框架 |
| UTS | - | TypeScript 超集，用于原生开发 |
| uni-pay-x | - | 统一支付模块 |
| 高德地图 | - | 地图定位服务 |
| 微信支付 | - | 微信支付功能 |
| 支付宝支付 | - | 支付宝支付功能 |

### 支持平台

| 平台 | 状态 | 说明 |
|------|------|------|
| 微信小程序 | ✅ 支持 | 主要目标平台 |
| Android App | ✅ 支持 | 原生应用 |
| iOS App | ✅ 支持 | 原生应用 |
| H5 网页 | ✅ 支持 | 网页应用 |
| 鸿蒙应用 | ✅ 支持 | HarmonyOS 应用 |

### 架构特点

- **分包加载**：优化小程序包体积，提升加载速度
- **组件化开发**：提高代码复用性和可维护性
- **状态管理**：使用 Pinia 进行全局状态管理
- **API 封装**：统一请求封装，便于接口管理
- **地图集成**：高德地图提供定位和导航服务

## 📁 项目结构

```
QuickErrand-app/
├── common/                     # 公共模块
│   ├── api/                    # API 接口
│   │   ├── user.uts            # 用户接口
│   │   ├── order.uts           # 订单接口
│   │   ├── runner.uts          # 跑腿员接口
│   │   ├── address.uts         # 地址接口
│   │   ├── chat.uts            # 聊天接口
│   │   ├── message.uts         # 消息接口
│   │   ├── announcement.uts    # 公告接口
│   │   └── banner.uts          # 轮播图接口
│   └── utils/                  # 工具函数
│       ├── request.uts         # 请求封装
│       └── common.uts          # 公共工具
│
├── components/                 # 公共组件
│   ├── NavBar/                 # 导航栏组件
│   ├── Empty/                  # 空状态组件
│   ├── Loading/                # 加载组件
│   └── RunnerTabBar.uvue       # 跑腿员底部导航
│
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
│
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
│
├── store/                      # 状态管理
│   ├── index.uts               # Store 入口
│   ├── user.uts                # 用户状态
│   ├── order.uts               # 订单状态
│   ├── address.uts             # 地址状态
│   └── message.uts             # 消息状态
│
├── static/                     # 静态资源
│   ├── icons/                  # 图标
│   ├── iconfont/               # 字体图标
│   └── fonts/                  # 字体文件
│
├── uni_modules/                # uni-app 插件
│   ├── uni-pay-x/              # 支付模块
│   ├── uni-icons/              # 图标组件
│   ├── uni-popup/              # 弹窗组件
│   ├── uni-nav-bar/            # 导航栏
│   ├── uni-easyinput/          # 输入框
│   ├── uni-forms/              # 表单
│   └── ...                     # 其他组件
│
├── wxcomponents/               # 微信小程序组件
│   └── vant/                   # Vant 组件
│
├── uniCloud-alipay/            # 云开发（可选）
│   ├── cloudfunctions/         # 云函数
│   └── database/               # 数据库
│
├── App.uvue                    # 应用入口
├── main.uts                    # 主入口
├── pages.json                  # 页面配置
├── manifest.json               # 应用配置
├── env.js                      # 环境配置
└── uni.scss                    # 全局样式
```

## 🚀 快速开始

### 环境要求

- **HBuilderX**: 3.99+ 版本
- **微信开发者工具**: 最新稳定版
- **Node.js**: 16+ （用于部分依赖安装）
- **微信小程序 AppID**: 需要在微信公众平台申请

### 安装步骤

#### 1. 克隆项目

```bash
git clone https://github.com/yourusername/QuickErrand.git
cd QuickErrand/QuickErrand-app
```

#### 2. 配置项目

##### 修改小程序 AppID

编辑 `manifest.json` 文件：

```json
{
  "mp-weixin": {
    "appid": "your_weixin_appid"
  }
}
```

##### 配置 API 地址

编辑 `common/utils/request.uts` 文件：

```typescript
const BASE_URL = 'http://localhost:8088/api'
```

##### 配置地图 Key

编辑 `manifest.json` 文件：

```json
{
  "app": {
    "distribute": {
      "modules": {
        "uni-map": {
          "amap": {}
        }
      }
    }
  },
  "web": {
    "sdkConfigs": {
      "maps": {
        "amap": {
          "key": "your_amap_key"
        }
      }
    }
  }
}
```

#### 3. 运行项目

##### 微信小程序

1. 打开 HBuilderX
2. 导入项目：文件 -> 导入 -> 从本地目录导入
3. 运行：运行 -> 运行到小程序模拟器 -> 微信开发者工具
4. 首次运行需要配置微信开发者工具路径

##### Android App

1. 运行：运行 -> 运行到手机或模拟器 -> 运行到 Android App 基座
2. 选择连接的 Android 设备或模拟器

##### iOS App

1. 运行：运行 -> 运行到手机或模拟器 -> 运行到 iOS 模拟器
2. 需要配置 iOS 证书

##### H5 网页

1. 运行：运行 -> 运行到浏览器 -> Chrome
2. 自动打开浏览器访问

## 📱 页面路由

### 主包页面 (pages)

| 路径 | 说明 | Tab |
|------|------|-----|
| /pages/home/index | 首页 | ✅ |
| /pages/service/index | 服务页 | ✅ |
| /pages/order/list | 订单列表 | ✅ |
| /pages/mine/mine | 我的 | ✅ |
| /pages/common/privacy | 隐私政策 | - |
| /pages/common/user-agreement | 用户协议 | - |
| /pages/common/webview | 网页容器 | - |

### 跑腿员页面 (pages/runner)

| 路径 | 说明 |
|------|------|
| /pages/runner/home | 跑腿员首页 |
| /pages/runner/profile | 跑腿员中心 |
| /pages/runner/auth-apply | 认证申请 |
| /pages/runner/auth-status | 认证状态 |
| /pages/runner/pending-orders | 待接订单 |
| /pages/runner/active-orders | 进行中订单 |
| /pages/runner/earnings | 我的收益 |
| /pages/runner/earnings-details | 收益明细 |
| /pages/runner/withdrawal | 提现 |
| /pages/runner/withdrawal-records | 提现记录 |
| /pages/runner/my-reviews | 用户评价 |
| /pages/runner/edit-profile | 编辑资料 |
| /pages/runner/confirm-receive | 确认收货 |

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
| /subpkg/contact-conversations | 订单聊天 |
| /subpkg/runner-profile | 跑腿员信息 |
| /subpkg/change-password | 修改密码 |

## 🔧 配置说明

### 分包配置

项目采用分包加载策略，优化小程序包体积：

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

### 支付配置

#### 微信支付

在 `manifest.json` 中配置：

```json
{
  "app": {
    "distribute": {
      "modules": {
        "uni-payment": {
          "wxpay": {
            "android": {},
            "ios": {
              "appid": "your_appid",
              "universalLink": "your_universal_link"
            }
          }
        }
      }
    }
  }
}
```

#### 支付宝支付

```json
{
  "app": {
    "distribute": {
      "modules": {
        "uni-payment": {
          "alipay": {}
        }
      }
    }
  }
}
```

### 地图配置

项目使用高德地图提供定位和导航服务：

```json
{
  "app": {
    "distribute": {
      "modules": {
        "uni-map": {
          "amap": {}
        }
      }
    }
  }
}
```

### 权限配置

#### 微信小程序权限

```json
{
  "mp-weixin": {
    "requiredPrivateInfos": [
      "getLocation",
      "onLocationChange",
      "startLocationUpdateBackground",
      "chooseLocation"
    ],
    "permission": {
      "scope.userLocation": {
        "desc": "你的位置信息将用于小程序位置接口的效果展示"
      }
    }
  }
}
```

## 🌐 部署指南

### 微信小程序部署

#### 1. 配置服务器域名

在微信公众平台配置以下域名：

**request 合法域名**：
- `https://your-api-domain.com`

**uploadFile 合法域名**：
- `https://your-upload-domain.com`

**downloadFile 合法域名**：
- `https://your-download-domain.com`

#### 2. 上传代码

1. 在 HBuilderX 中：发行 -> 小程序-微信
2. 在微信开发者工具中点击"上传"
3. 填写版本号和备注

#### 3. 提交审核

1. 登录微信公众平台
2. 进入版本管理
3. 提交审核

#### 4. 发布上线

审核通过后，点击"发布"即可上线

### App 部署

#### Android

1. 在 HBuilderX 中：发行 -> 原生 App-云打包
2. 配置证书和应用信息
3. 生成 APK 文件
4. 上传到应用市场

#### iOS

1. 在 HBuilderX 中：发行 -> 原生 App-云打包
2. 配置证书和描述文件
3. 生成 IPA 文件
4. 上传到 App Store

### 云开发部署

项目支持腾讯云开发 CloudBase 部署，详见 [DEPLOYMENT.md](./DEPLOYMENT.md)

## 🎨 设计规范

### 颜色方案

- **主色调**：蓝色渐变 (#2563eb → #3b82f6 → #60a5fa)
- **辅助色**：
  - 绿色 (#10b981, #34d399) - 成功状态、收益
  - 橙色 (#f97316, #fb923c) - 订单、警告
- **背景色**：白色卡片 + 渐变背景

### UI 特点

- 卡片式设计
- 圆角按钮（50rpx）
- 渐变背景和按钮
- 图标 + 文字组合
- 清晰的信息层次
- 状态标签颜色区分

### 交互特点

- 下拉刷新
- 上拉加载更多
- 状态标签区分
- 操作按钮明确
- 空状态提示
- 加载状态提示

## 📝 开发规范

### 代码规范

- 使用 Vue 3 Composition API
- 使用 `<script setup>` 语法
- 组件命名使用 PascalCase
- 文件命名使用 kebab-case
- 使用 UTS 进行原生开发

### 目录规范

- `pages/` - 主包页面
- `subpkg/` - 分包页面
- `components/` - 公共组件
- `common/api/` - API 接口
- `common/utils/` - 工具函数
- `store/` - 状态管理
- `static/` - 静态资源

### Git 提交规范

- `feat`: 新功能
- `fix`: 修复 Bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建/工具变动

## 🐛 常见问题

### 1. HBuilderX 版本过低

**问题**：项目无法运行或编译报错

**解决**：升级 HBuilderX 到 3.99+ 版本

### 2. 微信小程序 AppID 未配置

**问题**：无法在微信开发者工具中预览

**解决**：在 `manifest.json` 中配置正确的 AppID

### 3. API 地址配置错误

**问题**：接口请求失败

**解决**：检查 `common/utils/request.uts` 中的 BASE_URL 配置

### 4. 地图功能无法使用

**问题**：定位或地图选点失败

**解决**：
- 检查高德地图 Key 是否配置
- 确认小程序后台是否配置地图服务商

### 5. 支付功能无法使用

**问题**：支付失败或无法调起支付

**解决**：
- 检查支付配置是否正确
- 确认商户号和密钥配置
- 检查支付权限是否开通

## 📚 相关文档

- [uni-app x 官方文档](https://uniapp.dcloud.net.cn/)
- [微信小程序开发文档](https://developers.weixin.qq.com/miniprogram/dev/framework/)
- [高德地图开发文档](https://lbs.amap.com/api/javascript-api/summary)
- [Vue 3 官方文档](https://vuejs.org/)

## 👥 作者

**周政**

指导教师：**吴换霞**（副教授）

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](../LICENSE) 文件

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给一个 Star ⭐**

</div>
