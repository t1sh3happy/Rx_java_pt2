package com.rxjava.schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Планировщик единственного потока.
 */
public class RxSingleScheduler implements RxScheduler {
    private static final ExecutorService EXEC = Executors.newSingleThreadExecutor();

    @Override
    public void schedule(Runnable task) {
        EXEC.submit(task);
    }
}

