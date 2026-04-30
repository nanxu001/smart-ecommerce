<template>
  <div class="login-page">
    <div class="login-card">
      <h1>🛒 智能电商推荐系统</h1>
      <p class="subtitle">基于 AI 的智能推荐引擎</p>
      
      <el-tabs v-model="activeTab" class="login-tabs">
        <el-tab-pane label="登录" name="login">
          <el-form :model="loginForm" @submit.prevent="handleLogin">
            <el-form-item>
              <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="User" size="large" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="Lock" 
                        size="large" show-password @keyup.enter="handleLogin" />
            </el-form-item>
            <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="handleLogin">
              登录
            </el-button>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="注册" name="register">
          <el-form :model="registerForm" @submit.prevent="handleRegister">
            <el-form-item>
              <el-input v-model="registerForm.username" placeholder="用户名" prefix-icon="User" size="large" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="registerForm.password" type="password" placeholder="密码" prefix-icon="Lock" 
                        size="large" show-password />
            </el-form-item>
            <el-form-item>
              <el-input v-model="registerForm.nickname" placeholder="昵称（可选）" prefix-icon="Avatar" size="large" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="registerForm.email" placeholder="邮箱（可选）" prefix-icon="Message" size="large" />
            </el-form-item>
            <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="handleRegister">
              注册
            </el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      
      <div class="demo-hint">
        <el-text type="info" size="small">演示账号：admin / 123456</el-text>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '../api'

const router = useRouter()
const activeTab = ref('login')
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
})

const registerForm = reactive({
  username: '',
  password: '',
  nickname: '',
  email: '',
})

const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await authApi.login(loginForm)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data.user))
    ElMessage.success('登录成功')
    router.push('/home')
  } catch (e) {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  if (!registerForm.username || !registerForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    await authApi.register(registerForm)
    ElMessage.success('注册成功，请登录')
    activeTab.value = 'login'
    loginForm.username = registerForm.username
    registerForm.username = ''
    registerForm.password = ''
    registerForm.nickname = ''
    registerForm.email = ''
  } catch (e) {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  width: 400px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);
}
.login-card h1 {
  text-align: center;
  color: #303133;
  margin-bottom: 8px;
}
.subtitle {
  text-align: center;
  color: #909399;
  margin-bottom: 30px;
  font-size: 14px;
}
.demo-hint {
  text-align: center;
  margin-top: 16px;
}
</style>
