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
          <h2 class="auth-title">注册账号</h2>
          <p class="auth-subtitle">填写信息，开启观影之旅</p>
          
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
            
            <el-form-item prop="nickname">
              <el-input 
                v-model="form.nickname" 
                placeholder="请输入昵称"
                :prefix-icon="Avatar"
                clearable
              />
            </el-form-item>
            
            <el-form-item prop="phone">
              <el-input 
                v-model="form.phone" 
                placeholder="请输入手机号"
                :prefix-icon="Phone"
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
              />
            </el-form-item>
            
            <el-form-item prop="confirmPassword">
              <el-input 
                v-model="form.confirmPassword" 
                type="password" 
                placeholder="请确认密码"
                :prefix-icon="Lock"
                show-password
                clearable
                @keyup.enter="handleRegister"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="primary" 
                class="submit-btn"
                :loading="loading"
                @click="handleRegister"
              >
                立即注册
              </el-button>
            </el-form-item>
          </el-form>
          
          <div class="auth-footer">
            <span>已有账号？</span>
            <router-link to="/login" class="login-link">立即登录</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Phone, Avatar } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  nickname: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  
  setTimeout(() => {
    ElMessage.success('注册成功，请登录')
    router.push('/login')
    loading.value = false
  }, 1000)
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

.login-link {
  color: #667eea;
  text-decoration: none;
  font-weight: bold;
  margin-left: 8px;
}

.login-link:hover {
  color: #764ba2;
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
