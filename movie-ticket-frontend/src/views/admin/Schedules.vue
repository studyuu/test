<template>
  <div class="admin-schedules">
    <div class="page-header">
      <h2>排期管理</h2>
      <div class="header-actions">
        <el-select v-model="status" placeholder="请选择状态筛选" style="width: 150px;margin-right: 10px;">
          <el-option label="全部" value="all" />
          <el-option label="可用" value="1" />
          <el-option label="不可用" value="0" />
        </el-select>
        <el-input
          v-model="searchKeyword"
          placeholder="请输入影片名称筛选"
          clearable
          style="width: 200px; margin-right: 10px;"
        />
        <el-button type="primary" @click="handleAdd">添加排期</el-button>
      </div>
    </div>

    <el-table :data="filteredSchedules" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="movieId" label="影片ID" width="100" />
      <el-table-column prop="movieName" label="影片名称" />
      <el-table-column prop="cinemaName" label="影院名称" />
      <el-table-column prop="hallName" label="影厅" />
      <el-table-column prop="startTime" label="开始时间">
        <template #default="{ row }">
          {{ formatTime(row.startTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="endTime" label="结束时间">
        <template #default="{ row }">
          {{ formatTime(row.endTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="price" label="票价" width="100">
        <template #default="{ row }">
          ¥{{ row.price }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="isScheduleExpired(row) ? 'info' : (row.status === 1 ? 'success' : 'info')">{{ isScheduleExpired(row) ? '不可用' : (row.status === 1 ? '可用' : '不可用') }}</el-tag>
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

    <el-dialog :title="editMode ? '更新排期' : '添加排期'" v-model="dialogVisible" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="影片" prop="movieId" required>
          <el-select v-model="formData.movieId" placeholder="请选择影片">
            <el-option
              v-for="movie in movies"
              :key="movie.movieId"
              :label="movie.title"
              :value="movie.movieId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="影院" prop="cinemaId" required>
          <el-select v-model="formData.cinemaId" placeholder="请选择影院">
            <el-option
              v-for="cinema in cinemas"
              :key="cinema.cinemaId"
              :label="cinema.cinemaName"
              :value="cinema.cinemaId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="影厅" prop="hallName" required>
          <el-input v-model="formData.hallName" placeholder="请输入影厅名称" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime" required>
          <el-input v-model="formData.startTime" type="datetime-local" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime" required>
          <el-input v-model="formData.endTime" type="datetime-local" />
        </el-form-item>
        <el-form-item label="票价" prop="price" required>
          <el-input v-model.number="formData.price" type="number" placeholder="请输入票价" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status">
            <el-option label="可用" :value="1" />
            <el-option label="不可用" :value="0" />
          </el-select>
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
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { scheduleAPI, movieAPI, cinemaAPI } from '@/api/api'
import Pagination from '@/components/Pagination.vue'

const schedules = ref([])
const movies = ref([])
const cinemas = ref([])
const searchKeyword = ref('')
const status = ref('all')
const dialogVisible = ref(false)
const pagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})
const formData = ref({
  movieId: '',
  cinemaId: '',
  hallName: '',
  startTime: '',
  endTime: '',
  price: '',
  status: 1
})
const editMode = ref(false)
const editScheduleId = ref(null)

// 判断排期是否已过期
const isScheduleExpired = (schedule) => {
  if (!schedule.endTime) return false
  const [datePart, timePart] = schedule.endTime.split(' ')
  const [year, month, day] = datePart.split('-')
  const [hour, minute, second] = timePart.split(':')
  const endTime = new Date(year, month - 1, day, hour, minute, second)
  const now = new Date()
  return endTime < now
}

// 计算属性：根据影片名称和状态过滤排期
const filteredSchedules = computed(() => {
  let result = schedules.value
  
  // 根据影片名称过滤
  if (searchKeyword.value) {
    result = result.filter(schedule => 
      schedule.movieName && schedule.movieName.includes(searchKeyword.value)
    )
  }
  
  // 根据状态过滤
  if (status.value !== 'all') {
    result = result.filter(schedule => {
      const isExpired = isScheduleExpired(schedule)
      const scheduleStatus = isExpired ? 0 : schedule.status
      return scheduleStatus === parseInt(status.value)
    })
  }
  
  return result
})

const loadSchedules = async () => {
  try {
    const response = await scheduleAPI.getSchedules({
      pageNum: pagination.value.pageNum,
      pageSize: pagination.value.pageSize
    })
    if (response.data.code === 200) {
      const data = response.data.data
      schedules.value = data.content || data.list || data
      pagination.value.total = data.total || (data.content || data.list || data).length
    }
  } catch (error) {
    console.error('加载排期列表失败:', error)
  }
}

const handlePageChange = ({ pageNum, pageSize }) => {
  pagination.value.pageNum = pageNum
  pagination.value.pageSize = pageSize
  loadSchedules()
}

const loadMovies = async () => {
  try {
    const response = await movieAPI.getMovies({ status: 1 })
    if (response.data.code === 200) {
      movies.value = response.data.data.content || response.data.data.list || response.data.data
    }
  } catch (error) {
    console.error('加载影片列表失败:', error)
  }
}

const loadCinemas = async () => {
  try {
    const response = await cinemaAPI.getCinemas()
    if (response.data.code === 200) {
      cinemas.value = response.data.data.content || response.data.data.list || response.data.data
    }
  } catch (error) {
    console.error('加载影院列表失败:', error)
  }
}

const handleAdd = () => {
  editMode.value = false
  editScheduleId.value = null
  formData.value = {
    movieId: '',
    cinemaId: '',
    hallName: '',
    startTime: '',
    endTime: '',
    price: '',
    status: 1
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  editMode.value = true
  editScheduleId.value = row.id
  formData.value = {
    movieId: row.movieId,
    cinemaId: row.cinemaId,
    hallName: row.hallName,
    startTime: formatDateTime(row.startTime),
    endTime: formatDateTime(row.endTime),
    price: row.price,
    status: row.status
  }
  dialogVisible.value = true
}

const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  // 直接从字符串转换，避免时区问题
  // 格式是 "YYYY-MM-DD HH:mm:ss"，需要转成 "YYYY-MM-DDTHH:mm"
  return dateTimeStr.replace(' ', 'T').slice(0, 16)
}

const formatTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  // 格式是 "YYYY-MM-DD HH:mm:ss"，转成 "YYYY-MM-DD HH:mm"
  return dateTimeStr.slice(0, 16)
}

const handleSubmit = async () => {
  if (!formData.value.movieId || !formData.value.cinemaId || !formData.value.hallName || !formData.value.startTime || !formData.value.endTime || !formData.value.price) {
    ElMessage.error('请填写完整信息')
    return
  }

  const submitData = {
    movieId: formData.value.movieId,
    cinemaId: formData.value.cinemaId,
    hallName: formData.value.hallName,
    startTime: formData.value.startTime.replace('T', ' ') + ':00',
    endTime: formData.value.endTime.replace('T', ' ') + ':00',
    price: formData.value.price,
    status: formData.value.status
  }

  try {
    let response
    if (editMode.value) {
      response = await scheduleAPI.updateSchedule(editScheduleId.value, submitData)
    } else {
      response = await scheduleAPI.addSchedule(submitData)
    }

    if (response.data.code === 200) {
      ElMessage.success(editMode.value ? '更新成功' : '添加成功')
      dialogVisible.value = false
      loadSchedules()
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该排期吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await scheduleAPI.deleteSchedule(row.id)
      if (response.data.code === 200) {
        ElMessage.success('删除成功')
        loadSchedules()
      } else {
        ElMessage.error(response.data.message)
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

onMounted(() => {
  loadSchedules()
  loadMovies()
  loadCinemas()
})
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

.header-actions {
  display: flex;
  align-items: center;
}
</style>