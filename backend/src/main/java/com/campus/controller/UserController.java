package com.campus.controller;

import com.campus.common.Result;
import com.campus.entity.User;
import com.campus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户接口（前台：注册、个人信息、改密码；后台：管理）。
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /** 注册 */
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        return userService.register(user);
    }

    /** 校验手机号是否已注册（公开接口，供注册页失焦校验） */
    @GetMapping("/checkPhone")
    public Result<?> checkPhone(@RequestParam String phone) {
        return userService.checkPhone(phone);
    }

    /** 个人中心 */
    @GetMapping("/profile")
    public Result<?> profile() {
        return userService.profile();
    }

    /** 修改个人信息 */
    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody User user) {
        return userService.update(user);
    }

    /** 修改密码 */
    @PostMapping("/password")
    public Result<?> changePassword(@RequestBody Map<String, String> body) {
        return userService.changePassword(body.get("oldPassword"), body.get("newPassword"));
    }

    /** 管理员：用户列表 */
    @GetMapping("/admin/list")
    public Result<List<User>> list(@RequestParam(required = false) Integer role,
                                   @RequestParam(required = false) String keyword) {
        return Result.success(userService.findAll(role, keyword));
    }

    /** 管理员：冻结/解冻 */
    @PostMapping("/admin/status")
    public Result<?> status(@RequestBody Map<String, Integer> body) {
        return userService.updateStatus(body.get("id"), body.get("status"));
    }

    /** 管理员：编辑用户（昵称、角色） */
    @PutMapping("/admin/{id}")
    public Result<?> adminUpdate(@PathVariable Integer id, @RequestBody User user) {
        user.setId(id);
        return userService.adminUpdate(user);
    }

    /** 管理员：删除用户 */
    @DeleteMapping("/admin/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        return userService.delete(id);
    }
}
