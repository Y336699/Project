package com.qian.usercenter.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class RequestJoin implements Serializable {

    private static final long serialVersionUID = -8677661736117567250L;

    private Long id;

    private Long userId;

    private String password;



}
