package com.qian.usercenter.job.exception;

import com.qian.usercenter.comment.BaseResponse;
import com.qian.usercenter.comment.ErrorCode;
import com.qian.usercenter.comment.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessException(BusinessException e){
        log.error("记录错误信息"+e.getMessage(),e);
       return ResultUtils.error(e.getCode(),e.getMessage(),e.getDescription());
    }
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeException(RuntimeException e){
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,e.getMessage());
    }
}
