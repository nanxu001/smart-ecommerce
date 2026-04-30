package com.smartecommerce.config;

import com.smartecommerce.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtUtils jwtUtils;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(jwtUtils))
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/register", "/api/auth/login")
                .excludePathPatterns("/api/products/**")
                .excludePathPatterns("/api/categories/**")
                .excludePathPatterns("/api/products/hot");
    }

    @RequiredArgsConstructor
    private static class AuthInterceptor implements HandlerInterceptor {
        private final JwtUtils jwtUtils;

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                return true;
            }

            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                if (jwtUtils.validateToken(token)) {
                    Long userId = jwtUtils.getUserId(token);
                    request.setAttribute("userId", userId);
                    return true;
                }
            }

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或登录已过期\",\"data\":null}");
            return false;
        }
    }
}
