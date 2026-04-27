<template>
  <div class="banners-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>轮播图管理</span>
          <el-button type="primary" @click="handleAddBanner">
            <el-icon><Plus /></el-icon>
            添加轮播图
          </el-button>
        </div>
      </template>
      
      <el-table :data="banners" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="图片" width="120">
          <template #default="{ row }">
            <el-image
              :src="row.image"
              fit="cover"
              style="width: 100px; height: 60px"
            />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" width="180" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="movieId" label="关联电影ID" width="120" />
        <el-table-column prop="orderNum" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="handleEditBanner(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button size="small" type="danger" @click="handleDeleteBanner(row.id)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 添加/编辑轮播图弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form
        :model="bannerForm"
        :rules="rules"
        ref="bannerFormRef"
        label-width="100px"
      >
        <el-form-item label="图片" prop="image">
          <el-radio-group v-model="imageUploadType">
            <el-radio label="url">输入图片URL</el-radio>
            <el-radio label="upload">上传图片</el-radio>
          </el-radio-group>
          
          <div v-if="imageUploadType === 'url'" style="margin-top: 10px">
            <el-form-item prop="image">
              <el-input v-model="bannerForm.image" placeholder="请输入轮播图图片" />
            </el-form-item>
          </div>
          
          <div v-else style="margin-top: 10px">
            <el-upload
              class="upload-demo"
              action="http://localhost:8080/api/upload"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :file-list="fileList"
              :auto-upload="true"
              prop="file"
            >
              <el-button type="primary">
                <el-icon><Upload /></el-icon>
                点击上传
              </el-button>
              <template #tip>
                <div class="el-upload__tip">
                  只能上传jpg/png文件，且不超过10MB
                </div>
              </template>
            </el-upload>
          </div>
        </el-form-item>
        
        <el-form-item label="标题" prop="title">
          <el-input v-model="bannerForm.title" placeholder="请输入轮播图标题" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="bannerForm.description"
            type="textarea"
            placeholder="请输入轮播图描述"
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="关联电影ID" prop="movieId">
          <el-input
            v-model.number="bannerForm.movieId"
            type="number"
            placeholder="请输入关联电影ID"
          />
        </el-form-item>
        <el-form-item label="排序" prop="orderNum">
          <el-input
            v-model.number="bannerForm.orderNum"
            type="number"
            placeholder="请输入排序号"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="bannerForm.status"
            active-value="1"
            inactive-value="0"
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
import { bannerAPI } from '@/api/api'

const banners = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加轮播图')
const imageUploadType = ref('url') // 图片上传类型：url或upload
const fileList = ref([]) // 上传的文件列表
const bannerFormRef = ref(null) // 表单引用

const bannerForm = ref({
  image: '',
  title: '',
  description: '',
  movieId: null,
  orderNum: 0,
  status: 1
})

// 表单验证规则
const rules = {
  image: [
    {
      required: true,
      message: '请上传轮播图图片',
      trigger: 'change'
    }
  ],
  title: [
    { required: true, message: '请输入轮播图标题', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入轮播图描述', trigger: 'blur' }
  ],
  movieId: [
    { required: true, message: '请输入关联电影ID', trigger: 'blur' }
  ],
  orderNum: [
    { required: true, message: '请输入排序号', trigger: 'blur' }
  ],
  image: [
    {
      required: true,
      validator: (rule, value, callback) => {
        if (imageUploadType.value === 'url') {
          if (!value) {
            callback(new Error('请输入轮播图图片URL'))
          } else {
            callback()
          }
        } else {
          if (!value) {
            callback(new Error('请上传轮播图图片'))
          } else {
            callback()
          }
        }
      },
      trigger: 'blur'
    }
  ]
}

// 加载轮播图列表
const loadBanners = async () => {
  try {
    const response = await bannerAPI.getBanners()
    banners.value = response.data
  } catch (error) {
    ElMessage.error('加载轮播图失败')
    console.error('加载轮播图失败:', error)
  }
}

// 打开添加轮播图弹窗
const handleAddBanner = () => {
  dialogTitle.value = '添加轮播图'
  imageUploadType.value = 'url'
  fileList.value = []
  bannerForm.value = {
    image: '',
    title: '',
    description: '',
    movieId: null,
    orderNum: 0,
    status: 1
  }
  dialogVisible.value = true
}

// 打开编辑轮播图弹窗
const handleEditBanner = (row) => {
  dialogTitle.value = '编辑轮播图'
  imageUploadType.value = 'url'
  fileList.value = []
  bannerForm.value = { ...row }
  dialogVisible.value = true
}

// 删除轮播图
const handleDeleteBanner = (id) => {
  ElMessageBox.confirm('确定要删除该轮播图吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await bannerAPI.deleteBanner(id)
      ElMessage.success('删除成功')
      loadBanners()
    } catch (error) {
      ElMessage.error('删除失败')
      console.error('删除轮播图失败:', error)
    }
  })
}

// 处理文件上传成功
const handleUploadSuccess = (response, file) => {
  if (response.code === 200) {
    bannerForm.value.image = response.data.url
    ElMessage.success('上传成功')
  } else {
    ElMessage.error('上传失败')
  }
}

// 处理文件上传失败
const handleUploadError = (error) => {
  ElMessage.error('上传失败')
  console.error('上传失败:', error)
}

// 提交表单
const handleSubmit = async () => {
  try {
    // 验证表单
    await bannerFormRef.value.validate()
    
    if (bannerForm.value.id) {
      // 编辑轮播图
      await bannerAPI.updateBanner(bannerForm.value.id, bannerForm.value)
      ElMessage.success('编辑成功')
    } else {
      // 添加轮播图
      await bannerAPI.addBanner(bannerForm.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadBanners()
  } catch (error) {
    if (error.name === 'Error') {
      ElMessage.error('操作失败')
      console.error('操作轮播图失败:', error)
    }
    // 表单验证失败时，error 是一个对象，不需要处理
  }
}

// 页面加载时获取轮播图列表
onMounted(() => {
  loadBanners()
})
</script>

<style scoped>
.banners-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>