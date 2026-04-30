<template>
  <div class="product-detail" v-if="product">
    <el-row :gutter="30">
      <el-col :span="12">
        <div class="product-image">
          <el-image :src="getImage(product)" fit="contain" style="width: 100%; height: 400px">
            <template #error>
              <div class="image-placeholder">📦</div>
            </template>
          </el-image>
        </div>
      </el-col>
      <el-col :span="12">
        <h1>{{ product.name }}</h1>
        <p class="description">{{ product.description }}</p>
        
        <div class="price-section">
          <span class="price">¥{{ product.price }}</span>
          <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
        </div>

        <div class="stats">
          <el-tag type="info" size="small">已售 {{ product.sales || 0 }}</el-tag>
          <el-tag type="info" size="small">浏览 {{ product.views || 0 }}</el-tag>
          <el-tag :type="product.stock > 0 ? 'success' : 'danger'" size="small">
            {{ product.stock > 0 ? '有货' : '缺货' }}
          </el-tag>
        </div>

        <div class="actions" style="margin-top: 24px">
          <el-button type="primary" size="large" @click="addToCart" :disabled="product.stock <= 0">
            🛒 加入购物车
          </el-button>
          <el-button type="danger" size="large" @click="toggleFavorite">
            {{ isFavorited ? '❤️ 已收藏' : '🤍 收藏' }}
          </el-button>
        </div>
      </el-col>
    </el-row>

    <!-- 相关推荐 -->
    <div class="related-section" v-if="relatedProducts.length > 0">
      <h2>💡 看了又看</h2>
      <ProductGrid :products="relatedProducts" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { productApi, recommendApi, cartApi, favoriteApi, behaviorApi } from '../api'
import ProductGrid from '../components/ProductGrid.vue'

const route = useRoute()
const product = ref(null)
const relatedProducts = ref([])
const isFavorited = ref(false)

onMounted(async () => {
  const id = route.params.id
  try {
    const [detailRes, relatedRes] = await Promise.all([
      productApi.detail(id),
      recommendApi.related(id, 6),
    ])
    product.value = detailRes.data
    relatedProducts.value = relatedRes.data || []
    
    // 记录浏览行为
    behaviorApi.record({ productId: Number(id), type: 'view' })
  } catch (e) {
    console.error(e)
  }
})

const getImage = (p) => {
  if (p.images && Array.isArray(p.images) && p.images.length > 0) return p.images[0]
  return ''
}

const addToCart = async () => {
  try {
    await cartApi.add(product.value.id, 1)
    ElMessage.success('已加入购物车')
  } catch (e) {
    console.error(e)
  }
}

const toggleFavorite = async () => {
  try {
    if (isFavorited.value) {
      await favoriteApi.remove(product.value.id)
      isFavorited.value = false
      ElMessage.info('已取消收藏')
    } else {
      await favoriteApi.add(product.value.id)
      isFavorited.value = true
      ElMessage.success('已收藏')
    }
  } catch (e) {
    console.error(e)
  }
}
</script>

<style scoped>
.product-detail {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
}
.product-detail h1 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 12px;
}
.description {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 20px;
}
.price-section {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 16px;
}
.price {
  font-size: 32px;
  font-weight: bold;
  color: #e74c3c;
}
.original-price {
  font-size: 16px;
  color: #999;
  text-decoration: line-through;
}
.stats {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}
.actions {
  display: flex;
  gap: 12px;
}
.related-section {
  margin-top: 40px;
  border-top: 1px solid #eee;
  padding-top: 20px;
}
.related-section h2 {
  font-size: 20px;
  margin-bottom: 16px;
}
.image-placeholder {
  font-size: 64px;
  color: #c0c4cc;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}
</style>
