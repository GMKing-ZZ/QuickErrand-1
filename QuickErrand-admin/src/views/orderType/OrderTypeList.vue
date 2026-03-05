<template>
  <div class="page-container">
    <el-breadcrumb separator="/" class="page-breadcrumb">
      <el-breadcrumb-item>列表页</el-breadcrumb-item>
      <el-breadcrumb-item>订单类型管理</el-breadcrumb-item>
    </el-breadcrumb>

    <PageHeader
      title="订单类型管理"
      subtitle="管理系统订单类型，包括基础价格、每公里价格等配置"
      :icon="Document"
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
        <el-form-item label="状态:">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择"
            clearable
            style="width: 120px"
          >
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
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
          <el-button type="primary" :icon="Plus" @click="handleAdd">新建</el-button>
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
        <el-table-column type="index" label="序号" width="55" align="center" />
        <el-table-column prop="typeName" label="类型名称" sortable align="center" />
        <el-table-column prop="typeIcon" label="类型图标" show-overflow-tooltip align="center">
          <template #default="{ row }">
            <span v-if="row.typeIcon">{{ row.typeIcon }}</span>
            <span v-else style="color: var(--qe-text-muted);">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="typeDesc" label="类型描述" show-overflow-tooltip align="center" />
        <el-table-column prop="sortOrder" label="排序" sortable align="center" />
        <el-table-column prop="status" label="状态" sortable align="center">
          <template #default="{ row }">
            <span class="status-tag">
              <span :class="['status-dot', row.status === 1 ? 'running' : 'closed']"></span>
              {{ row.status === 1 ? '启用' : '禁用' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" sortable align="center" />
        <el-table-column prop="updateTime" label="更新时间" width="180" sortable align="center" />
        <el-table-column label="操作" width="250" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                type="text"
                @click="handleEdit(row)"
              >
                编辑
              </el-button>
              <el-button
                v-if="row.status === 1"
                type="text"
                @click="handleDisable(row)"
              >
                禁用
              </el-button>
              <el-button
                v-if="row.status === 0"
                type="text"
                @click="handleEnable(row)"
              >
                启用
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
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      class="form-dialog"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="类型名称" prop="typeName">
          <el-input v-model="form.typeName" placeholder="请输入类型名称" />
        </el-form-item>
        <el-form-item label="类型图标" prop="typeIcon">
          <el-input v-model="form.typeIcon" placeholder="请输入类型图标路径" />
        </el-form-item>
        <el-form-item label="类型描述" prop="typeDesc">
          <el-input
            v-model="form.typeDesc"
            type="textarea"
            :rows="3"
            placeholder="请输入类型描述"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number
            v-model="form.sortOrder"
            :min="0"
            placeholder="请输入排序值"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Setting, Document } from '@element-plus/icons-vue'
import {
  getOrderTypeList,
  createOrderType,
  updateOrderType,
  deleteOrderType,
  updateOrderTypeStatus,
  batchDeleteOrderTypes
} from '@/api/orderType'
import PageHeader from '@/components/PageHeader.vue'

const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const isEdit = ref(false)
const totalCount = ref(0)

const searchForm = reactive({
  keyword: '',
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  typeName: '',
  typeIcon: '',
  typeDesc: '',
  sortOrder: 0,
  status: 1
})

const rules = {
  typeName: [
    { required: true, message: '请输入类型名称', trigger: 'blur' }
  ],
  typeDesc: [
    { required: true, message: '请输入类型描述', trigger: 'blur' }
  ]
}

onMounted(async () => {
  // 先加载总数，再加载列表数据
  await loadTotalCount()
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
    const res = await getOrderTypeList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
    
    // 如果没有搜索条件，更新全部总数
    if (!searchForm.keyword && searchForm.status === null) {
      totalCount.value = res.data.total
    }
  } catch (error) {
    console.error('获取订单类型列表失败:', error)
    ElMessage.error('获取订单类型列表失败')
  } finally {
    loading.value = false
  }
}

// 加载全部订单类型总数（不受搜索条件影响）
const loadTotalCount = async () => {
  try {
    const params = {
      pageNum: 1,
      pageSize: 1
    }
    const res = await getOrderTypeList(params)
    console.log('loadTotalCount 响应数据:', res)
    if (res && res.data) {
      if (typeof res.data.total === 'number') {
        totalCount.value = res.data.total
        console.log('订单类型总数已更新:', totalCount.value)
      } else {
        console.warn('获取订单类型总数：total 字段不是数字', res.data)
      }
    } else {
      console.warn('获取订单类型总数：返回数据格式异常', res)
    }
  } catch (error) {
    console.error('获取订单类型总数失败:', error)
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

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增订单类型'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑订单类型'
  Object.assign(form, row)
  dialogVisible.value = true
}

const resetForm = () => {
  form.id = null
  form.typeName = ''
  form.typeIcon = ''
  form.typeDesc = ''
  form.sortOrder = 0
  form.status = 1
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          await updateOrderType(form.id, form)
          ElMessage.success('更新成功')
        } else {
          await createOrderType(form)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadData()
        loadTotalCount()
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
      }
    }
  })
}

const handleDisable = (row) => {
  ElMessageBox.confirm(
    `确定要禁用订单类型"${row.typeName}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await updateOrderTypeStatus(row.id, 0)
      ElMessage.success('禁用成功')
      loadData()
      loadTotalCount()
    } catch (error) {
      console.error('禁用失败:', error)
      ElMessage.error('禁用失败')
    }
  })
}

const handleEnable = (row) => {
  ElMessageBox.confirm(
    `确定要启用订单类型"${row.typeName}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(async () => {
    try {
      await updateOrderTypeStatus(row.id, 1)
      ElMessage.success('启用成功')
      loadData()
      loadTotalCount()
    } catch (error) {
      console.error('启用失败:', error)
      ElMessage.error('启用失败')
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除订单类型"${row.typeName}"吗？此操作不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(async () => {
    try {
      await deleteOrderType(row.id)
      ElMessage.success('删除成功')
      loadData()
      loadTotalCount()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条记录')
    return
  }
  
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedRows.value.length} 个订单类型吗？此操作不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(async () => {
    try {
      const ids = selectedRows.value.map(row => row.id)
      await batchDeleteOrderTypes(ids)
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
