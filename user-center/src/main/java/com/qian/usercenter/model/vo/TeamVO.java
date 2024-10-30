package com.qian.usercenter.model.vo;

import com.qian.usercenter.model.domain.User;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
@Data
public class TeamVO implements Serializable {

    private static final long serialVersionUID = 801047176707686437L;
    /**
     * id
     */
    private Long id;
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
     * 用户id（队长 id）
     */
    private Long userId;
    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    private UserVO createUser;


}
