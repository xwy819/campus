<template>
  <el-card>
    <el-button type="primary" style="margin-bottom:16px" @click="openAdd">发布公告</el-button>
    <el-table :data="notices">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="content" label="内容" show-overflow-tooltip />
      <el-table-column prop="createTime" label="发布时间" width="180" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      layout="prev, pager, next, total"
      @current-change="load"
      style="margin-top:16px;justify-content:center"
    />

    <el-dialog v-model="dialog" :title="editing ? '编辑公告' : '发布公告'" width="520px">
      <el-form label-width="60px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="6" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const notices = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const dialog = ref(false)
const editing = ref(null)
const form = ref({ title: '', content: '' })

const load = async () => {
  const res = await request.get('/notice/page', { params: { pageNum: pageNum.value, pageSize: pageSize.value } })
  notices.value = res.data.records
  total.value = res.data.total
}

const openAdd = () => {
  editing.value = null
  form.value = { title: '', content: '' }
  dialog.value = true
}

const openEdit = (row) => {
  editing.value = row
  form.value = { title: row.title, content: row.content }
  dialog.value = true
}

const save = async () => {
  if (editing.value) {
    await request.put('/notice/admin', { ...form.value, id: editing.value.id })
  } else {
    await request.post('/notice/admin', form.value)
  }
  ElMessage.success('保存成功')
  dialog.value = false
  load()
}

const remove = async (id) => {
  await ElMessageBox.confirm('确定删除该公告？', '提示', { type: 'warning' })
  await request.delete('/notice/admin/' + id)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>
