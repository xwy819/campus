package com.campus.service;

import com.campus.common.AuthContext;
import com.campus.common.Result;
import com.campus.entity.Admin;
import com.campus.entity.User;
import com.campus.mapper.AdminMapper;
import com.campus.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录认证服务：Token 会话、三角色登录、权限上下文。
 */
@Service
public class AuthService {

    private final Map<String, AuthContext> sessionMap = new ConcurrentHashMap<>();

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private UserMapper userMapper;

    /** 管理员登录 */
    public Result<Map<String, Object>> loginAdmin(String username, String password) {
        Admin admin = adminMapper.findByUsername(username);
        if (admin == null || !admin.getPassword().equals(password)) {
            return Result.error("用户名或密码错误");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        AuthContext ctx = new AuthContext();
        ctx.setRole("admin");
        ctx.setUserId(admin.getId());
        ctx.setUsername(admin.getUsername());
        ctx.setName(admin.getName());
        sessionMap.put(token, ctx);

        Map<String, Object> data = new java.util.HashMap<>();
        data.put("token", token);
        data.put("role", "admin");
        data.put("name", admin.getName());
        data.put("userId", admin.getId());
        return Result.success(data);
    }

    /** 用户（学生/教师）登录 */
    public Result<Map<String, Object>> loginUser(String phone, String password) {
        User user = userMapper.findByPhone(phone);
        if (user == null || !user.getPassword().equals(password)) {
            return Result.error("手机号或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            return Result.error("账号已被冻结，请联系管理员");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        AuthContext ctx = new AuthContext();
        ctx.setRole(user.getRole() == 1 ? "teacher" : "student");
        ctx.setUserId(user.getId());
        ctx.setUsername(user.getPhone());
        ctx.setName(user.getNickname());
        sessionMap.put(token, ctx);

        Map<String, Object> data = new java.util.HashMap<>();
        data.put("token", token);
        data.put("role", ctx.getRole());
        data.put("name", user.getNickname());
        data.put("userId", user.getId());
        return Result.success(data);
    }

    /** 根据 Token 获取登录上下文 */
    public AuthContext getContext(String token) {
        if (token == null) {
            return null;
        }
        return sessionMap.get(token);
    }

    /** 退出登录 */
    public void logout(String token) {
        if (token != null) {
            sessionMap.remove(token);
        }
    }
}
