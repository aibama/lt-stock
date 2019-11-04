package com.lt.interceptor;

import com.google.common.base.Joiner;
import com.lt.entity.User;
import com.lt.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 拦截器
 * 主要作用：日志、安全、异常等
 * 生命周期：IOC容器管理
 */
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        Map<String, String[]> map = request.getParameterMap();
        map.forEach((k,v) -> {
            if (k.equals("errorMsg") || k.equals("successMsg") || k.equals("target")) {
                request.setAttribute(k, Joiner.on(",").join(v));
            }
        });
        String reqUri =	request.getRequestURI();
        if (reqUri.startsWith("/static") || reqUri.startsWith("/error") ) {
            return true;
        }
        String token = request.getHeader(Constants.ACCESS_TOKEN);
        log.info("获取访问请求的token:{}",token);
        User user = User.builder()
                .username(token)
                .build();
        UserContext.setUser(user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        UserContext.remove();
    }
}
