<template>
  <div class="page page-fade-in">
    <header class="page-header">
      <h2 class="page-title">历史浏览</h2>
      <span class="page-subtitle" v-if="list.length">共 {{ list.length }} 条</span>
      <el-button v-if="list.length" class="clear-btn" text @click="clearAll">清空历史</el-button>
    </header>

    <div v-if="list.length > 0" class="grid">
      <div
        v-for="(h, i) in list"
        :key="h.productId"
        class="card"
        @click="$router.push(`/product/${h.productId}`)"
      >
        <div class="img-box">
          <img :src="h.imageUrl" class="img" loading="lazy" />
          <span class="rank" v-if="i < 3">{{ i + 1 }}</span>
        </div>
        <div class="info">
          <div class="title">{{ h.title }}</div>
          <div class="meta">
            <span class="price">¥{{ h.price?.toFixed(2) }}</span>
            <span class="time">{{ formatTime(h.time) }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="empty">
      <svg viewBox="0 0 24 24" width="64" height="64" aria-hidden="true">
        <circle cx="12" cy="12" r="9" fill="#EFF6FF" stroke="#3B82F6" stroke-width="1.6"/>
        <path d="M12 7v5l3 2" fill="none" stroke="#3B82F6" stroke-width="1.8" stroke-linecap="round"/>
      </svg>
      <p class="empty-title">暂无浏览记录</p>
      <p class="empty-desc">浏览过的商品会自动保存到这里</p>
      <el-button type="primary" @click="$router.push('/home')">开始浏览</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])

const load = () => {
  try {
    list.value = JSON.parse(localStorage.getItem('browseHistory') || '[]')
  } catch { list.value = [] }
}

const clearAll = async () => {
  try {
    await ElMessageBox.confirm('确定清空所有浏览记录？', '提示', {
      confirmButtonText: '清空', cancelButtonText: '取消', type: 'warning'
    })
    localStorage.removeItem('browseHistory')
    list.value = []
    ElMessage.success('已清空')
  } catch { /* 用户取消 */ }
}

const formatTime = (ts) => {
  if (!ts) return ''
  const d = new Date(ts)
  const now = new Date()
  const diff = (now - d) / 1000
  if (diff < 60) return '刚刚'
  if (diff < 3600) return Math.floor(diff / 60) + ' 分钟前'
  if (diff < 86400) return Math.floor(diff / 3600) + ' 小时前'
  return `${d.getMonth() + 1}月${d.getDate()}日`
}

onMounted(load)
</script>

<style scoped>
.page-header { display: flex; align-items: baseline; gap: 12px; margin-bottom: 18px; padding-left: 2px; }
.page-title { font-size: 20px; font-weight: 700; color: #1A2A3A; margin: 0; }
.page-subtitle { font-size: 13px; color: #94A3B8; }
.clear-btn { margin-left: auto; color: #EF4444 !important; font-size: 13px; }

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}
.card {
  background: #fff;
  border: 1px solid #EEF0F4;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.25s ease;
}
.card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(37, 99, 235, 0.12);
  border-color: #BFDBFE;
}
.img-box { position: relative; width: 100%; aspect-ratio: 4/3; background: #F5F6F8; }
.img { width: 100%; height: 100%; object-fit: cover; }
.rank {
  position: absolute; top: 8px; left: 8px;
  width: 22px; height: 22px; border-radius: 6px;
  background: #EF4444; color: #fff;
  font-size: 12px; font-weight: 700;
  display: flex; align-items: center; justify-content: center;
}
.info { padding: 12px 14px 14px; }
.title {
  font-size: 14px; font-weight: 600; color: #1A2A3A;
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
  margin-bottom: 6px;
}
.meta { display: flex; justify-content: space-between; align-items: center; }
.price { font-size: 16px; font-weight: 700; color: #EF4444; }
.time { font-size: 12px; color: #94A3B8; }

.empty { padding: 80px 20px; text-align: center; }
.empty-title { font-size: 16px; font-weight: 600; color: #1A2A3A; margin: 14px 0 6px; }
.empty-desc { font-size: 13px; color: #94A3B8; margin: 0 0 18px; }
</style>
