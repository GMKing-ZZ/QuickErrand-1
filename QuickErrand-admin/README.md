# QuickErrand 管理后台

<div align="center">

![Version](https://img.shields.io/badge/版本-1.0.0-blue.svg)
![Vue](https://img.shields.io/badge/Vue-3.4.15-brightgreen.svg)
![Element Plus](https://img.shields.io/badge/Element%20Plus-2.5.4-409EFF.svg)
![Vite](https://img.shields.io/badge/Vite-5.0.11-646CFF.svg)

**基于 Vue 3 + Element Plus 的跑腿小程序管理后台系统**

[功能特性](#功能特性) | [快速开始](#快速开始) | [项目结构](#项目结构) | [部署指南](#部署指南)

</div>

---

## 📖 项目简介

QuickErrand 管理后台是基于 Vue 3 和 Element Plus 开发的现代化管理系统，为跑腿小程序提供完整的后台管理功能。系统采用响应式设计，支持数据可视化、富文本编辑等高级功能，帮助管理员高效管理平台运营。

## ✨ 功能特性

### 📊 数据看板

#### 核心统计
- **订单统计**
  - 今日订单数
  - 待接单数
  - 进行中订单数
  - 订单趋势图表
- **用户统计**
  - 总用户数
  - 今日新增用户
  - 用户增长趋势
- **收益统计**
  - 今日收益
  - 总收益
  - 收益趋势分析
- **跑腿员统计**
  - 总跑腿员数
  - 已认证跑腿员数
  - 活跃跑腿员统计

#### 数据可视化
- ECharts 图表展示
- 实时数据更新
- 多维度数据分析

### 👥 用户管理

#### 用户列表
- 用户信息查询
- 多条件筛选
- 分页展示
- 用户详情查看

#### 用户状态管理
- 启用/禁用用户
- 用户角色管理
- 用户行为记录

### 📝 订单管理

#### 订单列表
- 订单信息查询
- 订单状态筛选
- 订单详情查看
- 订单时间范围筛选

#### 订单操作
- 订单状态管理
- 订单详情查看
- 订单取消处理

### 📦 订单类型管理

#### 类型配置
- 订单类型列表
- 新增订单类型
- 编辑订单类型
- 删除订单类型

#### 状态管理
- 启用/禁用订单类型
- 类型排序
- 类型图标配置

### 🏃 跑腿员管理

#### 认证审核
- 认证申请列表
- 认证详情查看
- 认证审核（通过/驳回）
- 审核记录查询

#### 信用等级管理
- 等级配置
- 等级规则设置
- 等级权益管理

### ⭐ 评价管理

#### 评价列表
- 评价信息查询
- 评价详情查看
- 评价统计
- 评价筛选

### 📢 内容管理

#### 公告管理
- 公告列表
- 新增/编辑公告
- 富文本编辑
- 公告发布/撤回
- 公告状态管理

#### 轮播图管理
- 轮播图列表
- 新增/编辑轮播图
- 图片上传
- 排序管理
- 状态管理（启用/禁用）

### 💰 财务管理

#### 提现审核管理
- 提现申请列表
- 提现详情查看
- 提现审核（通过/驳回）
- 提现状态管理
- 提现记录查询

#### 收益结算管理
- 收益记录列表
- 收益明细查看
- 收益统计
- 收益筛选

### 💬 消息管理

#### 系统消息
- 消息列表
- 发送系统消息
- 消息状态管理
- 消息历史记录

### 👤 个人中心

#### 管理员信息
- 管理员信息展示
- 信息修改
- 密码修改
- 头像上传

## 🏗️ 技术架构

### 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4.15 | 渐进式 JavaScript 框架 |
| Element Plus | 2.5.4 | Vue 3 UI 组件库 |
| Vite | 5.0.11 | 下一代前端构建工具 |
| Vue Router | 4.2.5 | Vue.js 官方路由 |
| Pinia | 2.1.7 | Vue.js 状态管理库 |
| Axios | 1.6.5 | 基于 Promise 的 HTTP 客户端 |
| Sass | 1.70.0 | CSS 预处理器 |
| ECharts | 5.4.3 | 数据可视化图表库 |
| WangEditor | 5.1.23 | 富文本编辑器 |

### 架构特点

- **现代化框架**：使用 Vue 3 Composition API
- **组件化开发**：提高代码复用性
- **状态管理**：Pinia 集中式状态管理
- **路由守卫**：完善的权限控制
- **请求封装**：统一的 API 请求处理
- **响应式设计**：适配多种屏幕尺寸

## 📁 项目结构

```
QuickErrand-admin/
├── src/
│   ├── api/                    # API 接口模块
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
│   │
│   ├── router/                 # 路由配置
│   │   └── index.js            # 路由定义和守卫
│   │
│   ├── store/                  # 状态管理
│   │   └── user.js             # 用户状态
│   │
│   ├── utils/                  # 工具函数
│   │   └── request.js          # Axios 封装
│   │
│   ├── components/             # 公共组件
│   │   └── PageHeader.vue      # 页面头部组件
│   │
│   ├── styles/                 # 样式文件
│   │   ├── ant-pro.css         # Ant Design Pro 样式
│   │   └── theme.css           # 主题样式
│   │
│   ├── assets/                 # 静态资源
│   │   ├── logo.svg            # Logo
│   │   └── background.png      # 背景图
│   │
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
│   │
│   ├── App.vue                 # 根组件
│   └── main.js                 # 入口文件
│
├── index.html                  # HTML 模板
├── vite.config.js              # Vite 配置
├── package.json                # 项目依赖
└── .env.production             # 生产环境配置
```

## 🚀 快速开始

### 环境要求

- **Node.js**: 16+
- **npm**: 7+ 或 **yarn**: 1.22+

### 安装步骤

#### 1. 克隆项目

```bash
git clone https://github.com/yourusername/QuickErrand.git
cd QuickErrand/QuickErrand-admin
```

#### 2. 安装依赖

```bash
npm install
# 或
yarn install
```

#### 3. 配置后端地址

编辑 `vite.config.js` 文件，配置后端 API 地址：

```javascript
export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8088',  // 后端服务地址
        changeOrigin: true
      }
    }
  }
})
```

#### 4. 启动开发服务器

```bash
npm run dev
```

访问地址：`http://localhost:3000`

### 默认账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | admin | admin123 |

**⚠️ 注意**: 请在生产环境中修改默认密码！

## 📦 构建部署

### 构建生产版本

```bash
npm run build
# 或
yarn build
```

构建后的文件将生成在 `dist` 目录中。

### 预览生产构建

```bash
npm run preview
# 或
yarn preview
```

### 部署到服务器

#### Nginx 部署

1. 构建项目：
```bash
npm run build
```

2. 上传 `dist` 目录到服务器

3. 配置 Nginx：
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    location / {
        root /path/to/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://localhost:8088;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

4. 重启 Nginx：
```bash
nginx -s reload
```

## 🔧 配置说明

### API 代理配置

在 `vite.config.js` 中配置了 API 代理：

```javascript
export default defineConfig({
  server: {
    port: 3000,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8088',
        changeOrigin: true
      }
    }
  }
})
```

### 路由守卫

在 `src/router/index.js` 中实现了路由守卫：

- 未登录用户访问需要认证的页面会被重定向到登录页
- 已登录用户访问登录页会被重定向到首页
- 自动验证 Token 有效性

### 状态管理

使用 Pinia 进行状态管理，用户状态存储在 `src/store/user.js`：

```javascript
export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}')
  }),
  
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },
    
    setUserInfo(userInfo) {
      this.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },
    
    logout() {
      this.token = ''
      this.userInfo = {}
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
})
```

### HTTP 请求封装

使用 Axios 进行 HTTP 请求，在 `src/utils/request.js` 中进行了封装：

```javascript
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          const userStore = useUserStore()
          userStore.logout()
          router.push('/login')
          break
        case 403:
          ElMessage.error('没有权限访问')
          break
        case 404:
          ElMessage.error('请求资源不存在')
          break
        case 500:
          ElMessage.error('服务器错误')
          break
        default:
          ElMessage.error(error.response.data.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

export default request
```

### 构建优化

在 `vite.config.js` 中配置了构建优化：

```javascript
export default defineConfig({
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          'element-plus': ['element-plus'],
          'vue-vendor': ['vue', 'vue-router', 'pinia'],
          'echarts': ['echarts'],
          'wangeditor': ['@wangeditor/editor', '@wangeditor/editor-for-vue']
        }
      }
    },
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: true,
        drop_debugger: true
      }
    },
    chunkSizeWarningLimit: 1000
  }
})
```

## 🎨 设计规范

### UI 设计

- **组件库**：Element Plus
- **布局方式**：Flex + Grid
- **响应式设计**：适配多种屏幕尺寸
- **主题色**：Element Plus 默认蓝色主题

### 代码规范

- 使用 Vue 3 Composition API
- 使用 `<script setup>` 语法
- 组件命名使用 PascalCase
- 文件命名使用 kebab-case
- 使用 ESLint + Prettier 统一格式

### 目录规范

- `api/` - API 接口定义
- `views/` - 页面组件
- `components/` - 公共组件
- `utils/` - 工具函数
- `store/` - 状态管理
- `styles/` - 样式文件

## 🌐 浏览器支持

| 浏览器 | 版本 |
|--------|------|
| Chrome | >= 87 |
| Firefox | >= 78 |
| Safari | >= 14 |
| Edge | >= 88 |

## 📝 开发规范

### Git 提交规范

- `feat`: 新功能
- `fix`: 修复 Bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建/工具变动

### 代码风格

- 使用 2 空格缩进
- 使用单引号
- 语句末尾不加分号
- 使用 ES6+ 语法

## 🐛 常见问题

### 1. 安装依赖失败

**问题**：npm install 失败

**解决**：
```bash
# 清除缓存
npm cache clean --force

# 使用淘宝镜像
npm install --registry=https://registry.npmmirror.com
```

### 2. 启动失败

**问题**：npm run dev 启动失败

**解决**：
- 检查 Node.js 版本是否 >= 16
- 检查端口 3000 是否被占用
- 删除 node_modules 重新安装

### 3. API 请求失败

**问题**：接口请求返回 404 或 500

**解决**：
- 检查后端服务是否启动
- 检查 vite.config.js 中的代理配置
- 检查接口地址是否正确

### 4. 登录后跳转失败

**问题**：登录成功但没有跳转到首页

**解决**：
- 检查路由配置
- 检查 localStorage 中是否保存了 token
- 检查路由守卫逻辑

### 5. 样式不生效

**问题**：修改样式后没有生效

**解决**：
- 清除浏览器缓存
- 检查样式优先级
- 使用 scoped 样式

## 📚 相关文档

- [Vue 3 官方文档](https://vuejs.org/)
- [Element Plus 官方文档](https://element-plus.org/)
- [Vite 官方文档](https://vitejs.dev/)
- [Pinia 官方文档](https://pinia.vuejs.org/)
- [ECharts 官方文档](https://echarts.apache.org/)

## 👥 作者

**周政**

指导教师：**吴换霞**（副教授）

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](../LICENSE) 文件

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给一个 Star ⭐**

</div>
