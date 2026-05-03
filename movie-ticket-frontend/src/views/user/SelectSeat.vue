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
          <el-button type="danger" size="large" @click="confirmOrder">
            确认选座
          </el-button>
        </div>
      </div>
        </template>
    </div>
    
    <!-- 支付二维码弹窗 -->
    <el-dialog
      v-model="showPaymentDialog"
      title="订单支付"
      width="400px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <div class="payment-dialog">
        <div class="payment-info">
          <p class="order-title">{{ movie?.title }}</p>
          <p class="order-seats">座位：{{ selectedSeats.map(s => `${s.rowLabel}排${s.colLabel}座`).join('、') }}</p>
          <p class="order-price">¥{{ totalPrice }}</p>
        </div>
        
        <div class="qrcode-container">
          <div class="qrcode-placeholder">
            <div class="qrcode-box">
              <span class="qrcode-icon">📱</span>
            </div>
            <p class="qrcode-tip">请使用微信或支付宝扫码支付</p>
            <p class="qrcode-hint">支付成功后自动跳转</p>
          </div>
        </div>
        
        <div class="payment-status" v-if="paymentStatus === 'paying'">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>等待支付...</span>
        </div>
        <div class="payment-status success" v-else-if="paymentStatus === 'success'">
          <el-icon color="#67C23A"><CircleCheck /></el-icon>
          <span>支付成功！</span>
        </div>
        <div class="payment-status error" v-else-if="paymentStatus === 'error'">
          <el-icon color="#F56C6C"><CircleClose /></el-icon>
          <span>支付失败</span>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="handlePaymentCancel">取消支付</el-button>
        <el-button type="primary" @click="simulatePaymentSuccess" :loading="paymentStatus === 'paying'">
          模拟支付成功
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading, CircleCheck, CircleClose } from '@element-plus/icons-vue'
import { scheduleAPI, orderAPI } from '@/api/api'

const route = useRoute()
const router = useRouter()
const scheduleId = parseInt(route.params.scheduleId)

const loading = ref(true)
const error = ref(null)
const schedule = ref(null)
const movie = ref(null)
const seats = ref([])
const selectedSeats = ref([])
const showPaymentDialog = ref(false)
const currentOrder = ref(null)
const paymentStatus = ref('idle') // idle, paying, success, error

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

  try {
    await ElMessageBox.confirm('确定要选择这些座位吗？', '确认选座', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })

    const response = await orderAPI.createOrder({
      scheduleId: scheduleId,
      userId: 1,
      selectedSeats: selectedSeats.value.map(seat => ({
        row: seat.row,
        rowLabel: seat.rowLabel,
        col: seat.col,
        colLabel: seat.colLabel
      }))
    })

    if (response.data.code === 200) {
      currentOrder.value = response.data.data
      paymentStatus.value = 'idle'
      showPaymentDialog.value = true
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('创建订单失败:', error)
      ElMessage.error('创建订单失败')
    }
  }
}

const simulatePaymentSuccess = () => {
  paymentStatus.value = 'paying'
  
  setTimeout(() => {
    paymentStatus.value = 'success'
    ElMessage.success('支付成功！')
    
    setTimeout(() => {
      showPaymentDialog.value = false
      router.push(`/order/${currentOrder.value.orderId}`)
    }, 1500)
  }, 2000)
}

const handlePaymentCancel = () => {
  showPaymentDialog.value = false
  paymentStatus.value = 'idle'
  ElMessage.warning('您取消了支付')
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

.payment-dialog {
  text-align: center;
}

.payment-info {
  margin-bottom: 30px;
}

.payment-info .order-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.payment-info .order-seats {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
}

.payment-info .order-price {
  font-size: 28px;
  color: #ff6b6b;
  font-weight: bold;
}

.qrcode-container {
  margin: 30px 0;
}

.qrcode-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.qrcode-box {
  width: 200px;
  height: 200px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f9f9f9;
}

.qrcode-icon {
  font-size: 60px;
}

.qrcode-tip {
  margin-top: 16px;
  font-size: 15px;
  color: #666;
}

.qrcode-hint {
  margin-top: 8px;
  font-size: 13px;
  color: #999;
}

.payment-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 20px;
  font-size: 15px;
}

.payment-status.success {
  color: #67C23A;
}

.payment-status.error {
  color: #F56C6C;
}
</style>
