package com.rxjava.schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Планировщик для I/O-потоков (cached thread pool).
 */
public class RxIOScheduler implements RxScheduler {
    private static final ExecutorService EXEC = Executors.newCachedThreadPool();

    @Override
    public void schedule(Runnable task) {
        EXEC.submit(task);
    }
}

