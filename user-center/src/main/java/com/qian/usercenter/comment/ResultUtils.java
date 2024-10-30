package com.qian.usercenter.comment;

public class ResultUtils {
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0,data,"ok");
    }
    public static BaseResponse error(ErrorCode code) {
        return new BaseResponse<>(code.getCode(),null,code.getMessage(),"");
    }

    public static BaseResponse error(int code, String message, String description) {
        return new BaseResponse<>(code,null,message,description);
    }

    public static BaseResponse error(ErrorCode systemError, String message) {
        return new BaseResponse(systemError.getCode(),null,message,"");
    }
}
