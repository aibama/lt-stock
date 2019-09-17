package com.lt.config;

import com.lt.interceptor.AuthInterceptor;
import com.lt.utils.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 2.0以前WebMvcConfigurerAdapter
 * 2.0以后WebMvcConfigurer
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 添加鉴权拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login","/register")
                .excludePathPatterns("/error")
                .excludePathPatterns("/static/**");
    }

    /**
     * 添加静态资源，过滤swagger-api
     *默认静态资源路径，优先级顺序为：META-INF/resources > resources > static > public
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    /**
     * 解决跨域请求
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")//匹配所有路径
                .allowedOrigins(CorsConfiguration.ALL)// 放行哪些原始域http://192.168.1.97
                .allowCredentials(true)// 是否发送Cookie信息
                .allowedMethods("GET", "POST", "PUT", "DELETE")// 放行哪些原始域(请求方式)
                .allowedHeaders(Constants.ACCESS_TOKEN)// 放行哪些原始域(头部信息)  token
                .maxAge(3600);//跨域允许时间
    }

}
