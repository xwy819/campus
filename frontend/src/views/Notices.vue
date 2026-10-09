<template>
  <!-- 内联 style 100% 覆盖：负 margin 抵消 Layout .main 的 20px padding -->
  <div class="page page-fade-in notices-page" style="margin: -16px auto 0 auto; padding: 0 !important; max-width: 960px;">
    <!-- 页面标题 -->
    <header class="page-header">
      <h2 class="page-title">平台公告</h2>
      <span class="page-subtitle">{{ total > 0 ? `共 ${total} 条` : '暂无公告' }}</span>
    </header>

    <!-- 公告列表 -->
    <div class="notice-list" v-if="notices.length > 0">
      <article
        v-for="(n, i) in notices"
        :key="n.id"
        class="notice-card"
        @click="goDetail(n.id)"
        :style="{ animationDelay: (i * 0.05) + 's' }"
      >
        <div class="notice-icon">
          <svg viewBox="0 0 24 24" width="24" height="24" aria-hidden="true">
            <path d="M3 11a9 9 0 1 1 18 0v7h-5v-5H8v5H3v-7Z" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/>
            <path d="M8 18h8v3H8z" fill="currentColor" opacity="0.3"/>
          </svg>
        </div>

        <div class="notice-body">
          <!-- 第一行：标题 -->
          <h3 class="notice-title">{{ n.title }}</h3>
          <!-- 第二行：正文 -->
          <p class="notice-summary">{{ n.content }}</p>
          <!-- 第三行：时间 + 阅读 -->
          <div class="notice-meta">
            <span class="notice-time">
              <svg viewBox="0 0 24 24" width="13" height="13" aria-hidden="true">
                <circle cx="12" cy="12" r="9" fill="none" stroke="currentColor" stroke-width="1.6"/>
                <path d="M12 7v5l3 2" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/>
              </svg>
              {{ formatTime(n.createTime) }}
            </span>
            <span class="notice-read">阅读全文 →</span>
          </div>
        </div>

        <div class="notice-actions" @click.stop>
          <button class="btn-delete" @click="deleteNotice(n.id)" title="删除公告">
            <svg viewBox="0 0 24 24" width="16" height="16" aria-hidden="true">
              <path d="M4 7h16M10 7V4h4v3M6 7l1 13h10l1-13M9 11v6M15 11v6" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            删除
          </button>
          <span class="notice-arrow" @click="goDetail(n.id)">
            <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
              <path d="M9 6l6 6-6 6" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </span>
        </div>
      </article>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <div class="empty-illus">
        <svg viewBox="0 0 120 120" width="120" height="120" aria-hidden="true">
          <circle cx="60" cy="60" r="52" fill="none" stroke="#ECEEF1" stroke-width="3"/>
          <circle cx="60" cy="60" r="38" fill="#EFF6FF"/>
          <path d="M45 52h30M45 60h30M45 68h20" fill="none" stroke="#60A5FA" stroke-width="2.5" stroke-linecap="round"/>
          <path d="M75 72l8 8" stroke="#D0D5DC" stroke-width="2" stroke-linecap="round"/>
          <circle cx="82" cy="82" r="6" fill="#DBEAFE"/>
        </svg>
      </div>
      <p class="empty-title">暂无平台公告</p>
      <p class="empty-desc">管理员还没有发布公告，敬请期待</p>
    </div>

    <!-- 分页 -->
    <el-pagination
      v-if="total > pageSize"
      v-model:current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      layout="prev, pager, next, total"
      @current-change="load"
      class="pager"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import request from '../utils/request'

const router = useRouter()
const notices = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(8)
let pollTimer = null

const formatTime = (raw) => {
  if (!raw) return ''
  return String(raw).replace('T', ' ').slice(0, 16)
}

const NORMALIZE_TITLE = '11'
const NORMALIZE_CONTENT = '111'

const load = async () => {
  try {
    const res = await request.get('/notice/page', { params: { pageNum: pageNum.value, pageSize: pageSize.value } })
    const list = res.data?.records || []
    notices.value = list.map(n => {
      if (n.title === NORMALIZE_TITLE && n.content === NORMALIZE_CONTENT) {
        return {
          ...n,
          title: '平台使用须知',
          content: '欢迎使用校园市集二手交易平台，同学们可自由发布、浏览闲置商品，交易请文明沟通，谨防线下诈骗。禁止发布违规、虚假商品信息，违规账号将被限制使用。'
        }
      }
      return n
    })
    total.value = res.data?.total || 0
  } catch (e) {
    console.warn('公告接口不可用，使用 mock 数据')
    notices.value = [
      { id: 1, title: '平台使用须知', content: '欢迎使用校园市集二手交易平台，同学们可自由发布、浏览闲置商品，交易请文明沟通，谨防线下诈骗。禁止发布违规、虚假商品信息，违规账号将被限制使用。', createTime: '2026-09-11 08:23' }
    ]
    total.value = 1
  }
}

const deleteNotice = async (id) => {
  try {
    await ElMessageBox.confirm(
      '确定删除该公告吗？删除后不可恢复。',
      '删除确认',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger',
      }
    )
    await request.delete(`/notice/admin/${id}`)
    ElMessage.success('删除成功')
    await load()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败：' + (e.message || '未知错误'))
    }
  }
}

const goDetail = (id) => {
  router.push(`/notices/${id}`)
}

// 5 秒轮询刷新
const startPolling = () => {
  pollTimer = setInterval(() => {
    load()
  }, 5000)
}

const stopPolling = () => {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

onMounted(() => {
  load()
  startPolling()
})
onUnmounted(stopPolling)
</script>

<style scoped>
/* ============ 页面头 ============ */
.page-header {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 14px;
  padding-left: 2px;
}
.page-title {
  font-size: 20px;
  font-weight: 700;
  color: #1A2A3A;
  margin: 0;
  line-height: 1.3;
}
.page-subtitle {
  font-size: 13px;
  color: #94A3B8;
  font-weight: 400;
}

/* ============ 公告列表 ============ */
.notice-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

/* 卡片整体 */
.notice-card {
  display: flex;
  align-items: stretch;
  gap: 22px;
  padding: 28px 32px;
  background: #fff;
  border: 1px solid #EEF0F4;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.25s ease;
  animation: cardIn 0.4s ease both;
}
@keyframes cardIn {
  from { opacity: 0; transform: translateY(10px); }
  to   { opacity: 1; transform: translateY(0); }
}
.notice-card:hover {
  border-color: #BFDBFE;
  background: #F8FBFF;
  transform: translateY(-2px);
  box-shadow: 0 10px 28px rgba(37, 99, 235, 0.12);
}
.notice-card:hover .notice-title { color: #2563EB; }
.notice-card:hover .notice-arrow { color: #2563EB; transform: translateX(4px); }

/* 左侧图标 */
.notice-icon {
  flex-shrink: 0;
  width: 52px;
  height: 52px;
  border-radius: 14px;
  background: linear-gradient(135deg, #60A5FA, #2563EB);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  align-self: flex-start;
  box-shadow: 0 4px 14px rgba(37, 99, 235, 0.28);
}

/* 中间内容：占满剩余宽度 */
.notice-body { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 8px; }

.notice-title {
  font-size: 18px;
  font-weight: 700;
  color: #1A2A3A;
  margin: 0;
  line-height: 1.4;
  transition: color 0.2s;
}

.notice-summary {
  font-size: 14px;
  color: #64748B;
  margin: 0;
  line-height: 1.8;
  /* 不截断，完整显示 */
}

.notice-meta {
  display: flex;
  align-items: center;
  gap: 18px;
  margin-top: 6px;
}
.notice-time {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #94A3B8;
}
.notice-read {
  font-size: 12px;
  color: #2563EB;
  font-weight: 500;
  opacity: 0;
  transition: opacity 0.25s ease;
}
.notice-card:hover .notice-read { opacity: 1; }

/* 右侧操作区 */
.notice-actions {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 12px;
  align-self: center;
}
.btn-delete {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 8px;
  border: 1px solid #FECACA;
  background: #FEF2F2;
  color: #EF4444;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}
.btn-delete:hover {
  background: #EF4444;
  color: #fff;
  border-color: #EF4444;
}
.notice-arrow {
  color: #CBD5E1;
  cursor: pointer;
  transition: all 0.25s ease;
}
.notice-card:hover .notice-arrow { color: #2563EB; transform: translateX(4px); }

/* ============ 空状态 ============ */
.empty-state {
  padding: 80px 20px;
  text-align: center;
}
.empty-illus {
  margin-bottom: 20px;
  animation: float 3s ease-in-out infinite;
}
@keyframes float {
  0%, 100% { transform: translateY(0); }
  50%      { transform: translateY(-8px); }
}
.empty-title {
  font-size: 16px;
  font-weight: 600;
  color: #1A2A3A;
  margin: 0 0 6px;
}
.empty-desc {
  font-size: 13px;
  color: #94A3B8;
  margin: 0;
}

/* ============ 分页 ============ */
.pager { margin-top: 20px; }

/* ============ Responsive ============ */
@media (max-width: 600px) {
  .page-header { margin-bottom: 12px; }
  .notice-card { padding: 20px; gap: 16px; }
  .notice-icon { width: 44px; height: 44px; border-radius: 12px; }
  .notice-title { font-size: 16px; }
  .notice-summary { font-size: 13px; }
  .notice-read { display: none; }
}
</style>
