<template>
  <div class="select-seat-page">
    <div class="container">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-spinner size="large" />
        <p>加载中...</p>
      </div>
      
      <!-- 错误状态 -->
      <div v-else-if="error" class="error-container">
        <el-icon name="warning" :size="48" />
        <p>{{ error }}</p>
        <el-button @click="loadScheduleDetail">重新加载</el-button>
      </div>
      
      <!-- 内容 -->
      <template v-else>
        <!-- 影片信息 -->
        <div class="movie-info-bar">
          <img :src="movie?.poster" class="movie-thumb" />
          <div class="movie-info">
            <h2>{{ movie?.title }}</h2>
            <p>{{ schedule?.startTime }} | {{ schedule?.hallName }}</p>
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
          <el-button type="danger" size="large" :loading="submitting" @click="confirmOrder">
            确认选座
          </el-button>
        </div>
      </div>
        </template>
    </div>
    
    <!-- 订单详情确认弹窗 -->
    <el-dialog
      v-model="showOrderDetailDialog"
      title="确认订单"
      width="500px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <div class="order-detail-dialog">
        <div class="order-movie-info">
          <img :src="movie?.poster" class="movie-poster" />
          <div class="movie-detail">
            <h3>{{ movie?.title }}</h3>
            <p>{{ schedule?.cinemaName }}</p>
            <p>{{ schedule?.hallName }} | {{ schedule?.startTime }}</p>
          </div>
        </div>
        
        <div class="order-seats">
          <h4>座位信息</h4>
          <div class="seat-list">
            <el-tag
              v-for="seat in selectedSeats"
              :key="seat.row + '-' + seat.col"
            >
              {{ seat.rowLabel }}排{{ seat.colLabel }}座
            </el-tag>
          </div>
        </div>
        
        <div class="order-summary">
          <div class="summary-row">
            <span>座位数量</span>
            <span>{{ selectedSeats.length }} 个</span>
          </div>
          <div class="summary-row">
            <span>单价</span>
            <span>¥{{ schedule?.price }}</span>
          </div>
          <div class="summary-row total">
            <span>应付金额</span>
            <span class="total-price">¥{{ totalPrice }}</span>
          </div>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="handleOrderCancel">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitPayment">
          确认支付
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { scheduleAPI, orderAPI, alipayAPI } from '@/api/api'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const scheduleId = parseInt(route.params.scheduleId)

const loading = ref(true)
const error = ref(null)
const schedule = ref(null)
const movie = ref(null)
const seats = ref([])
const selectedSeats = ref([])
const submitting = ref(false)
const showOrderDetailDialog = ref(false)
const currentOrderId = ref(null)

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
  if (!schedule.value || !schedule.value.price) return 0
  return selectedSeats.value.length * schedule.value.price
})

const loadScheduleDetail = async () => {
  loading.value = true
  error.value = null
  
  try {
    const response = await scheduleAPI.getScheduleDetail(scheduleId)
    if (response.data.code === 200) {
      const data = response.data.data
      if (!data || !data.schedule || !data.movie) {
        error.value = '获取排期信息失败'
        return
      }
      schedule.value = data.schedule
      movie.value = data.movie
      seats.value = data.seats || []
    } else {
      error.value = response.data.message || '获取排期信息失败'
    }
  } catch (err) {
    console.error('加载排期详情失败:', err)
    error.value = '加载排期详情失败，请稍后重试'
    ElMessage.error('加载排期详情失败')
  } finally {
    loading.value = false
  }
}

const confirmOrder = async () => {
  if (selectedSeats.value.length === 0) {
    ElMessage.warning('请选择座位')
    return
  }

  submitting.value = true

  try {
    await ElMessageBox.confirm('确定要选择这些座位吗？', '确认选座', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })

    const createResponse = await orderAPI.createOrder({
      scheduleId: scheduleId,
      userId: userStore.userInfo.id || userStore.userInfo.userId,
      selectedSeats: selectedSeats.value.map(seat => ({
        row: seat.row,
        rowLabel: seat.rowLabel,
        col: seat.col,
        colLabel: seat.colLabel
      }))
    })

    if (createResponse.data.code === 200) {
      const orderData = createResponse.data.data
      currentOrderId.value = orderData.orderId
      showOrderDetailDialog.value = true
    } else {
      ElMessage.error(createResponse.data.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('创建订单失败:', error)
      ElMessage.error('创建订单失败')
    }
  } finally {
    submitting.value = false
  }
}

const submitPayment = async () => {
  if (!currentOrderId.value) return
  
  submitting.value = true

  try {
    const payResponse = await alipayAPI.createPayment(currentOrderId.value)
    
    if (payResponse.data.code === 200) {
      const data = payResponse.data.data
      
      if (typeof data === 'string') {
        const tempDiv = document.createElement('div')
        tempDiv.innerHTML = data
        tempDiv.style.display = 'none'
        document.body.appendChild(tempDiv)
        
        const form = tempDiv.querySelector('form')
        if (form) {
          form.submit()
        } else {
          document.write(data)
          document.close()
        }
      } else {
        ElMessage.error('支付数据格式不正确')
      }
    } else {
      ElMessage.error(payResponse.data.message || '创建支付失败')
    }
  } catch (error) {
    console.error('创建支付失败:', error)
    ElMessage.error('创建支付失败')
  } finally {
    submitting.value = false
    showOrderDetailDialog.value = false
  }
}

const handleOrderCancel = () => {
  showOrderDetailDialog.value = false
  currentOrderId.value = null
}

onMounted(() => {
  loadScheduleDetail()
})
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

.loading-container, .error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 0;
  color: #666;
}

.loading-container p, .error-container p {
  margin-top: 20px;
  font-size: 16px;
}

.error-container {
  color: #ff6b6b;
}

.error-container el-icon {
  color: #ff6b6b;
}

.error-container el-button {
  margin-top: 20px;
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

.order-detail-dialog {
  padding: 10px 0;
}

.order-movie-info {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.movie-poster {
  width: 80px;
  height: 110px;
  object-fit: cover;
  border-radius: 6px;
}

.movie-detail h3 {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.movie-detail p {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.order-seats {
  margin-bottom: 20px;
}

.order-seats h4 {
  font-size: 15px;
  font-weight: bold;
  color: #333;
  margin-bottom: 12px;
}

.seat-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.order-summary {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 14px;
  color: #666;
}

.summary-row.total {
  padding-top: 12px;
  margin-top: 8px;
  border-top: 1px dashed #ddd;
  font-weight: bold;
}

.summary-row.total span:first-child {
  color: #333;
}

.summary-row .total-price {
  font-size: 24px;
  color: #ff6b6b;
  font-weight: bold;
}

</style>
