<template>
  <div class="page-container">
    <el-breadcrumb separator="/" class="page-breadcrumb">
      <el-breadcrumb-item>列表页</el-breadcrumb-item>
      <el-breadcrumb-item>提现审核管理</el-breadcrumb-item>
    </el-breadcrumb>

    <PageHeader
      title="提现审核管理"
      subtitle="审核跑腿员的提现申请，支持通过、驳回及标记已到账"
      :icon="Wallet"
    />

    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="关键词:">
          <el-input
            v-model="searchForm.keyword"
            placeholder="用户ID / 账户信息"
            clearable
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item label="状态:">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择"
            clearable
            style="width: 140px"
          >
            <el-option label="待审核" :value="1" />
            <el-option label="已通过" :value="2" />
            <el-option label="已驳回" :value="3" />
            <el-option label="已到账" :value="4" />
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
        <el-table-column prop="userNickname" label="用户昵称" width="120" align="center" />
        <el-table-column prop="amount" label="提现金额" align="center" />
        <el-table-column prop="fee" label="手续费" align="center" />
        <el-table-column prop="actualAmount" label="实际到账" align="center" />
        <el-table-column prop="accountTypeText" label="提现方式" align="center" />
        <el-table-column prop="accountInfo" label="账户信息" show-overflow-tooltip align="center" />
        <el-table-column prop="statusText" label="状态" align="center">
          <template #default="{ row }">
            <el-tag
              :type="getStatusTagType(row.status)"
              size="small"
            >
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="180" sortable align="center" />
        <el-table-column prop="auditTime" label="审核时间" width="180" sortable align="center" />
        <el-table-column prop="transferTime" label="转账时间" width="180" sortable align="center" />
        <el-table-column prop="rejectReason" label="驳回原因" show-overflow-tooltip align="center" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                v-if="row.status === 1"
                type="text"
                @click="handleApprove(row)"
              >
                通过
              </el-button>
              <el-button
                v-if="row.status === 1"
                type="text"
                class="delete-btn"
                @click="openRejectDialog(row)"
              >
                驳回
              </el-button>
              <el-button
                v-if="row.status === 2"
                type="text"
                @click="handleMarkTransferred(row)"
              >
                标记已到账
              </el-button>
              <el-button
                type="text"
                class="delete-btn"
                @click="handleSingleDelete(row)"
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

      <!-- 分页 -->
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

    <!-- 驳回对话框 -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="驳回提现申请"
      width="500px"
    >
      <el-form :model="rejectForm" label-width="90px">
        <el-form-item label="驳回原因">
          <el-input
            v-model="rejectForm.rejectReason"
            type="textarea"
            :rows="4"
            placeholder="请输入驳回原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRejectConfirm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Setting, Wallet } from '@element-plus/icons-vue'
import {
  getWithdrawalList,
  auditWithdrawal,
  markWithdrawalTransferred,
  deleteWithdrawalById,
  deleteWithdrawalBatch
} from '@/api/withdrawal'
import PageHeader from '@/components/PageHeader.vue'

const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([])

const searchForm = reactive({
  keyword: '',
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const rejectDialogVisible = ref(false)
const currentRow = ref(null)
const rejectForm = reactive({
  rejectReason: ''
})

onMounted(() => {
  loadData()
})

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getWithdrawalList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取提现列表失败:', error)
    ElMessage.error('获取提现列表失败')
  } finally {
    loading.value = false
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
  const map = {
    1: 'warning', // 待审核
    2: 'success', // 已通过
    3: 'danger',  // 已驳回
    4: 'info'     // 已到账
  }
  return map[status] || ''
}

const handleApprove = (row) => {
  ElMessageBox.confirm(
    `确定通过该提现申请吗？\n金额：¥${row.amount}，实际到账：¥${row.actualAmount}`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await auditWithdrawal({
        id: row.id,
        status: 2
      })
      ElMessage.success('审核通过成功')
      loadData()
    } catch (error) {
      console.error('审核通过失败:', error)
      ElMessage.error('审核通过失败')
    }
  }).catch(() => {})
}

const openRejectDialog = (row) => {
  currentRow.value = row
  rejectForm.rejectReason = ''
  rejectDialogVisible.value = true
}

const handleRejectConfirm = async () => {
  if (!rejectForm.rejectReason.trim()) {
    ElMessage.warning('请输入驳回原因')
    return
  }
  try {
    await auditWithdrawal({
      id: currentRow.value.id,
      status: 3,
      rejectReason: rejectForm.rejectReason
    })
    ElMessage.success('已驳回该提现申请')
    rejectDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('驳回失败:', error)
    ElMessage.error('驳回失败')
  }
}

const handleMarkTransferred = (row) => {
  ElMessageBox.confirm(
    `确认已向该账户完成转账？\n实际到账金额：¥${row.actualAmount}`,
    '确认已到账',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await markWithdrawalTransferred(row.id)
      ElMessage.success('已标记为已到账')
      loadData()
    } catch (error) {
      console.error('标记已到账失败:', error)
      ElMessage.error('标记已到账失败')
    }
  }).catch(() => {})
}

const doDelete = async (ids) => {
  if (!ids || ids.length === 0) return
  try {
    if (ids.length === 1) {
      await deleteWithdrawalById(ids[0])
      ElMessage.success('删除成功')
    } else {
      const res = await deleteWithdrawalBatch(ids)
      const deleted = res?.data ?? 0
      ElMessage.success(`批量删除成功，共删除 ${deleted} 条记录`)
    }
    loadData()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

const handleSingleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除该提现记录吗？金额：¥${row.amount}`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    doDelete([row.id])
  }).catch(() => {})
}

const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条记录')
    return
  }

  const ids = selectedRows.value.map(row => row.id)
  ElMessageBox.confirm(
    `确定要删除选中的 ${ids.length} 条提现记录吗？此操作不可恢复。`,
    '批量删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    doDelete(ids)
  }).catch(() => {})
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
  color: var(--qe-danger);
}

.action-buttons .delete-btn:hover {
  color: var(--qe-danger-dark);
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

.form-dialog :deep(.el-dialog) {
  border-radius: var(--qe-radius-xl);
  box-shadow: var(--qe-shadow-modal);
}

.form-dialog :deep(.el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid var(--qe-border-lighter);
  background: var(--qe-surface);
}

.form-dialog :deep(.el-dialog__body) {
  padding: 24px;
  background: var(--qe-bg);
}

.form-dialog :deep(.el-dialog__footer) {
  padding: 16px 24px;
  border-top: 1px solid var(--qe-border-lighter);
  background: var(--qe-surface);
}
</style>

