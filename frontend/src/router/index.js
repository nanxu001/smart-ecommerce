import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('../views/Home.vue'),
      },
      {
        path: 'products',
        name: 'Products',
        component: () => import('../views/Products.vue'),
      },
      {
        path: 'products/:id',
        name: 'ProductDetail',
        component: () => import('../views/ProductDetail.vue'),
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('../views/Cart.vue'),
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('../views/Orders.vue'),
      },
      {
        path: 'recommend',
        name: 'Recommend',
        component: () => import('../views/Recommend.vue'),
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue'),
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 导航守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
