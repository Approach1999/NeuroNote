package com.neuro.backend.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    // 🌟 注册 Sa-Token 拦截器，打开注解式鉴权功能
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns(
                        "/auth/login",      // 登录接口放行
                        "/auth/register",   // 注册接口放行
                        "/error",           // 错误页面放行
                        // 🌟 核心修复：放行跨域预检请求，否则前端会 401 报错！
                        "/**/OPTIONS"
                );
    }
}
