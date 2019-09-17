package com.lt.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * @ControllerAdvice:拦截所有的控制器@Controller
 * @ExceptionHandler:拦截匹配控制器内的异常
 */
@Slf4j
@RestControllerAdvice
public class ExceptionConfig {

    @ExceptionHandler(value = SQLException.class)
    public ResultEntity sqlError(Exception ex){
        return this.neatenError(ex,ResultCode.FAIL.getCode(),"SQL数据库异常");
    }

    @ExceptionHandler(value = Exception.class)
    public ResultEntity unknownError(Exception ex){
        return this.neatenError(ex,ResultCode.FAIL.getCode(),ResultCode.FAIL.getVal());
    }

    public ResultEntity neatenError(Exception ex,int code,String msg){
        log.info("全局异常Exception:",ex);
        return ResultEntity.fail(code,msg);
    }

}
