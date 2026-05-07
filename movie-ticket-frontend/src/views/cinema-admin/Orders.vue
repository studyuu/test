<template>
  <div class="cinema-orders">
    <div class="page-header">
      <h2>订单查询</h2>
      <div class="header-actions">
        <el-select v-model="status" placeholder="订单状态" style="width: 120px; margin-right: 10px;">
          <el-option label="全部" value="all" />
          <el-option label="待支付" value="1" />
          <el-option label="已支付" value="2" />
          <el-option label="已完成" value="3" />
          <el-option label="已取消" value="4" />
          <el-option label="退票中" value="refunding" />
          <el-option label="已退票" value="refunded" />
        </el-select>
        <el-input
          v-model="searchKeyword"
          placeholder="订单号/用户名"
          clearable
          style="width: 180px; margin-right: 10px;"
        />
        <el-button type="primary" @click="handleVerify">验票</el-button>
      </div>
    </div>

    <el-table :data="filteredOrders" border style="width: 100%">
      <el-table-column label="序号" width="80">
        <template #default="{ $index }">
          {{ (pagination.pageNum - 1) * pagination.pageSize + $index + 1 }}
        </template>
      </el-table-column>
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="movieName" label="影片" />
      <el-table-column prop="hallName" label="影厅" width="100" />
      <el-table-column prop="showTime" label="放映时间" width="160">
        <template #default="{ row }">
          {{ formatTime(row.showTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="seats" label="座位" width="120">
        <template #default="{ row }">
          {{ formatSeats(row.seats) }}
        </template>
      </el-table-column>
      <el-table-column prop="totalAmount" label="金额" width="100">
        <template #default="{ row }">
          <span style="color: #F56C6C; font-weight: bold;">¥{{ row.totalAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="userName" label="用户" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" width="180">
        <template #default="{ row }">
          {{ formatTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
          <el-button v-if="row.status === '退票中' || row.status === 'refunding'" type="danger" link @click="handleRefund(row)">处理退票</el-button>
        </template>
      </el-table-column>
    </el-table>

    <Pagination
      v-if="pagination.total > 0"
      :current-page="pagination.pageNum"
      :page-size="pagination.pageSize"
      :total="pagination.total"
      @change="handlePageChange"
    />

    <el-dialog title="订单详情" v-model="detailVisible" width="600px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="影片">{{ currentOrder.movieName }}</el-descriptions-item>
        <el-descriptions-item label="影厅">{{ currentOrder.hallName }}</el-descriptions-item>
        <el-descriptions-item label="放映时间">{{ formatTime(currentOrder.showTime) }}</el-descriptions-item>
        <el-descriptions-item label="座位">{{ formatSeats(currentOrder.seats) }}</el-descriptions-item>
        <el-descriptions-item label="总金额">
          <span style="color: #F56C6C; font-weight: bold;">¥{{ currentOrder.totalAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="用户">{{ currentOrder.userName }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ formatTime(currentOrder.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ currentOrder.payType === 1 ? '微信支付' : currentOrder.payType === 2 ? '支付宝' : '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button v-if="currentOrder && currentOrder.status === 2" type="success" @click="handleVerifyTicket">确认验票</el-button>
      </template>
    </el-dialog>

    <el-dialog title="验票" v-model="verifyVisible" width="400px">
      <el-input v-model="verifyCode" placeholder="请输入验票码或扫描二维码" />
      <template #footer>
        <el-button @click="verifyVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmVerify">确认验票</el-button>
      </template>
    </el-dialog>

    <el-dialog title="处理退票申请" v-model="refundVisible" width="500px">
      <el-form :model="refundForm" label-width="100px">
        <el-form-item label="订单号">
          <el-input :value="refundForm.orderNo" disabled />
        </el-form-item>
        <el-form-item label="订单金额">
          <el-input :value="'¥' + refundForm.amount" disabled />
        </el-form-item>
        <el-form-item label="处理结果" required>
          <el-radio-group v-model="refundForm.result">
            <el-radio :label="'approve'">同意退票</el-radio>
            <el-radio :label="'reject'">拒绝退票</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input
            v-model="refundForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入处理备注（拒绝退票时必填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="refundVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRefund">确认处理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import Pagination from '@/components/Pagination.vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const orders = ref([])
const searchKeyword = ref('')
const status = ref('all')
const detailVisible = ref(false)
const verifyVisible = ref(false)
const refundVisible = ref(false)
const currentOrder = ref(null)
const currentRefundOrder = ref(null)
const verifyCode = ref('')
const refundForm = ref({
  orderNo: '',
  amount: 0,
  result: '',
  remark: ''
})
const pagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const formatTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  return dateTimeStr.slice(0, 16)
}

const formatSeats = (seats) => {
  if (!seats) return '-'
  try {
    const seatArray = JSON.parse(seats)
    return seatArray.map(s => `${s.row}排${s.col}座`).join(', ')
  } catch {
    return seats
  }
}

const getStatusType = (status) => {
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

const getStatusText = (status) => {
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

const filteredOrders = computed(() => {
  let result = orders.value
  if (searchKeyword.value) {
    result = result.filter(o =>
      (o.orderNo && o.orderNo.includes(searchKeyword.value)) ||
      (o.userName && o.userName.includes(searchKeyword.value))
    )
  }
  if (status.value !== "all") {
    // 状态映射：数字 -> 中文
    const statusMap = {
      "1": "待支付",
      "2": "已支付",
      "3": "已完成",
      "4": "已取消",
      "refunding": "退票中",
      "refunded": "已退票"
    }
    const targetStatus = statusMap[status.value] || status.value
    result = result.filter(o => o.status === targetStatus)
  }
  return result
})

const loadOrders = async () => {
  try {
    const token = localStorage.getItem('token')
    const response = await axios.get('/api/cinema-admin/orders', {
      headers: { Authorization: `Bearer ${token}` },
      params: {
        userId: userStore.userInfo.id,
        pageNum: pagination.value.pageNum,
        pageSize: pagination.value.pageSize
      }
    })
    if (response.data.code === 200) {
      const data = response.data.data
      orders.value = data.content || data.list || data
      pagination.value.total = data.total || orders.value.length
    }
  } catch (error) {
    console.error('加载订单列表失败:', error)
  }
}

const handlePageChange = ({ pageNum, pageSize }) => {
  pagination.value.pageNum = pageNum
  pagination.value.pageSize = pageSize
  loadOrders()
}

const handleDetail = (row) => {
  currentOrder.value = row
  detailVisible.value = true
}

const handleVerify = () => {
  verifyCode.value = ''
  verifyVisible.value = true
}

const confirmVerify = async () => {
  if (!verifyCode.value) {
    ElMessage.error('请输入验票码')
    return
  }

  try {
    const token = localStorage.getItem('token')
    const response = await axios.post('/api/cinema-admin/orders/verify', {
      verifyCode: verifyCode.value
    }, {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (response.data.code === 200) {
      ElMessage.success('验票成功')
      verifyVisible.value = false
      loadOrders()
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    console.error('验票失败:', error)
    ElMessage.error('验票失败')
  }
}

const handleVerifyTicket = async () => {
  if (!currentOrder.value) return

  ElMessageBox.confirm('确认要为该订单验票吗？验票后订单将标记为已完成。', '验票确认', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const token = localStorage.getItem('token')
      const response = await axios.post('/api/cinema-admin/orders/verify', {
        orderId: currentOrder.value.orderId
      }, {
        headers: { Authorization: `Bearer ${token}` }
      })
      if (response.data.code === 200) {
        ElMessage.success('验票成功')
        detailVisible.value = false
        loadOrders()
      } else {
        ElMessage.error(response.data.message)
      }
    } catch (error) {
      console.error('验票失败:', error)
      ElMessage.error('验票失败')
    }
  })
}

const handleRefund = (row) => {
  currentRefundOrder.value = row
  refundForm.value = {
    orderNo: row.orderNo,
    amount: row.totalAmount,
    result: '',
    remark: ''
  }
  refundVisible.value = true
}

const confirmRefund = async () => {
  if (!refundForm.value.result) {
    ElMessage.error('请选择处理结果')
    return
  }
  if (refundForm.value.result === 'reject' && !refundForm.value.remark) {
    ElMessage.error('拒绝退票请填写备注')
    return
  }

  try {
    const token = localStorage.getItem('token')
    const response = await axios.post('/api/cinema-admin/orders/handle-refund', {
      orderId: currentRefundOrder.value.orderId,
      result: refundForm.value.result,
      remark: refundForm.value.remark
    }, {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (response.data.code === 200) {
      ElMessage.success(refundForm.value.result === 'approve' ? '退票成功' : '已拒绝退票')
      refundVisible.value = false
      loadOrders()
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    console.error('处理退票失败:', error)
    ElMessage.error('处理退票失败')
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.cinema-orders {
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

.header-actions {
  display: flex;
  align-items: center;
}
</style>
