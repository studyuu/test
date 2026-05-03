<template>
  <div class="movie-detail-page">
    <!-- 影片信息区 -->
    <div class="movie-header">
      <div class="container">
        <div class="movie-info-wrapper">
          <div class="movie-poster">
            <img :src="movie.poster" :alt="movie.title" />
          </div>
          <div class="movie-info">
            <h1 class="movie-title">{{ movie.title }}</h1>
            <p class="movie-english">{{ movie.englishTitle }}</p>
            <div class="movie-tags">
              <el-tag v-for="tag in (movie.category || '').split('/')" :key="tag" effect="plain">
                {{ tag }}
              </el-tag>
              <el-tag type="warning" effect="plain">{{ movie.duration }}分钟</el-tag>
            </div>
            <div class="movie-meta">
              <p><strong>上映日期：</strong>{{ movie.releaseDate }}</p>
              <p><strong>导演：</strong>{{ movie.director }}</p>
              <p><strong>主演：</strong>{{ movie.actors }}</p>
            </div>
            <div class="movie-rating-section">
              <div class="rating-score">
                <span class="score">{{ movie.rating }}</span>
                <span class="max">/10</span>
              </div>
              <div class="rating-stars">
                <el-rate :model-value="(movie.rating || 0) / 2" disabled show-score />
                <span class="rating-count">{{ Math.floor(Math.random() * 50000) + 10000 }}人评分</span>
              </div>
            </div>
            <div class="movie-actions">
              <el-button type="danger" size="large" round @click="scrollToSchedule">
                <el-icon><Ticket /></el-icon>
                特惠购票
              </el-button>
              <el-button size="large" round :type="isWished ? 'warning' : ''" @click="toggleWish">
                <el-icon><Star class="star-icon" :class="{ filled: isWished }" /></el-icon>
                {{ isWished ? '已想看' : '想看' }}
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 影片详情区 -->
    <div class="container">
      <div class="detail-content">
        <div class="main-content">
          <!-- 剧情简介 -->
          <section class="section">
            <h2 class="section-title">剧情简介</h2>
            <p class="movie-desc">{{ movie.synopsis || movie.description }}</p>
          </section>

          <!-- 选座购票 -->
          <section class="section" id="schedule-section">
            <h2 class="section-title">选座购票</h2>
            
            <!-- 日期筛选 -->
            <div v-if="dateOptions.length > 0" class="date-filter">
              <div 
                v-for="date in dateOptions" 
                :key="date"
                class="date-option"
                :class="{ active: selectedDate === date }"
                @click="selectedDate = date"
              >
                {{ formatDate(date) }}
              </div>
            </div>
            
            <div class="schedule-list">
              <div v-for="schedule in filteredSchedules" :key="schedule.id" class="schedule-item">
                <div class="schedule-time">
                  <div class="start-time">{{ formatTime(schedule.startTime) }}</div>
                  <div class="end-time">{{ formatTime(schedule.endTime) }}散场</div>
                </div>
                <div class="schedule-info">
                  <div class="hall-name">{{ schedule.hallName }}</div>
                  <div class="cinema-name">{{ schedule.cinemaName || '未知影院' }}</div>
                </div>
                <div class="schedule-price">
                  <span class="price">¥{{ schedule.price }}</span>
                </div>
                <el-button type="danger" @click="$router.push(`/select-seat/${schedule.id}`)">
                  选座购票
                </el-button>
              </div>
              
              <div v-if="filteredSchedules.length === 0" class="empty-schedule">
                <p>暂无可购票的场次</p>
              </div>
            </div>
          </section>

          <!-- 评论 -->
          <section class="section">
            <div class="section-header">
              <h2 class="section-title">观众评论</h2>
              <el-button type="primary" @click="openCommentDialog">
                <el-icon><EditPen /></el-icon>
                发表评论
              </el-button>
            </div>
            <div class="comment-list">
              <div v-for="comment in comments" :key="comment.id" class="comment-item">
                <div class="comment-user">
                  <el-avatar :size="40" :src="comment.avatar" />
                  <div class="user-info">
                    <span class="username">{{ comment.userName }}</span>
                    <el-rate :model-value="comment.rating" disabled size="small" />
                  </div>
                  <span class="comment-time">{{ comment.createTime }}</span>
                </div>
                <p class="comment-content">{{ comment.content }}</p>
                <div v-if="comment.images && comment.images.length > 0" class="comment-images">
                  <img 
                    v-for="(img, index) in comment.images" 
                    :key="index" 
                    :src="img" 
                    class="comment-image"
                    :alt="`评论图片${index + 1}`"
                  />
                </div>
                <div class="comment-actions">
                  <span 
                    class="like-count" 
                    :class="{ liked: comment.liked }"
                    @click="handleLike(comment.id)"
                  >
                    <el-icon><Pointer /></el-icon>
                    {{ comment.likes }}
                  </span>
                </div>
              </div>
            </div>
          </section>
        </div>

        <!-- 侧边栏 -->
        <aside class="sidebar">
          <div class="sidebar-section">
            <h3 class="sidebar-title">相关推荐</h3>
            <div class="related-list">
              <div v-for="m in relatedMovies" :key="m.id" class="related-item" @click="goToMovie(m.id)">
                <img :src="m.poster" :alt="m.title" />
                <div class="related-info">
                  <h4>{{ m.title }}</h4>
                  <span class="related-rating">{{ m.rating }}分</span>
                </div>
              </div>
            </div>
          </div>
        </aside>
      </div>
    </div>

    <!-- 发表评论弹窗 -->
    <el-dialog
      v-model="commentDialogVisible"
      title="发表评论"
      width="500px"
    >
      <el-form :model="commentForm" :rules="commentRules" ref="commentFormRef" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="commentForm.rating" show-score />
        </el-form-item>
        <el-form-item label="评论内容" prop="content">
          <el-input
            v-model="commentForm.content"
            type="textarea"
            placeholder="请输入您的评论..."
            :rows="4"
          />
        </el-form-item>
        <el-form-item label="上传图片">
          <el-upload
            class="upload-demo"
            action="http://localhost:8080/api/upload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :file-list="commentForm.images"
            :auto-upload="true"
            list-type="picture-card"
          >
            <el-icon size="20"><Plus /></el-icon>
            <div>点击上传</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="commentDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitComment">发表评论</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Ticket, Star, Pointer, EditPen, Plus } from '@element-plus/icons-vue'
import { mockMovies } from '@/api/mockData'
import { commentAPI, movieAPI, scheduleAPI } from '@/api/api'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const movieId = ref(parseInt(route.params.id))
const userStore = useUserStore()

const movie = ref({})
const schedules = ref([])
const comments = ref([])
const relatedMovies = ref([])
const isWished = ref(false)

const dateOptions = ref([])
const selectedDate = ref('')

const commentDialogVisible = ref(false)
const commentFormRef = ref(null)
const commentForm = reactive({
  rating: 5,
  content: '',
  images: []
})

const commentRules = {
  content: [
    { required: true, message: '请输入评论内容', trigger: 'blur' },
    { min: 1, max: 500, message: '评论内容长度在1到500个字符之间', trigger: 'blur' }
  ]
}

const formatTime = (datetime) => {
  return datetime.split(' ')[1]
}

const scrollToSchedule = () => {
  document.getElementById('schedule-section')?.scrollIntoView({ behavior: 'smooth' })
}

const goToMovie = (movieId) => {
  router.push(`/movie/${movieId}`)
}

const checkIsWished = async () => {
  if (!userStore.token) {
    isWished.value = false
    return
  }
  
  try {
    const response = await movieAPI.checkWish(movieId.value)
    if (response.data.code === 200) {
      isWished.value = response.data.data
    }
  } catch (error) {
    console.error('检查想看状态失败:', error)
    isWished.value = false
  }
}

const toggleWish = async () => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    if (isWished.value) {
      const response = await movieAPI.removeWish(movieId.value)
      if (response.data.code === 200) {
        isWished.value = false
        ElMessage.success('已取消想看')
      }
    } else {
      const response = await movieAPI.addWish(movieId.value)
      if (response.data.code === 200) {
        isWished.value = true
        ElMessage.success('已添加到想看列表')
      }
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

const openCommentDialog = () => {
  commentDialogVisible.value = true
}

const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    commentForm.images.push({
      url: response.data.url
    })
  }
}

const handleUploadError = (error) => {
  console.error('上传失败:', error)
}

const submitComment = async () => {
  commentFormRef.value.validate((valid) => {
    if (!valid) {
      return
    }
    
    const newComment = {
      movieId: movieId.value,
      userId: userStore.userInfo.id || userStore.userInfo.userId || 1,
      rating: commentForm.rating,
      content: commentForm.content,
      images: commentForm.images.map(img => img.url)
    }
    
    commentAPI.addComment(newComment)
      .then(response => {
        if (response.data.code === 200) {
          loadComments()
          commentDialogVisible.value = false
          commentForm.rating = 5
          commentForm.content = ''
          commentForm.images = []
        }
      })
      .catch(error => {
        console.error('发表评论失败:', error)
      })
  })
}

const loadComments = () => {
  commentAPI.getCommentsByMovieId(movieId.value)
    .then(response => {
      if (response.data.code === 200) {
        comments.value = response.data.data
      }
    })
    .catch(error => {
      console.error('加载评论失败:', error)
    })
}

const handleLike = (commentId) => {
  const comment = comments.value.find(c => c.id === commentId)
  if (!comment) return
  
  const currentLiked = comment.liked || false
  
  commentAPI.likeComment(commentId, currentLiked)
    .then(response => {
      if (response.data.code === 200) {
        comment.liked = !currentLiked
        if (currentLiked) {
          // 取消点赞
          comment.likes--
        } else {
          // 点赞
          comment.likes++
        }
      }
    })
    .catch(error => {
      console.error('操作失败:', error)
    })
}

const loadMovieDetail = () => {
  movieAPI.getMovieById(movieId.value)
    .then(response => {
      if (response.data.code === 200) {
        const data = response.data.data
        movie.value = {
          ...data,
          id: data.movieId || data.id,
          description: data.synopsis || data.description
        }
        relatedMovies.value = mockMovies.filter(m => m.id !== movieId.value).slice(0, 4)
      }
    })
    .catch(error => {
      console.error('加载影片详情失败:', error)
      const found = mockMovies.find(m => m.id === movieId) || mockMovies.find(m => m.id === 1) || mockMovies[0]
      movie.value = {
        ...found,
        id: found.id,
        description: found.description
      }
      relatedMovies.value = mockMovies.filter(m => m.id !== movieId.value).slice(0, 4)
    })
    .finally(() => {
      checkIsWished()
    })
}

const loadSchedules = () => {
  scheduleAPI.getSchedules({ movieId: movieId.value })
    .then(response => {
      if (response.data.code === 200) {
        schedules.value = response.data.data.slice(0, 10)
        generateDateOptions()
      }
    })
    .catch(error => {
      console.error('加载排期失败:', error)
    })
}

const generateDateOptions = () => {
  const dates = new Set()
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  
  schedules.value.forEach(schedule => {
    if (schedule.startTime) {
      const scheduleDate = new Date(schedule.startTime)
      scheduleDate.setHours(0, 0, 0, 0)
      if (scheduleDate >= today) {
        dates.add(scheduleDate.toISOString().split('T')[0])
      }
    }
  })
  
  dateOptions.value = Array.from(dates).sort()
  
  if (dateOptions.value.length > 0) {
    selectedDate.value = dateOptions.value[0]
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const dateCompare = new Date(dateStr)
  dateCompare.setHours(0, 0, 0, 0)
  
  const diffDays = Math.floor((dateCompare - today) / (1000 * 60 * 60 * 24))
  
  if (diffDays === 0) return '今天'
  if (diffDays === 1) return '明天'
  if (diffDays === 2) return '后天'
  
  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${month}月${day}日`
}

const filteredSchedules = computed(() => {
  if (!selectedDate.value || schedules.value.length === 0) {
    return schedules.value
  }
  
  return schedules.value.filter(schedule => {
    if (!schedule.startTime) return false
    return schedule.startTime.startsWith(selectedDate.value)
  }).filter(schedule => {
    const now = new Date()
    const scheduleTime = new Date(schedule.startTime)
    return scheduleTime > now
  })
})

loadMovieDetail()
loadComments()
loadSchedules()

watch(() => route.params.id, (newId) => {
  if (newId) {
    movieId.value = parseInt(newId)
    loadMovieDetail()
    loadComments()
    loadSchedules()
  }
})
</script>

<style scoped>
.movie-detail-page {
  background: #f8f9fa;
  min-height: calc(100vh - 70px);
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 40px;
}

/* 影片头部信息 */
.movie-header {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  padding: 50px 0;
  color: #fff;
}

.movie-info-wrapper {
  display: flex;
  gap: 40px;
}

.movie-poster {
  flex-shrink: 0;
  width: 280px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
}

.movie-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.movie-info {
  flex: 1;
}

.movie-title {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 8px;
}

.movie-english {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 20px;
}

.movie-tags {
  display: flex;
  gap: 10px;
  margin-bottom: 24px;
}

.movie-meta {
  margin-bottom: 24px;
}

.movie-meta p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 8px;
}

.movie-meta strong {
  color: rgba(255, 255, 255, 0.6);
}

.movie-rating-section {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.rating-score {
  display: flex;
  align-items: baseline;
}

.rating-score .score {
  font-size: 48px;
  font-weight: bold;
  color: #ffb800;
}

.rating-score .max {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.5);
}

.rating-stars {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.rating-count {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

.movie-actions {
  display: flex;
  gap: 16px;
}

.movie-actions .el-button {
  padding: 12px 32px;
  font-size: 16px;
}

.star-icon.filled {
  color: #ffb800;
}

/* 详情内容区 */
.detail-content {
  display: flex;
  gap: 30px;
  padding: 40px 0;
}

.main-content {
  flex: 1;
}

.section {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f0f0;
}

.section-title {
  font-size: 22px;
  font-weight: bold;
  color: #333;
  margin-bottom: 0;
}

.movie-desc {
  font-size: 15px;
  line-height: 1.8;
  color: #666;
}

/* 日期筛选 */
.date-filter {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.date-option {
  padding: 10px 24px;
  background: #f8f9fa;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
  color: #666;
}

.date-option:hover {
  background: #e9ecef;
}

.date-option.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

/* 场次列表 */
.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.schedule-item {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 10px;
  transition: all 0.3s;
}

.schedule-item:hover {
  background: #f0f0f0;
}

.schedule-time {
  width: 100px;
}

.start-time {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.end-time {
  font-size: 13px;
  color: #999;
}

.schedule-info {
  flex: 1;
  padding: 0 20px;
}

.hall-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.cinema-name {
  font-size: 13px;
  color: #999;
}

.schedule-price {
  width: 100px;
  text-align: center;
}

.schedule-price .price {
  font-size: 28px;
  color: #ff6b6b;
  font-weight: bold;
}

.empty-schedule {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #999;
}

.empty-schedule p {
  margin-top: 12px;
  font-size: 14px;
}

/* 评论列表 */
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.comment-user {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.user-info {
  flex: 1;
}

.username {
  font-size: 15px;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 4px;
}

.comment-time {
  font-size: 13px;
  color: #999;
}

.comment-content {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
  margin-bottom: 12px;
}

.comment-images {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 12px;
}

.comment-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.2s;
}

.comment-image:hover {
  transform: scale(1.05);
}

.comment-actions {
  display: flex;
  gap: 20px;
}

.like-count {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #999;
  cursor: pointer;
}

.like-count:hover {
  color: #ff6b6b;
}

.like-count.liked {
  color: #ff6b6b;
}

/* 侧边栏 */
.sidebar {
  width: 300px;
  flex-shrink: 0;
}

.sidebar-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.sidebar-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
}

.related-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.related-item {
  display: flex;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.related-item:hover {
  opacity: 0.8;
}

.related-item img {
  width: 70px;
  height: 100px;
  object-fit: cover;
  border-radius: 6px;
}

.related-info {
  flex: 1;
}

.related-info h4 {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.related-rating {
  font-size: 14px;
  color: #ffb800;
  font-weight: bold;
}

/* 弹窗样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.upload-demo {
  margin-top: 10px;
}

@media (max-width: 1000px) {
  .detail-content {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
  }
  
  .movie-info-wrapper {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .movie-poster {
    width: 200px;
  }
  
  .movie-tags {
    justify-content: center;
  }
  
  .movie-actions {
    justify-content: center;
  }
}

@media (max-width: 600px) {
  .schedule-item {
    flex-wrap: wrap;
    gap: 12px;
  }
  
  .schedule-info {
    width: 100%;
    padding: 0;
  }
  
  .schedule-price {
    flex: 1;
    text-align: left;
  }
}
</style>
