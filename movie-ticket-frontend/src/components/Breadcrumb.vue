<template>
  <el-breadcrumb separator="/">
    <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">控制台</el-breadcrumb-item>
    <el-breadcrumb-item v-for="item in breadcrumbList" :key="item.path" :to="{ path: item.path }">
      {{ item.name }}
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const breadcrumbMap = {
  '/admin/movies': { name: '影片管理', path: '/admin/movies' },
  '/admin/cinemas': { name: '影院管理', path: '/admin/cinemas' },
  '/admin/banners': { name: '轮播图管理', path: '/admin/banners' },
  '/admin/schedules': { name: '排期管理', path: '/admin/schedules' },
  '/admin/orders': { name: '订单管理', path: '/admin/orders' },
  '/admin/users': { name: '用户管理', path: '/admin/users' },
  '/admin/comments': { name: '评论管理', path: '/admin/comments' },
}

const breadcrumbList = computed(() => {
  const currentPath = route.path
  if (breadcrumbMap[currentPath]) {
    return [breadcrumbMap[currentPath]]
  }
  return []
})
</script>