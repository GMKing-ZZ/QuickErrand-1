<template>
  <div class="page-container">
    <el-breadcrumb separator="/" class="page-breadcrumb">
      <el-breadcrumb-item>列表页</el-breadcrumb-item>
      <el-breadcrumb-item>轮播图管理</el-breadcrumb-item>
    </el-breadcrumb>

    <PageHeader
      title="轮播图管理"
      subtitle="管理系统轮播图信息，包括首页轮播图、个人中心轮播图等"
      :icon="Picture"
    />

    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="关键词:">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入标题"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="展示位置:">
          <el-select
            v-model="searchForm.position"
            placeholder="请选择"
            clearable
            style="width: 150px"
          >
            <el-option label="首页" :value="1" />
            <el-option label="个人中心" :value="2" />
          </el-select>
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
        <el-table-column type="index" label="序号" width="55" align="center" :index="getIndex" />
        <el-table-column prop="title" label="标题" sortable align="center" />
        <el-table-column label="图片" align="center" width="150">
          <template #default="{ row }">
            <el-image
              v-if="row.imageUrl"
              :src="row.imageUrl"
              :preview-src-list="[row.imageUrl]"
              style="width: 100px; height: 60px; object-fit: cover; border-radius: var(--qe-radius-md);"
              fit="cover"
            />
            <span v-else style="color: var(--qe-text-muted);">暂无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="position" label="展示位置" sortable align="center">
          <template #default="{ row }">
            <el-tag :type="getPositionTagType(row.position)">
              {{ getPositionText(row.position) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" sortable align="center" width="80" />
        <el-table-column prop="status" label="状态" sortable align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="linkUrl" label="跳转链接" align="center" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" sortable align="center" />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons" style="display: flex; justify-content: center; align-items: center;">
              <el-button
                type="text"
                @click="handleEdit(row)"
              >
                编辑
              </el-button>
              <el-button
                type="text"
                @click="handleToggleStatus(row)"
              >
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button
                type="text"
                style="color: #ff4d4f;"
                @click="handleDelete(row)"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 表格底部 -->
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入轮播图标题" />
        </el-form-item>
        <el-form-item label="图片" prop="imageUrl">
          <div class="image-upload-wrapper">
            <el-upload
              class="image-uploader"
              :http-request="handleImageUpload"
              :show-file-list="false"
              :before-upload="beforeImageUpload"
            >
              <img v-if="form.imageUrl" :src="form.imageUrl" class="uploaded-image" />
              <el-icon v-else class="image-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div v-if="form.imageUrl" class="image-actions">
              <el-button size="small" type="danger" @click="handleRemoveImage">删除图片</el-button>
            </div>
            <div class="upload-tip">支持jpg、jpeg、png、gif格式，大小不超过10MB</div>
          </div>
        </el-form-item>
        <el-form-item label="跳转链接" prop="linkUrl">
          <el-input v-model="form.linkUrl" placeholder="请输入跳转链接（可选）" />
        </el-form-item>
        <el-form-item label="展示位置" prop="position">
          <el-radio-group v-model="form.position">
            <el-radio :label="1">首页</el-radio>
            <el-radio :label="2">个人中心</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" placeholder="数字越小越靠前" />
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
import { Plus, Setting, Picture } from '@element-plus/icons-vue'
import {
  getBannerList,
  createBanner,
  updateBanner,
  deleteBanner,
  updateBannerStatus,
  batchDeleteBanners
} from '@/api/banner'
import { uploadBannerImage } from '@/api/file'
import PageHeader from '@/components/PageHeader.vue'

const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const searchForm = reactive({
  keyword: '',
  position: null,
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  title: '',
  imageUrl: '',
  linkUrl: '',
  position: 1,
  sortOrder: 0,
  status: 1
})

const formRules = {
  title: [
    { required: true, message: '请输入轮播图标题', trigger: 'blur' }
  ],
  imageUrl: [
    { required: true, message: '请输入图片URL', trigger: 'blur' }
  ],
  position: [
    { required: true, message: '请选择展示位置', trigger: 'change' }
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
    const res = await getBannerList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取轮播图列表失败:', error)
    ElMessage.error('获取轮播图列表失败')
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
  searchForm.position = null
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

const getPositionText = (position) => {
  const positionMap = {
    1: '首页',
    2: '个人中心'
  }
  return positionMap[position] || '未知'
}

const getPositionTagType = (position) => {
  const positionMap = {
    1: 'primary',
    2: 'success'
  }
  return positionMap[position] || ''
}

const handleAdd = () => {
  dialogTitle.value = '新增轮播图'
  form.id = null
  form.title = ''
  form.imageUrl = ''
  form.linkUrl = ''
  form.position = 1
  form.sortOrder = 0
  form.status = 1
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑轮播图'
  form.id = row.id
  form.title = row.title
  form.imageUrl = row.imageUrl
  form.linkUrl = row.linkUrl || ''
  form.position = row.position
  form.sortOrder = row.sortOrder
  form.status = row.status
  dialogVisible.value = true
}

const handleDialogClose = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await updateBanner(form.id, form)
          ElMessage.success('更新轮播图成功')
        } else {
          await createBanner(form)
          ElMessage.success('创建轮播图成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('保存轮播图失败:', error)
        ElMessage.error('保存轮播图失败')
      }
    }
  })
}

const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await updateBannerStatus(row.id, newStatus)
    ElMessage.success(newStatus === 1 ? '启用成功' : '禁用成功')
    loadData()
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error('更新状态失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该轮播图吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteBanner(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条记录')
    return
  }
  
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedRows.value.length} 条轮播图吗？此操作不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(async () => {
    try {
      const ids = selectedRows.value.map(row => row.id)
      await batchDeleteBanners(ids)
      ElMessage.success('批量删除成功')
      selectedRows.value = []
      loadData()
    } catch (error) {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }).catch(() => {})
}

const handleImageUpload = async (options) => {
  try {
    const res = await uploadBannerImage(options.file)
    if (res && res.data) {
      form.imageUrl = res.data
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error('上传响应数据格式错误')
    }
  } catch (error) {
    console.error('图片上传失败:', error)
    ElMessage.error('图片上传失败: ' + (error.message || '未知错误'))
  }
}

const beforeImageUpload = (file) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/jpg' || 
                  file.type === 'image/png' || file.type === 'image/gif'
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件（jpg、jpeg、png、gif）')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过10MB')
    return false
  }
  return true
}

const handleRemoveImage = () => {
  form.imageUrl = ''
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

.image-upload-wrapper {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.image-uploader {
  width: 200px;
  height: 120px;
  border: 1px dashed var(--qe-border);
  border-radius: var(--qe-radius-md);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all var(--qe-transition-fast);
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--qe-surface);
}

.image-uploader:hover {
  border-color: var(--qe-primary);
}

.image-uploader-icon {
  font-size: 28px;
  color: var(--qe-text-muted);
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.uploaded-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.image-actions {
  display: flex;
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
</style>
