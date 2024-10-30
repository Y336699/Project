package com.qian.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField(value = "userName")
    private String userName;

    /**
     * 账户
     */
    @TableField(value = "userAccount")
    private String userAccount;

    /**
     * 头像
     */
    @TableField(value = "avatarUrl")
    private String avatarUrl;

    /**
     * 性别
     */
    @TableField(value = "gender")
    private Integer gender;

    /**
     * 
     */
    @TableField(value = "userPassword")
    private String userPassword;

    /**
     * 
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 
     */
    @TableField(value = "email")
    private String email;

    /**
     * 用户状态0正常
     */
    @TableField(value = "userStatus")
    private Integer userStatus;

    /**
     * 
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    /**
     * 
     */
    @TableField(value = "isDelete")
    @TableLogic
    private Integer isDelete;

    /**
     * 0-普通用户 1-管理员
     */
    @TableField(value = "userRole")
    private Integer userRole;

    /**
     * 标签列表
     */
    @TableField(value = "tags")
    private String tags;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}