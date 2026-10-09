<template>
  <div class="page" v-if="product">
    <!-- 返回栏 -->
    <header class="back-bar">
      <button class="back-btn" @click="goBack">
        <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
          <path d="M15 6l-6 6 6 6" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        返回
      </button>
    </header>

    <!-- ============ 顶部：商品主信息卡 ============ -->
    <el-card class="main-card">
      <div class="main-grid">
        <div class="img-wrap">
          <img v-if="product.imageUrl && !imgFailed" :src="product.imageUrl" :alt="product.title"
               @error="imgFailed = true" />
          <span v-else class="fallback-char">{{ product.title.charAt(0) }}</span>
          <span v-if="product.conditionLevel === 1" class="badge-new">全新</span>
          <div class="view-count">
            <svg viewBox="0 0 24 24" width="14" height="14" aria-hidden="true">
              <path d="M12 5c-7 0-10 7-10 7s3 7 10 7 10-7 10-7-3-7-10-7zm0 11a4 4 0 110-8 4 4 0 010 8z"
                    fill="#9AA3AD"/>
              <circle cx="12" cy="12" r="2.5" fill="#9AA3AD"/>
            </svg>
            <span>{{ product.viewCount || 0 }} 次浏览</span>
          </div>
        </div>

        <div class="info-wrap">
          <div class="cat-tag">{{ product.categoryName || '未分类' }}</div>
          <h1 class="title">{{ product.title }}</h1>

          <div class="price-row">
            <span class="price">￥{{ product.price }}</span>
            <el-tag v-if="product.conditionLevel === 1" effect="light">全新</el-tag>
            <el-tag v-else-if="product.conditionLevel === 2" type="success" effect="light">九成新</el-tag>
            <el-tag v-else-if="product.conditionLevel === 3" type="warning" effect="light">八成新</el-tag>
            <el-tag v-else type="info" effect="light">较旧</el-tag>
          </div>

          <div class="meta-row">
            <div class="meta-item">
              <label>发布时间</label><span>{{ formatTime(product.createTime) }}</span>
            </div>
            <div class="meta-item">
              <label>商品状态</label>
              <span :class="['status-tag', `status-${product.status}`]">{{ statusText }}</span>
            </div>
          </div>

          <div class="action-row">
            <el-button size="large" class="fav-btn"
                       :class="{ 'is-fav': favorited }"
                       :loading="favLoading" @click="toggleFavorite">
              <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true" class="heart-icon">
                <path :d="heartPath" :fill="favorited ? '#3B82F6' : 'none'"
                      :stroke="favorited ? '#3B82F6' : '#9AA3AD'" stroke-width="2"/>
              </svg>
              <span>{{ favorited ? '已收藏' : '收藏商品' }}</span>
            </el-button>

            <el-tooltip :content="buyDisabledReason" :disabled="!buyDisabled" placement="top">
              <span>
                <el-button type="primary" size="large" class="buy-btn"
                           :loading="buying" :disabled="buyDisabled" @click="openBuyDialog">
                  {{ buying ? '下单中...' : '立即下单' }}
                </el-button>
              </span>
            </el-tooltip>
          </div>
          <div v-if="buyDisabled" class="buy-tip">{{ buyDisabledReason }}</div>
        </div>
      </div>
    </el-card>

    <!-- ============ 中部：商品描述 + 卖家信息 并排 ============ -->
    <div class="mid-grid">
      <el-card class="desc-card">
        <template #header>
          <div class="card-title"><span class="bar"></span>商品描述</div>
        </template>
        <div class="desc-body">{{ product.description || '卖家未填写商品描述' }}</div>
      </el-card>

      <el-card class="seller-card">
        <template #header>
          <div class="card-title"><span class="bar"></span>卖家信息</div>
        </template>
        <div class="seller-row">
          <div class="seller-avatar">
            <img v-if="product.sellerAvatar && !avatarFailed" :src="product.sellerAvatar" class="avatar-img" @error="avatarFailed = true" />
            <span v-else>{{ (product.sellerNickname || '卖').charAt(0) }}</span>
          </div>
          <div class="seller-meta">
            <div class="seller-name">{{ product.sellerNickname || '匿名卖家' }}</div>
            <div class="seller-phone">{{ maskPhone(product.sellerPhone) }}</div>
          </div>
        </div>
        <el-button class="msg-btn" :disabled="msgBtnDisabled" @click="openMsgDialog">
          <svg viewBox="0 0 24 24" width="16" height="16" aria-hidden="true">
            <path d="M4 5h16a2 2 0 012 2v10a2 2 0 01-2 2H8l-4 4V7a2 2 0 012-2z"
                  fill="none" stroke="#3B82F6" stroke-width="2" stroke-linejoin="round"/>
          </svg>
          <span>私信卖家</span>
        </el-button>
        <div v-if="msgBtnDisabled" class="seller-tip">{{ msgBtnDisabledReason }}</div>
      </el-card>
    </div>

    <!-- ============ 底部：相似商品横向滚动 ============ -->
    <el-card class="similar-card" v-if="similarList.length">
      <template #header>
        <div class="card-title"><span class="bar"></span>相似商品推荐</div>
      </template>
      <div class="similar-scroll">
        <div class="similar-item" v-for="p in similarList" :key="p.id" @click="goSimilar(p.id)">
          <div class="sim-img">
            <img v-if="p.imageUrl && !p._failed" :src="p.imageUrl" :alt="p.title"
                 @error="p._failed = true" />
            <span v-else class="sim-fallback">{{ p.title.charAt(0) }}</span>
          </div>
          <div class="sim-title">{{ p.title }}</div>
          <div class="sim-bottom">
            <span class="sim-price">￥{{ p.price }}</span>
            <span class="sim-cond">{{ conditionTextOf(p.conditionLevel) }}</span>
          </div>
        </div>
      </div>
      <div v-if="!similarList.length" class="empty-tip">暂无相似商品</div>
    </el-card>

    <!-- ============ 下单确认弹窗 ============ -->
    <el-dialog v-model="buyDialogVisible" title="确认下单" width="500px" :close-on-click-modal="false">
      <div class="dialog-content">
        <div class="dialog-row"><label>商品名称</label><span class="val">{{ product.title }}</span></div>
        <div class="dialog-row"><label>售价</label><span class="val price-text">￥{{ product.price }}</span></div>
        <div class="dialog-row"><label>成色</label><span class="val">{{ conditionText }}</span></div>
        <div class="dialog-row"><label>卖家</label><span class="val">{{ product.sellerNickname }}（{{ maskPhone(product.sellerPhone) }}）</span></div>

        <div class="dialog-row">
          <label>交易方式</label>
          <div class="val">
            <el-radio-group v-model="orderDeliveryType">
              <el-radio v-if="supportsFace" :value="1">当面交易</el-radio>
              <el-radio v-if="supportsDelivery" :value="2">校内送货上门</el-radio>
            </el-radio-group>
          </div>
        </div>

        <div v-if="orderDeliveryType === 2" class="dialog-row">
          <label>收货地址</label>
          <div class="val">
            <el-select v-model="orderAddressId" placeholder="请选择收货地址" style="width: 100%">
              <el-option
                v-for="a in addressList"
                :key="a.id"
                :label="(a.label ? '[' + a.label + '] ' : '') + a.building + ' · ' + a.dorm + (a.isDefault === 1 ? '（默认）' : '')"
                :value="a.id"
              />
            </el-select>
            <p v-if="addressList.length === 0" class="addr-tip">
              暂无收货地址，<span class="link" @click="goAddress">去添加</span>
            </p>
          </div>
        </div>

        <el-alert class="dialog-tip" type="warning" :closable="false" show-icon>
          <span>校园二手商品，请按约定方式完成交易</span>
        </el-alert>
      </div>
      <template #footer>
        <el-button @click="buyDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="buying" @click="confirmBuy">确认下单</el-button>
      </template>
    </el-dialog>

    <!-- ============ 私信卖家弹窗 ============ -->
    <el-dialog v-model="msgDialogVisible" title="私信卖家" width="500px" :close-on-click-modal="false">
      <el-input :model-value="product.sellerNickname" readonly>
        <template #prepend>收件人</template>
      </el-input>
      <el-input v-model="msgContent" type="textarea" :rows="4" class="msg-textarea"
                placeholder="请输入私信内容（最多 200 字）" maxlength="200" show-word-limit />
      <template #footer>
        <el-button @click="msgDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="msgSending" @click="sendMsg">发送</el-button>
      </template>
    </el-dialog>
  </div>

  <div v-else class="loading-box">
    <el-icon class="is-loading"><Loading /></el-icon>
    <span>加载中...</span>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import request from '../utils/request'

const route = useRoute()
const router = useRouter()

const product = ref(null)
const imgFailed = ref(false)
const avatarFailed = ref(false)
const buying = ref(false)
const buyDialogVisible = ref(false)

const favorited = ref(false)
const favLoading = ref(false)

const similarList = ref([])

const msgDialogVisible = ref(false)
const msgContent = ref('')
const msgSending = ref(false)

// 心形 SVG 路径
const heartPath = 'M12 21s-7-4.5-9.5-9C1 9 2.5 5 6 5c2 0 3.5 1 6 3 2.5-2 4-3 6-3 3.5 0 5 4 3.5 7-2.5 4.5-9.5 9-9.5 9z'

// ============ 计算属性 ============
// 当前登录用户 id（登录时存入 localStorage）
const currentUserId = computed(() => {
  const id = localStorage.getItem('userId')
  return id ? Number(id) : null
})
// 是否为本商品卖家：优先用 localStorage 中的 userId 直接比对，回退到后端返回的 isSelf 标识
const isSelf = computed(() => {
  if (!product.value) return false
  if (currentUserId.value != null && product.value.userId != null) {
    return Number(currentUserId.value) === Number(product.value.userId)
  }
  return !!product.value.isSelf
})
const isOffShelf = computed(() => product.value?.status === 3)
const isSold = computed(() => product.value?.status === 2)

const buyDisabled = computed(() => {
  if (!product.value) return true
  if (isSelf.value) return true
  if (isOffShelf.value) return true
  return false
})

const buyDisabledReason = computed(() => {
  if (!product.value) return ''
  if (isSelf.value) return '不可购买自己发布的商品'
  if (isOffShelf.value) return '商品已下架，无法下单'
  return ''
})

const msgBtnDisabled = computed(() => {
  if (!product.value) return true
  if (!localStorage.getItem('token')) return true
  if (isSelf.value) return true
  return false
})
const msgBtnDisabledReason = computed(() => {
  if (!localStorage.getItem('token')) return '请先登录后再私信卖家'
  if (isSelf.value) return '不能给自己发送私信'
  return ''
})

const statusText = computed(() => {
  const s = product.value?.status
  if (s === 0) return '待审核'
  if (s === 1) return '在售中'
  if (s === 2) return '已售出'
  if (s === 3) return '已下架'
  return ''
})

const conditionText = computed(() => conditionTextOf(product.value?.conditionLevel))

function conditionTextOf(level) {
  if (level === 1) return '全新'
  if (level === 2) return '九成新'
  if (level === 3) return '八成新'
  return '较旧'
}

function formatTime(t) {
  if (!t) return ''
  return String(t).replace('T', ' ').substring(0, 16)
}

function maskPhone(phone) {
  if (!phone || phone.length < 7) return phone || '未填写'
  return phone.substring(0, 3) + '****' + phone.substring(phone.length - 4)
}

// ============ 数据加载 ============
const load = async () => {
  const id = route.params.id
  try {
    // 详情接口是公开的（不走鉴权），所以 isSelf/favorited 始终为 false
    const res = await request.get('/product/detail', { params: { id } })
    product.value = res.data
    imgFailed.value = false
    avatarFailed.value = false
    favorited.value = false

    // 写入浏览历史（localStorage，最多 30 条）
    try {
      const MAX = 30
      let list = JSON.parse(localStorage.getItem('browseHistory') || '[]')
      list = list.filter(h => h.productId !== res.data.id)
      list.unshift({
        productId: res.data.id,
        title: res.data.title,
        imageUrl: res.data.imageUrl,
        price: res.data.price,
        time: new Date().getTime()
      })
      if (list.length > MAX) list = list.slice(0, MAX)
      localStorage.setItem('browseHistory', JSON.stringify(list))
    } catch {}

    // 登录态下补充查询当前用户对该商品的标识位（isSelf、favorited）
    if (localStorage.getItem('token')) {
      try {
        const flags = await request.get('/product/myFlags', { params: { productId: id } })
        if (flags.data) {
          product.value.isSelf = !!flags.data.isSelf
          favorited.value = !!flags.data.favorited
        }
      } catch (e) {
        // 鉴权失败（token 失效）保持默认值，不阻断页面
      }
    }
    await loadSimilar(res.data)
  } catch (e) {
    // 错误由拦截器展示
  }
}

const loadSimilar = async (p) => {
  try {
    const res = await request.get('/product/similar', {
      params: { categoryId: p.categoryId, excludeId: p.id, limit: 8 }
    })
    similarList.value = res.data || []
  } catch (e) {
    similarList.value = []
  }
}

// ============ 收藏 ============
const toggleFavorite = async () => {
  if (!localStorage.getItem('token')) {
    ElMessage.warning('请先登录后再收藏')
    return
  }
  if (favLoading.value) return
  favLoading.value = true
  try {
    const res = await request.post('/favorite/toggle', { productId: product.value.id })
    favorited.value = !favorited.value
    ElMessage.success(res.message || (favorited.value ? '已收藏' : '已取消收藏'))
  } catch (e) {
    // 错误由拦截器展示
  } finally {
    favLoading.value = false
  }
}

// ============ 下单流程 ============
const orderDeliveryType = ref(1)
const orderAddressId = ref(null)
const addressList = ref([])

const supportsFace = computed(() => {
  const t = product.value?.deliveryType ?? 1
  return (t & 1) !== 0
})
const supportsDelivery = computed(() => {
  const t = product.value?.deliveryType ?? 1
  return (t & 2) !== 0
})

const loadAddresses = async () => {
  try {
    const res = await request.get('/address/list')
    addressList.value = res.data || []
    // 默认选中默认地址
    const def = addressList.value.find(a => a.isDefault === 1)
    orderAddressId.value = def ? def.id : (addressList.value[0]?.id || null)
  } catch (e) { addressList.value = [] }
}

const goAddress = () => {
  buyDialogVisible.value = false
  router.push('/address')
}

const openBuyDialog = async () => {
  if (buyDisabled.value) return
  if (!localStorage.getItem('token')) {
    ElMessage.warning('请先登录后再下单')
    return
  }
  // 默认选中卖家支持的第一种交易方式
  if (supportsFace.value) orderDeliveryType.value = 1
  else if (supportsDelivery.value) orderDeliveryType.value = 2
  orderAddressId.value = null
  buyDialogVisible.value = true
  if (supportsDelivery.value) await loadAddresses()
}

const confirmBuy = async () => {
  if (buying.value) return
  if (orderDeliveryType.value === 2 && !orderAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  buying.value = true
  try {
    await request.post('/order/create', {
      productId: product.value.id,
      deliveryType: orderDeliveryType.value,
      addressId: orderDeliveryType.value === 2 ? orderAddressId.value : null
    })
    buyDialogVisible.value = false
    ElMessage.success('下单成功，请等待卖家确认')
    // 自动跳转【我的订单】
    router.push('/myOrders')
  } catch (e) {
    // 错误由拦截器展示，弹窗保持打开
  } finally {
    buying.value = false
  }
}

// ============ 私信卖家 ============
const openMsgDialog = () => {
  // 打开弹窗前的硬性判断：未登录、本人是卖家均不允许打开输入框
  if (!localStorage.getItem('token')) {
    ElMessage.warning('请先登录后再私信卖家')
    return
  }
  if (isSelf.value) {
    ElMessage.warning('不能给自己发送私信')
    return
  }
  msgContent.value = ''
  msgDialogVisible.value = true
}

const sendMsg = async () => {
  if (!msgContent.value.trim()) {
    ElMessage.warning('请输入私信内容')
    return
  }
  if (msgSending.value) return
  msgSending.value = true
  try {
    await request.post('/message/sendDirect', {
      receiverId: product.value.userId,
      content: msgContent.value.trim()
    })
    ElMessage.success('消息已发送给卖家')
    msgDialogVisible.value = false
  } catch (e) {
    // 错误由拦截器展示
  } finally {
    msgSending.value = false
  }
}

// ============ 相似商品跳转 ============
const goSimilar = (id) => {
  router.push(`/product/${id}`)
}

// 监听路由参数变化（点击相似商品后重新加载）
watch(() => route.params.id, (newId) => {
  if (newId) {
    product.value = null
    load()
  }
})

onMounted(load)

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/home')
  }
}
</script>

<style scoped>
/* ============ 返回栏 ============ */
.back-bar { margin-bottom: var(--space-sm); }
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

.page { padding: var(--space-lg); max-width: 1280px; margin: 0 auto; }

/* ============ 主信息卡 ============ */
.main-card { margin-bottom: 20px; }
.main-grid {
  display: grid;
  grid-template-columns: 380px 1fr;
  gap: 36px;
  align-items: start;
}
.img-wrap {
  position: relative;
  width: 380px;
  height: 380px;
  background: linear-gradient(135deg, #EFF6FF, #DBEAFE);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;
}
.img-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.fallback-char {
  font-size: 130px;
  font-weight: 700;
  color: #3B82F6;
  opacity: 0.55;
}
.badge-new {
  position: absolute;
  top: 16px;
  left: 16px;
  background: linear-gradient(135deg, #60A5FA, #3B82F6);
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  padding: 5px 12px;
  border-radius: 4px;
  letter-spacing: 1px;
  box-shadow: 0 3px 8px rgba(59, 130, 246, 0.35);
}
.view-count {
  position: absolute;
  bottom: 14px;
  right: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
  background: rgba(26, 42, 58, 0.7);
  color: #fff;
  font-size: 12px;
  padding: 5px 10px;
  border-radius: 14px;
  backdrop-filter: blur(4px);
}

.info-wrap { padding-top: 4px; }
.cat-tag {
  display: inline-block;
  font-size: 12px;
  color: #3B82F6;
  background: #EFF6FF;
  padding: 4px 12px;
  border-radius: 4px;
  margin-bottom: 14px;
  letter-spacing: 0.5px;
}
.title {
  font-size: 28px;
  font-weight: 700;
  color: #1A2A3A;
  margin: 0 0 20px;
  line-height: 1.4;
  letter-spacing: 0.5px;
}
.price-row {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 22px;
  padding-bottom: 20px;
  border-bottom: 1px dashed #E3E6EA;
}
.price {
  color: #3B82F6;
  font-size: 34px;
  font-weight: 700;
  line-height: 1;
}
.meta-row {
  display: flex;
  gap: 40px;
  margin-bottom: 26px;
}
.meta-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.meta-item label {
  font-size: 12px;
  color: #9AA3AD;
  letter-spacing: 0.5px;
}
.meta-item span {
  font-size: 14px;
  color: #495057;
}
.status-tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}
.status-0 { background: #FDF3E1; color: #F5A623; }
.status-1 { background: #E8F8F7; color: #4ECDC4; }
.status-2 { background: #FDECE7; color: #E54D2E; }
.status-3 { background: #F0F1F4; color: #9AA3AD; }

.action-row {
  display: flex;
  gap: 14px;
  align-items: center;
}
.fav-btn {
  height: 46px;
  padding: 0 22px !important;
  font-size: 15px;
  font-weight: 600;
  border: 1px solid #E3E6EA !important;
  background: #fff !important;
  color: #495057 !important;
  border-radius: 8px !important;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all .2s;
}
.fav-btn:hover {
  border-color: #FFC5AD !important;
  color: #3B82F6 !important;
  background: #EFF6FF !important;
}
.fav-btn.is-fav {
  border-color: #FFC5AD !important;
  color: #3B82F6 !important;
  background: #EFF6FF !important;
}
.heart-icon { transition: all .2s; }

.buy-btn {
  height: 46px;
  padding: 0 36px !important;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 2px;
  border-radius: 8px !important;
  box-shadow: 0 4px 14px rgba(59, 130, 246, 0.28);
}
.buy-tip {
  margin-top: 12px;
  font-size: 12px;
  color: #9AA3AD;
  letter-spacing: 0.3px;
}

/* ============ 中部 grid ============ */
.mid-grid {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 20px;
  margin-bottom: 20px;
}
.card-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 700;
  color: #1A2A3A;
  letter-spacing: 0.5px;
}
.card-title .bar {
  width: 4px;
  height: 16px;
  background: linear-gradient(180deg, #60A5FA, #3B82F6);
  border-radius: 2px;
}

.desc-body {
  font-size: 14px;
  color: #1A2A3A;
  line-height: 1.85;
  white-space: pre-wrap;
  min-height: 80px;
}

.seller-row {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 22px;
}
.seller-avatar {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: linear-gradient(135deg, #60A5FA, #3B82F6);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  font-weight: 600;
  box-shadow: 0 4px 10px rgba(59, 130, 246, 0.25);
  overflow: hidden;
}
.seller-avatar .avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
  display: block;
}
.seller-meta { flex: 1; min-width: 0; }
.seller-name {
  font-size: 16px;
  font-weight: 600;
  color: #1A2A3A;
  margin-bottom: 4px;
}
.seller-phone {
  font-size: 13px;
  color: #9AA3AD;
  letter-spacing: 0.5px;
}
.msg-btn {
  width: 100%;
  height: 42px;
  font-size: 14px;
  font-weight: 600;
  border: 1px solid #FFC5AD !important;
  background: #EFF6FF !important;
  color: #3B82F6 !important;
  border-radius: 8px !important;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all .2s;
}
.msg-btn:hover:not(:disabled) {
  background: #DBEAFE !important;
}
.msg-btn:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}
.seller-tip {
  margin-top: 10px;
  font-size: 12px;
  color: #9AA3AD;
  text-align: center;
}

/* ============ 相似商品 ============ */
.similar-card { margin-bottom: 20px; }
.similar-scroll {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  padding-bottom: 8px;
  scroll-behavior: smooth;
}
.similar-scroll::-webkit-scrollbar { height: 6px; }
.similar-scroll::-webkit-scrollbar-thumb { background: #E3E6EA; border-radius: 3px; }
.similar-scroll::-webkit-scrollbar-thumb:hover { background: #FFC5AD; }
.similar-item {
  flex-shrink: 0;
  width: 200px;
  border: 1px solid #ECEEF1;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  transition: all .2s;
  background: #fff;
}
.similar-item:hover {
  transform: translateY(-3px);
  border-color: #FFC5AD;
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.15);
}
.sim-img {
  width: 100%;
  height: 150px;
  background: linear-gradient(135deg, #EFF6FF, #DBEAFE);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.sim-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.sim-fallback {
  font-size: 50px;
  font-weight: 700;
  color: #3B82F6;
  opacity: 0.55;
}
.sim-title {
  padding: 10px 12px 4px;
  font-size: 14px;
  font-weight: 600;
  color: #1A2A3A;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.sim-bottom {
  padding: 0 12px 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.sim-price {
  color: #3B82F6;
  font-size: 15px;
  font-weight: 700;
}
.sim-cond {
  font-size: 11px;
  color: #9AA3AD;
  background: #F7F8FA;
  padding: 2px 8px;
  border-radius: 4px;
}
.empty-tip {
  text-align: center;
  padding: 30px 0;
  color: #9AA3AD;
  font-size: 14px;
}

/* ============ 下单弹窗 ============ */
.dialog-content { padding: 0 4px; }
.dialog-row {
  display: flex;
  align-items: flex-start;
  padding: 12px 0;
  border-bottom: 1px dashed #F0F1F4;
}
.dialog-row:last-of-type { border-bottom: none; }
.dialog-row label {
  width: 80px;
  flex-shrink: 0;
  color: #9AA3AD;
  font-size: 13px;
  letter-spacing: 0.5px;
  padding-top: 1px;
}
.dialog-row .val {
  flex: 1;
  color: #1A2A3A;
  font-size: 14px;
  word-break: break-all;
}
.dialog-row .price-text {
  color: #3B82F6;
  font-size: 18px;
  font-weight: 700;
}
.dialog-tip {
  margin-top: 18px;
}
.addr-tip {
  margin: 6px 0 0;
  font-size: 12px;
  color: #9AA3AD;
}
.addr-tip .link {
  color: #60A5FA;
  cursor: pointer;
}

/* ============ 私信弹窗 ============ */
.msg-textarea {
  margin-top: 14px;
}
.msg-textarea :deep(.el-textarea__inner) {
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.6;
}

/* ============ 加载中 ============ */
.loading-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  gap: 12px;
  color: #9AA3AD;
  font-size: 14px;
}
.loading-box .is-loading {
  font-size: 28px;
  color: #3B82F6;
}

/* ============ 响应式 ============ */
@media (max-width: 992px) {
  .main-grid { grid-template-columns: 1fr; gap: 24px; }
  .img-wrap { width: 100%; height: 320px; max-width: 480px; margin: 0 auto; }
  .mid-grid { grid-template-columns: 1fr; }
}
@media (max-width: 768px) {
  .page { padding: 16px; }
  .img-wrap { height: 240px; }
  .fallback-char { font-size: 80px; }
  .title { font-size: 22px; }
  .price { font-size: 26px; }
  .meta-row { gap: 24px; flex-wrap: wrap; }
  .action-row { flex-direction: column; align-items: stretch; }
  .fav-btn, .buy-btn { width: 100%; }
}
</style>
