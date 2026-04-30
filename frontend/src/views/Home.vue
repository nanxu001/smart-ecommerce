<template>
  <div class="home">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="searchQuery" placeholder="🤖 用自然语言描述你想要的商品..." size="large"
                @keyup.enter="semanticSearch" class="search-input">
        <template #append>
          <el-button @click="semanticSearch">AI 搜索</el-button>
        </template>
      </el-input>
    </div>

    <!-- 分类导航 -->
    <div class="categories">
      <el-tag v-for="cat in categories" :key="cat.id" 
              :effect="selectedCat === cat.id ? 'dark' : 'plain'"
              @click="goCategory(cat.id)" class="cat-tag">
        {{ cat.icon }} {{ cat.name }}
      </el-tag>
    </div>

    <!-- 猜你喜欢 -->
    <div class="section">
      <div class="section-header">
        <h2>🎯 猜你喜欢</h2>
        <el-text type="info" size="small">基于你的浏览行为智能推荐</el-text>
      </div>
      <ProductGrid :products="guessProducts" loading-text="正在分析你的喜好..." />
    </div>

    <!-- 热门推荐 -->
    <div class="section">
      <div class="section-header">
        <h2>🔥 热门推荐</h2>
      </div>
      <ProductGrid :products="hotProducts" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { recommendApi, productApi, behaviorApi } from '../api'
import ProductGrid from '../components/ProductGrid.vue'

const router = useRouter()
const searchQuery = ref('')
const categories = ref([])
const guessProducts = ref([])
const hotProducts = ref([])
const selectedCat = ref(null)

onMounted(async () => {
  try {
    const [catRes, guessRes, hotRes] = await Promise.all([
      productApi.categories(),
      recommendApi.guess(8),
      recommendApi.hot(8),
    ])
    categories.value = catRes.data
    guessProducts.value = guessRes.data || []
    hotProducts.value = hotRes.data || []
  } catch (e) {
    console.error(e)
  }
})

const semanticSearch = async () => {
  if (!searchQuery.value.trim()) return
  try {
    const res = await recommendApi.semantic(searchQuery.value, 20)
    // 记录搜索行为
    behaviorApi.record({ type: 'search', searchQuery: searchQuery.value })
    // 跳转到商品列表并展示结果
    router.push({ path: '/products', query: { q: searchQuery.value } })
  } catch (e) {
    console.error(e)
  }
}

const goCategory = (catId) => {
  selectedCat.value = selectedCat.value === catId ? null : catId
  router.push({ path: '/products', query: { categoryId: catId } })
}
</script>

<style scoped>
.home {
  padding-bottom: 40px;
}
.search-bar {
  margin-bottom: 20px;
}
.search-input {
  max-width: 700px;
}
.categories {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 30px;
}
.cat-tag {
  cursor: pointer;
  font-size: 14px;
  padding: 8px 16px;
}
.section {
  margin-bottom: 40px;
}
.section-header {
  margin-bottom: 16px;
}
.section-header h2 {
  font-size: 22px;
  color: #303133;
  margin-bottom: 4px;
}
</style>
