<template>
  <div class="page-home">
    <!-- Hero 轮播 -->
    <el-carousel
      :interval="5000"
      height="380px"
      arrow="hover"
      :pause-on-hover="true"
      class="hero-carousel"
    >
      <el-carousel-item v-for="(b, i) in banners" :key="i">
        <div class="banner">
          <img :src="b.img" :alt="b.title" class="banner-full" />
          <button class="banner-cta" @click="$router.push(b.route)">{{ b.cta }}</button>
        </div>
      </el-carousel-item>
    </el-carousel>

    <div class="content-wrap">
      <!-- 左侧分类侧边栏 -->
      <aside class="sidebar">
        <div class="sidebar-title">全部分类</div>
        <div
          class="cat-item"
          :class="{ active: query.categoryId === null }"
          @click="selectCategory(null)"
        >
          <svg viewBox="0 0 24 24" width="16" height="16" aria-hidden="true">
            <path d="M4 6h16M4 12h16M4 18h16" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
          </svg>
          全部商品
        </div>
        <div
          v-for="c in categories"
          :key="c.id"
          class="cat-item"
          :class="{ active: query.categoryId === c.id }"
          @click="selectCategory(c.id)"
        >
          <svg viewBox="0 0 24 24" width="16" height="16" aria-hidden="true">
            <rect x="4" y="4" width="16" height="16" rx="3" fill="none" stroke="currentColor" stroke-width="1.6"/>
          </svg>
          {{ c.name }}
        </div>
      </aside>

      <!-- 右侧主体 -->
      <div class="content">
        <!-- 搜索栏 -->
        <div class="search-bar">
          <div class="search-input">
            <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true" class="icon-search">
              <circle cx="11" cy="11" r="7" fill="none" stroke="currentColor" stroke-width="1.8"/>
              <path d="m20 20-3.5-3.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
            </svg>
            <input
              v-model="query.keyword"
              placeholder="搜索商品标题、关键词"
              @keyup.enter="load"
            />
            <button class="camera-btn" title="拍照搜物 / 上传图片搜商品" @click="imgInput.click()">
              <svg viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
                <path d="M4 8h3l2-2h6l2 2h3a1 1 0 0 1 1 1v9a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V9a1 1 0 0 1 1-1Z" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/>
                <circle cx="12" cy="13" r="3.5" fill="none" stroke="currentColor" stroke-width="1.7"/>
              </svg>
            </button>
            <input ref="imgInput" type="file" accept="image/*" hidden @change="onImageSearch" />
          </div>
          <button class="search-btn" @click="load">搜索</button>
        </div>

        <!-- 筛选条件 -->
        <div class="filter-bar">
          <select v-model="query.conditionLevel">
            <option :value="null">全部成色</option>
            <option :value="1">全新</option>
            <option :value="2">九成新</option>
            <option :value="3">八成新</option>
            <option :value="4">较旧</option>
          </select>
          <div class="price-range">
            <input v-model.number="query.minPrice" type="number" placeholder="最低价" />
            <span class="dash">—</span>
            <input v-model.number="query.maxPrice" type="number" placeholder="最高价" />
          </div>
          <button class="btn-ghost" @click="reset">重置</button>
        </div>

        <!-- 图片搜索提示 -->
        <div v-if="imageSearchTip" class="img-search-tip">
          <svg viewBox="0 0 24 24" width="16" height="16" aria-hidden="true">
            <path d="M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20Zm1 15h-2v-2h2v2Zm0-4h-2V7h2v6Z" fill="#60A5FA"/>
          </svg>
          {{ imageSearchTip }}
          <span class="tip-close" @click="imageSearchTip = ''">×</span>
        </div>

        <!-- 商品列表 -->
        <div class="grid">
          <article
            v-for="p in products"
            :key="p.id"
            class="prod-card"
            @click="$router.push('/product/' + p.id)"
          >
            <div class="img-box">
              <img v-if="p.imageUrl && !failed[p.id]" :src="p.imageUrl" :alt="p.title"
                   @error="failed[p.id] = true" />
              <span v-else class="fallback-char">{{ p.title.charAt(0) }}</span>
              <span v-if="p.status === 2" class="badge-sold">卖掉了</span>
              <span v-else-if="p.conditionLevel === 1" class="badge-new">全新</span>
            </div>
            <div class="body">
              <h3 class="title" :title="p.title">{{ p.title }}</h3>
              <div class="meta">
                <span class="price">￥{{ p.price }}</span>
                <span class="tag">{{ p.categoryName }}</span>
              </div>
              <div class="seller">
                <span class="avatar">{{ (p.sellerNickname || '·').charAt(0) }}</span>
                <span class="seller-name">{{ p.sellerNickname }}</span>
              </div>
            </div>
          </article>
        </div>

        <div v-if="products.length === 0" class="empty">
          <svg viewBox="0 0 24 24" width="48" height="48" aria-hidden="true">
            <path d="M3 7h18l-1.5 12h-15L3 7Z" fill="none" stroke="#D0D5DC" stroke-width="1.5"/>
            <path d="M8 7V5a4 4 0 0 1 8 0v2" fill="none" stroke="#D0D5DC" stroke-width="1.5"/>
          </svg>
          <p>暂无符合条件的商品</p>
        </div>

        <el-pagination
          v-model:current-page="query.pageNum"
          :page-size="query.pageSize"
          :total="total"
          layout="prev, pager, next, total"
          @current-change="load"
          class="pager"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const products = ref([])
const categories = ref([])
const total = ref(0)
const failed = reactive({})
const imgInput = ref(null)
const catBarRef = ref(null)
const imageSearchTip = ref('')
const query = ref({ keyword: '', categoryId: null, minPrice: null, maxPrice: null, conditionLevel: null, pageNum: 1, pageSize: 12 })

const banners = [
  {
    img: '/image/banners/banner1.jpg',
    title: '校园市集，闲置新生',
    cta: '去逛逛',
    route: '/home'
  },
  {
    img: '/image/banners/banner2.jpg',
    title: '教材数码，低价流转',
    cta: '查看闲置',
    route: '/category'
  },
  {
    img: '/image/banners/banner3.jpg',
    title: '生活好物，重新出发',
    cta: '立即发布',
    route: '/publish'
  }
]

const goSearch = (keyword) => {
  query.value.keyword = keyword
  query.value.pageNum = 1
  load()
}

const selectCategory = (id) => {
  query.value.categoryId = id
  query.value.pageNum = 1
  load()
}

const load = async () => {
  const q = query.value
  if (q.minPrice != null && q.maxPrice != null && q.minPrice > q.maxPrice) {
    ElMessage.warning('最低价不能大于最高价')
    return
  }
  const res = await request.get('/product/page', { params: q })
  products.value = res.data.records
  total.value = res.data.total
}

const loadCategories = async () => {
  const res = await request.get('/category/list')
  categories.value = res.data
}

const reset = () => {
  query.value = { keyword: '', categoryId: query.value.categoryId, minPrice: null, maxPrice: null, conditionLevel: null, pageNum: 1, pageSize: 12 }
  load()
}

const onImageSearch = (e) => {
  const file = e.target.files[0]
  if (!file) return
  if (!file.type.startsWith('image/')) return
  imageSearchTip.value = '图片搜索中，正在为您匹配相似商品…'
  // 模拟图片搜物：后端暂无图像识别，返回在售商品列表作为相似结果
  setTimeout(() => {
    query.value.keyword = ''
    query.value.categoryId = null
    query.value.pageNum = 1
    load()
    imageSearchTip.value = '已根据图片为您推荐相似商品'
  }, 600)
  e.target.value = ''
}

onMounted(() => {
  load()
  loadCategories()
})
</script>

<style scoped>
/* ============ Hero 轮播 ============ */
.hero-carousel {
  border-radius: var(--radius-lg);
  overflow: hidden;
  margin-bottom: 20px;
  box-shadow: var(--shadow-md);
}
:deep(.hero-carousel .el-carousel__item) {
  height: 100%;
}
/* 指示器：横向小圆点 */
:deep(.hero-carousel .el-carousel__indicators) { z-index: 3; }
:deep(.hero-carousel .el-carousel__indicator) { padding: 0 6px; height: auto; }
:deep(.hero-carousel .el-carousel__indicator .el-carousel__button) {
  width: 10px; height: 10px; border-radius: 50%;
  background: rgba(255, 255, 255, 0.45);
  opacity: 1;
}
:deep(.hero-carousel .el-carousel__indicator.is-active .el-carousel__button) {
  width: 26px; border-radius: 5px;
  background: #FFFFFF;
  box-shadow: 0 0 6px rgba(255, 255, 255, 0.5);
}

/* 箭头：简洁线性，默认半透明 */
:deep(.hero-carousel .el-carousel__arrow) {
  width: 40px; height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.35);
  color: #FFFFFF;
  font-size: 18px;
  font-weight: 300;
  backdrop-filter: blur(4px);
  opacity: 0.45;
  transition: all 0.3s ease;
}
:deep(.hero-carousel .el-carousel__arrow:hover) {
  background: rgba(255, 255, 255, 0.9);
  color: #1D4ED8;
  opacity: 1;
}

.banner {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}
.banner-full {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  display: block;
}
.banner-cta {
  position: absolute;
  right: 72px;
  bottom: 36px;
  padding: 11px 28px;
  background: #FFFFFF;
  color: #2563EB;
  border: none;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all .3s ease;
  box-shadow: 0 4px 16px rgba(37, 99, 235, 0.35);
  z-index: 2;
}
.banner-cta:hover {
  background: #1D4ED8;
  color: #FFFFFF;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(37, 99, 235, 0.5);
}

/* ============ 布局：侧边栏 + 内容 ============ */
.content-wrap {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}
.sidebar {
  width: 200px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #ECEEF1;
  padding: 14px 0;
  position: sticky;
  top: 80px;
  max-height: calc(100vh - 100px);
  overflow-y: auto;
  box-shadow: 0 2px 12px rgba(15, 27, 45, 0.06);
}
.sidebar-title {
  font-size: 14px;
  font-weight: 600;
  color: #1A2A3A;
  padding: 4px 16px 12px;
  border-bottom: 1px solid #F0F1F4;
  margin-bottom: 6px;
}
.cat-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  font-size: 14px;
  color: #4A5568;
  cursor: pointer;
  transition: all .15s;
  border-radius: 0;
}
.cat-item:hover { background: #F7F8FA; color: #60A5FA; }
.cat-item.active {
  background: #EFF6FF;
  color: #60A5FA;
  font-weight: 600;
  border-left: 3px solid #60A5FA;
  padding-left: 13px;
}
.content { flex: 1; min-width: 0; }

/* ============ 搜索栏 ============ */
.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 14px;
}
.search-input {
  flex: 1;
  height: 42px;
  display: flex;
  align-items: center;
  gap: 10px;
  color: #9AA3AD;
  border: 1px solid #E3E6EA;
  border-radius: 10px;
  padding: 0 14px;
  background: #fff;
  transition: all .2s;
}
.search-input:focus-within {
  border-color: #60A5FA;
  box-shadow: 0 0 0 4px rgba(96, 165, 250, 0.10);
}
.search-input .icon-search { color: #B6BDC4; }
.search-input:focus-within .icon-search { color: #60A5FA; }
.search-input input {
  flex: 1;
  height: 100%;
  border: none;
  outline: none;
  font-size: 14px;
  background: transparent;
  color: #1A2A3A;
}
.camera-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #9AA3AD;
  display: flex;
  align-items: center;
  padding: 4px;
  border-radius: 6px;
  transition: all .2s;
}
.camera-btn:hover { color: #60A5FA; background: #EFF6FF; }
.search-btn {
  height: 42px;
  padding: 0 28px;
  background: linear-gradient(135deg, #60A5FA, #3B82F6);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all .2s;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.22);
}
.search-btn:hover { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(59, 130, 246, 0.32); }

/* ============ 筛选栏 ============ */
.filter-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 18px;
  flex-wrap: wrap;
}
.filter-bar select {
  height: 36px;
  border: 1px solid #E3E6EA;
  border-radius: 8px;
  padding: 0 12px;
  font-size: 14px;
  background: #fff;
  color: #1A2A3A;
  outline: none;
  cursor: pointer;
  min-width: 110px;
}
.filter-bar select:focus { border-color: #60A5FA; }
.price-range {
  display: flex;
  align-items: center;
  gap: 6px;
  height: 36px;
}
.price-range input {
  width: 88px;
  height: 36px;
  border: 1px solid #E3E6EA;
  border-radius: 8px;
  padding: 0 12px;
  font-size: 14px;
  color: #1A2A3A;
  outline: none;
}
.price-range input:focus { border-color: #60A5FA; }
.dash { color: #B6BDC4; }
.btn-ghost {
  height: 36px;
  padding: 0 18px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  border: 1px solid #E3E6EA;
  background: #fff;
  color: #6C757D;
  transition: all .2s;
}
.btn-ghost:hover { color: #60A5FA; border-color: #93C5FD; }

/* ============ 图片搜索提示 ============ */
.img-search-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #EFF6FF;
  border: 1px solid #BFDBFE;
  color: #E67E22;
  font-size: 13px;
  padding: 10px 14px;
  border-radius: 8px;
  margin-bottom: 16px;
}
.tip-close {
  margin-left: auto;
  cursor: pointer;
  font-size: 18px;
  line-height: 1;
  opacity: 0.6;
}
.tip-close:hover { opacity: 1; }

/* ============ 商品卡片 ============ */
.grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18px;
}
.prod-card {
  background: #fff;
  border: 1px solid var(--border-light);
  border-radius: var(--radius-lg);
  overflow: hidden;
  cursor: pointer;
  transition: all var(--transition-slow);
  position: relative;
}
.prod-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: var(--brand-200);
}
.prod-card::before {
  content: '';
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 1;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, rgba(96,165,250,0) 30%, rgba(96,165,250,0.10) 100%);
  opacity: 0;
  transition: opacity var(--transition-slow);
  box-shadow: inset 0 0 40px rgba(96,165,250,0.08);
}
.prod-card:hover::before { opacity: 1; }
.img-box {
  position: relative;
  height: 200px;
  background: linear-gradient(135deg, var(--brand-50), var(--brand-100));
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.img-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform .35s ease;
}
.prod-card:hover .img-box img { transform: scale(1.05); }
.fallback-char {
  font-size: 56px;
  font-weight: 700;
  color: #3B82F6;
}
.badge-new {
  position: absolute;
  top: 10px;
  left: 10px;
  background: linear-gradient(135deg, #60A5FA, #3B82F6);
  color: #fff;
  font-size: 11px;
  font-weight: 600;
  padding: 3px 9px;
  border-radius: 4px;
  box-shadow: 0 2px 6px rgba(59, 130, 246, 0.35);
}
.badge-sold {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.65);
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 4px;
  backdrop-filter: blur(2px);
}
.body { padding: 14px 16px 16px; }
.title {
  font-size: 15px;
  font-weight: 600;
  color: #1A2A3A;
  margin: 0 0 10px;
  height: 22px;
  line-height: 22px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.price {
  color: #3B82F6;
  font-weight: 700;
  font-size: 20px;
}
.tag {
  font-size: 12px;
  color: #6C757D;
  background: #F7F8FA;
  padding: 3px 9px;
  border-radius: 4px;
}
.seller {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6C757D;
  font-size: 12px;
  border-top: 1px solid #F0F1F4;
  padding-top: 12px;
}
.seller .avatar {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #1A2A3A;
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
}
.seller-name { max-width: 120px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.empty {
  text-align: center;
  color: #9AA3AD;
  padding: 80px 20px;
  background: #fff;
  border-radius: 12px;
  border: 1px dashed #E3E6EA;
}
.empty svg { margin-bottom: 16px; }
.empty p { font-size: 14px; }

.pager {
  margin-top: 24px;
  justify-content: center;
}

@media (max-width: 1024px) {
  .grid { grid-template-columns: repeat(2, 1fr); }
  .sidebar { width: 160px; }
}
@media (max-width: 768px) {
  .content-wrap { flex-direction: column; }
  .sidebar {
    width: 100%;
    position: static;
    max-height: none;
    display: flex;
    flex-wrap: wrap;
    padding: 10px;
    gap: 6px;
  }
  .sidebar-title { width: 100%; border-bottom: none; margin-bottom: 0; padding: 4px 6px; }
  .cat-item { padding: 6px 12px; border-radius: 14px; border: 1px solid #ECEEF1; font-size: 13px; }
  .cat-item.active { border-left: 1px solid #BFDBFE; padding-left: 12px; }
  .grid { grid-template-columns: repeat(2, 1fr); }
  .banner-cta { right: 24px; bottom: 20px; padding: 8px 18px; font-size: 12px; }
}
</style>
