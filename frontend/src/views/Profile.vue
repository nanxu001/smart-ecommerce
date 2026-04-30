<template>
  <div class="profile-page">
    <h2>👤 个人中心</h2>

    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>基本信息</template>
          <div class="profile-info">
            <el-avatar :size="64">{{ user?.nickname?.charAt(0) || 'U' }}</el-avatar>
            <h3>{{ user?.nickname || user?.username }}</h3>
            <el-text type="info">{{ user?.email || '未设置邮箱' }}</el-text>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card>
          <template #header>编辑资料</template>
          <el-form :model="form" label-width="80px">
            <el-form-item label="昵称">
              <el-input v-model="form.nickname" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" />
            </el-form-item>
            <el-form-item label="手机">
              <el-input v-model="form.phone" />
            </el-form-item>
            <el-form-item label="性别">
              <el-select v-model="form.gender" style="width: 100%">
                <el-option label="男" value="male" />
                <el-option label="女" value="female" />
              </el-select>
            </el-form-item>
            <el-form-item label="年龄段">
              <el-select v-model="form.ageRange" style="width: 100%">
                <el-option label="18-25" value="18-25" />
                <el-option label="25-35" value="25-35" />
                <el-option label="35-45" value="35-45" />
                <el-option label="45+" value="45+" />
              </el-select>
            </el-form-item>
            <el-form-item label="消费水平">
              <el-select v-model="form.budgetLevel" style="width: 100%">
                <el-option label="经济实惠" value="low" />
                <el-option label="中等" value="medium" />
                <el-option label="高端" value="high" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile" :loading="saving">保存</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <!-- 浏览历史 -->
    <el-card style="margin-top: 20px">
      <template #header>浏览历史</template>
      <ProductGrid :products="historyProducts" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { authApi, behaviorApi } from '../api'
import ProductGrid from '../components/ProductGrid.vue'

const user = ref(null)
const historyProducts = ref([])
const saving = ref(false)

const form = reactive({
  nickname: '',
  email: '',
  phone: '',
  gender: '',
  ageRange: '',
  budgetLevel: '',
})

onMounted(async () => {
  try {
    const [userRes, historyRes] = await Promise.all([
      authApi.getMe(),
      behaviorApi.history(12),
    ])
    user.value = userRes.data
    form.nickname = userRes.data.nickname || ''
    form.email = userRes.data.email || ''
    form.phone = userRes.data.phone || ''
    historyProducts.value = historyRes.data || []
  } catch (e) {
    console.error(e)
  }
})

const saveProfile = async () => {
  saving.value = true
  try {
    await authApi.updateProfile(form)
    ElMessage.success('保存成功')
    const userRes = await authApi.getMe()
    user.value = userRes.data
    localStorage.setItem('user', JSON.stringify(userRes.data))
  } catch (e) {
    console.error(e)
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.profile-page h2 { margin-bottom: 20px; }
.profile-info {
  text-align: center;
  padding: 20px 0;
}
.profile-info h3 {
  margin: 12px 0 4px;
}
</style>
