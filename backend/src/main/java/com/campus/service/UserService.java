package com.campus.service;

import com.campus.common.AuthContext;
import com.campus.common.Result;
import com.campus.entity.User;
import com.campus.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户业务服务。
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public List<User> findAll(Integer role, String keyword) {
        return userMapper.findAll(role, keyword);
    }

    public Result<?> register(User user) {
        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            return Result.error("手机号不能为空");
        }
        if (userMapper.findByPhone(user.getPhone().trim()) != null) {
            return Result.error("该手机号已注册");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword("123456"); // 默认密码
        }
        if (user.getNickname() == null || user.getNickname().trim().isEmpty()) {
            user.setNickname("用户" + user.getPhone().substring(Math.max(0, user.getPhone().length() - 4)));
        }
        if (user.getRole() == null) {
            user.setRole(0);
        }
        userMapper.insert(user);
        return Result.success();
    }

    public Result<?> update(User user) {
        AuthContext ctx = AuthContext.get();
        user.setId(ctx.getUserId());
        userMapper.update(user);
        return Result.success();
    }

    public Result<?> updateStatus(Integer id, Integer status) {
        userMapper.updateStatus(id, status);
        return Result.success();
    }

    /** 管理员编辑用户（昵称、角色） */
    public Result<?> adminUpdate(User user) {
        User exist = userMapper.findById(user.getId());
        if (exist == null) {
            return Result.error("用户不存在");
        }
        userMapper.update(user);
        return Result.success();
    }

    public Result<?> delete(Integer id) {
        userMapper.deleteById(id);
        return Result.success();
    }

    public Result<?> changePassword(String oldPassword, String newPassword) {
        AuthContext ctx = AuthContext.get();
        User user = userMapper.findById(ctx.getUserId());
        if (user == null || !user.getPassword().equals(oldPassword)) {
            return Result.error("原密码错误");
        }
        userMapper.updatePassword(ctx.getUserId(), newPassword);
        return Result.success();
    }

    public Result<?> profile() {
        AuthContext ctx = AuthContext.get();
        User user = userMapper.findById(ctx.getUserId());
        if (user != null) {
            user.setPassword(null); // 密码不外泄
        }
        return Result.success(user);
    }

    /** 校验手机号是否已注册（供前端失焦校验） */
    public Result<?> checkPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return Result.error("手机号不能为空");
        }
        User exist = userMapper.findByPhone(phone.trim());
        if (exist != null) {
            return Result.error("该手机号已注册");
        }
        return Result.success();
    }

    public long count() {
        return userMapper.count();
    }
}
