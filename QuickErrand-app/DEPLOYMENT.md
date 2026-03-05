# QuickErrand 小程序 CloudBase 部署指南

## 📋 部署概览

本项目已成功部署到腾讯云开发 CloudBase 环境，以下是详细的部署信息和配置说明。

## 🌐 环境信息

- **环境ID**: `cloudbase-4g1j4fs6cf7298bb`
- **环境别名**: cloudbase
- **区域**: ap-shanghai (上海)
- **套餐**: 个人版
- **状态**: 正常运行

## 📦 已部署资源

### 1. 数据库表 (13个)

| 表名 | 说明 |
|------|------|
| t_user | 用户表 |
| t_address | 地址表 |
| t_order_type | 订单类型表 |
| t_order | 订单表 |
| t_runner_info | 跑腿员信息表 |
| t_earnings_record | 收益记录表 |
| t_withdrawal_record | 提现记录表 |
| t_evaluation | 评价表 |
| t_message | 消息通知表 |
| t_chat_message | 聊天消息表 |
| t_chat_order_rel | 订单-聊天绑定表 |
| t_announcement | 公告表 |
| t_banner | 轮播图表 |

### 2. 云函数 (5个)

| 云函数名 | 功能 | 状态 |
|---------|------|------|
| user-service | 用户注册、登录、信息管理 | ✅ Active |
| order-service | 订单创建、支付、管理 | ✅ Active |
| runner-service | 跑腿员认证、接单、收益 | ✅ Active |
| message-service | 聊天消息、系统通知 | ✅ Active |
| system-service | 轮播图、公告、地址、评价等 | ✅ Active |

### 3. 静态资源

已上传到云存储：
- ✅ 图标资源 (static/icons/)
- ✅ Logo 和其他图片资源

**静态资源访问域名**: 
```
https://cloudbase-4g1j4fs6cf7298bb-1408061709.tcloudbaseapp.com
```

## 🔧 小程序配置

### 1. 微信开发者工具配置

在微信开发者工具中打开项目，修改以下配置：

#### project.config.json
```json
{
  "miniprogramRoot": "dist/dev/mp-weixin/",
  "cloudfunctionRoot": "uniCloud-alipay/cloudfunctions/",
  "setting": {
    "urlCheck": false
  }
}
```

### 2. 云开发环境配置

在 `manifest.json` 中配置云开发环境：

```json
{
  "mp-weixin": {
    "appid": "wx821334ef9399e36b",
    "cloudfunctionRoot": "uniCloud-alipay/cloudfunctions/",
    "cloudbaseRoot": "cloudbase-4g1j4fs6cf7298bb"
  }
}
```

### 3. 初始化云开发

在 `App.uvue` 或 `main.uts` 中初始化：

```typescript
// 初始化云开发
if (typeof wx !== 'undefined' && wx.cloud) {
  wx.cloud.init({
    env: 'cloudbase-4g1j4fs6cf7298bb',
    traceUser: true
  })
}
```

## 🚀 部署步骤

### 步骤 1: 安装依赖

```bash
cd QuickErrand-app
npm install
```

### 步骤 2: 配置云函数

确保所有云函数的 `package.json` 包含必要的依赖：

- user-service: bcryptjs, jsonwebtoken
- 其他服务: 无特殊依赖

### 步骤 3: 上传云函数

在微信开发者工具中：
1. 右键点击 `uniCloud-alipay/cloudfunctions` 目录
2. 选择"同步云函数"
3. 等待上传完成

### 步骤 4: 构建小程序

```bash
# 开发环境构建
npm run dev:mp-weixin

# 生产环境构建
npm run build:mp-weixin
```

### 步骤 5: 上传小程序

在微信开发者工具中：
1. 点击"上传"按钮
2. 填写版本号和备注
3. 提交审核

## 📱 云函数调用示例

### 用户登录

```typescript
wx.cloud.callFunction({
  name: 'user-service',
  data: {
    action: 'login',
    data: {
      username: 'user123',
      password: 'password123'
    }
  }
}).then(res => {
  console.log('登录成功', res.result)
}).catch(err => {
  console.error('登录失败', err)
})
```

### 创建订单

```typescript
wx.cloud.callFunction({
  name: 'order-service',
  data: {
    action: 'createOrder',
    data: {
      userId: 'xxx',
      orderTypeId: 1,
      pickupAddress: '取货地址',
      deliveryAddress: '收货地址',
      distance: 5000,
      itemDescription: '物品描述'
    }
  }
})
```

### 获取轮播图

```typescript
wx.cloud.callFunction({
  name: 'system-service',
  data: {
    action: 'getBanners',
    data: {
      position: 1
    }
  }
})
```

## 🔐 安全配置

### 1. 数据库安全规则

在云开发控制台设置数据库权限：
- 用户表：仅创建者和管理员可读写
- 订单表：创建者可读写自己的订单
- 其他表：根据业务需求设置

### 2. 云函数权限

云函数默认需要用户登录后调用，可在云函数中通过 `event.userInfo` 获取用户信息。

### 3. 域名白名单

在微信小程序后台配置服务器域名：
```
request合法域名:
https://cloudbase-4g1j4fs6cf7298bb-1408061709.tcloudbaseapp.com
https://tcb-api.tencentcloudapi.com

uploadFile合法域名:
https://636c-cloudbase-4g1j4fs6cf7298bb-1408061709.tcb.qcloud.la

downloadFile合法域名:
https://636c-cloudbase-4g1j4fs6cf7298bb-1408061709.tcb.qcloud.la
```

## 💰 费用说明

### CloudBase 个人版计费

- **免费额度**：
  - 数据库：2GB 存储
  - 云函数：4万GBs/月
  - 云存储：5GB 存储
  - CDN流量：5GB/月

- **超出计费**：
  - 按实际使用量计费
  - 建议设置费用告警

### 优化建议

1. 合理使用数据库索引
2. 云函数避免重复查询
3. 静态资源使用CDN缓存
4. 定期清理无用数据

## 📊 监控与运维

### 1. 云开发控制台

访问地址：
```
https://tcb.cloud.tencent.com/dev?envId=cloudbase-4g1j4fs6cf7298bb
```

### 2. 监控指标

- 云函数调用次数
- 数据库读写次数
- 云存储使用量
- 错误日志

### 3. 日志查看

在云开发控制台查看：
- 云函数日志
- 数据库操作日志
- 访问日志

## 🔧 常见问题

### 1. 云函数调用失败

**原因**：
- 云函数未上传
- 参数格式错误
- 权限不足

**解决**：
- 检查云函数是否已部署
- 查看云函数日志
- 检查数据库权限设置

### 2. 数据库连接失败

**原因**：
- 环境ID配置错误
- 数据库未初始化

**解决**：
- 确认环境ID正确
- 在控制台初始化数据库

### 3. 图片上传失败

**原因**：
- 存储空间不足
- 文件大小超限
- 权限不足

**解决**：
- 检查存储空间
- 压缩图片大小
- 配置存储权限

## 📝 开发建议

### 1. 代码规范

- 使用 TypeScript 编写云函数
- 统一错误处理
- 添加必要的日志

### 2. 性能优化

- 合理使用数据库索引
- 避免频繁查询
- 使用缓存减少请求

### 3. 安全建议

- 敏感数据加密存储
- 验证用户权限
- 防止SQL注入

## 🎯 下一步

1. ✅ 完成基础部署
2. ⏭️ 配置微信支付
3. ⏭️ 配置短信验证码
4. ⏭️ 配置地图服务
5. ⏭️ 性能优化
6. ⏭️ 上线审核

## 📞 技术支持

- CloudBase 文档：https://cloud.tencent.com/document/product/876
- 微信小程序文档：https://developers.weixin.qq.com/miniprogram/dev/framework/
- uni-app x 文档：https://uniapp.dcloud.net.cn/

---

**部署完成时间**: 2026-03-05
**环境状态**: ✅ 正常运行
