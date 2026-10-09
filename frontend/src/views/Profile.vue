<template>
  <div class="pf-page">
    <!-- 上方卡片：基础资料 -->
    <div class="pf-card">
      <div class="pf-card-title">基础资料</div>

      <div class="pf-layout">
        <!-- 左栏：头像 + 昵称 -->
        <div class="pf-left">
          <div class="pf-avatar-row">
            <div class="pf-avatar" @click="avatarInput.click()">
              <img v-if="profile?.avatar" :src="profile.avatar" />
              <span v-else>{{ (profile?.nickname || 'U').charAt(0) }}</span>
            </div>
            <div class="pf-nick">
              <div v-if="editingNick" class="pf-nick-edit">
                <el-input v-model="nickDraft" size="default" maxlength="12" @keyup.enter="saveNick" />
                <button type="button" class="pf-nick-cancel" @click="editingNick = false">取消</button>
              </div>
              <div v-else class="pf-nick-row">
                <span class="pf-nick-name">{{ profile?.nickname }}</span>
                <button type="button" class="pf-change-nick" @click="startEditNick">
                  <svg viewBox="0 0 24 24" width="14" height="14"><path d="M4 20h4L18.5 9.5a2.1 2.1 0 0 0-3-3L5 17v3Z" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round"/></svg>
                </button>
              </div>
              <button type="button" class="pf-change-avatar" @click.stop="avatarInput.click()">更换头像</button>
            </div>
          </div>

          <input ref="avatarInput" type="file" accept="image/*" hidden @change="onAvatarChange" />
        </div>

        <!-- 右栏：表单 -->
        <div class="pf-right">
          <div class="pf-field pf-field-bio">
            <label>简介</label>
            <el-input v-model="form.bio" type="textarea" :rows="3" maxlength="200" show-word-limit placeholder="介绍一下自己吧" />
          </div>

          <div class="pf-field">
            <label>性别</label>
            <el-radio-group v-model="form.gender" class="pf-radio">
              <el-radio :value="1">男</el-radio>
              <el-radio :value="2">女</el-radio>
              <el-radio :value="0">保密</el-radio>
            </el-radio-group>
          </div>

          <div class="pf-field">
            <label>生日</label>
            <el-date-picker v-model="form.birthday" type="date" placeholder="选择出生日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" :clearable="true" style="width:100%" />
          </div>

          <div class="pf-field pf-field-block">
            <label>爱好标签</label>
            <div class="pf-hobby-box">
              <el-input v-model="hobbyInput" placeholder="输入爱好后回车添加" @keyup.enter.prevent="addHobby">
                <template #append>
                  <el-button @click="addHobby">添加</el-button>
                </template>
              </el-input>
              <div v-if="hobbyList.length" class="pf-tags">
                <span v-for="(h, i) in hobbyList" :key="i" class="pf-tag">
                  {{ h }}
                  <button type="button" @click="removeHobby(i)">×</button>
                </span>
              </div>
              <div v-else class="pf-hint">还没有爱好标签，添加一个吧</div>
            </div>
          </div>

          <!-- 只读展示区 -->

          <div class="pf-field pf-readonly">
            <label>手机号</label>
            <span class="pf-ro-text">{{ maskPhone(profile?.phone) }}</span>
          </div>

          <div class="pf-field pf-readonly">
            <label>角色</label>
            <span class="pf-ro-text">{{ profile?.role === 1 ? '教师' : '学生' }}</span>
          </div>

          <div class="pf-field pf-readonly">
            <label>账号状态</label>
            <span :class="profile?.status === 1 ? 'pf-status-ok' : 'pf-status-frozen'">
              {{ profile?.status === 1 ? '正常' : '已冻结' }}
            </span>
          </div>

          <div class="pf-footer">
            <el-button type="primary" :loading="saving" @click="saveAll">保存资料</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 下方卡片：修改密码 -->
    <div class="pf-card">
      <div class="pf-card-title">修改密码</div>
      <div class="pf-sub-hint">为保障账号安全，请妥善保管密码</div>

      <el-form ref="pwdFormRef" :model="pwd" :rules="pwdRules" label-width="100px" label-position="left" class="pf-pwd-form">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwd.oldPassword" :type="showOldPwd ? 'text' : 'password'" placeholder="请输入原密码">
            <template #suffix>
              <span class="pf-eye" @click="showOldPwd = !showOldPwd">
                <svg v-if="!showOldPwd" viewBox="0 0 24 24" width="18" height="18"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12Z" fill="none" stroke="currentColor" stroke-width="1.8"/><circle cx="12" cy="12" r="3" fill="none" stroke="currentColor" stroke-width="1.8"/></svg>
                <svg v-else viewBox="0 0 24 24" width="18" height="18"><path d="M3 3l18 18M9.5 9.6A3 3 0 0 0 12 15c.7 0 1.3-.2 1.9-.6M9.3 5.6A10.6 10.6 0 0 1 12 5c6.5 0 10 7 10 7a17 17 0 0 1-2.6 3.6M6.2 6.3A17 17 0 0 0 2 12s3.5 7 10 7c1 0 1.9-.1 2.7-.3" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>
              </span>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwd.newPassword" :type="showNewPwd ? 'text' : 'password'" placeholder="6-16位字符" maxlength="16">
            <template #suffix>
              <span class="pf-eye" @click="showNewPwd = !showNewPwd">
                <svg v-if="!showNewPwd" viewBox="0 0 24 24" width="18" height="18"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12Z" fill="none" stroke="currentColor" stroke-width="1.8"/><circle cx="12" cy="12" r="3" fill="none" stroke="currentColor" stroke-width="1.8"/></svg>
                <svg v-else viewBox="0 0 24 24" width="18" height="18"><path d="M3 3l18 18M9.5 9.6A3 3 0 0 0 12 15c.7 0 1.3-.2 1.9-.6M9.3 5.6A10.6 10.6 0 0 1 12 5c6.5 0 10 7 10 7a17 17 0 0 1-2.6 3.6M6.2 6.3A17 17 0 0 0 2 12s3.5 7 10 7c1 0 1.9-.1 2.7-.3" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>
              </span>
            </template>
          </el-input>
          <div v-if="pwd.newPassword" class="pf-strength-row">
            <span class="pf-strength-label">密码强度：</span>
            <span class="pf-strength-bars">
              <span :class="['pf-bar', strength >= 1 ? 'on' : '']"></span>
              <span :class="['pf-bar', strength >= 2 ? 'on' : '']"></span>
              <span :class="['pf-bar', strength >= 3 ? 'on' : '']"></span>
            </span>
            <span :class="['pf-strength-text', strengthClass]">{{ strengthText }}</span>
          </div>
        </el-form-item>

        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="pwd.confirmPassword" :type="showConfirmPwd ? 'text' : 'password'" placeholder="请再次输入新密码" maxlength="16">
            <template #suffix>
              <span class="pf-eye" @click="showConfirmPwd = !showConfirmPwd">
                <svg v-if="!showConfirmPwd" viewBox="0 0 24 24" width="18" height="18"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12Z" fill="none" stroke="currentColor" stroke-width="1.8"/><circle cx="12" cy="12" r="3" fill="none" stroke="currentColor" stroke-width="1.8"/></svg>
                <svg v-else viewBox="0 0 24 24" width="18" height="18"><path d="M3 3l18 18M9.5 9.6A3 3 0 0 0 12 15c.7 0 1.3-.2 1.9-.6M9.3 5.6A10.6 10.6 0 0 1 12 5c6.5 0 10 7 10 7a17 17 0 0 1-2.6 3.6M6.2 6.3A17 17 0 0 0 2 12s3.5 7 10 7c1 0 1.9-.1 2.7-.3" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>
              </span>
            </template>
          </el-input>
          <div v-if="pwd.confirmPassword" class="pf-match-row">
            <span v-if="pwd.confirmPassword === pwd.newPassword" class="pf-match-ok">✓ 两次密码一致</span>
            <span v-else class="pf-match-bad">✗ 两次密码不一致</span>
          </div>
        </el-form-item>

        <el-form-item class="pf-pwd-footer">
          <el-button type="primary" :loading="pwdLoading" @click="changePwd">修改密码</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 退出登录 -->
    <div class="pf-logout-row">
      <button type="button" class="pf-logout-btn" @click="logout">退出登录</button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '../utils/request'
import { setAvatar, setName } from '../store/user'

const router = useRouter()

const profile = ref(null)
const editingNick = ref(false)
const nickDraft = ref('')
const avatarInput = ref(null)
const saving = ref(false)

const form = reactive({
  nickname: '',
  bio: '',
  gender: 0,
  birthday: '',
  hobbies: ''
})

const hobbyInput = ref('')
const hobbyList = computed(() => {
  if (!form.hobbies) return []
  return form.hobbies.split(',').filter(s => s.trim())
})

const addHobby = () => {
  const v = hobbyInput.value.trim()
  if (!v) return
  if (v.length > 12) { ElMessage.warning('单个爱好不超过12个字'); return }
  if (hobbyList.value.includes(v)) { ElMessage.warning('该爱好已存在'); return }
  form.hobbies = [...hobbyList.value, v].join(',')
  hobbyInput.value = ''
}

const removeHobby = (i) => {
  const list = hobbyList.value
  list.splice(i, 1)
  form.hobbies = list.join(',')
}

const maskPhone = (phone) => {
  if (!phone) return ''
  if (phone.length !== 11) return phone
  return phone.slice(0, 3) + '****' + phone.slice(7)
}

const loadProfile = async () => {
  const res = await request.get('/user/profile')
  profile.value = res.data
  form.nickname = res.data.nickname || ''
  form.bio = res.data.bio || ''
  form.gender = res.data.gender ?? 0
  form.birthday = res.data.birthday || ''
  form.hobbies = res.data.hobbies || ''
}

const onAvatarChange = (e) => {
  const file = e.target.files[0]
  if (!file) return
  const reader = new FileReader()
  reader.onload = async () => {
    const avatar = reader.result
    try {
      await request.put('/user/profile', { avatar })
      profile.value.avatar = avatar
      setAvatar(avatar)
      ElMessage.success('头像更新成功')
    } catch { /* 拦截器展示 */ }
  }
  reader.readAsDataURL(file)
  e.target.value = ''
}

const startEditNick = () => {
  nickDraft.value = profile.value?.nickname || ''
  editingNick.value = true
}

const saveNick = () => {
  const nick = nickDraft.value.trim()
  if (!nick) { ElMessage.warning('昵称不能为空'); return }
  if (nick.length < 2 || nick.length > 12) { ElMessage.warning('昵称长度为 2-12 个字符'); return }
  form.nickname = nick
  profile.value.nickname = nick
  setName(nick)
  editingNick.value = false
}

const saveProfile = async (fields) => {
  const payload = {}
  fields.forEach(f => { payload[f] = form[f] })
  try {
    await request.put('/user/profile', payload)
  } catch { /* 拦截器展示 */ }
}

const saveAll = async () => {
  saving.value = true
  try {
    await request.put('/user/profile', {
      nickname: form.nickname,
      bio: form.bio,
      gender: form.gender,
      birthday: form.birthday,
      hobbies: form.hobbies
    })
    setName(form.nickname)
    ElMessage.success('资料保存成功')
  } catch { /* 拦截器展示 */ }
  finally { saving.value = false }
}

// 密码模块
const pwdFormRef = ref(null)
const pwdLoading = ref(false)
const showOldPwd = ref(false)
const showNewPwd = ref(false)
const showConfirmPwd = ref(false)
const pwd = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const pwdRules = {
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

// 密码强度
const strength = computed(() => {
  const p = pwd.newPassword
  if (!p) return 0
  let s = 0
  if (p.length >= 6) s++
  if (/[A-Za-z]/.test(p) && /[0-9]/.test(p)) s++
  if (/[^A-Za-z0-9]/.test(p) || p.length >= 12) s++
  return s
})
const strengthText = computed(() => ['', '弱', '中', '强'][strength.value])
const strengthClass = computed(() => ['', 'pf-s-weak', 'pf-s-mid', 'pf-s-strong'][strength.value])

const changePwd = async () => {
  if (!pwdFormRef.value) return
  try { await pwdFormRef.value.validate() } catch { return }
  pwdLoading.value = true
  try {
    await request.post('/user/password', { oldPassword: pwd.oldPassword, newPassword: pwd.newPassword })
    ElMessage.success('密码修改成功')
    pwd.oldPassword = ''
    pwd.newPassword = ''
    pwd.confirmPassword = ''
    pwdFormRef.value.clearValidate()
  } catch { /* 拦截器 */ }
  finally { pwdLoading.value = false }
}

const logout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' })
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    localStorage.removeItem('name')
    localStorage.removeItem('userId')
    setAvatar('')
    setName('')
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch { /* 取消 */ }
}

onMounted(loadProfile)
</script>

<style scoped>
.pf-page {
  min-height: calc(100vh - 64px);
  background: #f0f5f9;
  padding: 24px 16px 32px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.pf-card {
  width: 100%;
  max-width: 820px;
  background: #ffffff;
  border-radius: 12px;
  padding: 28px 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
  box-sizing: border-box;
}
.pf-card-title {
  font-size: 17px;
  font-weight: 600;
  color: #333;
  padding-left: 10px;
  border-left: 3px solid #1677ff;
  margin-bottom: 20px;
}
.pf-sub-hint {
  font-size: 12px;
  color: #8c8c8c;
  margin-bottom: 20px;
}

/* 基础资料左右分栏 */
.pf-layout {
  display: flex;
  gap: 40px;
  align-items: flex-start;
}
.pf-left {
  flex-shrink: 0;
  padding-top: 0;
}
.pf-avatar-row {
  display: flex;
  align-items: center;
  gap: 20px;
}
.pf-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  background: linear-gradient(135deg, #60a5fa, #1677ff);
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(22, 119, 255, 0.25);
  transition: transform .2s;
}
.pf-avatar:hover { transform: scale(1.04); }
.pf-avatar img { width: 100%; height: 100%; object-fit: cover; display: block; }
.pf-avatar span {
  width: 100%; height: 100%;
  display: flex; align-items: center; justify-content: center;
  color: #fff; font-size: 32px; font-weight: 700;
}
.pf-nick {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 0;
}
.pf-nick-row {
  display: flex;
  align-items: center;
  gap: 10px;
}
.pf-nick-name {
  font-size: 17px;
  font-weight: 600;
  color: #333;
}
.pf-change-avatar {
  background: none;
  border: none;
  color: #1677ff;
  font-size: 13px;
  cursor: pointer;
  padding: 0;
  transition: color .2s;
}
.pf-change-avatar:hover { color: #0958d9; }
.pf-change-nick {
  background: none;
  border: none;
  color: #bfbfbf;
  cursor: pointer;
  padding: 0;
  display: inline-flex;
  transition: color .2s;
}
.pf-change-nick:hover { color: #1677ff; }
.pf-nick-edit {
  display: flex;
  align-items: center;
  gap: 8px;
}
.pf-nick-cancel {
  background: none;
  border: none;
  color: #8c8c8c;
  font-size: 13px;
  cursor: pointer;
  padding: 0;
  transition: color .2s;
}
.pf-nick-cancel:hover { color: #f53f3f; }

/* 右栏表单 */
.pf-right {
  flex: 1;
  min-width: 0;
}
.pf-field {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}
.pf-field-block {
  align-items: flex-start;
}
.pf-field > label {
  width: 100px;
  flex-shrink: 0;
  font-size: 14px;
  color: #666;
  text-align: left;
  line-height: 40px;
}
.pf-field-block > label {
  padding-top: 10px;
  line-height: 20px;
}
.pf-field-bio {
  align-items: center;
}
.pf-field-bio > label {
  line-height: 20px;
}
.pf-field > label::before {
  content: attr(data-required);
}
.pf-field :deep(.el-input),
.pf-field :deep(.el-textarea),
.pf-field :deep(.el-date-editor) {
  font-size: 14px;
  height: 40px;
  transition: all .2s ease;
}
.pf-field :deep(.el-textarea) { height: auto; }
.pf-field :deep(.el-input__wrapper),
.pf-field :deep(.el-textarea__inner),
.pf-field :deep(.el-select__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  background: #fff;
  transition: all .2s ease;
}
.pf-field :deep(.el-input__wrapper:hover),
.pf-field :deep(.el-textarea__inner:hover),
.pf-field :deep(.el-select__wrapper:hover) {
  box-shadow: 0 0 0 1px #7cc4ff inset;
}
.pf-field :deep(.el-input__wrapper.is-focus),
.pf-field :deep(.el-textarea__inner:focus),
.pf-field :deep(.el-select__wrapper.is-focused) {
  box-shadow: 0 0 0 1px #1677ff inset, 0 0 0 3px rgba(22, 119, 255, 0.12) !important;
  border: none !important;
  outline: none !important;
}
.pf-field :deep(.el-textarea__inner) {
  padding: 10px 12px;
  border: none;
  outline: none;
}

/* 爱好标签 */
.pf-hobby-box { flex: 1; min-width: 0; }
.pf-hobby-box :deep(.el-input-group) { border-radius: 8px; }
.pf-hobby-box :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  background: #fff;
  transition: all .2s ease;
}
.pf-hobby-box :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #7cc4ff inset;
}
.pf-hobby-box :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #1677ff inset, 0 0 0 3px rgba(22, 119, 255, 0.12) !important;
  border: none !important;
  outline: none !important;
}
.pf-tags {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.pf-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: #eff6ff;
  color: #1677ff;
  padding: 4px 12px;
  border-radius: 14px;
  font-size: 12px;
}
.pf-tag button {
  background: none;
  border: none;
  color: #1677ff;
  font-size: 14px;
  cursor: pointer;
  padding: 0;
  line-height: 1;
}
.pf-tag button:hover { color: #f53f3f; }
.pf-hint { font-size: 13px; color: #8c8c8c; margin-top: 10px; }

/* 字数统计颜色加深 */
.pf-field :deep(.el-input__count),
.pf-field :deep(.el-input__count-inner) {
  color: #666 !important;
}

/* 只读 */
.pf-section-title {
  font-size: 13px;
  color: #bfbfbf;
  font-weight: 500;
  margin: 28px 0 16px;
  padding-left: 100px;
}
.pf-readonly { margin-bottom: 14px; }
.pf-ro-text {
  font-size: 14px;
  color: #444;
  line-height: 40px;
}
.pf-status-ok {
  color: #36c669;
  font-weight: 500;
  line-height: 40px;
}
.pf-status-frozen {
  color: #f53f3f;
  font-weight: 500;
  line-height: 40px;
}

/* 保存按钮 */
.pf-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
}
.pf-footer :deep(.el-button--primary) {
  background: #1677ff !important;
  background-image: none !important;
  border-color: #1677ff !important;
  color: #ffffff !important;
  border-radius: 8px !important;
  padding: 10px 24px;
  font-size: 14px;
}
.pf-footer :deep(.el-button--primary:hover),
.pf-footer :deep(.el-button--primary.is-hover) {
  background: #0958d9 !important;
  border-color: #0958d9 !important;
  color: #ffffff !important;
}

/* 密码表单 */
.pf-pwd-form :deep(.el-form-item) {
  margin-bottom: 20px;
}
.pf-pwd-form :deep(.el-form-item__label) {
  font-size: 14px;
  color: #666;
  text-align: left;
  padding-right: 0;
}
.pf-pwd-form :deep(.el-input) {
  max-width: 420px;
  height: 40px;
  transition: all .2s ease;
}
.pf-pwd-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  background: #fff;
  transition: all .2s ease;
}
.pf-pwd-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #7cc4ff inset;
}
.pf-pwd-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #1677ff inset, 0 0 0 3px rgba(22, 119, 255, 0.12) !important;
  border: none !important;
  outline: none !important;
}
.pf-eye {
  color: #8c8c8c;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  transition: color .2s;
}
.pf-eye:hover { color: #1677ff; }

/* 密码强度 */
.pf-strength-row {
  margin-top: 6px;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  padding-left: 0;
}
.pf-strength-label { color: #8c8c8c; }
.pf-strength-bars {
  display: inline-flex;
  gap: 3px;
}
.pf-bar {
  display: inline-block;
  width: 36px;
  height: 4px;
  border-radius: 2px;
  background: #e5e7eb;
  transition: background .2s;
}
.pf-bar.on { background: #36c669; }
.pf-s-weak .pf-bar.on { background: #f53f3f; }
.pf-s-mid .pf-bar.on { background: #faad14; }
.pf-strength-text { font-weight: 500; }
.pf-s-weak .pf-strength-text { color: #f53f3f; }
.pf-s-mid .pf-strength-text { color: #faad14; }
.pf-s-strong .pf-strength-text { color: #36c669; }

/* 确认密码校验 */
.pf-match-row {
  margin-top: 6px;
  font-size: 12px;
}
.pf-match-ok { color: #36c669; }
.pf-match-bad { color: #f53f3f; }

.pf-pwd-footer :deep(.el-form-item__content) {
  max-width: 420px;
}
.pf-pwd-footer :deep(.el-button--primary) {
  background: #1677ff !important;
  background-image: none !important;
  border-color: #1677ff !important;
  color: #ffffff !important;
  border-radius: 8px !important;
  padding: 10px 22px;
}
.pf-pwd-footer :deep(.el-button--primary:hover),
.pf-pwd-footer :deep(.el-button--primary.is-hover) {
  background: #0958d9 !important;
  border-color: #0958d9 !important;
  color: #ffffff !important;
}

/* 退出登录 */
.pf-logout-row {
  width: 100%;
  max-width: 820px;
  display: flex;
  justify-content: flex-end;
  box-sizing: border-box;
}
.pf-logout-btn {
  background: #ffffff;
  border: 1px solid #f53f3f;
  color: #f53f3f;
  border-radius: 8px;
  padding: 10px 22px;
  font-size: 14px;
  cursor: pointer;
  transition: all .2s;
}
.pf-logout-btn:hover {
  background: #fff1f0;
  border-color: #f53f3f;
}

/* Radio 简化 */
.pf-radio :deep(.el-radio__input.is-checked .el-radio__inner) {
  border-color: #1677ff;
  background: #1677ff;
}
.pf-radio :deep(.el-radio__input.is-checked + .el-radio__label) {
  color: #1677ff;
}

/* 响应式 */
@media (max-width: 768px) {
  .pf-layout { flex-direction: column; gap: 24px; }
  .pf-avatar-row { justify-content: center; }
  .pf-card { padding: 20px; }
  .pf-field > label { text-align: left; width: 80px; }
}
</style>
