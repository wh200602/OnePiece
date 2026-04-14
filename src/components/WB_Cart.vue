<template>
  <div class="one-piece-cart">
    <!-- 顶部导航栏 -->
    <header class="top-header">
      <div class="logo-text">ONE PIECE</div>
      <nav class="nav-links">
        <a href="#" class="nav-item" @click.prevent="goShop">万博会</a>
        <a href="#" class="nav-item active">藏宝箱</a>
      </nav>
    </header>

    <main class="main-content">
      <!-- 背景气泡 -->
      <div class="bubbles">
        <span v-for="n in 15" :key="n" class="bubble"></span>
      </div>

      <div class="cart-container">
        <h1 class="cart-title">我的藏宝箱</h1>

        <!-- 加载中 -->
        <div v-if="loading" class="status-msg">正在打捞宝藏...</div>

        <!-- 空购物车 -->
        <div v-else-if="cartItems.length === 0" class="empty-cart">
          <p>藏宝箱空空如也，快去万博会淘宝吧！</p>
          <button class="back-btn" @click="goShop">前往万博会</button>
        </div>

        <!-- 购物车列表 -->
        <template v-else>
          <div class="cart-list">
            <div v-for="item in cartItems" :key="item.id" class="cart-item">
              <div class="item-image">
                <img :src="formatImage(item.imageUrl)" :alt="item.productName" />
              </div>
              <div class="item-info">
                <h3 class="item-name">{{ item.productName }}</h3>
                <p class="item-desc">{{ item.description }}</p>
              </div>
              <div class="item-price">¥ {{ item.price }}</div>
              <div class="item-quantity">
                <button class="qty-btn" @click="changeNum(item, -1)">-</button>
                <span class="qty-value">{{ item.num }}</span>
                <button class="qty-btn" @click="changeNum(item, 1)">+</button>
              </div>
              <div class="item-total">¥ {{ (item.price * item.num).toFixed(2) }}</div>
              <button class="delete-btn" @click="removeItem(item.id)">删除</button>
            </div>
          </div>

          <!-- 底部结算栏 -->
          <div class="cart-footer">
            <div class="total-info">
              <span class="total-count">共 {{ totalCount }} 件宝物</span>
              <span class="total-price">合计：<em>¥ {{ totalPrice }}</em></span>
            </div>
            <button class="checkout-btn">去结算</button>
          </div>
        </template>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCartListAPI, updateCartNumAPI, removeCartItemAPI } from '@/api/cart.js'

const router = useRouter()
const cartItems = ref([])
const loading = ref(false)

const totalCount = computed(() => cartItems.value.reduce((sum, item) => sum + item.num, 0))
const totalPrice = computed(() =>
  cartItems.value.reduce((sum, item) => sum + item.price * item.num, 0).toFixed(2)
)

const formatImage = (url) => {
  if (!url) return 'https://via.placeholder.com/120x120?text=No+Image'
  return url.startsWith('/') ? `http://localhost${url}` : url
}

const goShop = () => router.push('/shop')

// 加载购物车
const fetchCart = async () => {
  loading.value = true
  try {
    const data = await getCartListAPI()
    cartItems.value = data || []
  } catch (err) {
    console.error('加载购物车失败:', err)
  } finally {
    loading.value = false
  }
}

// 修改数量
const changeNum = async (item, delta) => {
  const newNum = item.num + delta
  if (newNum <= 0) {
    await removeItem(item.id)
    return
  }
  try {
    await updateCartNumAPI(item.id, newNum)
    item.num = newNum
  } catch (err) {
    console.error('更新数量失败:', err)
  }
}

// 删除
const removeItem = async (id) => {
  try {
    await removeCartItemAPI(id)
    cartItems.value = cartItems.value.filter(item => item.id !== id)
  } catch (err) {
    console.error('删除失败:', err)
  }
}

onMounted(() => fetchCart())
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.one-piece-cart {
  font-family: 'Arial Rounded MT Bold', 'Arial', sans-serif;
  color: #fff;
  background-color: #001220;
  min-height: 100vh;
  position: relative;
}

/* ========== 顶部导航（与商城统一） ========== */
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

/* ========== 主体 ========== */
.main-content {
  position: relative;
  min-height: 100vh;
  padding-top: 80px;
}

/* ========== 气泡 ========== */
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
  animation: float 10s infinite linear;
}

.bubble:nth-child(1) { left: 5%;  top: 15%; animation-duration: 9s; }
.bubble:nth-child(2) { left: 20%; top: 45%; animation-duration: 13s; }
.bubble:nth-child(3) { left: 40%; top: 10%; animation-duration: 11s; }
.bubble:nth-child(4) { left: 60%; top: 65%; animation-duration: 8s; }
.bubble:nth-child(5) { left: 85%; top: 25%; animation-duration: 14s; }

@keyframes float {
  0% { transform: translateY(0) scale(1); opacity: 1; }
  100% { transform: translateY(-100vh) scale(1.5); opacity: 0; }
}

/* ========== 购物车容器 ========== */
.cart-container {
  position: relative;
  z-index: 5;
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.cart-title {
  font-size: 32px;
  color: #ffd700;
  text-align: center;
  margin-bottom: 30px;
  text-shadow: 0 0 10px #ffd700, 0 0 20px #ff4500;
}

/* ========== 状态 ========== */
.status-msg {
  text-align: center;
  color: #a0a0a0;
  font-size: 18px;
  margin-top: 60px;
}

.empty-cart {
  text-align: center;
  margin-top: 80px;
}

.empty-cart p {
  color: #a0a0a0;
  font-size: 18px;
  margin-bottom: 20px;
}

.back-btn {
  background: linear-gradient(to right, #ff6b6b, #4ecdc4);
  color: #fff;
  border: none;
  padding: 10px 30px;
  border-radius: 25px;
  font-size: 16px;
  cursor: pointer;
  font-weight: bold;
  transition: transform 0.3s;
}

.back-btn:hover {
  transform: translateY(-2px);
}

/* ========== 购物车列表 ========== */
.cart-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 20px;
  background: rgba(16, 30, 45, 0.8);
  border-radius: 15px;
  padding: 15px 20px;
  border: 1px solid #2c3e50;
  transition: border-color 0.3s;
  backdrop-filter: blur(5px);
}

.cart-item:hover {
  border-color: #ffd700;
}

/* 商品图片 */
.item-image {
  flex-shrink: 0;
  width: 90px;
  height: 90px;
  border-radius: 10px;
  overflow: hidden;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  background: #222;
}

/* 商品信息 */
.item-info {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-size: 16px;
  color: #ffd700;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-desc {
  font-size: 13px;
  color: #888;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 单价 */
.item-price {
  color: #a0a0a0;
  font-size: 15px;
  width: 80px;
  text-align: center;
  flex-shrink: 0;
}

/* 数量控制 */
.item-quantity {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.qty-btn {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: 1px solid #ffd700;
  background: transparent;
  color: #ffd700;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.3s;
}

.qty-btn:hover {
  background: rgba(255, 215, 0, 0.2);
}

.qty-value {
  font-size: 16px;
  min-width: 24px;
  text-align: center;
  color: #fff;
}

/* 小计 */
.item-total {
  color: #ff6b6b;
  font-size: 16px;
  font-weight: bold;
  width: 90px;
  text-align: right;
  flex-shrink: 0;
}

/* 删除按钮 */
.delete-btn {
  background: transparent;
  border: 1px solid #ff4500;
  color: #ff4500;
  padding: 5px 12px;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.3s;
  flex-shrink: 0;
}

.delete-btn:hover {
  background: rgba(255, 69, 0, 0.2);
}

/* ========== 底部结算 ========== */
.cart-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 30px;
  margin-top: 30px;
  padding: 20px 25px;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 15px;
  border: 1px solid #ffd700;
}

.total-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.total-count {
  color: #a0a0a0;
  font-size: 15px;
}

.total-price {
  color: #fff;
  font-size: 18px;
}

.total-price em {
  color: #ff6b6b;
  font-style: normal;
  font-size: 24px;
  font-weight: bold;
}

.checkout-btn {
  background: linear-gradient(to right, #ff6b6b, #ff4500);
  color: #fff;
  border: none;
  padding: 12px 40px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: transform 0.3s, opacity 0.3s;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.4);
}

.checkout-btn:hover {
  transform: translateY(-2px);
  opacity: 0.9;
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .cart-item {
    flex-wrap: wrap;
    gap: 10px;
  }

  .item-info {
    width: calc(100% - 110px);
  }

  .item-price,
  .item-total {
    width: auto;
  }

  .cart-footer {
    flex-direction: column;
    gap: 15px;
  }
}
</style>
