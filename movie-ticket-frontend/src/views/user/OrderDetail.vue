<template>
  <div class="order-detail-page">
    <div class="container">
      <div v-if="loading" class="loading">
        <el-loading-spinner />
      </div>
      
      <el-card v-else>
        <template #header>
          <div class="order-header">
            <span>订单详情</span>
            <el-tag :type="getStatusType(orderDetail.status)">{{ orderDetail.status }}</el-tag>
          </div>
        </template>
        
        <div class="order-info">
          <div class="movie-section">
            <img :src="orderDetail.poster" class="movie-poster" />
            <div class="movie-info">
              <h2>{{ orderDetail.movieTitle }}</h2>
              <p>{{ orderDetail.cinemaName }}</p>
              <p>{{ orderDetail.hallName }} | {{ orderDetail.showTime }}</p>
            </div>
          </div>
          
          <div class="detail-section">
            <p><strong>订单号：</strong>{{ orderDetail.orderId }}</p>
            <p><strong>下单时间：</strong>{{ orderDetail.createTime }}</p>
            <p><strong>场次：</strong>{{ orderDetail.showTime }} {{ orderDetail.hallName }}</p>
            <p><strong>座位：</strong>{{ formatSeats(orderDetail.seats) }}</p>
            <p><strong>总价：</strong><span class="price">¥{{ orderDetail.totalPrice }}</span></p>
          </div>
          
          <div v-if="showTicketCode" class="ticket-section">
            <div class="qr-code-container">
              <div ref="qrCodeRef" class="qr-code"></div>
            </div>
            <div class="ticket-code">
              <p class="ticket-label">您的取票码</p>
              <div class="ticket-value">{{ ticketCode }}</div>
            </div>
          </div>
        </div>
        
        <div class="order-actions">
          <el-button type="primary" @click="toggleTicketCode">{{ showTicketCode ? '隐藏取票码' : '查看取票码' }}</el-button>
          <el-button v-if="orderDetail.status === '已完成'">申请退票</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { useRoute } from 'vue-router'
import { orderAPI } from '@/api/api'
import QRCode from 'qrcodejs2-fixes'

const route = useRoute()
const loading = ref(true)
const showTicketCode = ref(false)
const orderDetail = ref({})
const qrCodeRef = ref(null)
const ticketCode = ref('')

const getStatusType = (status) => {
  const map = {
    '待支付': 'warning',
    '已完成': 'success',
    '已取消': 'info'
  }
  return map[status] || 'info'
}

const formatSeats = (seats) => {
  try {
    const seatArray = JSON.parse(seats)
    return seatArray.map(s => `${s.rowLabel}排${s.colLabel}座`).join('、')
  } catch {
    return seats
  }
}

const generateQRCode = () => {
  if (!qrCodeRef.value || !ticketCode.value) return
  
  const qrContent = JSON.stringify({
    ticketCode: ticketCode.value,
    orderId: orderDetail.value?.orderId,
    timestamp: Date.now()
  })
  
  new QRCode(qrCodeRef.value, {
    text: qrContent,
    width: 128,
    height: 128,
    colorDark: '#000000',
    colorLight: '#ffffff',
    correctLevel: QRCode.CorrectLevel.H
  })
}

const toggleTicketCode = async () => {
  showTicketCode.value = !showTicketCode.value
  if (showTicketCode.value) {
    await nextTick()
    generateQRCode()
  }
}

const loadOrderDetail = async () => {
  loading.value = true
  try {
    const orderId = route.params.orderId
    const response = await orderAPI.getOrderDetail(orderId)
    if (response.data.code === 200) {
      orderDetail.value = response.data.data
      if (orderDetail.value.orderId) {
        ticketCode.value = 'QR' + orderDetail.value.orderId.substring(3)
      }
    }
  } catch (error) {
    console.error('加载订单详情失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadOrderDetail()
})
</script>

<style scoped>
.order-detail-page {
  padding: 40px 0;
  background: #f8f9fa;
  min-height: calc(100vh - 70px);
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 40px;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 50px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.movie-section {
  display: flex;
  gap: 20px;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
}

.movie-poster {
  width: 120px;
  height: 160px;
  object-fit: cover;
  border-radius: 8px;
}

.movie-info {
  flex: 1;
}

.movie-info h2 {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 10px;
}

.movie-info p {
  font-size: 14px;
  color: #666;
  margin-bottom: 6px;
}

.detail-section {
  padding: 20px 0;
}

.detail-section p {
  margin-bottom: 12px;
  font-size: 15px;
}

.detail-section strong {
  color: #666;
  margin-right: 8px;
}

.price {
  font-size: 24px;
  color: #ff6b6b;
  font-weight: bold;
}

.ticket-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 24px;
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.qr-code-container {
  background: #fff;
  padding: 12px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.2);
}

.qr-code {
  width: 128px;
  height: 128px;
}

.ticket-code {
  text-align: center;
}

.ticket-label {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  margin-bottom: 8px;
}

.ticket-value {
  color: #fff;
  font-size: 24px;
  font-weight: bold;
  font-family: 'Courier New', monospace;
  letter-spacing: 3px;
}

.order-actions {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
</style>
