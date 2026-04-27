<template>
  <div class="orders-page">
    <div class="container">
      <h1 class="page-title">我的订单</h1>
      
      <el-tabs v-model="activeTab" class="order-tabs">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待支付" name="unpaid" />
        <el-tab-pane label="已完成" name="completed" />
        <el-tab-pane label="已取消" name="cancelled" />
      </el-tabs>

      <div class="order-list">
        <div v-for="order in filteredOrders" :key="order.id" class="order-card">
          <div class="order-header">
            <span class="order-id">订单号：{{ order.id }}</span>
            <span class="order-time">{{ order.createTime }}</span>
            <el-tag :type="getStatusType(order.status)">{{ order.status }}</el-tag>
          </div>
          <div class="order-content">
            <img :src="order.poster" class="movie-poster" />
            <div class="order-info">
              <h3>{{ order.movieTitle }}</h3>
              <p>{{ order.cinemaName }} {{ order.hallName }}</p>
              <p>{{ order.showTime }}</p>
              <p>座位：{{ order.seats.join('、') }}</p>
            </div>
            <div class="order-price">
              <span class="price">¥{{ order.totalPrice }}</span>
              <div class="actions">
                <el-button v-if="order.status === '待支付'" type="danger">立即支付</el-button>
                <el-button v-if="order.status === '已完成'" type="primary" plain>查看详情</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { mockOrders } from '@/api/mockData'

const activeTab = ref('all')
const orders = ref(mockOrders)

const filteredOrders = computed(() => {
  if (activeTab.value === 'all') return orders.value
  const statusMap = {
    'unpaid': '待支付',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return orders.value.filter(o => o.status === statusMap[activeTab.value])
})

const getStatusType = (status) => {
  const map = {
    '待支付': 'warning',
    '已完成': 'success',
    '已取消': 'info'
  }
  return map[status] || 'info'
}
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
