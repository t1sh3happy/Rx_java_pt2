package com.rxjava.core;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Реализация механизма отмены подписки.
 */
public class RxDisposable {
    private final AtomicBoolean disposed = new AtomicBoolean(false);

    /**
     * Отменяет подписку и прекращает доставку событий.
     */
    public void dispose() {
        disposed.set(true);
    }

    /**
     * Проверяет, отменена ли подписка.
     *
     * @return true, если уже отменено
     */
    public boolean isDisposed() {
        return disposed.get();
    }
}

