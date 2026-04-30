<template>
  <div class="layout">
    <el-header class="header">
      <div class="header-content">
        <div class="logo" @click="$router.push('/home')">🛒 智能商城</div>
        <el-menu mode="horizontal" :default-active="$route.path" router class="nav-menu">
          <el-menu-item index="/home">首页</el-menu-item>
          <el-menu-item index="/products">商品</el-menu-item>
          <el-menu-item index="/recommend">🤖 AI推荐</el-menu-item>
          <el-menu-item index="/cart">
            <el-badge :value="cartCount" :hidden="cartCount === 0">
              购物车
            </el-badge>
          </el-menu-item>
          <el-menu-item index="/orders">订单</el-menu-item>
        </el-menu>
        <div class="user-area">
          <el-dropdown @command="handleCommand">
            <span class="user-name">
              <el-avatar :size="28">{{ user?.nickname?.charAt(0) || 'U' }}</el-avatar>
              {{ user?.nickname || '用户' }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>
    <el-main class="main">
      <router-view />
    </el-main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const user = computed(() => {
  const u = localStorage.getItem('user')
  return u ? JSON.parse(u) : null
})
const cartCount = ref(0)

onMounted(() => {
  loadCartCount()
})

const loadCartCount = async () => {
  try {
    const { cartApi } = await import('../api')
    const res = await cartApi.list()
    cartCount.value = res.data?.length || 0
  } catch (e) {
    cartCount.value = 0
  }
}

const handleCommand = (cmd) => {
  if (cmd === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    router.push('/login')
  } else if (cmd === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
}
.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  padding: 0;
  border-bottom: 1px solid #eee;
}
.header-content {
  display: flex;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  height: 60px;
}
.logo {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  cursor: pointer;
  margin-right: 20px;
  white-space: nowrap;
}
.nav-menu {
  flex: 1;
  border-bottom: none !important;
}
.user-area {
  margin-left: 20px;
}
.user-name {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #606266;
}
.main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
</style>
