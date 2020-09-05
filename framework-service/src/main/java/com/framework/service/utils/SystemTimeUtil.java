package com.framework.service.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class SystemTimeUtil {

    private static final SystemTimeUtil MILLIS_TIME = new SystemTimeUtil(1);
    private final long precision;
    private final AtomicLong now;

    private SystemTimeUtil(long precision) {
        this.precision = precision;
        now = new AtomicLong(System.currentTimeMillis());
        scheduleClockUpdating();
    }

    public static SystemTimeUtil millisTime() {
        return MILLIS_TIME;
    }

    private void scheduleClockUpdating() {
        // 每秒对now进行set
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "system.time");
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleAtFixedRate(() -> now.set(System.currentTimeMillis()), precision, precision, TimeUnit.MILLISECONDS);
    }

    public long now() {
        return now.get();
    }

}
