package com.framework.common.enums;

import org.apache.commons.lang3.StringUtils;

public enum CommonErrorEnum {

    SYS_ERROR("网络繁忙，请稍后再试", "1000"),
    API_NOT_EXIST("接口不存在", "1001"),
    INVALID_PARAM("接口参数不正确", "1002"),
    PERMISSION_DENIED("没有权限", "1003"),
    PARAM_VALIDATE_ERROR("参数验证失败", "1004"),
    SIGN_ERROR("签名验证失败", "1005"),
    ;

    private String name;
    private String value;

    CommonErrorEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static CommonErrorEnum getByValue(String value) {
        CommonErrorEnum[] valueList = CommonErrorEnum.values();
        for (CommonErrorEnum v : valueList) {
            if (StringUtils.equalsIgnoreCase(v.getValue(), value)) {
                return v;
            }
        }
        return null;
    }
}