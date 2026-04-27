<template>
  <div class="admin-schedules">
    <div class="page-header">
      <h2>排期管理</h2>
      <el-button type="primary">添加排期</el-button>
    </div>
    
    <el-table :data="schedules" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="movieId" label="影片ID" width="100" />
      <el-table-column prop="hallName" label="影厅" />
      <el-table-column prop="startTime" label="开始时间" />
      <el-table-column prop="endTime" label="结束时间" />
      <el-table-column prop="price" label="票价" width="100">
        <template #default="{ row }">
          ¥{{ row.price }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default>
          <el-button type="primary" link>编辑</el-button>
          <el-button type="danger" link>删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { mockSchedules } from '@/api/mockData'

const schedules = ref(mockSchedules)
</script>

<style scoped>
.admin-schedules {
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
