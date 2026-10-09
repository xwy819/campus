<template>
  <el-card>
    <el-form inline style="margin-bottom:16px">
      <el-form-item>
        <el-select v-model="query.status" placeholder="全部状态" clearable style="width:140px">
          <el-option label="待确认" :value="0" />
          <el-option label="已确认" :value="1" />
          <el-option label="已完成" :value="2" />
          <el-option label="已取消" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" @click="query.pageNum = 1; load()">查询</el-button></el-form-item>
    </el-form>

    <el-table :data="orders" v-loading="loading">
      <el-table-column prop="productTitle" label="商品" min-width="160" />
      <el-table-column prop="finalPrice" label="金额" width="90" />
      <el-table-column label="交易方式" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.deliveryType === 1" size="small">当面交易</el-tag>
          <el-tag v-else-if="row.deliveryType === 2" size="small" type="success">送货上门</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="buyerNickname" label="买家" width="100" />
      <el-table-column prop="sellerNickname" label="卖家" width="100" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag v-if="row.status === 0" type="warning">待确认</el-tag>
          <el-tag v-else-if="row.status === 1" type="success">已确认</el-tag>
          <el-tag v-else-if="row.status === 2" type="info">已完成</el-tag>
          <el-tag v-else type="danger">已取消</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" width="170">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" size="small" type="success" @click="changeStatus(row.id, 1)">确认</el-button>
          <el-button v-if="row.status === 0 || row.status === 1" size="small" type="primary" @click="changeStatus(row.id, 2)">完成</el-button>
          <el-button v-if="row.status === 0 || row.status === 1" size="small" type="danger" @click="changeStatus(row.id, 3)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="query.pageNum"
      :page-size="query.pageSize"
      :total="total"
      layout="prev, pager, next, total"
      @current-change="load"
      style="margin-top:16px;justify-content:center"
    />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const orders = ref([])
const total = ref(0)
const loading = ref(false)
const query = ref({ status: null, pageNum: 1, pageSize: 10 })

const formatTime = (t) => {
  if (!t) return ''
  return String(t).replace('T', ' ').substring(0, 19)
}

const load = async () => {
  loading.value = true
  try {
    const res = await request.get('/order/admin/page', { params: query.value })
    orders.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const changeStatus = async (id, status) => {
  const labelMap = { 1: '确认', 2: '完成', 3: '取消' }
  await ElMessageBox.confirm(`确定要${labelMap[status]}该订单吗？`, '提示', { type: 'warning' })
  await request.post('/order/status', { id, status })
  ElMessage.success('操作成功')
  load()
}

onMounted(load)
</script>
