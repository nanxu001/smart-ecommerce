<template>
  <div class="recommend-page">
    <h1>🤖 AI 智能推荐</h1>
    <p class="subtitle">用自然语言描述你想要的商品，AI 帮你找到最合适的</p>

    <!-- 语义搜索 -->
    <div class="semantic-search">
      <el-input v-model="query" type="textarea" :rows="3" 
                placeholder="例如：帮我挑适合送女友的生日礼物，预算500以内&#10;或者：推荐一款性价比高的笔记本电脑"
                @keyup.ctrl.enter="semanticSearch" />
      <el-button type="primary" size="large" @click="semanticSearch" :loading="loading" style="margin-top: 12px">
        🔍 AI 推荐
      </el-button>
    </div>

    <!-- 推荐结果 -->
    <div v-if="semanticResults.length > 0" class="results-section">
      <div class="results-header">
        <h2>推荐结果（{{ semanticResults.length }} 件）</h2>
      </div>
      <ProductGrid :products="semanticResults" />
    </div>

    <!-- 搜索历史 -->
    <div v-if="searchHistory.length > 0" class="history-section">
      <h3>📝 搜索历史</h3>
      <el-tag v-for="h in searchHistory" :key="h" @click="quickSearch(h)" 
              class="history-tag" effect="plain" size="large">
        {{ h }}
      </el-tag>
    </div>

    <!-- 快捷推荐 -->
    <div class="quick-recommend">
      <h3>💡 试试这些</h3>
      <div class="quick-tags">
        <el-tag v-for="q in quickQueries" :key="q" @click="quickSearch(q)" 
                class="quick-tag" effect="dark" size="large">
          {{ q }}
        </el-tag>
      </div>
    </div>

    <!-- 热门排行 -->
    <div class="hot-section">
      <h3>🔥 热门排行</h3>
      <ProductGrid :products="hotProducts" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { recommendApi, behaviorApi } from '../api'
import ProductGrid from '../components/ProductGrid.vue'

const query = ref('')
const loading = ref(false)
const semanticResults = ref([])
const searchHistory = ref([])
const hotProducts = ref([])

const quickQueries = [
  '送女友的生日礼物，预算500以内',
  '性价比高的笔记本电脑',
  '适合送父母的保健品',
  '夏天穿的清爽男装',
  '好用的护肤品套装',
  '智能家居好物推荐',
  '适合程序员的书籍',
  '情人节礼物推荐',
]

onMounted(async () => {
  try {
    const [hotRes, historyRes] = await Promise.all([
      recommendApi.hot(8),
      recommendApi.searchHistory(),
    ])
    hotProducts.value = hotRes.data || []
    searchHistory.value = historyRes.data || []
  } catch (e) {
    console.error(e)
  }
})

const semanticSearch = async () => {
  if (!query.value.trim()) return
  loading.value = true
  try {
    const res = await recommendApi.semantic(query.value, 20)
    semanticResults.value = res.data || []
    // 记录搜索行为
    behaviorApi.record({ type: 'search', searchQuery: query.value })
    // 刷新搜索历史
    const historyRes = await recommendApi.searchHistory()
    searchHistory.value = historyRes.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const quickSearch = (q) => {
  query.value = q
  semanticSearch()
}
</script>

<style scoped>
.recommend-page {
  padding-bottom: 40px;
}
.recommend-page h1 {
  font-size: 28px;
  margin-bottom: 8px;
}
.subtitle {
  color: #909399;
  margin-bottom: 24px;
  font-size: 15px;
}
.semantic-search {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}
.results-section {
  margin-bottom: 30px;
}
.results-header h2 {
  margin-bottom: 16px;
  color: #303133;
}
.history-section, .quick-recommend, .hot-section {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}
.history-section h3, .quick-recommend h3, .hot-section h3 {
  margin-bottom: 12px;
  color: #303133;
}
.history-tag {
  margin: 4px;
  cursor: pointer;
}
.quick-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.quick-tag {
  cursor: pointer;
  font-size: 13px;
  padding: 10px 16px;
}
</style>
