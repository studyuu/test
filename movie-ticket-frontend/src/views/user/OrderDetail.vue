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
            <div class="ticket-qr">
              <div class="qr-placeholder">
                <svg width="120" height="120" viewBox="0 0 120 120">
                  <rect width="120" height="120" fill="#fff" stroke="#ddd" stroke-width="1"/>
                  <rect x="10" y="10" width="20" height="20" fill="#333"/>
                  <rect x="90" y="10" width="20" height="20" fill="#333"/>
                  <rect x="10" y="90" width="20" height="20" fill="#333"/>
                  <rect x="20" y="20" width="80" height="80" fill="#fff" stroke="#333" stroke-width="1"/>
                  <rect v-for="i in 25" :key="i" 
                    :x="28 + ((i-1) % 5) * 14" 
                    :y="28 + Math.floor((i-1)/5) * 14" 
                    width="8" height="8" 
                    :fill="Math.random() > 0.5 ? '#333' : '#fff'"/>
                </svg>
              </div>
              <p class="ticket-code">{{ orderDetail.ticketCode }}</p>
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
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { orderAPI } from '@/api/api'

const route = useRoute()
const loading = ref(true)
const showTicketCode = ref(false)
const orderDetail = ref({})

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

const toggleTicketCode = () => {
  showTicketCode.value = !showTicketCode.value
}

const loadOrderDetail = async () => {
  loading.value = true
  try {
    const orderId = route.params.orderId
    const response = await orderAPI.getOrderDetail(orderId)
    if (response.data.code === 200) {
      orderDetail.value = response.data.data
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
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-top: 20px;
  text-align: center;
}

.ticket-qr {
  display: inline-block;
}

.qr-placeholder {
  background: #fff;
  padding: 10px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.ticket-code {
  margin-top: 12px;
  font-size: 18px;
  font-weight: bold;
  color: #333;
  font-family: monospace;
}

.order-actions {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
</style>
