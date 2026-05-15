<template>
  <div class="cinema-schedules">
    <div class="page-header">
      <h2>排期管理</h2>
      <div class="header-actions">
        <el-select v-model="status" placeholder="状态筛选" style="width: 120px; margin-right: 10px;">
          <el-option label="全部" value="all" />
          <el-option label="可用" value="1" />
          <el-option label="不可用" value="0" />
        </el-select>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索影片"
          clearable
          style="width: 180px; margin-right: 10px;"
        />
        <el-button type="primary" @click="handleAdd">添加排期</el-button>
      </div>
    </div>

    <el-table :data="filteredSchedules" border style="width: 100%">
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="movieName" label="影片名称" />
      <el-table-column prop="hallName" label="放映厅" width="120" />
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
      <el-table-column prop="soldSeats" label="已售" width="80" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="isExpired(row) ? 'info' : (row.status === 1 ? 'success' : 'info')">
            {{ isExpired(row) ? '已过期' : (row.status === 1 ? '可用' : '不可用') }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <el-dialog :title="editMode ? '编辑排期' : '添加排期'" v-model="dialogVisible" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="影片" required>
          <el-select v-model="formData.movieId" placeholder="请选择影片" style="width: 100%;">
            <el-option
              v-for="movie in movies"
              :key="movie.movieId"
              :label="movie.title"
              :value="movie.movieId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="放映厅" required>
          <el-select v-model="formData.hallId" placeholder="请选择放映厅" style="width: 100%;">
            <el-option
              v-for="hall in halls"
              :key="hall.hallId"
              :label="hall.hallName"
              :value="hall.hallId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" required>
          <el-date-picker
            v-model="formData.startTime"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="结束时间" required>
          <el-date-picker
            v-model="formData.endTime"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="票价" required>
          <el-input-number v-model="formData.price" :min="0" :precision="2" :step="1" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">可用</el-radio>
            <el-radio :label="0">不可用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
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

const schedules = ref([])
const movies = ref([])
const halls = ref([])
const searchKeyword = ref('')
const status = ref('all')
const dialogVisible = ref(false)
const editMode = ref(false)
const editScheduleId = ref(null)
const pagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})
const formData = ref({
  movieId: '',
  hallId: '',
  startTime: '',
  endTime: '',
  price: 0,
  status: 1
})

const isExpired = (schedule) => {
  if (!schedule.endTime) return false
  return new Date(schedule.endTime) < new Date()
}

const filteredSchedules = computed(() => {
  let result = schedules.value
  if (searchKeyword.value) {
    result = result.filter(s => s.movieName && s.movieName.includes(searchKeyword.value))
  }
  if (status.value !== 'all') {
    result = result.filter(s => !isExpired(s) && s.status === parseInt(status.value))
  }
  return result
})

const formatTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  return dateTimeStr.slice(0, 16)
}

const loadSchedules = async () => {
  try {
    const token = localStorage.getItem('token')
    const response = await axios.get('/api/cinema-admin/schedules', {
      headers: { Authorization: `Bearer ${token}` },
      params: { userId: userStore.userInfo.id, pageNum: pagination.value.pageNum, pageSize: pagination.value.pageSize }
    })
    if (response.data.code === 200) {
      const data = response.data.data
      schedules.value = data.content || data.list || data
      pagination.value.total = data.total || schedules.value.length
    }
  } catch (error) {
    console.error('加载排期列表失败:', error)
  }
}

const loadMovies = async () => {
  try {
    const token = localStorage.getItem('token')
    const response = await axios.get('/api/movies', {
      headers: { Authorization: `Bearer ${token}` },
      params: { status: 1 }
    })
    if (response.data.code === 200) {
      movies.value = response.data.data.content || response.data.data.list || response.data.data
    }
  } catch (error) {
    console.error('加载影片列表失败:', error)
  }
}

const loadHalls = async () => {
  try {
    const token = localStorage.getItem('token')
    const response = await axios.get('/api/cinema-admin/halls', {
      headers: { Authorization: `Bearer ${token}` },
      params: { userId: userStore.userInfo.id, status: 1 }
    })
    if (response.data.code === 200) {
      halls.value = response.data.data.content || response.data.data.list || response.data.data
    }
  } catch (error) {
    console.error('加载放映厅列表失败:', error)
  }
}

const handlePageChange = ({ pageNum, pageSize }) => {
  pagination.value.pageNum = pageNum
  pagination.value.pageSize = pageSize
  loadSchedules()
}

const handleAdd = () => {
  editMode.value = false
  editScheduleId.value = null
  formData.value = { movieId: '', hallId: '', startTime: '', endTime: '', price: 0, status: 1 }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  editMode.value = true
  editScheduleId.value = row.scheduleId
  formData.value = {
    movieId: row.movieId,
    hallId: row.hallId,
    startTime: row.startTime,
    endTime: row.endTime,
    price: row.price,
    status: row.status
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formData.value.movieId || !formData.value.hallId || !formData.value.startTime || !formData.value.endTime) {
    ElMessage.error('请填写完整信息')
    return
  }

  if (new Date(formData.value.startTime) >= new Date(formData.value.endTime)) {
    ElMessage.error('结束时间必须晚于开始时间')
    return
  }

  try {
    const token = localStorage.getItem('token')
    const payload = {
      movieId: Number(formData.value.movieId),
      hallId: Number(formData.value.hallId),
      startTime: formData.value.startTime,
      endTime: formData.value.endTime,
      price: Number(formData.value.price) || 0,
      status: Number(formData.value.status ?? 1),
      cinemaId: userStore.userInfo.cinemaId ?? userStore.userInfo.id
    }

    let response
    if (editMode.value) {
      response = await axios.put(`/api/cinema-admin/schedules/${editScheduleId.value}`, payload, {
        headers: { Authorization: `Bearer ${token}` },
        params: { userId: userStore.userInfo.id }
      })
    } else {
      response = await axios.post('/api/cinema-admin/schedules', payload, {
        headers: { Authorization: `Bearer ${token}` },
        params: { userId: userStore.userInfo.id }
      })
    }

    if (response.data.code === 200) {
      ElMessage.success(editMode.value ? '更新成功' : '添加成功')
      dialogVisible.value = false
      loadSchedules()
    } else {
      ElMessage.error(response.data.message || '操作失败')
    }
  } catch (error) {
    const errMsg = error.response?.data?.message || error.message || ''
    ElMessage.error(errMsg || '操作失败，请稍后重试')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该排期吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const token = localStorage.getItem('token')
      const response = await axios.delete(`/api/cinema-admin/schedules/${row.scheduleId}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      if (response.data.code === 200) {
        ElMessage.success('删除成功')
        loadSchedules()
      } else {
        ElMessage.error(response.data.message)
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

onMounted(() => {
  loadSchedules()
  loadMovies()
  loadHalls()
})
</script>

<style scoped>
.cinema-schedules {
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
