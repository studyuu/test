<template>
  <div class="admin-comments">
    <div class="page-header">
      <h2>评论管理</h2>
      <div class="filter-section">
        <el-select v-model="filterType" placeholder="筛选评论" style="width: 150px;font-weight: 600;">
          <el-option label="全部评论" value="all" />
          <el-option label="好评(4星及以上)" value="good" />
          <el-option label="差评(4星以下)" value="bad" />
        </el-select>
      </div>
    </div>
    
    <el-table :data="filteredComments" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="userName" label="用户" width="120" />
      <el-table-column prop="content" label="评论内容" show-overflow-tooltip />
      <el-table-column prop="rating" label="评分" width="150">
        <template #default="{ row }">
          <el-rate :model-value="row.rating" disabled />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="评论时间" width="160" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleView(row)">查看</el-button>
          <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="评论详情" v-model="dialogVisible" width="500px">
      <div class="comment-detail" v-if="selectedComment">
        <div class="user-info">
          <img :src="selectedComment.avatar" alt="头像" class="avatar" />
          <div class="user-detail">
            <span class="user-name">{{ selectedComment.userName }}</span>
            <span class="comment-time">{{ selectedComment.createTime }}</span>
          </div>
        </div>
        <div class="rating-section">
          <span class="label">评分：</span>
          <el-rate :model-value="selectedComment.rating" disabled />
        </div>
        <div class="content-section">
          <span class="label">评论内容：</span>
          <p>{{ selectedComment.content }}</p>
        </div>
        <div class="images-section" v-if="selectedComment.images && selectedComment.images.length > 0">
          <span class="label">图片：</span>
          <div class="images-container">
            <img v-for="(img, index) in selectedComment.images" :key="index" :src="img" 
                 class="comment-image" :alt="'图片' + (index + 1)" />
          </div>
        </div>
        <div class="likes-section">
          <span class="label">点赞数：</span>
          <span>{{ selectedComment.likes }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { commentAPI } from '@/api/api'

const comments = ref([])
const dialogVisible = ref(false)
const selectedComment = ref(null)
const filterType = ref('all')

const filteredComments = computed(() => {
  if (filterType.value === 'all') {
    return comments.value
  } else if (filterType.value === 'good') {
    return comments.value.filter(comment => comment.rating >= 4)
  } else if (filterType.value === 'bad') {
    return comments.value.filter(comment => comment.rating < 4)
  }
  return comments.value
})

const loadComments = async () => {
  try {
    const response = await commentAPI.getComments()
    if (response.data.code === 200) {
      comments.value = response.data.data
    }
  } catch (error) {
    console.error('加载评论列表失败:', error)
  }
}

const handleView = async (comment) => {
  try {
    const response = await commentAPI.getCommentById(comment.id)
    if (response.data.code === 200) {
      selectedComment.value = response.data.data
      dialogVisible.value = true
    } else {
      ElMessage.error('获取评论详情失败')
    }
  } catch (error) {
    console.error('获取评论详情失败:', error)
    ElMessage.error('获取评论详情失败')
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该评论吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await commentAPI.deleteComment(id)
      if (response.data.code === 200) {
        ElMessage.success('删除成功')
        loadComments()
      } else {
        ElMessage.error(response.data.message || '删除失败')
      }
    } catch (error) {
      console.error('删除评论失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

onMounted(() => {
  loadComments()
})
</script>

<style scoped>
.admin-comments {
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

.filter-section {
  display: flex;
  align-items: center;
}

.comment-detail {
  padding: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin-right: 15px;
}

.user-detail {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: bold;
  font-size: 16px;
}

.comment-time {
  color: #999;
  font-size: 14px;
  margin-top: 5px;
}

.rating-section,
.content-section,
.images-section,
.likes-section {
  margin-bottom: 15px;
}

.label {
  font-weight: bold;
  color: #666;
}

.content-section p {
  margin: 5px 0 0 0;
  line-height: 1.6;
  color: #333;
}

.images-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

.comment-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
}

.likes-section {
  padding-top: 15px;
  border-top: 1px solid #eee;
}
</style>