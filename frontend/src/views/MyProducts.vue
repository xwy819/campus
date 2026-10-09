<template>
  <el-card class="page-card">
    <h3 class="page-title">我的发布</h3>
    <el-table :data="products" style="width:100%" :row-class-name="rowHover">
      <template #empty>
        <div class="empty-wrap">
          <p class="empty-text">暂无发布的闲置商品</p>
          <el-button type="primary" @click="goPublish">去发布</el-button>
        </div>
      </template>
      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
      <el-table-column prop="categoryName" label="分类" width="110" />
      <el-table-column label="价格" width="110">
        <template #default="{ row }">
          <span class="price">￥{{ row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column label="新旧程度" width="100">
        <template #default="{ row }">
          {{ conditionText(row.conditionLevel) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 0" class="tag-pending" effect="light">待审核</el-tag>
          <el-tag v-else-if="row.status === 1" class="tag-approved" effect="light">已通过</el-tag>
          <el-tag v-else-if="row.status === 2" class="tag-sold" effect="light">已售出</el-tag>
          <el-tag v-else class="tag-offshelf" effect="light">已下架</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发布时间" width="170">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="290" fixed="right">
        <template #default="{ row }">
          <el-button size="small" class="btn-preview" @click="preview(row.id)">预览</el-button>
          <el-button
            v-if="row.status !== 0"
            size="small"
            class="btn-edit"
            @click="openEdit(row.id)"
          >编辑</el-button>
          <el-button
            v-if="row.status === 1"
            size="small"
            class="btn-offshelf"
            @click="offShelf(row.id)"
          >下架</el-button>
          <el-button size="small" class="btn-delete" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const router = useRouter()
const products = ref([])

const load = async () => {
  const res = await request.get('/product/my')
  products.value = res.data
}

const conditionText = (level) => {
  const map = { 1: '全新', 2: '九成新', 3: '八成新', 4: '较旧' }
  return map[level] || '未知'
}

const preview = (id) => {
  router.push(`/product/${id}`)
}

const openEdit = (id) => {
  router.push(`/product/edit/${id}`)
}

const offShelf = async (id) => {
  try {
    await ElMessageBox.confirm('确定下架该商品吗？', '提示', { type: 'warning' })
    await request.post('/product/offShelf', { id })
    ElMessage.success('下架成功')
    load()
  } catch (e) { /* 取消或错误 */ }
}

const remove = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该发布商品吗？删除后数据不可恢复。', '删除确认', {
      type: 'warning',
      confirmButtonText: '确认删除',
      cancelButtonText: '取消'
    })
    await request.delete(`/product/${id}`)
    ElMessage.success('删除成功')
    load()
  } catch (e) { /* 取消或错误 */ }
}

const goPublish = () => {
  router.push('/publish')
}

const formatTime = (t) => {
  if (!t) return ''
  return String(t).replace('T', ' ').substring(0, 19)
}

const rowHover = () => 'table-row-hover'

onMounted(() => {
  load()
})
</script>

<style scoped>
.price { color: #3B82F6; font-weight: 700; }

/* 状态标签配色 */
.tag-pending {
  background: #F3F4F6 !important;
  border-color: #E5E7EB !important;
  color: #6B7280 !important;
}
.tag-approved {
  background: #E8F7EC !important;
  border-color: #B7E4C4 !important;
  color: #2E8B57 !important;
}
.tag-sold {
  background: #EFF6FF !important;
  border-color: #BFDBFE !important;
  color: #3B82F6 !important;
}
.tag-offshelf {
  background: #FFF7ED !important;
  border-color: #FED7AA !important;
  color: #EA580C !important;
}

/* 操作按钮配色 */
.btn-preview {
  color: #64748B;
  background: #F1F5F9;
  border: 1px solid #E2E8F0;
}
.btn-preview:hover {
  background: #E2E8F0;
  color: #475569;
}
.btn-edit {
  color: #fff;
  background: #3B82F6;
  border: 1px solid #3B82F6;
}
.btn-edit:hover {
  background: #2563EB;
  border-color: #2563EB;
}
.btn-offshelf {
  color: #EA580C;
  background: #FFF7ED;
  border: 1px solid #FED7AA;
}
.btn-offshelf:hover {
  background: #FED7AA;
  color: #C2410C;
}
.btn-delete {
  color: #EF4444;
  background: #FEF2F2;
  border: 1px solid #FECACA;
}
.btn-delete:hover {
  background: #FECACA;
  color: #DC2626;
}

/* 行 hover */
:deep(.table-row-hover:hover > td) {
  background: #F8FAFC !important;
}

/* 空状态 */
.empty-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 48px 0;
}
.empty-text {
  margin: 0;
  color: #94A3B8;
  font-size: 14px;
}
</style>
