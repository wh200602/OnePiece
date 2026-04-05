<template>
  <div class="one-piece-expo">
    <!-- 顶部导航栏 -->
    <header class="top-header">
      <div class="logo-text">ONE PIECE</div>
      <nav class="nav-links">
        <a href="#" class="nav-item">势力划分</a>
        <a href="#" class="nav-item">船长室</a>
        <a href="#" class="nav-item">海贼聚会</a>
        <a href="#" class="nav-item active">万博会</a>
      </nav>
    </header>

    <!-- 主体内容：深海背景 -->
    <main class="main-content">
      <!-- 背景气泡动画 -->
      <div class="bubbles">
        <span v-for="n in 20" :key="n" class="bubble"></span>
      </div>

      <!-- 装饰元素：旗帜、罗盘、宝箱 -->
      <div class="decoration left-flag">
        <img src="https://cdn-icons-png.flaticon.com/512/5968/5968831.png" alt="海贼旗" class="deco-img" />
      </div>
      <div class="decoration right-compass">
        <img src="https://cdn-icons-png.flaticon.com/512/3130/3130727.png" alt="罗盘" class="deco-img" />
      </div>
      <div class="decoration bottom-chest">
        <img src="https://cdn-icons-png.flaticon.com/512/2668/2668396.png" alt="宝箱" class="deco-img" />
      </div>

      <!-- 中央核心区域 -->
      <div class="center-area">
        <!-- Logo 区域 -->
        <div class="logo-section">
          <div class="skull-logo">
            <img src="https://cdn-icons-png.flaticon.com/512/5968/5968831.png" alt="草帽骷髅" />
          </div>
          <h1 class="main-title">ONE PIECE</h1>
          <p class="subtitle">寻找传说中的宝藏 ONE PIECE，成为海贼王！踏上充满冒险与梦想的伟大航路。</p>
          <button class="explore-btn">启航之旅</button>
        </div>

        <!-- 商品分类导航 -->
        <div class="category-nav">
          <!-- 加载状态提示 -->
          <div v-if="loadingCategories" class="loading-message">
            正在加载分类...
          </div>

          <!-- 错误状态提示 -->
          <div v-if="errorCategories" class="error-message">
            分类加载失败: {{ errorCategories }}
          </div>

          <!-- 分类列表 -->
          <!-- 添加了 "全部" 分类项 -->
          <div
            class="category-item"
            :class="{ active: activeCategory === 0 }"
            @click="changeCategory(0)"
          >
            <span class="category-icon">🌊</span>
            <span class="category-name">全部</span>
          </div>
          <div
            v-for="category in categories"
            :key="category.id"
            class="category-item"
            :class="{ active: activeCategory === category.id }"
            @click="changeCategory(category.id)"
          >
            <span class="category-icon">{{ category.icon }}</span>
            <span class="category-name">{{ category.name }}</span>
          </div>
        </div>

        <!-- 商品展示区（瀑布流） -->
        <div class="product-grid">
          <!-- 加载状态提示 -->
          <div v-if="loadingProducts" class="loading-message">
            正在加载商品...
          </div>

          <!-- 错误状态提示 -->
          <div v-if="errorProducts" class="error-message">
            商品加载失败: {{ errorProducts }}
          </div>

          <!-- 商品列表 -->
          <div
            v-for="product in filteredProducts"
            :key="product.id"
            class="product-card"
          >
            <div class="product-image">
              <img :src="product.image" :alt="product.name" />
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <p class="product-desc">{{ product.description }}</p>
              <div class="product-price">¥ {{ product.price }}</div>
              <button class="add-to-cart">加入购物车</button>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import axios from 'axios';
import { onMounted } from 'vue';

// 定义后端API的基础URL（请根据您的实际部署情况修改）
const BASE_API_URL = 'http://localhost:8080'; // 或者 'http://localhost:8080/api'

// 响应式数据
const categories = ref([]); // 存储从后端获取的分类数据
const products = ref([]); // 存储从后端获取的商品数据
const activeCategory = ref(0); // 当前选中的分类ID，默认为0（全部）

// 加载状态
const loadingCategories = ref(false);
const loadingProducts = ref(false);

// 错误信息
const errorCategories = ref(null);
const errorProducts = ref(null);

// 根据当前选中的分类过滤商品
const filteredProducts = computed(() => {
  if (activeCategory.value === 0) return products.value; // 0代表全部
  return products.value.filter(product => product.categoryId === activeCategory.value); // 假设后端返回的商品数据中，关联分类的字段是 categoryId
});

// 切换分类
const changeCategory = (id) => {
  activeCategory.value = id;
};

// 从后端获取分类数据
const fetchCategories = async () => {
  loadingCategories.value = true;
  errorCategories.value = null;
  try {
    // 1. 获取token
    const token = localStorage.getItem("token");

    // 2. 带token请求
    const response = await axios.get(`${BASE_API_URL}/shop/categories`, {
      headers: {
        Authorization: "Bearer " + token
      }
    });
    // 假设后端返回的数据结构是 { code: 0, data: [...], message: "" }
    categories.value = response.data.data || response.data;
  } catch (err) {
    errorCategories.value = err.response?.data?.message || '网络错误，请稍后重试';
    console.error("Error fetching categories:", err);
  } finally {
    loadingCategories.value = false;
  }
};

// 从后端获取商品数据
const fetchProducts = async () => {
  loadingProducts.value = true;
  errorProducts.value = null;
  try {
    const token = localStorage.getItem("token");
    const response = await axios.get(`${BASE_API_URL}/shop`, {
      headers: { Authorization: "Bearer " + token }
    });

    console.log("Raw Products Response:", response.data);

    const pageInfo = response.data.data;
    const rawData = pageInfo.records;

    console.log("Raw Data Array (Records):", rawData);

    // --- 修正映射逻辑，严格按照 Shop 实体类的字段名 ---
    products.value = rawData.map(item => ({
      id: item.id,
      // 👇 修正字段名：后端是 productName
      name: item.productName || "未知商品",
      description: item.description,
      // 👇 修正字段名：后端是 price
      price: item.price || 0,
      // 👇 修正字段名：后端是 type_id
      categoryId: item.type_id || item.categoryId || item.category || 0, // 仍然尝试其他可能的字段名作为备选
      // 👇 修正字段名：后端是 imageUrl
      image: item.imageUrl?.startsWith('/')
             ? `${BASE_API_URL}${item.imageUrl}`
             : item.imageUrl || 'https://via.placeholder.com/200x200?text=No+Image',
      // 👇 映射库存字段：后端是 stock
      stock: item.stock || 0
    }));

    console.log("Mapped Products:", products.value);

  } catch (err) {
    errorProducts.value = err.response?.data?.message || '网络错误，请稍后重试';
    console.error("Error fetching products:", err);
  } finally {
    loadingProducts.value = false;
  }
};

// 组件挂载时调用两个接口
// 使用 Promise.all 并行请求，提高效率
onMounted(async () => {
  await Promise.all([fetchCategories(), fetchProducts()]);
});
</script>

<style scoped>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.one-piece-expo {
  font-family: 'Arial Rounded MT Bold', 'Arial', sans-serif;
  color: #fff;
  background-color: #001220; /* 深海蓝背景 */
  /* height: 100vh; */ /* 确认已删除 */
  overflow-y: auto; /* 确认已修改 */
  position: relative;
  /* 不再需要 padding-top，因为 main-content 已经处理了 */
}

/* 顶部导航栏样式 */
/* 顶部导航栏样式 */
.top-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 40px;
  background: rgba(0, 0, 0, 0.5);
  border-bottom: 2px solid #ffd700;
  position: fixed; /* 👈 改为 fixed */
  top: 0;
  left: 0;
  right: 0;
  z-index: 20; /* 提高层级 */
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

/* 主体内容样式 */
/* 主体内容样式 */
.main-content {
  position: relative;
  min-height: 100vh;

  padding-top: 60px; /* 👈 header 总高度 (15px * 2 + 30px content area) -> 60px 是估算，可能需要微调 */
  /* 如果 header 高度变化，这里也需要相应调整 */
}

/* 背景气泡动画 */
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

.bubble:nth-child(1) { left: 10%; top: 20%; animation-duration: 8s; }
.bubble:nth-child(2) { left: 30%; top: 50%; animation-duration: 12s; }
.bubble:nth-child(3) { left: 50%; top: 10%; animation-duration: 15s; }
.bubble:nth-child(4) { left: 70%; top: 70%; animation-duration: 9s; }
.bubble:nth-child(5) { left: 90%; top: 30%; animation-duration: 11s; }

@keyframes float {
  0% { transform: translateY(0) scale(1); opacity: 1; }
  100% { transform: translateY(-100vh) scale(1.5); opacity: 0; }
}

/* 装饰元素样式 */
.decoration {
  position: absolute;
  z-index: 2;
}

.left-flag { top: 100px; left: 20px; }
.right-compass { top: 120px; right: 20px; transform: rotate(15deg); }
.bottom-chest { bottom: 50px; right: 50px; }

.deco-img {
  width: 80px;
  height: 80px;
  filter: drop-shadow(0 0 10px rgba(255, 255, 0, 0.5));
}

/* 中央核心区域样式 */
.center-area {
  position: relative;
  z-index: 5;
  padding: 40px 20px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* Logo 区域样式 */
.logo-section {
  margin-bottom: 30px;
  text-align: center;
}

.skull-logo img {
  width: 120px;
  height: 120px;
  animation: pulse 2s infinite;
}

.main-title {
  font-size: 48px;
  font-weight: bold;
  color: #ffd700;
  text-shadow: 0 0 10px #ffd700, 0 0 20px #ff4500;
  margin: 10px 0 5px;
  letter-spacing: 5px;
}

.subtitle {
  color: #a0a0a0;
  font-size: 14px;
  margin-bottom: 20px;
  max-width: 600px;
  line-height: 1.6;
}

.explore-btn {
  background: linear-gradient(to right, #ff6b6b, #4ecdc4);
  color: #fff;
  border: none;
  padding: 10px 30px;
  border-radius: 25px;
  font-size: 16px;
  cursor: pointer;
  transition: transform 0.3s, opacity 0.3s;
  font-weight: bold;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.4);
}

.explore-btn:hover {
  transform: translateY(-2px);
  opacity: 0.9;
}

/* 商品分类导航样式 */
.category-nav {
  display: flex;
  gap: 20px;
  margin-bottom: 40px;
  background: rgba(0, 0, 0, 0.5);
  padding: 10px 20px;
  border-radius: 50px;
  border: 1px solid #ffd700;
  box-shadow: 0 0 15px rgba(255, 215, 0, 0.3);
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  padding: 10px;
  border-radius: 10px;
  transition: transform 0.3s, color 0.3s;
  color: #a0a0a0;
}

.category-item:hover {
  color: #ffd700;
  transform: translateY(-2px);
}

.category-item.active {
  color: #ffd700;
  font-weight: bold;
  text-shadow: 0 0 5px #ffd700;
}

.category-icon {
  font-size: 24px;
  margin-bottom: 5px;
}

.category-name {
  font-size: 14px;
}

/* 商品展示区样式 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.product-card {
  background: rgba(16, 30, 45, 0.8);
  border-radius: 15px;
  overflow: hidden;
  border: 1px solid #2c3e50;
  transition: transform 0.3s, box-shadow 0.3s;
  backdrop-filter: blur(5px);
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.3);
  border-color: #ffd700;
}

.product-image img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  background-color: #222;
}

.product-info {
  padding: 15px;
}

.product-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
  color: #ffd700;
}

.product-desc {
  color: #888;
  font-size: 14px;
  margin-bottom: 10px;
  line-height: 1.5;
}

.product-price {
  font-size: 20px;
  font-weight: bold;
  color: #ff6b6b;
  margin-bottom: 15px;
  display: block;
}

.add-to-cart {
  width: 100%;
  background: #ff6b6b;
  color: #fff;
  border: none;
  padding: 10px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.3s;
}

.add-to-cart:hover {
  background: #ff4500;
}

/* 新增的加载和错误提示样式 */
.loading-message, .error-message {
  color: #a0a0a0;
  font-size: 18px;
  margin: 20px 0;
  text-align: center;
}

.error-message {
  color: #ff6b6b;
}

/* 动画效果 */
@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); }
  100% { transform: scale(1); }
}

@keyframes float {
  0% { transform: translateY(0) rotate(0deg); }
  100% { transform: translateY(-20px) rotate(5deg); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-grid {
    grid-template-columns: 1fr;
  }

  .category-nav {
    flex-wrap: wrap;
    justify-content: center;
  }

  .main-title { font-size: 36px; }
  .logo-section img { width: 100px; height: 100px; }
}
</style>
