<template>
  <div class="page page-fade-in" v-if="notice">
    <!-- 返回栏 -->
    <header class="back-bar">
      <button class="back-btn" @click="goBack">
        <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
          <path d="M15 6l-6 6 6 6" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        返回公告列表
      </button>
    </header>

    <!-- 公告主卡片 -->
    <article class="detail-card">
      <!-- 左侧装饰图标 -->
      <div class="notice-icon">
        <svg viewBox="0 0 24 24" width="28" height="28" aria-hidden="true">
          <path d="M3 11a9 9 0 1 1 18 0v7h-5v-5H8v5H3v-7Z" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/>
          <path d="M8 18h8v3H8z" fill="currentColor" opacity="0.3"/>
        </svg>
      </div>

      <!-- 标题 -->
      <h1 class="notice-title">{{ notice.title }}</h1>

      <!-- 时间 -->
      <div class="notice-meta">
        <span class="time-tag">
          <svg viewBox="0 0 24 24" width="13" height="13" aria-hidden="true">
            <circle cx="12" cy="12" r="9" fill="none" stroke="currentColor" stroke-width="1.6"/>
            <path d="M12 7v5l3 2" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/>
          </svg>
          {{ formatTime(notice.createTime) }}
        </span>
      </div>

      <!-- 分割线 -->
      <div class="divider"></div>

      <!-- 正文 -->
      <div class="notice-content">{{ notice.content }}</div>
    </article>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../utils/request'

const route = useRoute()
const router = useRouter()
const notice = ref(null)

const formatTime = (t) => {
  if (!t) return ''
  return String(t).replace('T', ' ').substring(0, 16)
}

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/notices')
  }
}

const load = async () => {
  const id = route.params.id
  if (!id) {
    router.replace('/notices')
    return
  }
  const res = await request.get('/notice/detail', { params: { id } })
  notice.value = res.data
}

onMounted(load)
</script>

<style scoped>
/* ============ 返回栏 ============ */
.back-bar {
  margin-bottom: 18px;
}
.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--text-secondary);
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px 4px;
  border-radius: var(--radius-md);
  transition: all var(--transition-base);
}
.back-btn:hover {
  color: var(--brand-500);
  background: var(--brand-50);
  padding: 8px 12px;
}

/* ============ 主卡片 ============ */
.detail-card {
  background: #fff;
  border: 1px solid var(--border-light);
  border-radius: var(--radius-xl);
  padding: 40px 48px;
  position: relative;
}

/* 左侧图标 */
.notice-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, var(--brand-400), var(--brand-500));
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  box-shadow: var(--shadow-brand-glow);
}

/* 标题 */
.notice-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 16px;
  line-height: 1.4;
  word-break: break-word;
}

/* 时间 */
.notice-meta {
  margin-bottom: 20px;
}
.time-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-muted);
  background: var(--bg-page);
  padding: 6px 14px;
  border-radius: var(--radius-full);
  border: 1px solid var(--border-light);
}

/* 分割线 */
.divider {
  height: 1px;
  background: var(--border-light);
  margin: 16px 0 28px;
}

/* 正文 */
.notice-content {
  font-size: 15px;
  color: var(--text-primary);
  line-height: 2;
  white-space: pre-wrap;
  word-break: break-word;
  overflow-wrap: break-word;
}

/* ============ Responsive ============ */
@media (max-width: 600px) {
  .detail-card { padding: 28px 24px; }
  .notice-title { font-size: 20px; }
  .notice-icon { width: 48px; height: 48px; }
}
</style>
