<template>
  <div class="settings-page">
    <!-- 个人分组 -->
    <div class="group-card">
      <div class="group-title">个人</div>
      <div class="list-item" @click="$router.push('/profile/edit')">
        <div class="item-left">
          <span class="item-icon icon-profile">
            <svg viewBox="0 0 24 24" width="18" height="18"><path d="M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8ZM4 20c0-4 4-6 8-6s8 2 8 6" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>
          </span>
          <span class="item-name">个人资料</span>
        </div>
        <svg class="arrow" viewBox="0 0 24 24" width="16" height="16"><path d="M9 6l6 6-6 6" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>
      </div>
      <div class="divider" />
      <div class="list-item" @click="$router.push('/address')">
        <div class="item-left">
          <span class="item-icon icon-address">
            <svg viewBox="0 0 24 24" width="18" height="18"><path d="M12 21s7-6.5 7-12a7 7 0 1 0-14 0c0 5.5 7 12 7 12Z" fill="none" stroke="currentColor" stroke-width="1.8"/><circle cx="12" cy="9" r="2.5" fill="none" stroke="currentColor" stroke-width="1.8"/></svg>
          </span>
          <span class="item-name">地址管理</span>
        </div>
        <svg class="arrow" viewBox="0 0 24 24" width="16" height="16"><path d="M9 6l6 6-6 6" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>
      </div>
    </div>

    <!-- 账号与安全分组 -->
    <div class="group-card">
      <div class="group-title">账号与安全</div>
      <div class="list-item" @click="$router.push('/password')">
        <div class="item-left">
          <span class="item-icon icon-lock">
            <svg viewBox="0 0 24 24" width="18" height="18"><rect x="4" y="10" width="16" height="11" rx="2" fill="none" stroke="currentColor" stroke-width="1.8"/><path d="M8 10V7a4 4 0 0 1 8 0v3" fill="none" stroke="currentColor" stroke-width="1.8"/></svg>
          </span>
          <span class="item-name">修改密码</span>
        </div>
        <svg class="arrow" viewBox="0 0 24 24" width="16" height="16"><path d="M9 6l6 6-6 6" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>
      </div>
    </div>

    <!-- 其他分组 -->
    <div class="group-card">
      <div class="group-title">其他</div>
      <div class="list-item logout-item" @click="onLogout">
        <div class="item-left">
          <span class="item-icon icon-logout">
            <svg viewBox="0 0 24 24" width="18" height="18"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </span>
          <span class="item-name">退出登录</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

const onLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '退出登录',
      cancelButtonText: '取消',
      type: 'warning'
    })
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    localStorage.removeItem('name')
    localStorage.removeItem('userId')
    router.push('/login')
  } catch { /* 取消 */ }
}
</script>

<style scoped>
.settings-page {
  max-width: 720px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.group-card {
  background: #fff;
  border-radius: 14px;
  border: 1px solid #ECEEF1;
  box-shadow: 0 2px 12px rgba(15, 27, 45, 0.06);
  overflow: hidden;
}

.group-title {
  padding: 14px 20px 6px;
  font-size: 13px;
  color: #9AA3AD;
  font-weight: 500;
}

.list-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  cursor: pointer;
  transition: background .15s;
}
.list-item:hover { background: #F7F8FA; }
.logout-item:hover { background: #FFF1F0; }

.item-left {
  display: flex;
  align-items: center;
  gap: 14px;
}
.item-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.icon-profile { background: #E8F5FF; color: #4A90E2; }
.icon-address { background: #EFF6FF; color: #60A5FA; }
.icon-lock { background: #F0ECFF; color: #8B7CF6; }
.icon-logout { background: #FFECEC; color: #E54D2E; }
.logout-item .item-name { color: #E54D2E; }

.item-name {
  font-size: 15px;
  color: #1A2A3A;
  font-weight: 500;
}

.arrow { color: #C8CDD3; flex-shrink: 0; }

.divider {
  height: 1px;
  background: #F0F1F4;
  margin: 0 20px;
}
</style>
