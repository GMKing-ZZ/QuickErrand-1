<template>
  <div class="page-container">
    <el-breadcrumb separator="/" class="page-breadcrumb">
      <el-breadcrumb-item>列表页</el-breadcrumb-item>
      <el-breadcrumb-item>收益结算管理</el-breadcrumb-item>
    </el-breadcrumb>

    <PageHeader
      title="收益结算管理"
      subtitle="查看并管理跑腿员收益记录，支持批量手动结算。"
      :icon="TrendCharts"
    />

    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="关键词:">
          <el-input
            v-model="searchForm.keyword"
            placeholder="用户ID / 订单号 / 备注"
            clearable
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item label="类型:">
          <el-select
            v-model="searchForm.type"
            placeholder="全部"
            clearable
            style="width: 150px"
          >
            <el-option label="订单收益" :value="1" />
            <el-option label="奖励" :value="2" />
            <el-option label="提现" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="结算状态:">
          <el-select
            v-model="searchForm.status"
            placeholder="全部"
            clearable
            style="width: 150px"
          >
            <el-option label="待结算" :value="1" />
            <el-option label="已结算" :value="2" />
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
          <el-button
            type="primary"
            :disabled="selectedRows.length === 0"
            @click="handleBatchSettle"
          >
            手动结算
          </el-button>
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
        <el-table-column prop="id" label="记录ID" width="90" align="center" />
        <el-table-column prop="userNickname" label="用户昵称" width="120" align="center" />
        <el-table-column prop="typeText" label="收益类型" width="100" align="center" />
        <el-table-column prop="orderNo" label="订单编号" width="130" show-overflow-tooltip align="center" />
        <el-table-column prop="amount" label="金额(¥)" width="110" align="center" />
        <el-table-column prop="status" label="结算状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag
              :type="row.status === 1 ? 'warning' : 'success'"
              size="small"
            >
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" sortable align="center" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip align="center" />
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-tag
                :type="row.status === 1 ? 'warning' : 'success'"
                size="small"
                :class="{ 'clickable': row.status === 1 }"
                @click="row.status === 1 && handleSingleSettle(row)"
              >
                {{ row.status === 1 ? '手动结算' : '已结算' }}
              </el-tag>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Setting, TrendCharts } from '@element-plus/icons-vue'
import { getEarningsList, settleEarnings, deleteEarningsById, deleteEarningsBatch } from '@/api/earnings'
import PageHeader from '@/components/PageHeader.vue'

const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([])

const searchForm = reactive({
  keyword: '',
  type: null,
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
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
    const res = await getEarningsList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取收益列表失败:', error)
    ElMessage.error('获取收益列表失败')
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
  searchForm.type = null
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

const doSettle = async (ids) => {
  if (!ids || ids.length === 0) return
  try {
    const count = await settleEarnings(ids)
    const updated = count?.data ?? 0
    if (updated > 0) {
      ElMessage.success(`手动结算成功，共更新 ${updated} 条记录`)
      loadData()
    } else {
      ElMessage.info('没有可结算的记录')
    }
  } catch (error) {
    console.error('手动结算失败:', error)
    ElMessage.error('手动结算失败')
  }
}

const handleSingleSettle = (row) => {
  if (row.status === 2) {
    ElMessage.info('该记录已结算')
    return
  }
  ElMessageBox.confirm(
    `确定要手动结算该收益记录吗？金额：¥${row.amount}`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    doSettle([row.id])
  }).catch(() => {})
}

const handleBatchSettle = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条待结算记录')
    return
  }
  const pendingIds = selectedRows.value
    .filter(row => row.status === 1)
    .map(row => row.id)

  if (pendingIds.length === 0) {
    ElMessage.info('选中的记录均已结算')
    return
  }

  ElMessageBox.confirm(
    `确定要手动结算选中的 ${pendingIds.length} 条收益记录吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    doSettle(pendingIds)
  }).catch(() => {})
}

const doDelete = async (ids) => {
  if (!ids || ids.length === 0) return
  try {
    if (ids.length === 1) {
      await deleteEarningsById(ids[0])
      ElMessage.success('删除成功')
    } else {
      const res = await deleteEarningsBatch(ids)
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
    `确定要删除该收益记录吗？金额：¥${row.amount}`,
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
    `确定要删除选中的 ${ids.length} 条收益记录吗？此操作不可恢复。`,
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

.action-buttons .clickable {
  cursor: pointer;
  transition: all 0.2s;
}

.action-buttons .clickable:hover {
  opacity: 0.8;
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
</style>

