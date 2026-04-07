<template>
  <div class="detail-page">
    <!-- 粒子背景 -->
    <canvas ref="canvasRef" class="particle-canvas"></canvas>

    <!-- 导航栏 -->
    <header class="top-header">
      <div class="logo-text" @click="$router.push('/home')">ONE PIECE</div>
      <nav class="nav-links">
        <a href="#" class="nav-item" @click.prevent="$router.push('/group')">势力划分</a>
        <a href="#" class="nav-item" @click.prevent="$router.push('/shop')">万博会</a>
        <a href="#" class="nav-item" @click.prevent="$router.push('/cart')">藏宝箱</a>
      </nav>
    </header>

    <!-- 加载态 -->
    <main class="main-content" v-if="loading">
      <div class="loading-state">
        <div class="loading-spinner"></div>
        <p>正在打捞宝藏信息...</p>
      </div>
    </main>

    <!-- 错误态 -->
    <main class="main-content" v-else-if="error">
      <div class="error-state">
        <span class="error-icon">💀</span>
        <p>{{ error }}</p>
        <button class="back-btn" @click="$router.push('/shop')">返回万博会</button>
      </div>
    </main>

    <!-- 商品详情 -->
    <main class="main-content" v-else-if="product">
      <!-- 返回按钮 -->
      <div class="breadcrumb" :class="{ 'fade-in': mounted }">
        <a @click.prevent="$router.push('/shop')">万博会</a>
        <span class="sep">/</span>
        <span class="current">{{ product.productName }}</span>
      </div>

      <div class="detail-container" :class="{ 'fade-in': mounted }">
        <!-- 左侧：商品图片 -->
        <div class="image-section">
          <div class="image-frame">
            <div class="corner corner-tl"></div>
            <div class="corner corner-tr"></div>
            <div class="corner corner-bl"></div>
            <div class="corner corner-br"></div>
            <img :src="formatImage(product.imageUrl)" :alt="product.productName" class="product-img" />
            <div v-if="product.isLimited" class="limited-badge">限定</div>
          </div>
        </div>

        <!-- 右侧：商品信息 -->
        <div class="info-section">
          <!-- 商品名 -->
          <h1 class="product-name">{{ product.productName }}</h1>

          <!-- 标签行 -->
          <div class="tag-row">
            <span v-if="product.isLimited" class="tag tag-limited">限定商品</span>
            <span v-if="product.stock > 0" class="tag tag-stock">有货</span>
            <span v-else class="tag tag-out">缺货</span>
            <span v-if="categoryName" class="tag tag-category">{{ categoryName }}</span>
          </div>

          <!-- 价格 -->
          <div class="price-block">
            <span class="price-label">航海价</span>
            <span class="price-value">¥ {{ product.price }}</span>
          </div>

          <!-- 描述 -->
          <div class="desc-block">
            <h3 class="block-title">宝物描述</h3>
            <p class="desc-text">{{ product.description || '这是来自伟大航路的珍贵宝物，等待有缘的航海者将它带走。' }}</p>
          </div>

          <!-- 详情信息 -->
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">库存</span>
              <span class="info-value">{{ product.stock }} 件</span>
            </div>
            <div class="info-item" v-if="product.releaseDate">
              <span class="info-label">上架时间</span>
              <span class="info-value">{{ product.releaseDate }}</span>
            </div>
          </div>

          <!-- 数量选择 -->
          <div class="quantity-row">
            <span class="qty-label">数量</span>
            <div class="qty-control">
              <button class="qty-btn" @click="quantity > 1 && quantity--">-</button>
              <span class="qty-value">{{ quantity }}</span>
              <button class="qty-btn" @click="quantity < product.stock && quantity++">+</button>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="action-row">
            <button class="btn-cart" @click="handleAddToCart" :disabled="product.stock <= 0">
              加入藏宝箱
            </button>
            <button class="btn-back" @click="$router.push('/shop')">
              继续寻宝
            </button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/utils/request.js'
import { addToCartAPI } from '@/api/cart.js'

const route = useRoute()
const router = useRouter()

const product = ref(null)
const loading = ref(true)
const error = ref(null)
const mounted = ref(false)
const quantity = ref(1)
const canvasRef = ref(null)
const categories = ref([])

const categoryName = computed(() => {
  if (!product.value || !categories.value.length) return ''
  const cat = categories.value.find(c => String(c.id) === String(product.value.type_id))
  return cat ? cat.name : ''
})

const formatImage = (url) => {
  if (!url) return 'https://via.placeholder.com/500x500?text=No+Image'
  return url.startsWith('/') ? `http://localhost:8080${url}` : url
}

// 获取商品详情
const fetchDetail = async () => {
  loading.value = true
  error.value = null
  try {
    const data = await request.get(`/shop/${route.params.id}`)
    product.value = data
  } catch (err) {
    error.value = '宝物信息加载失败，请稍后再试'
    console.error(err)
  } finally {
    loading.value = false
  }
}

// 获取分类（用于显示分类名）
const fetchCategories = async () => {
  try {
    const data = await request.get('/shop/categories')
    categories.value = data || []
  } catch (err) {
    console.error(err)
  }
}

// 加入购物车
const handleAddToCart = async () => {
  try {
    await addToCartAPI(product.value.id, quantity.value)
  } catch (err) {
    console.error('加入购物车失败:', err)
  }
}

// ==================== 粒子动画 ====================
let animId = null

const initParticles = () => {
  const canvas = canvasRef.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  let w = canvas.width = window.innerWidth
  let h = canvas.height = window.innerHeight

  const dots = Array.from({ length: 50 }, () => ({
    x: Math.random() * w, y: Math.random() * h,
    r: Math.random() * 1.5 + 0.5,
    dx: (Math.random() - 0.5) * 0.3,
    dy: (Math.random() - 0.5) * 0.3,
    a: Math.random() * 0.4 + 0.1
  }))

  const draw = () => {
    ctx.clearRect(0, 0, w, h)
    for (const d of dots) {
      d.x += d.dx; d.y += d.dy
      if (d.x < 0 || d.x > w) d.dx *= -1
      if (d.y < 0 || d.y > h) d.dy *= -1
      ctx.beginPath()
      ctx.arc(d.x, d.y, d.r, 0, Math.PI * 2)
      ctx.fillStyle = `rgba(255, 215, 0, ${d.a})`
      ctx.fill()
    }
    animId = requestAnimationFrame(draw)
  }
  draw()

  window.addEventListener('resize', () => {
    w = canvas.width = window.innerWidth
    h = canvas.height = window.innerHeight
  })
}

onMounted(async () => {
  await Promise.all([fetchDetail(), fetchCategories()])
  mounted.value = true
  setTimeout(initParticles, 100)
})

onUnmounted(() => {
  if (animId) cancelAnimationFrame(animId)
})
</script>

<style scoped>
* { margin: 0; padding: 0; box-sizing: border-box; }

.detail-page {
  font-family: 'Arial Rounded MT Bold', 'Arial', sans-serif;
  color: #fff;
  background: #030b1a;
  min-height: 100vh;
  position: relative;
}

.particle-canvas {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  z-index: 0;
  pointer-events: none;
}

/* ========== 导航 ========== */
.top-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 40px;
  background: rgba(3, 11, 26, 0.85);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 215, 0, 0.15);
  position: fixed;
  top: 0; left: 0; right: 0;
  z-index: 100;
}

.logo-text {
  font-size: 24px; font-weight: bold;
  color: #ffd700;
  text-shadow: 0 0 15px rgba(255, 215, 0, 0.5);
  cursor: pointer;
}

.nav-links { display: flex; gap: 20px; }

.nav-item {
  color: #8892b0; text-decoration: none;
  font-size: 15px; padding: 6px 14px;
  border-radius: 6px; transition: all 0.3s;
}

.nav-item:hover {
  color: #ffd700;
  background: rgba(255, 215, 0, 0.08);
}

/* ========== 主体 ========== */
.main-content {
  position: relative;
  z-index: 1;
  min-height: 100vh;
  padding: 90px 40px 60px;
}

/* ========== 加载/错误 ========== */
.loading-state, .error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  gap: 20px;
}

.loading-spinner {
  width: 48px; height: 48px;
  border: 3px solid rgba(255, 215, 0, 0.15);
  border-top-color: #ffd700;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

.loading-state p, .error-state p {
  color: #5e6e8a; font-size: 16px;
}

.error-icon { font-size: 64px; }

.back-btn {
  background: linear-gradient(135deg, #ff6b6b, #4ecdc4);
  color: #fff; border: none;
  padding: 10px 30px; border-radius: 25px;
  font-size: 15px; cursor: pointer;
  transition: transform 0.3s;
}

.back-btn:hover { transform: translateY(-2px); }

/* ========== 面包屑 ========== */
.breadcrumb {
  max-width: 1100px;
  margin: 0 auto 30px;
  font-size: 14px;
  color: #3a4a6a;
  opacity: 0; transform: translateY(20px);
  transition: all 0.8s ease;
}

.breadcrumb.fade-in { opacity: 1; transform: translateY(0); }

.breadcrumb a {
  color: #ffd700; cursor: pointer;
  text-decoration: none;
  transition: opacity 0.3s;
}

.breadcrumb a:hover { opacity: 0.7; }
.breadcrumb .sep { margin: 0 10px; }
.breadcrumb .current { color: #5e6e8a; }

/* ========== 详情容器 ========== */
.detail-container {
  display: flex;
  gap: 50px;
  max-width: 1100px;
  margin: 0 auto;
  opacity: 0; transform: translateY(30px);
  transition: all 1s cubic-bezier(0.16, 1, 0.3, 1);
}

.detail-container.fade-in { opacity: 1; transform: translateY(0); }

/* ========== 左侧图片 ========== */
.image-section {
  flex: 0 0 460px;
}

.image-frame {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  background: rgba(10, 20, 40, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(10px);
}

/* 四角装饰 */
.corner {
  position: absolute;
  width: 24px; height: 24px;
  z-index: 2;
}

.corner-tl { top: 8px; left: 8px; border-top: 2px solid #ffd700; border-left: 2px solid #ffd700; }
.corner-tr { top: 8px; right: 8px; border-top: 2px solid #ffd700; border-right: 2px solid #ffd700; }
.corner-bl { bottom: 8px; left: 8px; border-bottom: 2px solid #ffd700; border-left: 2px solid #ffd700; }
.corner-br { bottom: 8px; right: 8px; border-bottom: 2px solid #ffd700; border-right: 2px solid #ffd700; }

.product-img {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
  display: block;
  transition: transform 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}

.image-frame:hover .product-img {
  transform: scale(1.05);
}

.limited-badge {
  position: absolute;
  top: 16px; right: 16px;
  background: linear-gradient(135deg, #ff6b6b, #ff4500);
  color: #fff;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: bold;
  letter-spacing: 2px;
  box-shadow: 0 4px 15px rgba(255, 69, 0, 0.4);
  z-index: 3;
}

/* ========== 右侧信息 ========== */
.info-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-name {
  font-size: 32px;
  color: #e6f1ff;
  letter-spacing: 2px;
  margin-bottom: 16px;
  line-height: 1.3;
}

/* 标签 */
.tag-row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 24px;
}

.tag {
  font-size: 12px;
  padding: 4px 14px;
  border-radius: 20px;
  letter-spacing: 1px;
}

.tag-limited {
  background: rgba(255, 69, 0, 0.15);
  color: #ff6b6b;
  border: 1px solid rgba(255, 69, 0, 0.3);
}

.tag-stock {
  background: rgba(46, 204, 113, 0.12);
  color: #2ecc71;
  border: 1px solid rgba(46, 204, 113, 0.3);
}

.tag-out {
  background: rgba(255, 50, 50, 0.12);
  color: #e74c3c;
  border: 1px solid rgba(255, 50, 50, 0.3);
}

.tag-category {
  background: rgba(255, 215, 0, 0.1);
  color: #ffd700;
  border: 1px solid rgba(255, 215, 0, 0.2);
}

/* 价格 */
.price-block {
  background: rgba(255, 215, 0, 0.04);
  border: 1px solid rgba(255, 215, 0, 0.1);
  border-radius: 12px;
  padding: 20px 24px;
  margin-bottom: 28px;
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.price-label {
  color: #5e6e8a;
  font-size: 14px;
}

.price-value {
  font-size: 36px;
  font-weight: 900;
  color: #ff6b6b;
  text-shadow: 0 0 20px rgba(255, 107, 107, 0.2);
}

/* 描述 */
.desc-block { margin-bottom: 24px; }

.block-title {
  color: #ffd700;
  font-size: 15px;
  letter-spacing: 2px;
  margin-bottom: 10px;
  position: relative;
  padding-left: 14px;
}

.block-title::before {
  content: '';
  position: absolute;
  left: 0; top: 2px; bottom: 2px;
  width: 3px;
  background: #ffd700;
  border-radius: 2px;
}

.desc-text {
  color: #5e6e8a;
  font-size: 14px;
  line-height: 2;
}

/* 信息网格 */
.info-grid {
  display: flex;
  gap: 20px;
  margin-bottom: 28px;
}

.info-item {
  flex: 1;
  background: rgba(10, 20, 40, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 10px;
  padding: 14px 18px;
  backdrop-filter: blur(8px);
}

.info-label {
  display: block;
  color: #3a4a6a;
  font-size: 12px;
  margin-bottom: 6px;
  letter-spacing: 1px;
}

.info-value {
  color: #8892b0;
  font-size: 15px;
  font-weight: bold;
}

/* 数量选择 */
.quantity-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
}

.qty-label {
  color: #5e6e8a;
  font-size: 14px;
}

.qty-control {
  display: flex;
  align-items: center;
  gap: 2px;
  background: rgba(10, 20, 40, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 10px;
  overflow: hidden;
}

.qty-btn {
  width: 38px; height: 38px;
  background: transparent;
  border: none;
  color: #ffd700;
  font-size: 18px;
  cursor: pointer;
  transition: background 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.qty-btn:hover { background: rgba(255, 215, 0, 0.1); }

.qty-value {
  min-width: 40px;
  text-align: center;
  font-size: 16px;
  color: #e6f1ff;
}

/* 操作按钮 */
.action-row {
  display: flex;
  gap: 16px;
}

.btn-cart {
  flex: 1;
  padding: 14px 0;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  letter-spacing: 2px;
  transition: all 0.4s;
  background: linear-gradient(135deg, #ffd700, #ff8c00);
  color: #030b1a;
  box-shadow: 0 4px 20px rgba(255, 215, 0, 0.25);
}

.btn-cart:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 8px 30px rgba(255, 215, 0, 0.35);
}

.btn-cart:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.btn-back {
  flex: 0 0 140px;
  padding: 14px 0;
  border: 1px solid rgba(255, 215, 0, 0.2);
  border-radius: 12px;
  background: transparent;
  color: #ffd700;
  font-size: 15px;
  cursor: pointer;
  letter-spacing: 2px;
  transition: all 0.3s;
}

.btn-back:hover {
  background: rgba(255, 215, 0, 0.06);
  transform: translateY(-2px);
}

/* ========== 响应式 ========== */
@media (max-width: 900px) {
  .detail-container {
    flex-direction: column;
    gap: 30px;
  }

  .image-section { flex: none; }

  .main-content { padding: 80px 20px 40px; }

  .product-name { font-size: 24px; }
  .price-value { font-size: 28px; }

  .info-grid { flex-direction: column; gap: 10px; }

  .action-row { flex-direction: column; }
  .btn-back { flex: none; }
}
</style>
