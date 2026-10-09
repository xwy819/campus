<template>
  <el-card>
    <el-form inline style="margin-bottom:16px">
      <el-form-item>
        <el-input v-model="query.keyword" placeholder="搜索标题" clearable style="width:180px" @keyup.enter="search" />
      </el-form-item>
      <el-form-item>
        <el-select v-model="query.categoryId" placeholder="全部分类" clearable style="width:130px">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-select v-model="query.status" placeholder="全部状态" clearable style="width:120px">
          <el-option label="待审核" :value="0" />
          <el-option label="在售" :value="1" />
          <el-option label="已售" :value="2" />
          <el-option label="已下架" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button type="success" :disabled="!selected.length" @click="batchApprove">批量审核通过</el-button>
        <el-button type="warning" :disabled="!selected.length" @click="batchOffShelf">批量下架</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="products" @selection-change="v => selected = v" v-loading="loading">
      <el-table-column type="selection" width="45" />
      <el-table-column prop="title" label="标题" min-width="160" />
      <el-table-column prop="categoryName" label="分类" width="100" />
      <el-table-column prop="sellerNickname" label="卖家" width="100" />
      <el-table-column prop="price" label="价格" width="80" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.status === 0" type="warning" size="small">待审核</el-tag>
          <el-tag v-else-if="row.status === 1" type="success" size="small">在售</el-tag>
          <el-tag v-else-if="row.status === 2" type="info" size="small">已售</el-tag>
          <el-tag v-else type="danger" size="small">已下架</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" size="small" type="success" @click="singleApprove(row.id)">审核通过</el-button>
          <el-button v-if="row.status === 1" size="small" type="warning" @click="singleOffShelf(row.id)">下架</el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
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

const products = ref([])
const categories = ref([])
const selected = ref([])
const total = ref(0)
const loading = ref(false)
const query = ref({ keyword: '', categoryId: null, status: null, pageNum: 1, pageSize: 12 })

const loadCategories = async () => {
  const res = await request.get('/category/list')
  categories.value = res.data
}

const load = async () => {
  loading.value = true
  try {
    const res = await request.get('/product/admin/page', { params: query.value })
    products.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const search = () => {
  query.value.pageNum = 1
  load()
}

const batchApprove = async () => {
  await request.post('/product/admin/batchApprove', { ids: selected.value.map(s => s.id) })
  ElMessage.success('批量审核通过')
  load()
}

const batchOffShelf = async () => {
  await request.post('/product/admin/batchOffShelf', { ids: selected.value.map(s => s.id) })
  ElMessage.success('批量下架')
  load()
}

const singleApprove = async (id) => {
  await request.post('/product/admin/batchApprove', { ids: [id] })
  ElMessage.success('已审核通过')
  load()
}

const singleOffShelf = async (id) => {
  await ElMessageBox.confirm('确定下架该商品？', '提示', { type: 'warning' })
  await request.post('/product/admin/batchOffShelf', { ids: [id] })
  ElMessage.success('已下架')
  load()
}

const remove = async (id) => {
  await ElMessageBox.confirm('确定删除该商品？删除后不可恢复。', '提示', { type: 'warning' })
  await request.delete('/product/admin/' + id)
  ElMessage.success('删除成功')
  load()
}

onMounted(() => {
  loadCategories()
  load()
})
</script>
