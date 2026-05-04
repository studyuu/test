<template>
  <div class="payment-success-page">
    <div class="container">
      <div class="success-card">
        <div class="success-icon">
          <svg class="icon" viewBox="0 0 1024 1024" width="80" height="80">
            <path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm193.5 301.7l-210.6 292a31.8 31.8 0 01-51.7 0L318.5 484.9a31.8 31.8 0 0151.7-41.2l114.3 157.8 184.7-255.9a31.8 31.8 0 0151.7 41.2z" fill="#67c23a"/>
          </svg>
        </div>
        
        <h1 class="success-title">支付成功</h1>
        <p class="success-message">您的订单已支付完成，请凭取票码到影院取票</p>
        
        <div class="order-info" v-if="orderInfo">
          <div class="info-item">
            <span class="label">订单号</span>
            <span class="value">{{ orderInfo.out_trade_no }}</span>
          </div>
          <div class="info-item" v-if="orderInfo.total_amount">
            <span class="label">支付金额</span>
            <span class="value price">¥{{ orderInfo.total_amount }}</span>
          </div>
          <div class="info-item" v-if="orderInfo.trade_no">
            <span class="label">交易号</span>
            <span class="value">{{ orderInfo.trade_no }}</span>
          </div>
        </div>
        
        <div class="ticket-code" v-if="ticketCode">
          <p class="ticket-label">您的取票码</p>
          <div class="ticket-value">{{ ticketCode }}</div>
        </div>
        
        <div class="action-buttons">
          <el-button type="primary" size="large" @click="goToOrders">
            查看订单
          </el-button>
          <el-button size="large" @click="goToHome">
            返回首页
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { orderAPI, alipayAPI } from '@/api/api'

const route = useRoute()
const router = useRouter()

const orderInfo = ref(null)
const ticketCode = ref('')
const isRefreshing = ref(false)

const getOrderInfoFromURL = () => {
  orderInfo.value = {
    out_trade_no: route.query.out_trade_no,
    total_amount: route.query.total_amount,
    trade_no: route.query.trade_no
  }
  
  if (orderInfo.value.out_trade_no) {
    ticketCode.value = 'QR' + orderInfo.value.out_trade_no.substring(3)
  }
}

const refreshOrderStatus = async () => {
  if (!orderInfo.value?.out_trade_no) return
  
  isRefreshing.value = true
  try {
    const response = await alipayAPI.queryPayment(orderInfo.value.out_trade_no)
    if (response.data.code === 200) {
      const data = response.data.data
      console.log('支付宝订单状态:', data)
      
      if (data.tradeStatus === 'TRADE_SUCCESS' || data.tradeStatus === 'TRADE_FINISHED') {
        await orderAPI.refreshOrderStatus(orderInfo.value.out_trade_no)
        console.log('订单状态已同步')
      }
    }
  } catch (error) {
    console.error('刷新订单状态失败:', error)
  } finally {
    isRefreshing.value = false
  }
}

const goToOrders = () => {
  router.push('/orders')
}

const goToHome = () => {
  router.push('/home')
}

onMounted(() => {
  getOrderInfoFromURL()
  refreshOrderStatus()
})
</script>

<style scoped>
.payment-success-page {
  padding: 60px 0;
  background: #f8f9fa;
  min-height: calc(100vh - 70px);
}

.container {
  max-width: 500px;
  margin: 0 auto;
  padding: 0 20px;
}

.success-card {
  background: #fff;
  border-radius: 16px;
  padding: 48px 40px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.success-icon {
  margin-bottom: 24px;
}

.success-title {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 12px;
}

.success-message {
  font-size: 16px;
  color: #666;
  margin-bottom: 32px;
  line-height: 1.6;
}

.order-info {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-item .label {
  color: #999;
  font-size: 14px;
}

.info-item .value {
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

.info-item .value.price {
  color: #ff6b6b;
  font-size: 20px;
  font-weight: bold;
}

.ticket-code {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 32px 24px;
  margin-bottom: 32px;
}

.ticket-label {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  margin-bottom: 12px;
}

.ticket-value {
  color: #fff;
  font-size: 32px;
  font-weight: bold;
  font-family: 'Courier New', monospace;
  letter-spacing: 4px;
}

.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.action-buttons .el-button {
  min-width: 140px;
  height: 48px;
  font-size: 16px;
  border-radius: 8px;
}
</style>
