import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import Layout from '@/views/Layout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '数据看板', requiresAuth: true }
      },
      {
        path: 'user/list',
        name: 'UserList',
        component: () => import('@/views/user/UserList.vue'),
        meta: { title: '用户管理', requiresAuth: true }
      },
      {
        path: 'order/list',
        name: 'OrderList',
        component: () => import('@/views/order/OrderList.vue'),
        meta: { title: '订单管理', requiresAuth: true }
      },
      {
        path: 'orderType/list',
        name: 'OrderTypeList',
        component: () => import('@/views/orderType/OrderTypeList.vue'),
        meta: { title: '订单类型管理', requiresAuth: true }
      },
      {
        path: 'runnerAuth/list',
        name: 'RunnerAuthList',
        component: () => import('@/views/runnerAuth/RunnerAuthList.vue'),
        meta: { title: '认证审核', requiresAuth: true }
      },
      {
        path: 'announcement/list',
        name: 'AnnouncementList',
        component: () => import('@/views/announcement/AnnouncementList.vue'),
        meta: { title: '公告管理', requiresAuth: true }
      },
      {
        path: 'review/list',
        name: 'ReviewList',
        component: () => import('@/views/review/ReviewList.vue'),
        meta: { title: '评价管理', requiresAuth: true }
      },
      {
        path: 'creditLevel/list',
        name: 'CreditLevelList',
        component: () => import('@/views/creditLevel/CreditLevelList.vue'),
        meta: { title: '信用等级管理', requiresAuth: true }
      },
      {
        path: 'banner/list',
        name: 'BannerList',
        component: () => import('@/views/banner/BannerList.vue'),
        meta: { title: '轮播图管理', requiresAuth: true }
      },
      {
        path: 'withdrawal/list',
        name: 'WithdrawalList',
        component: () => import('@/views/withdrawal/WithdrawalList.vue'),
        meta: { title: '提现审核管理', requiresAuth: true }
      },
      {
        path: 'earnings/list',
        name: 'EarningsList',
        component: () => import('@/views/earnings/EarningsList.vue'),
        meta: { title: '收益结算管理', requiresAuth: true }
      },
      {
        path: 'message/list',
        name: 'MessageList',
        component: () => import('@/views/message/MessageList.vue'),
        meta: { title: '消息管理', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token

  // 如果访问登录页且已登录，跳转到首页
  if (to.path === '/login' && token) {
    next('/')
    return
  }

  // 如果需要认证但未登录，跳转到登录页
  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }

  next()
})

export default router
