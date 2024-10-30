package com.qian.usercenter.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class RequestUpdate {

    private Long id;


    private String name;


    private String description;


    private Integer maxNum;


    private Date expireTime;


    private Integer status;


    private String password;


}
