package org.example.onepiece.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.example.onepiece.dto.UserDTO;
import org.example.onepiece.entity.User;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取当前请求路径
        String requestURI = request.getRequestURI();

        // 2. 直接放行商品相关接口【关键！】
        if (requestURI.startsWith("/shop/")) {
            return true;
        }

        // 3. 其他路径必须登录
        if(UserHolder.getUser() == null){
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
