<template>
  <div class="dashboard">
    <div class="welcome-section">
      <div class="welcome-content">
        <el-avatar 
          v-if="adminInfo?.avatar" 
          :src="adminInfo.avatar" 
          :size="64"
          class="welcome-avatar"
        />
        <el-avatar v-else :size="64" class="welcome-avatar">
          <el-icon style="font-size: 32px;"><User /></el-icon>
        </el-avatar>
        <div class="welcome-text">
          <div class="welcome-title">{{ getGreeting() }}，{{ adminInfo?.nickname || adminInfo?.username || '管理员' }}</div>
          <div class="welcome-subtitle">欢迎使用跑腿小程序管理后台</div>
        </div>
      </div>
    </div>
    <div class="time-overlay">
      <span class="time-line">当前时间：{{ currentTime || '-' }}</span>
      <span class="time-line">上次数据更新时间：{{ lastUpdateTime || '—' }}</span>
    </div>
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card class="stat-card" shadow="never">
          <div class="stat-content">
            <div class="stat-title">总收入</div>
            <div class="stat-value">¥ {{ formatMoney(statistics.totalRevenue) }}</div>
            <div class="stat-trend">
              <div class="trend-item">
                <span class="trend-label">周同比</span>
                <span class="trend-value" :class="getTrendClass(statistics.revenueWeekRatio)">
                  {{ formatRatio(statistics.revenueWeekRatio) }}
                </span>
              </div>
              <div class="trend-item">
                <span class="trend-label">日同比</span>
                <span class="trend-value" :class="getTrendClass(statistics.revenueDayRatio)">
                  {{ formatRatio(statistics.revenueDayRatio) }}
                </span>
              </div>
            </div>
            <div class="stat-detail">今日收入 ¥{{ formatMoney(statistics.todayRevenue) }} | 平台服务费 ¥{{ formatMoney(statistics.totalPlatformFee) }}</div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card" shadow="never">
          <div class="stat-content">
            <div class="stat-title">总用户数</div>
            <div class="stat-value">{{ statistics.totalUsers || 0 }}</div>
            <div class="stat-detail">今日新增 {{ statistics.todayNewUsers || 0 }}</div>
            <div class="mini-chart" ref="usersChart"></div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card" shadow="never">
          <div class="stat-content">
            <div class="stat-title">总订单数</div>
            <div class="stat-value">{{ statistics.totalOrders || 0 }}</div>
            <div class="stat-detail">今日新增 {{ statistics.todayNewOrders || 0 }}</div>
            <div class="mini-chart" ref="ordersChart"></div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card" shadow="never">
          <div class="stat-content">
            <div class="stat-title">总跑腿员数</div>
            <div class="stat-value">{{ statistics.totalRunners || 0 }}</div>
            <div class="stat-trend">
              <div class="trend-item">
                <span class="trend-label">周同比</span>
                <span class="trend-value" :class="getTrendClass(statistics.runnersWeekRatio)">
                  {{ formatRatio(statistics.runnersWeekRatio) }}
                </span>
              </div>
              <div class="trend-item">
                <span class="trend-label">日同比</span>
                <span class="trend-value" :class="getTrendClass(statistics.runnersDayRatio)">
                  {{ formatRatio(statistics.runnersDayRatio) }}
                </span>
              </div>
            </div>
            <div class="stat-detail">今日新增 {{ statistics.todayNewRunners || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <div class="header-tabs">
                <span 
                  class="tab-item" 
                  :class="{ active: chartTab === 'revenue' }"
                  @click="chartTab = 'revenue'"
                >收入趋势</span>
                <span 
                  class="tab-item" 
                  :class="{ active: chartTab === 'orders' }"
                  @click="chartTab = 'orders'"
                >订单趋势</span>
              </div>
            </div>
          </template>
          <div class="chart-container" ref="mainChart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <!-- 订单状态排名 -->
      <el-col :span="12">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">订单状态统计</span>
            </div>
          </template>
          <div class="ranking-list">
            <div 
              v-for="(item, index) in orderStatusList" 
              :key="index"
              class="ranking-item"
            >
              <div class="rank-number" :class="{ 'top-three': index < 3 }">
                {{ index + 1 }}
              </div>
              <div class="rank-content">
                <div class="rank-name">{{ item.name }}</div>
                <div class="rank-value">{{ item.value }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 订单类型占比 -->
      <el-col :span="12">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">订单类型占比</span>
            </div>
          </template>
          <div class="chart-container" ref="orderTypeChart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { User } from '@element-plus/icons-vue'
import { getDashboardStatistics } from '@/api/statistics'
import { useUserStore } from '@/store/user'
import { getAdminInfo } from '@/api/admin'
import * as echarts from 'echarts'

const userStore = useUserStore()
const adminInfo = computed(() => userStore.userInfo)

const statistics = reactive({
  totalUsers: 0,
  totalOrders: 0,
  totalRunners: 0,
  totalRevenue: 0,
  totalPlatformFee: 0,
  todayNewUsers: 0,
  todayNewOrders: 0,
  todayNewRunners: 0,
  todayRevenue: 0,
  revenueWeekRatio: null,
  revenueDayRatio: null,
  usersWeekRatio: null,
  usersDayRatio: null,
  ordersWeekRatio: null,
  ordersDayRatio: null,
  runnersWeekRatio: null,
  runnersDayRatio: null,
  orderStatusStatistics: [],
  orderTrend: [],
  revenueTrend: [],
  orderTypeStatistics: []
})

const chartTab = ref('revenue')
const mainChart = ref(null)
const orderTypeChart = ref(null)
const usersChart = ref(null)
const ordersChart = ref(null)

let mainChartInstance = null
let orderTypeChartInstance = null
let usersChartInstance = null
let ordersChartInstance = null

const orderStatusList = ref([])

// 当前时间与上次数据更新时间
const currentTime = ref('')
const lastUpdateTime = ref('')

// 定时静默刷新定时器
const REFRESH_INTERVAL = 10000 // 10秒刷新一次
let refreshTimer = null
let clockTimer = null

const resizeAll = () => {
  mainChartInstance?.resize()
  orderTypeChartInstance?.resize()
  usersChartInstance?.resize()
  ordersChartInstance?.resize()
}

onMounted(() => {
  loadAdminInfo()
  updateCurrentTime()
  clockTimer = setInterval(updateCurrentTime, 1000)

  loadData()
  refreshTimer = setInterval(() => {
    loadData(true)
  }, REFRESH_INTERVAL)
  window.addEventListener('resize', resizeAll)
})

const loadAdminInfo = async () => {
  if (!userStore.userInfo) {
    try {
      const res = await getAdminInfo()
      userStore.setUserInfo(res.data)
    } catch (error) {
      console.error('获取管理员信息失败:', error)
    }
  }
}

const getGreeting = () => {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 17) return '下午好'
  if (hour < 19) return '傍晚好'
  if (hour < 22) return '晚上好'
  return '夜深了'
}

onBeforeUnmount(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
  if (clockTimer) {
    clearInterval(clockTimer)
    clockTimer = null
  }
  if (mainChartInstance) mainChartInstance.dispose()
  if (orderTypeChartInstance) orderTypeChartInstance.dispose()
  if (usersChartInstance) usersChartInstance.dispose()
  if (ordersChartInstance) ordersChartInstance.dispose()
  window.removeEventListener('resize', resizeAll)
})

const loadData = async (isSilent = false) => {
  try {
    const res = await getDashboardStatistics()
    Object.assign(statistics, res.data)
    // 记录上次成功获取数据的时间
    lastUpdateTime.value = formatDateTime(new Date())
    
    // 处理订单状态统计
    if (statistics.orderStatusStatistics && statistics.orderStatusStatistics.length > 0) {
      orderStatusList.value = statistics.orderStatusStatistics.map(item => ({
        name: item.statusName,
        value: formatNumber(item.count)
      }))
    } else {
      // 默认订单状态
      orderStatusList.value = [
        { name: '待接单', value: '0' },
        { name: '进行中', value: '0' },
        { name: '已完成', value: '0' },
        { name: '已取消', value: '0' },
        { name: '待评价', value: '0' },
        { name: '已评价', value: '0' }
      ]
    }
    
    await nextTick()
    // 首次加载初始化图表，后续刷新仅更新数据，避免重复初始化
    if (!mainChartInstance) {
      initMainChart()
    } else {
      updateMainChart()
    }
    if (!orderTypeChartInstance) {
      initOrderTypeChart()
    } else {
      updateOrderTypeChart()
    }
    if (!usersChartInstance || !ordersChartInstance) {
      initMiniCharts()
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    if (!isSilent) {
      ElMessage.error('获取数据看板统计数据失败，请稍后重试')
    }
  }
}

// 实时时钟：更新当前时间
const updateCurrentTime = () => {
  currentTime.value = formatDateTime(new Date())
}

// 日期时间格式化：YYYY-MM-DD HH:mm:ss
const formatDateTime = (date) => {
  if (!date) return ''
  const pad = (n) => (n < 10 ? '0' + n : '' + n)
  const y = date.getFullYear()
  const m = pad(date.getMonth() + 1)
  const d = pad(date.getDate())
  const h = pad(date.getHours())
  const mi = pad(date.getMinutes())
  const s = pad(date.getSeconds())
  return `${y}-${m}-${d} ${h}:${mi}:${s}`
}

// 更新主图表（用于静默刷新）
const updateMainChart = () => {
  if (!mainChartInstance) return
  const data = chartTab.value === 'revenue'
    ? statistics.revenueTrend
    : statistics.orderTrend
  const option = {
    xAxis: {
      data: (data || []).map(item => item.date)
    },
    series: [{
      data: (data || []).map(item => Number(item.value))
    }]
  }
  mainChartInstance.setOption(option)
}

const formatMoney = (value) => {
  if (!value) return '0.00'
  return Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const formatNumber = (value) => {
  if (!value) return '0'
  return Number(value).toLocaleString('zh-CN')
}

const formatRatio = (value) => {
  if (value === null || value === undefined) return '-'
  const num = Number(value)
  if (isNaN(num)) return '-'
  const absNum = Math.abs(num)
  const sign = num >= 0 ? '▲' : '▼'
  return `${absNum.toFixed(2)}% ${sign}`
}

const getTrendClass = (value) => {
  if (value === null || value === undefined) return ''
  const num = Number(value)
  if (isNaN(num)) return ''
  return num >= 0 ? 'up' : 'down'
}

const initMainChart = () => {
  if (!mainChart.value) return
  mainChartInstance = echarts.init(mainChart.value)
  
  const data = chartTab.value === 'revenue' 
    ? statistics.revenueTrend 
    : statistics.orderTrend
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow',
        shadowStyle: {
          color: 'rgba(59, 130, 246, 0.08)'
        }
      },
      formatter: chartTab.value === 'revenue' 
        ? '{b}<br/>{a}: ¥{c}' 
        : '{b}<br/>{a}: {c}',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: 'rgba(59, 130, 246, 0.2)',
      borderWidth: 1,
      textStyle: {
        color: '#1f2937'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.date),
      boundaryGap: false,
      axisLine: {
        lineStyle: {
          color: '#e4e7ed'
        }
      },
      axisLabel: {
        color: '#6b7280'
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        lineStyle: {
          color: '#e4e7ed'
        }
      },
      axisLabel: {
        color: '#6b7280',
        formatter: chartTab.value === 'revenue' ? '¥{value}' : '{value}'
      },
      splitLine: {
        lineStyle: {
          color: '#f2f6fc',
          type: 'dashed'
        }
      }
    },
    series: [{
      name: chartTab.value === 'revenue' ? '收入' : '订单数',
      type: 'bar',
      data: data.map(item => Number(item.value)),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#3b82f6' },
          { offset: 1, color: '#2563eb' }
        ]),
        borderRadius: [6, 6, 0, 0]
      },
      emphasis: {
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#60a5fa' },
            { offset: 1, color: '#3b82f6' }
          ])
        }
      },
      barWidth: '50%'
    }]
  }
  
  mainChartInstance.setOption(option)
}

// 监听tab切换，更新图表
watch(() => chartTab.value, () => {
  if (!mainChartInstance) return
  const newData = chartTab.value === 'revenue' 
    ? statistics.revenueTrend 
    : statistics.orderTrend
  
  const option = {
    tooltip: {
      formatter: chartTab.value === 'revenue' 
        ? '{b}<br/>{a}: ¥{c}' 
        : '{b}<br/>{a}: {c}'
    },
    series: [{
      name: chartTab.value === 'revenue' ? '收入' : '订单数',
      data: newData.map(item => Number(item.value))
    }],
    xAxis: {
      data: newData.map(item => item.date)
    },
    yAxis: {
      axisLabel: {
        formatter: chartTab.value === 'revenue' ? '¥{value}' : '{value}'
      }
    }
  }
  mainChartInstance.setOption(option)
})

const initOrderTypeChart = () => {
  if (!orderTypeChart.value) return
  orderTypeChartInstance = echarts.init(orderTypeChart.value)
  
  const data = statistics.orderTypeStatistics.map(item => ({
    name: item.typeName,
    value: item.count
  }))
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: 'rgba(59, 130, 246, 0.2)',
      borderWidth: 1,
      textStyle: {
        color: '#1f2937'
      }
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'middle',
      textStyle: {
        color: '#6b7280'
      }
    },
    series: [{
      name: '订单类型',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['60%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 3
      },
      label: {
        show: false
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 14,
          fontWeight: 'bold'
        },
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.1)'
        }
      },
      data: data,
      color: ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4']
    }]
  }
  
  orderTypeChartInstance.setOption(option)
}

// 更新订单类型图表（用于静默刷新）
const updateOrderTypeChart = () => {
  if (!orderTypeChartInstance) return
  const data = (statistics.orderTypeStatistics || []).map(item => ({
    name: item.typeName,
    value: item.count
  }))
  const option = {
    series: [{
      data
    }]
  }
  orderTypeChartInstance.setOption(option)
}

const initMiniCharts = () => {
  if (usersChart.value) {
    usersChartInstance = echarts.init(usersChart.value)
    const option = {
      grid: { left: 0, right: 0, top: 0, bottom: 0 },
      xAxis: { type: 'category', show: false, data: ['1', '2', '3', '4', '5', '6', '7'] },
      yAxis: { type: 'value', show: false },
      series: [{
        data: [120, 132, 101, 134, 90, 230, 210],
        type: 'line',
        smooth: true,
        symbol: 'none',
        lineStyle: { color: '#3b82f6', width: 2 },
        areaStyle: { 
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(59, 130, 246, 0.25)' },
            { offset: 1, color: 'rgba(59, 130, 246, 0.02)' }
          ])
        }
      }]
    }
    usersChartInstance.setOption(option)
  }
  
  if (ordersChart.value) {
    ordersChartInstance = echarts.init(ordersChart.value)
    const option = {
      grid: { left: 0, right: 0, top: 0, bottom: 0 },
      xAxis: { type: 'category', show: false, data: ['1', '2', '3', '4', '5', '6', '7'] },
      yAxis: { type: 'value', show: false },
      series: [{
        data: [120, 132, 101, 134, 90, 230, 210],
        type: 'bar',
        itemStyle: { 
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#10b981' },
            { offset: 1, color: '#059669' }
          ]),
          borderRadius: [3, 3, 0, 0] 
        },
        barWidth: '60%'
      }]
    }
    ordersChartInstance.setOption(option)
  }
}

</script>

<style scoped>
.dashboard {
  width: 100%;
  position: relative;
}

.welcome-section {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border-radius: var(--qe-radius-xl);
  padding: 28px 36px;
  margin-bottom: 24px;
  box-shadow: var(--qe-primary-shadow);
  position: relative;
  overflow: hidden;
  transition: all var(--qe-transition-base);
}

html.dark .welcome-section {
  background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%);
  box-shadow: 0 4px 14px rgba(30, 64, 175, 0.3);
}

.welcome-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.08) 0%, transparent 60%);
  pointer-events: none;
}

.welcome-content {
  display: flex;
  align-items: center;
  gap: 20px;
  position: relative;
  z-index: 1;
}

.welcome-avatar {
  border: 3px solid rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.2);
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.welcome-text {
  flex: 1;
}

.welcome-title {
  font-size: 26px;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 8px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  letter-spacing: 0.3px;
}

.welcome-subtitle {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.9);
  letter-spacing: 0.2px;
}

.stat-row {
  margin-bottom: 24px;
  display: flex;
}

.stat-row :deep(.el-col) {
  display: flex;
}

.stat-card {
  border: 1px solid var(--qe-border-lighter);
  border-radius: var(--qe-radius-xl);
  width: 100%;
  display: flex;
  flex-direction: column;
  transition: all var(--qe-transition-base);
  overflow: hidden;
  background: var(--qe-bg);
}

.stat-card:hover {
  box-shadow: var(--qe-shadow-card-hover);
  transform: translateY(-2px);
}

.stat-card :deep(.el-card__body) {
  padding: 24px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.stat-content {
  position: relative;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.stat-title {
  font-size: 14px;
  color: var(--qe-text-muted);
  margin-bottom: 12px;
  flex-shrink: 0;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: var(--qe-text);
  margin-bottom: 16px;
  line-height: 1.2;
  flex-shrink: 0;
  letter-spacing: -0.5px;
}

.time-value {
  font-size: 22px;
}

.time-overlay {
  position: absolute;
  top: -24px;
  font-size: 12px;
  color: var(--qe-text-muted);
  line-height: 1.5;
  display: flex;
  gap: 20px;
  align-items: center;
  pointer-events: none;
  z-index: 10;
}

.time-line {
  white-space: nowrap;
}

.stat-trend {
  display: flex;
  gap: 20px;
  margin-bottom: 12px;
  flex-shrink: 0;
}

.trend-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.trend-label {
  font-size: 13px;
  color: var(--qe-text-muted);
}

.trend-value {
  font-size: 13px;
  font-weight: 600;
}

.trend-value.up {
  color: var(--qe-success);
}

.trend-value.down {
  color: var(--qe-danger);
}

.stat-detail {
  font-size: 13px;
  color: var(--qe-text-muted);
  margin-top: auto;
  flex-shrink: 0;
  padding-top: 12px;
  border-top: 1px solid var(--qe-border-lighter);
}

.mini-chart {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 100px;
  height: 50px;
}

.chart-row {
  margin-bottom: 24px;
  display: flex;
}

.chart-row :deep(.el-col) {
  display: flex;
}

.chart-card {
  border: 1px solid var(--qe-border-lighter);
  border-radius: var(--qe-radius-xl);
  width: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: box-shadow var(--qe-transition-base);
  background: var(--qe-bg);
}

.chart-card:hover {
  box-shadow: var(--qe-shadow-card-hover);
}

.chart-card :deep(.el-card__header) {
  padding: 18px 24px;
  border-bottom: 1px solid var(--qe-border-lighter);
  background: var(--qe-surface);
  flex-shrink: 0;
}

.chart-card :deep(.el-card__body) {
  padding: 24px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--qe-text);
  letter-spacing: 0.3px;
}

.header-tabs {
  display: flex;
  gap: 28px;
}

.tab-item {
  font-size: 14px;
  color: var(--qe-text-muted);
  cursor: pointer;
  padding: 6px 0;
  position: relative;
  transition: color var(--qe-transition-fast);
  font-weight: 500;
}

.tab-item:hover {
  color: var(--qe-text-secondary);
}

.tab-item.active {
  color: var(--qe-primary);
  font-weight: 600;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -18px;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #3b82f6 0%, #2563eb 100%);
  border-radius: var(--qe-radius-full) var(--qe-radius-full) 0 0;
}

.chart-container {
  width: 100%;
  flex: 1;
  min-height: 400px;
}

.ranking-list {
  padding: 8px 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.ranking-item {
  display: flex;
  align-items: center;
  padding: 14px 0;
  border-bottom: 1px solid var(--qe-border-lighter);
  transition: all var(--qe-transition-fast);
}

.ranking-item:hover {
  background: var(--qe-primary-light-bg);
  margin: 0 -12px;
  padding: 14px 12px;
  border-radius: var(--qe-radius-md);
}

.ranking-item:last-child {
  border-bottom: none;
}

.rank-number {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--qe-radius-md);
  background: var(--qe-surface-2);
  color: var(--qe-text-muted);
  font-size: 13px;
  font-weight: 600;
  margin-right: 16px;
  transition: all var(--qe-transition-fast);
}

.ranking-item:hover .rank-number {
  transform: scale(1.1);
}

.rank-number.top-three {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: #fff;
  font-weight: 700;
  box-shadow: var(--qe-primary-shadow);
}

.rank-content {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.rank-name {
  font-size: 14px;
  color: var(--qe-text);
  font-weight: 500;
}

.rank-value {
  font-size: 14px;
  color: var(--qe-text-secondary);
  font-weight: 600;
}
</style>
