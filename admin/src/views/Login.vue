<template>
  <div class="login-wrap">
    <div class="login-card">
      <div class="login-header">
        <svg class="login-logo" viewBox="0 0 32 32" width="32" height="32" aria-hidden="true">
          <path d="M6 12 L16 6 L26 12 L26 22 L16 26 L6 22 Z"
                fill="none" stroke="#FF6B35" stroke-width="2" stroke-linejoin="round"/>
          <path d="M11 16 L21 16 M16 12 L16 20"
                stroke="#FF6B35" stroke-width="2" stroke-linecap="round"/>
        </svg>
        <h2>管理后台</h2>
        <p>校园市集后台管理系统</p>
      </div>
      <form @submit.prevent="login" class="login-form">
        <div class="field">
          <label>用户名</label>
          <div class="input-wrap">
            <svg class="input-icon" viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
              <path d="M12 12a5 5 0 1 0 0-10 5 5 0 0 0 0 10Zm0 2c-4.5 0-9 2.3-9 6v2h18v-2c0-3.7-4.5-6-9-6Z"
                    fill="none" stroke="currentColor" stroke-width="1.8"/>
            </svg>
            <input v-model="form.username" placeholder="请输入用户名" autocomplete="username" />
          </div>
        </div>
        <div class="field">
          <label>密码</label>
          <div class="input-wrap">
            <svg class="input-icon" viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
              <rect x="5" y="10" width="14" height="10" rx="2" fill="none" stroke="currentColor" stroke-width="1.8"/>
              <path d="M8 10V7a4 4 0 0 1 8 0v3" fill="none" stroke="currentColor" stroke-width="1.8"/>
            </svg>
            <input v-model="form.password" :type="showPwd ? 'text' : 'password'"
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
      </form>
      <p class="demo-tip">演示账号 <code>admin</code> · 密码 <code>123456</code></p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const router = useRouter()
const form = ref({ username: '', password: '' })
const showPwd = ref(false)
const loading = ref(false)

const login = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请填写用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await request.post('/auth/login/admin', form.value)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('name', res.data.name)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (e) {
    // 错误由 request 拦截器处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrap {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #0F1B2D;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC',
    'Microsoft YaHei', sans-serif;
}

.login-card {
  width: 400px;
  background: rgba(15, 27, 45, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 14px;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.20);
  padding: 48px 44px 40px;
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
}

/* 头部 */
.login-header {
  text-align: center;
  margin-bottom: 36px;
}
.login-logo {
  margin-bottom: 16px;
}
.login-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 8px;
  letter-spacing: 2px;
}
.login-header p {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.45);
  margin: 0;
  letter-spacing: 0.5px;
}

/* 表单 */
.login-form .field { margin-bottom: 18px; }
.login-form label {
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
  border-color: rgba(255, 138, 101, 0.6);
  background: rgba(255, 255, 255, 0.10);
}
.input-wrap:focus-within {
  border-color: #FF8A65;
  background: rgba(255, 255, 255, 0.10);
  box-shadow: 0 0 0 4px rgba(255, 138, 101, 0.16);
}
.input-icon {
  color: rgba(255, 255, 255, 0.5);
  margin-left: 14px;
  flex-shrink: 0;
  display: block;
  transition: color .2s;
}
.input-wrap:focus-within .input-icon { color: #FF8A65; }

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
.pwd-toggle:hover { color: #FF8A65; }

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 46px;
  border: none;
  border-radius: 8px;
  background: linear-gradient(135deg, #FF8A65 0%, #FF6B35 100%);
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

/* 底部提示 */
.demo-tip {
  margin: 24px 0 0;
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

@media (max-width: 480px) {
  .login-card { width: auto; margin: 0 16px; padding: 36px 24px 32px; }
  .login-header h2 { font-size: 20px; }
}
</style>
