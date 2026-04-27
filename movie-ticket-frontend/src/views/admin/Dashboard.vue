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
              <div v-for="i in 7" :key="i" class="bar" :style="{ height: Math.random() * 200 + 50 + 'px' }"></div>
            </div>
            <div class="chart-labels">
              <span v-for="day in ['周一', '周二', '周三', '周四', '周五', '周六', '周日']" :key="day">{{ day }}</span>
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
        <span>最近订单</span>
      </template>
      <el-table :data="recentOrders" style="width: 100%">
        <el-table-column prop="id" label="订单号" width="180" />
        <el-table-column prop="movieTitle" label="影片" />
        <el-table-column prop="cinemaName" label="影院" />
        <el-table-column prop="totalPrice" label="金额" width="100">
          <template #default="{ row }">
            ¥{{ row.totalPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { mockOrders } from '@/api/mockData'

const stats = ref([
  { title: '今日订单', value: '156', icon: 'Document', color: '#409EFF' },
  { title: '今日营收', value: '¥12,580', icon: 'Money', color: '#67C23A' },
  { title: '注册用户', value: '3,256', icon: 'User', color: '#E6A23C' },
  { title: '影片数量', value: '48', icon: 'VideoCamera', color: '#F56C6C' }
])

const pieData = ref([
  { label: '科幻', value: 30, color: '#409EFF' },
  { label: '喜剧', value: 25, color: '#67C23A' },
  { label: '动作', value: 20, color: '#E6A23C' },
  { label: '爱情', value: 15, color: '#F56C6C' },
  { label: '其他', value: 10, color: '#909399' }
])

const recentOrders = ref(mockOrders)

const getStatusType = (status) => {
  const map = { '待支付': 'warning', '已完成': 'success', '已取消': 'info' }
  return map[status] || 'info'
}
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
</style>
