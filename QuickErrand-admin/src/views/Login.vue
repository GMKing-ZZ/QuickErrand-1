<template>
  <div class="login-container" :style="loginBgStyle">
    <div class="login-shell">
      <!-- 左侧品牌与介绍 -->
      <div class="hero-panel">
        <div class="hero-badge">跑腿小程序管理后台</div>
        <h1 class="hero-title">轻松管理订单与跑腿员服务</h1>
        <p class="hero-subtitle">
          统一管理用户、订单、跑腿员认证、信用等级与提现审核，数据实时可视化。
        </p>
        <ul class="hero-list">
          <li>数据看板实时掌握平台运行情况</li>
          <li>完善的跑腿员认证与信用体系</li>
          <li>安全可控的提现审核流程</li>
        </ul>
      </div>

      <!-- 右侧登录卡片 -->
      <el-card class="login-card">
        <div class="brand">
          <img class="brand-logo" :src="logoUrl" alt="跑腿小程序管理系统登录" />
          <div class="brand-text">
            <div class="brand-title">跑腿小程序管理系统登录</div>
            <div class="brand-sub">请输入管理员账号登录后台</div>
          </div>
        </div>
        <el-form :model="loginForm" :rules="rules" ref="formRef">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { login } from '@/api/admin'
import logoUrl from '@/assets/logo.svg'
import bgUrl from '@/assets/background.png'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

// 登录背景样式：整页使用自定义 PNG 作为背景
const loginBgStyle = {
  backgroundImage: `url(${bgUrl})`
}

const loginForm = reactive({
  username: 'admin',
  password: 'admin123'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login(loginForm)
        userStore.setToken(res.data.token)
        userStore.setUserInfo(res.data.userInfo)
        ElMessage.success('登录成功')
        router.push('/')
      } catch (error) {
        console.error('登录失败:', error)
        ElMessage.error(error.message || '登录失败，请检查用户名和密码')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center top;
  position: relative;
  overflow: hidden;
}

.login-container::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at 30% 30%, rgba(59, 130, 246, 0.05) 0%, transparent 50%),
              radial-gradient(circle at 70% 70%, rgba(139, 92, 246, 0.05) 0%, transparent 50%);
  animation: float 20s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  50% {
    transform: translate(-2%, -2%) rotate(1deg);
  }
}

.login-shell {
  width: 960px;
  max-width: 100%;
  padding: 32px;
  display: grid;
  grid-template-columns: minmax(0, 3fr) minmax(0, 2.5fr);
  gap: 32px;
  align-items: stretch;
  position: relative;
  z-index: 1;
}

@media (max-width: 900px) {
  .login-shell {
    grid-template-columns: minmax(0, 1fr);
  }
}

.hero-panel {
  border-radius: var(--qe-radius-2xl);
  padding: 32px 28px 36px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.9) 100%);
  color: var(--qe-text);
  box-shadow: var(--qe-shadow-xl), 0 0 0 1px rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(20px);
  position: relative;
  overflow: hidden;
}

.hero-panel::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image: url('data:image/svg+xml,<svg width="60" height="60" viewBox="0 0 60 60" xmlns="http://www.w3.org/2000/svg"><defs><pattern id="p" width="60" height="60" patternUnits="userSpaceOnUse"><circle cx="30" cy="30" r="1" fill="rgba(59,130,246,0.08)"/></pattern></defs><rect width="100%" height="100%" fill="url(%23p)"/></svg>');
  opacity: 0.6;
  pointer-events: none;
}

.hero-panel::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #3b82f6 0%, #2563eb 100%);
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 16px;
  border-radius: var(--qe-radius-full);
  font-size: 12px;
  font-weight: 600;
  background: var(--qe-primary-light-bg);
  color: var(--qe-primary-dark);
  position: relative;
  z-index: 1;
  letter-spacing: 0.5px;
}

.hero-title {
  margin: 24px 0 12px;
  font-size: 32px;
  font-weight: 800;
  line-height: 1.3;
  letter-spacing: 0.5px;
  color: var(--qe-text);
  position: relative;
  z-index: 1;
}

.hero-subtitle {
  margin: 0 0 24px;
  font-size: 15px;
  line-height: 1.7;
  color: var(--qe-text-secondary);
  position: relative;
  z-index: 1;
}

.hero-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
  font-size: 14px;
  color: var(--qe-text-muted);
  position: relative;
  z-index: 1;
}

.hero-list li {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  background: rgba(59, 130, 246, 0.04);
  border-radius: var(--qe-radius-md);
  transition: all var(--qe-transition-fast);
}

.hero-list li:hover {
  background: var(--qe-primary-light-bg);
  transform: translateX(4px);
}

.hero-list li::before {
  content: '✓';
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--qe-primary-gradient);
  color: white;
  border-radius: 50%;
  font-size: 11px;
  font-weight: bold;
  flex-shrink: 0;
}

.login-card {
  width: 420px;
  max-width: 100%;
  padding: 32px 28px 24px;
  border-radius: var(--qe-radius-2xl);
  border: none;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98) 0%, rgba(255, 255, 255, 0.95) 100%);
  box-shadow: var(--qe-shadow-2xl), 0 0 0 1px rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(20px);
  position: relative;
  overflow: hidden;
}

.login-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #3b82f6 0%, #2563eb 100%);
}

.brand {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 28px;
}

.brand-logo {
  width: 48px;
  height: 48px;
  filter: drop-shadow(0 4px 12px rgba(59, 130, 246, 0.2));
}

.brand-text {
  display: flex;
  flex-direction: column;
}

.brand-title {
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 0.3px;
  color: var(--qe-text);
}

.brand-sub {
  margin-top: 4px;
  font-size: 13px;
  color: var(--qe-text-muted);
}

.login-btn {
  width: 100%;
  border-radius: var(--qe-radius-lg);
  height: 44px;
  font-size: 15px;
  font-weight: 600;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  box-shadow: var(--qe-primary-shadow);
  transition: all var(--qe-transition-fast);
}

.login-btn:hover {
  box-shadow: var(--qe-primary-shadow-hover);
  transform: translateY(-2px);
}

:deep(.el-input__wrapper) {
  border-radius: var(--qe-radius-lg);
  padding: 4px 16px;
  box-shadow: 0 0 0 1px var(--qe-border) inset;
  transition: all var(--qe-transition-fast);
  background: var(--qe-bg);
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--qe-primary-light) inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--qe-primary) inset, var(--qe-shadow-md);
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-input__inner) {
  height: 42px;
  font-size: 14px;
}

:deep(.el-input__prefix-inner) {
  font-size: 16px;
  color: var(--qe-text-muted);
}
</style>
