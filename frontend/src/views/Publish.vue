<template>
  <el-card class="page-card" body-class="page-card-body">
    <h3 class="page-title">发布商品</h3>
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="88px"
      class="form"
      @keyup.enter="submit"
    >
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入商品标题" maxlength="50" show-word-limit />
      </el-form-item>

      <el-form-item label="分类" prop="categoryId">
        <el-select v-model="form.categoryId" placeholder="选择分类" style="width:100%">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>

      <el-form-item label="价格" prop="price">
        <el-input-number
          v-model="form.price"
          :min="0"
          :precision="2"
          :step="0.01"
          :controls="false"
          placeholder="请输入价格"
          style="width:100%"
        />
      </el-form-item>

      <el-form-item label="新旧程度" prop="conditionLevel">
        <el-select v-model="form.conditionLevel" placeholder="选择新旧程度" style="width:100%">
          <el-option label="全新" :value="1" />
          <el-option label="九成新" :value="2" />
          <el-option label="八成新" :value="3" />
          <el-option label="较旧" :value="4" />
        </el-select>
      </el-form-item>

      <el-form-item label="商品图片" prop="imageUrl">
        <div class="upload-area">
          <div v-if="form.imageUrl" class="img-preview">
            <img :src="form.imageUrl" alt="商品图片" />
            <div class="preview-overlay">
              <button type="button" class="remove-img" @click="form.imageUrl = ''">
                <svg viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
                  <path d="M6 6l12 12M18 6l-12 12" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
              </button>
            </div>
          </div>
          <label v-else class="upload-btn">
            <input type="file" accept="image/*" @change="onFileChange" hidden />
            <div class="upload-inner">
              <div class="upload-icon">
                <svg viewBox="0 0 24 24" width="30" height="30" aria-hidden="true">
                  <path d="M12 16V5m0 0l-4 4m4-4l4 4M4 17v2a2 2 0 002 2h12a2 2 0 002-2v-2"
                        fill="none" stroke="#60A5FA" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <span class="upload-text">点击上传商品图片</span>
              <span class="upload-sub">支持 JPG / PNG，最大 5MB</span>
            </div>
          </label>
        </div>
      </el-form-item>

      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="5" placeholder="详细描述商品的成色、使用情况等" maxlength="500" show-word-limit />
      </el-form-item>

      <el-form-item label="交易方式" prop="deliveryType">
        <el-checkbox-group v-model="deliveryOptions">
          <el-checkbox :value="1">当面交易</el-checkbox>
          <el-checkbox :value="2">校内送货上门</el-checkbox>
        </el-checkbox-group>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :loading="submitting" :disabled="submitting" @click="submit">
          {{ submitting ? '提交中...' : '提交审核' }}
        </el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()
const formRef = ref(null)
const categories = ref([])
const submitting = ref(false)

const form = reactive({
  title: '',
  categoryId: null,
  price: 0,
  conditionLevel: 1,
  imageUrl: '',
  description: ''
})

// 交易方式位掩码：1 当面交易，2 校内送货上门，默认两者都支持
const deliveryOptions = ref([1, 2])

const rules = {
  title: [{ required: true, message: '请输入商品标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' },
    {
      validator: (rule, value, cb) => {
        if (value === null || value === undefined || value <= 0) {
          return cb(new Error('价格必须大于 0'))
        }
        cb()
      },
      trigger: 'blur'
    }
  ],
  conditionLevel: [{ required: true, message: '请选择新旧程度', trigger: 'change' }],
  description: [{ required: true, message: '请输入商品描述', trigger: 'blur' }]
}

const loadCategories = async () => {
  const res = await request.get('/category/list')
  categories.value = res.data
}

// 本地图片预览（不上传后端，仅存 data URL 用于展示）
const onFileChange = (e) => {
  const file = e.target.files[0]
  if (!file) return
  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    return
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片大小不能超过 5MB')
    return
  }
  const reader = new FileReader()
  reader.onload = () => {
    form.imageUrl = reader.result
  }
  reader.readAsDataURL(file)
}

const submit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }
  submitting.value = true
  try {
    if (deliveryOptions.value.length === 0) {
      ElMessage.warning('请至少选择一种交易方式')
      submitting.value = false
      return
    }
    const deliveryType = deliveryOptions.value.reduce((a, b) => a | b, 0)
    await request.post('/product/publish', {
      title: form.title.trim(),
      categoryId: form.categoryId,
      price: form.price,
      conditionLevel: form.conditionLevel,
      imageUrl: form.imageUrl || '',
      description: form.description.trim(),
      deliveryType
    })
    ElMessage.success('发布成功，等待管理员审核')
    router.push('/myProducts')
  } catch (e) {
    // 错误由 request 拦截器展示
  } finally {
    submitting.value = false
  }
}

onMounted(loadCategories)
</script>

<style scoped>
.form { max-width: 620px; margin-top: 8px; }

.upload-area { display: flex; flex-direction: column; gap: 10px; }

.upload-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 140px;
  height: 140px;
  border: 2px dashed var(--brand-200);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-base);
  background: var(--brand-50);
  position: relative;
  overflow: hidden;
}
.upload-btn::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, transparent 40%, rgba(96,165,250,0.08) 100%);
  opacity: 0;
  transition: opacity var(--transition-base);
}
.upload-btn:hover {
  border-color: var(--brand-400);
  background: #fff;
  transform: scale(1.02);
  box-shadow: var(--shadow-brand-glow);
}
.upload-btn:hover::before { opacity: 1; }

.upload-inner {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  text-align: center;
}
.upload-icon {
  width: 46px;
  height: 46px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 6px;
  box-shadow: var(--shadow-sm);
}
.upload-text {
  font-size: 13px;
  color: var(--brand-600);
  font-weight: 500;
}
.upload-sub {
  font-size: 11px;
  color: var(--text-muted);
}

.img-preview {
  position: relative;
  width: 160px;
  height: 160px;
  border-radius: var(--radius-lg);
  overflow: hidden;
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-sm);
}
.img-preview img { width: 100%; height: 100%; object-fit: cover; }

.preview-overlay {
  position: absolute;
  inset: 0;
  background: rgba(15, 27, 45, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity var(--transition-base);
}
.img-preview:hover .preview-overlay { opacity: 1; }

.remove-img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: var(--danger);
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-base);
  box-shadow: 0 2px 8px rgba(229, 77, 46, 0.4);
}
.remove-img:hover {
  transform: scale(1.1);
  background: #D03E21;
}
</style>
