package com.framework.web.service;

import com.framework.common.exception.ApiInvokeException;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractInvokeService {

    // api入口
    public <T> T invoke(String appId, String methodName, String content) {
        boolean hasPermission = checkPermission(appId, methodName);
        if (!hasPermission) {
            throw ApiInvokeException.DO_NOT_HAS_PERMISSION;
        }
        boolean checkSign = checkSign(content, appId);
        if (!checkSign) {
            throw ApiInvokeException.API_INVALID_SIGIN;
        }
        // methodName为调用方法 比如 com.framework.web.xxx.getSthInfo
        // 使用BeanPostProcessor在装载完成时postProcessAfterInitialization把带指定注解的方法放置一个容器
        // content为签名加密的参数
        // 调用方法采用反射
        return null;
    }

    protected abstract boolean checkPermission(String appId, String methodName);

    protected abstract boolean checkSign(String content, String appId);
}
