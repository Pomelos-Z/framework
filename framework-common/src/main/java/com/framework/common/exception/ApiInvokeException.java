package com.framework.common.exception;

import com.framework.common.enums.CommonErrorEnum;

import java.text.MessageFormat;

public class ApiInvokeException extends BaseException {

    public static final ApiInvokeException API_NOT_EXIST = new ApiInvokeException(CommonErrorEnum.API_NOT_EXIST);

    public static final ApiInvokeException API_PARAM_LENGTH_ERROR = new ApiInvokeException(CommonErrorEnum.INVALID_PARAM);

    public static final ApiInvokeException DO_NOT_HAS_PERMISSION = new ApiInvokeException(CommonErrorEnum.PERMISSION_DENIED);

    public static final ApiInvokeException API_PARAM_VALIDATE_ERROR = new ApiInvokeException(CommonErrorEnum.PARAM_VALIDATE_ERROR);

    public static final ApiInvokeException API_INVALID_SIGIN = new ApiInvokeException(CommonErrorEnum.SIGN_ERROR);

    public static final ApiInvokeException API_INVOKE_ERROR = new ApiInvokeException(CommonErrorEnum.SYS_ERROR);

    public ApiInvokeException() {
    }

    private ApiInvokeException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private ApiInvokeException(CommonErrorEnum commonErrorEnum) {
        this(commonErrorEnum.getValue(), commonErrorEnum.getName());
    }

    @Override
    public ApiInvokeException newInstance(String msgFormat, Object... args) {
        return new ApiInvokeException(this.code, MessageFormat.format(msgFormat, args));
    }
}
