<template>
  <el-container class="cinema-admin-layout">
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <el-icon size="32" color="#fff"><OfficeBuilding /></el-icon>
        <span>影院管理后台</span>
      </div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/cinema-admin/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>经营概览</span>
        </el-menu-item>
        <el-menu-item index="/cinema-admin/movies">
          <el-icon><Film /></el-icon>
          <span>影片管理</span>
        </el-menu-item>
        <el-menu-item index="/cinema-admin/halls">
          <el-icon><Grid /></el-icon>
          <span>放映厅管理</span>
        </el-menu-item>
        <el-menu-item index="/cinema-admin/schedules">
          <el-icon><Calendar /></el-icon>
          <span>排期管理</span>
        </el-menu-item>
        <el-menu-item index="/cinema-admin/orders">
          <el-icon><Ticket /></el-icon>
          <span>订单查询</span>
        </el-menu-item>
        <el-menu-item index="/cinema-admin/comments">
          <el-icon><ChatDotRound /></el-icon>
          <span>评论管理</span>
        </el-menu-item>
        <el-menu-item index="/cinema-admin/profile">
          <el-icon><Setting /></el-icon>
          <span>影院设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <Breadcrumb />
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo.avatar" />
              {{ userStore.userInfo.nickname }}
              <span class="role-tag">影院管理员</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="home">返回前台</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import Breadcrumb from '@/components/Breadcrumb.vue'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      ElMessage.success('退出成功')
      router.push('/login')
    })
  } else if (command === 'home') {
    router.push('/home')
  }
}
</script>

<style scoped>
.cinema-admin-layout {
  min-height: 100vh;
}

.sidebar {
  background: #304156;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  border-bottom: 1px solid #1f2d3d;
}

.header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.role-tag {
  background: #67C23A;
  color: #fff;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}

.main {
  background: #f0f2f5;
  padding: 20px;
}
</style>
