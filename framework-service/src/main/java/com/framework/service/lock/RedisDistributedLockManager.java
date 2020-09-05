package com.framework.service.lock;

import com.framework.service.lock.exception.LockException;
import com.framework.service.redis.RedisCache;
import com.framework.service.utils.SystemTimeUtil;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class RedisDistributedLockManager implements DistributedLockManager {

    /**
     * redis连接对象
     */
    private RedisCache redisCache;

    /**
     * 默认等待锁时间(毫秒)
     */
    private final static long DEFAULT_TRY_WAIT_TIME = 3 * 1000L;

    /**
     * 默认redis锁过期时间(毫秒)
     */
    private final static long DEFAULT_EXPIRE_TIME = 10 * 1000L;

    @Override
    public <T> T execute(String lockKey, Supplier<T> objectHandle) {
        return execute(lockKey, DEFAULT_TRY_WAIT_TIME, DEFAULT_EXPIRE_TIME, objectHandle);
    }

    @Override
    public <T> T execute(String lockKey, long tryWaitTime, Supplier<T> objectHandle) {
        return execute(lockKey, tryWaitTime, DEFAULT_EXPIRE_TIME, objectHandle);
    }

    @Override
    public <T> T execute(String lockKey, long tryWaitTime, long expireTime, Supplier<T> objectHandle) {
        long currentTime = SystemTimeUtil.millisTime().now();
        RLock lock = null;
        T result;
        try {
            lock = tryLock(lockKey, tryWaitTime, expireTime);
            // 业务处理
            result = objectHandle.get();
        } catch (Exception e) {
            throw new LockException("redis加锁执行业务失败", e);
        } finally {
            unLock(lock, lockKey, currentTime);
        }
        return result;
    }

    @Override
    public void executeNoReturn(String lockKey, LockObjectHandle objectHandle) {
        executeNoReturn(lockKey, DEFAULT_TRY_WAIT_TIME, DEFAULT_EXPIRE_TIME, objectHandle);
    }

    @Override
    public void executeNoReturn(String lockKey, long tryWaitTime, LockObjectHandle objectHandle) {
        executeNoReturn(lockKey, tryWaitTime, DEFAULT_EXPIRE_TIME, objectHandle);
    }

    @Override
    public void executeNoReturn(String lockKey, long tryWaitTime, long expireTime, LockObjectHandle objectHandle) {
        long currentTime = SystemTimeUtil.millisTime().now();
        RLock lock = null;
        try {
            lock = tryLock(lockKey, tryWaitTime, expireTime);
            // 业务处理
            objectHandle.handle();
        } catch (Exception e) {
            throw new LockException("redis加锁执行业务失败", e);
        } finally {
            unLock(lock, lockKey, currentTime);
        }
    }

    // 加锁
    private RLock tryLock(String lockKey, long tryWaitTime, long expireTime) {
        RLock lock;
        try {
            lock = redisCache.getRedisLock(lockKey);
            boolean isSuccess = lock.tryLock(tryWaitTime, expireTime, TimeUnit.MILLISECONDS);
            if (!isSuccess) {
                throw new LockException();
            }
        } catch (Exception e) {
            throw new LockException("redis加锁失败", e);
        }
        return lock;
    }

    // 解锁
    private boolean unLock(RLock lock, String lockKey, long lockStartTime) {
        if (null == lock) {
            return false;
        }
        try {
            lock.unlock();
        } catch (Exception e) {
            return true;
        }
        return true;
    }

    public void setRedisCache(RedisCache redisCache) {
        this.redisCache = redisCache;
    }
}
