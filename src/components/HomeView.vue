<template>
  <div class="home" @mousemove="handleMouse">
    <!-- 背景 -->
    <div class="bg" :style="bgStyle"></div>
    <!-- 遮罩 -->
    <div class="overlay"></div>
    <!-- 主视觉 -->
    <section class="hero">
      <h1 class="title" :class="{ show: showTitle }">ONE PIECE</h1>
      <p class="subtitle" :class="{ show: showTitle }">成为海贼王的男人</p>
      <!-- 新增：全屏按钮 + 登录按钮 -->
      <div class="btn-group">
        <button class="fullscreen" @click="toggleFullscreen">全屏模式</button>
        <button class="enter" @click="toLogin">扬帆起航</button>
      </div>
    </section>
    <!-- 滚动提示 -->
    <div class="scroll-tip">↓ SCROLL ↓</div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'

// 引入封装的接口
// import { sendCodeAPI, loginAPI, registerAPI } from "@/api/user"

const showTitle = ref(false)
const router = useRouter()

// 鼠标视差
const mouseX = ref(0)
const mouseY = ref(0)
const handleMouse = (e) => {
  mouseX.value = (e.clientX / window.innerWidth - 0.5) * 20
  mouseY.value = (e.clientY / window.innerHeight - 0.5) * 20
}
const bgStyle = computed(() => {
  return {
    transform: `scale(1.1) translate(${mouseX.value}px, ${mouseY.value}px)`
  }
})

// 登录跳转
const toLogin = () => {
  router.push('/login')
}

// 核心：全屏切换逻辑
const toggleFullscreen = () => {
  const docEl = document.documentElement
  // 判断当前是否全屏
  const isFullscreen = !!document.fullscreenElement ||
                       !!document.webkitFullscreenElement ||
                       !!document.msFullscreenElement

  if (isFullscreen) {
    // 退出全屏
    if (document.exitFullscreen) {
      document.exitFullscreen()
    } else if (document.webkitExitFullscreen) {
      document.webkitExitFullscreen()
    } else if (document.msExitFullscreen) {
      document.msExitFullscreen()
    }
  } else {
    // 进入全屏
    if (docEl.requestFullscreen) {
      docEl.requestFullscreen()
    } else if (docEl.webkitRequestFullscreen) {
      docEl.webkitRequestFullscreen()
    } else if (docEl.msRequestFullscreen) {
      docEl.msRequestFullscreen()
    }
  }
}

// 自动进入全屏（可选：打开页面就全屏，注释掉则需要手动点击）
// onMounted(() => {
//   setTimeout(() => toggleFullscreen(), 500)
// })

onMounted(async () => {
  showTitle.value = true
})


</script>

<!-- <script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'

// 修正接口导入路径：API → api（Vue项目中目录名一般小写）
import { sendCodeAPI, loginAPI, registerAPI } from "@/api/user"

// ========== 补全缺失的核心变量 ==========
// 标题显示控制
const showTitle = ref(false)
// 路由实例
const router = useRouter()
// 模式切换（login/register）
const mode = ref("login")
// 登录表单（适配后端LoginFormDTO）
const loginForm = ref({
  phone: "",
  password: ""
})
// 注册相关变量
const phone = ref("")
const code = ref("")
const password = ref("")
const countdown = ref(0)

// ========== 鼠标视差效果 ==========
const mouseX = ref(0)
const mouseY = ref(0)
const handleMouse = (e) => {
  mouseX.value = (e.clientX / window.innerWidth - 0.5) * 20
  mouseY.value = (e.clientY / window.innerHeight - 0.5) * 20
}
const bgStyle = computed(() => {
  return {
    transform: `scale(1.1) translate(${mouseX.value}px, ${mouseY.value}px)`
  }
})

// ========== 页面跳转 ==========
const toLogin = () => {
  router.push('/login')
}

// ========== 全屏切换逻辑 ==========
const toggleFullscreen = () => {
  const docEl = document.documentElement
  // 判断当前是否全屏（兼容多浏览器）
  const isFullscreen = !!document.fullscreenElement ||
                       !!document.webkitFullscreenElement ||
                       !!document.msFullscreenElement

  if (isFullscreen) {
    // 退出全屏
    if (document.exitFullscreen) {
      document.exitFullscreen()
    } else if (document.webkitExitFullscreen) {
      document.webkitExitFullscreen()
    } else if (document.msExitFullscreen) {
      document.msExitFullscreen()
    }
  } else {
    // 进入全屏
    if (docEl.requestFullscreen) {
      docEl.requestFullscreen()
    } else if (docEl.webkitRequestFullscreen) {
      docEl.webkitRequestFullscreen()
    } else if (docEl.msRequestFullscreen) {
      docEl.msRequestFullscreen()
    }
  }
}

// ========== 登录方法 ==========
const login = async () => {
  // 表单校验
  if (!loginForm.value.phone || !loginForm.value.password) {
    return alert("请输入手机号和密码！")
  }
  if (!/^1\d{10}$/.test(loginForm.value.phone)) {
    return alert("请输入正确的手机号！")
  }

  try {
    // 调用封装的登录接口
    const data = await loginAPI(loginForm.value)
    alert("登录成功！")
    // 登录成功跳转首页（根据你的路由配置修改）
    router.push("/home")
    // 可选：存储token到本地
    // localStorage.setItem("token", data.token)
  } catch (error) {
    console.error("登录失败：", error)
  }
}

// ========== 发送验证码 ==========
const sendCode = async () => {
  if (!/^1\d{10}$/.test(phone.value)) {
    return alert("请输入正确的手机号！")
  }

  try {
    await sendCodeAPI(phone.value)
    alert("验证码发送成功！")
    // 开启60秒倒计时（防止重复点击）
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    console.error("发送验证码失败：", error)
  }
}

// ========== 注册方法 ==========
const register = async () => {
  // 表单校验
  if (!phone.value || !code.value || !password.value) {
    return alert("请填写完整的注册信息！")
  }
  if (!/^1\d{10}$/.test(phone.value)) {
    return alert("请输入正确的手机号！")
  }
  if (password.value.length < 6) {
    return alert("密码长度不能少于6位！")
  }

  try {
    await registerAPI({
      phone: phone.value,
      code: code.value,
      password: password.value
    })
    alert("注册成功！请登录")
    // 切换到登录模式并清空表单
    mode.value = "login"
    phone.value = ""
    code.value = ""
    password.value = ""
  } catch (error) {
    console.error("注册失败：", error)
  }
}

// ========== 生命周期 ==========
// 自动进入全屏（可选，注释掉则手动触发）
// onMounted(() => {
//   setTimeout(() => toggleFullscreen(), 500)
// })

onMounted(async () => {
  showTitle.value = true
})
</script> -->

<style scoped>
/* 核心修改：消除所有边距，强制占满整个视口 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  overflow: hidden; /* 隐藏滚动条，彻底占满窗口 */
}

.home {
  position: relative;
  height: 100vh; /* 改为100vh，只占一屏 */
  width: 100%;
  overflow-x: hidden;
  color: white;
}

/* 背景图：强制覆盖整个屏幕 */
.bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('/images/bg.jpg') center/cover no-repeat;
  z-index: -2;
  transition: transform 0.2s ease-out;
}

/* 遮罩 */
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: linear-gradient(to bottom, rgba(0,0,0,0.6), rgba(0,0,0,0.95));
  z-index: -1;
}

/* 主视觉：居中优化 */
.hero {
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 0 20px;
}

.title {
  font-size: clamp(60px, 15vw, 90px); /* 响应式字体大小 */
  opacity: 0;
  transform: translateY(40px);
  transition: all 1s ease;
}

.title.show {
  opacity: 1;
  transform: translateY(0);
}

.subtitle {
  margin-top: 20px;
  font-size: clamp(16px, 3vw, 24px);
  opacity: 0;
  transition: all 1.5s ease;
}

.subtitle.show {
  opacity: 1;
}

/* 按钮组布局优化 */
.btn-group {
  margin-top: 40px;
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  justify-content: center;
}

.enter {
  padding: 12px 30px;
  border-radius: 30px;
  border: none;
  font-size: 18px;
  background: linear-gradient(45deg, #0ea5e9, #6366f1);
  color: white;
  cursor: pointer;
  transition: transform 0.3s;
}

.enter:hover {
  transform: scale(1.1);
}

/* 全屏按钮样式 */
.fullscreen {
  padding: 12px 30px;
  border-radius: 30px;
  border: 2px solid white;
  font-size: 18px;
  background: transparent;
  color: white;
  cursor: pointer;
  transition: all 0.3s;
}

.fullscreen:hover {
  background: rgba(255,255,255,0.2);
  transform: scale(1.1);
}

/* 滚动提示 */
.scroll-tip {
  position: absolute;
  bottom: 20px;
  width: 100%;
  text-align: center;
  animation: bounce 2s infinite;
  font-size: 14px;
}

@keyframes bounce {
  0%,100% { transform: translateY(0); }
  50% { transform: translateY(10px); }
}
</style>
