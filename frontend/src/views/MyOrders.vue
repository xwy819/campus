<template>
  <div class="page page-fade-in">
    <header class="page-header">
      <h2 class="page-title">我的订单</h2>
      <span class="page-subtitle">共 {{ filtered.length }} 条</span>
    </header>

    <!-- tab 切换 -->
    <div class="tabs">
      <button
        v-for="t in tabs" :key="t.key"
        class="tab" :class="{ active: activeTab === t.key }"
        @click="switchTab(t.key)"
      >{{ t.label }}</button>
    </div>

    <!-- 筛选 -->
    <div class="filter">
      <el-select v-model="query.status" placeholder="全部状态" clearable style="width:160px">
        <el-option label="待确认" :value="0" />
        <el-option label="已确认" :value="1" />
        <el-option label="已完成" :value="2" />
        <el-option label="已取消" :value="3" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
    </div>

    <el-table :data="paged" style="width:100%;margin-top:8px" empty-text="暂无订单">
      <el-table-column prop="productTitle" label="商品" min-width="200" show-overflow-tooltip />
      <el-table-column label="金额" width="110">
        <template #default="{ row }">
          <span class="price">￥{{ row.finalPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column label="角色" width="90">
        <template #default="{ row }">
          <span v-if="isBuyer(row)" class="role-buyer">我买的</span>
          <span v-else class="role-seller">我卖的</span>
        </template>
      </el-table-column>
      <el-table-column prop="buyerNickname" label="买家" width="110" />
      <el-table-column prop="sellerNickname" label="卖家" width="110" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 0" type="warning" effect="light">待确认</el-tag>
          <el-tag v-else-if="row.status === 1" type="primary" effect="light">已确认</el-tag>
          <el-tag v-else-if="row.status === 2" type="success" effect="light">已完成</el-tag>
          <el-tag v-else type="danger" effect="light">已取消</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button v-if="isSeller(row) && row.status === 0" size="small" type="primary" link @click="change(row.id, 1)">确认发货</el-button>
          <el-button v-if="isBuyer(row) && row.status === 0" size="small" type="danger" link @click="change(row.id, 3)">取消订单</el-button>
          <el-button v-if="isBuyer(row) && row.status === 1" size="small" type="success" link @click="change(row.id, 2)">确认收货</el-button>
          <span v-if="!hasAction(row)" class="no-action">—</span>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="filtered.length > 10"
      v-model:current-page="query.pageNum"
      :page-size="query.pageSize"
      :total="filtered.length"
      layout="prev, pager, next, total"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const allOrders = ref([])
const currentUserId = ref(null)

const tabs = [
  { key: 'all', label: '全部订单' },
  { key: 'buy', label: '我买到的' },
  { key: 'sell', label: '我卖出的' }
]
const activeTab = ref('all')

const query = reactive({ status: null, pageNum: 1, pageSize: 10 })

// 先按 tab 筛选角色，再按 status 筛选
const filtered = computed(() => {
  let list = allOrders.value
  if (activeTab.value === 'buy') list = list.filter(o => isBuyer(o))
  if (activeTab.value === 'sell') list = list.filter(o => isSeller(o))
  if (query.status !== null && query.status !== undefined && query.status !== '') {
    list = list.filter(o => o.status === query.status)
  }
  return list
})

// 分页切片
const paged = computed(() => {
  const start = (query.pageNum - 1) * query.pageSize
  return filtered.value.slice(start, start + query.pageSize)
})

const switchTab = (k) => {
  activeTab.value = k
  query.pageNum = 1
}

const load = async () => {
  const res = await request.get('/order/page', { params: { pageNum: 1, pageSize: 999 } })
  allOrders.value = res.data.records || []
}

const loadUser = async () => {
  try {
    const res = await request.get('/user/profile')
    currentUserId.value = res.data?.id
  } catch {}
}

const isBuyer = (row) => !!currentUserId.value && row.buyerId === currentUserId.value
const isSeller = (row) => !!currentUserId.value && row.sellerId === currentUserId.value
const hasAction = (row) => {
  if (row.status === 0) return true
  if (row.status === 1 && isBuyer(row)) return true
  return false
}

const change = async (id, status) => {
  await request.post('/order/status', { id, status })
  ElMessage.success('操作成功')
  load()
}

onMounted(() => { loadUser(); load() })
</script>

<style scoped>
.page-header { display: flex; align-items: baseline; gap: 12px; margin-bottom: 14px; padding-left: 2px; }
.page-title { font-size: 20px; font-weight: 700; color: #1A2A3A; margin: 0; }
.page-subtitle { font-size: 13px; color: #94A3B8; }

.tabs { display: flex; gap: 4px; margin-bottom: 16px; border-bottom: 1px solid #EEF0F4; }
.tab {
  padding: 10px 18px; font-size: 14px; font-weight: 500;
  color: #64748B; background: none; border: none; border-bottom: 2px solid transparent;
  cursor: pointer; transition: all .2s; margin-bottom: -1px;
}
.tab:hover { color: #3B82F6; }
.tab.active { color: #3B82F6; border-bottom-color: #3B82F6; font-weight: 600; }

.filter { display: flex; gap: 10px; align-items: center; margin-bottom: 12px; }
.price { color: #EF4444; font-weight: 700; }
.no-action { color: #CBD5E1; }
.role-buyer { font-size: 12px; color: #3B82F6; background: #EFF6FF; padding: 2px 8px; border-radius: 8px; }
.role-seller { font-size: 12px; color: #10B981; background: #ECFDF5; padding: 2px 8px; border-radius: 8px; }

/* 表格美化 */
:deep(.el-table) {
  border-radius: 12px; overflow: hidden;
  --el-table-border-color: #EEF0F4;
  --el-table-header-bg-color: #F8FAFC;
  --el-table-row-hover-bg-color: #F0F7FF;
}
:deep(.el-table th.el-table__cell) {
  background: #F8FAFC; font-weight: 600; color: #475569; font-size: 13px;
}
:deep(.el-table td.el-table__cell) { font-size: 13px; color: #334155; }
</style>
