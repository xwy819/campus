<template>
  <el-card>
    <el-button type="primary" style="margin-bottom:16px" @click="openAdd">新增分类</el-button>
    <el-table :data="categories">
      <el-table-column prop="name" label="分类名称" />
      <el-table-column prop="sort" label="排序" width="100" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialog" :title="editing ? '编辑分类' : '新增分类'" width="420px">
      <el-form label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
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

const categories = ref([])
const dialog = ref(false)
const editing = ref(null)
const form = ref({ name: '', sort: 0 })

const load = async () => {
  const res = await request.get('/category/list')
  categories.value = res.data
}

const openAdd = () => {
  editing.value = null
  form.value = { name: '', sort: 0 }
  dialog.value = true
}

const openEdit = (row) => {
  editing.value = row
  form.value = { name: row.name, sort: row.sort }
  dialog.value = true
}

const save = async () => {
  if (editing.value) {
    await request.put('/category/admin', { ...form.value, id: editing.value.id })
  } else {
    await request.post('/category/admin', form.value)
  }
  ElMessage.success('保存成功')
  dialog.value = false
  load()
}

const remove = async (id) => {
  await ElMessageBox.confirm('确定删除该分类？', '提示', { type: 'warning' })
  await request.delete('/category/admin/' + id)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>
