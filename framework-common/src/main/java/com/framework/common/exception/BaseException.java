package com.framework.common.exception;

import java.text.MessageFormat;

public abstract class BaseException extends RuntimeException {

    protected String msg;
    protected String code;

    public BaseException(String code, String msgFormat, Object... args) {
        super(MessageFormat.format(msgFormat, args));
        this.code = code;
        this.msg = MessageFormat.format(msgFormat, args);
    }

    public BaseException() {
        super();
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(String message) {
        super(message);
    }

    public abstract BaseException newInstance(String msgFormat, Object... args);

    public String getMsg() {
        return msg;
    }

    public String getCode() {
        return code;
    }

    /*
     * 重写堆栈填充，不填充错误堆栈信息
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    @Override
    public String getMessage() {
        return this.code + "-" + this.msg;
    }

}


