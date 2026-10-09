import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'

const app = createApp(App)

// 全局错误捕获：防止未处理异常导致整页白屏（"闪退"），并输出详细错误便于定位
app.config.errorHandler = (err, instance, info) => {
  console.error('[Vue errorHandler]', err, 'info:', info, 'instance:', instance)
}

// 未处理的 Promise 拒绝同样捕获，避免导致崩溃
window.addEventListener('unhandledrejection', (e) => {
  console.error('[unhandledrejection]', e.reason)
})

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(ElementPlus)
app.use(router)
app.mount('#app')
