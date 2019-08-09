package com.zhangyu.coderman.exception;

import com.zhangyu.coderman.myenums.CustomizeErrorCode;

public class CustomizeException extends RuntimeException {

    @Override
    public String getMessage() {
        return super.getMessage();
    }
    public CustomizeException(CustomizeErrorCode customizeErrorCode) {
        super(customizeErrorCode.getMessage());
    }
}
