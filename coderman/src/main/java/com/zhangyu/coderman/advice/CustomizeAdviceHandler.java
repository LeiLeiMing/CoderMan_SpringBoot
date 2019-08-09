package com.zhangyu.coderman.advice;

import com.zhangyu.coderman.exception.CustomizeException;
import com.zhangyu.coderman.myenums.CustomizeErrorCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CustomizeAdviceHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView myHandException(HttpServletRequest request, Exception e){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error");
        if(e instanceof CustomizeException){
            modelAndView.addObject("errormessage",e.getMessage());
            return modelAndView;
        }else {
            e.printStackTrace();
            System.out.println("服务异常:"+e.getMessage());
            modelAndView.addObject("errormessage", CustomizeErrorCode.SYSTEM_Error.getMessage());
            return modelAndView;
        }
    }

}
