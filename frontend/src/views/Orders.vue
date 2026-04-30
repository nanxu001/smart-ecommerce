<template>
  <div class="orders-page">
    <h2>📋 我的订单</h2>

    <el-card v-for="order in orders" :key="order.id" class="order-card">
      <div class="order-header">
        <span>订单号：{{ order.orderNo }}</span>
        <el-tag :type="statusType(order.status)">{{ statusText(order.status) }}</el-tag>
      </div>
      
      <div class="order-items">
        <div v-for="item in order.items" :key="item.productId" class="order-item">
          <span>{{ item.productName }}</span>
          <span>×{{ item.quantity }}</span>
          <span>¥{{ item.price }}</span>
        </div>
      </div>

      <div class="order-footer">
        <span class="order-time">{{ formatTime(order.createdAt) }}</span>
        <span class="order-total">合计：¥{{ order.totalAmount }}</span>
        <div class="order-actions">
          <el-button v-if="order.status === 'pending'" type="primary" size="small" @click="payOrder(order.id)">
            支付
          </el-button>
          <el-button v-if="order.status === 'pending'" type="danger" size="small" @click="cancelOrder(order.id)">
            取消
          </el-button>
        </div>
      </div>
    </el-card>

    <el-empty v-if="orders.length === 0" description="暂无订单" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { orderApi } from '../api'

const orders = ref([])

onMounted(async () => {
  try {
    const res = await orderApi.list()
    orders.value = res.data || []
  } catch (e) {
    console.error(e)
  }
})

const statusType = (status) => {
  const map = { pending: 'warning', paid: 'success', shipped: '', completed: 'info', cancelled: 'danger' }
  return map[status] || 'info'
}

const statusText = (status) => {
  const map = { pending: '待支付', paid: '已支付', shipped: '已发货', completed: '已完成', cancelled: '已取消' }
  return map[status] || status
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

const payOrder = async (id) => {
  try {
    await orderApi.pay(id)
    ElMessage.success('支付成功')
    const res = await orderApi.list()
    orders.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const cancelOrder = async (id) => {
  try {
    await orderApi.cancel(id)
    ElMessage.success('已取消')
    const res = await orderApi.list()
    orders.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}
</script>

<style scoped>
.orders-page h2 { margin-bottom: 20px; }
.order-card { margin-bottom: 16px; }
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
  margin-bottom: 12px;
}
.order-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  color: #606266;
}
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #eee;
  margin-top: 12px;
}
.order-time { color: #909399; font-size: 13px; }
.order-total { font-weight: bold; color: #e74c3c; }
.order-actions { display: flex; gap: 8px; }
</style>
