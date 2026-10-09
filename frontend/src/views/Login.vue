<template>
  <div class="auth-page">
    <!-- 左侧品牌区 -->
    <aside class="brand-panel">
      <div class="brand-deco brand-deco-1"></div>
      <div class="brand-deco brand-deco-2"></div>
      <div class="brand-deco brand-deco-3"></div>

      <header class="brand-header">
        <div class="logo-mark">
          <svg viewBox="0 0 32 32" width="34" height="34" aria-hidden="true">
            <path d="M6 12 L16 6 L26 12 L26 22 L16 26 L6 22 Z"
                  fill="none" stroke="#60A5FA" stroke-width="2" stroke-linejoin="round"/>
            <path d="M11 16 L21 16 M16 12 L16 20"
                  stroke="#60A5FA" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        <span class="logo-text">校园市集</span>
      </header>

      <div class="brand-body">
        <h1>让闲置物品<br/>在校园里流转</h1>
        <p>书籍 · 数码 · 日用 · 运动<br/>校园内的可信二手交易市场</p>
      </div>

      <ul class="brand-stats">
        <li>
          <strong>1,200<span>+</span></strong>
          <span>在售商品</span>
        </li>
        <li>
          <strong>3,800<span>+</span></strong>
          <span>注册用户</span>
        </li>
        <li>
          <strong>96<span>%</span></strong>
          <span>好评率</span>
        </li>
      </ul>
    </aside>

    <!-- 右侧表单区 -->
    <main class="form-panel">
      <div class="form-wrap">
        <div class="form-box">
          <header class="form-header">
            <h2>欢迎回来</h2>
            <p>登录账号，继续你的交易之旅</p>
          </header>

          <form @submit.prevent="userLogin" class="login-form">
            <div class="field">
              <label>手机号码</label>
              <div class="input-wrap">
                <svg class="input-icon" viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
                  <path d="M12 12a5 5 0 1 0 0-10 5 5 0 0 0 0 10Zm0 2c-4.5 0-9 2.3-9 6v2h18v-2c0-3.7-4.5-6-9-6Z"
                        fill="none" stroke="currentColor" stroke-width="1.8"/>
                </svg>
                <input v-model="userForm.phone" type="tel" placeholder="请输入手机号码" autocomplete="username" />
              </div>
            </div>

            <div class="field">
              <label>密码</label>
              <div class="input-wrap">
                <svg class="input-icon" viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
                  <rect x="5" y="10" width="14" height="10" rx="2" fill="none" stroke="currentColor" stroke-width="1.8"/>
                  <path d="M8 10V7a4 4 0 0 1 8 0v3" fill="none" stroke="currentColor" stroke-width="1.8"/>
                </svg>
                <input v-model="userForm.password" :type="showPwd ? 'text' : 'password'"
                       placeholder="请输入密码" autocomplete="current-password" />
                <button type="button" class="pwd-toggle" @click="showPwd = !showPwd" tabindex="-1" aria-label="切换密码可见">
                  <svg v-if="!showPwd" viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
                    <path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12Z" fill="none" stroke="currentColor" stroke-width="1.8"/>
                    <circle cx="12" cy="12" r="3" fill="none" stroke="currentColor" stroke-width="1.8"/>
                  </svg>
                  <svg v-else viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
                    <path d="M3 3l18 18M9.5 9.6A3 3 0 0 0 12 15c.7 0 1.3-.2 1.9-.6M9.3 5.6A10.6 10.6 0 0 1 12 5c6.5 0 10 7 10 7a17 17 0 0 1-2.6 3.6M6.2 6.3A17 17 0 0 0 2 12s3.5 7 10 7c1 0 1.9-.1 2.7-.3"
                          fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
                  </svg>
                </button>
              </div>
            </div>

            <button type="submit" class="login-btn" :disabled="loading">
              <span v-if="!loading">登 录</span>
              <span v-else class="loading-text">
                <span class="spinner"></span> 登录中…
              </span>
            </button>

            <div class="form-footer">
              <span>还没有账号？</span>
              <a @click="showRegister = true">立即注册</a>
            </div>
          </form>
        </div>

        <p class="demo-tip">
          演示账号 <code>13900000001</code> · 密码 <code>123456</code>
        </p>
      </div>
    </main>

    <!-- 注册弹窗 -->
    <el-dialog
      v-model="showRegister"
      title="用户注册"
      width="420px"
      align-center
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      @closed="onDialogClosed"
    >
      <el-form
        ref="regFormRef"
        :model="regForm"
        :rules="regRules"
        label-width="72px"
        @submit.prevent
      >
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="regForm.phone"
            placeholder="请输入手机号"
            maxlength="11"
            @blur="onPhoneBlur"
            clearable
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="regForm.password"
            type="text"
            :class="{ 'pwd-masked': !showRegPwd }"
            placeholder="6-16位字符"
            maxlength="16"
          >
            <template #suffix>
              <span class="reg-pwd-toggle" @click="showRegPwd = !showRegPwd">
                <svg v-if="!showRegPwd" viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
                  <path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12Z" fill="none" stroke="currentColor" stroke-width="1.8"/>
                  <circle cx="12" cy="12" r="3" fill="none" stroke="currentColor" stroke-width="1.8"/>
                </svg>
                <svg v-else viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
                  <path d="M3 3l18 18M9.5 9.6A3 3 0 0 0 12 15c.7 0 1.3-.2 1.9-.6M9.3 5.6A10.6 10.6 0 0 1 12 5c6.5 0 10 7 10 7a17 17 0 0 1-2.6 3.6M6.2 6.3A17 17 0 0 0 2 12s3.5 7 10 7c1 0 1.9-.1 2.7-.3"
                        fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
                </svg>
              </span>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="昵称" prop="nickname">
          <el-input
            v-model="regForm.nickname"
            placeholder="2-12个字符"
            maxlength="12"
            @blur="trimNickname"
            clearable
          />
        </el-form-item>

        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="regForm.role">
            <el-radio :value="0">学生</el-radio>
            <el-radio :value="1">教师</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button native-type="button" @click="cancelRegister">取消</el-button>
        <el-button type="primary" native-type="button" :loading="regLoading" :disabled="regLoading" @click="register">注册</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'
import { userStore } from '../store/user'

const router = useRouter()
const showRegister = ref(false)
const showPwd = ref(false)
const loading = ref(false)

const userForm = ref({ phone: '', password: '' })
const regForm = reactive({ phone: '', password: '', nickname: '', role: 0 })

// 注册弹窗相关
const regFormRef = ref(null)
const showRegPwd = ref(false)
const regLoading = ref(false)

// 表单校验规则
const phoneValidator = (rule, value, callback) => {
  if (!value) return callback(new Error('请输入手机号'))
  if (!/^\d{11}$/.test(value)) return callback(new Error('请输入11位数字手机号'))
  callback()
}
const pwdValidator = (rule, value, callback) => {
  if (!value) return callback(new Error('请输入密码'))
  if (value.length < 6 || value.length > 16) return callback(new Error('密码长度为6-16位'))
  callback()
}
const nicknameValidator = (rule, value, callback) => {
  if (!value) return callback(new Error('请输入昵称'))
  const trimmed = value.trim()
  if (!trimmed) return callback(new Error('昵称不能全为空格'))
  if (trimmed.length < 2 || trimmed.length > 12) return callback(new Error('昵称长度为2-12个字符'))
  callback()
}
const regRules = {
  phone: [{ validator: phoneValidator, trigger: 'blur' }],
  password: [{ validator: pwdValidator, trigger: 'blur' }],
  nickname: [{ validator: nicknameValidator, trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

// 手机号失焦：先本地格式校验，通过后请求后端校验是否已注册
const onPhoneBlur = async () => {
  if (!regForm.phone) return
  if (!/^\d{11}$/.test(regForm.phone)) return
  try {
    await request.get('/user/checkPhone', { params: { phone: regForm.phone } })
    ElMessage.success('该手机号可用')
  } catch (e) {
    // 后端返回"该手机号已注册"等错误信息，由 request 拦截器统一以 ElMessage 展示
  }
}

// 昵称失焦去除首尾空格
const trimNickname = () => {
  if (regForm.nickname) regForm.nickname = regForm.nickname.trim()
}

// 取消注册：清空表单，角色重置为学生
const cancelRegister = () => {
  regForm.phone = ''
  regForm.password = ''
  regForm.nickname = ''
  regForm.role = 0
  showRegPwd.value = false
  if (regFormRef.value) regFormRef.value.clearValidate()
  showRegister.value = false
}

// 弹窗关闭后清理校验状态
const onDialogClosed = () => {
  regForm.phone = ''
  regForm.password = ''
  regForm.nickname = ''
  regForm.role = 0
  showRegPwd.value = false
  if (regFormRef.value) regFormRef.value.clearValidate()
}

// 提交注册（仅由「注册」按钮点击触发，无键盘快捷入口，避免与原生 form 隐式提交冲突）
const register = async () => {
  if (!regFormRef.value) return
  try {
    await regFormRef.value.validate()
  } catch (err) {
    // 校验失败：错误信息已由 el-form 内联展示，直接返回
    return
  }
  // 提交前统一去除首尾空格
  const payload = {
    phone: regForm.phone.trim(),
    password: regForm.password,
    nickname: regForm.nickname.trim(),
    role: regForm.role
  }
  regLoading.value = true
  try {
    await request.post('/user/register', payload)
    ElMessage.success('注册成功，请登录')
    showRegister.value = false
    // 注册成功后清空表单
    regForm.phone = ''
    regForm.password = ''
    regForm.nickname = ''
    regForm.role = 0
    // 跳转登录页
    router.push('/login')
  } catch (err) {
    // 后端错误信息已由 request 拦截器展示，这里不再重复
  } finally {
    regLoading.value = false
  }
}

const userLogin = async () => {
  if (!userForm.value.phone || !userForm.value.password) {
    ElMessage.warning('请填写手机号和密码')
    return
  }
  loading.value = true
  try {
    const res = await request.post('/auth/login/user', userForm.value)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('role', res.data.role)
    localStorage.setItem('name', res.data.name)
    localStorage.setItem('userId', String(res.data.userId))
    // 初始化全局状态
    userStore.name = res.data.name
    userStore.role = res.data.role
    userStore.userId = String(res.data.userId)
    // 顺便拉一次 profile 拿头像
    try {
      const p = await request.get('/user/profile')
      userStore.avatar = p.data.avatar || ''
      if (p.data.avatar) localStorage.setItem('avatar', p.data.avatar)
    } catch {}
    ElMessage.success('登录成功')
    router.push('/home')
  } catch (e) {
    // 错误已由 request 拦截器统一处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  display: flex;
  height: 100vh;
  background: linear-gradient(135deg, #0F1B2D 0%, #1A2A3A 50%, #0F1B2D 100%);
  padding: 24px;
  gap: 24px;
  box-sizing: border-box;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC',
    'Microsoft YaHei', sans-serif;
}

/* ============ 左侧品牌区 ============ */
.brand-panel {
  flex: 4.5;
  position: relative;
  background: linear-gradient(160deg, #1A2A3A 0%, #0F1B2D 100%);
  color: #fff;
  padding: 56px 64px 104px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden;
  border-radius: 16px;
}

.brand-deco {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
  filter: blur(0.5px);
  opacity: 0;
}
.brand-deco-1 {
  top: -120px; left: -120px;
  width: 320px; height: 320px;
  background: radial-gradient(circle, rgba(96,165,250,0.45) 0%, transparent 70%);
}
.brand-deco-2 {
  bottom: -80px; right: -100px;
  width: 280px; height: 280px;
  background: radial-gradient(circle, rgba(102,182,255,0.30) 0%, transparent 70%);
}
.brand-deco-3 {
  top: 40%; right: 10%;
  width: 200px; height: 200px;
  background: radial-gradient(circle, rgba(96,165,250,0.20) 0%, transparent 70%);
}

.brand-header {
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  z-index: 1;
}
.logo-mark {
  width: 44px; height: 44px;
  display: flex; align-items: center; justify-content: center;
}
.logo-mark svg path {
  stroke: #3B82F6 !important;
}
.logo-text {
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 2px;
}

.brand-body {
  position: relative;
  z-index: 1;
}
.brand-body h1 {
  font-size: 44px;
  font-weight: 700;
  line-height: 1.4;
  margin: 0 0 20px;
  letter-spacing: 2px;
}
.brand-body p {
  font-size: 15px;
  color: rgba(255,255,255,0.45);
  line-height: 1.8;
  margin: 0;
}

.brand-stats {
  list-style: none;
  margin: 0;
  padding: 28px 0 0;
  display: flex;
  gap: 24px;
  border-top: 1px solid rgba(255,255,255,0.08);
  position: relative;
  z-index: 1;
}
.brand-stats li {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}
.brand-stats strong {
  font-size: 28px;
  color: #60A5FA;
  font-weight: 700;
  line-height: 1.1;
  display: flex;
  align-items: baseline;
  justify-content: center;
}
.brand-stats strong span {
  font-size: 18px;
  margin-left: 1px;
}
.brand-stats li > span {
  font-size: 12px;
  color: rgba(255,255,255,0.5);
  margin-top: 6px;
  letter-spacing: 0.5px;
}

/* ============ 右侧表单区 ============ */
.form-panel {
  flex: 5.5;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
  background: transparent;
  border-radius: 16px;
}
.form-wrap {
  width: 100%;
  max-width: 450px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.form-box {
  width: 100%;
  background: rgba(15, 27, 45, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 14px;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.20);
  padding: 48px 52px;
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
}
.form-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 10px;
  letter-spacing: 1px;
}
.form-header p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.55);
  margin: 0 0 36px;
}

.field { margin-bottom: 18px; }
.field label {
  display: block;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.72);
  margin-bottom: 8px;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.input-wrap {
  position: relative;
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 8px;
  transition: all .2s ease;
}
.input-wrap:hover {
  border-color: rgba(96, 165, 250, 0.6);
  background: rgba(255, 255, 255, 0.10);
}
.input-wrap:focus-within {
  border-color: #60A5FA;
  background: rgba(255, 255, 255, 0.10);
  box-shadow: 0 0 0 4px rgba(96, 165, 250, 0.16);
}
.input-icon {
  color: rgba(255, 255, 255, 0.5);
  margin-left: 14px;
  flex-shrink: 0;
  display: block;
  transition: color .2s;
}
.input-wrap:focus-within .input-icon { color: #60A5FA; }

.input-wrap input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  padding: 13px 22px 13px 14px;
  font-size: 14px;
  color: #fff;
  letter-spacing: 0.3px;
}
.input-wrap input::placeholder { color: rgba(255, 255, 255, 0.4); }

.pwd-toggle {
  border: none;
  background: transparent;
  cursor: pointer;
  padding: 0 20px;
  color: rgba(255, 255, 255, 0.5);
  display: flex;
  align-items: center;
  flex-shrink: 0;
  transition: color .2s;
}
.pwd-toggle svg { display: block; }
.pwd-toggle:hover { color: #60A5FA; }

.login-btn {
  width: 100%;
  height: 46px;
  border: none;
  border-radius: 8px;
  background: linear-gradient(135deg, #60A5FA 0%, #3B82F6 100%);
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 6px;
  text-align: center;
  cursor: pointer;
  transition: all .2s ease;
  margin-top: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.18);
}
.login-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  filter: brightness(0.92);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.22);
}
.login-btn:active:not(:disabled) { transform: translateY(0); filter: brightness(0.88); }
.login-btn:disabled { opacity: .65; cursor: not-allowed; }

.loading-text { display: inline-flex; align-items: center; gap: 8px; letter-spacing: 2px; }
.spinner {
  width: 14px; height: 14px;
  border: 2px solid rgba(255,255,255,0.45);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

.form-footer {
  text-align: center;
  margin-top: 22px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.55);
}
.form-footer a {
  color: #60A5FA;
  cursor: pointer;
  margin-left: 6px;
  font-weight: 500;
  text-decoration: none;
  transition: opacity .2s, text-decoration .2s;
}
.form-footer a:hover { opacity: 0.85; text-decoration: underline; }

.demo-tip {
  margin: 24px 0 0;
  padding: 0;
  background: transparent;
  border: none;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.42);
  text-align: center;
  letter-spacing: 0.3px;
}
.demo-tip::after {
  content: "（测试使用）";
  margin-left: 4px;
}
.demo-tip code {
  font-family: 'SF Mono', Consolas, monospace;
  background: transparent;
  padding: 0;
  border-radius: 0;
  color: rgba(255, 255, 255, 0.62);
  margin: 0 1px;
}

/* ============ 响应式 ============ */
@media (max-width: 1280px) {
  .form-panel { padding: 40px; }
  .form-box { padding: 40px 44px; }
  .brand-body h1 { font-size: 38px; }
}
@media (max-width: 960px) {
  .brand-panel { display: none; }
}
@media (max-width: 480px) {
  .form-panel { padding: 24px 16px; }
  .form-box { padding: 32px 24px; }
  .form-header h2 { font-size: 24px; }
}

/* ============ 注册弹窗（暗黑风格，复用登录卡片视觉） ============ */
:deep(.el-dialog) {
  background: rgba(15, 27, 45, 0.86);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 14px;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.38);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  color: #fff;
  overflow: hidden;
}
:deep(.el-dialog__header) {
  padding: 28px 24px 16px;
  margin-right: 0;
}
:deep(.el-dialog__title) {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 1px;
}
/* 右上角关闭图标 */
:deep(.el-dialog__headerbtn) {
  top: 20px;
  right: 20px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}
:deep(.el-dialog__close) {
  font-size: 20px !important;
  color: rgba(255, 255, 255, 0.55) !important;
  transition: color .2s;
}
:deep(.el-dialog__headerbtn:hover .el-dialog__close) {
  color: #60A5FA !important;
}

:deep(.el-dialog__body) {
  padding: 16px 24px 8px;
  color: #fff;
}
/* 表单项间距统一 */
:deep(.el-form-item) {
  margin-bottom: 22px;
}
:deep(.el-form-item:last-child) {
  margin-bottom: 0;
}
:deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.72) !important;
  font-size: 13px;
  font-weight: 500;
}
:deep(.el-form-item__content) {
  line-height: 1;
}

/* 弹窗内输入框：暗黑底色 + hover/focus 反馈 */
:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.15) !important;
  box-shadow: none !important;
  border-radius: 8px;
  padding: 0 14px;
  transition: all .2s ease;
}
:deep(.el-input__wrapper:hover) {
  border-color: rgba(96, 165, 250, 0.6) !important;
  background: rgba(255, 255, 255, 0.10) !important;
}
:deep(.el-input__wrapper.is-focus) {
  border-color: #60A5FA !important;
  background: rgba(255, 255, 255, 0.10) !important;
  box-shadow: 0 0 0 4px rgba(96, 165, 250, 0.16) !important;
}
:deep(.el-input__inner) {
  color: #fff !important;
  height: 38px;
  font-size: 14px;
}
:deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.4) !important;
}

/* 单选框：选项拉开间距，圆圈与文字保留距离 */
:deep(.el-radio) {
  margin-right: 36px;
  height: 32px;
  line-height: 32px;
}
:deep(.el-radio:last-child) {
  margin-right: 0;
}
:deep(.el-radio__input) {
  margin-right: 8px;
}
:deep(.el-radio__label) {
  color: rgba(255, 255, 255, 0.78) !important;
  padding-left: 6px;
  font-size: 14px;
}
:deep(.el-radio__inner) {
  background: transparent;
  border-color: rgba(255, 255, 255, 0.4);
  width: 16px;
  height: 16px;
}
:deep(.el-radio__input.is-checked .el-radio__inner) {
  background: #3B82F6;
  border-color: #3B82F6;
}
:deep(.el-radio__input.is-checked + .el-radio__label) {
  color: #60A5FA !important;
}

/* 底部按钮组 */
:deep(.el-dialog__footer) {
  padding: 20px 24px 30px;
  display: flex;
  justify-content: center;
  gap: 16px;
}
:deep(.el-dialog__footer .el-button) {
  height: 42px;
  padding: 0 28px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  min-width: 110px;
  transition: all .2s ease;
}
/* 注册按钮：与登录页登录按钮橙色完全一致 */
:deep(.el-dialog__footer .el-button--primary) {
  background: linear-gradient(135deg, #60A5FA 0%, #3B82F6 100%);
  border: none;
  color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.18);
}
:deep(.el-dialog__footer .el-button--primary:hover) {
  filter: brightness(0.92);
}
:deep(.el-dialog__footer .el-button--primary:active) {
  filter: brightness(0.88);
}
/* 取消按钮：适配深色弹窗 */
:deep(.el-dialog__footer .el-button:not(.el-button--primary)) {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.18);
  color: rgba(255, 255, 255, 0.82);
}
:deep(.el-dialog__footer .el-button:not(.el-button--primary):hover) {
  background: rgba(255, 255, 255, 0.14);
  border-color: rgba(96, 165, 250, 0.5);
  color: #fff;
}

/* 注册弹窗：密码遮罩（type=text + text-security 模拟密码隐藏，避免 el-input 密码类型触发崩溃） */
.pwd-masked :deep(input) {
  -webkit-text-security: disc;
}

/* 注册弹窗：密码眼睛切换图标（暗黑背景可见） */
.reg-pwd-toggle {
  cursor: pointer;
  color: rgba(255, 255, 255, 0.5);
  display: inline-flex;
  align-items: center;
  transition: color .2s;
}
.reg-pwd-toggle:hover { color: #60A5FA; }

/* 注册弹窗：表单校验错误提示（暗黑背景下可见） */
:deep(.el-form-item__error) {
  color: #60A5FA !important;
  padding-top: 4px;
  font-size: 12px;
}
:deep(.el-form-item.is-error .el-input__wrapper) {
  border-color: #60A5FA !important;
  box-shadow: 0 0 0 2px rgba(96, 165, 250, 0.18) !important;
}
</style>

/* 弹窗遮罩加深（teleport 到 body，需全局样式） */
<style>
.el-overlay {
  background-color: rgba(0, 0, 0, 0.72) !important;
}
</style>
