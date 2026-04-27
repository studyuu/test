<template>
  <div class="admin-orders">
    <div class="page-header">
      <h2>订单管理</h2>
    </div>
    
    <el-table :data="orders" border>
      <el-table-column prop="id" label="订单号" width="180" />
      <el-table-column prop="movieTitle" label="影片" />
      <el-table-column prop="cinemaName" label="影院" />
      <el-table-column prop="showTime" label="场次" />
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
      <el-table-column label="操作" width="150">
        <template #default>
          <el-button type="primary" link>查看</el-button>
          <el-button type="danger" link>取消</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { mockOrders } from '@/api/mockData'

const orders = ref(mockOrders)

const getStatusType = (status) => {
  const map = { '待支付': 'warning', '已完成': 'success', '已取消': 'info' }
  return map[status] || 'info'
}
</script>

<style scoped>
.admin-orders {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}
</style>
