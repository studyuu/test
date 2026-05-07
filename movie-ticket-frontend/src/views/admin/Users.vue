<template>
  <div class="admin-users">
    <div class="page-header">
      <h2>用户管理</h2>
      <el-button type="primary" @click="handleAdd">添加用户</el-button>
    </div>

    <el-table :data="users" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 'admin' ? 'danger' : ''">{{ row.role === 'admin' ? '管理员' : '用户' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'active' ? 'success' : 'info'">{{ row.status === 'active' ? '正常' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button :type="row.status === 'active' ? 'danger' : 'success'" link @click="handleStatus(row)">
            {{ row.status === 'active' ? '禁用' : '启用' }}
          </el-button>
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

    <el-dialog :title="editMode ? '更新用户' : '添加用户'" v-model="dialogVisible" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="formData.password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请输入密码"
          >
            <template #suffix>
              <span class="password-icon" @click="showPassword = !showPassword">
                <el-icon><View v-if="showPassword" /><Hide v-else /></el-icon>
              </span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="formData.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="formData.role">
            <el-option label="用户" value="user" />
            <el-option label="管理员" value="admin" />
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { View, Hide } from '@element-plus/icons-vue'
import { userAPI } from '@/api/api'
import Pagination from '@/components/Pagination.vue'

const users = ref([])
const dialogVisible = ref(false)
const pagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})
const formData = ref({
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  role: 'user'
})
const editMode = ref(false)
const editUserId = ref(null)
const showPassword = ref(false)

const loadUsers = async () => {
  try {
    const response = await userAPI.getUsers({
      pageNum: pagination.value.pageNum,
      pageSize: pagination.value.pageSize
    })
    if (response.data.code === 200) {
      const data = response.data.data
      users.value = data.content || data.list || data
      pagination.value.total = data.total || (data.content || data.list || data).length
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
  }
}

const handlePageChange = ({ pageNum, pageSize }) => {
  pagination.value.pageNum = pageNum
  pagination.value.pageSize = pageSize
  loadUsers()
}

const handleAdd = () => {
  editMode.value = false
  editUserId.value = null
  showPassword.value = false
  formData.value = {
    username: '',
    password: '',
    nickname: '',
    email: '',
    phone: '',
    role: 'user'
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  editMode.value = true
  editUserId.value = row.id
  showPassword.value = false
  formData.value = {
    username: row.username,
    password: '',
    nickname: row.nickname,
    email: row.email || '',
    phone: row.phone || '',
    role: row.role
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formData.value.username || !formData.value.password) {
    ElMessage.error('用户名和密码不能为空')
    return
  }

  try {
    let response
    if (editMode.value) {
      const data = {
        username: formData.value.username,
        nickname: formData.value.nickname,
        email: formData.value.email,
        phone: formData.value.phone
      }
      response = await userAPI.updateUser(editUserId.value, data)
    } else {
      response = await userAPI.addUser(formData.value)
    }

    if (response.data.code === 200) {
      ElMessage.success(editMode.value ? '更新成功' : '添加成功')
      dialogVisible.value = false
      loadUsers()
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

const handleStatus = async (row) => {
  const newStatus = row.status === 'active' ? 'disabled' : 'active'
  const action = row.status === 'active' ? '禁用' : '启用'

  ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await userAPI.updateUserStatus(row.id, { status: newStatus })
      if (response.data.code === 200) {
        ElMessage.success(`${action}成功`)
        loadUsers()
      } else {
        ElMessage.error(response.data.message)
      }
    } catch (error) {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  })
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.admin-users {
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

.password-icon {
  cursor: pointer;
  color: #999;
  font-size: 16px;
}

.password-icon:hover {
  color: #409eff;
}
</style>