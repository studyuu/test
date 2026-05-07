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

      <!-- 想看列表 -->
      <el-card class="wishlist-card">
        <template #header>
          <div class="wishlist-header">
            <span class="wishlist-title">
              <el-icon><Star class="star-icon" /></el-icon>
              我的想看
            </span>
            <span class="wishlist-count">{{ wishList.length }}部</span>
          </div>
        </template>
        
        <div v-if="wishList.length === 0" class="empty-state">
          <el-icon name="film" :size="48" />
          <p>还没有想看的电影</p>
          <el-button type="primary" @click="goToMovies">去看看</el-button>
        </div>
        
        <div v-else class="wishlist-movies">
          <div 
            v-for="movie in wishList" 
            :key="movie.movieId" 
            class="wishlist-item"
            @click="goToMovie(movie.movieId)"
          >
            <img :src="movie.poster" :alt="movie.title" class="wishlist-poster" />
            <div class="wishlist-info">
              <h4>{{ movie.title }}</h4>
              <p class="wishlist-category">{{ movie.category }}</p>
              <p class="wishlist-rating">评分：{{ movie.rating }}分</p>
              <p class="wishlist-date">上映日期：{{ movie.releaseDate }}</p>
            </div>
            <div class="wishlist-actions">
              <el-button 
                type="text" 
                @click.stop="removeFromWishList(movie.movieId)"
                class="remove-btn"
              >
                移除
              </el-button>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Camera, Star } from '@element-plus/icons-vue'
import { userAPI, uploadAPI, movieAPI } from '@/api/api'

const userStore = useUserStore()
const router = useRouter()
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

const wishList = ref([])

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

const loadWishList = async () => {
  const userId = userStore.userInfo.id || userStore.userInfo.userId || 1
  try {
    const response = await movieAPI.getWishList(userId)
    if (response.data.code === 200) {
      wishList.value = response.data.data.content || []
    }
  } catch (error) {
    console.error('加载想看列表失败:', error)
  }
}

const goToMovie = (movieId) => {
  router.push(`/movie/${movieId}`)
}

const goToMovies = () => {
  router.push('/movies')
}

const removeFromWishList = async (movieId) => {
  const userId = userStore.userInfo.id || userStore.userInfo.userId || 1
  try {
    const response = await movieAPI.removeWish(movieId, userId)
    if (response.data.code === 200) {
      wishList.value = wishList.value.filter(m => m.movieId !== movieId)
      ElMessage.success('已从想看列表移除')
    }
  } catch (error) {
    console.error('移除失败:', error)
    ElMessage.error('移除失败')
  }
}

onMounted(() => {
  loadUserInfo()
  loadWishList()
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

.wishlist-card {
  margin-top: 20px;
}

.wishlist-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.wishlist-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.star-icon {
  color: #ffb800;
}

.wishlist-count {
  font-size: 14px;
  color: #999;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
  color: #999;
}

.empty-state p {
  margin: 16px 0;
}

.wishlist-movies {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.wishlist-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.wishlist-item:hover {
  background: #f0f0f0;
}

.wishlist-poster {
  width: 80px;
  height: 110px;
  object-fit: cover;
  border-radius: 8px;
  flex-shrink: 0;
}

.wishlist-info {
  flex: 1;
}

.wishlist-info h4 {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.wishlist-category {
  font-size: 13px;
  color: #999;
  margin-bottom: 4px;
}

.wishlist-rating {
  font-size: 13px;
  color: #ffb800;
  margin-bottom: 4px;
}

.wishlist-date {
  font-size: 13px;
  color: #999;
}

.wishlist-actions {
  display: flex;
  align-items: center;
}

.remove-btn {
  color: #ff6b6b;
}

.remove-btn:hover {
  color: #ff4757;
}
</style>