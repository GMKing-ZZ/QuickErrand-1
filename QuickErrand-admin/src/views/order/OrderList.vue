<template>
  <div class="page-container">
    <el-breadcrumb separator="/" class="page-breadcrumb">
      <el-breadcrumb-item>列表页</el-breadcrumb-item>
      <el-breadcrumb-item>订单管理</el-breadcrumb-item>
    </el-breadcrumb>

    <PageHeader
      title="订单管理"
      subtitle="管理系统订单信息，包括订单状态、金额、跑腿员分配等"
      :icon="ShoppingCart"
    />

    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="关键词:">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="订单状态:">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择"
            clearable
            style="width: 150px"
          >
            <el-option label="待支付" :value="1" />
            <el-option label="待接单" :value="2" />
            <el-option label="服务中" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已取消" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <div class="table-toolbar">
        <div class="table-toolbar-left">
          <span v-if="selectedRows.length > 0" style="color: var(--qe-text-muted);">
            已选择 {{ selectedRows.length }} 项
          </span>
        </div>
        <div class="table-toolbar-right">
          <el-icon class="table-toolbar-icon"><Setting /></el-icon>
        </div>
      </div>

      <el-table
        :data="tableData"
        v-loading="loading"
        class="data-table"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="序号" width="55" align="center" :index="getIndex" />
        <el-table-column prop="orderNo" label="订单号" width="130" show-overflow-tooltip sortable align="center" />
        <el-table-column prop="userNickname" label="用户" align="center" />
        <el-table-column prop="orderTypeName" label="订单类型" align="center" />
        <el-table-column prop="pickupAddress" label="取件地址" width="200" show-overflow-tooltip align="center" />
        <el-table-column prop="deliveryAddress" label="收件地址" width="200" show-overflow-tooltip align="center" />
        <el-table-column prop="amount" label="金额" sortable align="center">
          <template #default="{ row }">
            ¥{{ row.amount }}
          </template>
        </el-table-column>
        <el-table-column prop="statusText" label="状态" width="120" sortable align="center">
          <template #default="{ row }">
            <span class="status-tag">
              <span :class="['status-dot', getStatusDotClass(row.status)]"></span>
              {{ row.statusText }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="runnerNickname" label="跑腿员" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="180" sortable align="center" />
        <el-table-column label="操作" width="250" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                type="text"
                @click="handleViewDetail(row)"
              >
                查看详情
              </el-button>
              <el-button
                type="text"
                @click="handleOpenStatusDialog(row)"
              >
                修改状态
              </el-button>
              <el-button
                type="text"
                class="delete-btn"
                @click="handleDelete(row)"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-footer" v-if="selectedRows.length > 0">
        <div class="table-footer-left">
          已选择 {{ selectedRows.length }} 项
        </div>
        <div class="table-footer-right">
          <el-button class="batch-delete-btn" @click="handleBatchDelete">批量删除</el-button>
        </div>
      </div>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="800px"
      class="detail-dialog"
    >
      <div v-if="currentOrder" class="order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">
            {{ currentOrder.orderNo }}
          </el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusTagType(currentOrder.status)">
              {{ currentOrder.statusText }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="订单类型">
            {{ currentOrder.orderTypeName }}
          </el-descriptions-item>
          <el-descriptions-item label="订单金额">
            ¥{{ currentOrder.amount }}
          </el-descriptions-item>
          <el-descriptions-item label="配送费">
            ¥{{ calculateDeliveryFee(currentOrder) }}
          </el-descriptions-item>
          <el-descriptions-item label="平台服务费">
            ¥{{ currentOrder.platformFee || '0.00' }}
          </el-descriptions-item>
          <el-descriptions-item label="取件联系人">
            {{ currentOrder.pickupContact }}
          </el-descriptions-item>
          <el-descriptions-item label="取件电话">
            {{ currentOrder.pickupPhone }}
          </el-descriptions-item>
          <el-descriptions-item label="取件地址" :span="2">
            {{ currentOrder.pickupAddress }}
          </el-descriptions-item>
          <el-descriptions-item label="收件联系人">
            {{ currentOrder.deliveryContact }}
          </el-descriptions-item>
          <el-descriptions-item label="收件电话">
            {{ currentOrder.deliveryPhone }}
          </el-descriptions-item>
          <el-descriptions-item label="收件地址" :span="2">
            {{ currentOrder.deliveryAddress }}
          </el-descriptions-item>
          <el-descriptions-item label="距离">
            {{ currentOrder.distance }} 公里
          </el-descriptions-item>
          <el-descriptions-item label="收货码">
            {{ currentOrder.pickupCode }}
          </el-descriptions-item>
          <el-descriptions-item label="跑腿员" v-if="currentOrder.runnerName">
            {{ currentOrder.runnerName }}
          </el-descriptions-item>
          <el-descriptions-item label="跑腿员电话" v-if="currentOrder.runnerPhone">
            {{ currentOrder.runnerPhone }}
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2" v-if="currentOrder.remark">
            {{ currentOrder.remark }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">
            {{ currentOrder.createTime }}
          </el-descriptions-item>
          <el-descriptions-item label="接单时间" :span="2" v-if="currentOrder.acceptTime">
            {{ currentOrder.acceptTime }}
          </el-descriptions-item>
          <el-descriptions-item label="取件时间" :span="2" v-if="currentOrder.pickupTime">
            {{ currentOrder.pickupTime }}
          </el-descriptions-item>
          <el-descriptions-item label="完成时间" :span="2" v-if="currentOrder.completeTime">
            {{ currentOrder.completeTime }}
          </el-descriptions-item>
          <el-descriptions-item label="取消时间" :span="2" v-if="currentOrder.cancelTime">
            {{ currentOrder.cancelTime }}
          </el-descriptions-item>
          <el-descriptions-item label="物品描述" :span="2" v-if="currentOrder.itemDescription">
            {{ currentOrder.itemDescription }}
          </el-descriptions-item>
        </el-descriptions>
        <div v-if="itemImages.length > 0" class="item-images-section">
          <div class="section-title">物品图片</div>
          <div class="image-gallery">
            <el-image
              v-for="(img, index) in itemImages"
              :key="index"
              :src="img"
              :preview-src-list="itemImages"
              :initial-index="index"
              fit="cover"
              class="item-image"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                  <span>加载失败</span>
                </div>
              </template>
            </el-image>
          </div>
        </div>
        <div v-else-if="hasInvalidImages" class="item-images-section">
          <div class="section-title">物品图片</div>
          <div class="invalid-images-tip">
            <el-icon><WarningFilled /></el-icon>
            <span>该订单的物品图片为临时文件，无法在管理后台查看</span>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleDetailUpdateStatus">
            <el-icon style="margin-right: 4px;"><Edit /></el-icon>
            修改状态
          </el-button>
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog
      v-model="statusDialogVisible"
      title="修改订单状态"
      width="450px"
      class="status-dialog"
    >
      <div class="status-form">
        <div class="status-info" v-if="currentOrder">
          <div class="info-row">
            <span class="info-label">订单号：</span>
            <span class="info-value">{{ currentOrder.orderNo }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">当前状态：</span>
            <el-tag :type="getStatusTagType(currentOrder.status)" size="small">
              {{ statusLabelMap[currentOrder.status] }}
            </el-tag>
          </div>
        </div>
        <el-divider />
        <el-form label-width="100px">
          <el-form-item label="新状态：">
            <el-select v-model="newStatus" placeholder="请选择新状态" style="width: 100%">
              <el-option
                v-for="item in statusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
                <div class="status-option">
                  <span :class="['status-dot', getStatusDotClass(item.value)]"></span>
                  <span>{{ item.label }}</span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpdateStatus" :loading="statusUpdating">
          确认修改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Setting, ShoppingCart, Edit, Picture, WarningFilled } from '@element-plus/icons-vue'
import { getOrderList, getOrderDetail, batchDeleteOrders, updateOrderStatus, deleteOrder } from '@/api/order'
import PageHeader from '@/components/PageHeader.vue'

const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([])
const detailDialogVisible = ref(false)
const statusDialogVisible = ref(false)
const currentOrder = ref(null)
const totalCount = ref(0)
const newStatus = ref(null)
const statusUpdating = ref(false)

const statusOptions = [
  { value: 1, label: '待支付' },
  { value: 2, label: '待接单' },
  { value: 3, label: '服务中' },
  { value: 4, label: '已完成' },
  { value: 5, label: '已取消' }
]

const statusLabelMap = computed(() => {
  const map = {}
  statusOptions.forEach(item => {
    map[item.value] = item.label
  })
  return map
})

const itemImages = computed(() => {
  if (!currentOrder.value || !currentOrder.value.itemImages) {
    return []
  }
  try {
    const images = JSON.parse(currentOrder.value.itemImages)
    if (!Array.isArray(images)) {
      return []
    }
    return images.filter(img => {
      if (!img || typeof img !== 'string') {
        return false
      }
      if (img.startsWith('http://tmp/') || img.startsWith('https://tmp/') || 
          img.startsWith('tmp/') || img.startsWith('wxfile://')) {
        return false
      }
      return true
    })
  } catch (e) {
    console.error('解析物品图片失败:', e)
    return []
  }
})

const hasInvalidImages = computed(() => {
  if (!currentOrder.value || !currentOrder.value.itemImages) {
    return false
  }
  try {
    const images = JSON.parse(currentOrder.value.itemImages)
    if (!Array.isArray(images)) {
      return false
    }
    return images.some(img => {
      if (!img || typeof img !== 'string') {
        return false
      }
      return img.startsWith('http://tmp/') || img.startsWith('https://tmp/') || 
             img.startsWith('tmp/') || img.startsWith('wxfile://')
    })
  } catch (e) {
    return false
  }
})

const searchForm = reactive({
  keyword: '',
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

onMounted(() => {
  loadData()
  loadTotalCount()
})

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getOrderList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

// 加载全部订单总数（不受搜索条件影响）
const loadTotalCount = async () => {
  try {
    const params = {
      pageNum: 1,
      pageSize: 1
    }
    const res = await getOrderList(params)
    totalCount.value = res.data.total
  } catch (error) {
    console.error('获取订单总数失败:', error)
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = null
  pagination.pageNum = 1
  loadData()
}

const handleSizeChange = () => {
  pagination.pageNum = 1
  loadData()
}

const handleCurrentChange = () => {
  loadData()
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 计算序号
const getIndex = (index) => {
  return (pagination.pageNum - 1) * pagination.pageSize + index + 1
}

const getStatusTagType = (status) => {
  switch (status) {
    case 0:
      return 'info'
    case 1:
      return 'warning'
    case 2:
      return 'warning'
    case 3:
      return 'primary'
    case 4:
      return 'success'
    case 5:
      return 'danger'
    default:
      return ''
  }
}

const getStatusDotClass = (status) => {
  switch (status) {
    case 0:
      return 'closed'
    case 1:
    case 2:
      return 'running'
    case 3:
      return 'running'
    case 4:
      return 'online'
    case 5:
      return 'abnormal'
    default:
      return 'closed'
  }
}

const calculateDeliveryFee = (order) => {
  if (!order) return '0.00'
  const amount = Number(order.amount) || 0
  const platformFee = Number(order.platformFee) || 0
  const deliveryFee = amount - platformFee
  return deliveryFee.toFixed(2)
}

const handleViewDetail = async (row) => {
  try {
    const res = await getOrderDetail(row.id)
    currentOrder.value = res.data
    detailDialogVisible.value = true
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
  }
}

const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条记录')
    return
  }
  
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedRows.value.length} 条订单吗？此操作不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(async () => {
    try {
      const ids = selectedRows.value.map(row => row.id)
      await batchDeleteOrders(ids)
      ElMessage.success('批量删除成功')
      selectedRows.value = []
      loadData()
      loadTotalCount()
    } catch (error) {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除订单 "${row.orderNo}" 吗？此操作不可恢复！`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteOrder(row.id)
      ElMessage.success('删除成功')
      loadData()
      loadTotalCount()
    } catch (error) {
      console.error('删除订单失败:', error)
      ElMessage.error('删除订单失败')
    }
  }).catch(() => {})
}

const handleOpenStatusDialog = (row) => {
  currentOrder.value = row
  newStatus.value = row.status
  statusDialogVisible.value = true
}

const handleUpdateStatus = async () => {
  if (newStatus.value === currentOrder.value.status) {
    ElMessage.warning('状态未改变')
    return
  }
  
  statusUpdating.value = true
  try {
    await updateOrderStatus(currentOrder.value.id, newStatus.value)
    ElMessage.success('订单状态修改成功')
    statusDialogVisible.value = false
    loadData()
    if (detailDialogVisible.value) {
      const res = await getOrderDetail(currentOrder.value.id)
      currentOrder.value = res.data
    }
  } catch (error) {
    console.error('修改订单状态失败:', error)
    ElMessage.error('修改订单状态失败')
  } finally {
    statusUpdating.value = false
  }
}

const handleDetailUpdateStatus = () => {
  newStatus.value = currentOrder.value.status
  statusDialogVisible.value = true
}
</script>

<style scoped>
.action-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
}

.action-buttons .delete-btn {
  color: #ef4444;
}

.action-buttons .delete-btn:hover {
  color: #dc2626;
}

.order-detail {
  padding: 20px 0;
}

.detail-dialog :deep(.el-dialog) {
  border-radius: var(--qe-radius-xl);
  box-shadow: var(--qe-shadow-modal);
}

.detail-dialog :deep(.el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid var(--qe-border-lighter);
  background: var(--qe-surface);
}

.detail-dialog :deep(.el-dialog__body) {
  padding: 24px;
  background: var(--qe-bg);
}

.detail-dialog :deep(.el-dialog__footer) {
  padding: 16px 24px;
  border-top: 1px solid var(--qe-border-lighter);
  background: var(--qe-surface);
}

.batch-delete-btn {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%) !important;
  border: none !important;
  color: #ffffff !important;
  box-shadow: 0 4px 14px rgba(239, 68, 68, 0.25) !important;
}

.batch-delete-btn:hover {
  box-shadow: 0 6px 20px rgba(239, 68, 68, 0.35) !important;
  transform: translateY(-1px);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.status-dialog :deep(.el-dialog) {
  border-radius: var(--qe-radius-xl);
  box-shadow: var(--qe-shadow-modal);
}

.status-dialog :deep(.el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid var(--qe-border-lighter);
  background: var(--qe-surface);
}

.status-dialog :deep(.el-dialog__body) {
  padding: 24px;
  background: var(--qe-bg);
}

.status-dialog :deep(.el-dialog__footer) {
  padding: 16px 24px;
  border-top: 1px solid var(--qe-border-lighter);
  background: var(--qe-surface);
}

.status-form {
  padding: 0;
}

.status-info {
  background: var(--qe-surface);
  border-radius: var(--qe-radius-md);
  padding: 16px;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-label {
  color: var(--qe-text-muted);
  font-size: 14px;
  min-width: 80px;
}

.info-value {
  color: var(--qe-text);
  font-size: 14px;
  font-weight: 500;
}

.status-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
}

.status-dot.online {
  background-color: #10b981;
  box-shadow: 0 0 0 2px rgba(16, 185, 129, 0.2);
}

.status-dot.running {
  background-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
}

.status-dot.abnormal {
  background-color: #ef4444;
  box-shadow: 0 0 0 2px rgba(239, 68, 68, 0.2);
}

.status-dot.closed {
  background-color: #9ca3af;
  box-shadow: 0 0 0 2px rgba(156, 163, 175, 0.2);
}

.item-images-section {
  margin-top: 24px;
  padding: 16px;
  background: var(--qe-surface);
  border-radius: var(--qe-radius-md);
  border: 1px solid var(--qe-border-lighter);
}

.section-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--qe-text);
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--qe-border-lighter);
}

.image-gallery {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.item-image {
  width: 100px;
  height: 100px;
  border-radius: var(--qe-radius-md);
  overflow: hidden;
  cursor: pointer;
  border: 1px solid var(--qe-border-lighter);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.item-image:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: var(--qe-bg);
  color: var(--qe-text-muted);
  font-size: 12px;
}

.image-error .el-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.invalid-images-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #fef3c7;
  border-radius: var(--qe-radius-md);
  color: #92400e;
  font-size: 14px;
}

.invalid-images-tip .el-icon {
  font-size: 18px;
  color: #f59e0b;
}
</style>
