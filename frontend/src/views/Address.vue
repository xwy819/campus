<template>
  <div class="address-page">
    <div class="page-header">
      <span class="back" @click="$router.back()">
        <svg viewBox="0 0 24 24" width="18" height="18"><path d="M15 6l-6 6 6 6" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>
      </span>
      <h2>地址管理</h2>
      <el-button type="primary" size="small" @click="openAdd">新增地址</el-button>
    </div>

    <div class="section-card" v-if="list.length > 0">
      <div v-for="(item, idx) in list" :key="item.id" class="address-item">
        <div class="addr-main">
          <div class="addr-top">
            <el-tag v-if="item.label" size="small" effect="light" type="warning">{{ item.label }}</el-tag>
            <el-tag v-if="item.isDefault === 1" size="small" effect="light" type="success">默认</el-tag>
          </div>
          <div class="addr-text">{{ item.building }} · {{ item.dorm }}</div>
        </div>
        <div class="addr-actions">
          <el-button v-if="item.isDefault !== 1" link type="primary" size="small" @click="setDefault(item.id)">设为默认</el-button>
          <el-button link type="primary" size="small" @click="openEdit(item)">编辑</el-button>
          <el-button link type="danger" size="small" @click="remove(item.id)">删除</el-button>
        </div>
        <div v-if="idx < list.length - 1" class="divider" />
      </div>
    </div>

    <div v-else class="empty-state">
      <svg viewBox="0 0 24 24" width="48" height="48"><path d="M12 21s7-6.5 7-12a7 7 0 1 0-14 0c0 5.5 7 12 7 12Z" fill="none" stroke="#D0D5DC" stroke-width="1.5"/><circle cx="12" cy="9" r="3" fill="none" stroke="#D0D5DC" stroke-width="1.5"/></svg>
      <p>还没有收货地址</p>
      <el-button type="primary" @click="openAdd">新增地址</el-button>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editing ? '编辑地址' : '新增地址'" width="460px" align-center>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="70px">
        <el-form-item label="标签" prop="label">
          <el-input v-model="form.label" placeholder="如：宿舍、实验室（选填）" maxlength="20" />
        </el-form-item>
        <el-form-item label="楼栋" prop="building">
          <el-input v-model="form.building" placeholder="如：3号宿舍楼" maxlength="100" />
        </el-form-item>
        <el-form-item label="宿舍号" prop="dorm">
          <el-input v-model="form.dorm" placeholder="如：305" maxlength="50" />
        </el-form-item>
        <el-form-item label="默认地址">
          <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const list = ref([])
const dialogVisible = ref(false)
const editing = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const form = reactive({ id: null, label: '', building: '', dorm: '', isDefault: 0 })
const rules = {
  building: [{ required: true, message: '请输入楼栋', trigger: 'blur' }],
  dorm: [{ required: true, message: '请输入宿舍号', trigger: 'blur' }]
}

const load = async () => {
  const res = await request.get('/address/list')
  list.value = res.data || []
}

const openAdd = () => {
  editing.value = false
  Object.assign(form, { id: null, label: '', building: '', dorm: '', isDefault: 0 })
  dialogVisible.value = true
}

const openEdit = (item) => {
  editing.value = true
  Object.assign(form, { id: item.id, label: item.label || '', building: item.building, dorm: item.dorm, isDefault: item.isDefault || 0 })
  dialogVisible.value = true
}

const submit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitting.value = true
  try {
    if (editing.value) {
      await request.put('/address/update', form)
    } else {
      await request.post('/address/add', form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    load()
  } catch (err) { /* 拦截器 */ }
  finally { submitting.value = false }
}

const setDefault = async (id) => {
  await request.post('/address/default', { id })
  ElMessage.success('已设为默认')
  load()
}

const remove = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该地址吗？', '提示', { type: 'warning' })
    await request.delete(`/address/${id}`)
    ElMessage.success('已删除')
    load()
  } catch { /* 取消 */ }
}

onMounted(load)
</script>

<style scoped>
.address-page { max-width: 720px; margin: 0 auto; }
.page-header { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.back { cursor: pointer; color: #6C757D; display: flex; }
.page-header h2 { margin: 0; font-size: 20px; font-weight: 700; color: #1A2A3A; flex: 1; }

.section-card {
  background: #fff;
  border-radius: 14px;
  border: 1px solid #ECEEF1;
  box-shadow: 0 2px 12px rgba(15, 27, 45, 0.06);
  padding: 8px 20px;
}

.address-item { padding: 16px 0; }
.addr-main { display: flex; flex-direction: column; gap: 8px; margin-bottom: 10px; }
.addr-top { display: flex; gap: 8px; align-items: center; }
.addr-text { font-size: 15px; color: #1A2A3A; font-weight: 500; }
.addr-actions { display: flex; gap: 12px; }
.divider { height: 1px; background: #F0F1F4; margin-top: 16px; }

.empty-state {
  background: #fff;
  border-radius: 14px;
  border: 1px solid #ECEEF1;
  box-shadow: 0 2px 12px rgba(15, 27, 45, 0.06);
  padding: 60px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: #9AA3AD;
}
.empty-state p { margin: 0; font-size: 14px; }
</style>
