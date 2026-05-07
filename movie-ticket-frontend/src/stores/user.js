import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const expireTime = ref(parseInt(localStorage.getItem('expireTime') || '0'))

  const isLoggedIn = computed(() => {
    if (!token.value) return false
    if (expireTime.value > 0 && Date.now() > expireTime.value) {
      logout()
      return false
    }
    return true
  })
  
  const isAdmin = computed(() => userInfo.value?.role === 'admin')

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setExpireTime = (time) => {
    expireTime.value = time
    localStorage.setItem('expireTime', String(time))
  }

  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
    if (info.token) {
      setToken(info.token)
    }
    if (info.expireTime) {
      setExpireTime(info.expireTime)
    }
  }

  const logout = () => {
    token.value = ''
    userInfo.value = {}
    expireTime.value = 0
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('expireTime')
  }

  const checkTokenExpired = () => {
    if (expireTime.value > 0 && Date.now() > expireTime.value) {
      logout()
      return true
    }
    return false
  }

  return {
    token,
    userInfo,
    expireTime,
    isLoggedIn,
    isAdmin,
    setToken,
    setExpireTime,
    setUserInfo,
    logout,
    checkTokenExpired
  }
})
