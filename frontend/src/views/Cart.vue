<template>
  <div class="cart-page">
    <h2>🛒 购物车</h2>

    <el-card v-if="cartItems.length > 0">
      <el-table :data="cartItems" style="width: 100%">
        <el-table-column label="商品" min-width="300">
          <template #default="{ row }">
            <div class="cart-item-info" @click="$router.push(`/products/${row.productId}`)" style="cursor:pointer">
              <el-image :src="row.product?.images?.[0] || ''" fit="cover" style="width: 60px; height: 60px; border-radius: 8px; margin-right: 12px">
                <template #error><div style="width:60px;height:60px;background:#f5f7fa;border-radius:8px;display:flex;align-items:center;justify-content:center">📦</div></template>
              </el-image>
              <span>{{ row.product?.name || `商品 #${row.productId}` }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="120">
          <template #default="{ row }">¥{{ row.product?.price || 0 }}</template>
        </el-table-column>
        <el-table-column label="数量" width="150">
          <template #default="{ row }">
            <el-input-number v-model="row.quantity" :min="1" :max="99" size="small"
                             @change="updateQuantity(row)" />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="{ row }">
            <span style="color: #e74c3c; font-weight: bold">
              ¥{{ ((row.product?.price || 0) * row.quantity).toFixed(2) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button type="danger" text @click="removeItem(row.productId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="cart-footer">
        <div class="total">
          合计：<span class="total-price">¥{{ totalPrice }}</span>
        </div>
        <el-button type="primary" size="large" @click="checkout">去结算</el-button>
      </div>
    </el-card>

    <el-empty v-else description="购物车空空如也~" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cartApi, productApi, orderApi } from '../api'

const router = useRouter()
const cartItems = ref([])
const productsMap = ref({})

onMounted(async () => {
  await loadCart()
})

const loadCart = async () => {
  try {
    const res = await cartApi.list()
    cartItems.value = res.data || []
    // 加载商品信息
    for (const item of cartItems.value) {
      if (!productsMap.value[item.productId]) {
        try {
          const pRes = await productApi.detail(item.productId)
          productsMap.value[item.productId] = pRes.data
          item.product = pRes.data
        } catch (e) {
          // ignore
        }
      } else {
        item.product = productsMap.value[item.productId]
      }
    }
  } catch (e) {
    console.error(e)
  }
}

const totalPrice = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    return sum + (item.product?.price || 0) * item.quantity
  }, 0).toFixed(2)
})

const updateQuantity = async (item) => {
  try {
    await cartApi.update(item.productId, item.quantity)
  } catch (e) {
    console.error(e)
  }
}

const removeItem = async (productId) => {
  try {
    await cartApi.remove(productId)
    ElMessage.success('已移除')
    await loadCart()
  } catch (e) {
    console.error(e)
  }
}

const checkout = async () => {
  try {
    const cartIds = cartItems.value.map(item => item.id)
    const res = await orderApi.create({ cartIds, address: '默认地址' })
    ElMessage.success('订单创建成功！')
    await orderApi.pay(res.data.id)
    router.push('/orders')
  } catch (e) {
    console.error(e)
  }
}
</script>

<style scoped>
.cart-page h2 {
  margin-bottom: 20px;
}
.cart-item-info {
  display: flex;
  align-items: center;
}
.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}
.total-price {
  font-size: 24px;
  font-weight: bold;
  color: #e74c3c;
}
</style>
