package com.qian.usercenter.job.exception;

import com.qian.usercenter.comment.ErrorCode;

public class BusinessException extends RuntimeException{
    private int code;
    private String description;

    public BusinessException() {
    }

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode code) {
        super(code.getMessage());
        this.code = code.getCode();
        this.description = code.getDescription();
    }

    public BusinessException(ErrorCode code, String description) {
        super(code.getMessage());
        this.code = code.getCode();
        this.description = description;
    }


    public int getCode() {
        return code;
    }


    public String getDescription() {
        return description;
    }
}


