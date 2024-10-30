package com.qian.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍
 * @TableName team
 */
@TableName(value ="team")
@Data
public class Team implements Serializable {
    /**
     * id
     */
    @JsonProperty
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 队伍名称
     */
    @JsonProperty
    @TableField(value = "name")
    private String name;

    /**
     * 描述
     */
    @JsonProperty
    @TableField(value = "description")
    private String description;

    /**
     * 最大人数
     */
    @JsonProperty
    @TableField(value = "maxNum")
    private Integer maxNum;

    /**
     * 过期时间
     */
    @JsonProperty
    @TableField(value = "expireTime")
    private Date expireTime;

    /**
     * 用户id（队长 id）
     */
    @JsonProperty
    @TableField(value = "userId")
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    @TableField(value = "status")
    @JsonProperty
    private Integer status;

    /**
     * 密码
     */
    @TableField(value = "password")
    @JsonProperty
    private String password;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    @JsonProperty
    private Date createTime;

    /**
     * 
     */
    @TableField(value = "updateTime")
    @JsonProperty
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableField(value = "isDelete")
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}