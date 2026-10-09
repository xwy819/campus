import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  { path: '/login', component: () => import('../views/Login.vue') },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    children: [
      { path: '', redirect: '/home' },
      { path: 'home', component: () => import('../views/Home.vue'), meta: { title: '首页' } },
      { path: 'category', component: () => import('../views/Category.vue'), meta: { title: '分类' } },
      { path: 'product/:id', component: () => import('../views/ProductDetail.vue'), meta: { title: '商品详情' } },
      { path: 'publish', component: () => import('../views/Publish.vue'), meta: { title: '发布商品', auth: true } },
      { path: 'myProducts', component: () => import('../views/MyProducts.vue'), meta: { title: '我的发布', auth: true } },
      { path: 'product/edit/:id', component: () => import('../views/ProductEdit.vue'), meta: { title: '编辑商品', auth: true } },
      { path: 'myOrders', component: () => import('../views/MyOrders.vue'), meta: { title: '我的订单', auth: true } },
      { path: 'messages', component: () => import('../views/Messages.vue'), meta: { title: '站内消息', auth: true } },
      { path: 'notices', component: () => import('../views/Notices.vue'), meta: { title: '平台公告' } },
      { path: 'notices/:id', component: () => import('../views/NoticeDetail.vue'), meta: { title: '公告详情' } },
      { path: 'profile', component: () => import('../views/Profile.vue'), meta: { title: '个人中心', auth: true } },
      { path: 'profile/edit', component: () => import('../views/ProfileEdit.vue'), meta: { title: '编辑资料', auth: true } },
      { path: 'address', component: () => import('../views/Address.vue'), meta: { title: '地址管理', auth: true } },
      { path: 'password', component: () => import('../views/Password.vue'), meta: { title: '修改密码', auth: true } },
      { path: 'favorites', component: () => import('../views/Favorites.vue'), meta: { title: '我的收藏', auth: true } },
      { path: 'history', component: () => import('../views/History.vue'), meta: { title: '历史浏览', auth: true } },
      { path: 'settings', component: () => import('../views/Settings.vue'), meta: { title: '设置', auth: true } }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.auth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/home')
  } else {
    next()
  }
})

export default router
