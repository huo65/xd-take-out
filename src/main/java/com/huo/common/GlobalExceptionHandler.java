package com.huo.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public GeneralResult exceptionHandler(SQLIntegrityConstraintViolationException exp){
        log.error(exp.getMessage());
        if (exp.getMessage().contains("Duplicate entry")){
            String[] split = exp.getMessage().split(" ");
            String msg = split[2]+"用户名已存在";

            return GeneralResult.error(msg);
        }
        return GeneralResult.error("出错");
    }


    /**
     * 处理自定义的异常，为了让前端展示我们的异常信息，这里需要把异常进行全局捕获，然后返回给前端
     * @param exception
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public GeneralResult<String> exceptionHandle(CustomException exception){
        log.error(exception.getMessage()); //报错打日志
        return GeneralResult.error(exception.getMessage());
    }


}
