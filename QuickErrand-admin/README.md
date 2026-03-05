# QuickErrand 管理后台

基于 Vue3 + Element Plus 的跑腿小程序管理后台系统。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4.15 | 前端框架 |
| Element Plus | 2.5.4 | UI组件库 |
| Vue Router | 4.2.5 | 路由管理 |
| Pinia | 2.1.7 | 状态管理 |
| Axios | 1.6.5 | HTTP客户端 |
| Vite | 5.0.11 | 构建工具 |
| Sass | 1.70.0 | CSS预处理 |
| ECharts | 5.4.3 | 数据可视化 |
| WangEditor | 5.1.23 | 富文本编辑器 |

## 项目结构

```
QuickErrand-admin/
├── src/
│   ├── api/                    # API接口模块
│   │   ├── admin.js            # 管理员接口
│   │   ├── user.js             # 用户接口
│   │   ├── order.js            # 订单接口
│   │   ├── orderType.js        # 订单类型接口
│   │   ├── runnerAuth.js       # 跑腿员认证接口
│   │   ├── review.js           # 评价接口
│   │   ├── announcement.js     # 公告接口
│   │   ├── banner.js           # 轮播图接口
│   │   ├── creditLevel.js      # 信用等级接口
│   │   ├── earnings.js         # 收益接口
│   │   ├── withdrawal.js       # 提现接口
│   │   ├── message.js          # 消息接口
│   │   ├── statistics.js       # 统计接口
│   │   └── file.js             # 文件上传接口
│   ├── router/                 # 路由配置
│   │   └── index.js            # 路由定义和守卫
│   ├── store/                  # 状态管理
│   │   └── user.js             # 用户状态
│   ├── utils/                  # 工具函数
│   │   └── request.js          # Axios封装
│   ├── components/             # 公共组件
│   │   └── PageHeader.vue      # 页面头部组件
│   ├── styles/                 # 样式文件
│   │   ├── ant-pro.css         # Ant Design Pro样式
│   │   └── theme.css           # 主题样式
│   ├── assets/                 # 静态资源
│   │   ├── logo.svg            # Logo
│   │   └── background.png      # 背景图
│   ├── views/                  # 页面组件
│   │   ├── Layout.vue          # 布局组件
│   │   ├── Login.vue           # 登录页面
│   │   ├── Dashboard.vue       # 数据看板
│   │   ├── user/
│   │   │   └── UserList.vue    # 用户管理
│   │   ├── order/
│   │   │   └── OrderList.vue   # 订单管理
│   │   ├── orderType/
│   │   │   └── OrderTypeList.vue # 订单类型管理
│   │   ├── runnerAuth/
│   │   │   └── RunnerAuthList.vue # 认证审核
│   │   ├── review/
│   │   │   └── ReviewList.vue  # 评价管理
│   │   ├── announcement/
│   │   │   └── AnnouncementList.vue # 公告管理
│   │   ├── banner/
│   │   │   └── BannerList.vue  # 轮播图管理
│   │   ├── creditLevel/
│   │   │   └── CreditLevelList.vue # 信用等级管理
│   │   ├── earnings/
│   │   │   └── EarningsList.vue # 收益结算管理
│   │   ├── withdrawal/
│   │   │   └── WithdrawalList.vue # 提现审核管理
│   │   ├── message/
│   │   │   └── MessageList.vue # 消息管理
│   │   └── profile/
│   │       └── Profile.vue     # 个人中心
│   ├── App.vue                 # 根组件
│   └── main.js                 # 入口文件
├── index.html                  # HTML模板
├── vite.config.js              # Vite配置
└── package.json                # 项目依赖
```

## 功能模块

### 已实现功能

#### 核心功能
- ✅ 管理员登录认证
- ✅ 路由权限守卫
- ✅ 响应式布局
- ✅ 用户状态管理
- ✅ HTTP请求拦截
- ✅ 退出登录

#### 数据看板
- ✅ 订单统计（今日订单数、待接单数、进行中数）
- ✅ 用户统计（总用户数、今日新增）
- ✅ 收益统计（今日收益、总收益）
- ✅ 跑腿员统计（总跑腿员数、已认证数）

#### 用户管理
- ✅ 用户列表查询
- ✅ 用户状态管理（启用/禁用）
- ✅ 用户详情查看

#### 订单管理
- ✅ 订单列表查询
- ✅ 订单详情查看
- ✅ 订单状态筛选

#### 订单类型管理
- ✅ 订单类型列表
- ✅ 新增/编辑/删除订单类型
- ✅ 状态管理（启用/禁用）

#### 认证审核
- ✅ 跑腿员认证申请列表
- ✅ 认证审核（通过/驳回）
- ✅ 认证详情查看

#### 评价管理
- ✅ 评价列表查询
- ✅ 评价详情查看

#### 信用等级管理
- ✅ 信用等级配置
- ✅ 等级规则设置

#### 公告管理
- ✅ 公告列表
- ✅ 新增/编辑/删除公告
- ✅ 公告发布/撤回
- ✅ 富文本编辑

#### 轮播图管理
- ✅ 轮播图列表
- ✅ 新增/编辑/删除轮播图
- ✅ 排序管理
- ✅ 状态管理

#### 提现审核管理
- ✅ 提现申请列表
- ✅ 提现审核（通过/驳回）
- ✅ 提现状态管理

#### 收益结算管理
- ✅ 收益记录列表
- ✅ 收益明细查看

#### 消息管理
- ✅ 消息列表
- ✅ 发送消息

#### 个人中心
- ✅ 管理员信息展示
- ✅ 信息修改

## 快速开始

### 1. 安装依赖

```bash
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

访问地址: http://localhost:3000

### 3. 构建生产版本

```bash
npm run build
```

### 4. 预览生产构建

```bash
npm run preview
```

## 配置说明

### API代理配置

在 `vite.config.js` 中配置了API代理，将 `/api` 请求转发到后端服务器：

```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8088',
    changeOrigin: true
  }
}
```

### 路由守卫

在 `src/router/index.js` 中实现了路由守卫：

- 未登录用户访问需要认证的页面会被重定向到登录页
- 已登录用户访问登录页会被重定向到首页

### 状态管理

使用 Pinia 进行状态管理，用户状态存储在 `src/store/user.js`：

- `token`: 用户令牌（存储在 localStorage）
- `userInfo`: 用户信息
- `setToken()`: 设置令牌
- `setUserInfo()`: 设置用户信息
- `logout()`: 退出登录

### HTTP请求

使用 Axios 进行HTTP请求，在 `src/utils/request.js` 中进行了封装：

- 自动添加 Authorization 请求头
- 统一处理响应错误
- 显示错误提示消息

## 默认账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | admin | admin123 |

**注意**: 请在生产环境中修改默认密码！

## 开发规范

### 代码风格

- 使用 Vue 3 Composition API
- 使用 `<script setup>` 语法
- 组件命名使用 PascalCase
- 文件命名使用 kebab-case

### 目录规范

- `api/` - API接口定义
- `views/` - 页面组件
- `components/` - 公共组件
- `utils/` - 工具函数
- `store/` - 状态管理
- `styles/` - 样式文件

### 提交规范

- feat: 新功能
- fix: 修复Bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 代码重构
- test: 测试相关
- chore: 构建过程或辅助工具的变动

## 浏览器支持

- Chrome >= 87
- Firefox >= 78
- Safari >= 14
- Edge >= 88

## 作者

周政

## 指导教师

吴换霞（副教授）
