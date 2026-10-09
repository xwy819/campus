<template>
  <div class="pe-page">
    <div class="pe-card">
      <div class="pe-card-title">
        <h3>编辑闲置商品</h3>
      </div>

      <div class="pe-body">
        <!-- 左栏：图片管理 -->
        <div class="pe-left">
          <div class="pe-section-title">
            <span>商品图片</span>
          </div>
          <div class="pe-section-sub">最多上传 5 张，支持新增、删除、替换图片，第一张为商品主图</div>

          <!-- 已上传图片 -->
          <div class="pe-thumbs">
            <div
              v-for="(url, idx) in form.images"
              :key="idx"
              class="pe-thumb"
              :class="{ active: idx === 0 }"
            >
              <img :src="url" @click="previewImg(url)" />
              <span v-if="idx === 0" class="pe-thumb-badge">主图</span>
              <button type="button" class="pe-thumb-del" @click="removeImage(idx)" title="删除">×</button>
            </div>

            <!-- 上传框 -->
            <label v-if="form.images.length < 5" class="pe-upload">
              <input
                type="file"
                accept="image/jpeg,image/png,image/jpg"
                multiple
                @change="onFileChange"
                hidden
              />
              <div class="pe-upload-inner">
                <svg viewBox="0 0 24 24" width="28" height="28" aria-hidden="true">
                  <rect x="3" y="5" width="18" height="14" rx="2" fill="none" stroke="#1677ff" stroke-width="1.6"/>
                  <circle cx="8.5" cy="10.5" r="1.5" fill="#1677ff"/>
                  <path d="M21 17l-5-5-4 4-3-3-5 5" fill="none" stroke="#1677ff" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <div class="pe-upload-text">点击或拖拽图片到此处上传</div>
              </div>
            </label>

            <div v-if="form.images.length >= 5" class="pe-upload-disabled">
              图片数量已达上限
            </div>
          </div>

          <div v-if="uploadError" class="pe-upload-err">{{ uploadError }}</div>
        </div>

        <!-- 右栏：表单 -->
        <div class="pe-right">
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="90px"
            label-position="right"
          >
            <el-form-item label="标题" prop="title">
              <el-input v-model="form.title" maxlength="50" show-word-limit size="large" />
            </el-form-item>
            <el-form-item label="分类" prop="categoryId">
              <el-select v-model="form.categoryId" style="width:100%" size="large">
                <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="价格" prop="price">
              <el-input-number
                v-model="form.price"
                :min="0"
                :precision="2"
                :controls="false"
                style="width:100%"
                size="large"
              />
              <div class="pe-form-hint">仅输入数字，单位：元</div>
            </el-form-item>
            <el-form-item label="新旧程度" prop="conditionLevel">
              <el-select v-model="form.conditionLevel" style="width:100%" size="large">
                <el-option label="全新" :value="1" />
                <el-option label="九成新" :value="2" />
                <el-option label="八成新" :value="3" />
                <el-option label="七成新" :value="4" />
                <el-option label="五成及以下" :value="5" />
              </el-select>
            </el-form-item>
            <el-form-item label="商品描述" prop="description">
              <el-input
                v-model="form.description"
                type="textarea"
                :rows="6"
                maxlength="500"
                show-word-limit
                size="large"
              />
              <div class="pe-form-hint">详细描述物品成色、瑕疵，方便买家咨询</div>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <!-- 底部按钮 -->
      <div class="pe-footer">
        <el-button @click="resetForm">重置</el-button>
        <el-button @click="previewProduct">预览商品</el-button>
        <el-button @click="goBack">返回我的发布</el-button>
        <el-button type="primary" :loading="saving" @click="save">
          {{ saving ? '保存中…' : '保存修改' }}
        </el-button>
      </div>
    </div>

    <!-- 大图预览 -->
    <el-dialog v-model="previewVisible" :show-close="false" width="640px" align-center class="pe-preview-dialog">
      <div class="pe-preview-wrap">
        <img :src="previewUrl" />
        <button type="button" class="pe-preview-close" @click="previewVisible = false">×</button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const saving = ref(false)
const categories = ref([])
const uploadError = ref('')
const previewVisible = ref(false)
const previewUrl = ref('')
const initialSnap = ref(null) // 用于 reset 恢复初始值

const form = reactive({
  id: null,
  title: '',
  categoryId: null,
  price: 0,
  conditionLevel: 1,
  description: '',
  images: []
})

const rules = {
  title: [{ required: true, message: '请输入商品标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' },
    {
      validator: (rule, value, cb) => {
        if (value === null || value === undefined || value <= 0) return cb(new Error('价格必须大于 0'))
        cb()
      },
      trigger: 'blur'
    }
  ],
  conditionLevel: [{ required: true, message: '请选择新旧程度', trigger: 'change' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }]
}

// ========== 初始化 ==========
const loadCategories = async () => {
  try {
    const res = await request.get('/category/list')
    categories.value = res.data || []
  } catch (e) { /* ignore */ }
}

const loadProduct = async () => {
  const id = route.params.id
  try {
    const res = await request.get('/product/detail', { params: { id } })
    const d = res.data
    form.id = d.id
    form.title = d.title || ''
    form.categoryId = d.categoryId
    form.price = d.price
    form.conditionLevel = d.conditionLevel || 1
    form.description = d.description || ''
    // 图片：优先取 images 数组，兼容旧数据回退到 imageUrl
    if (Array.isArray(d.images) && d.images.length) {
      form.images = [...d.images]
    } else if (d.imageUrl) {
      form.images = [d.imageUrl]
    }
    // 快照一份用于 reset
    initialSnap.value = JSON.parse(JSON.stringify({
      title: form.title,
      categoryId: form.categoryId,
      price: form.price,
      conditionLevel: form.conditionLevel,
      description: form.description,
      images: [...form.images]
    }))
  } catch (e) {
    ElMessage.error('加载商品信息失败')
  }
}

// ========== 图片管理 ==========
const onFileChange = (e) => {
  uploadError.value = ''
  const files = Array.from(e.target.files || [])
  handleFiles(files)
  e.target.value = '' // 清空，允许重复选同文件
}

const handleFiles = async (files) => {
  const remaining = 5 - form.images.length
  if (remaining <= 0) {
    uploadError.value = '图片数量已达上限'
    return
  }
  const toProcess = files.slice(0, remaining)
  for (const f of toProcess) {
    if (!/^image\/(jpeg|png|jpg)$/.test(f.type)) {
      uploadError.value = '仅支持 JPG / PNG 格式'
      continue
    }
    if (f.size > 2 * 1024 * 1024) {
      uploadError.value = `图片 ${f.name} 超过 2MB`
      continue
    }
    const url = await readAsDataURL(f)
    form.images.push(url)
  }
}

const readAsDataURL = (file) => new Promise((resolve, reject) => {
  const r = new FileReader()
  r.onload = () => resolve(r.result)
  r.onerror = reject
  r.readAsDataURL(file)
})

const removeImage = (idx) => {
  form.images.splice(idx, 1)
}

const previewImg = (url) => {
  previewUrl.value = url
  previewVisible.value = true
}

// ========== 按钮 ==========
const goBack = () => router.push('/myProducts')

const previewProduct = () => {
  if (form.id) window.open(`/product/${form.id}`, '_blank')
  else ElMessage.warning('商品未保存，无法预览')
}

const resetForm = async () => {
  try {
    await ElMessageBox.confirm('确定要重置本次修改吗？将恢复到初始加载数据。', '提示', { type: 'warning' })
    if (initialSnap.value) {
      form.title = initialSnap.value.title
      form.categoryId = initialSnap.value.categoryId
      form.price = initialSnap.value.price
      form.conditionLevel = initialSnap.value.conditionLevel
      form.description = initialSnap.value.description
      form.images = [...initialSnap.value.images]
      formRef.value?.clearValidate()
    }
  } catch (e) { /* 取消 */ }
}

const save = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }
  if (form.images.length === 0) {
    ElMessage.warning('请至少上传一张商品图片')
    return
  }
  saving.value = true
  try {
    await request.put('/product/update', {
      id: form.id,
      title: form.title.trim(),
      categoryId: form.categoryId,
      price: form.price,
      conditionLevel: form.conditionLevel,
      description: form.description.trim(),
      images: form.images
    })
    ElMessage.success('保存成功')
    setTimeout(() => router.push('/myProducts'), 800)
  } catch (e) {
    // 错误由拦截器展示，保留表单
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  await loadCategories()
  await loadProduct()
})
</script>

<style scoped>
.pe-page {
  min-height: calc(100vh - 64px);
  background: #f0f5f9;
  padding: 24px 0;
}
.pe-card {
  max-width: 900px;
  margin: 0 auto;
  background: #ffffff;
  padding: 32px;
  border-radius: 12px;
  box-shadow: 0 2px 14px rgba(0, 0, 0, 0.08);
}
.pe-card-title h3 {
  margin: 0 0 24px 0;
  font-size: 22px;
  font-weight: 700;
  color: #1A2A3A;
  padding-left: 12px;
  border-left: 3px solid #1677ff;
}
.pe-body {
  display: flex;
  gap: 36px;
}
.pe-left {
  flex: 0 0 340px;
}
.pe-section-title {
  font-size: 15px;
  font-weight: 700;
  color: #1A2A3A;
  margin-bottom: 4px;
}
.pe-section-sub {
  font-size: 12px;
  color: #8c8c8c;
  margin-bottom: 18px;
}

/* 缩略图 */
.pe-thumbs {
  display: grid;
  grid-template-columns: repeat(3, 80px);
  gap: 12px;
}
.pe-thumb {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 6px;
  overflow: visible;
  cursor: zoom-in;
  transition: transform .2s;
}
.pe-thumb img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
  transition: transform .2s;
  display: block;
}
.pe-thumb:hover img {
  transform: scale(1.08);
}
.pe-thumb.active img {
  border: 2px solid #1677ff;
}
.pe-thumb-badge {
  position: absolute;
  top: -6px;
  left: -6px;
  background: #1677ff;
  color: #fff;
  font-size: 10px;
  padding: 1px 6px;
  border-radius: 8px;
  font-weight: 500;
  z-index: 2;
}
.pe-thumb-del {
  position: absolute;
  top: -6px;
  right: -6px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: none;
  background: #f53f3f;
  color: #fff;
  font-size: 16px;
  line-height: 1;
  cursor: pointer;
  display: none;
  align-items: center;
  justify-content: center;
  padding: 0;
  z-index: 2;
}
.pe-thumb:hover .pe-thumb-del {
  display: flex;
}
.pe-thumb-del:hover {
  background: #d4380d;
}

/* 上传框 */
.pe-upload {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  border: 1px dashed #cdd5e0;
  border-radius: 8px;
  cursor: pointer;
  transition: all .2s;
  background: #fafbfd;
}
.pe-upload:hover {
  border-color: #1677ff;
  background: #f0f7ff;
}
.pe-upload-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}
.pe-upload-text {
  font-size: 11px;
  color: #8c8c8c;
  text-align: center;
  padding: 0 4px;
  line-height: 1.3;
}
.pe-upload-disabled {
  width: 80px;
  height: 80px;
  border: 1px dashed #e5e7eb;
  border-radius: 8px;
  background: #f5f7fa;
  color: #bfbfbf;
  font-size: 11px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 0 6px;
  line-height: 1.3;
}
.pe-upload-err {
  margin-top: 10px;
  font-size: 12px;
  color: #f53f3f;
}

/* 右栏表单 */
.pe-right {
  flex: 1;
  min-width: 0;
}
.pe-right :deep(.el-form-item) {
  margin-bottom: 20px;
}
.pe-right :deep(.el-form-item__label) {
  font-size: 15px;
  color: #1A2A3A;
}
.pe-right :deep(.el-input__wrapper),
.pe-right :deep(.el-textarea__inner),
.pe-right :deep(.el-select__wrapper),
.pe-right :deep(.el-input-number) {
  font-size: 15px;
}
.pe-form-hint {
  font-size: 12px;
  color: #8c8c8c;
  margin-top: 6px;
  line-height: 1.4;
}

/* 底部按钮 */
.pe-footer {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
  gap: 16px;
}
.pe-footer :deep(.el-button) {
  border-radius: 8px;
  padding: 10px 22px;
  font-size: 14px;
  transition: all .2s;
}
.pe-footer :deep(.el-button--default) {
  background: #fff;
  border: 1px solid #1677ff;
  color: #1677ff;
}
.pe-footer :deep(.el-button--default):hover {
  background: #f0f7ff;
  border-color: #1677ff;
  color: #1677ff;
}
.pe-footer :deep(.el-button--primary) {
  background: #1677ff;
  border-color: #1677ff;
}
.pe-footer :deep(.el-button--primary:hover) {
  background: #0958d9;
  border-color: #0958d9;
}

/* 大图预览 */
.pe-preview-wrap {
  position: relative;
  text-align: center;
}
.pe-preview-wrap img {
  max-width: 100%;
  max-height: 70vh;
  border-radius: 8px;
}
.pe-preview-close {
  position: absolute;
  top: -12px;
  right: -12px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: #f53f3f;
  color: #fff;
  font-size: 20px;
  cursor: pointer;
  line-height: 1;
}

/* 响应式 */
@media (max-width: 768px) {
  .pe-card { padding: 20px; margin: 16px; }
  .pe-body { flex-direction: column; gap: 24px; }
  .pe-left { flex: none; width: 100%; }
  .pe-footer { flex-wrap: wrap; }
}
</style>
