<template>
  <div class="products-page">
    <!-- 搜索和筛选 -->
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索商品..." prefix-icon="Search" 
                style="width: 300px" @keyup.enter="loadProducts" />
      <el-select v-model="sortBy" placeholder="排序" style="width: 140px" @change="loadProducts">
        <el-option label="最新" value="default" />
        <el-option label="销量" value="sales" />
        <el-option label="价格↑" value="price_asc" />
        <el-option label="价格↓" value="price_desc" />
      </el-select>
    </div>

    <!-- 商品列表 -->
    <ProductGrid :products="products" />

    <!-- 分页 -->
    <el-pagination v-model:current-page="page" :page-size="12" :total="total" 
                   layout="prev, pager, next" @current-change="loadProducts" class="pagination" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { productApi, recommendApi, behaviorApi } from '../api'
import ProductGrid from '../components/ProductGrid.vue'

const route = useRoute()
const products = ref([])
const keyword = ref('')
const sortBy = ref('default')
const page = ref(1)
const total = ref(0)

onMounted(async () => {
  // 检查是否是语义搜索结果
  if (route.query.q) {
    keyword.value = route.query.q
    try {
      const res = await recommendApi.semantic(route.query.q, 20)
      products.value = res.data || []
      total.value = products.value.length
    } catch (e) {
      console.error(e)
    }
  } else if (route.query.categoryId) {
    // 按分类加载
    loadProducts()
  } else {
    loadProducts()
  }
})

const loadProducts = async () => {
  try {
    const params = {
      page: page.value,
      size: 12,
      keyword: keyword.value || undefined,
      sortBy: sortBy.value !== 'default' ? sortBy.value : undefined,
    }
    if (route.query.categoryId) {
      params.categoryId = route.query.categoryId
    }
    const res = await productApi.list(params)
    products.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  }
}
</script>

<style scoped>
.products-page {
  padding-bottom: 40px;
}
.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  align-items: center;
}
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
