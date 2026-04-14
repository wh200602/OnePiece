<template>
  <div class="one-piece-expo">
    <!-- 顶部导航栏 -->
    <header class="top-header">
      <div class="logo-text">ONE PIECE</div>
      <nav class="nav-links">
        <a href="#" class="nav-item" @click.prevent="$router.push('/group')">势力划分</a>
        <a href="#" class="nav-item">船长室</a>
        <a href="#" class="nav-item">海贼聚会</a>
        <a href="#" class="nav-item" @click.prevent="$router.push('/shop')">万博会</a>
        <a href="#" class="nav-item active">限时夺宝</a>
        <a href="#" class="nav-item" @click.prevent="$router.push('/cart')">藏宝箱</a>
      </nav>
    </header>

    <!-- 主体内容 -->
    <main class="main-content">
      <!-- 背景气泡动画 -->
      <div class="bubbles">
        <span v-for="n in 20" :key="n" class="bubble"></span>
      </div>

      <div class="center-area">
        <!-- 页面标题区 -->
        <div class="page-header">
          <h1 class="page-title">限时夺宝</h1>
          <p class="page-desc">海贼王的宝藏限时开启，手快有手慢无！</p>
        </div>

        <!-- 店铺选择器 -->
        <div class="shop-selector">
          <label class="shop-label">选择店铺：</label>
          <input
            v-model.number="shopIdInput"
            type="number"
            min="1"
            class="shop-input"
            placeholder="输入店铺ID"
            @keyup.enter="fetchVouchers"
          />
          <button class="search-btn" @click="fetchVouchers" :disabled="loading">
            {{ loading ? '加载中...' : '查询优惠券' }}
          </button>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-message">正在搜寻宝藏...</div>

        <!-- 错误提示 -->
        <div v-if="error" class="error-message">{{ error }}</div>

        <!-- 无数据提示 -->
        <div v-if="!loading && !error && searched && vouchers.length === 0" class="empty-message">
          该店铺暂无优惠券活动
        </div>

        <!-- 优惠券列表 -->
        <div v-if="vouchers.length > 0" class="voucher-grid">
          <div
            v-for="voucher in vouchers"
            :key="voucher.id"
            class="voucher-card"
            :class="{ 'voucher-disabled': !isActive(voucher) }"
          >
            <!-- 左侧金额区 -->
            <div class="voucher-left">
              <div class="voucher-value">
                <span class="currency">¥</span>
                <span class="amount">{{ formatPrice(voucher.actualValue) }}</span>
              </div>
              <div class="voucher-condition">
                满{{ formatPrice(voucher.payValue) }}可用
              </div>
            </div>

            <!-- 锯齿分割线 -->
            <div class="voucher-divider">
              <span v-for="i in 8" :key="i" class="dot"></span>
            </div>

            <!-- 右侧信息区 -->
            <div class="voucher-right">
              <h3 class="voucher-title">{{ voucher.title }}</h3>
              <p class="voucher-subtitle" v-if="voucher.subTitle">{{ voucher.subTitle }}</p>
              <p class="voucher-rules" v-if="voucher.rules">{{ voucher.rules }}</p>

              <!-- 时间信息 -->
              <div class="voucher-time">
                <span v-if="getStatus(voucher) === 'not_started'" class="time-tag upcoming">
                  {{ formatTime(voucher.beginTime) }} 开抢
                </span>
                <span v-else-if="getStatus(voucher) === 'active'" class="time-tag active-tag">
                  抢购中 · 截止 {{ formatTime(voucher.endTime) }}
                </span>
                <span v-else class="time-tag expired">已结束</span>
              </div>

              <!-- 库存进度条 -->
              <div class="stock-bar" v-if="voucher.stock !== undefined">
                <div class="stock-progress" :style="{ width: stockPercent(voucher) + '%' }"></div>
                <span class="stock-text">剩余 {{ voucher.stock }} 张</span>
              </div>

              <!-- 秒杀按钮 -->
              <button
                class="seckill-btn"
                :class="{
                  'btn-active': getStatus(voucher) === 'active' && voucher.stock > 0,
                  'btn-disabled': getStatus(voucher) !== 'active' || voucher.stock <= 0
                }"
                :disabled="getStatus(voucher) !== 'active' || voucher.stock <= 0 || seckilling === voucher.id"
                @click="handleSeckill(voucher)"
              >
                <template v-if="seckilling === voucher.id">
                  <span class="btn-loading"></span> 抢购中...
                </template>
                <template v-else-if="getStatus(voucher) === 'not_started'">未开始</template>
                <template v-else-if="getStatus(voucher) === 'expired'">已结束</template>
                <template v-else-if="voucher.stock <= 0">已抢光</template>
                <template v-else>立即抢购</template>
              </button>
            </div>
          </div>
        </div>

        <!-- 秒杀成功弹窗 -->
        <div v-if="showSuccessDialog" class="dialog-overlay" @click.self="showSuccessDialog = false">
          <div class="dialog-box">
            <div class="dialog-icon">🎉</div>
            <h2 class="dialog-title">恭喜，夺宝成功！</h2>
            <p class="dialog-order">订单号：{{ successOrderId }}</p>
            <button class="dialog-btn" @click="showSuccessDialog = false">确认</button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useRoute } from 'vue-router'
import { queryVoucherOfShopAPI, seckillVoucherAPI } from '@/api/voucher.js'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

// ========== 响应式数据 ==========
const shopIdInput = ref(Number(route.params.shopId) || 1)
const vouchers = ref([])
const loading = ref(false)
const error = ref(null)
const searched = ref(false)
const seckilling = ref(null) // 当前正在秒杀的优惠券ID
const showSuccessDialog = ref(false)
const successOrderId = ref(null)

// 初始库存快照（用于计算进度条百分比）
const initialStockMap = ref({})

// ========== 工具函数 ==========

/** 分转元 */
const formatPrice = (fen) => {
  if (fen === null || fen === undefined) return '0.00'
  return (fen / 100).toFixed(2)
}

/** 格式化时间 */
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  // 兼容 "2025-04-14T10:00:00" 和 "2025-04-14 10:00:00" 格式
  const d = new Date(timeStr.replace(' ', 'T'))
  if (isNaN(d.getTime())) return timeStr
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getMonth() + 1}/${d.getDate()} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

/** 获取优惠券状态 */
const getStatus = (voucher) => {
  const now = Date.now()
  const begin = new Date(voucher.beginTime?.replace(' ', 'T')).getTime()
  const end = new Date(voucher.endTime?.replace(' ', 'T')).getTime()

  if (isNaN(begin) || isNaN(end)) return 'active' // 时间字段缺失则默认可用
  if (now < begin) return 'not_started'
  if (now > end) return 'expired'
  return 'active'
}

/** 是否可参与秒杀 */
const isActive = (voucher) => {
  return getStatus(voucher) === 'active' && voucher.stock > 0
}

/** 库存百分比 */
const stockPercent = (voucher) => {
  const initial = initialStockMap.value[voucher.id] || voucher.stock || 1
  return Math.max(0, Math.min(100, (voucher.stock / initial) * 100))
}

// ========== 业务逻辑 ==========

/** 查询店铺优惠券 */
const fetchVouchers = async () => {
  if (!shopIdInput.value || shopIdInput.value <= 0) {
    ElMessage.warning('请输入有效的店铺ID')
    return
  }

  loading.value = true
  error.value = null
  searched.value = true

  try {
    const data = await queryVoucherOfShopAPI(shopIdInput.value)
    vouchers.value = data || []

    // 记录初始库存
    vouchers.value.forEach((v) => {
      if (!initialStockMap.value[v.id]) {
        initialStockMap.value[v.id] = v.stock || 0
      }
    })
  } catch (err) {
    error.value = err.message || '查询优惠券失败'
    vouchers.value = []
  } finally {
    loading.value = false
  }
}

/** 秒杀抢购 */
const handleSeckill = async (voucher) => {
  // 前端二次校验
  if (getStatus(voucher) !== 'active') {
    ElMessage.warning('当前不在秒杀时间范围内')
    return
  }
  if (voucher.stock <= 0) {
    ElMessage.warning('库存不足，下次早点来吧')
    return
  }

  // 检查是否已登录
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录后再抢购')
    router.push('/login')
    return
  }

  seckilling.value = voucher.id

  try {
    const orderId = await seckillVoucherAPI(voucher.id)

    // 秒杀成功
    successOrderId.value = orderId
    showSuccessDialog.value = true

    // 本地扣减库存（乐观更新）
    voucher.stock = Math.max(0, voucher.stock - 1)
  } catch (err) {
    // 错误提示已由 request.js 拦截器处理（如"每人限购一张"、"库存不足"等）
    console.error('秒杀失败:', err)
  } finally {
    seckilling.value = null
  }
}

// ========== 初始化 ==========
// 如果路由携带了 shopId 参数，自动查询
if (route.params.shopId) {
  fetchVouchers()
}
</script>

<style scoped>
/* ====== 全局重置 ====== */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.one-piece-expo {
  font-family: 'Arial Rounded MT Bold', 'Arial', sans-serif;
  color: #fff;
  background-color: #001220;
  overflow-y: auto;
  position: relative;
  min-height: 100vh;
}

/* ====== 顶部导航栏 ====== */
.top-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 40px;
  background: rgba(0, 0, 0, 0.5);
  border-bottom: 2px solid #ffd700;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 20;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  color: #ffd700;
  text-shadow: 2px 2px 0 #000;
}

.nav-links {
  display: flex;
  gap: 20px;
}

.nav-item {
  color: #fff;
  text-decoration: none;
  font-size: 16px;
  transition: color 0.3s;
  position: relative;
  cursor: pointer;
}

.nav-item:hover {
  color: #ffd700;
}

.nav-item.active {
  color: #ffd700;
  font-weight: bold;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 0;
  width: 100%;
  height: 2px;
  background: #ffd700;
}

/* ====== 主体内容 ====== */
.main-content {
  position: relative;
  min-height: 100vh;
  padding-top: 80px;
}

/* ====== 背景气泡 ====== */
.bubbles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.bubble {
  position: absolute;
  width: 10px;
  height: 10px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  animation: floatUp 10s infinite linear;
}

.bubble:nth-child(1) { left: 10%; top: 20%; animation-duration: 8s; }
.bubble:nth-child(2) { left: 30%; top: 50%; animation-duration: 12s; }
.bubble:nth-child(3) { left: 50%; top: 10%; animation-duration: 15s; }
.bubble:nth-child(4) { left: 70%; top: 70%; animation-duration: 9s; }
.bubble:nth-child(5) { left: 90%; top: 30%; animation-duration: 11s; }

@keyframes floatUp {
  0% { transform: translateY(0) scale(1); opacity: 1; }
  100% { transform: translateY(-100vh) scale(1.5); opacity: 0; }
}

/* ====== 中央区域 ====== */
.center-area {
  position: relative;
  z-index: 5;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

/* ====== 页面标题 ====== */
.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 42px;
  font-weight: bold;
  color: #ffd700;
  text-shadow: 0 0 10px #ffd700, 0 0 20px #ff4500;
  letter-spacing: 5px;
  margin-bottom: 8px;
}

.page-desc {
  color: #a0a0a0;
  font-size: 16px;
}

/* ====== 店铺选择器 ====== */
.shop-selector {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.shop-label {
  color: #ccc;
  font-size: 16px;
}

.shop-input {
  width: 160px;
  padding: 10px 16px;
  border-radius: 25px;
  border: 1px solid #ffd700;
  background: rgba(0, 0, 0, 0.5);
  color: #ffd700;
  font-size: 16px;
  text-align: center;
  outline: none;
  transition: box-shadow 0.3s;
}

.shop-input:focus {
  box-shadow: 0 0 10px rgba(255, 215, 0, 0.5);
}

.shop-input::placeholder {
  color: #666;
}

.search-btn {
  padding: 10px 24px;
  border-radius: 25px;
  border: none;
  background: linear-gradient(to right, #ffd700, #ff8c00);
  color: #001220;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: transform 0.3s, opacity 0.3s;
}

.search-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  opacity: 0.9;
}

.search-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* ====== 提示信息 ====== */
.loading-message,
.empty-message {
  text-align: center;
  color: #a0a0a0;
  font-size: 18px;
  margin: 40px 0;
}

.error-message {
  text-align: center;
  color: #ff6b6b;
  font-size: 18px;
  margin: 40px 0;
}

/* ====== 优惠券列表 ====== */
.voucher-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(520px, 1fr));
  gap: 24px;
}

/* ====== 优惠券卡片 ====== */
.voucher-card {
  display: flex;
  align-items: stretch;
  background: rgba(16, 30, 45, 0.9);
  border-radius: 16px;
  border: 1px solid #2c3e50;
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s, border-color 0.3s;
}

.voucher-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(255, 215, 0, 0.15);
  border-color: #ffd700;
}

.voucher-card.voucher-disabled {
  opacity: 0.55;
  filter: grayscale(50%);
}

/* 左侧金额 */
.voucher-left {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 24px 28px;
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.15), rgba(255, 68, 68, 0.25));
  min-width: 140px;
}

.voucher-value {
  display: flex;
  align-items: baseline;
}

.currency {
  font-size: 18px;
  color: #ff6b6b;
  margin-right: 2px;
}

.amount {
  font-size: 40px;
  font-weight: bold;
  color: #ff6b6b;
  line-height: 1;
}

.voucher-condition {
  color: #ff9999;
  font-size: 13px;
  margin-top: 8px;
}

/* 锯齿分割线 */
.voucher-divider {
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  padding: 8px 0;
}

.dot {
  width: 10px;
  height: 10px;
  background: #001220;
  border-radius: 50%;
  margin: 2px -5px;
}

/* 右侧信息 */
.voucher-right {
  flex: 1;
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.voucher-title {
  font-size: 18px;
  color: #ffd700;
  font-weight: bold;
}

.voucher-subtitle {
  color: #bbb;
  font-size: 14px;
}

.voucher-rules {
  color: #888;
  font-size: 12px;
  line-height: 1.5;
}

/* 时间标签 */
.voucher-time {
  margin: 4px 0;
}

.time-tag {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 12px;
}

.time-tag.upcoming {
  background: rgba(78, 205, 196, 0.2);
  color: #4ecdc4;
  border: 1px solid rgba(78, 205, 196, 0.4);
}

.time-tag.active-tag {
  background: rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
  border: 1px solid rgba(255, 107, 107, 0.4);
  animation: pulse-glow 2s infinite;
}

.time-tag.expired {
  background: rgba(128, 128, 128, 0.2);
  color: #888;
  border: 1px solid rgba(128, 128, 128, 0.4);
}

@keyframes pulse-glow {
  0%, 100% { box-shadow: 0 0 4px rgba(255, 107, 107, 0.3); }
  50% { box-shadow: 0 0 12px rgba(255, 107, 107, 0.6); }
}

/* 库存进度条 */
.stock-bar {
  position: relative;
  height: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  overflow: hidden;
}

.stock-progress {
  height: 100%;
  background: linear-gradient(to right, #ff6b6b, #ff4500);
  border-radius: 10px;
  transition: width 0.5s ease;
  min-width: 8%;
}

.stock-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 11px;
  color: #fff;
  white-space: nowrap;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

/* 秒杀按钮 */
.seckill-btn {
  margin-top: 4px;
  padding: 10px 0;
  border-radius: 8px;
  border: none;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.seckill-btn.btn-active {
  background: linear-gradient(to right, #ff6b6b, #ff4500);
  color: #fff;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.4);
}

.seckill-btn.btn-active:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.6);
}

.seckill-btn.btn-disabled {
  background: rgba(128, 128, 128, 0.3);
  color: #666;
  cursor: not-allowed;
}

/* 按钮加载动画 */
.btn-loading {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ====== 成功弹窗 ====== */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 100;
}

.dialog-box {
  background: linear-gradient(135deg, #001830, #002040);
  border: 2px solid #ffd700;
  border-radius: 20px;
  padding: 40px 50px;
  text-align: center;
  box-shadow: 0 0 40px rgba(255, 215, 0, 0.3);
  animation: dialogPop 0.4s ease;
}

@keyframes dialogPop {
  0% { transform: scale(0.7); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}

.dialog-icon {
  font-size: 60px;
  margin-bottom: 16px;
}

.dialog-title {
  font-size: 24px;
  color: #ffd700;
  margin-bottom: 12px;
}

.dialog-order {
  color: #a0a0a0;
  font-size: 14px;
  margin-bottom: 24px;
}

.dialog-btn {
  padding: 10px 40px;
  border-radius: 25px;
  border: none;
  background: linear-gradient(to right, #ffd700, #ff8c00);
  color: #001220;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: transform 0.3s;
}

.dialog-btn:hover {
  transform: translateY(-2px);
}

/* ====== 响应式 ====== */
@media (max-width: 768px) {
  .voucher-grid {
    grid-template-columns: 1fr;
  }

  .voucher-card {
    flex-direction: column;
  }

  .voucher-left {
    flex-direction: row;
    gap: 12px;
    padding: 16px;
    min-width: unset;
  }

  .voucher-divider {
    flex-direction: row;
    padding: 0 8px;
  }

  .dot {
    margin: -5px 2px;
  }

  .page-title {
    font-size: 28px;
  }

  .top-header {
    padding: 12px 16px;
  }

  .nav-links {
    gap: 10px;
    font-size: 14px;
  }
}
</style>
