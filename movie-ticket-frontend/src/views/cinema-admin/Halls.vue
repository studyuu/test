<template>
  <div class="cinema-halls">
    <div class="page-header">
      <h2>放映厅管理</h2>
      <el-button type="primary" @click="handleAdd">添加放映厅</el-button>
    </div>

    <el-table :data="halls" border style="width: 100%">
      <el-table-column prop="hallId" label="ID" width="80" />
      <el-table-column prop="hallName" label="厅名" />
      <el-table-column prop="rowCount" label="行数" width="100" />
      <el-table-column prop="colCount" label="列数" width="100" />
      <el-table-column prop="seatCount" label="座位数" width="100">
        <template #default="{ row }">
          {{ row.rowCount * row.colCount }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template #default="{ row }">
          {{ formatTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
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

    <el-dialog :title="editMode ? '编辑放映厅' : '添加放映厅'" v-model="dialogVisible" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="厅名" required>
          <el-input v-model="formData.hallName" placeholder="如：1号厅、IMAX厅" />
        </el-form-item>
        <el-form-item label="行数" required>
          <el-input-number v-model="formData.rowCount" :min="1" :max="20" />
        </el-form-item>
        <el-form-item label="列数" required>
          <el-input-number v-model="formData.colCount" :min="1" :max="30" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
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
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import Pagination from '@/components/Pagination.vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const halls = ref([])
const dialogVisible = ref(false)
const editMode = ref(false)
const editHallId = ref(null)
const pagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})
const formData = ref({
  hallName: '',
  rowCount: 10,
  colCount: 15,
  status: 1
})

const formatTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  return dateTimeStr.slice(0, 19)
}

const loadHalls = async () => {
  try {
    const token = localStorage.getItem('token')
    const response = await axios.get('/api/cinema-admin/halls', {
      headers: { Authorization: `Bearer ${token}` },
      params: {
        userId: userStore.userInfo.id,
        pageNum: pagination.value.pageNum,
        pageSize: pagination.value.pageSize
      }
    })
    if (response.data.code === 200) {
      const data = response.data.data
      halls.value = data.content || data.list || data
      pagination.value.total = data.total || halls.value.length
    }
  } catch (error) {
    console.error('加载放映厅列表失败:', error)
    ElMessage.error('加载数据失败')
  }
}

const handlePageChange = ({ pageNum, pageSize }) => {
  pagination.value.pageNum = pageNum
  pagination.value.pageSize = pageSize
  loadHalls()
}

const handleAdd = () => {
  editMode.value = false
  editHallId.value = null
  formData.value = {
    hallName: '',
    rowCount: 10,
    colCount: 15,
    status: 1
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  editMode.value = true
  editHallId.value = row.hallId
  formData.value = {
    hallName: row.hallName,
    rowCount: row.rowCount,
    colCount: row.colCount,
    status: row.status
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formData.value.hallName) {
    ElMessage.error('请填写厅名')
    return
  }

  try {
    const token = localStorage.getItem('token')
    let response
    if (editMode.value) {
      response = await axios.put(`/api/cinema-admin/halls/${editHallId.value}`, formData.value, {
        headers: { Authorization: `Bearer ${token}` },
        params: { userId: userStore.userInfo.id }
      })
    } else {
      response = await axios.post('/api/cinema-admin/halls', formData.value, {
        headers: { Authorization: `Bearer ${token}` },
        params: { userId: userStore.userInfo.id }
      })
    }

    if (response.data.code === 200) {
      ElMessage.success(editMode.value ? '更新成功' : '添加成功')
      dialogVisible.value = false
      loadHalls()
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该放映厅吗？删除前请确保没有关联的排期。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const token = localStorage.getItem('token')
      const response = await axios.delete(`/api/cinema-admin/halls/${row.hallId}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      if (response.data.code === 200) {
        ElMessage.success('删除成功')
        loadHalls()
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
  loadHalls()
})
</script>

<style scoped>
.cinema-halls {
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
