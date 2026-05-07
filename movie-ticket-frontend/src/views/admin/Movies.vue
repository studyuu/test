<template>
  <div class="admin-movies">
    <div class="page-header">
      <h2>影片管理</h2>
      <div class="header-right">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入影片名称搜索"
          clearable
          style="width: 250px; margin-right: 10px;"
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch" style="margin-right: 15px;">搜索</el-button>
        <el-button type="primary" @click="handleAddMovie">添加影片</el-button>
      </div>
    </div>
    
    <el-table :data="movies" border>
      <el-table-column prop="movieId" label="影片ID" width="120" />
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
          <el-button type="danger" link @click="handleDeleteMovie(row.movieId)">删除</el-button>
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
            <el-option label="上映中" :value="1" />
            <el-option label="即将上映" :value="2" />
            <el-option label="已下线" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="海报" prop="poster">
          <el-upload
            v-model:file-list="fileList"
            :action="uploadUrl"
            list-type="picture-card"
            :on-preview="handlePictureCardPreview"
            :on-remove="handleRemove"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            accept="image/*"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <el-dialog v-model="dialogVisibleImg" title="海报预览" width="400px">
            <img w-full :src="dialogImageUrl" alt="海报预览" style="width: 100%" />
          </el-dialog>
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
import { Plus, Search } from '@element-plus/icons-vue'
import { movieAPI } from '@/api/api'
import Pagination from '@/components/Pagination.vue'

const movies = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加影片')
const movieFormRef = ref(null)
const pagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const searchKeyword = ref('')
let searchTimer = null

const uploadUrl = 'http://localhost:8080/api/upload'
const fileList = ref([])
const dialogVisibleImg = ref(false)
const dialogImageUrl = ref('')

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
    { required: true, message: '请上传海报', trigger: 'change' }
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
    const params = {
      pageNum: pagination.value.pageNum,
      pageSize: pagination.value.pageSize
    }
    // 如果有搜索关键词，添加到请求参数中
    if (searchKeyword.value.trim()) {
      params.keyword = searchKeyword.value.trim()
    }
    
    const response = await movieAPI.getMovies(params)
    const data = response.data.data
    movies.value = data.content || data.list || data
    pagination.value.total = data.total || (data.content || data.list || data).length
  } catch (error) {
    ElMessage.error('加载影片失败')
    console.error('加载影片失败:', error)
  }
}

// 搜索处理
const handleSearch = () => {
  // 重置到第一页
  pagination.value.pageNum = 1
  loadMovies()
}

// 输入时防抖搜索
const handleSearchInput = () => {
  // 清除之前的定时器
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  // 延迟300ms后搜索
  searchTimer = setTimeout(() => {
    handleSearch()
  }, 300)
}

const handlePageChange = ({ pageNum, pageSize }) => {
  pagination.value.pageNum = pageNum
  pagination.value.pageSize = pageSize
  loadMovies()
}

// 计算序号，确保分页时序号连续
const indexMethod = (index) => {
  return (pagination.value.pageNum - 1) * pagination.value.pageSize + index + 1
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
  fileList.value = []
  dialogVisible.value = true
}

// 打开编辑影片弹窗
const handleEditMovie = (row) => {
  dialogTitle.value = '编辑影片'
  // 处理日期格式，确保 el-date-picker 能正确解析
  let releaseDate = row.releaseDate
  if (releaseDate && typeof releaseDate === 'string' && !releaseDate.includes('T')) {
    // 如果是简单日期格式，转换为 Date 对象
    releaseDate = new Date(releaseDate + 'T00:00:00')
  }
  movieForm.value = { 
    ...row,
    id: row.movieId,
    releaseDate
  }
  if (row.poster) {
    fileList.value = [{ name: row.title, url: row.poster }]
  } else {
    fileList.value = []
  }
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

// 海报上传成功
const handleAvatarSuccess = (response, uploadFile) => {
  if (response.code === 200) {
    movieForm.value.poster = response.data.url
    fileList.value = [uploadFile]
    ElMessage.success('海报上传成功')
  } else {
    ElMessage.error('海报上传失败')
  }
}

// 上传前验证
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('请上传图片文件')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

// 移除海报
const removePoster = () => {
  movieForm.value.poster = ''
}

// 图片预览
const handlePictureCardPreview = (uploadFile) => {
  dialogImageUrl.value = uploadFile.url
  dialogVisibleImg.value = true
}

// 删除图片
const handleRemove = (uploadFile) => {
  movieForm.value.poster = ''
  fileList.value = []
}

// 提交表单
const handleSubmit = async () => {
  if (!movieFormRef.value) return
  
  try {
    await movieFormRef.value.validate()
    
    // 处理日期格式
    const formData = { ...movieForm.value }
    if (formData.releaseDate) {
      // 如果是日期对象，转换为字符串格式
      if (typeof formData.releaseDate === 'object' && formData.releaseDate.toISOString) {
        const date = formData.releaseDate
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        formData.releaseDate = `${year}-${month}-${day}`
      } else if (typeof formData.releaseDate === 'string' && formData.releaseDate.includes('T')) {
        // 如果是 ISO 格式的字符串，只取日期部分
        formData.releaseDate = formData.releaseDate.split('T')[0]
      }
    }
    
    if (formData.id) {
      // 编辑影片
      await movieAPI.updateMovie(formData.id, formData)
      ElMessage.success('编辑成功')
    } else {
      // 添加影片
      await movieAPI.addMovieAlt(formData)
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

.header-right {
  display: flex;
  align-items: center;
}

.page-header h2 {
  margin: 0;
}

.poster-upload {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.avatar-uploader {
  width: 120px;
  height: 160px;
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-icon {
  color: #999;
  font-size: 28px;
}

.remove-btn {
  color: #ff6b6b;
  margin-top: 4px;
}

.remove-btn:hover {
  color: #ff4757;
}
</style>
