<template>
  <div class="product-grid">
    <el-row :gutter="16">
      <el-col v-for="product in products" :key="product.id" :xs="12" :sm="8" :md="6" :lg="6">
        <el-card shadow="hover" class="product-card" @click="$router.push(`/products/${product.id}`)">
          <div class="product-image">
            <el-image :src="getImage(product)" fit="cover" lazy>
              <template #error>
                <div class="image-placeholder">📦</div>
              </template>
            </el-image>
          </div>
          <div class="product-info">
            <h3 class="product-name">{{ product.name }}</h3>
            <div class="product-price">
              <span class="price">¥{{ product.price }}</span>
              <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
            </div>
            <div class="product-stats">
              <span>已售 {{ product.sales || 0 }}</span>
              <span>👁 {{ product.views || 0 }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-if="!loading && products && products.length === 0" :description="loadingText || '暂无商品'" />
  </div>
</template>

<script setup>
const props = defineProps({
  products: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  loadingText: { type: String, default: '暂无商品' },
})

const getImage = (product) => {
  if (product.images && Array.isArray(product.images) && product.images.length > 0) {
    return product.images[0]
  }
  return ''
}
</script>

<style scoped>
.product-grid {
  margin-bottom: 20px;
}
.product-card {
  margin-bottom: 16px;
  cursor: pointer;
  transition: transform 0.2s;
}
.product-card:hover {
  transform: translateY(-4px);
}
.product-card :deep(.el-card__body) {
  padding: 0;
}
.product-image {
  height: 180px;
  overflow: hidden;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}
.product-image :deep(.el-image) {
  width: 100%;
  height: 100%;
}
.image-placeholder {
  font-size: 48px;
  color: #c0c4cc;
}
.product-info {
  padding: 12px;
}
.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.product-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 6px;
}
.price {
  font-size: 18px;
  font-weight: bold;
  color: #e74c3c;
}
.original-price {
  font-size: 12px;
  color: #999;
  text-decoration: line-through;
}
.product-stats {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #909399;
}
</style>
