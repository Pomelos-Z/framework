package com.framework.service.lock;

@FunctionalInterface
public interface LockObjectHandle {
    // 业务处理
    void handle();
}
