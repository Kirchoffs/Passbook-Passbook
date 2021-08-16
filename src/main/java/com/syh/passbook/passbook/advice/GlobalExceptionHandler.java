package com.syh.passbook.passbook.advice;

import com.syh.passbook.passbook.vo.ErrorInfo;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ErrorInfo<String> errorHandler(HttpServletRequest request, Exception exception) throws Exception {
        ErrorInfo<String> info = new ErrorInfo<>();

        info.setCode(ErrorInfo.ERROR);
        info.setMessage(exception.getMessage());
        info.setData("error");
        info.setUrl(request.getRequestURL().toString());

        return info;
    }
}
