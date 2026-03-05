<template>
  <div class="page-container">
    <el-breadcrumb separator="/" class="page-breadcrumb">
      <el-breadcrumb-item>列表页</el-breadcrumb-item>
      <el-breadcrumb-item>消息管理</el-breadcrumb-item>
    </el-breadcrumb>

    <PageHeader
      title="消息管理"
      subtitle="管理系统消息通知，支持发送系统消息、查看消息列表等"
      :icon="ChatDotRound"
    />

    <el-row :gutter="20" class="stats-row">
      <el-col :span="4">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-value">{{ stats.totalCount || 0 }}</div>
            <div class="stats-label">消息总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card unread">
          <div class="stats-content">
            <div class="stats-value">{{ stats.unreadCount || 0 }}</div>
            <div class="stats-label">未读消息</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card order">
          <div class="stats-content">
            <div class="stats-value">{{ stats.orderMessageCount || 0 }}</div>
            <div class="stats-label">订单消息</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card system">
          <div class="stats-content">
            <div class="stats-value">{{ stats.systemMessageCount || 0 }}</div>
            <div class="stats-label">系统消息</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stats-card chat">
          <div class="stats-content">
            <div class="stats-value">{{ stats.chatMessageCount || 0 }}</div>
            <div class="stats-label">聊天消息</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="关键词:">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索标题或内容"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="消息类型:">
          <el-select
            v-model="searchForm.type"
            placeholder="请选择"
            clearable
            style="width: 150px"
          >
            <el-option label="订单消息" :value="1" />
            <el-option label="系统消息" :value="2" />
            <el-option label="聊天消息" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户ID:">
          <el-input
            v-model="searchForm.userId"
            placeholder="请输入用户ID"
            clearable
            style="width: 150px"
          />
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
          <el-button type="primary" :icon="Plus" @click="handleSend">发送消息</el-button>
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
        <el-table-column prop="title" label="消息标题" min-width="150" show-overflow-tooltip />
        <el-table-column prop="content" label="消息内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="userName" label="接收用户" width="120" align="center">
          <template #default="{ row }">
            <span v-if="row.userId">{{ row.userName || '用户' + row.userId }}</span>
            <el-tag v-else type="info" size="small">全部用户</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="消息类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)">
              {{ row.typeText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isRead" label="阅读状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isRead === 1 ? 'success' : 'warning'" size="small">
              {{ row.isRead === 1 ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" align="center" sortable />
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
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
      title="发送系统消息"
      width="600px"
      class="form-dialog"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="发送方式" prop="sendType">
          <el-radio-group v-model="form.sendType">
            <el-radio label="all">发送给所有用户</el-radio>
            <el-radio label="user">发送给指定用户</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item 
          v-if="form.sendType === 'user'" 
          label="用户ID" 
          prop="userIds"
        >
          <el-select
            v-model="form.userIds"
            multiple
            filterable
            remote
            reserve-keyword
            placeholder="请输入用户ID或手机号搜索"
            :remote-method="searchUsers"
            :loading="userSearchLoading"
            style="width: 100%"
          >
            <el-option
              v-for="user in userOptions"
              :key="user.id"
              :label="user.nickname || user.phone"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="消息标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入消息标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="消息内容" prop="content">
          <el-input 
            v-model="form.content" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入消息内容"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Setting, ChatDotRound } from '@element-plus/icons-vue'
import { 
  getMessagePage, 
  getMessageStats, 
  sendMessageToAll, 
  sendMessageToUsers,
  deleteMessage,
  batchDeleteMessages
} from '@/api/message'
import { getUserList } from '@/api/user'
import PageHeader from '@/components/PageHeader.vue'

const loading = ref(false)
const submitLoading = ref(false)
const userSearchLoading = ref(false)
const tableData = ref([])
const selectedRows = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)
const userOptions = ref([])

const stats = reactive({
  totalCount: 0,
  unreadCount: 0,
  orderMessageCount: 0,
  systemMessageCount: 0,
  chatMessageCount: 0
})

const searchForm = reactive({
  keyword: '',
  type: null,
  userId: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  sendType: 'all',
  userIds: [],
  title: '',
  content: ''
})

const formRules = {
  sendType: [
    { required: true, message: '请选择发送方式', trigger: 'change' }
  ],
  userIds: [
    { 
      validator: (rule, value, callback) => {
        if (form.sendType === 'user' && (!value || value.length === 0)) {
          callback(new Error('请选择至少一个用户'))
        } else {
          callback()
        }
      }, 
      trigger: 'change' 
    }
  ],
  title: [
    { required: true, message: '请输入消息标题', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入消息内容', trigger: 'blur' }
  ]
}

onMounted(() => {
  loadData()
  loadStats()
})

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getMessagePage(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取消息列表失败:', error)
    ElMessage.error('获取消息列表失败')
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try {
    const res = await getMessageStats()
    Object.assign(stats, res.data)
  } catch (error) {
    console.error('获取消息统计失败:', error)
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.type = null
  searchForm.userId = null
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

const getIndex = (index) => {
  return (pagination.pageNum - 1) * pagination.pageSize + index + 1
}

const getTypeTagType = (type) => {
  const typeMap = {
    1: 'primary',
    2: 'success',
    3: 'warning'
  }
  return typeMap[type] || 'info'
}

const searchUsers = async (query) => {
  if (!query) {
    userOptions.value = []
    return
  }
  
  userSearchLoading.value = true
  try {
    const res = await getUserList({ keyword: query, pageNum: 1, pageSize: 20 })
    userOptions.value = res.data.records || []
  } catch (error) {
    console.error('搜索用户失败:', error)
  } finally {
    userSearchLoading.value = false
  }
}

const handleSend = () => {
  form.sendType = 'all'
  form.userIds = []
  form.title = ''
  form.content = ''
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
      submitLoading.value = true
      try {
        const data = {
          title: form.title,
          content: form.content
        }
        
        if (form.sendType === 'all') {
          await sendMessageToAll(data)
          ElMessage.success('系统消息发送成功')
        } else {
          data.userIds = form.userIds
          await sendMessageToUsers(data)
          ElMessage.success('消息发送成功')
        }
        
        dialogVisible.value = false
        loadData()
        loadStats()
      } catch (error) {
        console.error('发送消息失败:', error)
        ElMessage.error('发送消息失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该消息吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteMessage(row.id)
      ElMessage.success('删除成功')
      loadData()
      loadStats()
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
    `确定要删除选中的 ${selectedRows.value.length} 条消息吗？此操作不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(async () => {
    try {
      const ids = selectedRows.value.map(row => row.id)
      await batchDeleteMessages(ids)
      ElMessage.success('批量删除成功')
      selectedRows.value = []
      loadData()
      loadStats()
    } catch (error) {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }).catch(() => {})
}
</script>

<style scoped>
.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  border-radius: var(--qe-radius-lg);
  transition: all 0.3s ease;
}

.stats-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--qe-shadow-lg);
}

.stats-content {
  text-align: center;
  padding: 10px 0;
}

.stats-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--qe-primary);
  line-height: 1.2;
}

.stats-label {
  font-size: 14px;
  color: var(--qe-text-muted);
  margin-top: 8px;
}

.stats-card.unread .stats-value {
  color: var(--qe-warning);
}

.stats-card.order .stats-value {
  color: var(--qe-primary);
}

.stats-card.system .stats-value {
  color: var(--qe-success);
}

.stats-card.chat .stats-value {
  color: var(--qe-info);
}

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
