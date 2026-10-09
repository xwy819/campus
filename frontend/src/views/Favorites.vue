<template>
  <div class="page page-fade-in">
    <header class="page-header">
      <h2 class="page-title">我的收藏</h2>
      <span class="page-subtitle">{{ list.length ? `共 ${list.length} 件` : '' }}</span>
    </header>

    <div v-if="list.length > 0" class="grid">
      <div
        v-for="f in list"
        :key="f.id"
        class="card"
        @click="$router.push(`/product/${f.productId}`)"
      >
        <div class="img-box">
          <img :src="f.productImageUrl" class="img" loading="lazy" />
          <button class="btn-remove" @click.stop="remove(f.productId)" title="取消收藏">
            <svg viewBox="0 0 24 24" width="14" height="14"><path d="M18 6 6 18M6 6l12 12" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"/></svg>
          </button>
          <span class="status-tag" v-if="f.productStatus === 0">已下架</span>
        </div>
        <div class="info">
          <div class="title">{{ f.productTitle }}</div>
          <div class="meta">
            <span class="price">¥{{ f.productPrice?.toFixed(2) }}</span>
            <span class="seller">@{{ f.sellerNickname }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="empty">
      <svg viewBox="0 0 24 24" width="64" height="64" aria-hidden="true">
        <path d="M12 21s-7-4.5-9.5-9A5.5 5.5 0 0 1 12 6a5.5 5.5 0 0 1 9.5 6c-2.5 4.5-9.5 9-9.5 9Z" fill="#EFF6FF" stroke="#3B82F6" stroke-width="1.6" stroke-linejoin="round"/>
      </svg>
      <p class="empty-title">还没有收藏的商品</p>
      <p class="empty-desc">在商品详情页点击 ❤️ 收藏喜欢的商品吧</p>
      <el-button type="primary" @click="$router.push('/home')">去首页逛逛</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const list = ref([])

const load = async () => {
  try {
    const res = await request.get('/favorite/my')
    list.value = res.data || []
  } catch (e) {
    console.warn('加载收藏失败', e)
  }
}

const remove = async (productId) => {
  try {
    await ElMessageBox.confirm('取消收藏该商品？', '提示', {
      confirmButtonText: '取消收藏', cancelButtonText: '再想想', type: 'warning'
    })
    await request.post('/favorite/toggle', { productId })
    ElMessage.success('已取消收藏')
    load()
  } catch (e) { /* 用户取消 */ }
}

onMounted(load)
</script>

<style scoped>
.page-header { display: flex; align-items: baseline; gap: 12px; margin-bottom: 18px; padding-left: 2px; }
.page-title { font-size: 20px; font-weight: 700; color: #1A2A3A; margin: 0; }
.page-subtitle { font-size: 13px; color: #94A3B8; }

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
.btn-remove {
  position: absolute; top: 8px; right: 8px;
  width: 28px; height: 28px; border-radius: 50%;
  border: none; background: rgba(0,0,0,0.45); color: #fff;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; opacity: 0; transition: opacity .2s;
}
.card:hover .btn-remove { opacity: 1; }
.btn-remove:hover { background: #EF4444; }
.status-tag {
  position: absolute; top: 8px; left: 8px;
  font-size: 11px; padding: 2px 8px; border-radius: 10px;
  background: rgba(239,68,68,0.9); color: #fff;
}
.info { padding: 12px 14px 14px; }
.title {
  font-size: 14px; font-weight: 600; color: #1A2A3A;
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
  margin-bottom: 6px;
}
.meta { display: flex; justify-content: space-between; align-items: center; }
.price { font-size: 16px; font-weight: 700; color: #EF4444; }
.seller { font-size: 12px; color: #94A3B8; }

.empty { padding: 80px 20px; text-align: center; }
.empty-title { font-size: 16px; font-weight: 600; color: #1A2A3A; margin: 14px 0 6px; }
.empty-desc { font-size: 13px; color: #94A3B8; margin: 0 0 18px; }
</style>
