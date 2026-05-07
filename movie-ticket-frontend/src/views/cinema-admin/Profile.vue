<template>
  <div class="cinema-profile">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>影院信息设置</span>
        </div>
      </template>

      <el-form :model="formData" label-width="120px" style="max-width: 600px;">
        <el-form-item label="影院Logo">
          <el-upload
            class="avatar-uploader"
            action="/api/upload"
            :headers="{ Authorization: 'Bearer ' + token }"
            :show-file-list="false"
            :on-success="handleLogoSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="formData.logo" :src="formData.logo" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="影院名称">
          <el-input v-model="formData.cinemaName" placeholder="请输入影院名称" />
        </el-form-item>

        <el-form-item label="地址">
          <el-input v-model="formData.address" placeholder="请输入详细地址" />
        </el-form-item>

        <el-form-item label="联系电话">
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
        </el-form-item>

        <el-form-item label="影院简介">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入影院简介"
          />
        </el-form-item>

        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">营业中</el-radio>
            <el-radio :label="0">停业中</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>账户信息</span>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ userInfo.nickname }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ userInfo.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ userInfo.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag type="success">影院管理员</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="入驻时间">{{ formatTime(userInfo.createTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const token = localStorage.getItem('token') || ''
const userInfo = ref({})

const formData = ref({
  logo: '',
  cinemaName: '',
  address: '',
  phone: '',
  description: '',
  status: 1
})

const formatTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  return dateTimeStr.slice(0, 16)
}

const loadCinemaInfo = async () => {
  try {
    const response = await axios.get('/api/cinema-admin/profile', {
      headers: { Authorization: `Bearer ${token}` },
      params: { userId: userStore.userInfo.id }
    })
    if (response.data.code === 200) {
      const data = response.data.data
      formData.value = {
        logo: data.logo || '',
        cinemaName: data.cinemaName || '',
        address: data.address || '',
        phone: data.phone || '',
        description: data.description || '',
        status: data.status || 1
      }
    }
  } catch (error) {
    console.error('加载影院信息失败:', error)
  }
}

const loadUserInfo = async () => {
  userInfo.value = userStore.userInfo || {}
}

const handleLogoSuccess = (res) => {
  if (res.code === 200) {
    formData.value.logo = res.data
    ElMessage.success('Logo上传成功')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

const handleSubmit = async () => {
  if (!formData.value.cinemaName || !formData.value.address) {
    ElMessage.error('请填写必填信息')
    return
  }

  try {
    const response = await axios.put('/api/cinema-admin/profile', formData.value, {
      headers: { Authorization: `Bearer ${token}` },
      params: { userId: userStore.userInfo.id }
    })
    if (response.data.code === 200) {
      ElMessage.success('保存成功')
      loadCinemaInfo()
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  loadCinemaInfo()
  loadUserInfo()
})
</script>

<style scoped>
.cinema-profile {
  padding: 20px;
}

.card-header {
  font-weight: bold;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.avatar {
  width: 120px;
  height: 120px;
  object-fit: cover;
}
</style>
