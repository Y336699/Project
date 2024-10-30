package com.qian.usercenter.comment;

import com.sun.org.apache.bcel.internal.classfile.Code;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID = 6579476291413199247L;
    private int code;
    private T data;
    private String message;
    private String description;

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }
    public BaseResponse(int code, T data, String message) {
        this(code,data,message,"");
    }
    public BaseResponse(int code, T data) {
        this(code,data,"","");
    }
    public BaseResponse(ErrorCode code){
        this(code.getCode(),null,code.getMessage(),"");
    }


}

