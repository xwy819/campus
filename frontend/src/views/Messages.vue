<template>
  <div class="messages-page">
    <!-- 标签切换 -->
    <div class="tabs">
      <span
        class="tab"
        :class="{ active: activeTab === 'chat' }"
        @click="switchTab('chat')"
      >私聊会话</span>
      <span
        class="tab"
        :class="{ active: activeTab === 'notice' }"
        @click="switchTab('notice')"
      >站内消息</span>
    </div>

    <!-- 私聊会话 -->
    <div v-show="activeTab === 'chat'" class="chat-layout">
      <!-- 左栏：会话列表 -->
      <div class="conv-list">
        <div
          v-for="c in conversations"
          :key="c.peerId"
          class="conv-item"
          :class="{ active: currentPeerId === c.peerId }"
          @click="openConversation(c.peerId)"
        >
          <div class="conv-avatar">
            <img v-if="c.peerAvatar" :src="c.peerAvatar" class="avatar-img" />
            <span v-else>{{ (c.peerNickname || '·').charAt(0) }}</span>
          </div>
          <div class="conv-info">
            <div class="conv-top">
              <span class="conv-name">{{ c.peerNickname || '未知用户' }}</span>
              <span class="conv-time">{{ formatTime(c.lastTime) }}</span>
            </div>
            <div class="conv-last">{{ formatContent(c.lastContent) }}</div>
          </div>
          <span v-if="c.unreadCount > 0" class="conv-badge">
            {{ c.unreadCount > 99 ? '99+' : c.unreadCount }}
          </span>
          <button class="conv-clear" @click.stop="confirmClear(c.peerId)" title="清空记录">清空记录</button>
        </div>
        <div v-if="conversations.length === 0" class="conv-empty">
          <svg viewBox="0 0 24 24" width="40" height="40" aria-hidden="true">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2Z" fill="none" stroke="#D0D5DC" stroke-width="1.5"/>
          </svg>
          <p>暂无聊天会话</p>
          <p class="conv-tip">去商品详情联系卖家发起对话</p>
          <el-button type="primary" size="small" @click="$router.push('/home')">去逛逛</el-button>
        </div>
      </div>

      <!-- 右栏：聊天窗口 -->
      <div class="chat-panel">
        <template v-if="currentPeerId">
          <div class="chat-header">
            <div class="chat-peer">
              <span class="peer-avatar">
                <img v-if="currentPeer?.peerAvatar" :src="currentPeer.peerAvatar" class="avatar-img" />
                <span v-else>{{ (currentPeer?.peerNickname || '·').charAt(0) }}</span>
              </span>
              <span class="peer-name">{{ currentPeer?.peerNickname || '未知用户' }}</span>
            </div>
          </div>
          <div ref="msgListRef" class="msg-list">
            <div v-if="messages.length === 0" class="chat-empty">
              <p>开始对话，发送第一条消息</p>
            </div>
            <div
              v-for="m in messages"
              :key="m.id"
              class="msg-row"
              :class="m.senderId === myId ? 'mine' : 'theirs'"
            >
              <!-- 对方头像 -->
              <div v-if="m.senderId !== myId" class="msg-avatar">
                <img v-if="currentPeer?.peerAvatar" :src="currentPeer.peerAvatar" class="avatar-img" />
                <span v-else>{{ (currentPeer?.peerNickname || '·').charAt(0) }}</span>
              </div>
              <!-- 气泡 + 时间 -->
              <div class="msg-col">
                <div class="msg-bubble" :class="{ 'order-card': !!parseOrder(m) }">
                  <template v-if="parseOrder(m)">
                    <div class="order-card-body">
                      <img v-if="parseOrder(m).imageUrl" :src="parseOrder(m).imageUrl" class="order-thumb" />
                      <div class="order-info">
                        <div class="order-title">{{ parseOrder(m).title }}</div>
                        <div class="order-price">¥{{ parseOrder(m).price?.toFixed(2) }}</div>
                        <div class="order-tags">
                          <span class="order-role-tag" :class="parseOrder(m).role === '买家' ? 'role-buyer' : 'role-seller'">
                            {{ parseOrder(m).role }}
                          </span>
                          <el-tag :type="orderStatusTag(parseOrder(m).status)" effect="light" size="small">
                            {{ orderStatusText(parseOrder(m).status) }}
                          </el-tag>
                        </div>
                      </div>
                    </div>
                  </template>
                  <template v-else>{{ m.content }}</template>
                </div>
                <span class="msg-time">{{ formatMsgTime(m.createTime) }}</span>
              </div>
              <!-- 我的头像 -->
              <div v-if="m.senderId === myId" class="msg-avatar me">
                <img v-if="myAvatar" :src="myAvatar" class="avatar-img" />
                <span v-else>{{ (myName || '我').charAt(0) }}</span>
              </div>
            </div>
          </div>
          <div class="chat-input">
            <textarea
              v-model="draft"
              class="input-box"
              placeholder="输入消息，按 Enter 发送，Shift+Enter 换行"
              @keydown.enter.exact.prevent="sendMessage"
              rows="2"
            />
            <button class="order-btn" @click="openOrderPicker">发送订单</button>
            <button class="send-btn" :disabled="!draft.trim()" @click="sendMessage">发送</button>
          </div>
        </template>
        <div v-else class="chat-placeholder">
          <svg viewBox="0 0 24 24" width="48" height="48" aria-hidden="true">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2Z" fill="none" stroke="#D0D5DC" stroke-width="1.5"/>
          </svg>
          <p>选择一个会话开始聊天</p>
        </div>
      </div>
    </div>

    <!-- 站内消息 -->
    <div v-show="activeTab === 'notice'" class="notice-layout">
      <el-table :data="noticeList" style="width:100%" empty-text="暂无站内消息">
        <el-table-column label="内容" min-width="300">
          <template #default="{ row }">
            <div class="msg-content" :class="{ unread: row.isRead !== 1 }" @click="markRead(row)">
              <span v-if="row.isRead !== 1" class="dot"></span>
              {{ formatContent(row.content) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.isRead === 1" type="info" effect="light">已读</el-tag>
            <el-tag v-else type="warning" effect="light">未读</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="180" />
        <el-table-column label="操作" width="70" fixed="right">
          <template #default="{ row }">
            <button class="notice-del" title="删除" @click.stop="confirmDelete(row)">
              <svg viewBox="0 0 24 24" width="16" height="16" aria-hidden="true">
                <path d="M3 6h18M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2M6 6l1 14a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2l1-14" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-if="total > 0"
        v-model:current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next, total"
        @current-change="loadNotices"
        class="pager"
      />
    </div>

    <!-- 订单选择弹窗 -->
    <el-dialog
      v-model="orderPickerVisible"
      title="选择要发送的订单"
      width="560px"
      :close-on-click-modal="false"
    >
      <!-- 标签切换 -->
      <div class="picker-tabs">
        <button
          v-for="t in orderTabs" :key="t.key"
          class="picker-tab" :class="{ active: orderTab === t.key }"
          @click="orderTab = t.key"
        >{{ t.label }}</button>
      </div>

      <div v-if="filteredOrders.length > 0" class="order-picker-list">
        <div v-for="o in filteredOrders" :key="o.id" class="order-picker-item">
          <img :src="o.productImageUrl" class="order-picker-thumb" />
          <div class="order-picker-info">
            <div class="order-picker-title">{{ o.productTitle }}</div>
            <div class="order-picker-meta">
              <span class="order-picker-price">¥{{ o.finalPrice?.toFixed(2) }}</span>
              <span class="order-picker-role" :class="orderRoleClass(o)">
                {{ o.buyerId === myId ? '买家' : '卖家' }}
              </span>
              <el-tag :type="orderStatusTag(o.status)" effect="light" size="small">
                {{ orderStatusText(o.status) }}
              </el-tag>
            </div>
          </div>
          <el-button type="primary" size="small" @click="selectOrder(o)">选择</el-button>
        </div>
      </div>
      <div v-else class="order-picker-empty">
        <p>暂无订单</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'
import { userStore } from '../store/user'

const activeTab = ref('chat')
const myId = computed(() => parseInt(localStorage.getItem('userId') || '0'))
const myName = computed(() => localStorage.getItem('name') || '我')
const myAvatar = computed(() => userStore.avatar)

// 私聊会话
const conversations = ref([])
const currentPeerId = ref(null)
const messages = ref([])
const draft = ref('')
const msgListRef = ref(null)

// 站内消息（独立 ref，避免污染私聊 messages）
const noticeList = ref([])

// 订单选择
const orderPickerVisible = ref(false)
const allOrders = ref([])
const orderTab = ref('all')
const orderTabs = [
  { key: 'all', label: '全部' },
  { key: 'buy', label: '我买到的' },
  { key: 'sell', label: '我卖出的' }
]
const filteredOrders = computed(() => {
  if (orderTab.value === 'buy') return allOrders.value.filter(o => o.buyerId === myId.value)
  if (orderTab.value === 'sell') return allOrders.value.filter(o => o.sellerId === myId.value)
  return allOrders.value
})

const currentPeer = computed(() =>
  conversations.value.find(c => c.peerId === currentPeerId.value)
)

// 站内消息
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const switchTab = (tab) => {
  activeTab.value = tab
  if (tab === 'notice') {
    loadNotices()
  } else {
    loadConversations()
    // 切回私聊时，若已有选中会话则重新加载消息（防止被站内消息覆盖）
    if (currentPeerId.value) loadMessages(currentPeerId.value)
  }
}

const loadConversations = async () => {
  const res = await request.get('/message/conversations')
  // 过滤掉自己和自己的会话（peerId === myId）
  conversations.value = (res.data || []).filter(c => c.peerId !== myId.value)
}

const openConversation = async (peerId) => {
  // 防御：不允许打开和自己的会话
  if (peerId === myId.value) return
  currentPeerId.value = peerId
  await loadMessages(peerId)
  // 标记已读
  try {
    await request.post('/message/readAll', { peerId })
    const conv = conversations.value.find(c => c.peerId === peerId)
    if (conv) conv.unreadCount = 0
  } catch (e) { /* ignore */ }
  await nextTick()
  scrollToBottom()
}

const loadMessages = async (peerId) => {
  const res = await request.get(`/message/conversation/${peerId}`)
  messages.value = res.data || []
}

const sendMessage = async () => {
  const content = draft.value.trim()
  if (!content || !currentPeerId.value) return
  const peerId = currentPeerId.value
  draft.value = ''
  // 乐观更新
  messages.value.push({
    id: Date.now(),
    senderId: myId.value,
    receiverId: peerId,
    content,
    createTime: new Date()
  })
  await nextTick()
  scrollToBottom()
  try {
    await request.post('/message/sendDirect', { receiverId: peerId, content })
  } catch (e) {
    // 发送失败回滚提示由拦截器展示
  }
}

// ============ 清空聊天记录 ============
const confirmClear = async (peerId) => {
  try {
    await ElMessageBox.confirm(
      '确定清空该会话全部聊天记录？操作不可恢复，会话不会被删除。',
      '清空记录',
      {
        confirmButtonText: '确认清空',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )
    await request.delete(`/message/conversation/${peerId}`)
    ElMessage.success('已清空聊天记录')
    // 刷新会话列表（占位消息会成为最后一条）
    await loadConversations()
    // 如果当前打开的是该会话，刷新消息
    if (currentPeerId.value === peerId) {
      await loadMessages(peerId)
      await nextTick()
      scrollToBottom()
    }
  } catch (e) { /* 用户取消 */ }
}

// ============ 发送订单卡片 ============
const openOrderPicker = async () => {
  if (!currentPeerId.value) return
  try {
    const res = await request.get('/order/page', { params: { pageNum: 1, pageSize: 999 } })
    allOrders.value = res.data?.records || []
  } catch (e) {
    allOrders.value = []
  }
  orderTab.value = 'all'
  orderPickerVisible.value = true
}

const orderRoleClass = (o) => o.buyerId === myId.value ? 'role-buyer' : 'role-seller'

const selectOrder = async (order) => {
  const peerId = currentPeerId.value
  if (!peerId) return
  const isBuyer = order.buyerId === myId.value
  // 构造订单卡片内容（JSON），不含 base64 图片避免超长
  const cardContent = JSON.stringify({
    type: 'order',
    orderId: order.id,
    title: order.productTitle,
    price: order.finalPrice,
    status: order.status,
    role: isBuyer ? '买家' : '卖家'
  })
  // 乐观更新
  const tempId = Date.now()
  messages.value.push({
    id: tempId,
    senderId: myId.value,
    receiverId: peerId,
    content: cardContent,
    createTime: new Date()
  })
  orderPickerVisible.value = false
  await nextTick()
  scrollToBottom()
  try {
    await request.post('/message/send', {
      orderId: order.id,
      receiverId: peerId,
      content: cardContent
    })
  } catch (e) {
    // 失败由拦截器提示，乐观消息不回滚保持体验
  }
}

// 解析消息内容是否为订单卡片
const orderCardCache = new Map()
const parseOrder = (m) => {
  if (!m || !m.content) return null
  if (orderCardCache.has(m.id)) return orderCardCache.get(m.id)
  if (typeof m.content !== 'string' || !m.content.startsWith('{')) return null
  try {
    const obj = JSON.parse(m.content)
    if (obj && obj.type === 'order') {
      orderCardCache.set(m.id, obj)
      return obj
    }
  } catch { /* not JSON */ }
  return null
}

// 格式化消息摘要（会话列表、站内消息通用）
const formatContent = (content) => {
  if (typeof content !== 'string' || !content.startsWith('{')) return content
  try {
    const obj = JSON.parse(content)
    if (obj && obj.type === 'order') {
      return `【订单】${obj.title || '商品'}`
    }
  } catch { /* not JSON */ }
  return content
}

const orderStatusText = (s) => {
  const map = { 0: '待确认', 1: '已确认', 2: '已完成', 3: '已取消' }
  return map[s] || '未知'
}
const orderStatusTag = (s) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'danger' }
  return map[s] || 'info'
}

const scrollToBottom = () => {
  if (msgListRef.value) {
    msgListRef.value.scrollTop = msgListRef.value.scrollHeight
  }
}

// 站内消息
const loadNotices = async () => {
  const res = await request.get('/message/page', { params: { pageNum: pageNum.value, pageSize: pageSize.value } })
  noticeList.value = res.data.records
  total.value = res.data.total
}

const markRead = async (row) => {
  if (row.isRead === 1) return
  try {
    await request.post('/message/read', { id: row.id })
    row.isRead = 1
  } catch (e) { /* ignore */ }
}

const confirmDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条消息吗？', '删除确认', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
      confirmButtonClass: 'el-button--danger'
    })
    await request.delete(`/message/${row.id}`)
    ElMessage.success('已删除')
    await loadNotices()
  } catch (e) { /* 用户取消或拦截器展示错误 */ }
}

const formatTime = (t) => {
  if (!t) return ''
  const s = String(t).replace('T', ' ')
  return s.length > 16 ? s.slice(5, 16) : s
}

const formatMsgTime = (t) => {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  const pad = (n) => String(n).padStart(2, '0')
  const sameDay = d.toDateString() === now.toDateString()
  if (sameDay) return `${pad(d.getHours())}:${pad(d.getMinutes())}`
  const yesterday = new Date(now); yesterday.setDate(now.getDate() - 1)
  if (d.toDateString() === yesterday.toDateString()) return `昨天 ${pad(d.getHours())}:${pad(d.getMinutes())}`
  return `${d.getMonth() + 1}/${d.getDate()} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

onMounted(() => {
  loadConversations()
})
</script>

<style scoped>
.messages-page {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #ECEEF1;
  box-shadow: 0 2px 12px rgba(15, 27, 45, 0.06);
  overflow: hidden;
}

/* 标签 */
.tabs {
  display: flex;
  border-bottom: 1px solid #F0F1F4;
  padding: 0 20px;
}
.tab {
  padding: 14px 20px;
  font-size: 15px;
  color: #6C757D;
  cursor: pointer;
  position: relative;
  transition: all .2s;
  font-weight: 500;
}
.tab:hover { color: #60A5FA; }
.tab.active {
  color: #60A5FA;
  font-weight: 600;
}
.tab.active::after {
  content: '';
  position: absolute;
  left: 20px;
  right: 20px;
  bottom: 0;
  height: 2px;
  background: #60A5FA;
  border-radius: 2px;
}

/* 私聊布局 */
.chat-layout {
  display: flex;
  height: calc(100vh - 180px);
  min-height: 500px;
}

/* 左栏 会话列表 */
.conv-list {
  width: 300px;
  flex-shrink: 0;
  border-right: 1px solid #F0F1F4;
  overflow-y: auto;
}
.conv-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  cursor: pointer;
  border-bottom: 1px solid #F7F8FA;
  transition: background .15s;
  position: relative;
}
.conv-item:hover { background: #F7F8FA; }
.conv-item.active { background: #EFF6FF; }
.conv-avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: linear-gradient(135deg, #60A5FA, #3B82F6);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 600;
  flex-shrink: 0;
}
.conv-info { flex: 1; min-width: 0; }
.conv-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}
.conv-name {
  font-size: 14px;
  font-weight: 600;
  color: #1A2A3A;
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.conv-time { font-size: 11px; color: #B6BDC4; flex-shrink: 0; }
.conv-last {
  font-size: 12px;
  color: #9AA3AD;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.conv-badge {
  position: absolute;
  top: 10px;
  right: 12px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  background: #FF4D4F;
  color: #fff;
  font-size: 10px;
  font-weight: 700;
  border-radius: 9px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
}
/* 清空记录按钮：默认隐藏，hover 该行才显示 */
.conv-clear {
  position: absolute;
  right: 12px;
  bottom: 12px;
  background: none;
  border: none;
  padding: 2px 6px;
  font-size: 11px;
  color: #9AA3AD;
  cursor: pointer;
  border-radius: 6px;
  opacity: 0;
  transition: all .15s;
  z-index: 2;
}
.conv-item:hover .conv-clear { opacity: 1; }
.conv-clear:hover { color: #EF4444; background: #FEF2F2; }
.conv-empty {
  padding: 60px 20px;
  text-align: center;
  color: #9AA3AD;
}
.conv-empty p { margin: 8px 0; font-size: 14px; }
.conv-empty .conv-tip { font-size: 12px; color: #B6BDC4; }

/* 右栏 聊天窗口 */
.chat-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.chat-header {
  padding: 14px 20px;
  border-bottom: 1px solid #F0F1F4;
}
.chat-peer {
  display: flex;
  align-items: center;
  gap: 10px;
}
.peer-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #60A5FA, #3B82F6);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
}
.peer-name { font-size: 15px; font-weight: 600; color: #1A2A3A; }

.msg-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #F7F8FA;
  display: flex;
  flex-direction: column;
  gap: 18px;
}
/* 滚动条美化 */
.msg-list::-webkit-scrollbar { width: 6px; }
.msg-list::-webkit-scrollbar-thumb { background: #D1D5DB; border-radius: 3px; }
.msg-list::-webkit-scrollbar-thumb:hover { background: #9CA3AF; }
.msg-list::-webkit-scrollbar-track { background: transparent; }
.chat-empty {
  margin: auto;
  color: #B6BDC4;
  font-size: 14px;
}
.msg-row {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  max-width: 70%;
}
.msg-row.mine {
  align-self: flex-end;
}
.msg-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: #1A2A3A;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
  /* 让头像与气泡底部对齐（抵消下方时间的高度） */
  margin-bottom: 18px;
}
.msg-avatar.me { background: linear-gradient(135deg, #60A5FA, #3B82F6); }
/* 气泡 + 时间纵向排列 */
.msg-col {
  display: flex;
  flex-direction: column;
  max-width: 100%;
}
.msg-row.mine .msg-col { align-items: flex-end; }
.msg-row.theirs .msg-col { align-items: flex-start; }
.msg-bubble {
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
  max-width: 420px;
}
.msg-row.theirs .msg-bubble {
  background: #fff;
  color: #1A2A3A;
  border: 1px solid #E5E7EB;
  border-bottom-left-radius: 4px;
}
.msg-row.mine .msg-bubble {
  background: linear-gradient(135deg, #60A5FA, #3B82F6);
  color: #fff;
  border-bottom-right-radius: 4px;
}
/* 消息时间 */
.msg-time {
  font-size: 11px;
  color: #94A3B8;
  margin-top: 4px;
  padding: 0 2px;
}

.chat-input {
  display: flex;
  gap: 10px;
  padding: 14px 20px;
  border-top: 1px solid #F0F1F4;
  background: #fff;
  align-items: flex-end;
}
.input-box {
  flex: 1;
  resize: none;
  border: 1px solid #E3E6EA;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  outline: none;
  font-family: inherit;
  transition: border-color .2s;
}
.input-box:focus { border-color: #60A5FA; }
.send-btn {
  height: 40px;
  padding: 0 22px;
  background: linear-gradient(135deg, #60A5FA, #3B82F6);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all .2s;
}
.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.send-btn:not(:disabled):hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.32);
}

/* 发送订单按钮：浅蓝边框小按钮 */
.order-btn {
  height: 40px;
  padding: 0 14px;
  background: #fff;
  color: #3B82F6;
  border: 1px solid #BFDBFE;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all .2s;
  flex-shrink: 0;
}
.order-btn:hover {
  background: #EFF6FF;
  border-color: #3B82F6;
}

/* 订单卡片消息 */
.msg-bubble.order-card { padding: 8px; min-width: 220px; max-width: 280px; }
.order-card-body {
  display: flex;
  gap: 10px;
  align-items: center;
}
.order-thumb {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  object-fit: cover;
  flex-shrink: 0;
  background: #F1F5F9;
}
.order-info { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 4px; }
.order-title {
  font-size: 13px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.order-price { font-size: 15px; font-weight: 700; color: #EF4444; }
/* 自己发的订单卡片（蓝色气泡）文字颜色适配 */
.msg-row.mine .order-title { color: #fff; }
.msg-row.mine .order-price { color: #FDE68A; }

.chat-placeholder {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #B6BDC4;
  gap: 12px;
}
.chat-placeholder p { font-size: 14px; }

/* 站内消息 */
.notice-layout {
  padding: 20px;
}
.pager {
  margin-top: 20px;
  justify-content: center;
}
.msg-content {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  line-height: 1.6;
}
.msg-content.unread { font-weight: 600; color: #1A2A3A; }
.msg-content .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #3B82F6;
  flex-shrink: 0;
}

.notice-del {
  background: none;
  border: none;
  padding: 4px 6px;
  color: #f53f3f;
  cursor: pointer;
  border-radius: 6px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: all .15s;
}
.notice-del:hover {
  background: #fff1f0;
  color: #d93838;
}

/* 订单选择弹窗 */
.picker-tabs {
  display: flex;
  gap: 4px;
  border-bottom: 1px solid #EEF0F4;
  margin-bottom: 14px;
}
.picker-tab {
  padding: 8px 16px;
  font-size: 13px;
  font-weight: 500;
  color: #64748B;
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  cursor: pointer;
  transition: all .2s;
  margin-bottom: -1px;
}
.picker-tab:hover { color: #3B82F6; }
.picker-tab.active { color: #3B82F6; border-bottom-color: #3B82F6; font-weight: 600; }

.order-picker-list { display: flex; flex-direction: column; gap: 10px; max-height: 420px; overflow-y: auto; }
.order-picker-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  border: 1px solid #EEF0F4;
  border-radius: 10px;
  transition: all .2s;
}
.order-picker-item:hover { border-color: #BFDBFE; background: #F8FAFC; }
.order-picker-thumb {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
  flex-shrink: 0;
  background: #F1F5F9;
}
.order-picker-info { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 4px; }
.order-picker-title {
  font-size: 14px;
  font-weight: 600;
  color: #1A2A3A;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.order-picker-meta { display: flex; align-items: center; gap: 8px; }
.order-picker-price { font-size: 15px; font-weight: 700; color: #EF4444; }
.order-picker-role { font-size: 11px; padding: 1px 7px; border-radius: 8px; font-weight: 500; }
.order-picker-role.role-buyer { color: #3B82F6; background: #EFF6FF; }
.order-picker-role.role-seller { color: #10B981; background: #ECFDF5; }
.order-picker-empty { text-align: center; color: #9AA3AD; padding: 40px 0; }

/* 订单卡片内的角色标签 */
.order-tags { display: flex; align-items: center; gap: 6px; }
.order-role-tag { font-size: 11px; padding: 1px 7px; border-radius: 8px; font-weight: 500; display: inline-block; }
.order-role-tag.role-buyer { color: #3B82F6; background: #EFF6FF; }
.order-role-tag.role-seller { color: #10B981; background: #ECFDF5; }
/* 自己发的蓝底卡片中角色标签颜色适配 */
.msg-row.mine .order-role-tag.role-buyer { color: #BFDBFE; background: rgba(255,255,255,0.15); }
.msg-row.mine .order-role-tag.role-seller { color: #A7F3D0; background: rgba(255,255,255,0.15); }

@media (max-width: 768px) {
  .chat-layout { flex-direction: column; height: auto; }
  .conv-list { width: 100%; max-height: 240px; border-right: none; border-bottom: 1px solid #F0F1F4; }
  .msg-row { max-width: 85%; }
}

/* 头像图片通用：填满父容器，圆形 */
.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
  display: block;
}
</style>
