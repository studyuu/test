<template>
  <div class="select-seat-page">
    <div class="container">
      <!-- 影片信息 -->
      <div class="movie-info-bar">
        <img :src="movie.poster" class="movie-thumb" />
        <div class="movie-info">
          <h2>{{ movie.title }}</h2>
          <p>{{ schedule.startTime }} | {{ schedule.hallName }}</p>
        </div>
      </div>

      <!-- 选座区域 -->
      <div class="seat-selection">
        <div class="screen">银幕</div>
        
        <div class="seat-map">
          <div v-for="row in seatRows" :key="row" class="seat-row">
            <span class="row-label">{{ row }}</span>
            <div class="seats">
              <div
                v-for="seat in getSeatsByRow(row)"
                :key="seat.row + '-' + seat.col"
                class="seat"
                :class="{
                  'available': seat.status === 'available',
                  'sold': seat.status === 'sold',
                  'selected': isSelected(seat)
                }"
                @click="toggleSeat(seat)"
              >
                {{ seat.col }}
              </div>
            </div>
          </div>
        </div>

        <div class="seat-legend">
          <div class="legend-item">
            <div class="seat-box available"></div>
            <span>可选</span>
          </div>
          <div class="legend-item">
            <div class="seat-box sold"></div>
            <span>已售</span>
          </div>
          <div class="legend-item">
            <div class="seat-box selected"></div>
            <span>已选</span>
          </div>
        </div>
      </div>

      <!-- 订单信息 -->
      <div class="order-panel" v-if="selectedSeats.length > 0">
        <div class="selected-info">
          <h3>已选座位</h3>
          <div class="seat-tags">
            <el-tag
              v-for="seat in selectedSeats"
              :key="seat.row + '-' + seat.col"
              closable
              @close="toggleSeat(seat)"
            >
              {{ seat.rowLabel }}排{{ seat.colLabel }}座
            </el-tag>
          </div>
        </div>
        <div class="price-info">
          <span class="total-price">¥{{ totalPrice }}</span>
          <el-button type="danger" size="large" @click="confirmOrder">
            确认选座
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { mockMovies, mockSchedules } from '@/api/mockData'

const route = useRoute()
const router = useRouter()
const scheduleId = parseInt(route.params.scheduleId)

const schedule = computed(() => {
  return mockSchedules.find(s => s.id === scheduleId) || mockSchedules[0]
})

const movie = computed(() => {
  return mockMovies.find(m => m.id === schedule.value.movieId) || mockMovies[0]
})

const seats = ref(schedule.value.seats || [])
const selectedSeats = ref([])

const seatRows = computed(() => {
  const rows = [...new Set(seats.value.map(s => s.rowLabel))]
  return rows
})

const getSeatsByRow = (row) => {
  return seats.value.filter(s => s.rowLabel === row).sort((a, b) => a.col - b.col)
}

const isSelected = (seat) => {
  return selectedSeats.value.some(s => s.row === seat.row && s.col === seat.col)
}

const toggleSeat = (seat) => {
  if (seat.status === 'sold') {
    ElMessage.warning('该座位已售出')
    return
  }
  
  const index = selectedSeats.value.findIndex(s => s.row === seat.row && s.col === seat.col)
  if (index > -1) {
    selectedSeats.value.splice(index, 1)
  } else {
    if (selectedSeats.value.length >= 4) {
      ElMessage.warning('最多只能选择4个座位')
      return
    }
    selectedSeats.value.push(seat)
  }
}

const totalPrice = computed(() => {
  return selectedSeats.value.length * schedule.value.price
})

const confirmOrder = () => {
  const orderId = 'ORD' + Date.now()
  ElMessage.success('选座成功，正在生成订单...')
  setTimeout(() => {
    router.push(`/order/${orderId}`)
  }, 1000)
}
</script>

<style scoped>
.select-seat-page {
  padding: 30px 0;
  background: #f8f9fa;
  min-height: calc(100vh - 70px);
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 40px;
}

.movie-info-bar {
  display: flex;
  align-items: center;
  gap: 20px;
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.movie-thumb {
  width: 80px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
}

.movie-info h2 {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.movie-info p {
  font-size: 14px;
  color: #666;
}

.seat-selection {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.screen {
  text-align: center;
  padding: 15px;
  background: linear-gradient(to bottom, #e0e0e0, #f5f5f5);
  border-radius: 50% 50% 0 0;
  margin-bottom: 40px;
  color: #666;
  font-size: 14px;
  letter-spacing: 8px;
}

.seat-map {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 30px;
}

.seat-row {
  display: flex;
  align-items: center;
  gap: 20px;
}

.row-label {
  width: 30px;
  text-align: center;
  color: #999;
  font-size: 14px;
}

.seats {
  display: flex;
  gap: 8px;
  flex: 1;
  justify-content: center;
}

.seat {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #ddd;
}

.seat.available {
  background: #fff;
  color: #666;
}

.seat.available:hover {
  background: #ff6b6b;
  color: #fff;
  border-color: #ff6b6b;
}

.seat.sold {
  background: #e0e0e0;
  color: #999;
  cursor: not-allowed;
}

.seat.selected {
  background: #ff6b6b;
  color: #fff;
  border-color: #ff6b6b;
}

.seat-legend {
  display: flex;
  justify-content: center;
  gap: 40px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.seat-box {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  border: 1px solid #ddd;
}

.seat-box.available {
  background: #fff;
}

.seat-box.sold {
  background: #e0e0e0;
}

.seat-box.selected {
  background: #ff6b6b;
  border-color: #ff6b6b;
}

.order-panel {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.selected-info h3 {
  font-size: 16px;
  color: #333;
  margin-bottom: 12px;
}

.seat-tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.price-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.total-price {
  font-size: 32px;
  color: #ff6b6b;
  font-weight: bold;
}
</style>
