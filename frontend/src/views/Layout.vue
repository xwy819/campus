<template>
  <div class="layout">
    <header class="topbar">
      <div class="brand" @click="$router.push('/home')">
        <svg viewBox="0 0 32 32" width="26" height="26" aria-hidden="true">
          <path d="M6 12 L16 6 L26 12 L26 22 L16 26 L6 22 Z"
                fill="none" stroke="#60A5FA" stroke-width="2" stroke-linejoin="round"/>
          <path d="M11 16 L21 16 M16 12 L16 20"
                stroke="#60A5FA" stroke-width="2" stroke-linecap="round"/>
        </svg>
        <span>校园市集</span>
      </div>

      <nav class="nav">
        <router-link v-for="m in menus" :key="m.path" :to="m.path"
                     class="nav-item" :class="{ active: isActive(m.path) }">
          {{ m.label }}
          <span v-if="m.path === '/messages' && unreadCount > 0" class="unread-dot">
            {{ unreadCount > 99 ? '99+' : unreadCount }}
          </span>
        </router-link>
      </nav>

      <div class="right-actions">
        <button class="publish-btn" @click="$router.push('/publish')">
          <svg viewBox="0 0 24 24" width="16" height="16" aria-hidden="true">
            <path d="M12 5v14M5 12h14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          发布闲置
        </button>

        <el-dropdown trigger="click" @command="handleCommand">
          <div class="user-trigger">
            <div class="avatar">
              <img v-if="avatar" :src="avatar" class="avatar-img" />
              <span v-else>{{ (name || 'U').charAt(0) }}</span>
            </div>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人主页</el-dropdown-item>
              <el-dropdown-item command="favorites">我的收藏</el-dropdown-item>
              <el-dropdown-item command="history">历史浏览</el-dropdown-item>
              <el-dropdown-item command="myProducts">我发布的</el-dropdown-item>
              <el-dropdown-item command="orders">我的订单</el-dropdown-item>
              <el-dropdown-item command="settings">设置</el-dropdown-item>
              <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <main class="main">
      <router-view />
    </main>

    <!-- 回到顶部 -->
    <button class="back-top" :class="{ show: showTop }" @click="scrollToTop" aria-label="回到顶部">
      <svg viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
        <path d="M6 15l6-6 6 6" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </button>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import request from '../utils/request'
import { userStore, clearUser } from '../store/user'

const router = useRouter()
const route = useRoute()
const name = computed(() => userStore.name || '未登录')
const avatar = computed(() => userStore.avatar)
const unreadCount = ref(0)
const menus = [
  { path: '/home', label: '首页' },
  { path: '/category', label: '分类' },
  { path: '/notices', label: '公告' },
  { path: '/messages', label: '消息' }
]

const isActive = (path) => route.path === path

// Layout 挂载时拉一次完整用户信息，确保 avatar/name 同步
const loadUserInfo = async () => {
  if (!localStorage.getItem('token')) return
  try {
    const res = await request.get('/user/profile')
    const data = res.data
    if (data.avatar) {
      userStore.avatar = data.avatar
      localStorage.setItem('avatar', data.avatar)
    }
    if (data.nickname && !userStore.name) {
      userStore.name = data.nickname
      localStorage.setItem('name', data.nickname)
    }
  } catch (e) { /* token 过期等，静默 */ }
}

const loadUnread = async () => {
  if (!localStorage.getItem('token')) {
    unreadCount.value = 0
    return
  }
  try {
    const res = await request.get('/message/unread')
    unreadCount.value = res.data || 0
  } catch (e) {
    unreadCount.value = 0
  }
}

// 路由变化时刷新未读数；进入 /profile 时重新拉用户信息
watch(() => route.path, (p) => {
  loadUnread()
  if (p === '/profile') loadUserInfo()
})

const handleCommand = (cmd) => {
  const map = {
    profile: '/profile',
    favorites: '/favorites',
    history: '/history',
    myProducts: '/myProducts',
    orders: '/myOrders',
    settings: '/settings'
  }
  if (cmd === 'logout') {
    clearUser()
    router.push('/login')
  } else if (map[cmd]) {
    router.push(map[cmd])
  }
}

onMounted(loadUnread)
onMounted(loadUserInfo)

/* —— 回到顶部 —— */
const showTop = ref(false)
const onScroll = () => { showTop.value = window.scrollY >= 300 }
const scrollToTop = () => { window.scrollTo({ top: 0, behavior: 'smooth' }) }
onMounted(() => window.addEventListener('scroll', onScroll, { passive: true }))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<style scoped>
.layout { min-height: 100vh; background: var(--bg-page); }

/* ============ 顶栏 ============ */
.topbar {
  height: 60px;
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: saturate(180%) blur(12px);
  -webkit-backdrop-filter: saturate(180%) blur(12px);
  padding: 0 32px;
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1px solid var(--border-light);
  transition: box-shadow var(--transition-base);
}

/* ============ Brand ============ */
.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 17px;
  font-weight: 600;
  letter-spacing: 1px;
  cursor: pointer;
  margin-right: 48px;
  color: var(--text-primary);
  transition: transform var(--transition-base);
}
.brand:hover { transform: scale(1.02); }
.brand svg { transition: transform var(--transition-base); }
.brand:hover svg { transform: rotate(-6deg); }

/* ============ Nav ============ */
.nav { display: flex; gap: 4px; }

.nav-item {
  position: relative;
  padding: 8px 18px;
  font-size: 15px;
  color: var(--ink-400);
  text-decoration: none;
  border-radius: var(--radius-md);
  transition: all var(--transition-base);
  font-weight: 500;
}
.nav-item:hover {
  color: var(--brand-500);
  background: var(--brand-50);
}
.nav-item.active {
  color: var(--brand-500);
  font-weight: 600;
  background: var(--brand-50);
}
.nav-item.active::after {
  content: '';
  position: absolute;
  left: 18px;
  right: 18px;
  bottom: 2px;
  height: 3px;
  background: linear-gradient(90deg, var(--brand-400), var(--brand-500));
  border-radius: 2px;
}

/* ============ 未读红点 ============ */
.unread-dot {
  position: absolute;
  top: 2px;
  right: 6px;
  min-width: 17px;
  height: 17px;
  padding: 0 5px;
  background: var(--danger);
  color: #fff;
  font-size: 10px;
  font-weight: 700;
  border-radius: 9px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
  animation: dotPop 0.3s ease;
  box-shadow: 0 0 0 2px #fff;
}
@keyframes dotPop {
  0%   { transform: scale(0.6); opacity: 0; }
  60%  { transform: scale(1.15); opacity: 1; }
  100% { transform: scale(1); }
}

/* ============ 右侧 ============ */
.right-actions {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 16px;
}

.publish-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 36px;
  padding: 0 16px;
  background: var(--brand-50);
  color: var(--brand-600);
  border: 1px solid var(--brand-200);
  border-radius: var(--radius-md);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-base);
}
.publish-btn:hover {
  background: linear-gradient(135deg, var(--brand-400), var(--brand-500));
  border-color: transparent;
  color: #fff;
  box-shadow: var(--shadow-brand-glow);
  transform: translateY(-1px);
}

.user-trigger {
  display: flex;
  align-items: center;
  cursor: pointer;
}
.avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--brand-400), var(--brand-500));
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  transition: transform var(--transition-base);
  box-shadow: var(--shadow-brand-glow);
}
.user-trigger:hover .avatar { transform: scale(1.08); }
.avatar-img { width: 100%; height: 100%; object-fit: cover; border-radius: 50%; }

/* ============ Main ============ */
.main { padding: 20px; max-width: 1280px; margin: 0 auto; }

/* ============ Responsive ============ */
@media (max-width: 768px) {
  .topbar { padding: 0 16px; }
  .brand { margin-right: 12px; font-size: 15px; }
  .nav-item { padding: 6px 10px; font-size: 13px; }
  .publish-btn span { display: none; }
  .publish-btn { padding: 0 10px; }
}

/* ============ 回到顶部 ============ */
.back-top {
  position: fixed;
  bottom: 32px;
  right: 32px;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: none;
  background: #FFFFFF;
  color: #2578F5;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  opacity: 0;
  pointer-events: none;
  transform: translateY(10px);
  transition: all 0.3s ease;
  z-index: 999;
}
.back-top.show {
  opacity: 1;
  pointer-events: auto;
  transform: translateY(0);
}
.back-top:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}
</style>
