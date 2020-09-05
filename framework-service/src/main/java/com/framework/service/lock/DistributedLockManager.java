package com.framework.service.lock;

import java.util.function.Supplier;

public interface DistributedLockManager {

    /*
     * 带返回值的分布式锁模版方法
     * (默认最长等待锁时间3秒,默认锁过期时间10秒)
     */
    <T> T execute(String lockKey, Supplier<T> objectHandle);

    /*
     * 带返回值的分布式锁模版方法
     */
    <T> T execute(String lockKey, long tryWaitTime, Supplier<T> objectHandle);

    /*
     * 带返回值的分布式锁模版方法
     */
    <T> T execute(String lockKey, long tryWaitTime, long expireTime, Supplier<T> objectHandle);

    /*
     * 分布式锁模版方法（无返回值）
     * (默认最长等待锁时间3秒,默认锁过期时间10秒)
     */
    void executeNoReturn(String lockKey, LockObjectHandle objectHandle);

    /*
     * 分布式锁模版方法（无返回值）
     */
    void executeNoReturn(String lockKey, long tryWaitTime, LockObjectHandle objectHandle);

    /*
     * 分布式锁模版方法（无返回值）
     */
    void executeNoReturn(String lockKey, long tryWaitTime, long expireTime, LockObjectHandle objectHandle);
}
