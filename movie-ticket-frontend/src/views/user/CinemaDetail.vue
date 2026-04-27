<template>
  <div class="cinema-detail-page">
    <!-- 影院信息 -->
    <div class="cinema-info-section">
      <div class="container">
        <div class="cinema-info">
          <div class="cinema-image">
            <img :src="cinemaImage" alt="影院图片" />
          </div>
          <div class="cinema-details">
            <h1>{{ cinema.cinemaName }}</h1>
            <p class="cinema-address">
              <el-icon><Location /></el-icon>
              {{ cinema.address }}
            </p>
            <p class="cinema-phone">
              <el-icon><Phone /></el-icon>
              {{ cinema.phone }}
            </p>
            <div class="cinema-rating">
              <el-rate :model-value="(cinema.rating || 0) / 2" disabled show-score />
              <span class="rating-text">{{ cinema.rating || 0 }}分</span>
            </div>
          </div>
        </div>
        
        <!-- 影院介绍 -->
        <div class="cinema-intro">
          <h3>影院介绍</h3>
          <p>{{ cinema.description || defaultDescription }}</p>
        </div>
      </div>
    </div>

    <!-- 影片列表 -->
    <div class="movies-section">
      <div class="container">
        <h2>正在热映</h2>
        <div class="movie-list">
          <div 
            v-for="movie in movies" 
            :key="movie.id" 
            class="movie-card"
            :class="{ active: selectedMovieId === movie.id }"
            @click="selectMovie(movie.id)"
          >
            <img :src="movie.poster" :alt="movie.title" />
            <div class="movie-info">
              <h4>{{ movie.title }}</h4>
              <p class="movie-category">{{ movie.category }}</p>
              <p class="movie-duration">{{ movie.duration }}分钟</p>
            </div>
          </div>
        </div>

        <!-- 影片排期 -->
        <div class="schedule-section" v-if="selectedMovieId">
          <h3>{{ selectedMovie.title }}</h3>
          <div class="schedule-dates">
            <el-button 
              v-for="date in showDates" 
              :key="date" 
              :type="selectedDate === date ? 'primary' : 'default'"
              @click="selectedDate = date"
            >
              {{ date }}
            </el-button>
          </div>
          <div class="schedule-list">
            <div v-for="schedule in filteredSchedules" :key="schedule.id" class="schedule-item">
              <span class="schedule-time">{{ formatTime(schedule.startTime) }}</span>
              <span class="schedule-hall">{{ schedule.hallName }}</span>
              <span class="schedule-price">¥{{ schedule.price }}</span>
              <el-button type="primary" size="small" @click="gotoSelectSeat(schedule.id)">
                选座购票
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Location, Phone } from '@element-plus/icons-vue'
import { mockMovies, mockSchedules } from '@/api/mockData'
import { cinemaAPI } from '@/api/api'

const route = useRoute()
const router = useRouter()
const cinemaId = parseInt(route.params.id)

const cinema = ref({})
const movies = ref(mockMovies)
const selectedMovieId = ref(null)
const selectedDate = ref('今天')

const cinemaImage = computed(() => {
  if (cinema.value.logo) {
    return cinema.value.logo
  }
  return `https://picsum.photos/400/300?random=${cinema.value.cinemaId || 1}`
})

const defaultDescription = computed(() => {
  return `${cinema.value.cinemaName || '该影院'}是一家现代化的影院，提供舒适的观影环境和先进的放映设备。欢迎前来观影！`
})

const selectedMovie = computed(() => {
  return movies.value.find(m => m.id === selectedMovieId.value) || movies.value[0]
})

const showDates = ref(['今天', '明天', '后天'])

const filteredSchedules = computed(() => {
  if (!selectedMovieId.value) return []
  return mockSchedules.filter(s => 
    s.cinemaId === cinemaId && 
    s.movieId === selectedMovieId.value
  )
})

const selectMovie = (movieId) => {
  selectedMovieId.value = movieId
}

const formatTime = (timeStr) => {
  return timeStr.split(' ')[1]
}

const gotoSelectSeat = (scheduleId) => {
  router.push(`/select-seat/${scheduleId}`)
}

const loadCinemaDetail = async () => {
  try {
    const response = await cinemaAPI.getCinemaById(cinemaId)
    if (response.data.code === 200) {
      cinema.value = response.data.data
    }
  } catch (error) {
    console.error('加载影院详情失败:', error)
    cinema.value = {
      cinemaName: '未知影院',
      address: '地址未填写',
      phone: '电话未填写',
      description: '暂无影院介绍'
    }
  }
}

onMounted(() => {
  loadCinemaDetail()
  if (movies.value.length > 0) {
    selectedMovieId.value = movies.value[0].id
  }
})
</script>

<style scoped>
.cinema-detail-page {
  min-height: calc(100vh - 70px);
  background: #f8f9fa;
}

.cinema-info-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 40px 0;
}

.cinema-info {
  display: flex;
  gap: 30px;
  margin-bottom: 30px;
}

.cinema-image {
  flex-shrink: 0;
}

.cinema-image img {
  width: 300px;
  height: 200px;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.cinema-details h1 {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 16px;
}

.cinema-address,
.cinema-phone {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  margin-bottom: 12px;
}

.cinema-rating {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
}

.rating-text {
  font-size: 16px;
  font-weight: bold;
}

.cinema-intro {
  background: rgba(255,255,255,0.1);
  padding: 20px;
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.cinema-intro h3 {
  font-size: 20px;
  margin-bottom: 12px;
}

.cinema-intro p {
  line-height: 1.6;
  font-size: 14px;
  opacity: 0.9;
}

.movies-section {
  padding: 40px 0;
}

.movies-section h2 {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 24px;
  color: #333;
}

.movie-list {
  display: flex;
  gap: 20px;
  overflow-x: auto;
  padding-bottom: 20px;
  margin-bottom: 30px;
}

.movie-card {
  flex-shrink: 0;
  width: 160px;
  cursor: pointer;
  transition: all 0.3s;
  padding: 12px;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.movie-card:hover,
.movie-card.active {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.12);
  border: 2px solid #667eea;
}

.movie-card img {
  width: 100%;
  height: 220px;
  object-fit: cover;
  border-radius: 6px;
  margin-bottom: 12px;
}

.movie-info h4 {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 4px;
  color: #333;
}

.movie-category,
.movie-duration {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.schedule-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.schedule-section h3 {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}

.schedule-dates {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.schedule-item {
  display: flex;
  align-items: center;
  gap: 30px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  font-size: 14px;
}

.schedule-time {
  width: 100px;
  color: #333;
  font-weight: 500;
}

.schedule-hall {
  flex: 1;
  color: #666;
}

.schedule-price {
  width: 80px;
  color: #ff6b6b;
  font-weight: bold;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 40px;
}

@media (max-width: 768px) {
  .cinema-info {
    flex-direction: column;
  }
  
  .cinema-image img {
    width: 100%;
    height: 250px;
  }
  
  .container {
    padding: 0 20px;
  }
  
  .schedule-item {
    flex-wrap: wrap;
    gap: 12px;
  }
  
  .schedule-time,
  .schedule-hall,
  .schedule-price {
    width: auto;
  }
}
</style>