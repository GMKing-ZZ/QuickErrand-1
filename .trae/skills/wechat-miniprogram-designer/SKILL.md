---
name: "wechat-miniprogram-designer"
description: "设计和实现基于uni-app x的微信小程序前端页面，遵循项目现有代码规范和设计风格。Invoke when user asks to design or create new WeChat Mini Program pages."
---

# 微信小程序前端页面设计器

## 项目概述

本项目是基于 **uni-app x** 开发的跑腿小程序（QuickErrand），支持微信小程序、App 等多端运行。

## 技术栈

- **uni-app x**: 跨平台框架
- **Vue 3.x**: 前端框架
- **UTS**: TypeScript超集
- **uni-pay-x**: 支付模块
- **Vant**: UI组件库

## 项目结构

```
QuickErrand-app/
├── common/          # 公共模块
│   ├── api/        # API接口
│   ├── config/     # 配置
│   └── utils/      # 工具函数
├── components/      # 公共组件
├── pages/          # 主包页面
├── subpkg/         # 分包页面
├── store/          # 状态管理
├── static/         # 静态资源
└── wxcomponents/   # 微信小程序组件
```

## 设计规范

### 1. 页面文件格式

使用 `.uvue` 文件格式，包含三个部分：
- `<template>`: 模板
- `<script>`: 脚本（使用Options API）
- `<style>`: 样式（scoped）

### 2. 代码风格

**颜色规范：**
- 主色调：`#2563eb`, `#3b82f6`, `#60a5fa`（蓝色系）
- 背景色：`#f9fafb`
- 文字色：`#1f2937`（深灰）, `#6b7280`（中灰）
- 成功色：`#10b981`
- 警告色：`#f59e0b`
- 错误色：`#ef4444`

**圆角规范：**
- 卡片：`24rpx`
- 按钮：`50rpx`
- 搜索框：`50rpx`

**间距规范：**
- 页面边距：`30rpx`
- 卡片间距：`24rpx`
- 元素间距：`12rpx`, `16rpx`, `20rpx`, `30rpx`

**字体大小：**
- 大标题：`40rpx`, `44rpx`
- 小标题：`32rpx`
- 正文：`28rpx`
- 小字：`24rpx`, `26rpx`

### 3. 页面配置

在 `pages.json` 中配置页面路由：

```json
{
  "path": "pages/xxx/xxx",
  "style": {
    "navigationBarTitleText": "页面标题"
  }
}
```

## 常用组件模式

### 页面头部

```vue
<view class="page-header">
  <text class="page-title">标题</text>
  <text class="page-subtitle">副标题</text>
</view>
```

### 卡片组件

```vue
<view class="card">
  <view class="card-header">
    <text class="card-title">卡片标题</text>
  </view>
  <view class="card-content">
    <!-- 内容 -->
  </view>
</view>
```

### 按钮组件

```vue
<view class="btn primary-btn" @click="handleClick">
  <text class="btn-text">按钮文字</text>
</view>
```

### 空状态

```vue
<view class="empty-state">
  <text class="empty-icon">📦</text>
  <text class="empty-text">暂无数据</text>
</view>
```

### 加载状态

```vue
<view class="loading-state">
  <text class="loading-text">加载中...</text>
</view>
```

## API调用规范

从 `@/common/api/` 导入API：

```typescript
import { getOrderTypes } from '@/common/api/order.uts'
import type { OrderTypeInfo } from '@/common/api/order.uts'
```

## 页面生命周期

```vue
<script>
export default {
  data() {
    return {
      // 数据
    }
  },
  onLoad(options) {
    // 页面加载
  },
  onShow() {
    // 页面显示
  },
  methods: {
    // 方法
  }
}
</script>
```

## 导航跳转

```typescript
// 跳转新页面
uni.navigateTo({
  url: '/subpkg/xxx'
})

// 切换TabBar页面
uni.switchTab({
  url: '/pages/home/index'
})

// 重定向
uni.reLaunch({
  url: '/pages/xxx'
})
```

## 样式模板

### 基础页面样式

```scss
.page-container {
  min-height: 100vh;
  background: #f9fafb;
  padding: 30rpx;
}

.section-header {
  margin-bottom: 30rpx;
}

.section-title {
  font-size: 40rpx;
  font-weight: 700;
  color: #1f2937;
  display: block;
  margin-bottom: 12rpx;
}

.section-subtitle {
  font-size: 26rpx;
  color: #6b7280;
}
```

### 卡片样式

```scss
.card {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
  border: 1rpx solid rgba(37, 99, 235, 0.08);
}
```

### 按钮样式

```scss
.primary-btn {
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%);
  border-radius: 50rpx;
  padding: 24rpx 60rpx;
  box-shadow: 0 4rpx 16rpx rgba(37, 99, 235, 0.25);
}

.primary-btn:active {
  transform: scale(0.95);
}

.btn-text {
  font-size: 30rpx;
  color: #ffffff;
  font-weight: 600;
}
```

## 创建新页面步骤

1. **创建页面文件**：在 `pages/` 或 `subpkg/` 目录下创建 `.uvue` 文件
2. **配置路由**：在 `pages.json` 中添加页面配置
3. **实现页面**：遵循现有代码风格和设计规范
4. **添加样式**：使用统一的设计语言

## 参考页面

- 首页：`pages/home/index.uvue`
- 订单创建：`subpkg/order-create.uvue`
- 我的：`pages/mine/mine.uvue`

## 注意事项

1. 使用 UTS 语法，注意类型声明
2. 遵循 uni-app x 的组件规范
3. 保持与现有页面风格一致
4. 响应式设计，适配不同屏幕
5. 不添加冗余注释
