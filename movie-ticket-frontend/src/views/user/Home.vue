<template>
  <div class="home">
    <!-- 轮播图 -->
    <section class="hero-section">
      <el-carousel height="500px" indicator-position="outside">
        <el-carousel-item v-for="(banner, index) in banners" :key="index">
          <div 
            class="banner-item" 
            :style="{ backgroundImage: `url(${banner.image})` }"
            @click="$router.push(`/movie/${banner.movieId}`)"
          >
            <div class="banner-overlay">
              <div class="banner-content">
                <h2 class="banner-title">{{ banner.title }}</h2>
                <p class="banner-desc">{{ banner.description }}</p>
                <el-button type="danger" size="large" round @click.stop="$router.push(`/movie/${banner.movieId}`)">
                  立即购票
                </el-button>
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- 正在热映和今日票房 -->
    <section class="section">
      <div class="container">
        <div class="main-content">
          <!-- 正在热映 -->
          <div class="hot-movies-section">
            <div class="section-header">
              <h2 class="section-title">
                <el-icon size="28" color="#ff6b6b"><VideoCamera /></el-icon>
                正在热映
              </h2>
              <router-link to="/movies" class="view-all">
                查看全部 <el-icon><ArrowRight /></el-icon>
              </router-link>
            </div>
            
            <div class="movie-grid">
              <div 
                v-for="movie in hotMovies.slice(0, 4)" 
                :key="movie.id" 
                class="movie-card"
                @click="$router.push(`/movie/${movie.id}`)"
              >
                <div class="movie-poster">
                  <img :src="movie.poster" :alt="movie.title" />
                  <div class="movie-rating" v-if="movie.rating">
                    <el-rate :model-value="movie.rating / 2" disabled show-score :max="5" />
                    <span class="rating-score">{{ movie.rating }}</span>
                  </div>
                  <div class="movie-overlay">
                    <el-button type="danger" round>立即购票</el-button>
                  </div>
                </div>
                <div class="movie-info">
                  <h3 class="movie-title">{{ movie.title }}</h3>
                  <p class="movie-category">{{ movie.category }}</p>
                  <p class="movie-price">
                    <span class="price-label">起</span>
                    <span class="price-value">¥{{ movie.price }}</span>
                  </p>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 评分排行 -->
          <div class="box-office-section">
            <h2 class="section-title" style="margin-bottom: 20px;">
              <el-icon size="28" color="#ff6b6b"><Star /></el-icon>
              评分排行
            </h2>
            
            <div class="box-office-list">
              <div 
                v-for="(item, index) in ratingList" 
                :key="index" 
                class="box-office-item"
                :class="{ 'top-item': index === 0 }"
                @click="$router.push(`/movie/${item.movieId}`)"
              >
                <div v-if="index === 0" class="top-item-content">
                  <img :src="item.poster" :alt="item.title" class="top-poster" />
                  <div class="top-info">
                    <span class="rank-badge">1</span>
                    <h4 class="movie-name">{{ item.title }}</h4>
                    <p class="box-office-amount">{{ item.rating }}分</p>
                  </div>
                </div>
                <div v-else class="other-item">
                  <span class="rank-number">{{ index + 1 }}</span>
                  <span class="movie-name">{{ item.title }}</span>
                  <span class="box-office-amount">{{ item.rating }}分</span>
                </div>
              </div>
            </div>
            
            <div class="box-office-footer">
              <div class="total-box-office">
                <div class="total-label">平均评分</div>
                <div class="total-amount">{{ averageRating }}分</div>
              </div>
              <router-link to="/movies" class="view-more-box-office">查看更多</router-link>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 即将上映 -->
    <section class="section section-gray">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon size="28" color="#667eea"><Calendar /></el-icon>
            即将上映
          </h2>
          <router-link to="/movies" class="view-all">
            查看全部 <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
        
        <div class="coming-soon-list">
          <div 
            v-for="movie in comingMovies" 
            :key="movie.id" 
            class="coming-item"
            @click="$router.push(`/movie/${movie.id}`)"
          >
            <div class="coming-poster">
              <img :src="movie.poster" :alt="movie.title" />
              <div class="coming-date">
                <span class="month">{{ formatMonth(movie.releaseDate) }}</span>
                <span class="day">{{ formatDay(movie.releaseDate) }}</span>
              </div>
            </div>
            <div class="coming-info">
              <h3 class="coming-title">{{ movie.title }}</h3>
              <p class="coming-wish">{{ Math.floor(Math.random() * 50000) + 10000 }}人想看</p>
              <el-button type="primary" plain size="small">预售</el-button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 热门影院 -->
    <section class="section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon size="28" color="#ff6b6b"><Location /></el-icon>
            热门影院
          </h2>
          <router-link to="/cinemas" class="view-all">
            查看全部 <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
        
        <div class="cinema-list">
          <div 
            v-for="cinema in cinemas" 
            :key="cinema.cinemaId" 
            class="cinema-card"
          >
            <div class="cinema-header">
              <h3 class="cinema-name">{{ cinema.cinemaName }}</h3>
              <span class="cinema-price">{{ cinema.rating }}<small>分</small></span>
            </div>
            <p class="cinema-address">
              <el-icon><Location /></el-icon>
              {{ cinema.address }}
            </p>
            <div class="cinema-tags">
              <el-tag v-for="(service, index) in ['IMAX厅', '杜比全景声', '3D放映'].slice(0, 3)" :key="index" size="small" effect="plain">
                {{ service }}
              </el-tag>
            </div>
            <div class="cinema-footer">
              <span class="cinema-distance">
                <el-icon><Position /></el-icon>
                3.5km
              </span>
              <el-button type="primary" size="small"  @click="gotoCinemaDetail(cinema.cinemaId)">选座购票</el-button>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { mockMovies, mockCinemas } from '@/api/mockData'
import { bannerAPI, movieAPI, cinemaAPI } from '@/api/api'
import { VideoCamera, Calendar, Location, ArrowRight, Position, TrendCharts } from '@element-plus/icons-vue'

const router = useRouter()

const banners = ref([])

// 加载轮播图数据
const loadBanners = async () => {
  try {
    const response = await bannerAPI.getBanners()
    banners.value = response.data
  } catch (error) {
    console.error('加载轮播图失败:', error)
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadBanners()
  loadHotMovies()
  loadComingMovies()
  loadCinemas()
  loadBoxOffice()
})

const hotMovies = ref([])
const comingMovies = ref([])
const cinemas = ref([])
const ratingList = ref([])
const averageRating = ref('')

// 加载热映电影数据
const loadHotMovies = async () => {
  try {
    const response = await movieAPI.getHotMovies()
    hotMovies.value = response.data.data.map(movie => ({
      ...movie,
      id: movie.movieId || movie.id
    }))
  } catch (error) {
    console.error('加载热映电影失败:', error)
    hotMovies.value = mockMovies.filter(m => m.status === '上映中').slice(0, 6)
  }
}

// 加载即将上映电影数据
const loadComingMovies = async () => {
  try {
    const response = await movieAPI.getComingMovies()
    comingMovies.value = response.data.data.map(movie => ({
      ...movie,
      id: movie.movieId || movie.id
    }))
  } catch (error) {
    console.error('加载即将上映电影失败:', error)
    comingMovies.value = mockMovies.filter(m => m.status === '即将上映').slice(0, 4)
  }
}

// 加载热门影院数据
const loadCinemas = async () => {
  try {
    const response = await cinemaAPI.getHotCinemas()
    cinemas.value = response.data.data
  } catch (error) {
    console.error('加载热门影院失败:', error)
  }
}

// 加载评分排行数据
const loadBoxOffice = async () => {
  try {
    const response = await movieAPI.getHotMovies()
    if (response.data.code === 200) {
      const movies = response.data.data.map(movie => ({
        ...movie,
        movieId: movie.movieId || movie.id,
        rating: movie.rating
      })).sort((a, b) => b.rating - a.rating).slice(0, 5)
      
      ratingList.value = movies
      if (movies.length > 0) {
        const avg = movies.reduce((sum, m) => sum + (m.rating || 0), 0) / movies.length
        averageRating.value = avg.toFixed(1)
      }
    }
  } catch (error) {
    console.error('加载评分数据失败:', error)
    ratingList.value = [
      { movieId: 1, title: '流浪地球3', poster: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=sci-fi%20movie%20poster%20space%20earth&image_size=portrait_4_3', rating: 9.6 },
      { movieId: 2, title: '第二十条', rating: 9.2 },
      { movieId: 3, title: '热辣滚烫', rating: 9.0 },
      { movieId: 4, title: '飞驰人生2', rating: 8.8 },
      { movieId: 5, title: '熊出没·逆转时空', rating: 8.5 }
    ]
    averageRating.value = '9.0'
  }
}

const formatMonth = (dateStr) => {
  const date = new Date(dateStr)
  return date.getMonth() + 1 + '月'
}

const formatDay = (dateStr) => {
  const date = new Date(dateStr)
  return date.getDate()
}

const gotoCinemaDetail = (cinemaId) => {
  router.push(`/cinema/${cinemaId}`)
}
</script>

<style scoped>
.home {
  background: #f8f9fa;
}

/* 轮播图 */
.hero-section {
  margin-bottom: 40px;
}

.banner-item {
  height: 100%;
  background-size: cover;
  background-position: center;
  cursor: pointer;
  position: relative;
}

.banner-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to right, rgba(0,0,0,0.7) 0%, rgba(0,0,0,0.3) 50%, transparent 100%);
  display: flex;
  align-items: center;
}

.banner-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 60px;
  color: #fff;
}

.banner-title {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 16px;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.5);
}

.banner-desc {
  font-size: 20px;
  margin-bottom: 24px;
  opacity: 0.9;
}

/* 通用section样式 */
.section {
  padding: 50px 0;
}

.section-gray {
  background: #fff;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 40px;
}

.main-content {
  display: flex;
  gap: 30px;
}

.hot-movies-section {
  flex: 2;
}

.box-office-section {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  height: fit-content;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.view-all {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.view-all:hover {
  color: #ff6b6b;
}

/* 电影卡片网格 */
.movie-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.movie-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.movie-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.15);
}

.movie-poster {
  position: relative;
  aspect-ratio: 2/3;
  overflow: hidden;
}

.movie-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.movie-card:hover .movie-poster img {
  transform: scale(1.05);
}

.movie-rating {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 8px 12px;
  background: linear-gradient(to top, rgba(0,0,0,0.8), transparent);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.rating-score {
  color: #ffb800;
  font-weight: bold;
  font-size: 18px;
}

.movie-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.movie-card:hover .movie-overlay {
  opacity: 1;
}

.movie-info {
  padding: 12px;
}

.movie-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.movie-category {
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
}

.movie-price {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.price-label {
  font-size: 12px;
  color: #999;
}

.price-value {
  font-size: 20px;
  color: #ff6b6b;
  font-weight: bold;
}

/* 即将上映列表 */
.coming-soon-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.coming-item {
  background: #f8f9fa;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.coming-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.1);
}

.coming-poster {
  position: relative;
  aspect-ratio: 2/3;
}

.coming-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.coming-date {
  position: absolute;
  top: 10px;
  left: 10px;
  background: rgba(255,107,107,0.95);
  color: #fff;
  padding: 8px 12px;
  border-radius: 8px;
  text-align: center;
  min-width: 50px;
}

.coming-date .month {
  display: block;
  font-size: 12px;
}

.coming-date .day {
  display: block;
  font-size: 24px;
  font-weight: bold;
}

.coming-info {
  padding: 16px;
}

.coming-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.coming-wish {
  font-size: 13px;
  color: #999;
  margin-bottom: 12px;
}

/* 影院列表 */
.cinema-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.cinema-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  transition: all 0.3s;
}

.cinema-card:hover {
  box-shadow: 0 8px 16px rgba(0,0,0,0.12);
}

.cinema-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.cinema-name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.cinema-price {
  font-size: 24px;
  color: #ff6b6b;
  font-weight: bold;
}

.cinema-price small {
  font-size: 14px;
  color: #999;
  font-weight: normal;
}

.cinema-address {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
  font-size: 14px;
  margin-bottom: 12px;
}

.cinema-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.cinema-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cinema-distance {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #999;
  font-size: 14px;
}

/* 票房榜单 */
.box-office-list {
  margin-bottom: 20px;
}

.box-office-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.2s;
}

.box-office-item:hover {
  background: #f8f9fa;
}

.box-office-item:last-child {
  border-bottom: none;
}

.top-item-content {
  display: flex;
  gap: 12px;
}

.top-poster {
  width: 60px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
}

.top-info {
  flex: 1;
  position: relative;
}

.rank-badge {
  position: absolute;
  top: 0;
  right: 0;
  background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
  color: #fff;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
}

.other-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.rank-number {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 14px;
  font-weight: bold;
}

.rank-number:nth-child(-n+3) {
  color: #ff6b6b;
}

.movie-name {
  flex: 1;
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.box-office-amount {
  color: #ff6b6b;
  font-size: 14px;
  font-weight: bold;
}

.box-office-footer {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.total-box-office {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.total-label {
  background: #ff6b6b;
  color: #fff;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.total-amount {
  font-size: 28px;
  color: #ff6b6b;
  font-weight: bold;
}

.view-more-box-office {
  color: #ff6b6b;
  text-decoration: none;
  font-size: 14px;
}

.view-more-box-office:hover {
  text-decoration: underline;
}

/* 响应式 */
@media (max-width: 1200px) {
  .main-content {
    flex-direction: column;
  }
  
  .movie-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .coming-soon-list {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .movie-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .coming-soon-list {
    grid-template-columns: 1fr;
  }
  
  .cinema-list {
    grid-template-columns: 1fr;
  }
  
  .banner-title {
    font-size: 32px;
  }
  
  .banner-desc {
    font-size: 16px;
  }
}
</style>
