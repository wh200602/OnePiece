// @/api/user.js
import request, { post } from "@/utils/request";

/**
 * 发送短信验证码
 * @param {string} phone 手机号
 * @returns {Promise} 响应结果
 */
export const sendCodeAPI = (phone) => {
  return request({
    url: "/user/code",
    method: "post",
    params: { phone } // GET/POST带查询参数（后端@RequestParam）
  });
};

/**
 * 用户登录
 * @param {Object} loginForm 登录表单
 * @param {string} loginForm.phone 手机号
 * @param {string} loginForm.password 密码
 * @returns {Promise} 响应结果（含token等）
 */
export const loginAPI = (loginForm) => {
  return post("/user/login", loginForm); // 用封装的post方法更简洁
};

/**
 * 用户注册
 * @param {Object} registerForm 注册表单
 * @param {string} registerForm.phone 手机号
 * @param {string} registerForm.code 验证码
 * @param {string} registerForm.password 密码
 * @returns {Promise} 响应结果
 */
export const registerAPI = (registerForm) => {
  return post("/user/register", registerForm);
};

/**
 * 退出登录（扩展接口）
 * @returns {Promise} 响应结果
 */
export const logoutAPI = () => {
  return post("/user/logout");
};

/**
 * 获取用户信息（扩展接口）
 * @returns {Promise} 用户信息
 */
export const getUserInfoAPI = () => {
  return request({
    url: "/user/info",
    method: "get"
  });
};
