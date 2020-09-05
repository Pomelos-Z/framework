package com.framework.common.exception;

import com.framework.common.enums.CommonErrorEnum;

import java.io.Serializable;
import java.text.MessageFormat;

public class CommonException extends BaseException implements Serializable {

    private static final long serialVersionUID = -2270697749471863079L;

    public static final CommonException SYSTEM_ERROR = new CommonException(CommonErrorEnum.SYS_ERROR);

    public static final CommonException INVALID_PARAM_ERROR = new CommonException(CommonErrorEnum.INVALID_PARAM);


    public CommonException() {
    }

    private CommonException(String code, String msg) {
        super(code, msg);
    }

    private CommonException(CommonErrorEnum commonErrorEnum) {
        this(commonErrorEnum.getValue(), commonErrorEnum.getName());
    }

    @Override
    public CommonException newInstance(String msgFormat, Object... args) {
        return new CommonException(this.code, MessageFormat.format(msgFormat, args));
    }
}