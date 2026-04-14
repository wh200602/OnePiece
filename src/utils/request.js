// @/utils/request.js
import axios from "axios";
import { ElMessage } from "element-plus"; // 推荐用UI组件库提示，替代原生alert

// 统一走 nginx 负载均衡（端口80，/api 前缀）
const baseURL = import.meta.env.VITE_API_BASE_URL || "/api";

// 创建axios实例
const request = axios.create({
  baseURL,
  timeout: 10000,
  withCredentials: true,
  headers: {
    "Content-Type": "application/json;charset=utf-8"
  }
});

// 请求拦截器：统一添加token、请求头处理
request.interceptors.request.use(
  (config) => {
    // 从本地存储获取token（登录后存储）
    const token = localStorage.getItem("token");
    if (token) {
      // config.headers.Authorization = `Bearer ${token}`; // JWT标准格式
      config.headers.authorization = token;
    }
    return config;
  },
  (error) => {
    ElMessage.error("请求发送失败，请检查网络");
    console.error("[Request Error]", error);
    return Promise.reject(error);
  }
);

// 响应拦截器：统一处理响应结果（适配黑马点评格式）
// 响应拦截器：统一处理响应结果（适配黑马点评格式）
request.interceptors.response.use(
  (response) => {
    // 1. 拿到后端真实返回的所有数据
    const res = response.data || {};
    // 2. 打印到控制台，方便排查（调试完可删除）
    console.log("后端返回的原始数据：", res);

    // 3. ✅ 新增：对特定接口静默处理（如/shop系列）
    const silentEndpoints = ["/shop", "/shop/categories"]; // 需要静默的接口
    const shouldSilent = silentEndpoints.some(endpoint =>
      response.config.url?.includes(endpoint)
    );

    // 4. 兼容所有可能的成功格式（黑马code=0 / 旧success=true / 旧code=200）
    if (res.code === 0 || res.success === true || res.code === 200) {
      // ✅ 成功：只有非静默接口才提示
      if (!shouldSilent) {
        ElMessage.success(res.msg || res.errorMsg || "操作成功");
      }
      return res.data || null;
    }

    // 5. 业务失败（如验证码错误）：提示具体原因
    ElMessage.error(res.msg || res.errorMsg || "操作失败");
    return Promise.reject(new Error(res.msg || "业务错误"));
  },
  (error) => {
    // ... 错误处理保持不变
    let errMsg = "网络异常，请稍后重试";
    if (error.code === "ECONNABORTED") {
      errMsg = "请求超时，请检查网络";
    } else if (error.response) {
      const status = error.response.status;
      errMsg = `请求失败 [${status}]：${error.response.data?.msg || "服务器错误"}`;
    }
    ElMessage.error(errMsg);
    console.error("真实网络错误：", error);
    return Promise.reject(error);
  }
);
// 导出请求方法，支持直接调用
export default request;

// 额外导出便捷请求方法（可选）
export const get = (url, params = {}) => request.get(url, { params });
export const post = (url, data = {}) => request.post(url, data);
export const put = (url, data = {}) => request.put(url, data);
export const del = (url) => request.delete(url);
