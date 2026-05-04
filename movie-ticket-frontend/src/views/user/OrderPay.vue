<template>
  <div class="pay-page">
    <div class="container">
      <div v-if="loading" class="loading">
        <div class="spinner"></div>
        <p>正在获取支付信息...</p>
      </div>

      <div v-else-if="error" class="error">
        <el-alert type="error" :title="error" show-icon />
        <el-button type="primary" @click="goBack">返回订单列表</el-button>
      </div>

      <div v-else class="pay-content">
        <div class="order-summary">
          <h2>订单支付</h2>
          <div class="order-info">
            <p><strong>订单号：</strong>{{ orderInfo.orderId }}</p>
            <p><strong>影片：</strong>{{ orderInfo.movieName }}</p>
            <p><strong>影院：</strong>{{ orderInfo.cinemaName }}</p>
            <p><strong>场次：</strong>{{ orderInfo.startTime }} {{ orderInfo.hallName }}</p>
            <p><strong>座位：</strong>{{ formatSeats(orderInfo.seats) }}</p>
            <p class="total"><strong>应付金额：</strong><span class="price">¥{{ orderInfo.totalPrice }}</span></p>
          </div>
        </div>

        <div class="pay-options">
          <h3>选择支付方式</h3>
          <div class="pay-method">
            <el-radio-group v-model="payMethod" class="pay-radio">
              <el-radio value="alipay" label="alipay">
                <span class="pay-icon alipay">
                  <svg width="32" height="32" viewBox="0 0 24 24" fill="#1677FF">
                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z"/>
                  </svg>
                </span>
                <span>支付宝支付</span>
              </el-radio>
            </el-radio-group>
          </div>

          <div class="pay-button">
            <el-button 
              type="primary" 
              size="large" 
              :loading="submitting"
              @click.prevent="submitPay"
              class="submit-btn"
            >
              立即支付 ¥{{ orderInfo.totalPrice }}
            </el-button>
          </div>

          <div v-if="showAlipayForm" ref="alipayFormContainer" class="alipay-form">
            <div v-html="alipayForm"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { orderAPI, alipayAPI } from '@/api/api'

const route = useRoute()
const router = useRouter()

const orderId = route.params.orderId
const loading = ref(true)
const error = ref('')
const submitting = ref(false)
const payMethod = ref('alipay')
const showAlipayForm = ref(false)
const alipayForm = ref('')
const alipayFormContainer = ref(null)

const orderInfo = ref({
  orderId: '',
  movieName: '',
  cinemaName: '',
  hallName: '',
  startTime: '',
  seats: '',
  totalPrice: 0
})

const formatSeats = (seats) => {
  try {
    const seatArray = JSON.parse(seats)
    return seatArray.map(s => `${s.rowLabel}排${s.colLabel}座`).join('、')
  } catch {
    return seats
  }
}

const loadOrderInfo = async () => {
  try {
    const response = await orderAPI.getOrderById(orderId)
    if (response.data.code === 200) {
      const data = response.data.data
      orderInfo.value = {
        orderId: data.orderId,
        movieName: data.movieName,
        cinemaName: data.cinemaName,
        hallName: data.hallName,
        startTime: data.startTime,
        seats: data.selectedSeats ? JSON.stringify(data.selectedSeats) : '[]',
        totalPrice: data.totalPrice
      }
    } else {
      error.value = response.data.message || '获取订单信息失败'
    }
  } catch (err) {
    error.value = '获取订单信息失败: ' + err.message
  } finally {
    loading.value = false
  }
}

const submitPay = async () => {
  submitting.value = true
  
  try {
    const response = await alipayAPI.createPayment(orderId)
    
    if (response.data.code === 200) {
      const data = response.data.data
      
      if (typeof data === 'string') {
        // 创建临时容器来解析表单
        const tempDiv = document.createElement('div')
        tempDiv.innerHTML = data
        tempDiv.style.display = 'none'
        document.body.appendChild(tempDiv)
        
        const form = tempDiv.querySelector('form')
        if (form) {
          // 直接提交表单
          form.submit()
        } else {
          // 备用：直接将HTML作为页面内容
          document.write(data)
          document.close()
        }
      } else {
        error.value = '支付数据格式不正确'
      }
    } else {
      error.value = response.data.message || '创建支付失败'
    }
  } catch (err) {
    error.value = '创建支付失败: ' + err.message
  } finally {
    submitting.value = false
  }
}

const goBack = () => {
  router.push('/orders')
}

onMounted(() => {
  loadOrderInfo()
})
</script>

<style scoped>
.pay-page {
  padding: 40px 0;
  background: #f8f9fa;
  min-height: calc(100vh - 70px);
}

.container {
  max-width: 600px;
  margin: 0 auto;
  padding: 0 20px;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 0;
}

.loading .spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #409eff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading p {
  margin-top: 16px;
  color: #666;
}

.error {
  padding: 60px 20px;
  text-align: center;
}

.error .el-button {
  margin-top: 20px;
}

.pay-content {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.order-summary {
  padding: 24px;
  border-bottom: 1px solid #eee;
}

.order-summary h2 {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}

.order-info p {
  margin-bottom: 12px;
  font-size: 15px;
  color: #666;
}

.order-info strong {
  color: #999;
  margin-right: 8px;
}

.order-info .total {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px dashed #eee;
  font-size: 18px;
}

.order-info .total .price {
  font-size: 28px;
  color: #ff6b6b;
  font-weight: bold;
  margin-left: 8px;
}

.pay-options {
  padding: 24px;
}

.pay-options h3 {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 16px;
  color: #333;
}

.pay-method {
  margin-bottom: 24px;
}

.pay-radio {
  display: flex;
  gap: 16px;
}

.pay-radio .el-radio {
  padding: 12px 20px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 12px;
}

.pay-radio .el-radio.is-checked {
  background: #e6f7ff;
  border: 1px solid #91d5ff;
}

.pay-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.pay-button {
  text-align: center;
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 18px;
  border-radius: 8px;
}

.alipay-form {
  display: none;
}
</style>