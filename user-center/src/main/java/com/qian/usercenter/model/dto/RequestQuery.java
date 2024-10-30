package com.qian.usercenter.model.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RequestQuery {
    private static final long serialVersionUID = -6715179983799024147L;

    private Long id;
    /**
     * id列表
     */
    private List<Long> idList;
    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;

    private int pageSize = 1;

    private int pageNum = 3;
}
