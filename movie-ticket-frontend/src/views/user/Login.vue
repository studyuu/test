<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-left">
        <div class="brand-section">
          <el-icon size="64" color="#ff6b6b"><VideoPlay /></el-icon>
          <h1 class="brand-title">影票通</h1>
          <p class="brand-slogan">发现好电影，享受好时光</p>
        </div>
        <div class="feature-list">
          <div class="feature-item">
            <el-icon size="24" color="#667eea"><Ticket /></el-icon>
            <span>在线选座，快捷购票</span>
          </div>
          <div class="feature-item">
            <el-icon size="24" color="#667eea"><Discount /></el-icon>
            <span>优惠多多，省钱省心</span>
          </div>
          <div class="feature-item">
            <el-icon size="24" color="#667eea"><Star /></el-icon>
            <span>海量影片，精彩不断</span>
          </div>
        </div>
      </div>
      
      <div class="auth-right">
        <div class="auth-box">
          <h2 class="auth-title">欢迎登录</h2>
          <p class="auth-subtitle">登录后即可享受更多服务</p>
          
          <el-form 
            ref="formRef" 
            :model="form" 
            :rules="rules"
            class="auth-form"
            size="large"
          >
            <el-form-item prop="username">
              <el-input 
                v-model="form.username" 
                placeholder="请输入用户名"
                :prefix-icon="User"
                clearable
              />
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="请输入密码"
                :prefix-icon="Lock"
                show-password
                clearable
                @keyup.enter="handleLogin"
              />
            </el-form-item>
            
            <div class="form-options">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
              <a href="#" class="forgot-link">忘记密码？</a>
            </div>
            
            <el-form-item>
              <el-button 
                type="primary" 
                class="submit-btn"
                :loading="loading"
                @click="handleLogin"
              >
                立即登录
              </el-button>
            </el-form-item>
          </el-form>
          
          <div class="auth-footer">
            <span>还没有账号？</span>
            <router-link to="/register" class="register-link">立即注册</router-link>
          </div>
          
          <div class="divider">
            <span>其他登录方式</span>
          </div>
          
          <div class="social-login">
            <div class="social-btn wechat">
              <el-icon size="24"><ChatDotRound /></el-icon>
              <span>微信</span>
            </div>
            <div class="social-btn qq">
              <el-icon size="24"><Message /></el-icon>
              <span>QQ</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Lock, ChatDotRound, Message } from '@element-plus/icons-vue'
import { authAPI } from '@/api/api'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
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
      userStore.setToken('mock_token_' + Date.now())
      userStore.setUserInfo(response.data.data)
      ElMessage.success('登录成功')
      router.push('/home')
    } else {
      ElMessage.error(response.data.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error('登录失败，请稍后重试')
  }
  
  loading.value = false
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.auth-container {
  display: flex;
  width: 1000px;
  min-height: 600px;
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.auth-left {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: #fff;
}

.brand-section {
  text-align: center;
  margin-bottom: 60px;
}

.brand-title {
  font-size: 42px;
  font-weight: bold;
  margin-top: 20px;
  margin-bottom: 10px;
}

.brand-slogan {
  font-size: 18px;
  opacity: 0.9;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 16px;
}

.auth-right {
  flex: 1;
  padding: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.auth-box {
  width: 100%;
  max-width: 360px;
}

.auth-title {
  font-size: 32px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
  text-align: center;
}

.auth-subtitle {
  font-size: 14px;
  color: #999;
  text-align: center;
  margin-bottom: 40px;
}

.auth-form {
  margin-bottom: 24px;
}

:deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 4px 15px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.forgot-link {
  color: #667eea;
  text-decoration: none;
  font-size: 14px;
}

.forgot-link:hover {
  color: #764ba2;
}

.submit-btn {
  width: 100%;
  height: 48px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.submit-btn:hover {
  opacity: 0.9;
}

.auth-footer {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.register-link {
  color: #667eea;
  text-decoration: none;
  font-weight: bold;
  margin-left: 8px;
}

.register-link:hover {
  color: #764ba2;
}

.divider {
  display: flex;
  align-items: center;
  margin: 30px 0;
  color: #999;
  font-size: 14px;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #e0e0e0;
}

.divider span {
  padding: 0 16px;
}

.social-login {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.social-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 24px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e0e0e0;
}

.social-btn:hover {
  background: #f5f5f5;
}

.social-btn span {
  font-size: 14px;
  color: #666;
}

@media (max-width: 900px) {
  .auth-container {
    flex-direction: column;
    width: 100%;
    max-width: 480px;
  }
  
  .auth-left {
    padding: 40px;
    min-height: 200px;
  }
  
  .brand-section {
    margin-bottom: 30px;
  }
  
  .feature-list {
    display: none;
  }
  
  .auth-right {
    padding: 40px;
  }
}
</style>
