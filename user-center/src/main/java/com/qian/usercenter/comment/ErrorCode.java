package com.qian.usercenter.comment;

public enum  ErrorCode {
    SUCCESS(0,"ok",""),
    NULL_ERROR(40001,"请求数据为空",""),
    PARAMS_ERROR(40002,"请求参数错误",""),
    NOT_LOGIN(40100,"未登录",""),
    NO_AUTH(40200,"无权限",""),
    SYSTEM_ERROR(50000,"系统错误","");
    private final int code;
    private final String message;
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
