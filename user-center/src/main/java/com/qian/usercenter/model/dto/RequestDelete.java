package com.qian.usercenter.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RequestDelete implements Serializable {
    private static final long serialVersionUID = 4938958145560225103L;
    private Long id;
}
