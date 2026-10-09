package com.campus.config;

import com.campus.common.AuthContext;
import com.campus.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证拦截器：校验 Token，注入登录上下文，做接口权限控制。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 CORS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            return forbidden(response, 401, "未登录");
        }

        AuthContext ctx = authService.getContext(token);
        if (ctx == null) {
            return forbidden(response, 401, "登录已失效，请重新登录");
        }

        // 接口权限控制：路径中包含 /admin/ 的接口仅管理员可访问
        String uri = request.getRequestURI();
        if (uri.contains("/admin/") && !"admin".equals(ctx.getRole())) {
            return forbidden(response, 403, "无权限访问");
        }

        AuthContext.set(ctx);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }

    private boolean forbidden(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":" + code + ",\"message\":\"" + message + "\",\"data\":null}");
        return false;
    }
}
