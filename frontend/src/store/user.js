import { reactive } from 'vue'

// 全局用户状态，任何组件修改后，所有引用它的组件自动响应
export const userStore = reactive({
  avatar: localStorage.getItem('avatar') || '',
  name: localStorage.getItem('name') || '',
  role: localStorage.getItem('role') || '',
  userId: localStorage.getItem('userId') || ''
})

export function setAvatar(url) {
  userStore.avatar = url
  if (url) localStorage.setItem('avatar', url)
  else localStorage.removeItem('avatar')
}

export function setName(n) {
  userStore.name = n
  if (n) localStorage.setItem('name', n)
  else localStorage.removeItem('name')
}

export function clearUser() {
  userStore.avatar = ''
  userStore.name = ''
  userStore.role = ''
  userStore.userId = ''
  localStorage.removeItem('token')
  localStorage.removeItem('avatar')
  localStorage.removeItem('name')
  localStorage.removeItem('role')
  localStorage.removeItem('userId')
}
