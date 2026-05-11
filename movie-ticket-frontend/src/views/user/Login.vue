<template>
  <div class="auth-page">
    <!-- 左侧品牌展示区 -->
    <div class="brand-section">
      <div class="brand-bg">
        <img src="@/assets/images/background2.jpg" alt="cinema background" class="bg-image" />
        <div class="bg-overlay"></div>
      </div>
      <div class="brand-content">
        <div class="brand-logo">
          <el-icon size="42" color="#fff"><VideoPlay /></el-icon>
          <span class="brand-name">影票通</span>
        </div>
        <h1 class="brand-title">在线票务平台</h1>
        <p class="brand-subtitle">智慧影院服务 · 一站式购票体验</p>

        <div class="feature-cards">
          <div class="feature-card">
            <div class="feature-icon">
              <el-icon size="24"><Ticket /></el-icon>
            </div>
            <div class="feature-text">
              <h4>在线选座</h4>
              <p>实时座位图 · 自由选择</p>
            </div>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <el-icon size="24"><Star /></el-icon>
            </div>
            <div class="feature-text">
              <h4>热映推荐</h4>
              <p>精选影片 · 智能推荐</p>
            </div>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <el-icon size="24"><CreditCard /></el-icon>
            </div>
            <div class="feature-text">
              <h4>便捷支付</h4>
              <p>多种方式 · 安全快捷</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧表单区 -->
    <div class="form-section">
      <div class="form-wrapper">
        <!-- Tab 切换 -->
        <div class="form-header">
          <h2 class="form-title">{{ activeTab === 'login' ? '账号登录' : '用户注册' }}</h2>
          <p class="form-subtitle">{{ activeTab === 'login' ? '请输入您的登录信息' : '创建您的账号' }}</p>
        </div>

        <div class="tab-switch">
          <button 
            class="tab-btn" 
            :class="{ active: activeTab === 'login' }"
            @click="activeTab = 'login'"
          >登录</button>
          <button 
            class="tab-btn" 
            :class="{ active: activeTab === 'register' }"
            @click="activeTab = 'register'"
          >注册</button>
          <div class="tab-indicator" :class="{ right: activeTab === 'register' }"></div>
        </div>

        <!-- 登录表单 -->
        <template v-if="activeTab === 'login'">
          <el-form 
            ref="formRef" 
            :model="form" 
            :rules="loginRules"
            class="auth-form"
            size="large"
          >
            <el-form-item prop="username">
              <el-autocomplete
                v-model="form.username"
                :fetch-suggestions="querySearch"
                placeholder="请输入用户名"
                class="form-input"
                :prefix-icon="User"
                clearable
                value-key="value"
                @select="selectUsername"
              />
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="请输入密码"
                class="form-input"
                :prefix-icon="Lock"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>
            
            <div class="form-options">
              <el-checkbox v-model="rememberMe">记住登录状态</el-checkbox>
              <a href="#" class="forgot-link">忘记密码?</a>
            </div>
            
            <el-button 
              type="primary" 
              class="submit-btn"
              :loading="loading"
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form>

          <div class="social-login">
            <div class="social-divider">
              <span>其他登录方式</span>
            </div>
            <div class="social-btns">
              <button class="social-btn wechat" @click="handleSocialLogin('wechat')">
                <el-icon size="20"><ChatDotRound /></el-icon>
              </button>
              <button class="social-btn qq" @click="handleSocialLogin('qq')">
                <el-icon size="20"><Message /></el-icon>
              </button>
            </div>
          </div>
        </template>

        <!-- 注册表单 -->
        <template v-else>
          <el-form 
            ref="registerFormRef" 
            :model="registerForm" 
            :rules="registerRules"
            class="auth-form"
            size="large"
          >
            <el-form-item prop="username">
              <el-input 
                v-model="registerForm.username" 
                placeholder="请输入用户名"
                class="form-input"
                :prefix-icon="User"
              />
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input 
                v-model="registerForm.password" 
                type="password" 
                placeholder="请输入密码"
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
                placeholder="请输入昵称"
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
              class="submit-btn"
              :loading="registerLoading"
              @click="handleRegister"
            >
              {{ registerLoading ? '注册中...' : '注 册' }}
            </el-button>
          </el-form>

          <div class="social-login">
            <div class="social-divider">
              <span>其他注册方式</span>
            </div>
            <div class="social-btns">
              <button class="social-btn wechat" @click="handleSocialLogin('wechat')">
                <el-icon size="20"><ChatDotRound /></el-icon>
              </button>
              <button class="social-btn qq" @click="handleSocialLogin('qq')">
                <el-icon size="20"><Message /></el-icon>
              </button>
            </div>
          </div>
        </template>

        <!-- 底部版权 -->
        <div class="form-footer">
          <p>&copy; 2026 影票通 在线票务平台</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Lock, ChatDotRound, Message, VideoPlay, Ticket, Star, CreditCard } from '@element-plus/icons-vue'
import { authAPI } from '@/api/api'

const router = useRouter()
const route = useRoute()
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

const selectUsername = (item) => {
  const username = item.value || item
  form.username = username
  const user = savedUsernames.value.find(u => u.username === username)
  if (user) {
    form.password = user.password
  }
}

const querySearch = (queryString, cb) => {
  if (!savedUsernames.value || savedUsernames.value.length === 0) {
    cb([])
    return
  }
  const str = (queryString || '').toLowerCase()
  const results = savedUsernames.value
    .filter(user => user && user.username && user.username.toLowerCase().includes(str))
    .map(user => ({ value: user.username }))
  cb(results)
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
      phone: registerForm.phone.trim() || null
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
      const msg = response.data.message || '注册失败'
      if (msg.includes('uk_phone') || msg.includes('phone') || msg.includes('ConstraintViolation')) {
        ElMessage.error('该手机号已被注册，请直接登录或使用其他手机号')
      } else if (msg.includes('uk_username') || msg.includes('username')) {
        ElMessage.error('该用户名已被使用，请更换用户名')
      } else {
        ElMessage.error(msg)
      }
    }
  } catch (error) {
    console.error('注册失败:', error)
    const errMsg = error.response?.data?.message || error.message || ''
    if (errMsg.includes('uk_phone') || errMsg.includes('phone') || errMsg.includes('ConstraintViolation')) {
      ElMessage.error('该手机号已被注册，请直接登录或使用其他手机号')
    } else if (errMsg.includes('uk_username') || errMsg.includes('username')) {
      ElMessage.error('该用户名已被使用，请更换用户名')
    } else {
      ElMessage.error('注册失败，请稍后重试')
    }
  }
  
  registerLoading.value = false
}

onMounted(() => {
  if (route.path === '/register') {
    activeTab.value = 'register'
  }
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
  display: flex;
  width: 100%;
}

/* ========== 左侧品牌区 ========== */
.brand-section {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  min-width: 480px;
}

.brand-bg {
  position: absolute;
  inset: 0;
}

.bg-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
}

.bg-overlay {
  position: absolute;
  inset: 0;
}

.brand-bg::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse at 20% 50%, rgba(255,255,255,0.08) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 20%, rgba(255,107,107,0.12) 0%, transparent 40%),
    radial-gradient(ellipse at 60% 80%, rgba(255,255,255,0.06) 0%, transparent 45%);
}

.brand-bg::after {
  content: '';
  position: absolute;
  inset: 0;
}

.brand-content {
  position: relative;
  z-index: 1;
  padding: 60px;
  color: #fff;
  max-width: 520px;
}

.brand-logo {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 48px;
}

.brand-name {
  font-size: 32px;
  font-weight: 700;
  letter-spacing: 2px;
}

.brand-title {
  font-size: 40px;
  font-weight: 800;
  line-height: 1.3;
  margin: 0 0 16px 0;
  letter-spacing: 2px;
}

.brand-subtitle {
  font-size: 18px;
  opacity: 0.85;
  margin: 0 0 56px 0;
  font-weight: 300;
  letter-spacing: 1px;
}

.feature-cards {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.feature-card {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 20px 24px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  transition: all 0.3s ease;
}

.feature-card:hover {
  background: rgba(255, 255, 255, 0.16);
  transform: translateX(8px);
}

.feature-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.feature-text h4 {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 4px 0;
}

.feature-text p {
  font-size: 13px;
  opacity: 0.75;
  margin: 0;
}

/* ========== 右侧表单区 ========== */
.form-section {
  flex: 0 0 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  padding: 40px;
}

.form-wrapper {
  width: 100%;
  max-width: 400px;
}

.form-header {
  margin-bottom: 36px;
}

.form-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0 0 8px 0;
}

.form-subtitle {
  font-size: 14px;
  color: #999;
  margin: 0;
}

/* Tab 切换 */
.tab-switch {
  display: flex;
  position: relative;
  background: #f5f6fa;
  border-radius: 10px;
  padding: 4px;
  margin-bottom: 32px;
}

.tab-btn {
  flex: 1;
  padding: 11px 0;
  border: none;
  background: transparent;
  font-size: 15px;
  font-weight: 600;
  color: #999;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.35s ease;
  position: relative;
  z-index: 1;
}

.tab-btn.active {
  color: #667eea;
}

.tab-indicator {
  position: absolute;
  top: 4px;
  left: 4px;
  width: calc(50% - 4px);
  height: calc(100% - 8px);
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.18);
  transition: transform 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.tab-indicator.right {
  transform: translateX(100%);
}

/* 表单 */
.auth-form {
  margin-bottom: 24px;
}

.form-input {
  width: 100%;
}

:deep(.form-input .el-input__wrapper),
:deep(.form-input .el-autocomplete__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e8e8e8 inset;
  padding: 4px 14px;
  transition: all 0.3s ease;
}

:deep(.form-input .el-input__wrapper:hover),
:deep(.form-input .el-autocomplete__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

:deep(.form-input .el-input__wrapper.is-focus),
:deep(.form-input .el-autocomplete__wrapper.is-focus) {
  box-shadow: 0 0 0 1.5px #667eea inset !important;
}

:deep(.el-form-item) {
  margin-bottom: 22px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 26px;
}

:deep(.form-options .el-checkbox__label) {
  font-size: 13px;
  color: #666;
}

:deep(.form-options .el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #667eea;
  border-color: #667eea;
}

.forgot-link {
  font-size: 13px;
  color: #667eea;
  text-decoration: none;
  transition: opacity 0.3s;
}

.forgot-link:hover {
  opacity: 0.75;
}

.submit-btn {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, #bfc2d0 0%, #8277a0 50%, #b87bdc 100%);
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  color: #fff;
  transition: all 0.3s ease;
  box-shadow: 0 4px 18px rgba(74, 99, 210, 0.4);
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 24px rgba(74, 99, 210, 0.5);
  background: linear-gradient(135deg, #556ce0 0%, #6a4fbb 50%, #8b52ae 100%);
}

.submit-btn:active {
  transform: translateY(0);
}

/* 社交登录 */
.social-login {
  margin-top: 8px;
}

.social-divider {
  text-align: center;
  position: relative;
  margin-bottom: 20px;
}

.social-divider::before,
.social-divider::after {
  content: '';
  position: absolute;
  top: 50%;
  width: calc(50% - 70px);
  height: 1px;
  background: #eee;
}

.social-divider::before {
  left: 0;
}

.social-divider::after {
  right: 0;
}

.social-divider span {
  font-size: 12px;
  color: #bbb;
  background: #fff;
  padding: 0 14px;
  position: relative;
}

.social-btns {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.social-btn {
  width: 46px;
  height: 46px;
  border-radius: 50%;
  border: 1.5px solid #e8e8e8;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #999;
}

.social-btn:hover {
  border-color: #667eea;
  color: #667eea;
  transform: scale(1.08);
}

.social-btn.wechat:hover {
  border-color: #07c160;
  color: #07c160;
}

.social-btn.qq:hover {
  border-color: #12b7f5;
  color: #12b7f5;
}

/* 底部 */
.form-footer {
  text-align: center;
  margin-top: 36px;
}

.form-footer p {
  font-size: 12px;
  color: #ccc;
  margin: 0;
}

/* 响应式 */
@media (max-width: 900px) {
  .brand-section {
    display: none;
  }

  .form-section {
    flex: 1;
    min-width: auto;
  }

  .auth-page {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }

  .form-section {
    background: transparent;
  }

  .form-wrapper {
    background: #fff;
    border-radius: 20px;
    padding: 44px 36px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.25);
  }
}
</style>
