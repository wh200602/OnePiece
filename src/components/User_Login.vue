<template>
  <div class="login-page" @mousemove="handleMouse">
    <div class="bg" :style="bgStyle"></div>
    <div class="wave"></div>

    <div class="login-box">
      <h1 class="title">ONE PIECE</h1>
      <p class="subtitle">航向伟大航路</p>

      <!-- 手机号+验证码 统一表单 -->
      <div class="input-box">
        <input v-model="phone" required placeholder="" />
        <label>手机号</label>
      </div>

      <div class="input-box code-box">
        <input v-model="code" required placeholder="" />
        <label>验证码</label>
        <button class="code-btn" @click="sendCode" :disabled="countdown > 0">
          {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
        </button>
      </div>

      <!-- 登录/注册 统一按钮 -->
      <button class="btn" @click="loginOrRegister">登 录 / 注 册</button>

      <div class="footer">
        <span>验证码一键登录，无需密码</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue"
import { useRouter } from "vue-router"
import { ElMessage } from "element-plus"
// 引入项目封装的请求工具和用户接口
import { sendCodeAPI, loginAPI } from "@/api/user"

// 路由实例
const router = useRouter()

// 核心数据
const phone = ref("")
const code = ref("")
const countdown = ref(0)
const mouseX = ref(0)
const mouseY = ref(0)

// 鼠标视差效果
const handleMouse = (e) => {
  mouseX.value = (e.clientX / window.innerWidth - 0.5) * 20
  mouseY.value = (e.clientY / window.innerHeight - 0.5) * 20
}
const bgStyle = computed(() => ({
  transform: `translate(${mouseX.value}px, ${mouseY.value}px)`
}))

// 发送验证码
const sendCode = async () => {
  // 手机号格式校验
  if (!/^1\d{10}$/.test(phone.value)) {
    return ElMessage.warning("请输入正确的11位手机号！")
  }
  // 倒计时中禁止重复点击
  if (countdown.value > 0) return

  try {
    // 调用 API，user.js 中已处理 params 传参
    await sendCodeAPI(phone.value)

    // 启动倒计时
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
        countdown.value = 0
      }
    }, 1000)
  } catch (error) {
    // 拦截器已处理错误提示（ElMessage.error），此处只需记录日志或做额外处理
    console.error("发送验证码失败：", error)
  }
}

// 统一登录/注册逻辑
const loginOrRegister = async () => {
  // 表单校验
  if (!/^1\d{10}$/.test(phone.value)) {
    return ElMessage.warning("请输入正确的11位手机号！")
  }
  if (!code.value || !/^\d{6}$/.test(code.value)) {
    return ElMessage.warning("请输入6位数字验证码！")
  }

  try {
    // 【关键修改】构造登录表单，字段名必须与后端 LoginFormDTO 一致
    // 后端 UserServiceImpl 中使用了 loginForm.getCode()，所以这里必须是 code
    const loginForm = {
      phone: phone.value,
      code: code.value
    }

    // 调用登录 API
    // 注意：request.js 拦截器在成功时会自动返回 res.data (即 token 字符串)
    // 如果失败，拦截器会抛出异常并弹出提示，代码不会执行到这里
    const token = await loginAPI(loginForm)

    // 成功处理
    if (token) {
      localStorage.setItem("token", token)
      ElMessage.success("登录成功，即将跳转首页！")

      // 延迟一点跳转让用户看到提示
      setTimeout(() => {
        router.push("/home")
      }, 500)
    } else {
      // 理论上拦截器已处理，但为了健壮性加个兜底
      ElMessage.warning("登录成功但未获取到 Token")
    }

    // 清空表单
    phone.value = ""
    code.value = ""

  } catch (error) {
    // 拦截器已捕获错误并弹窗，这里通常不需要再处理，除非需要特殊逻辑
    console.error("登录/注册流程异常：", error)
  }
}
</script>

<style scoped>
.login-page {
  position: relative;
  height: 100vh;
  overflow: hidden;
  background: #000;
}

/* 背景层 */
.bg {
  position: absolute;
  width: 120%;
  height: 120%;
  background: radial-gradient(circle at top, #0b1c2c, #000);
  transition: 0.2s;
}

/* 波浪动画层 */
.wave {
  position: absolute;
  bottom: 0;
  width: 200%;
  height: 120px;
  background: url("https://i.imgur.com/Za7dG9b.png");
  background-size: cover;
  opacity: 0.2;
  animation: waveMove 10s linear infinite;
}

@keyframes waveMove {
  from {
    transform: translateX(0);
  }
  to {
    transform: translateX(-50%);
  }
}

/* 登录卡片容器 */
.login-box {
  position: relative;
  z-index: 2;
  width: 380px;
  padding: 40px;
  margin: auto;
  top: 50%;
  transform: translateY(-50%);
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 215, 0, 0.3);
  box-shadow: 0 0 30px rgba(255, 215, 0, 0.2);
  text-align: center;
}

/* 标题样式 */
.title {
  color: gold;
  font-size: 28px;
  margin-bottom: 10px;
  letter-spacing: 2px;
}

.subtitle {
  color: #aaa;
  margin-bottom: 30px;
}

/* 输入框通用样式 */
.input-box {
  position: relative;
  margin-bottom: 25px;
}

.input-box input {
  width: 100%;
  padding: 12px;
  background: transparent;
  border: none;
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  outline: none;
}

.input-box label {
  position: absolute;
  left: 0;
  top: 12px;
  color: #aaa;
  transition: 0.3s;
}

/* 输入框聚焦/有值时标签上移 */
.input-box input:focus + label,
.input-box input:valid + label {
  top: -10px;
  font-size: 12px;
  color: gold;
}

/* 主按钮样式 */
.btn {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 25px;
  background: linear-gradient(45deg, gold, orange);
  color: black;
  font-weight: bold;
  cursor: pointer;
  transition: 0.3s;
}

.btn:hover {
  box-shadow: 0 0 15px gold;
  transform: translateY(-2px);
}

/* 底部说明 */
.footer {
  margin-top: 20px;
  color: #888;
  font-size: 12px;
}

.footer span {
  color: gold;
  cursor: default;
}

/* 验证码输入框容器 */
.code-box {
  display: flex;
  align-items: center;
}

.code-box input {
  flex: 1;
}

/* 获取验证码按钮 */
.code-btn {
  margin-left: 10px;
  padding: 6px 10px;
  border: none;
  border-radius: 10px;
  background: gold;
  cursor: pointer;
  font-size: 12px;
  white-space: nowrap;
}

.code-btn:disabled {
  background: #666;
  cursor: not-allowed;
  opacity: 0.7;
}
</style>
