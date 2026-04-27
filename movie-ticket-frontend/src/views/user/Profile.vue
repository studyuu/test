<template>
  <div class="profile-page">
    <div class="container">
      <el-card class="profile-card">
        <div class="profile-header">
          <div class="avatar-wrapper">
            <el-avatar :size="100" :src="userInfo.avatar">
              <User />
            </el-avatar>
            <div class="avatar-upload-btn" @click="triggerAvatarUpload">
              <el-icon><Camera /></el-icon>
            </div>
            <input 
              type="file" 
              ref="avatarInput" 
              class="avatar-input" 
              accept="image/*" 
              @change="handleAvatarChange"
            />
          </div>
          <div class="profile-info">
            <h2>{{ userInfo.nickname }}</h2>
          </div>
        </div>
        
        <el-form :model="userInfo" label-width="100px" class="profile-form">
          <el-form-item label="昵称">
            <el-input v-model="userInfo.nickname" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="userInfo.phone" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="userInfo.email" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveProfile">保存修改</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Camera } from '@element-plus/icons-vue'
import { userAPI, uploadAPI } from '@/api/api'

const userStore = useUserStore()
const avatarInput = ref(null)

const userInfo = ref({
  id: '',
  userId: '',
  username: '',
  nickname: '',
  email: '',
  phone: '',
  avatar: ''
})

const loadUserInfo = async () => {
  const userId = userStore.userInfo.id || userStore.userInfo.userId || 1
  try {
    const response = await userAPI.getUserById(userId)
    if (response.data.code === 200) {
      userInfo.value = response.data.data
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

const triggerAvatarUpload = () => {
  avatarInput.value?.click()
}

const handleAvatarChange = async (event) => {
  const file = event.target.files?.[0]
  if (!file) return

  try {
    const response = await uploadAPI.uploadFile(file)
    if (response.data.code === 200) {
      const avatarUrl = response.data.data.url
      userInfo.value.avatar = avatarUrl
      await saveAvatar(avatarUrl)
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error('头像上传失败')
    }
  } catch (error) {
    console.error('上传头像失败:', error)
    ElMessage.error('头像上传失败')
  }
  
  event.target.value = ''
}

const saveAvatar = async (avatarUrl) => {
  const userId = userStore.userInfo.id || userStore.userInfo.userId || 1
  try {
    const response = await userAPI.updateUser(userId, { avatar: avatarUrl })
    if (response.data.code === 200) {
      userStore.setUserInfo({ ...userStore.userInfo, avatar: avatarUrl })
    }
  } catch (error) {
    console.error('保存头像失败:', error)
  }
}

const saveProfile = async () => {
  const userId = userStore.userInfo.id || userStore.userInfo.userId || 1
  try {
    const response = await userAPI.updateUser(userId, {
      nickname: userInfo.value.nickname,
      phone: userInfo.value.phone,
      email: userInfo.value.email
    })
    if (response.data.code === 200) {
      userStore.setUserInfo(response.data.data)
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(response.data.message || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-page {
  padding: 40px 0;
  background: #f8f9fa;
  min-height: calc(100vh - 70px);
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 40px;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 40px;
  padding-bottom: 30px;
  border-bottom: 1px solid #eee;
}

.avatar-wrapper {
  position: relative;
}

.avatar-upload-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 36px;
  height: 36px;
  background: #409eff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #fff;
  transition: background 0.3s;
}

.avatar-upload-btn:hover {
  background: #67a9ff;
}

.avatar-input {
  display: none;
}

.profile-info h2 {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
}

.profile-info p {
  color: #999;
}

.profile-form {
  max-width: 500px;
}
</style>