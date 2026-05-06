<template>
  <div class="admin-orders">
    <div class="page-header">
      <h2>订单管理</h2>
      <div class="search-bar">
        <el-input
          v-model="searchOrderId"
          placeholder="请输入订单号"
          class="search-input"
          @keyup.enter="loadOrders"
        >
          <template #append>
            <el-button @click="loadOrders">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
        <el-select v-model="statusFilter" placeholder="状态筛选" @change="loadOrders">
          <el-option label="全部" value="all" />
          <el-option label="待支付" value="pending" />
          <el-option label="已完成" value="completed" />
          <el-option label="已取消" value="cancelled" />
          <el-option label="退票中" value="refunding" />
          <el-option label="已退票" value="refunded" />
        </el-select>
      </div>
    </div>
    
    <el-table :data="orders" border v-loading="loading">
      <el-table-column prop="orderId" label="订单号" width="180" />
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
        <template #default="{ row }">
          <el-button type="primary" link @click="viewDetail(row.orderId)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="订单详情" v-model="showDetail" width="500px">
      <div v-if="orderDetail" class="order-detail">
        <div class="detail-row">
          <span class="label">订单号：</span>
          <span>{{ orderDetail.orderId }}</span>
        </div>
        <div class="detail-row">
          <span class="label">下单时间：</span>
          <span>{{ orderDetail.createTime }}</span>
        </div>
        <div class="detail-row">
          <span class="label">用户ID：</span>
          <span>{{ orderDetail.userId }}</span>
        </div>
        <div class="detail-row">
          <span class="label">影片：</span>
          <span>{{ orderDetail.movieTitle }}</span>
        </div>
        <div class="detail-row">
          <span class="label">影院：</span>
          <span>{{ orderDetail.cinemaName }}</span>
        </div>
        <div class="detail-row">
          <span class="label">影厅：</span>
          <span>{{ orderDetail.hallName }}</span>
        </div>
        <div class="detail-row">
          <span class="label">放映时间：</span>
          <span>{{ orderDetail.showTime }}</span>
        </div>
        <div class="detail-row">
          <span class="label">座位：</span>
          <span>{{ formatSeats(orderDetail.seats) }}</span>
        </div>
        <div class="detail-row">
          <span class="label">金额：</span>
          <span class="price">¥{{ orderDetail.totalPrice }}</span>
        </div>
        <div class="detail-row">
          <span class="label">状态：</span>
          <el-tag :type="getStatusType(orderDetail.status)">{{ orderDetail.status }}</el-tag>
        </div>
        <div class="detail-row">
          <span class="label">取票码：</span>
          <span class="ticket-code">{{ orderDetail.ticketCode }}</span>
        </div>
        <div v-if="orderDetail.status === '退票中'" class="refund-section">
          <div class="detail-row">
            <span class="label">退票原因：</span>
            <span>{{ getRefundReasonText(orderDetail.refundReason) }}</span>
          </div>
          <div class="refund-actions">
            <el-button type="success" @click="approveRefund(orderDetail.orderId)">批准退票</el-button>
            <el-button type="danger" @click="rejectRefund(orderDetail.orderId)">拒绝退票</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { orderAPI } from '@/api/api'

const orders = ref([])
const loading = ref(true)
const showDetail = ref(false)
const orderDetail = ref(null)
const searchOrderId = ref('')
const statusFilter = ref('all')

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

const getRefundReasonText = (reason) => {
  if (!reason) return ''
  const map = {
    'personal': '个人原因，无法按时观影',
    'schedule_change': '场次时间变更',
    'service_issue': '影院服务问题',
    'other': '其他原因'
  }
  const prefix = reason.split(':')[0]
  return map[prefix] || reason
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
    const params = {
      status: statusFilter.value
    }
    if (searchOrderId.value.trim()) {
      params.orderId = searchOrderId.value.trim()
    }
    const response = await orderAPI.getAllOrders(params)
    if (response.data.code === 200) {
      orders.value = response.data.data.list
    }
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

const viewDetail = async (orderId) => {
  try {
    const response = await orderAPI.getOrderDetailForAdmin(orderId)
    if (response.data.code === 200) {
      orderDetail.value = response.data.data
      showDetail.value = true
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
  }
}

const cancelOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '取消订单', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await orderAPI.adminCancelOrder(orderId)
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

const approveRefund = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要批准该退票申请吗？款项将原路退回。', '批准退票', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })

    const response = await orderAPI.approveRefund(orderId, 'approve')
    if (response.data.code === 200) {
      ElMessage.success('退票已批准')
      showDetail.value = false
      loadOrders()
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批准退票失败:', error)
      ElMessage.error('批准退票失败')
    }
  }
}

const rejectRefund = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要拒绝该退票申请吗？订单将恢复正常状态。', '拒绝退票', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await orderAPI.approveRefund(orderId, 'reject')
    if (response.data.code === 200) {
      ElMessage.success('退票已拒绝，订单恢复正常')
      showDetail.value = false
      loadOrders()
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('拒绝退票失败:', error)
      ElMessage.error('拒绝退票失败')
    }
  }
}

onMounted(() => {
  loadOrders()
})
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

.search-bar {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-input {
  width: 250px;
}

.el-select {
  width: 140px;
}

.order-detail {
  padding: 10px 0;
}

.detail-row {
  display: flex;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.detail-row:last-child {
  border-bottom: none;
}

.label {
  width: 100px;
  color: #666;
  flex-shrink: 0;
}

.price {
  color: #ff6b6b;
  font-weight: bold;
  font-size: 16px;
}

.ticket-code {
  font-family: 'Courier New', monospace;
  font-weight: bold;
  color: #667eea;
}

.refund-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px dashed #ddd;
}

.refund-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.refund-actions .el-button {
  flex: 1;
}
</style>
