import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api'

const instance = axios.create({
  baseURL: BASE_URL,
  timeout: 10000
})

instance.interceptors.response.use(
  response => response,
  error => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

export const bannerAPI = {
  // 获取所有轮播图列表
  getBanners: () => {
    return instance.get('/banners')
  },

  // 添加新轮播图
  addBanner: (data) => {
    return instance.post('/banners', data)
  },

  // 更新轮播图信息
  updateBanner: (id, data) => {
    return instance.put(`/banners/${id}`, data)
  },

  // 删除轮播图
  deleteBanner: (id) => {
    return instance.delete(`/banners/${id}`)
  }
}

export const movieAPI = {
  // 获取电影列表（支持筛选、排序、分页）
  getMovies: (params) => {
    return instance.get('/movies', { params })
  },

  // 获取电影详情
  getMovieById: (id) => {
    return instance.get(`/movies/${id}`)
  },

  // 获取热映电影列表
  getHotMovies: () => {
    return instance.get('/movies/hot')
  },

  // 获取即将上映电影列表
  getComingMovies: () => {
    return instance.get('/movies/coming')
  },

  // 获取电影类型列表
  getMovieTypes: () => {
    return instance.get('/movies/types')
  },

  // 获取电影地区列表
  getMovieRegions: () => {
    return instance.get('/movies/regions')
  },

  // 获取电影年代列表
  getMovieYears: () => {
    return instance.get('/movies/years')
  },

  // 添加电影（导入数据用）
  addMovie: (data) => {
    return instance.post('/movies', data)
  },

  // 添加电影（后台管理用）
  addMovieAlt: (data) => {
    return instance.post('/add/movies', data)
  },

  // 更新电影信息
  updateMovie: (id, data) => {
    return instance.put(`/movies/${id}`, data)
  },

  // 删除电影
  deleteMovie: (id) => {
    return instance.delete(`/movies/${id}`)
  },

  // 添加想看
  addWish: (movieId) => {
    return instance.post(`/movies/${movieId}/wish`)
  },

  // 取消想看
  removeWish: (movieId) => {
    return instance.delete(`/movies/${movieId}/wish`)
  },

  // 检查是否已想看
  checkWish: (movieId) => {
    return instance.get(`/movies/${movieId}/wish`)
  },

  // 获取用户想看列表
  getWishList: (params) => {
    return instance.get('/movies/wishlist', { params })
  }
}

export const dashboardAPI = {
  // 获取统计数据
  getStats: () => {
    return instance.get('/dashboard/stats')
  },

  // 获取订单趋势
  getOrderTrend: () => {
    return instance.get('/dashboard/order-trend')
  },

  // 获取影片类型分布
  getMovieTypeDistribution: () => {
    return instance.get('/dashboard/movie-type-distribution')
  },

  // 获取最近订单
  getRecentOrders: (params) => {
    return instance.get('/dashboard/recent-orders', { params })
  },

  // 获取订单详情
  getOrderDetail: (orderId) => {
    return instance.get('/dashboard/order-detail', { params: { orderId } })
  }
}

export const cinemaAPI = {
  // 获取热门影院列表
  getHotCinemas: () => {
    return instance.get('/cinemas/hot')
  },

  // 获取影院详情
  getCinemaById: (id) => {
    return instance.get(`/cinemas/${id}`)
  },

  // 获取影院列表（分页）
  getCinemas: (params) => {
    return instance.get('/cinemas', { params })
  },

  // 添加影院
  addCinema: (data) => {
    return instance.post('/cinemas', data)
  },

  // 更新影院信息
  updateCinema: (id, data) => {
    return instance.put(`/cinemas/${id}`, data)
  },

  // 删除影院
  deleteCinema: (id) => {
    return instance.delete(`/cinemas/${id}`)
  }
}

export const commentAPI = {
  // 获取所有评论（后台管理）
  getComments: () => {
    return instance.get('/comments')
  },

  // 根据ID获取评论详情
  getCommentById: (id) => {
    return instance.get(`/comments/${id}`)
  },

  // 根据电影ID获取评论列表
  getCommentsByMovieId: (movieId) => {
    return instance.get(`/comments/movie/${movieId}`)
  },

  // 发表新评论
  addComment: (data) => {
    return instance.post('/comments', data)
  },

  // 点赞/取消点赞评论
  likeComment: (commentId, liked) => {
    return instance.post(`/comments/${commentId}/like`, { liked })
  },

  // 删除评论
  deleteComment: (id) => {
    return instance.delete(`/comments/${id}`)
  }
}

export const authAPI = {
  // 用户登录
  login: (data) => {
    return instance.post('/auth/login', data)
  }
}

export const userAPI = {
  // 获取用户列表
  getUsers: () => {
    return instance.get('/users')
  },

  // 获取用户信息
  getUserById: (id) => {
    return instance.get(`/users/${id}`)
  },

  // 添加用户
  addUser: (data) => {
    return instance.post('/users', data)
  },

  // 更新用户信息
  updateUser: (id, data) => {
    return instance.put(`/users/${id}`, data)
  },

  // 更新用户状态
  updateUserStatus: (id, data) => {
    return instance.put(`/users/${id}/status`, data)
  },

  // 删除用户
  deleteUser: (id) => {
    return instance.delete(`/users/${id}`)
  }
}

export const scheduleAPI = {
  // 获取排期列表（支持按影片ID或影院ID筛选）
  getSchedules: (params) => {
    return instance.get('/schedules', { params })
  },

  // 获取排期详情
  getScheduleById: (id) => {
    return instance.get(`/schedules/${id}`)
  },

  // 获取排期详情（含座位信息）
  getScheduleDetail: (id) => {
    return instance.get(`/schedules/${id}/detail`)
  },

  // 添加排期
  addSchedule: (data) => {
    return instance.post('/schedules', data)
  },

  // 更新排期信息
  updateSchedule: (id, data) => {
    return instance.put(`/schedules/${id}`, data)
  },

  // 删除排期
  deleteSchedule: (id) => {
    return instance.delete(`/schedules/${id}`)
  }
}

export const orderAPI = {
  // 创建订单
  createOrder: (data) => {
    return instance.post('/orders', data)
  },

  // 获取订单详情
  getOrderById: (orderId) => {
    return instance.get(`/orders/${orderId}`)
  },

  // 获取用户订单列表
  getUserOrders: (userId, params) => {
    return instance.get(`/users/${userId}/orders`, { params })
  },

  // 获取订单详情（用户端）
  getOrderDetail: (orderId) => {
    return instance.get(`/orders/${orderId}/detail`)
  },

  // 刷新订单状态
  refreshOrderStatus: (orderId) => {
    return instance.post(`/orders/${orderId}/refresh`)
  },

  // 取消订单
  cancelOrder: (orderId) => {
    return instance.post(`/orders/${orderId}/cancel`)
  },

  // 删除订单
  deleteOrder: (orderId) => {
    return instance.delete(`/orders/${orderId}`)
  },

  // 获取所有订单列表（后台管理）
  getAllOrders: (params) => {
    return instance.get('/admin/orders', { params })
  },

  // 获取订单详情（后台管理）
  getOrderDetailForAdmin: (orderId) => {
    return instance.get(`/admin/orders/${orderId}`)
  },

  // 取消订单（后台管理）
  adminCancelOrder: (orderId) => {
    return instance.post(`/admin/orders/${orderId}/cancel`)
  },

  // 申请退票
  applyRefund: (orderId, reason) => {
    return instance.post(`/orders/${orderId}/refund`, { reason })
  },

  // 审核退票（后台管理）
  approveRefund: (orderId, action) => {
    return instance.post(`/admin/orders/${orderId}/refund/approve`, { action })
  },

  // 获取退票申请列表（后台管理）
  getRefundOrders: (params) => {
    return instance.get('/admin/orders/refund/list', { params })
  }
}

export const uploadAPI = {
  // 上传文件（图片等）
  uploadFile: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return instance.post('/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

export const alipayAPI = {
  createPayment: (orderId) => {
    return instance.get('/alipay/pay', { params: { orderId } })
  },
  queryPayment: (orderId) => {
    return instance.get('/alipay/query', { params: { orderId } })
  },
  refund: (orderId, refundAmount) => {
    return instance.post('/alipay/refund', { orderId, refundAmount })
  }
}

export default instance