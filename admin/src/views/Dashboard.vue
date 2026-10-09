<template>
  <div>
    <el-row :gutter="16">
      <el-col :span="6" v-for="c in cards" :key="c.label">
        <el-card>
          <div class="stat">
            <div class="num">{{ c.value }}</div>
            <div class="label">{{ c.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="12">
        <el-card>
          <h4>各分类商品数量</h4>
          <div ref="barRef" style="height:320px" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <h4>价格档位分布</h4>
          <div ref="pieRef" style="height:320px" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import request from '../utils/request'

const cards = ref([
  { label: '在售商品', value: 0 },
  { label: '注册用户', value: 0 },
  { label: '已完成订单', value: 0 },
  { label: '待审核商品', value: 0 }
])
const barRef = ref(null)
const pieRef = ref(null)
let barChart = null
let pieChart = null

const loadDashboard = async () => {
  const res = await request.get('/stats/dashboard')
  cards.value[0].value = res.data.productCnt
  cards.value[1].value = res.data.userCnt
  cards.value[2].value = res.data.orderCnt
  cards.value[3].value = res.data.pendingCnt
}

const loadCategory = async () => {
  const res = await request.get('/stats/category')
  const names = res.data.map(d => d.category_name)
  const counts = res.data.map(d => d.product_cnt)
  barChart = echarts.init(barRef.value)
  barChart.setOption({
    xAxis: { type: 'category', data: names },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: counts, itemStyle: { color: '#409EFF' } }]
  })
}

const loadPrice = async () => {
  const res = await request.get('/stats/price')
  pieChart = echarts.init(pieRef.value)
  pieChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: res.data.low_cnt, name: '低价(<20)' },
        { value: res.data.mid_cnt, name: '平价(20-100)' },
        { value: res.data.high_cnt, name: '高价(>=100)' }
      ]
    }]
  })
}

const resize = () => {
  barChart && barChart.resize()
  pieChart && pieChart.resize()
}

onMounted(() => {
  loadDashboard()
  loadCategory()
  loadPrice()
  window.addEventListener('resize', resize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resize)
  barChart && barChart.dispose()
  pieChart && pieChart.dispose()
})
</script>

<style scoped>
.stat { text-align: center; padding: 20px 0; }
.num { font-size: 32px; font-weight: bold; color: #409EFF; }
.label { color: #999; margin-top: 8px; }
</style>
