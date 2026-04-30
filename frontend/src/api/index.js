import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 请求拦截器：添加 token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    const data = response.data
    if (data.code === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
      return Promise.reject(new Error(data.message))
    }
    if (data.code !== 200) {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message))
    }
    return data
  },
  (error) => {
    ElMessage.error('网络错误，请稍后重试')
    return Promise.reject(error)
  }
)

// 认证
export const authApi = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
  getMe: () => api.get('/auth/me'),
  updateProfile: (data) => api.put('/auth/profile', data),
  getProfile: () => api.get('/auth/profile'),
}

// 商品
export const productApi = {
  list: (params) => api.get('/products', { params }),
  detail: (id) => api.get(`/products/${id}`),
  hot: (limit = 10) => api.get('/products/hot', { params: { limit } }),
  categories: () => api.get('/categories'),
}

// 购物车
export const cartApi = {
  list: () => api.get('/cart'),
  add: (productId, quantity = 1) => api.post(`/cart/${productId}`, null, { params: { quantity } }),
  update: (productId, quantity) => api.put(`/cart/${productId}`, null, { params: { quantity } }),
  remove: (productId) => api.delete(`/cart/${productId}`),
  clear: () => api.delete('/cart'),
}

// 订单
export const orderApi = {
  create: (data) => api.post('/orders', data),
  list: () => api.get('/orders'),
  detail: (id) => api.get(`/orders/${id}`),
  pay: (id) => api.put(`/orders/${id}/pay`),
  cancel: (id) => api.put(`/orders/${id}/cancel`),
}

// 收藏
export const favoriteApi = {
  add: (productId) => api.post(`/favorites/${productId}`),
  remove: (productId) => api.delete(`/favorites/${productId}`),
  list: (limit = 20) => api.get('/favorites', { params: { limit } }),
}

// 行为
export const behaviorApi = {
  record: (data) => api.post('/behavior', data),
  history: (limit = 20) => api.get('/behavior/history', { params: { limit } }),
}

// 推荐
export const recommendApi = {
  guess: (limit = 10) => api.get('/ai/recommend/guess', { params: { limit } }),
  hot: (limit = 10) => api.get('/ai/recommend/hot', { params: { limit } }),
  related: (productId, limit = 6) => api.get(`/ai/recommend/related/${productId}`, { params: { limit } }),
  semantic: (query, limit = 10) => api.post('/ai/recommend/semantic', { query, limit }),
  searchHistory: () => api.get('/ai/recommend/search-history'),
}

export default api
