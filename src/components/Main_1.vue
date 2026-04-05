<template>
  <div class="one-piece-homepage">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="logo">ONE PIECE</div>
      <div class="nav-links">
        <a href="#forces" @click.prevent="scrollToSection('forces')">势力划分</a>
        <a href="#captain" @click.prevent="scrollToSection('captain')">船长室</a>
        <a href="#crew" @click.prevent="scrollToSection('crew')">海贼聚会</a>
        <a @click="goToShop">万博会</a>
      </div>
    </nav>

    <!-- 主屏 -->
    <section class="hero" ref="heroRef">
      <h1>ONE PIECE</h1>
      <p>寻找传说中的宝藏 ONE PIECE，成为海贼王！踏上充满冒险与梦想的伟大航路。</p>
      <button class="cta-button" @click="startAdventure">启航之旅</button>
    </section>

    <!-- 势力划分 -->
    <section id="forces" class="content-section">
      <h2 class="section-title">势力划分</h2>
      <div class="cards-container">
        <div v-for="(item, index) in forcesData" :key="index"
             class="card"
             :class="{ 'hovered': hoveredIndex === index && currentSection === 'forces' }"
             @mouseenter="hoverCard(index, 'forces')"
             @mouseleave="unHoverCard">
          <div class="card-icon">{{ item.icon }}</div>
          <h3>{{ item.title }}</h3>
          <p>{{ item.description }}</p>
        </div>
      </div>
    </section>

    <!-- 船长室 -->
    <section id="captain" class="content-section">
      <h2 class="section-title">船长室</h2>
      <div class="cards-container">
        <div v-for="(item, index) in captainsData" :key="index"
             class="card"
             :class="{ 'hovered': hoveredIndex === index && currentSection === 'captain' }"
             @mouseenter="hoverCard(index, 'captain')"
             @mouseleave="unHoverCard">
          <div class="card-icon">{{ item.icon }}</div>
          <h3>{{ item.title }}</h3>
          <p>{{ item.description }}</p>
        </div>
      </div>
    </section>

    <!-- 海贼团 -->
    <section id="crew" class="content-section">
      <h2 class="section-title">海贼聚会</h2>
      <div class="cards-container">
        <div v-for="(item, index) in crewsData" :key="index"
             class="card"
             :class="{ 'hovered': hoveredIndex === index && currentSection === 'crew' }"
             @mouseenter="hoverCard(index, 'crew')"
             @mouseleave="unHoverCard">
          <div class="card-icon">{{ item.icon }}</div>
          <h3>{{ item.title }}</h3>
          <p>{{ item.description }}</p>
        </div>
      </div>
    </section>

    <!-- 万博会（WB_Shop 组件）-->
    <section id="wbshop" class="content-section">
      <h2 class="section-title">万博会</h2>
      <WB_Shop />
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import WB_Shop from './WB_Shop.vue'  // 引入组件
import { useRouter } from 'vue-router'  // 加这行

const router = useRouter() // 加这行

// 跳转到万博会
const goToShop = () => {
  router.push('/shop')
}

// 数据定义
const forcesData = [
  { icon: '⚔️', title: '海军', description: '世界政府直属的正义组织，以绝对正义为信条，维护海洋秩序。' },
  { icon: '🏴‍☠️', title: '四皇', description: '称霸新世界的四位最强海贼，拥有巨大的影响力和实力。' },
  { icon: '⚖️', title: '七武海', description: '曾被世界政府承认的合法海贼，后制度被废除。' }
]

const captainsData = [
  { icon: '👑', title: '蒙奇·D·路飞', description: '草帽海贼团船长，橡胶果实能力者，梦想是成为海贼王。' },
  { icon: '⚔️', title: '红发香克斯', description: '四皇之一，路飞的启蒙恩人，实力深不可测。' },
  { icon: '🦜', title: '马歇尔·D·蒂奇', description: '黑胡子海贼团船长，拥有黑暗果实和震震果实双重能力。' }
]

const crewsData = [
  { icon: '⛵', title: '草帽海贼团', description: '由路飞率领，成员各怀绝技，追寻自由与梦想的传奇团队。' },
  { icon: '⚓', title: '红发海贼团', description: '四皇香克斯的团队，实力强大，成员神秘莫测。' },
  { icon: '💀', title: '百兽海贼团', description: '四皇凯多的团队，以动物系恶魔果实能力者为主力。' }
]

// Refs
const heroRef = ref(null)
const hoveredIndex = ref(-1)
const currentSection = ref('')

// 方法
const scrollToSection = (id) => {
  const element = document.getElementById(id)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
  }
}

const startAdventure = () => {
  alert('启航！向着梦想的伟大航路前进！')
}

const hoverCard = (index, section) => {
  hoveredIndex.value = index
  currentSection.value = section
}

const unHoverCard = () => {
  hoveredIndex.value = -1
}
</script>

<style scoped>
.one-piece-homepage {
  font-family: 'Arial', sans-serif;
  color: #e6f1ff;
  overflow-x: hidden;
  background-image: url('https://wanx.alicdn.com/wanx/1774153909522409517/text_to_image_lite_v2/c77ac2483fbf4a08ad8e8031854ef9f5_0_visibleWatermark.png');
  background-size: cover;
  background-attachment: fixed;
  background-position: center;
}

/* 导航栏 */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 1000;
  background: rgba(10, 25, 47, 0.9);
  backdrop-filter: blur(10px);
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid rgba(100, 149, 237, 0.3);
}

.logo {
  font-size: 1.8rem;
  font-weight: bold;
  color: #ffcc00;
  text-shadow: 0 0 10px rgba(255, 204, 0, 0.7);
}

.nav-links {
  display: flex;
  gap: 2rem;
}

.nav-links a {
  text-decoration: none;
  color: #ccd6f6;
  font-size: 1.1rem;
  transition: all 0.3s ease;
  padding: 0.5rem 1rem;
  border-radius: 4px;
}

.nav-links a:hover {
  color: #ffcc00;
  background: rgba(100, 149, 237, 0.2);
  transform: translateY(-2px);
}

/* 主屏 */
.hero {
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  padding: 0 2rem;
  position: relative;
}

.hero::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  z-index: -1;
}

.hero h1 {
  font-size: 4rem;
  margin-bottom: 1rem;
  color: #ffcc00;
  text-shadow: 0 0 20px rgba(255, 204, 0, 0.7);
}

.hero p {
  font-size: 1.5rem;
  max-width: 800px;
  line-height: 1.6;
  margin-bottom: 2rem;
}

.cta-button {
  background: linear-gradient(45deg, #ffcc00, #ff6b00);
  color: #0a192f;
  border: none;
  padding: 1rem 2rem;
  font-size: 1.2rem;
  font-weight: bold;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 0 20px rgba(255, 204, 0, 0.5);
}

.cta-button:hover {
  transform: scale(1.05);
  box-shadow: 0 0 30px rgba(255, 204, 0, 0.8);
}

/* 内容区域 */
.content-section {
  padding: 6rem 2rem;
  min-height: 100vh;
  background: rgba(10, 25, 47, 0.85);
}

.section-title {
  text-align: center;
  font-size: 2.5rem;
  margin-bottom: 3rem;
  color: #ffcc00;
  text-shadow: 0 0 10px rgba(255, 204, 0, 0.5);
}

.cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.card {
  background: rgba(18, 38, 63, 0.7);
  border-radius: 15px;
  padding: 2rem;
  text-align: center;
  transition: all 0.3s ease;
  border: 1px solid rgba(100, 149, 237, 0.3);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  position: relative;
  overflow: hidden;
}

.card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, transparent, #ffcc00, transparent);
  transform: scaleX(0);
  transition: transform 0.3s ease;
  z-index: 1;
}

.card.hovered::before {
  transform: scaleX(1);
}

.card.hovered {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4), 0 0 20px rgba(100, 149, 237, 0.3);
  background: rgba(25, 55, 95, 0.8);
}

.card-icon {
  font-size: 3rem;
  margin-bottom: 1.5rem;
  color: #ffcc00;
  transition: transform 0.3s ease;
}

.card.hovered .card-icon {
  transform: scale(1.2) rotate(10deg);
  color: #64ffda;
}

.card h3 {
  font-size: 1.8rem;
  margin-bottom: 1rem;
  color: #64ffda;
  transition: all 0.3s ease;
}

.card.hovered h3 {
  color: #ffcc00;
  text-shadow: 0 0 10px rgba(255, 204, 0, 0.5);
}

.card p {
  color: #8892b0;
  line-height: 1.6;
  transition: all 0.3s ease;
}

.card.hovered p {
  color: #ccd6f6;
}

/* 响应式 */
@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    gap: 1rem;
    padding: 1rem;
  }

  .nav-links {
    gap: 1rem;
  }

  .hero h1 {
    font-size: 2.5rem;
  }

  .hero p {
    font-size: 1.2rem;
  }
}
</style>
