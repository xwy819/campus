<template>
  <router-view v-slot="{ Component }">
    <transition name="fade-slide" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<script setup>
</script>

<style>
/* ============ Reset & 基础 ============ */
*, *::before, *::after { margin: 0; padding: 0; box-sizing: border-box; }

html { scroll-behavior: smooth; }

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC',
    'Microsoft YaHei', 'Helvetica Neue', sans-serif;
  background: var(--bg-page);
  color: var(--text-primary);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  line-height: 1.6;
  min-height: 100vh;
  font-size: var(--fs-body);
}

/* ============ CSS 变量体系 ============ */
:root {
  /* —— 校园清新蓝主色 —— */
  --brand-50:  #EFF6FF;
  --brand-100: #DBEAFE;
  --brand-200: #BFDBFE;
  --brand-300: #93C5FD;
  --brand-400: #60A5FA;
  --brand-500: #3B82F6;   /* 主色 */
  --brand-600: #2563EB;
  --brand-700: #1D4ED8;

  /* 深蓝灰背景 */
  --ink-50:  #F0F4F8;
  --ink-100: #E1E7EF;
  --ink-200: #B8C4D1;
  --ink-300: #8395A8;
  --ink-400: #5A6E83;
  --ink-500: #3A4C5E;
  --ink-600: #2A3B4C;
  --ink-700: #1A2A3A;
  --ink-800: #0F1B2D;

  /* 功能色 */
  --success: #10B981;
  --success-soft: #ECFDF5;
  --danger:  #EF4444;
  --danger-soft: #FEF2F2;
  --warning: #F59E0B;
  --warning-soft: #FFFBEB;

  /* 语义化别名 */
  --bg-page:     #F0F4F8;
  --bg-card:     #FFFFFF;
  --bg-hover:    var(--brand-50);
  --bg-active:   var(--brand-100);

  /* —— 三级字体层级 —— */
  --text-primary:   #1E293B;
  --text-secondary: #64748B;
  --text-muted:     #94A3B8;
  --text-inverse:   #FFFFFF;

  /* —— 字号 —— */
  --fs-title:   20px;
  --fs-subtitle: 16px;
  --fs-body:    14px;
  --fs-caption: 12px;

  /* —— 字重 —— */
  --fw-bold:    700;
  --fw-medium:  500;
  --fw-regular: 400;

  /* —— 统一留白 —— */
  --space-xs:  4px;
  --space-sm:  8px;
  --space-md:  16px;
  --space-lg:  24px;
  --space-xl:  32px;

  --border-light:   #E2E8F0;
  --border-base:    #CBD5E1;
  --border-dark:    #94A3B8;

  /* —— 分层阴影体系 —— */
  --shadow-sm:  0 1px 2px rgba(15, 23, 42, 0.04), 0 1px 3px rgba(15, 23, 42, 0.06);
  --shadow-md:  0 4px 6px rgba(15, 23, 42, 0.05), 0 2px 4px rgba(15, 23, 42, 0.03), 0 8px 16px rgba(15, 23, 42, 0.04);
  --shadow-lg:  0 10px 15px rgba(15, 23, 42, 0.06), 0 4px 6px rgba(15, 23, 42, 0.04), 0 20px 40px rgba(15, 23, 42, 0.05);
  --shadow-brand-glow: 0 4px 16px rgba(59, 130, 246, 0.25);

  /* —— 统一圆角 12px 体系 —— */
  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 12px;
  --radius-xl: 16px;
  --radius-full: 999px;

  --transition-fast: 0.15s ease;
  --transition-base: 0.2s ease;
  --transition-slow: 0.3s ease;

  /* Element Plus 主题色覆盖 */
  --el-color-primary: var(--brand-500);
  --el-color-primary-light-3: var(--brand-400);
  --el-color-primary-light-5: var(--brand-300);
  --el-color-primary-light-7: var(--brand-200);
  --el-color-primary-light-8: var(--brand-100);
  --el-color-primary-light-9: var(--brand-50);
  --el-color-primary-dark-2: var(--brand-600);
  --el-color-danger: var(--danger);
  --el-color-danger-light-9: var(--danger-soft);
  --el-color-success: var(--success);
  --el-color-success-light-9: var(--success-soft);
  --el-color-warning: var(--warning);
  --el-color-warning-light-9: var(--warning-soft);
  --el-border-radius-base: var(--radius-md);
  --el-font-family: inherit;
}

/* ============ 全局滚动条美化 ============ */
::-webkit-scrollbar { width: 8px; height: 8px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb {
  background: #D0D5DC;
  border-radius: 4px;
  transition: background var(--transition-base);
}
::-webkit-scrollbar-thumb:hover { background: #B8C4D1; }

/* ============ 页面过渡动画 ============ */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity var(--transition-slow), transform var(--transition-slow);
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(8px);
}
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

/* ============ 通用工具类 ============ */
.page-fade-in { animation: pageFadeIn 0.4s ease both; }
@keyframes pageFadeIn {
  from { opacity: 0; transform: translateY(12px); }
  to   { opacity: 1; transform: translateY(0); }
}

.page-title {
  font-size: var(--fs-title);
  font-weight: var(--fw-bold);
  color: var(--text-primary);
  margin-bottom: var(--space-lg);
  letter-spacing: 0.5px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.page-title::before {
  content: '';
  width: 4px;
  height: 20px;
  background: linear-gradient(180deg, var(--brand-400), var(--brand-600));
  border-radius: 2px;
}

/* ============ Element Plus 深度定制 ============ */

/* --- 卡片 --- */
.el-card {
  border: 1px solid var(--border-light) !important;
  border-radius: var(--radius-lg) !important;
  box-shadow: var(--shadow-sm);
  transition: box-shadow var(--transition-base);
}
.el-card:hover { box-shadow: var(--shadow-md); }
.el-card__header {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-light);
  background: linear-gradient(to bottom, #fff, #FAFBFC);
}
.el-card__body { padding: 20px; }

/* --- 按钮 --- */
.el-button {
  border-radius: var(--radius-md);
  font-weight: 500;
  transition: all var(--transition-base);
}
.el-button--primary {
  background: linear-gradient(135deg, var(--brand-400), var(--brand-600)) !important;
  border-color: transparent !important;
  box-shadow: 0 2px 6px rgba(59, 130, 246, 0.20);
}
.el-button--primary:hover {
  background: linear-gradient(135deg, var(--brand-300), var(--brand-500)) !important;
  transform: translateY(-1px);
  box-shadow: var(--shadow-brand-glow);
}
.el-button--primary:active { transform: translateY(0); }
.el-button--primary:disabled {
  transform: none;
  box-shadow: none;
  opacity: 0.6;
}
.el-button--success {
  background: linear-gradient(135deg, #34D399, var(--success)) !important;
  border-color: transparent !important;
}
.el-button--danger {
  background: linear-gradient(135deg, #F87171, var(--danger)) !important;
  border-color: transparent !important;
}

/* --- 表格 --- */
.el-table {
  color: var(--text-primary);
  font-size: 14px;
  border-radius: var(--radius-lg);
  overflow: hidden;
}
.el-table th.el-table__cell {
  background: #F7F8FA !important;
  color: var(--ink-500);
  font-weight: 600;
  height: 48px;
  line-height: 48px;
}
.el-table td.el-table__cell { vertical-align: middle; }
.el-table tr:hover > td.el-table__cell { background: var(--bg-hover) !important; }
.el-table .el-table__row:nth-child(even) td.el-table__cell { background: #FAFBFC; }
.el-table .el-table__row:nth-child(even):hover > td.el-table__cell { background: var(--bg-hover) !important; }

/* --- 分页 --- */
.el-pagination { justify-content: center; }
.el-pagination .el-pager li,
.el-pagination button {
  border-radius: var(--radius-sm);
  min-width: 32px;
  height: 32px;
  line-height: 32px;
  transition: all var(--transition-fast);
}
.el-pagination .el-pager li.is-active {
  background: linear-gradient(135deg, var(--brand-400), var(--brand-600)) !important;
  color: #fff !important;
  box-shadow: var(--shadow-brand-glow);
}
.el-pagination .el-pager li:hover:not(.is-active) {
  color: var(--brand-500);
  background: var(--brand-50);
}

/* --- 输入框 --- */
.el-input__wrapper,
.el-textarea__inner {
  border-radius: var(--radius-md) !important;
  transition: all var(--transition-base);
}
.el-input__wrapper.is-focus {
  box-shadow: 0 0 0 1px var(--brand-500) inset, 0 0 0 4px rgba(59, 130, 246, 0.10) !important;
}
.el-textarea__inner:focus {
  box-shadow: 0 0 0 1px var(--brand-500) inset, 0 0 0 4px rgba(59, 130, 246, 0.10) !important;
}

/* --- Tag --- */
.el-tag {
  border-radius: var(--radius-sm);
  font-weight: 500;
}

/* --- Dialog --- */
.el-dialog {
  border-radius: var(--radius-lg) !important;
  overflow: hidden;
}
.el-dialog__header { padding: 22px 24px 14px; }
.el-dialog__title { font-weight: 700; font-size: 17px; }
.el-dialog__body { padding: 10px 24px; }
.el-dialog__footer { padding: 18px 24px 24px; }
.el-overlay {
  background-color: rgba(15, 27, 45, 0.55) !important;
  backdrop-filter: blur(2px);
}

/* --- Message --- */
.el-message {
  border-radius: var(--radius-md) !important;
  box-shadow: var(--shadow-lg) !important;
}

/* --- 下拉菜单 --- */
.el-select-dropdown,
.el-dropdown-menu {
  border-radius: var(--radius-md) !important;
  box-shadow: var(--shadow-md) !important;
  border: 1px solid var(--border-light) !important;
}
.el-select-dropdown__item.is-hovering,
.el-dropdown-menu__item:hover {
  background: var(--brand-50) !important;
  color: var(--brand-600);
}

/* --- Checkbox / Radio 选中色 --- */
.el-checkbox__input.is-checked .el-checkbox__inner,
.el-radio__input.is-checked .el-radio__inner {
  background-color: var(--brand-500);
  border-color: var(--brand-500);
}
.el-checkbox__input.is-checked + .el-checkbox__label,
.el-radio__input.is-checked + .el-radio__label {
  color: var(--brand-600);
}

/* --- Empty --- */
.el-empty { padding: 30px 0; }

/* ============ 页面通用容器 ============ */
.page {
  padding: var(--space-lg);
  max-width: 1280px;
  margin: 0 auto;
}
.page-card {
  padding: var(--space-lg);
  animation: pageFadeIn 0.4s ease both;
}

/* ============ 简易响应式 ============ */
@media (max-width: 768px) {
  .page { padding: 16px; }
  .page-card { padding: 16px; }
  .page-title { font-size: 19px; }
}
</style>
