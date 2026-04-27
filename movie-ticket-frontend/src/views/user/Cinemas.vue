<template>
  <div class="cinemas-page">
    <div class="container">
      <div class="page-header">
        <h1>影院列表</h1>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索影院"
          class="search-input"
          clearable
          :prefix-icon="Search"
        />
      </div>

      <div class="cinema-list">
        <div v-for="cinema in filteredCinemas" :key="cinema.cinemaId" class="cinema-card">
          <div class="cinema-main">
            <h3 class="cinema-name">{{ cinema.cinemaName }}</h3>
            <p class="cinema-address">
              <el-icon><Location /></el-icon>
              {{ cinema.address }}
            </p>
            <div class="cinema-rating">
              <el-rate :model-value="(cinema.rating || 0) / 2" disabled size="small" />
              <span>{{ cinema.rating || 0 }}分</span>
            </div>
          </div>
          <div class="cinema-action">
            <span class="cinema-phone">{{ cinema.phone }}</span>
            <el-button type="primary" @click="gotoCinemaDetail(cinema.cinemaId)">
              选座购票
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Location } from '@element-plus/icons-vue'
import { cinemaAPI } from '@/api/api'

const router = useRouter()
const searchKeyword = ref('')
const cinemas = ref([])

const filteredCinemas = computed(() => {
  if (!searchKeyword.value) return cinemas.value
  return cinemas.value.filter(c => 
    c.cinemaName.includes(searchKeyword.value) || 
    c.address.includes(searchKeyword.value)
  )
})

const gotoCinemaDetail = (cinemaId) => {
  router.push(`/cinema/${cinemaId}`)
}

const loadCinemas = async () => {
  try {
    const response = await cinemaAPI.getHotCinemas()
    if (response.data.code === 200) {
      cinemas.value = response.data.data
    }
  } catch (error) {
    console.error('加载影院列表失败:', error)
  }
}

onMounted(() => {
  loadCinemas()
})
</script>

<style scoped>
.cinemas-page {
  padding: 40px 0;
  background: #f8f9fa;
  min-height: calc(100vh - 70px);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 40px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: bold;
}

.search-input {
  width: 300px;
}

.cinema-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.cinema-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.cinema-name {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 10px;
}

.cinema-address {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
  margin-bottom: 8px;
}

.cinema-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #999;
  font-size: 14px;
}

.cinema-action {
  text-align: right;
}

.cinema-phone {
  display: block;
  color: #999;
  font-size: 14px;
  margin-bottom: 12px;
}
</style>