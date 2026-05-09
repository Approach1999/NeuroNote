package com.neuro.backend.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/auth/login",      // 👉 必须加上 /api 前缀！
                        "/api/auth/register",   // 👉 必须加上 /api 前缀！
                        "/api/auth/refresh",    // 👉 顺便把刷新接口也放行！
                        "/error",
                        "/**/OPTIONS"
                );
    }
}
