<template>
  <div class="cinema-dashboard">
    <h2>经营概览</h2>

    <el-row :gutter="20" class="stats-cards">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409EFF;">
              <el-icon size="24"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">今日票房</div>
              <div class="stat-value">¥{{ stats.todayRevenue || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67C23A;">
              <el-icon size="24"><Ticket /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">待使用订单</div>
              <div class="stat-value">{{ stats.pendingOrders || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #E6A23C;">
              <el-icon size="24"><Wallet /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">本月收入</div>
              <div class="stat-value">¥{{ stats.monthRevenue || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #F56C6C;">
              <el-icon size="24"><Star /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">综合评分</div>
              <div class="stat-value">{{ stats.rating || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>近期订单</span>
              <el-button type="primary" link @click="$router.push('/cinema-admin/orders')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentOrders" border>
            <el-table-column prop="orderNo" label="订单号" width="180" />
            <el-table-column prop="movieName" label="影片" />
            <el-table-column prop="hallName" label="影厅" width="100" />
            <el-table-column prop="showTime" label="放映时间" width="160">
              <template #default="{ row }">
                {{ formatTime(row.showTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="totalAmount" label="金额" width="100">
              <template #default="{ row }">
                ¥{{ row.totalAmount }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getOrderStatusType(row.status)">
                  {{ getOrderStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>热门影片 TOP5</span>
            </div>
          </template>
          <div class="movie-rank-list">
            <div v-for="(movie, index) in topMovies" :key="movie.movieId" class="movie-rank-item">
              <span class="rank-num" :class="{ 'top-3': index < 3 }">{{ index + 1 }}</span>
              <span class="movie-name">{{ movie.movieName }}</span>
              <span class="movie-sales">¥{{ movie.sales }}</span>
            </div>
            <el-empty v-if="!topMovies.length" description="暂无数据" :image-size="60" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>今日排期</span>
              <el-button type="primary" link @click="$router.push('/cinema-admin/schedules')">管理排期</el-button>
            </div>
          </template>
          <el-table :data="todaySchedules" border>
            <el-table-column prop="movieName" label="影片" />
            <el-table-column prop="hallName" label="影厅" width="100" />
            <el-table-column prop="startTime" label="开始时间" width="160">
              <template #default="{ row }">
                {{ formatTime(row.startTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="endTime" label="结束时间" width="160">
              <template #default="{ row }">
                {{ formatTime(row.endTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="price" label="票价" width="100">
              <template #default="{ row }">
                ¥{{ row.price }}
              </template>
            </el-table-column>
            <el-table-column prop="seats" label="已售/总座位" width="120">
              <template #default="{ row }">
                {{ row.soldSeats || 0 }} / {{ row.totalSeats || 0 }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'">
                  {{ row.status === 1 ? '可用' : '不可用' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!todaySchedules.length" description="今日暂无排期" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const stats = ref({
  todayRevenue: 0,
  pendingOrders: 0,
  monthRevenue: 0,
  rating: 0
})

const recentOrders = ref([])
const topMovies = ref([])
const todaySchedules = ref([])

const formatTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  return dateTimeStr.slice(0, 16)
}

const getOrderStatusType = (status) => {
  const types = {
    '待支付': 'warning',
    '已完成': 'success',
    '已取消': 'danger',
    '退票中': 'info',
    '已退票': 'info',
    'pending': 'warning',
    'completed': 'success',
    'cancelled': 'danger',
    'refunding': 'info',
    'refunded': 'info',
    1: 'warning',
    2: 'success',
    3: 'info',
    4: 'danger'
  }
  return types[status] || 'info'
}

const getOrderStatusText = (status) => {
  const texts = {
    '待支付': '待支付',
    '已完成': '已完成',
    '已取消': '已取消',
    '退票中': '退票中',
    '已退票': '已退票',
    'pending': '待支付',
    'completed': '已完成',
    'cancelled': '已取消',
    'refunding': '退票中',
    'refunded': '已退票',
    1: '待支付',
    2: '已支付',
    3: '已完成',
    4: '已取消'
  }
  return texts[status] || status || '未知'
}

const loadDashboardData = async () => {
  try {
    const token = localStorage.getItem('token')
    const response = await axios.get('/api/cinema-admin/dashboard', {
      headers: { Authorization: `Bearer ${token}` },
      params: { userId: userStore.userInfo.id }
    })
    if (response.data.code === 200) {
      const data = response.data.data
      stats.value = data.stats || stats.value
      recentOrders.value = data.recentOrders || []
      topMovies.value = data.topMovies || []
      todaySchedules.value = data.todaySchedules || []
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.cinema-dashboard {
  padding: 20px;
}

.cinema-dashboard h2 {
  margin: 0 0 20px 0;
}

.stats-cards {
  margin-bottom: 10px;
}

.stat-card {
  border-radius: 8px;
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
  width: 56px;
  height: 56px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-label {
  color: #909399;
  font-size: 14px;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.movie-rank-list {
  padding: 0;
}

.movie-rank-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.movie-rank-item:last-child {
  border-bottom: none;
}

.rank-num {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #909399;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  margin-right: 12px;
}

.rank-num.top-3 {
  background: #F56C6C;
}

.movie-name {
  flex: 1;
  color: #303133;
}

.movie-sales {
  color: #F56C6C;
  font-weight: bold;
}
</style>
