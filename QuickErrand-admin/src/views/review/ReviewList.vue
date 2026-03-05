<template>
  <div class="page-container">
    <el-breadcrumb separator="/" class="page-breadcrumb">
      <el-breadcrumb-item>列表页</el-breadcrumb-item>
      <el-breadcrumb-item>评价管理</el-breadcrumb-item>
    </el-breadcrumb>

    <PageHeader
      title="评价管理"
      subtitle="管理系统用户评价信息，包括订单评价、评分等"
      :icon="Star"
    />

    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="关键词:">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入"
            clearable
            style="width: 250px"
          />
        </el-form-item>
        <el-form-item label="服务质量评分:">
          <el-select
            v-model="searchForm.serviceScore"
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
        <el-form-item label="状态:">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择"
            clearable
            style="width: 120px"
          >
            <el-option label="正常" :value="0" />
            <el-option label="已删除" :value="1" />
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
        <el-table-column prop="orderNo" label="订单编号" width="130" show-overflow-tooltip sortable align="center" />
        <el-table-column prop="userNickname" label="用户昵称" align="center" />
        <el-table-column prop="runnerName" label="跑腿员" align="center" />
        <el-table-column prop="serviceScore" label="服务质量评分" width="160" sortable align="center">
          <template #default="{ row }">
            <el-rate v-model="row.serviceScore" disabled show-score />
          </template>
        </el-table-column>
        <el-table-column prop="attitudeScore" label="服务态度评分" width="160" sortable align="center">
          <template #default="{ row }">
            <el-rate v-model="row.attitudeScore" disabled show-score />
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评价内容" show-overflow-tooltip align="center" />
        <el-table-column prop="status" label="状态" sortable align="center">
          <template #default="{ row }">
            <span class="status-tag">
              <span :class="['status-dot', row.status === 0 ? 'running' : 'closed']"></span>
              {{ row.status === 0 ? '正常' : '已删除' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="评价时间" width="180" sortable align="center" />
        <el-table-column label="操作" width="150" fixed="right" align="center">
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
      title="评价详情"
      width="700px"
      class="detail-dialog"
    >
      <div v-if="currentReview" class="review-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单编号">
            {{ currentReview.orderNo }}
          </el-descriptions-item>
          <el-descriptions-item label="用户昵称">
            {{ currentReview.userNickname }}
          </el-descriptions-item>
          <el-descriptions-item label="跑腿员">
            {{ currentReview.runnerName }}
          </el-descriptions-item>
          <el-descriptions-item label="服务质量评分">
            <el-rate v-model="currentReview.serviceScore" disabled show-score />
          </el-descriptions-item>
          <el-descriptions-item label="服务态度评分">
            <el-rate v-model="currentReview.attitudeScore" disabled show-score />
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentReview.status === 0 ? 'success' : 'info'">
              {{ currentReview.status === 0 ? '正常' : '已删除' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="评价时间" :span="2">
            {{ currentReview.createTime }}
          </el-descriptions-item>
          <el-descriptions-item label="评价内容" :span="2">
            {{ currentReview.content }}
          </el-descriptions-item>
        </el-descriptions>

        <div v-if="getTagList(currentReview.tags).length > 0" class="review-tags">
          <div class="tags-label">评价标签</div>
          <div class="tags-list">
            <el-tag
              v-for="(tag, index) in getTagList(currentReview.tags)"
              :key="index"
              type="primary"
              effect="light"
              class="tag-item"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>

        <div v-if="currentReview.images" class="review-images">
          <div class="images-label">评价图片</div>
          <div class="images-list">
            <el-image
              v-for="(image, index) in getImageList(currentReview.images)"
              :key="index"
              :src="image"
              :preview-src-list="getImageList(currentReview.images)"
              fit="cover"
              style="width: 100px; height: 100px; margin-right: 10px"
            />
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Setting, Star } from '@element-plus/icons-vue'
import {
  getReviewList,
  updateReviewStatus,
  deleteReview,
  batchDeleteReviews
} from '@/api/review'
import PageHeader from '@/components/PageHeader.vue'

const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([])
const detailDialogVisible = ref(false)
const currentReview = ref(null)

const searchForm = reactive({
  keyword: '',
  serviceScore: null,
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
    const res = await getReviewList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取评价列表失败:', error)
    ElMessage.error('获取评价列表失败')
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
  searchForm.serviceScore = null
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

const handleViewDetail = (row) => {
  currentReview.value = row
  detailDialogVisible.value = true
}

const handleStatusChange = async (row) => {
  try {
    await updateReviewStatus(row.id, row.status)
    ElMessage.success('更新状态成功')
    loadData()
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error('更新状态失败')
    row.status = row.status === 1 ? 0 : 1
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该评价吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteReview(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const getImageList = (images) => {
  if (!images) return []
  try {
    const parsed = JSON.parse(images)
    if (Array.isArray(parsed)) {
      return parsed
    }
  } catch (e) {
    // 如果不是JSON格式，尝试逗号分隔
  }
  return images.split(',').filter(url => url.trim())
}

const getTagList = (tags) => {
  if (!tags) return []
  try {
    const parsed = JSON.parse(tags)
    if (Array.isArray(parsed)) {
      return parsed.filter(tag => tag && tag.trim())
    }
  } catch (e) {
    // 如果不是JSON格式，尝试逗号分隔
  }
  return tags.split(',').filter(tag => tag.trim())
}

const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条记录')
    return
  }
  
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedRows.value.length} 条评价吗？此操作不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(async () => {
    try {
      const ids = selectedRows.value.map(row => row.id)
      await batchDeleteReviews(ids)
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

.action-buttons .delete-btn {
  color: var(--qe-danger);
}

.action-buttons .delete-btn:hover {
  color: var(--qe-danger-dark);
}

.review-detail {
  padding: 20px 0;
}

.review-tags {
  margin-top: 20px;
}

.tags-label {
  margin-bottom: 10px;
  font-weight: 600;
  color: var(--qe-text-secondary);
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  border-radius: 16px;
}

.review-images {
  margin-top: 20px;
}

.images-label {
  margin-bottom: 10px;
  font-weight: 600;
  color: var(--qe-text-secondary);
}

.images-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
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
</style>
