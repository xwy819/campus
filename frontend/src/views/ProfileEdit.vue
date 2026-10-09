<template>
  <div class="profile-edit-page">
    <div class="page-header">
      <span class="back" @click="$router.back()">
        <svg viewBox="0 0 24 24" width="18" height="18"><path d="M15 6l-6 6 6 6" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>
      </span>
      <h2>编辑个人资料</h2>
    </div>

    <div class="section-card">
      <!-- 头像 -->
      <div class="avatar-row">
        <label class="form-label">头像</label>
        <div class="avatar-wrap" @click="avatarInput.click()">
          <img v-if="form.avatar" :src="form.avatar" class="avatar-img" />
          <span v-else class="avatar-text">{{ (form.nickname || 'U').charAt(0) }}</span>
          <div class="avatar-mask">
            <svg viewBox="0 0 24 24" width="18" height="18"><path d="M4 8h3l2-2h6l2 2h3a1 1 0 0 1 1 1v9a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V9a1 1 0 0 1 1-1Z" fill="none" stroke="#fff" stroke-width="1.7"/><circle cx="12" cy="13" r="3.5" fill="none" stroke="#fff" stroke-width="1.7"/></svg>
            <span>更换</span>
          </div>
        </div>
        <input ref="avatarInput" type="file" accept="image/*" hidden @change="onAvatarChange" />
      </div>

      <el-divider />

      <div class="form-row">
        <label class="form-label">昵称</label>
        <el-input v-model="form.nickname" maxlength="12" placeholder="2-12个字符" />
      </div>

      <div class="form-row">
        <label class="form-label">简介</label>
        <el-input v-model="form.bio" type="textarea" :rows="3" maxlength="200" show-word-limit placeholder="介绍一下自己吧" />
      </div>

      <div class="form-row">
        <label class="form-label">性别</label>
        <el-radio-group v-model="form.gender">
          <el-radio :value="1">男</el-radio>
          <el-radio :value="2">女</el-radio>
          <el-radio :value="0">保密</el-radio>
        </el-radio-group>
      </div>

      <div class="form-row">
        <label class="form-label">生日</label>
        <el-date-picker v-model="form.birthday" type="date" placeholder="选择出生日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" :clearable="true" style="width: 240px" />
      </div>

      <div class="form-row">
        <label class="form-label">爱好标签</label>
        <div class="form-control">
          <el-input v-model="hobbyInput" placeholder="输入爱好后回车添加" @keyup.enter.prevent="addHobby">
            <template #append><el-button @click="addHobby">添加</el-button></template>
          </el-input>
          <div class="tag-list" v-if="hobbyList.length > 0">
            <el-tag v-for="(h, i) in hobbyList" :key="i" closable type="warning" effect="light" @close="removeHobby(i)">{{ h }}</el-tag>
          </div>
        </div>
      </div>
    </div>

    <div class="footer-bar">
      <el-button type="primary" :loading="saving" @click="save">保存</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const saving = ref(false)
const avatarInput = ref(null)
const form = reactive({ nickname: '', avatar: '', bio: '', gender: 0, birthday: '', hobbies: '' })
const hobbyInput = ref('')
const hobbyList = computed(() => form.hobbies ? form.hobbies.split(',').filter(s => s.trim()) : [])

const addHobby = () => {
  const v = hobbyInput.value.trim()
  if (!v) return
  if (v.length > 12) { ElMessage.warning('单个爱好不超过12个字'); return }
  const list = hobbyList.value
  if (list.includes(v)) { ElMessage.warning('该爱好已存在'); return }
  form.hobbies = [...list, v].join(',')
  hobbyInput.value = ''
}
const removeHobby = (i) => {
  const list = hobbyList.value
  list.splice(i, 1)
  form.hobbies = list.join(',')
}

const onAvatarChange = (e) => {
  const file = e.target.files[0]
  if (!file) return
  const reader = new FileReader()
  reader.onload = () => { form.avatar = reader.result }
  reader.readAsDataURL(file)
  e.target.value = ''
}

const save = async () => {
  const nick = form.nickname.trim()
  if (!nick) { ElMessage.warning('昵称不能为空'); return }
  if (nick.length < 2 || nick.length > 12) { ElMessage.warning('昵称长度为 2-12 个字符'); return }
  saving.value = true
  try {
    await request.put('/user/profile', { ...form })
    localStorage.setItem('name', nick)
    ElMessage.success('保存成功')
  } catch (err) { /* 拦截器 */ }
  finally { saving.value = false }
}

onMounted(async () => {
  const res = await request.get('/user/profile')
  Object.assign(form, {
    nickname: res.data.nickname || '',
    avatar: res.data.avatar || '',
    bio: res.data.bio || '',
    gender: res.data.gender ?? 0,
    birthday: res.data.birthday || '',
    hobbies: res.data.hobbies || ''
  })
})
</script>

<style scoped>
.profile-edit-page {
  max-width: 680px;
  margin: 0 auto;
  padding-bottom: 80px;
}
.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}
.back { cursor: pointer; color: #6C757D; display: flex; }
.page-header h2 { margin: 0; font-size: 20px; font-weight: 700; color: #1A2A3A; }

.section-card {
  background: #fff;
  border-radius: 14px;
  border: 1px solid #ECEEF1;
  box-shadow: 0 2px 12px rgba(15, 27, 45, 0.06);
  padding: 24px 28px;
}

.avatar-row {
  display: flex;
  align-items: center;
  gap: 20px;
}
.form-label {
  width: 72px;
  flex-shrink: 0;
  font-size: 14px;
  color: #6C757D;
  font-weight: 500;
}
.avatar-wrap {
  position: relative;
  width: 80px; height: 80px;
  border-radius: 50%;
  cursor: pointer;
  overflow: hidden;
  background: linear-gradient(135deg, #60A5FA, #3B82F6);
}
.avatar-img { width: 100%; height: 100%; object-fit: cover; }
.avatar-text { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 32px; font-weight: 700; }
.avatar-mask { position: absolute; inset: 0; background: rgba(0,0,0,0.5); display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 2px; color: #fff; font-size: 11px; opacity: 0; transition: opacity .2s; }
.avatar-wrap:hover .avatar-mask { opacity: 1; }

.form-row {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 12px 0;
}
.form-row > :not(.form-label) { flex: 1; min-width: 0; }
.form-control { flex: 1; }
.tag-list { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 10px; }

.footer-bar {
  position: sticky;
  bottom: 0;
  background: #F7F8FA;
  padding: 16px 0;
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
