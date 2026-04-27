<template>
  <div class="admin-movies">
    <div class="page-header">
      <h2>影片管理</h2>
      <el-button type="primary" @click="handleAddMovie">添加影片</el-button>
    </div>
    
    <el-table :data="movies" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="影片名称" />
      <el-table-column prop="category" label="类型" width="120" />
      <el-table-column prop="duration" label="时长" width="100" />
      <el-table-column prop="rating" label="评分" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'info' : 'warning'">{{ row.status === 1 ? '上映中' : row.status === 2 ? '即将上映' : '已下线' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEditMovie(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDeleteMovie(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 添加/编辑影片弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form
        :model="movieForm"
        label-width="100px"
        :rules="rules"
        ref="movieFormRef"
      >
        <el-form-item label="影片名称" prop="title">
          <el-input v-model="movieForm.title" placeholder="请输入影片名称" />
        </el-form-item>
        <el-form-item label="英文名称" prop="englishTitle">
          <el-input v-model="movieForm.englishTitle" placeholder="请输入英文名称" />
        </el-form-item>
        <el-form-item label="类型" prop="category">
          <el-input v-model="movieForm.category" placeholder="请输入类型，如：科幻/冒险" />
        </el-form-item>
        <el-form-item label="时长" prop="duration">
          <el-input v-model.number="movieForm.duration" type="number" placeholder="请输入时长（分钟）" />
        </el-form-item>
        <el-form-item label="评分" prop="rating">
          <el-input v-model.number="movieForm.rating" type="number" step="0.1" placeholder="请输入评分" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="movieForm.status" placeholder="请选择状态">
            <el-option label="上映中" value="1" />
            <el-option label="即将上映" value="2" />
            <el-option label="已下线" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="海报" prop="poster">
          <el-input v-model="movieForm.poster" placeholder="请输入海报URL" />
        </el-form-item>
        <el-form-item label="导演" prop="director">
          <el-input v-model="movieForm.director" placeholder="请输入导演" />
        </el-form-item>
        <el-form-item label="演员" prop="actors">
          <el-input v-model="movieForm.actors" placeholder="请输入演员，用逗号分隔" />
        </el-form-item>
        <el-form-item label="上映日期" prop="releaseDate">
          <el-date-picker
            v-model="movieForm.releaseDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input v-model.number="movieForm.price" type="number" step="0.1" placeholder="请输入价格" />
        </el-form-item>
        <el-form-item label="简介" prop="synopsis">
          <el-input
            v-model="movieForm.synopsis"
            type="textarea"
            :rows="4"
            placeholder="请输入影片简介"
          />
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
import { movieAPI } from '@/api/api'

const movies = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加影片')
const movieFormRef = ref(null)

const movieForm = ref({
  title: '',
  englishTitle: '',
  category: '',
  duration: null,
  rating: null,
  status: 1,
  poster: '',
  director: '',
  actors: '',
  releaseDate: '',
  price: null,
  synopsis: ''
})

const rules = {
  title: [
    { required: true, message: '请输入影片名称', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请输入类型', trigger: 'blur' }
  ],
  duration: [
    { required: true, message: '请输入时长', trigger: 'blur' },
    { type: 'number', message: '时长必须是数字', trigger: 'blur' }
  ],
  rating: [
    { required: true, message: '请输入评分', trigger: 'blur' },
    { type: 'number', message: '评分必须是数字', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  poster: [
    { required: true, message: '请输入海报URL', trigger: 'blur' }
  ],
  director: [
    { required: true, message: '请输入导演', trigger: 'blur' }
  ],
  actors: [
    { required: true, message: '请输入演员', trigger: 'blur' }
  ],
  releaseDate: [
    { required: true, message: '请选择上映日期', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' },
    { type: 'number', message: '价格必须是数字', trigger: 'blur' }
  ]
}

// 加载影片列表
const loadMovies = async () => {
  try {
    const response = await movieAPI.getMovies({})
    movies.value = response.data.data.content
  } catch (error) {
    ElMessage.error('加载影片失败')
    console.error('加载影片失败:', error)
  }
}

// 打开添加影片弹窗
const handleAddMovie = () => {
  dialogTitle.value = '添加影片'
  movieForm.value = {
    title: '',
    englishTitle: '',
    category: '',
    duration: null,
    rating: null,
    status: 1,
    poster: '',
    director: '',
    actors: '',
    releaseDate: '',
    price: null,
    synopsis: ''
  }
  dialogVisible.value = true
}

// 打开编辑影片弹窗
const handleEditMovie = (row) => {
  dialogTitle.value = '编辑影片'
  movieForm.value = { ...row }
  dialogVisible.value = true
}

// 删除影片
const handleDeleteMovie = (id) => {
  ElMessageBox.confirm('确定要删除该影片吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await movieAPI.deleteMovie(id)
      ElMessage.success('删除成功')
      loadMovies()
    } catch (error) {
      ElMessage.error('删除失败')
      console.error('删除影片失败:', error)
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!movieFormRef.value) return
  
  try {
    await movieFormRef.value.validate()
    
    if (movieForm.value.id) {
      // 编辑影片
      await movieAPI.updateMovie(movieForm.value.id, movieForm.value)
      ElMessage.success('编辑成功')
    } else {
      // 添加影片
      await movieAPI.addMovieAlt(movieForm.value)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    loadMovies()
  } catch (error) {
    if (error.name === 'Error') {
      ElMessage.error('操作失败: ' + error.message)
    }
    console.error('操作影片失败:', error)
  }
}

// 页面加载时获取影片列表
onMounted(() => {
  loadMovies()
})
</script>

<style scoped>
.admin-movies {
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
