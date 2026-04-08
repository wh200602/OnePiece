package org.example.onepiece.config;

import jakarta.annotation.Resource;
import org.example.onepiece.utils.LoginInterceptor;
import org.example.onepiece.utils.RefreshInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private LoginInterceptor loginInterceptor;

    @Resource
    private RefreshInterceptor refreshInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // ✅ 刷新token拦截器：先执行
        registry.addInterceptor(refreshInterceptor)
                .addPathPatterns("/**")
                .order(0);

        // ✅ 登录拦截器：后执行
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login", "/user/code", "/shop/**")
                .order(1);
    }
}