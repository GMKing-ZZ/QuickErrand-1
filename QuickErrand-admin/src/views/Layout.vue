<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapsed ? '72px' : '240px'" class="qe-aside">
      <div class="logo" :class="{ collapsed: isCollapsed }" @click="router.push('/dashboard')">
        <img class="logo-mark" :src="logoUrl" alt="跑腿小程序管理后台" />
        <div v-if="!isCollapsed" class="logo-text">
          <div class="logo-title">跑腿小程序管理后台</div>
        </div>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        :collapse="isCollapsed"
        :collapse-transition="false"
        class="qe-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>数据看板</span>
        </el-menu-item>
        <el-menu-item index="/user/list">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/order/list">
          <el-icon><Document /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/orderType/list">
          <el-icon><List /></el-icon>
          <span>订单类型管理</span>
        </el-menu-item>
        <el-menu-item index="/runnerAuth/list">
          <el-icon><Checked /></el-icon>
          <span>认证审核</span>
        </el-menu-item>
        <el-menu-item index="/announcement/list">
          <el-icon><Bell /></el-icon>
          <span>公告管理</span>
        </el-menu-item>
        <el-menu-item index="/message/list">
          <el-icon><Message /></el-icon>
          <span>消息管理</span>
        </el-menu-item>
        <el-menu-item index="/review/list">
          <el-icon><ChatDotRound /></el-icon>
          <span>评价管理</span>
        </el-menu-item>
        <el-menu-item index="/creditLevel/list">
          <el-icon><Medal /></el-icon>
          <span>信用等级管理</span>
        </el-menu-item>
        <el-menu-item index="/earnings/list">
          <el-icon><TrendCharts /></el-icon>
          <span>收益结算管理</span>
        </el-menu-item>
        <el-menu-item index="/withdrawal/list">
          <el-icon><Wallet /></el-icon>
          <span>提现审核管理</span>
        </el-menu-item>
        <el-menu-item index="/banner/list">
          <el-icon><Picture /></el-icon>
          <span>轮播图管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header>
        <div class="header-content">
          <div class="header-left">
            <el-button class="collapse-btn" text @click="isCollapsed = !isCollapsed">
              <el-icon><Fold v-if="!isCollapsed" /><Expand v-else /></el-icon>
            </el-button>
            <span class="title">{{ currentTitle }}</span>
          </div>
          <div class="header-right">
            <el-button class="theme-toggle" text @click="toggleTheme">
              {{ isDark ? '白天' : '黑夜' }}
            </el-button>
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar 
                  v-if="adminInfo?.avatar" 
                  :src="adminInfo.avatar" 
                  :size="32"
                  class="user-avatar"
                />
                <el-avatar v-else :size="32" class="user-avatar">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <span class="user-name">{{ adminInfo?.nickname || adminInfo?.username || '管理员' }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { User, MoonNight, Sunny, Fold, Expand, Message } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getAdminInfo } from '@/api/admin'
import logoUrl from '@/assets/logo.svg'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isCollapsed = ref(false)
const isDark = ref(false)
const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '')

const adminInfo = computed(() => userStore.userInfo)

onMounted(async () => {
  // 检查保存的主题状态
  const savedTheme = localStorage.getItem('theme')
  if (savedTheme === 'dark') {
    isDark.value = true
    document.documentElement.classList.add('dark')
  }
  
  if (!userStore.userInfo) {
    try {
      const res = await getAdminInfo()
      userStore.setUserInfo(res.data)
    } catch (error) {
      console.error('获取管理员信息失败:', error)
    }
  }
})

const toggleTheme = () => {
  isDark.value = !isDark.value
  if (isDark.value) {
    document.documentElement.classList.add('dark')
    localStorage.setItem('theme', 'dark')
  } else {
    document.documentElement.classList.remove('dark')
    localStorage.setItem('theme', 'light')
  }
}

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/login')
    })
  }
}
</script>

<style scoped>
.layout-container {
  width: 100%;
  height: 100%;
}

.qe-aside {
  background: linear-gradient(180deg, var(--qe-bg) 0%, var(--qe-bg-2) 100%);
  border-right: 1px solid var(--qe-border-lighter);
  overflow: hidden;
  position: relative;
  box-shadow: var(--qe-shadow-sm);
}

.qe-aside::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  width: 1px;
  background: linear-gradient(180deg, transparent 0%, var(--qe-border-light) 50%, transparent 100%);
}

.logo {
  height: 64px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  color: var(--qe-text);
  border-bottom: 1px solid var(--qe-border-lighter);
  cursor: pointer;
  user-select: none;
  transition: all var(--qe-transition-base);
  position: relative;
  background: var(--qe-bg);
}

.logo::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: 20px;
  right: 20px;
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, var(--qe-border-light) 50%, transparent 100%);
}

.logo:hover {
  background: var(--qe-surface);
}

.logo.collapsed {
  justify-content: center;
  padding: 0;
}

.logo-mark {
  width: 38px;
  height: 38px;
  transition: transform var(--qe-transition-base);
}

.logo:hover .logo-mark {
  transform: scale(1.05);
}

.logo-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.logo-title {
  font-size: 14px;
  font-weight: 700;
  letter-spacing: 0.3px;
  color: var(--qe-text);
  background: var(--qe-primary-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.el-header {
  background: var(--qe-bg);
  box-shadow: var(--qe-shadow-sm);
  display: flex;
  align-items: center;
  border-bottom: 1px solid var(--qe-border-lighter);
  height: var(--qe-header-height) !important;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.theme-toggle {
  height: 36px;
  padding: 0 16px;
  border-radius: var(--qe-radius-md);
  transition: all var(--qe-transition-base);
  color: var(--qe-text-secondary);
  position: relative;
  overflow: hidden;
  border: 3px solid var(--qe-border-lighter);
}

.theme-toggle::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: var(--qe-radius-md);
  background: var(--qe-primary-light-bg);
  transform: translate(-50%, -50%);
  transition: all var(--qe-transition-base);
  z-index: 0;
}

.theme-toggle:hover::before {
  width: 100%;
  height: 100%;
}

.theme-toggle span {
  position: relative;
  z-index: 1;
  font-size: 14px;
  font-weight: 500;
  transition: all var(--qe-transition-base);
}

.theme-toggle:hover span {
  color: var(--qe-primary);
}

.theme-toggle:active span {
  transform: scale(0.95);
}

.collapse-btn {
  height: 36px;
  width: 36px;
  border-radius: var(--qe-radius-md);
  transition: all var(--qe-transition-fast);
  color: var(--qe-text-secondary);
}

.collapse-btn:hover {
  background: var(--qe-primary-light-bg);
  color: var(--qe-primary);
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: var(--qe-text);
  letter-spacing: 0.3px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: var(--qe-radius-lg);
  transition: all var(--qe-transition-fast);
  border: 1px solid transparent;
}

.user-info:hover {
  background: var(--qe-surface);
  border-color: var(--qe-border-lighter);
}

.user-avatar {
  flex-shrink: 0;
  border: 2px solid var(--qe-border-lighter);
  transition: border-color var(--qe-transition-fast);
}

.user-info:hover .user-avatar {
  border-color: var(--qe-primary-light);
}

.user-name {
  font-size: 14px;
  color: var(--qe-text-secondary);
  font-weight: 500;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.el-main {
  background: var(--qe-bg-3);
  padding: 24px;
  min-height: calc(100vh - var(--qe-header-height));
}

:deep(.qe-menu) {
  border-right: 0;
  background: transparent;
}

:deep(.qe-menu.el-menu--vertical) {
  padding: 12px 8px;
  background: transparent;
}

:deep(.qe-menu .el-menu-item) {
  height: 44px;
  line-height: 44px;
  margin: 3px 8px;
  color: var(--qe-text-secondary);
  background: transparent;
  transition: all var(--qe-transition-fast);
  font-weight: 500;
  position: relative;
  border-radius: var(--qe-radius-md);
  overflow: hidden;
}

:deep(.qe-menu .el-menu-item::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 0;
  background: linear-gradient(180deg, #3b82f6 0%, #2563eb 100%);
  border-radius: 0 var(--qe-radius-full) var(--qe-radius-full) 0;
  transition: height var(--qe-transition-fast);
}

:deep(.qe-menu .el-menu-item:hover) {
  background: var(--qe-surface);
  color: var(--qe-text);
}

:deep(.qe-menu .el-menu-item:hover::before) {
  height: 16px;
}

:deep(.qe-menu .el-menu-item.is-active) {
  color: var(--qe-primary) !important;
  background: var(--qe-primary-light-bg) !important;
  font-weight: 600;
}

:deep(.qe-menu .el-menu-item.is-active::before) {
  height: 20px;
}

:deep(.qe-menu .el-menu-item.is-active .el-icon) {
  color: var(--qe-primary);
}

:deep(.qe-menu .el-menu-item .el-icon) {
  margin-right: 12px;
  font-size: 18px;
  transition: transform var(--qe-transition-fast);
}

:deep(.qe-menu .el-menu-item:hover .el-icon) {
  transform: scale(1.1);
}

:deep(.qe-menu.el-menu--collapse .el-menu-item) {
  justify-content: center;
  margin: 3px 4px;
}

:deep(.qe-menu.el-menu--collapse .el-menu-item .el-icon) {
  margin-right: 0;
}

:deep(.el-dropdown-menu) {
  border-radius: var(--qe-radius-lg);
  border: 1px solid var(--qe-border-lighter);
  box-shadow: var(--qe-shadow-dropdown);
  padding: 8px;
}

:deep(.el-dropdown-menu__item) {
  border-radius: var(--qe-radius-md);
  padding: 10px 16px;
  font-size: 14px;
  transition: all var(--qe-transition-fast);
}

:deep(.el-dropdown-menu__item:hover) {
  background: var(--qe-primary-light-bg);
  color: var(--qe-primary);
}
</style>
