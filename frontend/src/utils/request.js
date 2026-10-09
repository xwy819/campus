import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器：自动携带 token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['token'] = token
  }
  return config
})

// 响应拦截器：统一处理 code
request.interceptors.response.use(
  response => {
    const res = response.data
    // 业务失败：HTTP 200 但 code 非 0，属于业务逻辑错误，展示后端返回的明确文案
    if (res.code !== 0) {
      ElMessage.error(res.message || '请求失败')
      if (res.code === 401) {
        // 仅当本次请求确实携带了 token 时，才判定为"登录已失效"并跳转登录页。
        // 未携带 token 的公开请求（如注册页手机号校验 /user/checkPhone）返回 401
        // 不应触发整页跳转，否则会导致注册页"闪退/整页刷新、控制台清空"。
        const headers = response.config && response.config.headers
        const sentToken = headers && (headers.token ?? (typeof headers.get === 'function' ? headers.get('token') : undefined))
        if (sentToken) {
          localStorage.removeItem('token')
          localStorage.removeItem('role')
          localStorage.removeItem('name')
          localStorage.removeItem('userId')
          window.location.href = '/login'
        }
      }
      return Promise.reject(res)
    }
    return res
  },
  error => {
    // 进入此处说明非正常 200 响应：可能是网络层失败，或后端抛异常返回 4xx/5xx
    let msg = '网络错误'
    if (error.response) {
      // 服务器有响应但状态码非 2xx
      const status = error.response.status
      const data = error.response.data
      // 优先尝试读取后端业务错误信息（{code, message} 结构）
      if (data && typeof data === 'object' && data.message) {
        msg = data.message
      } else if (status === 401) {
        msg = '登录已失效，请重新登录'
      } else if (status === 403) {
        msg = '无权限访问'
      } else if (status >= 500) {
        msg = '服务器异常，请稍后重试'
      } else {
        msg = `请求失败（${status}）`
      }
    } else if (error.request) {
      // 请求已发出但无响应：网络层失败
      msg = '网络连接失败，请检查网络后重试'
    } else {
      // 请求配置错误等
      msg = error.message || '请求异常'
    }
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export default request
