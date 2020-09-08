package com.framework.service.redis;

import com.framework.common.exception.CommonException;
import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Service
public class RedisCache {
    /*
     * 默认缓存时间
     */
    private static final Long DEFAULT_EXPIRED = 5 * 60L;
    /*
     * redis key前缀
     */
    private static final String REDIS_KEY_PREFIX = "";
    /*
     * redisson client对象
     */
    private RedissonClient redisson;
    /*
     * redis host
     */
    private String host;
    /*
     * redis password
     */
    private String password;
    /*
     * 连接超时时间
     */
    private Integer connectTimeout;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    @PostConstruct
    public void init() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(host)
                .setPassword(password)
                .setDatabase(0)
                .setTimeout(5000)
                .setSubscriptionConnectionMinimumIdleSize(1)
                .setSubscriptionConnectionPoolSize(256)
                .setConnectTimeout(connectTimeout)
                .setConnectionPoolSize(256)
                .setConnectionMinimumIdleSize(1)
                .setRetryAttempts(3)
                .setRetryInterval(3000)
                .setIdleConnectionTimeout(30000)
                .setClientName("RedisCache");
        if (redisson == null) {
            redisson = Redisson.create(config);
        }
    }

    @PreDestroy
    public void close() {
        try {
            if (redisson != null) {
                redisson.shutdown();
            }
        } catch (Exception ex) {
            throw CommonException.SYSTEM_ERROR;
        }
    }

    public <T> T get(String key) {
        RBucket<T> bucket = redisson.getBucket(REDIS_KEY_PREFIX + key);
        return bucket.get();
    }

    public String getString(String key) {
        RBucket<String> bucket = redisson.getBucket(REDIS_KEY_PREFIX + key, StringCodec.INSTANCE);
        return bucket.get();
    }

    // redisson会自动选择序列化反序列化方式
    public <T> void put(String key, T value) {
        RBucket<T> bucket = redisson.getBucket(REDIS_KEY_PREFIX + key);
        bucket.set(value, DEFAULT_EXPIRED, TimeUnit.SECONDS);
    }

    public <T> void put(String key, T value, long expired) {
        RBucket<T> bucket = redisson.getBucket(REDIS_KEY_PREFIX + key);
        bucket.set(value, expired <= 0 ? DEFAULT_EXPIRED : expired, TimeUnit.SECONDS);
    }

    public <T> void put(String key, T value, Codec codec) {
        RBucket<T> bucket = redisson.getBucket(REDIS_KEY_PREFIX + key, codec);
        bucket.set(value, DEFAULT_EXPIRED, TimeUnit.SECONDS);
    }

    public <T> void put(String key, T value, long expired, Codec codec) {
        RBucket<T> bucket = redisson.getBucket(REDIS_KEY_PREFIX + key, codec);
        bucket.set(value, expired, TimeUnit.SECONDS);
    }

    public <T> T get(String key, Codec codec) {
        RBucket<T> bucket = redisson.getBucket(REDIS_KEY_PREFIX + key, codec);
        return bucket.get();
    }

    public void putString(String key, String value) {
        RBucket<String> bucket = redisson.getBucket(REDIS_KEY_PREFIX + key, StringCodec.INSTANCE);
        bucket.set(value, DEFAULT_EXPIRED, TimeUnit.SECONDS);
    }

    public void putString(String key, String value, long expired) {
        RBucket<String> bucket = redisson.getBucket(REDIS_KEY_PREFIX + key, StringCodec.INSTANCE);
        bucket.set(value, expired <= 0 ? DEFAULT_EXPIRED : expired, TimeUnit.SECONDS);
    }

    // 如果不存在则写入缓存（string方式，不带有redisson的格式信息）
    public boolean putStringIfAbsent(String key, String value, long expired) {
        RBucket<String> bucket = redisson.getBucket(REDIS_KEY_PREFIX + key, StringCodec.INSTANCE);
        return bucket.trySet(value, expired <= 0 ? DEFAULT_EXPIRED : expired, TimeUnit.SECONDS);
    }

    // 如果不存在则写入缓存（string方式，不带有redisson的格式信息）（不带过期时间，永久保存）
    public boolean putStringIfAbsent(String key, String value) {
        RBucket<String> bucket = redisson.getBucket(REDIS_KEY_PREFIX + key, StringCodec.INSTANCE);
        return bucket.trySet(value);
    }

    // hash
    public void putMap(String redisKey, String mapKey, String value) {
        ConcurrentMap<String, String> concurrentMap = redisson.getMap(redisKey);
        concurrentMap.put(mapKey, value);
    }

    public ConcurrentMap<String, String> getHashMap(String redisKey) {
        return redisson.getMap(redisKey);
    }

    // 计数器自增（+1），并返回计算前的原值
    // 如果key不存在则按当前值为0计算
    public long getAndIncrement(String key, long expired) {
        RAtomicLong atomicLong = redisson.getAtomicLong(key);
        long num = atomicLong.getAndIncrement();
        atomicLong.expire(expired, TimeUnit.SECONDS);
        return num;
    }

    // 计数器自增（-1），并返回计算前的原值
    public long getAndDecrement(String key) {
        RAtomicLong atomicLong = redisson.getAtomicLong(key);
        return atomicLong.getAndDecrement();
    }

    // 计数器累加指定的值，并返回计算前的原值
    // 如果key不存在则按当前值为0计算
    public long getAndIncrement(String key, long delta, long expired) {
        RAtomicLong atomicLong = redisson.getAtomicLong(key);
        long num = atomicLong.getAndAdd(delta);
        atomicLong.expire(expired, TimeUnit.SECONDS);
        return num;
    }

    public void remove(String key) {
        redisson.getBucket(REDIS_KEY_PREFIX + key).delete();
    }

    public boolean exists(String key) {
        return redisson.getBucket(REDIS_KEY_PREFIX + key).isExists();
    }

    public RLock getRedisLock(String key) {
        return redisson.getLock(REDIS_KEY_PREFIX + key);
    }
}
