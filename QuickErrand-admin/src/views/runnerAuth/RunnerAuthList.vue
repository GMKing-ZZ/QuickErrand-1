<template>
  <div class="page-container">
    <el-breadcrumb separator="/" class="page-breadcrumb">
      <el-breadcrumb-item>列表页</el-breadcrumb-item>
      <el-breadcrumb-item>认证审核</el-breadcrumb-item>
    </el-breadcrumb>

    <PageHeader
      title="认证审核"
      subtitle="审核跑腿员认证申请，包括身份信息、资质证明等审核"
      :icon="Checked"
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
        <el-form-item label="认证状态:">
          <el-select
            v-model="searchForm.certStatus"
            placeholder="请选择"
            clearable
            style="width: 150px"
          >
            <el-option label="审核中" :value="1" />
            <el-option label="已认证" :value="2" />
            <el-option label="已驳回" :value="3" />
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
        <el-table-column prop="nickname" label="用户昵称" sortable align="center" />
        <el-table-column prop="phone" label="手机号" width="130" align="center" />
        <el-table-column prop="realName" label="真实姓名" align="center" />
        <el-table-column prop="idCard" label="身份证号" width="200" align="center" />
        <el-table-column prop="certStatusText" label="认证状态" sortable align="center">
          <template #default="{ row }">
            <span class="status-tag">
              <span :class="['status-dot', getCertStatusDotClass(row.certStatus)]"></span>
              {{ row.certStatusText }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="180" sortable align="center" />
        <el-table-column prop="certTime" label="认证时间" width="180" align="center" />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                type="text"
                @click="handleViewDetail(row)"
              >
                查看详情
              </el-button>
              <el-button
                v-if="row.certStatus === 1"
                type="text"
                @click="handleApprove(row)"
              >
                审核
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
      title="认证申请详情"
      width="800px"
      class="detail-dialog"
    >
      <div v-if="currentApplication" class="auth-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户昵称">
            {{ currentApplication.nickname }}
          </el-descriptions-item>
          <el-descriptions-item label="手机号">
            {{ currentApplication.phone }}
          </el-descriptions-item>
          <el-descriptions-item label="真实姓名">
            {{ currentApplication.realName }}
          </el-descriptions-item>
          <el-descriptions-item label="身份证号">
            {{ currentApplication.idCard }}
          </el-descriptions-item>
          <el-descriptions-item label="认证状态">
            <el-tag :type="getCertStatusTagType(currentApplication.certStatus)">
              {{ currentApplication.certStatusText }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请时间">
            {{ currentApplication.createTime }}
          </el-descriptions-item>
          <el-descriptions-item label="认证时间" v-if="currentApplication.certTime">
            {{ currentApplication.certTime }}
          </el-descriptions-item>
          <el-descriptions-item label="驳回原因" :span="2" v-if="currentApplication.rejectReason">
            {{ currentApplication.rejectReason }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="id-card-images">
          <div class="image-item">
            <div class="image-label">身份证正面</div>
            <el-image
              :src="currentApplication.idCardFront"
              :preview-src-list="[currentApplication.idCardFront]"
              fit="contain"
              style="width: 300px; height: 200px"
            />
          </div>
          <div class="image-item">
            <div class="image-label">身份证反面</div>
            <el-image
              :src="currentApplication.idCardBack"
              :preview-src-list="[currentApplication.idCardBack]"
              fit="contain"
              style="width: 300px; height: 200px"
            />
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="approveDialogVisible"
      title="审核认证申请"
      width="500px"
      class="form-dialog"
    >
      <el-form
        ref="approveFormRef"
        :model="approveForm"
        :rules="approveRules"
        label-width="100px"
      >
        <el-form-item label="审核结果" prop="certStatus">
          <el-radio-group v-model="approveForm.certStatus">
            <el-radio :label="2">通过</el-radio>
            <el-radio :label="3">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          label="驳回原因"
          prop="rejectReason"
          v-if="approveForm.certStatus === 3"
        >
          <el-input
            v-model="approveForm.rejectReason"
            type="textarea"
            :rows="4"
            placeholder="请输入驳回原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitApprove">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Setting, Checked } from '@element-plus/icons-vue'
import { getAuthApplicationList, approveAuth, batchDeleteAuthApplications, deleteAuthApplication } from '@/api/runnerAuth'
import PageHeader from '@/components/PageHeader.vue'

const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([])
const detailDialogVisible = ref(false)
const approveDialogVisible = ref(false)
const currentApplication = ref(null)
const approveFormRef = ref(null)

const searchForm = reactive({
  keyword: '',
  certStatus: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const approveForm = reactive({
  runnerInfoId: null,
  certStatus: 2,
  rejectReason: ''
})

const approveRules = {
  certStatus: [
    { required: true, message: '请选择审核结果', trigger: 'change' }
  ],
  rejectReason: [
    { required: true, message: '请输入驳回原因', trigger: 'blur' }
  ]
}

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
    const res = await getAuthApplicationList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取认证申请列表失败:', error)
    ElMessage.error('获取认证申请列表失败')
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
  searchForm.certStatus = null
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

const getCertStatusTagType = (certStatus) => {
  switch (certStatus) {
    case 0:
      return 'info'
    case 1:
      return 'warning'
    case 2:
      return 'success'
    case 3:
      return 'danger'
    default:
      return ''
  }
}

const getCertStatusDotClass = (certStatus) => {
  switch (certStatus) {
    case 1:
      return 'running'
    case 2:
      return 'online'
    case 3:
      return 'abnormal'
    default:
      return 'closed'
  }
}

const handleViewDetail = (row) => {
  currentApplication.value = row
  detailDialogVisible.value = true
}

const handleApprove = (row) => {
  approveForm.runnerInfoId = row.id
  approveForm.certStatus = 2
  approveForm.rejectReason = ''
  approveDialogVisible.value = true
}

const handleSubmitApprove = async () => {
  if (!approveFormRef.value) return

  // 如果是驳回，需要验证驳回原因
  if (approveForm.certStatus === 3) {
    await approveFormRef.value.validate(async (valid) => {
      if (valid) {
        await submitApprove()
      }
    })
  } else {
    await submitApprove()
  }
}

const submitApprove = async () => {
  try {
    await approveAuth(approveForm)
    ElMessage.success('审核成功')
    approveDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error('审核失败')
  }
}

const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条记录')
    return
  }
  
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedRows.value.length} 条认证申请吗？此操作不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(async () => {
    try {
      const ids = selectedRows.value.map(row => row.id)
      await batchDeleteAuthApplications(ids)
      ElMessage.success('批量删除成功')
      selectedRows.value = []
      loadData()
    } catch (error) {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除该认证申请记录吗？此操作不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(async () => {
    try {
      await deleteAuthApplication(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
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

.auth-detail {
  padding: 20px 0;
}

.id-card-images {
  margin-top: 20px;
  display: flex;
  gap: 20px;
  justify-content: center;
}

.image-item {
  text-align: center;
}

.image-label {
  margin-bottom: 10px;
  font-weight: 600;
  color: var(--qe-text-secondary);
}

.detail-dialog :deep(.el-dialog),
.form-dialog :deep(.el-dialog) {
  border-radius: var(--qe-radius-xl);
  box-shadow: var(--qe-shadow-modal);
}

.detail-dialog :deep(.el-dialog__header),
.form-dialog :deep(.el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid var(--qe-border-lighter);
  background: var(--qe-surface);
}

.detail-dialog :deep(.el-dialog__body),
.form-dialog :deep(.el-dialog__body) {
  padding: 24px;
  background: var(--qe-bg);
}

.detail-dialog :deep(.el-dialog__footer),
.form-dialog :deep(.el-dialog__footer) {
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

.delete-btn {
  color: #ef4444 !important;
}

.delete-btn:hover {
  color: #dc2626 !important;
}
</style>
