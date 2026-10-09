<template>
  <div class="password-page">
    <div class="page-header">
      <span class="back" @click="$router.back()">
        <svg viewBox="0 0 24 24" width="18" height="18"><path d="M15 6l-6 6 6 6" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>
      </span>
      <h2>修改密码</h2>
    </div>

    <div class="section-card">
      <el-form ref="formRef" :model="pwd" :rules="rules" label-width="100px" class="pwd-form">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwd.oldPassword" :type="showOld ? 'text' : 'password'" placeholder="请输入原密码" style="max-width: 380px">
            <template #suffix>
              <span class="eye" @click="showOld = !showOld">
                <svg v-if="!showOld" viewBox="0 0 24 24" width="18" height="18"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12Z" fill="none" stroke="currentColor" stroke-width="1.8"/><circle cx="12" cy="12" r="3" fill="none" stroke="currentColor" stroke-width="1.8"/></svg>
                <svg v-else viewBox="0 0 24 24" width="18" height="18"><path d="M3 3l18 18M9.5 9.6A3 3 0 0 0 12 15c.7 0 1.3-.2 1.9-.6M9.3 5.6A10.6 10.6 0 0 1 12 5c6.5 0 10 7 10 7a17 17 0 0 1-2.6 3.6M6.2 6.3A17 17 0 0 0 2 12s3.5 7 10 7c1 0 1.9-.1 2.7-.3" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>
              </span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwd.newPassword" :type="showNew ? 'text' : 'password'" placeholder="6-16位字符" maxlength="16" style="max-width: 380px">
            <template #suffix>
              <span class="eye" @click="showNew = !showNew">
                <svg v-if="!showNew" viewBox="0 0 24 24" width="18" height="18"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12Z" fill="none" stroke="currentColor" stroke-width="1.8"/><circle cx="12" cy="12" r="3" fill="none" stroke="currentColor" stroke-width="1.8"/></svg>
                <svg v-else viewBox="0 0 24 24" width="18" height="18"><path d="M3 3l18 18M9.5 9.6A3 3 0 0 0 12 15c.7 0 1.3-.2 1.9-.6M9.3 5.6A10.6 10.6 0 0 1 12 5c6.5 0 10 7 10 7a17 17 0 0 1-2.6 3.6M6.2 6.3A17 17 0 0 0 2 12s3.5 7 10 7c1 0 1.9-.1 2.7-.3" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>
              </span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="pwd.confirmPassword" :type="showConfirm ? 'text' : 'password'" placeholder="请再次输入新密码" maxlength="16" style="max-width: 380px">
            <template #suffix>
              <span class="eye" @click="showConfirm = !showConfirm">
                <svg v-if="!showConfirm" viewBox="0 0 24 24" width="18" height="18"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12Z" fill="none" stroke="currentColor" stroke-width="1.8"/><circle cx="12" cy="12" r="3" fill="none" stroke="currentColor" stroke-width="1.8"/></svg>
                <svg v-else viewBox="0 0 24 24" width="18" height="18"><path d="M3 3l18 18M9.5 9.6A3 3 0 0 0 12 15c.7 0 1.3-.2 1.9-.6M9.3 5.6A10.6 10.6 0 0 1 12 5c6.5 0 10 7 10 7a17 17 0 0 1-2.6 3.6M6.2 6.3A17 17 0 0 0 2 12s3.5 7 10 7c1 0 1.9-.1 2.7-.3" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>
              </span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" :disabled="loading" @click="submit">修改密码</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const formRef = ref(null)
const loading = ref(false)
const showOld = ref(false)
const showNew = ref(false)
const showConfirm = ref(false)
const pwd = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const rules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { validator: (r, v, cb) => { if (v && (v.length < 6 || v.length > 16)) return cb(new Error('密码长度为 6-16 位')); cb() }, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: (r, v, cb) => { if (v !== pwd.newPassword) return cb(new Error('两次输入的密码不一致')); cb() }, trigger: 'blur' }
  ]
}

const submit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  loading.value = true
  try {
    await request.post('/user/password', { oldPassword: pwd.oldPassword, newPassword: pwd.newPassword })
    ElMessage.success('密码修改成功')
    pwd.oldPassword = ''
    pwd.newPassword = ''
    pwd.confirmPassword = ''
    formRef.value.clearValidate()
  } catch (err) { /* 拦截器 */ }
  finally { loading.value = false }
}
</script>

<style scoped>
.password-page { max-width: 600px; margin: 0 auto; }
.page-header { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.back { cursor: pointer; color: #6C757D; display: flex; }
.page-header h2 { margin: 0; font-size: 20px; font-weight: 700; color: #1A2A3A; }

.section-card {
  background: #fff;
  border-radius: 14px;
  border: 1px solid #ECEEF1;
  box-shadow: 0 2px 12px rgba(15, 27, 45, 0.06);
  padding: 28px;
}
.eye { cursor: pointer; color: rgba(0,0,0,0.45); display: inline-flex; transition: color .2s; }
.eye:hover { color: #3B82F6; }
</style>
