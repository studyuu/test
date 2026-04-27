<template>
  <div class="movies-page">
    <div class="container">
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <div class="filter-section">
          <span class="filter-label">类型：</span>
          <div class="filter-options">
            <span 
              v-for="type in movieTypes" 
              :key="type"
              class="filter-item"
              :class="{ active: selectedType === type }"
              @click="selectedType = type"
            >
              {{ type }}
            </span>
          </div>
        </div>
        <div class="filter-section">
          <span class="filter-label">地区：</span>
          <div class="filter-options">
            <span 
              v-for="region in regions" 
              :key="region"
              class="filter-item"
              :class="{ active: selectedRegion === region }"
              @click="selectedRegion = region"
            >
              {{ region }}
            </span>
          </div>
        </div>
        <div class="filter-section">
          <span class="filter-label">年代：</span>
          <div class="filter-options">
            <span 
              v-for="year in years" 
              :key="year"
              class="filter-item"
              :class="{ active: selectedYear === year }"
              @click="selectedYear = year"
            >
              {{ year }}
            </span>
          </div>
        </div>
      </div>

      <!-- 排序栏 -->
      <div class="sort-bar">
        <div class="sort-options">
          <span 
            v-for="sort in sortOptions" 
            :key="sort.value"
            class="sort-item"
            :class="{ active: selectedSort === sort.value }"
            @click="selectedSort = sort.value"
          >
            {{ sort.label }}
          </span>
        </div>
        <div class="search-wrapper">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索影片"
            class="search-input"
            clearable
            :prefix-icon="Search"
            @keyup.enter="handleSearch"
          />
          <el-button type="primary" class="search-btn" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </div>
      </div>

      <!-- 影片列表 -->
      <div class="movies-grid">
        <div 
          v-for="movie in movies" 
          :key="movie.movieId"
          class="movie-card"
          @click="$router.push(`/movie/${movie.movieId}`)"
        >
          <div class="movie-poster">
            <img :src="movie.poster" :alt="movie.title" />
            <div class="movie-rating" v-if="movie.rating">
              <span class="rating-score">{{ movie.rating }}</span>
            </div>
            <div class="movie-status" :class="movie.status === 1 ? 'showing' : 'coming'">
              {{ movie.status === 1 ? '上映中' : '即将上映' }}
            </div>
          </div>
          <div class="movie-info">
            <h3 class="movie-title">{{ movie.title }}</h3>
            <p class="movie-english">{{ movie.englishTitle }}</p>
            <p class="movie-meta">{{ movie.category }} | {{ movie.duration }}分钟</p>
            <p class="movie-actors">{{ movie.actors }}</p>
            <div class="movie-footer">
              <span class="movie-price">¥{{ movie.price }}<small>起</small></span>
              <el-button type="danger" size="small" round>购票</el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[12, 24, 36]"
          layout="total, sizes, prev, pager, next"
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { movieAPI } from '@/api/api'

const movieTypes = ref(['全部'])
const regions = ref(['全部'])
const years = ref(['全部'])
const movies = ref([])
const total = ref(0)

const sortOptions = [
  { label: '热门', value: 'hot' },
  { label: '最新', value: 'new' },
  { label: '评分', value: 'rating' },
  { label: '价格', value: 'price' }
]

const selectedType = ref('全部')
const selectedRegion = ref('全部')
const selectedYear = ref('全部')
const selectedSort = ref('hot')
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(12)

const fetchMovieTypes = async () => {
  try {
    const response = await movieAPI.getMovieTypes()
    if (response.data.code === 200) {
      movieTypes.value = ['全部', ...response.data.data]
    }
  } catch (error) {
    console.error('获取电影类型失败:', error)
    movieTypes.value = ['全部', '爱情', '喜剧', '动作', '科幻', '动画', '剧情', '悬疑', '恐怖']
  }
}

const fetchRegions = async () => {
  try {
    const response = await movieAPI.getMovieRegions()
    if (response.data.code === 200) {
      regions.value = ['全部', ...response.data.data]
    }
  } catch (error) {
    console.error('获取地区列表失败:', error)
    regions.value = ['全部', '中国大陆', '美国', '韩国', '日本', '中国香港', '中国台湾', '其他']
  }
}

const fetchYears = async () => {
  try {
    const response = await movieAPI.getMovieYears()
    if (response.data.code === 200) {
      years.value = ['全部', ...response.data.data]
    }
  } catch (error) {
    console.error('获取年代列表失败:', error)
    years.value = ['全部', '2025', '2024', '2023', '2022', '2021', '2020', '更早']
  }
}

const fetchMovies = async () => {
  try {
    const params = {}
    if (selectedType.value !== '全部') params.type = selectedType.value
    if (selectedRegion.value !== '全部') params.region = selectedRegion.value
    if (selectedYear.value !== '全部') params.year = selectedYear.value
    if (searchKeyword.value) params.keyword = searchKeyword.value
    params.sort = selectedSort.value
    params.page = currentPage.value
    params.size = pageSize.value

    const response = await movieAPI.getMovies(params)
    if (response.data.code === 200) {
      movies.value = response.data.data.content
      total.value = response.data.data.totalElements
    }
  } catch (error) {
    console.error('获取电影列表失败:', error)
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchMovies()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchMovies()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchMovies()
}

watch([selectedType, selectedRegion, selectedYear, selectedSort], () => {
  currentPage.value = 1
  nextTick(() => {
    fetchMovies()
  })
})

onMounted(() => {
  fetchMovieTypes()
  fetchRegions()
  fetchYears()
  fetchMovies()
})
</script>

<style scoped>
.movies-page {
  padding: 30px 0;
  background: #f8f9fa;
  min-height: calc(100vh - 70px);
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 40px;
}

.filter-bar {
  background: #fff;
  border-radius: 12px;
  padding: 20px 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.filter-section {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
}

.filter-section:last-child {
  margin-bottom: 0;
}

.filter-label {
  font-size: 14px;
  color: #999;
  margin-right: 16px;
  white-space: nowrap;
  padding-top: 4px;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-item {
  padding: 4px 12px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
}

.filter-item:hover {
  color: #ff6b6b;
}

.filter-item.active {
  background: #ff6b6b;
  color: #fff;
}

.sort-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.sort-options {
  display: flex;
  gap: 24px;
}

.sort-item {
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: color 0.3s;
  position: relative;
}

.sort-item:hover {
  color: #ff6b6b;
}

.sort-item.active {
  color: #ff6b6b;
  font-weight: bold;
}

.sort-item.active::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  right: 0;
  height: 2px;
  background: #ff6b6b;
  border-radius: 1px;
}

.search-wrapper {
  display: flex;
  gap: 8px;
}

.search-input {
  width: 240px;
}

.search-btn {
  white-space: nowrap;
}

.movies-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 24px;
  margin-bottom: 40px;
}

.movie-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.movie-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.12);
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
  top: 10px;
  right: 10px;
  background: rgba(255,107,107,0.95);
  padding: 4px 10px;
  border-radius: 6px;
}

.rating-score {
  color: #fff;
  font-weight: bold;
  font-size: 16px;
}

.movie-status {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  color: #fff;
}

.movie-status.showing {
  background: #67c23a;
}

.movie-status.coming {
  background: #909399;
}

.movie-info {
  padding: 16px;
}

.movie-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.movie-english {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.movie-meta {
  font-size: 13px;
  color: #666;
  margin-bottom: 6px;
}

.movie-actors {
  font-size: 13px;
  color: #999;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.movie-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.movie-price {
  font-size: 20px;
  color: #ff6b6b;
  font-weight: bold;
}

.movie-price small {
  font-size: 12px;
  color: #999;
  font-weight: normal;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

@media (max-width: 1200px) {
  .movies-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 900px) {
  .movies-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 600px) {
  .movies-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .sort-bar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .search-input {
    width: 100%;
  }
}
</style>