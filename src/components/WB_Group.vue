<template>
  <div class="group-page">
    <!-- 粒子背景 -->
    <canvas ref="canvasRef" class="particle-canvas"></canvas>

    <!-- 导航栏 -->
    <header class="top-header">
      <div class="logo-text" @click="$router.push('/home')">ONE PIECE</div>
      <nav class="nav-links">
        <a href="#" class="nav-item active">势力划分</a>
        <a href="#" class="nav-item" @click.prevent="$router.push('/shop')">万博会</a>
        <a href="#" class="nav-item" @click.prevent="$router.push('/cart')">藏宝箱</a>
      </nav>
    </header>

    <!-- 主视觉区 -->
    <section class="hero-banner">
      <div class="hero-content" :class="{ 'fade-in': mounted }">
        <h1 class="glitch-title" data-text="势力划分">势力划分</h1>
        <p class="hero-sub">大海贼时代 — 三大势力撕裂世界的均衡</p>
        <div class="scroll-hint">
          <span>向下探索</span>
          <div class="scroll-arrow"></div>
        </div>
      </div>
    </section>

    <!-- 势力版块 -->
    <section
      v-for="(group, gIdx) in groups"
      :key="group.name"
      class="force-section"
      :class="[`section-${gIdx}`, { 'visible': visibleSections[gIdx] }]"
      :ref="el => sectionRefs[gIdx] = el"
    >
      <!-- 区块标题 -->
      <div class="section-header">
        <span class="section-icon">{{ group.icon }}</span>
        <h2 class="section-title">{{ group.name }}</h2>
        <p class="section-desc">{{ group.subtitle }}</p>
        <div class="title-line"></div>
      </div>

      <!-- 卡片网格 -->
      <div class="cards-grid">
        <div
          v-for="(member, mIdx) in group.members"
          :key="member.name"
          class="member-card"
          :class="{ 'card-visible': visibleSections[gIdx] }"
          :style="{ animationDelay: `${mIdx * 0.12}s` }"
          @mouseenter="activeCard = `${gIdx}-${mIdx}`"
          @mouseleave="activeCard = null"
        >
          <!-- 能量条背景 -->
          <div class="card-energy" :style="{ background: member.color }"></div>

          <!-- 卡片内容 -->
          <div class="card-body">
            <div class="card-avatar">
              <span class="avatar-icon">{{ member.icon }}</span>
              <div class="avatar-ring" :style="{ borderColor: member.color }"></div>
            </div>
            <h3 class="card-name">{{ member.name }}</h3>
            <span class="card-title" :style="{ color: member.color }">{{ member.title }}</span>
            <p class="card-desc">{{ member.desc }}</p>

            <!-- 能力标签 -->
            <div class="card-tags">
              <span
                v-for="tag in member.tags"
                :key="tag"
                class="tag"
                :style="{ borderColor: member.color, color: member.color }"
              >{{ tag }}</span>
            </div>

            <!-- 战力条 -->
            <div class="power-bar">
              <div class="power-label">
                <span>战力</span>
                <span>{{ member.power }}%</span>
              </div>
              <div class="power-track">
                <div
                  class="power-fill"
                  :style="{
                    width: activeCard === `${gIdx}-${mIdx}` ? member.power + '%' : '0%',
                    background: `linear-gradient(90deg, ${member.color}, ${member.colorEnd})`
                  }"
                ></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 底部世界观 -->
    <section class="world-footer" :class="{ 'visible': footerVisible }">
      <div class="footer-content">
        <h2>世界的均衡</h2>
        <p>海军本部、四皇、王下七武海 — 三大势力相互牵制，维系着这个世界脆弱的平衡。<br>
        然而，随着顶上战争的爆发、七武海制度的废除、以及新世代海贼的崛起，<br>
        旧秩序正在崩塌，新时代的浪潮势不可挡。</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const mounted = ref(false)
const canvasRef = ref(null)
const activeCard = ref(null)
const footerVisible = ref(false)
const sectionRefs = ref([])
const visibleSections = ref([])

// ==================== 数据 ====================
const groups = [
  {
    name: '四皇',
    icon: '👑',
    subtitle: '君临新世界的四位绝对霸主，拥有撼动世界的力量',
    members: [
      {
        name: '红发香克斯', title: '四皇 · 红发海贼团船长', icon: '⚔️',
        desc: '以强大的霸王色霸气闻名天下，路飞的引路人。没有恶魔果实能力，纯粹以剑术与霸气立于顶点。',
        tags: ['霸王色霸气', '剑术', '和平主义'], power: 96,
        color: '#ff3333', colorEnd: '#ff6b6b'
      },
      {
        name: '百兽凯多', title: '四皇 · 百兽海贼团总督', icon: '🐉',
        desc: '被称为"世界最强生物"，鱼鱼果实·青龙形态能力者。坚不可摧的身躯，渴望在战争中迎来壮烈之死。',
        tags: ['幻兽种', '霸王色缠绕', '不死身'], power: 98,
        color: '#9b59b6', colorEnd: '#c39bd3'
      },
      {
        name: 'BIG MOM', title: '四皇 · BIG MOM海贼团船长', icon: '🎂',
        desc: '魂魂果实能力者，可操纵灵魂创造"霍米兹"。梦想建立所有种族平等共存的国度，手段却极其残忍。',
        tags: ['魂魂果实', '钢铁气球', '三将星'], power: 95,
        color: '#e91e8c', colorEnd: '#ff69b4'
      },
      {
        name: '黑胡子蒂奇', title: '四皇 · 黑胡子海贼团提督', icon: '🕳️',
        desc: '史上唯一拥有双果实能力者：暗暗果实与震震果实。精于算计，是新时代最危险的变数。',
        tags: ['暗暗果实', '震震果实', '双能力'], power: 94,
        color: '#2c3e50', colorEnd: '#5d6d7e'
      }
    ]
  },
  {
    name: '海军',
    icon: '⚓',
    subtitle: '世界政府直属武装力量，以"正义"之名维护秩序',
    members: [
      {
        name: '赤犬萨卡斯基', title: '海军元帅', icon: '🌋',
        desc: '岩浆果实能力者，信奉"彻底的正义"。顶上战争中击杀艾斯，是海军最强战力的象征。',
        tags: ['岩浆果实', '彻底正义', '元帅'], power: 97,
        color: '#e74c3c', colorEnd: '#ff6348'
      },
      {
        name: '青雉库赞', title: '前海军大将', icon: '❄️',
        desc: '冰冰果实能力者，信奉"懒惰的正义"。与赤犬决战落败后离开海军，独自行走于灰色地带。',
        tags: ['冰冰果实', '懒散正义', '前大将'], power: 94,
        color: '#3498db', colorEnd: '#5dade2'
      },
      {
        name: '黄猿波鲁萨利诺', title: '海军大将', icon: '💡',
        desc: '闪闪果实能力者，光速的踢技与激光令敌人绝望。看似随性散漫，实则城府极深。',
        tags: ['闪闪果实', '光速', '不确定正义'], power: 93,
        color: '#f1c40f', colorEnd: '#f9e154'
      },
      {
        name: '藤虎一笑', title: '海军大将', icon: '🌌',
        desc: '重力果实能力者，自盲双目却心怀仁义。主张废除七武海制度，是海军中的改革派。',
        tags: ['重力果实', '仁义', '盲剑客'], power: 92,
        color: '#8e44ad', colorEnd: '#a569bd'
      }
    ]
  },
  {
    name: '王下七武海',
    icon: '⚖️',
    subtitle: '曾被世界政府公认的七位大海贼，制度已于世界会议后废除',
    members: [
      {
        name: '鹰眼米霍克', title: '世界最强剑士', icon: '🗡️',
        desc: '黑刀·夜的持有者，唯一能与红发香克斯匹敌的剑士。孤高冷傲，追求极致剑道。',
        tags: ['黑刀·夜', '最强剑士', '孤高'], power: 96,
        color: '#f39c12', colorEnd: '#f5b041'
      },
      {
        name: '波雅·汉库克', title: '海贼女帝', icon: '🐍',
        desc: '甜甜果实能力者，九蛇海贼团船长。美貌绝世，实力也不容小觑，是路飞的坚定守护者。',
        tags: ['甜甜果实', '霸王色', '九蛇'], power: 85,
        color: '#e91e63', colorEnd: '#f48fb1'
      },
      {
        name: '多弗朗明哥', title: '天夜叉', icon: '🕸️',
        desc: '线线果实觉醒者，德雷斯罗萨前国王。操纵暗线编织世界，是地下世界的支配者。',
        tags: ['线线果实', '觉醒', '天龙人'], power: 88,
        color: '#e84393', colorEnd: '#fd79a8'
      },
      {
        name: '巴索罗缪·熊', title: '暴君', icon: '🐾',
        desc: '肉球果实能力者，革命军干部。自愿改造为和平主义者，为女儿波妮牺牲一切。',
        tags: ['肉球果实', '改造人', '革命军'], power: 84,
        color: '#00b894', colorEnd: '#55efc4'
      }
    ]
  },
  {
    name: '革命军',
    icon: '🔥',
    subtitle: '直接对抗世界政府的地下势力，被视为最危险的存在',
    members: [
      {
        name: '蒙奇·D·龙', title: '革命军总司令 · 世界最凶恶罪犯', icon: '🌪️',
        desc: '路飞的父亲，海军英雄卡普之子。领导革命军对抗世界政府，被称为"世界最凶恶的罪犯"。',
        tags: ['风之能力?', '总司令', 'D之一族'], power: 97,
        color: '#27ae60', colorEnd: '#2ecc71'
      },
      {
        name: '萨博', title: '革命军参谋总长 · No.2', icon: '🔥',
        desc: '路飞与艾斯的义兄，继承了艾斯的烧烧果实。龙爪拳传人，革命军的核心战力。',
        tags: ['烧烧果实', '龙爪拳', '义兄弟'], power: 90,
        color: '#e67e22', colorEnd: '#f0932b'
      },
      {
        name: '伊万科夫', title: '革命军军队长 · 人妖王', icon: '👸',
        desc: '激素果实能力者，卡玛巴卡王国女王。性格豪放不羁，在推进城中拯救了路飞。',
        tags: ['激素果实', '人妖拳法', '卡玛巴卡'], power: 78,
        color: '#6c5ce7', colorEnd: '#a29bfe'
      },
      {
        name: '巴索罗缪·熊', title: '革命军干部 · 双重身份', icon: '🛡️',
        desc: '既是七武海也是革命军干部，在龙的指示下潜伏于世界政府内部，最终为信念献出自我。',
        tags: ['双面间谍', '自我牺牲', '和平主义者'], power: 84,
        color: '#00cec9', colorEnd: '#81ecec'
      }
    ]
  }
]

// ==================== 粒子动画 ====================
let animationId = null

const initParticles = () => {
  const canvas = canvasRef.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  let w = canvas.width = window.innerWidth
  let h = canvas.height = document.body.scrollHeight

  const particles = Array.from({ length: 80 }, () => ({
    x: Math.random() * w,
    y: Math.random() * h,
    r: Math.random() * 2 + 0.5,
    dx: (Math.random() - 0.5) * 0.4,
    dy: (Math.random() - 0.5) * 0.4,
    alpha: Math.random() * 0.5 + 0.1
  }))

  const draw = () => {
    ctx.clearRect(0, 0, w, h)
    for (const p of particles) {
      p.x += p.dx
      p.y += p.dy
      if (p.x < 0 || p.x > w) p.dx *= -1
      if (p.y < 0 || p.y > h) p.dy *= -1
      ctx.beginPath()
      ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2)
      ctx.fillStyle = `rgba(255, 215, 0, ${p.alpha})`
      ctx.fill()
    }
    // 连线
    for (let i = 0; i < particles.length; i++) {
      for (let j = i + 1; j < particles.length; j++) {
        const dx = particles[i].x - particles[j].x
        const dy = particles[i].y - particles[j].y
        const dist = Math.sqrt(dx * dx + dy * dy)
        if (dist < 120) {
          ctx.beginPath()
          ctx.moveTo(particles[i].x, particles[i].y)
          ctx.lineTo(particles[j].x, particles[j].y)
          ctx.strokeStyle = `rgba(255, 215, 0, ${0.06 * (1 - dist / 120)})`
          ctx.stroke()
        }
      }
    }
    animationId = requestAnimationFrame(draw)
  }
  draw()

  const handleResize = () => {
    w = canvas.width = window.innerWidth
    h = canvas.height = document.body.scrollHeight
  }
  window.addEventListener('resize', handleResize)
}

// ==================== 滚动观察 ====================
let observer = null

const initObserver = () => {
  observer = new IntersectionObserver(
    (entries) => {
      entries.forEach(entry => {
        const idx = sectionRefs.value.indexOf(entry.target)
        if (idx !== -1 && entry.isIntersecting) {
          visibleSections.value[idx] = true
        }
      })
    },
    { threshold: 0.15 }
  )

  sectionRefs.value.forEach(el => {
    if (el) observer.observe(el)
  })
}

// footer observer
let footerObs = null

onMounted(() => {
  mounted.value = true
  visibleSections.value = groups.map(() => false)

  setTimeout(() => {
    initParticles()
    initObserver()
  }, 100)

  // footer
  footerObs = new IntersectionObserver(
    ([e]) => { if (e.isIntersecting) footerVisible.value = true },
    { threshold: 0.2 }
  )
  const footerEl = document.querySelector('.world-footer')
  if (footerEl) footerObs.observe(footerEl)
})

onUnmounted(() => {
  if (animationId) cancelAnimationFrame(animationId)
  if (observer) observer.disconnect()
  if (footerObs) footerObs.disconnect()
})
</script>

<style scoped>
/* ==================== 基础 ==================== */
* { margin: 0; padding: 0; box-sizing: border-box; }

.group-page {
  font-family: 'Arial Rounded MT Bold', 'Arial', sans-serif;
  color: #fff;
  background: #030b1a;
  min-height: 100vh;
  overflow-x: hidden;
  position: relative;
}

.particle-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}

/* ==================== 导航栏 ==================== */
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
  font-size: 24px;
  font-weight: bold;
  color: #ffd700;
  text-shadow: 0 0 15px rgba(255, 215, 0, 0.5);
  cursor: pointer;
}

.nav-links { display: flex; gap: 20px; }

.nav-item {
  color: #8892b0;
  text-decoration: none;
  font-size: 15px;
  padding: 6px 14px;
  border-radius: 6px;
  transition: all 0.3s;
}

.nav-item:hover {
  color: #ffd700;
  background: rgba(255, 215, 0, 0.08);
}

.nav-item.active {
  color: #ffd700;
  font-weight: bold;
  background: rgba(255, 215, 0, 0.1);
  border: 1px solid rgba(255, 215, 0, 0.2);
}

/* ==================== 主视觉 ==================== */
.hero-banner {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  position: relative;
  z-index: 1;
  background:
    radial-gradient(ellipse at 30% 50%, rgba(255, 50, 50, 0.06) 0%, transparent 60%),
    radial-gradient(ellipse at 70% 50%, rgba(50, 100, 255, 0.06) 0%, transparent 60%);
}

.hero-content {
  opacity: 0;
  transform: translateY(40px);
  transition: all 1.2s cubic-bezier(0.16, 1, 0.3, 1);
}

.hero-content.fade-in {
  opacity: 1;
  transform: translateY(0);
}

/* 故障文字效果 */
.glitch-title {
  font-size: 72px;
  font-weight: 900;
  color: #ffd700;
  text-shadow:
    0 0 20px rgba(255, 215, 0, 0.4),
    0 0 60px rgba(255, 215, 0, 0.15);
  letter-spacing: 12px;
  position: relative;
  animation: glow 3s ease-in-out infinite alternate;
}

@keyframes glow {
  from { text-shadow: 0 0 20px rgba(255, 215, 0, 0.4), 0 0 60px rgba(255, 215, 0, 0.15); }
  to   { text-shadow: 0 0 30px rgba(255, 215, 0, 0.6), 0 0 80px rgba(255, 215, 0, 0.25), 0 0 120px rgba(255, 215, 0, 0.1); }
}

.hero-sub {
  color: #5e6e8a;
  font-size: 18px;
  margin-top: 16px;
  letter-spacing: 4px;
}

.scroll-hint {
  margin-top: 60px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  color: #3a4a6a;
  font-size: 13px;
  letter-spacing: 2px;
}

.scroll-arrow {
  width: 20px;
  height: 20px;
  border-right: 2px solid #3a4a6a;
  border-bottom: 2px solid #3a4a6a;
  transform: rotate(45deg);
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% { transform: rotate(45deg) translateY(0); }
  50% { transform: rotate(45deg) translateY(8px); }
}

/* ==================== 势力区块 ==================== */
.force-section {
  position: relative;
  z-index: 1;
  padding: 100px 40px 80px;
  opacity: 0;
  transform: translateY(60px);
  transition: all 0.9s cubic-bezier(0.16, 1, 0.3, 1);
}

.force-section.visible {
  opacity: 1;
  transform: translateY(0);
}

/* 交替背景 */
.section-0 { background: rgba(255, 50, 50, 0.02); }
.section-1 { background: rgba(50, 100, 255, 0.02); }
.section-2 { background: rgba(255, 200, 0, 0.02); }
.section-3 { background: rgba(50, 200, 100, 0.02); }

.section-header { text-align: center; margin-bottom: 60px; }

.section-icon {
  font-size: 48px;
  display: block;
  margin-bottom: 12px;
  filter: drop-shadow(0 0 10px rgba(255, 215, 0, 0.3));
}

.section-title {
  font-size: 36px;
  color: #ffd700;
  letter-spacing: 6px;
  text-shadow: 0 0 20px rgba(255, 215, 0, 0.3);
}

.section-desc {
  color: #5e6e8a;
  font-size: 15px;
  margin-top: 10px;
  letter-spacing: 2px;
}

.title-line {
  width: 80px;
  height: 2px;
  background: linear-gradient(90deg, transparent, #ffd700, transparent);
  margin: 20px auto 0;
}

/* ==================== 卡片网格 ==================== */
.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 28px;
  max-width: 1280px;
  margin: 0 auto;
}

/* ==================== 卡片 ==================== */
.member-card {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  background: rgba(10, 20, 40, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(12px);
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  cursor: default;

  /* 入场动画 */
  opacity: 0;
  transform: translateY(40px) scale(0.95);
}

.member-card.card-visible {
  animation: cardIn 0.7s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes cardIn {
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.member-card:hover {
  transform: translateY(-8px) scale(1.02);
  border-color: rgba(255, 215, 0, 0.2);
  box-shadow:
    0 20px 50px rgba(0, 0, 0, 0.4),
    0 0 30px rgba(255, 215, 0, 0.05);
}

/* 顶部能量条 */
.card-energy {
  height: 3px;
  opacity: 0.6;
  transition: opacity 0.4s;
}

.member-card:hover .card-energy {
  opacity: 1;
  height: 4px;
}

.card-body { padding: 28px 24px 24px; }

/* 头像 */
.card-avatar {
  position: relative;
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-icon {
  font-size: 32px;
  z-index: 1;
  transition: transform 0.4s;
}

.member-card:hover .avatar-icon {
  transform: scale(1.2) rotate(8deg);
}

.avatar-ring {
  position: absolute;
  inset: 0;
  border: 2px solid;
  border-radius: 50%;
  opacity: 0.3;
  transition: all 0.4s;
}

.member-card:hover .avatar-ring {
  opacity: 0.7;
  transform: scale(1.15);
  box-shadow: 0 0 15px currentColor;
}

.card-name {
  font-size: 18px;
  color: #e6f1ff;
  text-align: center;
  margin-bottom: 4px;
}

.card-title {
  display: block;
  text-align: center;
  font-size: 12px;
  letter-spacing: 1px;
  margin-bottom: 14px;
  opacity: 0.8;
}

.card-desc {
  font-size: 13px;
  color: #5e6e8a;
  line-height: 1.8;
  margin-bottom: 16px;
  transition: color 0.3s;
}

.member-card:hover .card-desc { color: #8892b0; }

/* 标签 */
.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 16px;
}

.tag {
  font-size: 11px;
  padding: 3px 10px;
  border-radius: 20px;
  border: 1px solid;
  opacity: 0.6;
  transition: opacity 0.3s;
}

.member-card:hover .tag { opacity: 1; }

/* 战力条 */
.power-bar { margin-top: auto; }

.power-label {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  color: #3a4a6a;
  margin-bottom: 6px;
}

.power-track {
  height: 4px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 2px;
  overflow: hidden;
}

.power-fill {
  height: 100%;
  border-radius: 2px;
  transition: width 1.2s cubic-bezier(0.16, 1, 0.3, 1);
  box-shadow: 0 0 8px currentColor;
}

/* ==================== 底部 ==================== */
.world-footer {
  position: relative;
  z-index: 1;
  text-align: center;
  padding: 100px 40px;
  background: linear-gradient(180deg, transparent 0%, rgba(255, 215, 0, 0.02) 100%);
  opacity: 0;
  transform: translateY(30px);
  transition: all 1s ease;
}

.world-footer.visible {
  opacity: 1;
  transform: translateY(0);
}

.footer-content h2 {
  font-size: 28px;
  color: #ffd700;
  margin-bottom: 20px;
  letter-spacing: 6px;
  text-shadow: 0 0 20px rgba(255, 215, 0, 0.3);
}

.footer-content p {
  color: #3a4a6a;
  font-size: 15px;
  line-height: 2;
  max-width: 700px;
  margin: 0 auto;
}

/* ==================== 响应式 ==================== */
@media (max-width: 768px) {
  .glitch-title { font-size: 40px; letter-spacing: 6px; }
  .hero-sub { font-size: 14px; }
  .force-section { padding: 60px 16px 50px; }
  .section-title { font-size: 26px; }
  .cards-grid { grid-template-columns: 1fr; }
  .top-header { padding: 12px 16px; }
  .nav-links { gap: 10px; }
}
</style>
