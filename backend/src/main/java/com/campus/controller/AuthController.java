package com.campus.controller;

import com.campus.common.Result;
import com.campus.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 登录认证接口。
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login/admin")
    public Result<Map<String, Object>> loginAdmin(@RequestBody Map<String, String> body) {
        return authService.loginAdmin(body.get("username"), body.get("password"));
    }

    @PostMapping("/login/user")
    public Result<Map<String, Object>> loginUser(@RequestBody Map<String, String> body) {
        return authService.loginUser(body.get("phone"), body.get("password"));
    }

    @PostMapping("/logout")
    public Result<?> logout(HttpServletRequest request) {
        authService.logout(request.getHeader("token"));
        return Result.success();
    }
}
