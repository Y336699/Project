package com.qian.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = -5938695045410234573L;

    String userAccount;

    String userPassword;


}
