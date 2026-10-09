package com.campus.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class User {
    private Integer id;
    private String phone;
    private String password;
    private String nickname;
    private String avatar;
    /** 简介 */
    private String bio;
    /** 0 保密，1 男，2 女 */
    private Integer gender;
    /** 生日 */
    private LocalDate birthday;
    /** 爱好，逗号分隔 */
    private String hobbies;
    /** 0 学生，1 教师 */
    private Integer role;
    /** 0 冻结，1 正常 */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
