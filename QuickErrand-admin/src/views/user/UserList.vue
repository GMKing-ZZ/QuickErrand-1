<template>
  <div class="page-container">
    <el-breadcrumb separator="/" class="page-breadcrumb">
      <el-breadcrumb-item>列表页</el-breadcrumb-item>
      <el-breadcrumb-item>用户管理</el-breadcrumb-item>
    </el-breadcrumb>

    <PageHeader
      title="用户管理"
      subtitle="管理系统用户信息，包括普通用户、跑腿员和管理员"
      :icon="User"
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
        <el-form-item label="用户类型:">
          <el-select
            v-model="searchForm.userType"
            placeholder="请选择"
            clearable
            style="width: 150px"
          >
            <el-option label="普通用户" :value="1" />
            <el-option label="跑腿员" :value="2" />
            <el-option label="管理员" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态:">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择"
            clearable
            style="width: 120px"
          >
            <el-option label="正常" :value="1" />
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
        <el-table-column prop="username" label="用户名" sortable align="center" />
        <el-table-column prop="nickname" label="昵称" align="center" />
        <el-table-column prop="phone" label="手机号" align="center" />
        <el-table-column prop="userTypeText" label="用户类型" sortable align="center">
          <template #default="{ row }">
            <el-tag
              :type="getUserTypeTagType(row.userType)"
              size="small"
            >
              {{ row.userTypeText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="statusText" label="状态" sortable align="center">
          <template #default="{ row }">
            <span class="status-tag">
              <span :class="['status-dot', row.status === 1 ? 'running' : 'closed']"></span>
              {{ row.statusText }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" sortable align="center" />
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                v-if="row.status === 1 && row.userType !== 3"
                type="text"
                @click="handleDisable(row)"
              >
                禁用
              </el-button>
              <el-button
                v-if="row.status === 0 && row.userType !== 3"
                type="text"
                @click="handleEnable(row)"
              >
                启用
              </el-button>
              <el-button
                v-if="row.userType !== 3"
                type="text"
                class="delete-btn"
                @click="handleDelete(row)"
              >
                删除
              </el-button>
              <span v-if="row.userType === 3" style="color: var(--qe-text-disabled); font-size: 14px;">--</span>
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
      width="650px"
      class="user-form-dialog"
      @close="handleDialogClose"
    >
      <div class="form-description">
        用户表单用于创建或编辑用户信息，请填写完整的用户资料。
      </div>
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-position="top"
        class="user-form"
      >
        <div class="form-row">
          <div class="form-col">
            <el-form-item label="用户名：" prop="username">
              <el-input 
                v-model="form.username" 
                placeholder="请输入用户名"
                clearable
                style="width: 280px"
              />
            </el-form-item>
          </div>
          <div class="form-col">
            <el-form-item label="手机号：" prop="phone">
              <el-input 
                v-model="form.phone" 
                placeholder="请输入手机号"
                clearable
                style="width: 200px"
              />
            </el-form-item>
          </div>
        </div>
        
        <div class="form-row">
          <div class="form-col">
            <el-form-item label="昵称：" prop="nickname">
              <el-input 
                v-model="form.nickname" 
                placeholder="请输入昵称"
                clearable
                style="width: 280px"
              />
            </el-form-item>
          </div>
          <div class="form-col">
            <el-form-item label="密码：" prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                show-password
                clearable
                style="width: 280px"
              />
            </el-form-item>
          </div>
        </div>

        <div class="form-row">
          <div class="form-col form-col-full">
            <el-form-item label="头像：" prop="avatar">
              <div class="avatar-upload">
                <el-upload
                  class="avatar-uploader"
                  :http-request="handleAvatarUpload"
                  :show-file-list="false"
                  :before-upload="beforeAvatarUpload"
                >
                  <img v-if="form.avatar" :src="form.avatar" class="avatar" />
                  <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                </el-upload>
                <span class="avatar-tip">支持jpg、png格式，大小不超过2MB</span>
              </div>
            </el-form-item>
          </div>
        </div>

        <div class="form-row">
          <div class="form-col">
            <el-form-item label="性别：" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio :label="0">未知</el-radio>
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
          <div class="form-col">
            <el-form-item label="生日：" prop="birthday">
              <el-date-picker
                v-model="form.birthday"
                type="date"
                placeholder="请选择生日"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 200px"
              />
            </el-form-item>
          </div>
        </div>

        <div class="form-row">
          <div class="form-col">
            <el-form-item label="用户类型：" prop="userType">
              <el-select v-model="form.userType" placeholder="请选择用户类型" style="width: 200px">
                <el-option label="普通用户" :value="1" />
                <el-option label="跑腿员" :value="2" />
                <el-option label="管理员" :value="3" />
              </el-select>
            </el-form-item>
          </div>
          <div class="form-col">
            <el-form-item label="状态：" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :label="1">正常</el-radio>
                <el-radio :label="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
        </div>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleFormReset">重置</el-button>
          <el-button type="primary" @click="handleSubmit">提交</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Setting, User } from '@element-plus/icons-vue'
import { getUserList, updateUserStatus, createUser, deleteUser, batchDeleteUsers } from '@/api/user'
import { uploadFile } from '@/api/file'
import PageHeader from '@/components/PageHeader.vue'

const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新建用户')
const formRef = ref(null)
const totalCount = ref(0)

// 默认头像URL
const DEFAULT_AVATAR = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const form = reactive({
  id: null,
  username: '',
  phone: '',
  nickname: '',
  password: '',
  avatar: DEFAULT_AVATAR,
  gender: 0,
  birthday: null,
  userType: 1,
  status: 1
})

const handleAvatarUpload = async (options) => {
  try {
    const res = await uploadFile(options.file)
    console.log('上传响应:', res)
    if (res && res.data) {
      // 确保使用完整的URL
      form.avatar = res.data
      console.log('头像URL:', form.avatar)
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error('上传响应数据格式错误')
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败: ' + (error.message || '未知错误'))
  }
}

const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('头像只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  return true
}

const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { max: 20, message: '昵称长度不能超过20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  userType: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

const searchForm = reactive({
  keyword: '',
  userType: null,
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
    const res = await getUserList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 加载全部用户总数（不受搜索条件影响）
const loadTotalCount = async () => {
  try {
    const params = {
      pageNum: 1,
      pageSize: 1
    }
    const res = await getUserList(params)
    totalCount.value = res.data.total
  } catch (error) {
    console.error('获取用户总数失败:', error)
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.userType = null
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

const getUserTypeTagType = (userType) => {
  switch (userType) {
    case 1:
      return ''
    case 2:
      return 'warning'
    case 3:
      return 'danger'
    default:
      return ''
  }
}

const handleDisable = (row) => {
  ElMessageBox.confirm(
    `确定要禁用用户"${row.nickname || row.username}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await updateUserStatus(row.id, 0)
      ElMessage.success('禁用成功')
      loadData()
      loadTotalCount()
    } catch (error) {
      console.error('禁用用户失败:', error)
      ElMessage.error('禁用失败')
    }
  })
}

const handleEnable = (row) => {
  ElMessageBox.confirm(
    `确定要启用用户"${row.nickname || row.username}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(async () => {
    try {
      await updateUserStatus(row.id, 1)
      ElMessage.success('启用成功')
      loadData()
      loadTotalCount()
    } catch (error) {
      console.error('启用用户失败:', error)
      ElMessage.error('启用失败')
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除用户"${row.nickname || row.username}"吗？此操作不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      loadData()
      loadTotalCount()
    } catch (error) {
      console.error('删除用户失败:', error)
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }).catch(() => {})
}

const handleAdd = () => {
  dialogTitle.value = '新建用户'
  form.id = null
  form.username = ''
  form.phone = ''
  form.nickname = ''
  form.password = ''
  form.avatar = DEFAULT_AVATAR
  form.gender = 0
  form.birthday = null
  form.userType = 1
  form.status = 1
  dialogVisible.value = true
}


const handleDialogClose = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

const handleFormReset = () => {
  if (formRef.value) {
    formRef.value.resetFields()
    // 重置表单数据
    form.id = null
    form.username = ''
    form.phone = ''
    form.nickname = ''
    form.password = ''
    form.avatar = DEFAULT_AVATAR
    form.gender = 0
    form.birthday = null
    form.userType = 1
    form.status = 1
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    await createUser(form)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    loadData()
    loadTotalCount()
  } catch (error) {
    if (error !== false) {
      console.error('创建用户失败:', error)
      ElMessage.error(error.response?.data?.message || '创建失败')
    }
  }
}

const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条记录')
    return
  }
  
  // 检查是否包含管理员
  const hasAdmin = selectedRows.value.some(row => row.userType === 3)
  if (hasAdmin) {
    ElMessage.warning('不能删除管理员用户')
    return
  }
  
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedRows.value.length} 个用户吗？此操作不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(async () => {
    try {
      const ids = selectedRows.value.map(row => row.id)
      await batchDeleteUsers(ids)
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

.user-form-dialog :deep(.el-dialog) {
  border-radius: var(--qe-radius-xl);
  box-shadow: var(--qe-shadow-modal);
  overflow: hidden;
}

.user-form-dialog :deep(.el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid var(--qe-border-lighter);
  background: var(--qe-surface);
}

.user-form-dialog :deep(.el-dialog__title) {
  font-size: 16px;
  font-weight: 600;
  color: var(--qe-text);
}

.user-form-dialog :deep(.el-dialog__body) {
  padding: 24px;
  background: var(--qe-bg);
}

.user-form-dialog :deep(.el-dialog__footer) {
  padding: 16px 24px;
  border-top: 1px solid var(--qe-border-lighter);
  background: var(--qe-surface);
}

.form-description {
  color: var(--qe-text-muted);
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--qe-border-lighter);
}

.form-row {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.form-row:last-child {
  margin-bottom: 0;
}

.form-col {
  flex: 1;
  min-width: 0;
}

.form-col-full {
  flex: 1 1 100%;
}

.user-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.user-form :deep(.el-form-item__label) {
  color: var(--qe-text-secondary);
  font-weight: 500;
  font-size: 14px;
  padding: 0 0 8px 0;
  margin-bottom: 0;
}

.user-form :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px var(--qe-border) inset;
  border-radius: var(--qe-radius-md);
  transition: all var(--qe-transition-fast);
}

.user-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--qe-primary-light) inset;
}

.user-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--qe-primary) inset;
}

.user-form :deep(.el-radio-group) {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-form :deep(.el-radio__input.is-checked .el-radio__inner) {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border-color: var(--qe-primary);
}

.avatar-upload {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 8px 0;
}

.avatar-uploader {
  border: 1px dashed var(--qe-border);
  border-radius: var(--qe-radius-lg);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all var(--qe-transition-fast);
  width: 104px;
  height: 104px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--qe-surface);
}

.avatar-uploader:hover {
  border-color: var(--qe-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: var(--qe-text-muted);
}

.avatar {
  width: 104px;
  height: 104px;
  display: block;
  object-fit: cover;
}

.avatar-tip {
  color: var(--qe-text-muted);
  font-size: 14px;
  line-height: 1.5;
  margin-top: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.dialog-footer .el-button {
  border-radius: var(--qe-radius-md);
  height: 36px;
  padding: 0 20px;
  font-size: 14px;
  font-weight: 500;
  transition: all var(--qe-transition-fast);
  min-width: 80px;
}

.dialog-footer .el-button--primary {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  box-shadow: var(--qe-primary-shadow);
}

.dialog-footer .el-button--primary:hover {
  box-shadow: var(--qe-primary-shadow-hover);
  transform: translateY(-1px);
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
