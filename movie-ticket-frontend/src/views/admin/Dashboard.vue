<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <div class="stat-cards">
      <el-card class="stat-card" v-for="stat in stats" :key="stat.title">
        <div class="stat-content">
          <div class="stat-icon" :style="{ background: stat.color }">
            <el-icon size="32" color="#fff"><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-title">{{ stat.title }}</p>
            <p class="stat-value">{{ stat.value }}</p>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>订单趋势</span>
          </template>
          <div class="chart-placeholder">
            <div class="mock-chart">
              <div 
                v-for="(item, index) in orderTrend" 
                :key="index" 
                class="bar" 
                :style="{ height: (item.count / getMaxOrderCount() * 200 + 50) + 'px' }"
              ></div>
            </div>
            <div class="chart-labels">
              <span v-for="(item, index) in orderTrend" :key="index">{{ item.day }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>影片类型分布</span>
          </template>
          <div class="pie-chart">
            <div class="pie-item" v-for="(item, index) in pieData" :key="index">
              <span class="pie-color" :style="{ background: item.color }"></span>
              <span class="pie-label">{{ item.label }}</span>
              <span class="pie-value">{{ item.value }}%</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近订单 -->
    <el-card class="recent-orders">
      <template #header>
        <div class="card-header">
          <span class="card-title">最近订单</span>
          <el-input 
            v-model="searchOrderId" 
            placeholder="请输入订单号搜索" 
            clearable 
            style="width: 300px;"
            @keyup.enter="handleSearch"
          />
        </div>
      </template>
      <div class="table-container">
        <el-table 
          :data="recentOrders" 
          :max-height="400"
          stripe
          border
          highlight-current-row
          fit
        >
          <el-table-column 
            prop="orderId" 
            label="订单号" 
            align="center"
            min-width="180"
          >
            <template #default="{ row }">
              <span class="order-id">{{ row.orderId }}</span>
            </template>
          </el-table-column>
          <el-table-column 
            prop="seats" 
            label="座位" 
            align="center"
            min-width="120"
          >
            <template #default="{ row }">
              <span class="seat-info">{{ formatSeats(row.seats) }}</span>
            </template>
          </el-table-column>
          <el-table-column 
            prop="totalPrice" 
            label="金额" 
            align="center"
            min-width="100"
          >
            <template #default="{ row }">
              <span class="price">¥{{ row.totalPrice }}</span>
            </template>
          </el-table-column>
          <el-table-column 
            prop="status" 
            label="状态" 
            align="center"
            min-width="100"
          >
            <template #default="{ row }">
              <el-tag 
                :type="getStatusType(row.status)" 
                class="status-tag"
              >
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column 
            prop="createTime" 
            label="创建时间" 
            align="center"
            min-width="180"
          />
          <el-table-column 
            label="操作" 
            align="center"
            min-width="120"
          >
            <template #default="{ row }">
              <el-button 
                type="primary" 
                size="small" 
                @click="showOrderDetail(row)"
              >
                详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-container">
          <Pagination
            :current-page="pagination.pageNum"
            :page-size="pagination.pageSize"
            :total="pagination.total"
            @change="handlePageChange"
            style="padding: 20px 0 0 0;"
          />
        </div>
      </div>
    </el-card>
  </div>

  <!-- 订单详情弹窗 -->
  <el-dialog 
    title="订单详情" 
    v-model="detailDialogVisible" 
    width="550px"
    :close-on-click-modal="false"
  >
    <div v-if="detailLoading" class="loading-container">
      <el-loading-spinner />
      <span>加载中...</span>
    </div>
    <div v-else-if="selectedOrder" class="order-detail">
      <el-form :model="selectedOrder" label-width="100px">
        <el-form-item label="订单号">
          <span class="detail-value order-id">{{ selectedOrder.orderId }}</span>
        </el-form-item>
        <el-form-item label="影片名称">
          <span class="detail-value">{{ selectedOrder.movieTitle || '-' }}</span>
        </el-form-item>
        <el-form-item label="影院名称">
          <span class="detail-value">{{ selectedOrder.cinemaName || '-' }}</span>
        </el-form-item>
        <el-form-item label="影厅">
          <span class="detail-value">{{ selectedOrder.hallName || '-' }}</span>
        </el-form-item>
        <el-form-item label="放映时间">
          <span class="detail-value">{{ selectedOrder.showTime || '-' }}</span>
        </el-form-item>
        <el-form-item label="座位">
          <span class="detail-value">{{ formatSeats(selectedOrder.seats) }}</span>
        </el-form-item>
        <el-form-item label="金额">
          <span class="detail-value price">¥{{ selectedOrder.totalPrice }}</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-tag :type="getStatusType(selectedOrder.status)">
            {{ getStatusText(selectedOrder.status) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="下单用户">
          <span class="detail-value">{{ selectedOrder.userName || '-' }}</span>
        </el-form-item>
        <el-form-item label="用户手机">
          <span class="detail-value">{{ selectedOrder.userPhone || '-' }}</span>
        </el-form-item>
        <el-form-item label="下单时间">
          <span class="detail-value">{{ selectedOrder.createTime }}</span>
        </el-form-item>
      </el-form>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Document, Money, User, VideoCamera } from '@element-plus/icons-vue'
import { dashboardAPI } from '@/api/api'
import Pagination from '@/components/Pagination.vue'

const stats = ref([
  { title: '今日订单', value: '0', icon: Document, color: '#409EFF' },
  { title: '今日营收', value: '¥0', icon: Money, color: '#67C23A' },
  { title: '注册用户', value: '0', icon: User, color: '#E6A23C' },
  { title: '影片数量', value: '0', icon: VideoCamera, color: '#F56C6C' }
])

const orderTrend = ref([])
const pieData = ref([])
const recentOrders = ref([])
const searchOrderId = ref('')
const allOrders = ref([])

const getStatusType = (status) => {
  const map = { 
    '待支付': 'danger', 
    '已完成': 'success', 
    '已取消': 'info', 
    '退票中': 'warning', 
    '已退票': 'info',
    'pending': 'danger',
    'completed': 'success',
    'cancelled': 'info',
    'refunding': 'warning',
    'refunded': 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'pending': '待支付',
    'completed': '已完成',
    'cancelled': '已取消',
    'refunding': '退票中',
    'refunded': '已退票'
  }
  return map[status] || status
}

const formatSeats = (seats) => {
  if (!seats) return '-'
  try {
    const seatArray = JSON.parse(seats)
    if (Array.isArray(seatArray)) {
      return seatArray.map(s => `${s.rowLabel}${s.col}`).join(', ')
    }
    return seats.substring(0, 30) + (seats.length > 30 ? '...' : '')
  } catch (e) {
    return seats.substring(0, 30) + (seats.length > 30 ? '...' : '')
  }
}

const pagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const detailDialogVisible = ref(false)
const selectedOrder = ref(null)
const detailLoading = ref(false)

const showOrderDetail = async (row) => {
  detailLoading.value = true
  detailDialogVisible.value = true
  
  try {
    const response = await dashboardAPI.getOrderDetail(row.orderId)
    if (response.data.code === 200) {
      selectedOrder.value = response.data.data
    } else {
      selectedOrder.value = row
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    selectedOrder.value = row
  } finally {
    detailLoading.value = false
  }
}

const handleSizeChange = (val) => {
  pagination.value.pageSize = val
  loadRecentOrders()
}

const handleCurrentChange = (val) => {
  pagination.value.pageNum = val
  loadRecentOrders()
}

const handlePageChange = ({ pageNum, pageSize }) => {
  pagination.value.pageNum = pageNum
  pagination.value.pageSize = pageSize
  loadRecentOrders()
}

const loadStats = async () => {
  try {
    const response = await dashboardAPI.getStats()
    if (response.data.code === 200) {
      const data = response.data.data
      stats.value = [
        { title: '今日订单', value: data.todayOrders.toString(), icon: Document, color: '#409EFF' },
        { title: '今日营收', value: '¥' + data.todayRevenue.toLocaleString(), icon: Money, color: '#67C23A' },
        { title: '注册用户', value: data.totalUsers.toLocaleString(), icon: User, color: '#E6A23C' },
        { title: '影片数量', value: data.totalMovies.toString(), icon: VideoCamera, color: '#F56C6C' }
      ]
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadOrderTrend = async () => {
  try {
    const response = await dashboardAPI.getOrderTrend()
    if (response.data.code === 200) {
      orderTrend.value = response.data.data
    }
  } catch (error) {
    console.error('加载订单趋势失败:', error)
    orderTrend.value = [
      { day: '周一', count: 120 },
      { day: '周二', count: 150 },
      { day: '周三', count: 130 },
      { day: '周四', count: 180 },
      { day: '周五', count: 160 },
      { day: '周六', count: 140 },
      { day: '周日', count: 156 }
    ]
  }
}

const loadMovieTypeDistribution = async () => {
  try {
    const response = await dashboardAPI.getMovieTypeDistribution()
    if (response.data.code === 200) {
      const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#FFC107', '#722ED1']
      pieData.value = response.data.data.map((item, index) => ({
        label: item.type,
        value: item.percentage,
        color: colors[index % colors.length]
      }))
    }
  } catch (error) {
    console.error('加载影片类型分布失败:', error)
    pieData.value = [
      { label: '科幻', value: 30, color: '#409EFF' },
      { label: '喜剧', value: 25, color: '#67C23A' },
      { label: '动作', value: 20, color: '#E6A23C' },
      { label: '爱情', value: 15, color: '#F56C6C' },
      { label: '其他', value: 10, color: '#909399' }
    ]
  }
}

const loadRecentOrders = async () => {
  try {
    const response = await dashboardAPI.getRecentOrders({
      pageNum: pagination.value.pageNum,
      pageSize: pagination.value.pageSize
    })
    if (response.data.code === 200) {
      allOrders.value = response.data.data.list
      recentOrders.value = allOrders.value
      pagination.value.total = response.data.data.total
    }
  } catch (error) {
    console.error('加载最近订单失败:', error)
    recentOrders.value = [
      { id: 'ORD202403240001', movieTitle: '流浪地球3', cinemaName: '万达影城（CBD店）', totalPrice: 130, status: '已完成' },
      { id: 'ORD202403240002', movieTitle: '热辣滚烫', cinemaName: 'CGV影城（颐堤港店）', totalPrice: 96, status: '待支付' }
    ]
    pagination.value.total = 2
  }
}

const getMaxOrderCount = () => {
  if (orderTrend.value.length === 0) return 200
  return Math.max(...orderTrend.value.map(item => item.count)) + 50
}

const handleSearch = () => {
  if (!searchOrderId.value) {
    recentOrders.value = allOrders.value
    return
  }
  recentOrders.value = allOrders.value.filter(order => 
    order.orderId === searchOrderId.value
  )
}

onMounted(() => {
  loadStats()
  loadOrderTrend()
  loadMovieTypeDistribution()
  loadRecentOrders()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card :deep(.el-card__body) {
  padding: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

.mock-chart {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 250px;
  padding: 0 20px;
}

.bar {
  width: 40px;
  background: linear-gradient(to top, #409EFF, #67C23A);
  border-radius: 4px 4px 0 0;
  transition: all 0.3s;
}

.chart-labels {
  display: flex;
  justify-content: space-around;
  padding-top: 10px;
  color: #909399;
  font-size: 12px;
}

.pie-chart {
  padding: 20px;
}

.pie-item {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.pie-color {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  margin-right: 12px;
}

.pie-label {
  flex: 1;
  color: #606266;
}

.pie-value {
  font-weight: bold;
  color: #303133;
}

.recent-orders {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.order-id {
  font-family: 'Courier New', monospace;
  font-size: 13px;
  color: #409EFF;
  font-weight: 500;
}

.seat-info {
  font-size: 13px;
  color: #606266;
  word-break: break-all;
}

.price {
  font-size: 14px;
  font-weight: bold;
  color: #F56C6C;
}

.status-tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}

:deep(.el-table) {
  font-size: 13px;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
  text-align: center;
}

:deep(.el-table td) {
  padding: 12px 0;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background-color: #fafafa;
}

:deep(.el-table__row:hover td) {
  background-color: #e8f4fd;
}

.table-container {
  display: flex;
  flex-direction: column;
  min-height: 400px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  border-top: 1px solid #ebeef5;
}

:deep(.el-pagination) {
  margin: 0;
}

.order-detail {
  padding: 10px 0;
}

.detail-value {
  font-size: 14px;
  color: #303133;
}

.detail-value.price {
  font-size: 16px;
  font-weight: bold;
  color: #F56C6C;
}

:deep(.el-dialog__header) {
  background-color: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-dialog__title) {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.loading-container span {
  margin-top: 12px;
  color: #909399;
  font-size: 14px;
}

.detail-value.order-id {
  color: #409EFF;
  font-family: 'Courier New', monospace;
  font-weight: 500;
}
</style>
