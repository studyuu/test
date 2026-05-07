<template>
  <div class="auth-page">
    <div class="header">
      <div class="logo">
        <el-icon size="36" color="#ff6b6b"><VideoPlay /></el-icon>
        <span class="logo-text">影票通</span>
      </div>
    </div>
    
    <div class="login-container">
      <div class="login-box">
        <div class="login-tabs">
          <span class="tab" :class="{ active: activeTab === 'login' }" @click="activeTab = 'login'">个人用户登录</span>
          <span class="tab" :class="{ active: activeTab === 'register' }" @click="activeTab = 'register'">个人用户注册</span>
        </div>
        
        <div class="login-content">
          <div class="login-left">
            <div class="image-area">
              <img src="@/assets/images/login.png" alt="影院座位" class="bg-image" />
            </div>
            <div class="slogan-section">
              <h3>发现好电影</h3>
              <p>享受好时光</p>
            </div>
          </div>
          
          <div class="login-divider">
            <div class="line"></div>
            <div class="line"></div>
          </div>
          
          <div class="login-right">
            <template v-if="activeTab === 'login'">
              <div class="right-tabs">
                <span class="right-tab active">密码登录</span>
              </div>
              
              <el-form 
                ref="formRef" 
                :model="form" 
                :rules="loginRules"
                class="login-form"
                size="large"
              >
                <el-form-item prop="username">
                  <el-autocomplete
                    v-model="form.username"
                    :fetch-suggestions="querySearch"
                    placeholder="用户名"
                    class="form-input"
                    :prefix-icon="User"
                    clearable
                    @select="selectUsername"
                  />
                </el-form-item>
                
                <el-form-item prop="password">
                  <el-input 
                    v-model="form.password" 
                    type="password" 
                    placeholder="密码"
                    class="form-input"
                    :prefix-icon="Lock"
                    show-password
                    @keyup.enter="handleLogin"
                  />
                </el-form-item>
                
                <div class="form-options">
                  <el-checkbox v-model="rememberMe">记住我</el-checkbox>
                  <a href="#" class="forgot-link">忘记密码</a>
                </div>
                
                <el-button 
                  type="primary" 
                  class="login-btn"
                  :loading="loading"
                  @click="handleLogin"
                >
                  {{ loading ? '登录中...' : '登 录' }}
                </el-button>
              </el-form>
              
              <div class="social-login">
                <button class="social-btn wechat" @click="handleSocialLogin('wechat')">
                  <el-icon size="18"><ChatDotRound /></el-icon>
                  <span>微信登录</span>
                </button>
                <button class="social-btn qq" @click="handleSocialLogin('qq')">
                  <el-icon size="18"><Message /></el-icon>
                  <span>QQ登录</span>
                </button>
              </div>
            </template>
            
            <template v-else>
              <div class="right-tabs">
                <span class="right-tab active">用户注册</span>
              </div>
              
              <el-form 
                ref="registerFormRef" 
                :model="registerForm" 
                :rules="registerRules"
                class="login-form"
                size="large"
              >
                <el-form-item prop="username">
                  <el-input 
                    v-model="registerForm.username" 
                    placeholder="用户名"
                    class="form-input"
                    :prefix-icon="User"
                  />
                </el-form-item>
                
                <el-form-item prop="password">
                  <el-input 
                    v-model="registerForm.password" 
                    type="password" 
                    placeholder="密码"
                    class="form-input"
                    :prefix-icon="Lock"
                    show-password
                  />
                </el-form-item>
                
                <el-form-item prop="confirmPassword">
                  <el-input 
                    v-model="registerForm.confirmPassword" 
                    type="password" 
                    placeholder="确认密码"
                    class="form-input"
                    :prefix-icon="Lock"
                    show-password
                  />
                </el-form-item>
                
                <el-form-item prop="nickname">
                  <el-input 
                    v-model="registerForm.nickname" 
                    placeholder="昵称"
                    class="form-input"
                    :prefix-icon="User"
                  />
                </el-form-item>
                
                <el-form-item prop="phone">
                  <el-input 
                    v-model="registerForm.phone" 
                    placeholder="手机号（选填）"
                    class="form-input"
                    :prefix-icon="Message"
                  />
                </el-form-item>
                
                <el-button 
                  type="primary" 
                  class="login-btn"
                  :loading="registerLoading"
                  @click="handleRegister"
                >
                  {{ registerLoading ? '注册中...' : '注 册' }}
                </el-button>
              </el-form>
              
              <div class="social-login">
                <button class="social-btn wechat" @click="handleSocialLogin('wechat')">
                  <el-icon size="18"><ChatDotRound /></el-icon>
                  <span>微信登录</span>
                </button>
                <button class="social-btn qq" @click="handleSocialLogin('qq')">
                  <el-icon size="18"><Message /></el-icon>
                  <span>QQ登录</span>
                </button>
              </div>
            </template>
          </div>
        </div>
      </div>
    </div>
    
    <div class="footer">
      <p>关于我们 | 联系方式 | 人才招聘 | 商家入驻 | 广告服务 | 手机影票通</p>
      <p>Copyright © 2026 影票通. All Rights Reserved.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Lock, ChatDotRound, Message, VideoPlay } from '@element-plus/icons-vue'
import { authAPI } from '@/api/api'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const registerFormRef = ref(null)
const loading = ref(false)
const registerLoading = ref(false)
const rememberMe = ref(false)
const savedUsernames = ref([])
const activeTab = ref('login')

const form = reactive({
  username: '',
  password: '',
  captcha: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  phone: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3到20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ]
}

const loadSavedUsers = () => {
  try {
    const saved = localStorage.getItem('rememberedUsers')
    if (saved) {
      savedUsernames.value = JSON.parse(saved)
    }
  } catch (e) {
    console.error('加载保存的用户信息失败:', e)
  }
}

const saveUser = (username, password) => {
  if (!rememberMe.value) return
  
  try {
    loadSavedUsers()
    const index = savedUsernames.value.findIndex(u => u.username === username)
    if (index > -1) {
      savedUsernames.value.splice(index, 1)
    }
    savedUsernames.value.unshift({ username, password })
    if (savedUsernames.value.length > 5) {
      savedUsernames.value = savedUsernames.value.slice(0, 5)
    }
    localStorage.setItem('rememberedUsers', JSON.stringify(savedUsernames.value))
  } catch (e) {
    console.error('保存用户信息失败:', e)
  }
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  
  try {
    const response = await authAPI.login({
      username: form.username,
      password: form.password
    })
    
    if (response.data.code === 200) {
      userStore.setUserInfo(response.data.data)
      
      saveUser(form.username, form.password)
      
      ElMessage.success('登录成功')
      
      const role = response.data.data.role
      if (role === 'admin') {
        router.push('/admin/dashboard')
      } else if (role === 'cinema_admin') {
        router.push('/cinema-admin/dashboard')
      } else {
        router.push('/home')
      }
    } else {
      ElMessage.error(response.data.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error('登录失败，请稍后重试')
  }
  
  loading.value = false
}

const selectUsername = (username) => {
  form.username = username
  const user = savedUsernames.value.find(u => u.username === username)
  if (user) {
    form.password = user.password
  }
}

const querySearch = (queryString, cb) => {
  const results = savedUsernames.value.filter(user => 
    user.username.toLowerCase().includes(queryString.toLowerCase())
  )
  cb(results.map(user => ({ value: user.username })))
}

const handleSocialLogin = (type) => {
  ElMessage.info(`${type === 'wechat' ? '微信' : 'QQ'}登录功能开发中`)
}

const handleRegister = async () => {
  const valid = await registerFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  registerLoading.value = true
  
  try {
    const response = await authAPI.register({
      username: registerForm.username,
      password: registerForm.password,
      nickname: registerForm.nickname,
      phone: registerForm.phone
    })
    
    if (response.data.code === 200) {
      ElMessage.success('注册成功')
      activeTab.value = 'login'
      form.username = registerForm.username
      form.password = registerForm.password
      registerForm.username = ''
      registerForm.password = ''
      registerForm.confirmPassword = ''
      registerForm.nickname = ''
      registerForm.phone = ''
    } else {
      ElMessage.error(response.data.message || '注册失败')
    }
  } catch (error) {
    console.error('注册失败:', error)
    ElMessage.error('注册失败，请稍后重试')
  }
  
  registerLoading.value = false
}

onMounted(() => {
  loadSavedUsers()
  if (savedUsernames.value.length > 0) {
    form.username = savedUsernames.value[0].username
    form.password = savedUsernames.value[0].password
  }
})
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #fff5f5 0%, #ffe4e4 50%, #fff0f0 100%);
  display: flex;
  flex-direction: column;
}

.header {
  padding: 20px 60px;
  background: linear-gradient(135deg, #fff9f9 0%, #fff0f0 50%, #fff5f5 100%);
  border-bottom: 1px solid #ffe0e0;
  box-shadow: 0 2px 15px rgba(255, 107, 107, 0.15);
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.login-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
}

.login-box {
  width: 100%;
  max-width: 850px;
  background: linear-gradient(135deg, #fff9f9 0%, #fff5f5 50%, #fff7f7 100%);
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(255, 107, 107, 0.25);
  overflow: hidden;
  border: 1px solid #ffe0e0;
}

.login-tabs {
  display: flex;
  padding: 20px 60px;
  border-bottom: 1px solid #f0f0f0;
}

.login-tabs .tab {
  font-size: 18px;
  font-weight: 600;
  padding-bottom: 15px;
  margin-right: 40px;
  cursor: pointer;
  color: #666;
  border-bottom: 2px solid transparent;
  transition: all 0.3s ease;
}

.login-tabs .tab.active {
  color: #ff6b6b;
  border-bottom-color: #ff6b6b;
}

.login-content {
  display: flex;
  padding: 40px 60px;
}

.login-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-right: 40px;
  height: 350px;
}

.image-area {
  width: 100%;
  flex: 1;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
}

.bg-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.slogan-section {
  text-align: center;
}

.slogan-section h3 {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin: 0 0 8px 0;
}

.slogan-section p {
  font-size: 14px;
  color: #999;
  margin: 0;
}

/* .login-divider {
  display: flex;
  align-items: center;
  padding: 0 20px;
} */

.login-divider .line {
  flex: 1;
  height: 1px;
  background: #e8e8e8;
}

/* .login-divider .or {
  padding: 0 15px;
  font-size: 12px;
  color: #999;
} */

.login-right {
  flex: 1;
  padding-left: 40px;
  border-left: 1px solid #e8e8e8;
}

.right-tabs {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
}

.right-tab {
  font-size: 14px;
  cursor: pointer;
  color: #666;
  padding-bottom: 8px;
  border-bottom: 2px solid transparent;
}

.right-tab.active {
  color: #ff6b6b;
  border-bottom-color: #ff6b6b;
}

.login-form {
  margin-bottom: 20px;
}

.form-input {
  width: 100%;
  height: 40px;
  margin-bottom: 16px;
}

:deep(.form-input .el-input__wrapper) {
  border-radius: 4px;
}

.captcha-row {
  display: flex;
  gap: 12px;
}

.captcha-input {
  flex: 1;
  height: 40px;
}

.captcha-code {
  width: 100px;
  height: 40px;
  background: #f5f5f5;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: bold;
  color: #666;
  letter-spacing: 4px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

:deep(.form-options .el-checkbox__label) {
  font-size: 12px;
  color: #666;
}

:deep(.form-options .el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #ff6b6b;
  border-color: #ff6b6b;
}

.forgot-link {
  font-size: 12px;
  color: #ff6b6b;
  text-decoration: none;
}

.login-btn {
  width: 100%;
  height: 40px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ffa8a8 100%);
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
}

.social-login {
  display: flex;
  align-items: center;
  gap: 16px;
}

.social-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  background: #fff;
  font-size: 12px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
}

.social-btn:hover {
  border-color: #ff6b6b;
  color: #ff6b6b;
}

.register-link {
  font-size: 12px;
  color: #ff6b6b;
  text-decoration: none;
  margin-left: auto;
}

.footer {
  text-align: center;
  padding: 30px 0;
  font-size: 12px;
  color: #999;
}

.footer p {
  margin: 6px 0;
}
</style>
