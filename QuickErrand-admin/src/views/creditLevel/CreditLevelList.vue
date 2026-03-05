<template>
  <div class="page-container">
    <el-breadcrumb separator="/" class="page-breadcrumb">
      <el-breadcrumb-item>列表页</el-breadcrumb-item>
      <el-breadcrumb-item>信用等级管理</el-breadcrumb-item>
    </el-breadcrumb>

    <PageHeader
      title="信用等级管理"
      subtitle="管理系统用户信用等级，包括等级配置、信用积分等"
      :icon="Trophy"
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
        <el-form-item label="信用等级:">
          <el-select
            v-model="searchForm.creditLevel"
            placeholder="请选择"
            clearable
            style="width: 120px"
          >
            <el-option label="5星" :value="5" />
            <el-option label="4星" :value="4" />
            <el-option label="3星" :value="3" />
            <el-option label="2星" :value="2" />
            <el-option label="1星" :value="1" />
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
          <el-button @click="handleRecalculateAll">批量重算</el-button>
          <span v-if="selectedRows.length > 0" style="margin-left: 16px; color: var(--qe-text-muted);">
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
        <el-table-column prop="realName" label="真实姓名" width="120" sortable align="center" />
        <el-table-column prop="phone" label="手机号" width="130" align="center" />
        <el-table-column prop="creditLevel" label="信用等级" width="170" sortable align="center">
          <template #default="{ row }">
            <el-rate v-model="row.creditLevel" disabled show-score />
          </template>
        </el-table-column>
        <el-table-column prop="totalOrders" label="完成订单数" width="120" sortable align="center" />
        <el-table-column prop="goodRate" label="好评率" width="100" sortable align="center">
          <template #default="{ row }">
            {{ row.goodRate ? row.goodRate.toFixed(2) + '%' : '0.00%' }}
          </template>
        </el-table-column>
        <el-table-column prop="serviceTime" label="服务时间" min-width="150" show-overflow-tooltip align="center" />
        <el-table-column prop="serviceRange" label="服务范围" width="100" sortable align="center">
          <template #default="{ row }">
            {{ row.serviceRange ? (row.serviceRange / 1000).toFixed(1) + 'km' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="认证时间" width="180" sortable align="center" />
        <el-table-column label="操作" width="250" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                type="text"
                @click="handleEdit(row)"
              >
                调整等级
              </el-button>
              <el-button
                type="text"
                @click="handleRecalculate(row)"
              >
                重新计算
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
      v-model="editDialogVisible"
      title="调整信用等级"
      width="500px"
      class="form-dialog"
    >
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="跑腿员">
          {{ editForm.realName }}
        </el-form-item>
        <el-form-item label="当前等级">
          <el-rate v-model="editForm.currentLevel" disabled show-score />
        </el-form-item>
        <el-form-item label="新等级">
          <el-rate v-model="editForm.newLevel" show-score />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveEdit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Setting, Trophy } from '@element-plus/icons-vue'
import {
  getRunnerCreditList,
  updateCreditLevel,
  recalculateCreditLevel,
  recalculateAllCreditLevels,
  batchDeleteCreditLevels
} from '@/api/creditLevel'
import PageHeader from '@/components/PageHeader.vue'

const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([])
const editDialogVisible = ref(false)

const searchForm = reactive({
  keyword: '',
  creditLevel: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const editForm = reactive({
  runnerId: null,
  realName: '',
  currentLevel: 0,
  newLevel: 0
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
    const res = await getRunnerCreditList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取信用等级列表失败:', error)
    ElMessage.error('获取信用等级列表失败')
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
  searchForm.creditLevel = null
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

const handleEdit = (row) => {
  editForm.runnerId = row.id
  editForm.realName = row.realName
  editForm.currentLevel = row.creditLevel
  editForm.newLevel = row.creditLevel
  editDialogVisible.value = true
}

const handleSaveEdit = async () => {
  if (editForm.newLevel < 1 || editForm.newLevel > 5) {
    ElMessage.warning('信用等级必须在1-5之间')
    return
  }

  try {
    await updateCreditLevel(editForm.runnerId, editForm.newLevel)
    ElMessage.success('调整信用等级成功')
    editDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('调整信用等级失败:', error)
    ElMessage.error('调整信用等级失败')
  }
}

const handleRecalculate = (row) => {
  ElMessageBox.confirm('确定要重新计算该跑腿员的信用等级吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await recalculateCreditLevel(row.id)
      ElMessage.success(`重新计算成功，新等级为${res.data}星`)
      loadData()
    } catch (error) {
      console.error('重新计算失败:', error)
      ElMessage.error('重新计算失败')
    }
  }).catch(() => {})
}

const handleRecalculateAll = () => {
  ElMessageBox.confirm('确定要批量重新计算所有跑腿员的信用等级吗？此操作可能需要较长时间。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    loading.value = true
    try {
      await recalculateAllCreditLevels()
      ElMessage.success('批量重新计算成功')
      loadData()
    } catch (error) {
      console.error('批量重新计算失败:', error)
      ElMessage.error('批量重新计算失败')
    } finally {
      loading.value = false
    }
  }).catch(() => {})
}

const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条记录')
    return
  }
  
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedRows.value.length} 条信用等级记录吗？此操作不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(async () => {
    try {
      const ids = selectedRows.value.map(row => row.id)
      await batchDeleteCreditLevels(ids)
      ElMessage.success('批量删除成功')
      selectedRows.value = []
      loadData()
    } catch (error) {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
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
</style>

