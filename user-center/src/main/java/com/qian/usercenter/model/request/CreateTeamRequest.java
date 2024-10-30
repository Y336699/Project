package com.qian.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CreateTeamRequest implements Serializable {

    private static final long serialVersionUID = -5385096124956927644L;
    private String name;
    private String description;
    private Integer maxNum;
    private Date expireTime;
    private Integer status;
    private String password;
}
