<template>
  <div class="admin-cinemas">
    <div class="page-header">
      <h2>影院管理</h2>
      <el-button type="primary" @click="handleAddCinema">添加影院</el-button>
    </div>
    
    <el-table :data="cinemas" border>
      <el-table-column prop="cinemaId" label="ID" width="80" />
      <el-table-column prop="cinemaName" label="影院名称" />
      <el-table-column prop="address" label="地址" />
      <el-table-column prop="phone" label="电话" width="140" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'warning'">{{ row.status === 1 ? '营业中' : '停业' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="rating" label="评分" width="100" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEditCinema(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDeleteCinema(row.cinemaId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 添加/编辑影院弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form
        :model="cinemaForm"
        label-width="100px"
        :rules="rules"
        ref="cinemaFormRef"
      >
        <el-form-item label="影院名称" prop="cinemaName">
          <el-input v-model="cinemaForm.cinemaName" placeholder="请输入影院名称" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="cinemaForm.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="cinemaForm.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="cinemaForm.status" placeholder="请选择状态">
            <el-option label="营业中" :value="1" />
            <el-option label="停业" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="评分" prop="rating">
          <el-input v-model.number="cinemaForm.rating" type="number" step="0.1" placeholder="请输入评分" />
        </el-form-item>
        <el-form-item label="是否热门">
          <el-switch v-model="cinemaForm.isHot" />
        </el-form-item>
        <el-form-item label="简介" prop="description">
          <el-input
            v-model="cinemaForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入影院简介"
          />
        </el-form-item>
        <el-form-item label="Logo" prop="logo">
          <el-input v-model="cinemaForm.logo" placeholder="请输入Logo URL" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cinemaAPI } from '@/api/api'

const cinemas = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加影院')
const cinemaFormRef = ref(null)

const cinemaForm = ref({
  cinemaName: '',
  address: '',
  phone: '',
  status: 1,
  rating: 0,
  isHot: false,
  description: '',
  logo: ''
})

const rules = {
  cinemaName: [
    { required: true, message: '请输入影院名称', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入电话', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

const loadCinemas = async () => {
  try {
    const response = await cinemaAPI.getCinemas({})
    cinemas.value = response.data.data.content
  } catch (error) {
    ElMessage.error('加载影院失败')
    console.error('加载影院失败:', error)
  }
}

const handleAddCinema = () => {
  dialogTitle.value = '添加影院'
  cinemaForm.value = {
    cinemaName: '',
    address: '',
    phone: '',
    status: 1,
    rating: 0,
    isHot: false,
    description: '',
    logo: ''
  }
  dialogVisible.value = true
}

const handleEditCinema = (row) => {
  dialogTitle.value = '编辑影院'
  cinemaForm.value = { ...row }
  dialogVisible.value = true
}

const handleDeleteCinema = (id) => {
  ElMessageBox.confirm('确定要删除该影院吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await cinemaAPI.deleteCinema(id)
      ElMessage.success('删除成功')
      loadCinemas()
    } catch (error) {
      ElMessage.error('删除失败')
      console.error('删除影院失败:', error)
    }
  })
}

const handleSubmit = async () => {
  if (!cinemaFormRef.value) return
  
  try {
    await cinemaFormRef.value.validate()
    
    if (cinemaForm.value.cinemaId) {
      await cinemaAPI.updateCinema(cinemaForm.value.cinemaId, cinemaForm.value)
      ElMessage.success('编辑成功')
    } else {
      await cinemaAPI.addCinema(cinemaForm.value)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    loadCinemas()
  } catch (error) {
    if (error.name === 'Error') {
      ElMessage.error('操作失败: ' + error.message)
    }
    console.error('操作影院失败:', error)
  }
}

onMounted(() => {
  loadCinemas()
})
</script>

<style scoped>
.admin-cinemas {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>