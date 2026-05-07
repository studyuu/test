<template>
  <div class="orders-page">
    <div class="container">
      <h1 class="page-title">我的订单</h1>
      
      <el-tabs v-model="activeTab" class="order-tabs" @tab-change="loadOrders">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待支付" name="unpaid" />
        <el-tab-pane label="已完成" name="completed" />
        <el-tab-pane label="已取消" name="cancelled" />
        <el-tab-pane label="退票中" name="refunding" />
        <el-tab-pane label="已退票" name="refunded" />
      </el-tabs>

      <div v-if="loading" class="loading">
        <el-loading-spinner />
      </div>

      <div v-else class="order-list">
        <div v-for="order in orders" :key="order.orderId" class="order-card">
          <div class="order-header">
            <span class="order-id">订单号：{{ order.orderId }}</span>
            <span class="order-time">{{ order.createTime }}</span>
            <el-tag :type="getStatusType(order.status)">{{ order.status }}</el-tag>
          </div>
          <div class="order-content">
            <img :src="order.poster" class="movie-poster" />
            <div class="order-info">
              <h3>{{ order.movieTitle }}</h3>
              <p>{{ order.cinemaName }} {{ order.hallName }}</p>
              <p>{{ order.showTime }}</p>
              <p>座位：{{ formatSeats(order.seats) }}</p>
            </div>
            <div class="order-price">
              <span class="price">¥{{ order.totalPrice }}</span>
              <div class="actions">
                <el-button v-if="order.status === '待支付'" type="primary" @click="payOrder(order.orderId)">立即支付</el-button>
                <el-button v-if="order.status === '待支付'" plain  @click="cancelOrder(order.orderId)" style="margin-left: 0px;">取消订单</el-button>
                <el-button v-if="order.status === '已完成'" type="primary" plain @click="goToDetail(order.orderId)">查看详情</el-button>
                <el-button v-if="order.status === '退票中'" type="primary" plain @click="goToDetail(order.orderId)">查看详情</el-button>
                <el-button v-if="order.status === '已取消' || order.status === '已退票'" type="danger" plain @click="deleteOrder(order.orderId)">删除订单</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="!loading && orders.length === 0" class="empty">
        <el-empty description="暂无订单" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderAPI } from '@/api/api'

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref('all')
const orders = ref([])
const loading = ref(true)

const getStatusType = (status) => {
  const map = {
    '待支付': 'danger',
    '已完成': 'success',
    '已取消': 'info',
    '退票中': 'warning',
    '已退票': 'info'
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

const loadOrders = async () => {
  loading.value = true
  try {
    const statusMap = {
      'all': 'all',
      'unpaid': 'pending',
      'completed': 'completed',
      'cancelled': 'cancelled',
      'refunding': 'refunding',
      'refunded': 'refunded'
    }
    const userId = userStore.userInfo.id || userStore.userInfo.userId || 1
    const response = await orderAPI.getUserOrders(userId, { 
      status: statusMap[activeTab.value] 
    })
    if (response.data.code === 200) {
      orders.value = response.data.data.list
    }
  } catch (error) {
    console.error('加载订单失败:', error)
  } finally {
    loading.value = false
  }
}

const goToDetail = (orderId) => {
  router.push(`/order/${orderId}`)
}

const payOrder = (orderId) => {
  router.push(`/order/${orderId}/pay`)
}

const cancelOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '取消订单', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await orderAPI.cancelOrder(orderId)
    if (response.data.code === 200) {
      ElMessage.success('订单已取消')
      loadOrders()
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  }
}

const deleteOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要删除该订单吗？删除后无法恢复。', '删除订单', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'danger'
    })

    const response = await orderAPI.deleteOrder(orderId)
    if (response.data.code === 200) {
      ElMessage.success('订单已删除')
      loadOrders()
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除订单失败:', error)
      ElMessage.error('删除订单失败')
    }
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.orders-page {
  padding: 40px 0;
  background: #f8f9fa;
  min-height: calc(100vh - 70px);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 40px;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 24px;
}

.order-tabs {
  margin-bottom: 24px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.order-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 16px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #eee;
}

.order-id {
  font-size: 14px;
  color: #666;
}

.order-time {
  flex: 1;
  font-size: 14px;
  color: #999;
}

.order-content {
  display: flex;
  padding: 20px;
  gap: 20px;
}

.movie-poster {
  width: 100px;
  height: 140px;
  object-fit: cover;
  border-radius: 8px;
}

.order-info {
  flex: 1;
}

.order-info h3 {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 12px;
}

.order-info p {
  font-size: 14px;
  color: #666;
  margin-bottom: 6px;
}

.order-price {
  text-align: right;
}

.order-price .price {
  font-size: 24px;
  color: #ff6b6b;
  font-weight: bold;
  display: block;
  margin-bottom: 12px;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
</style>
