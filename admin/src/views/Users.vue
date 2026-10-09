<template>
  <el-card>
    <el-form inline style="margin-bottom:16px">
      <el-form-item>
        <el-select v-model="query.role" placeholder="全部角色" clearable style="width:120px">
          <el-option label="学生" :value="0" />
          <el-option label="教师" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-input v-model="query.keyword" placeholder="昵称/手机号" clearable style="width:180px" @keyup.enter="load" />
      </el-form-item>
      <el-form-item><el-button type="primary" @click="load">查询</el-button></el-form-item>
    </el-form>

    <el-table :data="users" v-loading="loading">
      <el-table-column prop="nickname" label="昵称" min-width="120" />
      <el-table-column prop="phone" label="手机号" width="140" />
      <el-table-column label="角色" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.role === 1" type="warning" size="small">教师</el-tag>
          <el-tag v-else type="primary" size="small">学生</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.status === 1" type="success" size="small">正常</el-tag>
          <el-tag v-else type="danger" size="small">冻结</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="注册时间" width="170">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
            {{ row.status === 1 ? '冻结' : '解冻' }}
          </el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="editVisible" title="编辑用户" width="420px">
      <el-form :model="editForm" label-width="72px">
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" placeholder="请输入昵称" maxlength="12" />
        </el-form-item>
        <el-form-item label="角色">
          <el-radio-group v-model="editForm.role">
            <el-radio :value="0">学生</el-radio>
            <el-radio :value="1">教师</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const users = ref([])
const loading = ref(false)
const query = ref({ role: null, keyword: '' })

const formatTime = (t) => {
  if (!t) return ''
  return String(t).replace('T', ' ').substring(0, 19)
}

const load = async () => {
  loading.value = true
  try {
    const res = await request.get('/user/admin/list', { params: query.value })
    users.value = res.data
  } finally {
    loading.value = false
  }
}

const toggleStatus = async (row) => {
  await request.post('/user/admin/status', { id: row.id, status: row.status === 1 ? 0 : 1 })
  ElMessage.success('操作成功')
  load()
}

const remove = async (id) => {
  await ElMessageBox.confirm('确定删除该用户？删除后不可恢复。', '提示', { type: 'warning' })
  await request.delete('/user/admin/' + id)
  ElMessage.success('删除成功')
  load()
}

// 编辑用户
const editVisible = ref(false)
const editLoading = ref(false)
const editForm = ref({ id: null, nickname: '', role: 0 })

const openEdit = (row) => {
  editForm.value = { id: row.id, nickname: row.nickname, role: row.role }
  editVisible.value = true
}

const saveEdit = async () => {
  if (!editForm.value.nickname || !editForm.value.nickname.trim()) {
    ElMessage.warning('昵称不能为空')
    return
  }
  editLoading.value = true
  try {
    await request.put('/user/admin/' + editForm.value.id, {
      nickname: editForm.value.nickname.trim(),
      role: editForm.value.role
    })
    ElMessage.success('保存成功')
    editVisible.value = false
    load()
  } finally {
    editLoading.value = false
  }
}

onMounted(load)
</script>
