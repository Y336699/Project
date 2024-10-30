package com.qian.usercenter.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 5927147400677512044L;

    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 账户
     */
    private String userAccount;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    private String phone;

    /**
     * 邮件
     */
    private String email;


    /**
     * 0-普通用户 1-管理员
     */
    private Integer userRole;

}
