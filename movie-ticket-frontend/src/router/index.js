import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/user/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/user/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'movies',
        name: 'Movies',
        component: () => import('@/views/user/Movies.vue'),
        meta: { title: '影片' }
      },
      {
        path: 'movie/:id',
        name: 'MovieDetail',
        component: () => import('@/views/user/MovieDetail.vue'),
        meta: { title: '影片详情' }
      },
      {
        path: 'select-seat/:scheduleId',
        name: 'SelectSeat',
        component: () => import('@/views/user/SelectSeat.vue'),
        meta: { title: '选座购票', requiresAuth: true }
      },
      {
        path: 'order/:orderId',
        name: 'OrderDetail',
        component: () => import('@/views/user/OrderDetail.vue'),
        meta: { title: '订单详情', requiresAuth: true }
      },
      {
        path: 'order/:orderId/pay',
        name: 'OrderPay',
        component: () => import('@/views/user/OrderPay.vue'),
        meta: { title: '订单支付', requiresAuth: true }
      },
      {
        path: 'payment/success',
        name: 'PaymentSuccess',
        component: () => import('@/views/user/PaymentSuccess.vue'),
        meta: { title: '支付成功', requiresAuth: false }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/user/Orders.vue'),
        meta: { title: '我的订单', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      },
      {
        path: 'cinemas',
        name: 'Cinemas',
        component: () => import('@/views/user/Cinemas.vue'),
        meta: { title: '影院' }
      },
      {
        path: 'cinema/:id',
        name: 'CinemaDetail',
        component: () => import('@/views/user/CinemaDetail.vue'),
        meta: { title: '影院详情' }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/user/Login.vue'),
    meta: { title: '登录' }
  },

  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/views/admin/Layout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '控制台' }
      },
      {
        path: 'movies',
        name: 'AdminMovies',
        component: () => import('@/views/admin/Movies.vue'),
        meta: { title: '影片管理' }
      },
      {
        path: 'cinemas',
        name: 'AdminCinemas',
        component: () => import('@/views/admin/Cinemas.vue'),
        meta: { title: '影院管理' }
      },
      {
        path: 'banners',
        name: 'AdminBanners',
        component: () => import('@/views/admin/Banners.vue'),
        meta: { title: '轮番图管理' }
      },
      {
        path: 'schedules',
        name: 'AdminSchedules',
        component: () => import('@/views/admin/Schedules.vue'),
        meta: { title: '排期管理' }
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/Orders.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'comments',
        name: 'AdminComments',
        component: () => import('@/views/admin/Comments.vue'),
        meta: { title: '评论管理' }
      }
    ]
  },

  {
    path: '/cinema-admin',
    name: 'CinemaAdminLayout',
    component: () => import('@/views/cinema-admin/Layout.vue'),
    redirect: '/cinema-admin/dashboard',
    meta: { requiresAuth: true, requiresCinemaAdmin: true },
    children: [
      {
        path: 'dashboard',
        name: 'CinemaDashboard',
        component: () => import('@/views/cinema-admin/Dashboard.vue'),
        meta: { title: '经营概览' }
      },
      {
        path: 'halls',
        name: 'CinemaHalls',
        component: () => import('@/views/cinema-admin/Halls.vue'),
        meta: { title: '放映厅管理' }
      },
      {
        path: 'schedules',
        name: 'CinemaSchedules',
        component: () => import('@/views/cinema-admin/Schedules.vue'),
        meta: { title: '排期管理' }
      },
      {
        path: 'orders',
        name: 'CinemaOrders',
        component: () => import('@/views/cinema-admin/Orders.vue'),
        meta: { title: '订单查询' }
      },
      {
        path: 'profile',
        name: 'CinemaProfile',
        component: () => import('@/views/cinema-admin/Profile.vue'),
        meta: { title: '影院设置' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  userStore.checkTokenExpired()

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.meta.requiresAdmin && userStore.userInfo.role !== 'admin') {
    next('/')
  } else if (to.meta.requiresCinemaAdmin && userStore.userInfo.role !== 'cinema_admin') {
    next('/')
  } else {
    next()
  }
})

export default router