<template>
  <div class="page-container">
    <el-breadcrumb separator="/" class="page-breadcrumb">
      <el-breadcrumb-item>个人中心</el-breadcrumb-item>
    </el-breadcrumb>

    <PageHeader
      title="个人中心"
      subtitle="管理个人信息和账户设置"
      :icon="User"
    />

    <div class="profile-content">
      <el-card class="info-card">
        <template #header>
          <div class="card-header">
            <span>基本信息</span>
          </div>
        </template>
        <el-form
          ref="infoFormRef"
          :model="infoForm"
          :rules="infoFormRules"
          label-width="100px"
        >
          <el-form-item label="用户名">
            <el-input v-model="infoForm.username" disabled />
          </el-form-item>
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="infoForm.nickname" placeholder="请输入昵称" />
          </el-form-item>
          <el-form-item label="头像">
            <div class="avatar-upload">
              <el-avatar
                v-if="infoForm.avatar"
                :src="infoForm.avatar"
                :size="100"
              />
              <el-avatar v-else :size="100">
                <el-icon><User /></el-icon>
              </el-avatar>
              <el-button
                type="primary"
                size="small"
                style="margin-left: 16px;"
                @click="handleUploadAvatar"
              >
                上传头像
              </el-button>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleUpdateInfo">保存修改</el-button>
            <el-button @click="handleResetInfo">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card class="password-card">
        <template #header>
          <div class="card-header">
            <span>修改密码</span>
          </div>
        </template>
        <el-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordFormRules"
          label-width="100px"
        >
          <el-form-item label="原密码" prop="oldPassword">
            <el-input
              v-model="passwordForm.oldPassword"
              type="password"
              placeholder="请输入原密码"
              show-password
            />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="passwordForm.newPassword"
              type="password"
              placeholder="请输入新密码"
              show-password
            />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="passwordForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleUpdatePassword">修改密码</el-button>
            <el-button @click="handleResetPassword">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { User } from '@element-plus/icons-vue'
import { getAdminInfo, updateAdminInfo, updateAdminPassword } from '@/api/admin'
import { uploadFile } from '@/api/file'
import { useUserStore } from '@/store/user'
import PageHeader from '@/components/PageHeader.vue'

const userStore = useUserStore()

const infoFormRef = ref(null)
const passwordFormRef = ref(null)
const avatarInputRef = ref(null)

const infoForm = reactive({
  username: '',
  nickname: '',
  avatar: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const infoFormRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ]
}

const passwordFormRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

onMounted(() => {
  loadAdminInfo()
})

const loadAdminInfo = async () => {
  try {
    const res = await getAdminInfo()
    const userInfo = res.data
    infoForm.username = userInfo.username
    infoForm.nickname = userInfo.nickname || ''
    infoForm.avatar = userInfo.avatar || ''
    userStore.setUserInfo(userInfo)
  } catch (error) {
    console.error('获取管理员信息失败:', error)
    ElMessage.error('获取管理员信息失败')
  }
}

const handleUpdateInfo = async () => {
  if (!infoFormRef.value) return

  await infoFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await updateAdminInfo({
          nickname: infoForm.nickname,
          avatar: infoForm.avatar
        })
        userStore.setUserInfo({
          ...userStore.userInfo,
          nickname: infoForm.nickname,
          avatar: infoForm.avatar
        })
        ElMessage.success('更新信息成功')
        loadAdminInfo()
      } catch (error) {
        console.error('更新信息失败:', error)
        ElMessage.error('更新信息失败')
      }
    }
  })
}

const handleResetInfo = () => {
  loadAdminInfo()
  if (infoFormRef.value) {
    infoFormRef.value.clearValidate()
  }
}

const handleUpdatePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await updateAdminPassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        ElMessage.success('修改密码成功，请重新登录')
        // 清空密码表单
        passwordForm.oldPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
        passwordFormRef.value.resetFields()
      } catch (error) {
        console.error('修改密码失败:', error)
        ElMessage.error(error.response?.data?.message || '修改密码失败')
      }
    }
  })
}

const handleResetPassword = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  if (passwordFormRef.value) {
    passwordFormRef.value.resetFields()
  }
}

const handleUploadAvatar = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = async (e) => {
    const file = e.target.files[0]
    if (!file) return
    
    const isImage = file.type.startsWith('image/')
    if (!isImage) {
      ElMessage.error('只能上传图片文件')
      return
    }
    
    const isLt10M = file.size / 1024 / 1024 < 10
    if (!isLt10M) {
      ElMessage.error('图片大小不能超过 10MB')
      return
    }
    
    const loadingInstance = ElLoading.service({ text: '上传中...' })
    try {
      const res = await uploadFile(file)
      console.log('上传响应:', res)
      if (res && res.data) {
        infoForm.avatar = res.data
        loadingInstance.close()
        ElMessage.success('头像上传成功')
      } else {
        throw new Error('响应数据格式错误')
      }
    } catch (error) {
      loadingInstance.close()
      console.error('上传头像失败:', error)
      ElMessage.error(error.message || '上传头像失败')
    }
  }
  input.click()
}
</script>

<style scoped>
.profile-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

@media (max-width: 1200px) {
  .profile-content {
    grid-template-columns: 1fr;
  }
}

.info-card,
.password-card {
  border: 1px solid var(--qe-border-lighter);
  border-radius: var(--qe-radius-xl);
  box-shadow: var(--qe-shadow-card);
  transition: box-shadow var(--qe-transition-base);
}

.info-card:hover,
.password-card:hover {
  box-shadow: var(--qe-shadow-card-hover);
}

.info-card :deep(.el-card__header),
.password-card :deep(.el-card__header) {
  padding: 18px 24px;
  border-bottom: 1px solid var(--qe-border-lighter);
  background: var(--qe-surface);
}

.info-card :deep(.el-card__body),
.password-card :deep(.el-card__body) {
  padding: 24px;
  background: var(--qe-bg);
}

.card-header {
  font-weight: 600;
  font-size: 16px;
  color: var(--qe-text);
}

.avatar-upload {
  display: flex;
  align-items: center;
}

.info-card :deep(.el-form-item__label),
.password-card :deep(.el-form-item__label) {
  color: var(--qe-text-secondary);
  font-weight: 500;
}

.info-card :deep(.el-input__wrapper),
.password-card :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px var(--qe-border) inset;
  border-radius: var(--qe-radius-md);
  transition: all var(--qe-transition-fast);
}

.info-card :deep(.el-input__wrapper:hover),
.password-card :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--qe-primary-light) inset;
}

.info-card :deep(.el-input__wrapper.is-focus),
.password-card :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--qe-primary) inset;
}

.info-card :deep(.el-button--primary),
.password-card :deep(.el-button--primary) {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  box-shadow: var(--qe-primary-shadow);
}

.info-card :deep(.el-button--primary:hover),
.password-card :deep(.el-button--primary:hover) {
  box-shadow: var(--qe-primary-shadow-hover);
  transform: translateY(-1px);
}
</style>
