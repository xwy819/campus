package com.campus.common;

import lombok.Data;

/**
 * 当前登录用户上下文，通过 ThreadLocal 保存，请求结束后清理。
 */
@Data
public class AuthContext {
    /** 角色：admin / student / teacher */
    private String role;
    /** 用户ID（admin 表或 user 表的主键） */
    private Integer userId;
    /** 登录账号 */
    private String username;
    /** 姓名/昵称 */
    private String name;

    private static final ThreadLocal<AuthContext> HOLDER = new ThreadLocal<>();

    public static void set(AuthContext ctx) {
        HOLDER.set(ctx);
    }

    public static AuthContext get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }

    public boolean isAdmin() {
        return "admin".equals(role);
    }
}
