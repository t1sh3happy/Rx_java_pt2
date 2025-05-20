// src/main/java/com/rxjavawork/core/RxCompositeDisposable.java
package com.rxjava.core;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CompositeDisposable для групповой отмены нескольких подписок.
 */
public class RxCompositeDisposable {
    private final Set<RxDisposable> disposables = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * Добавляет Disposable в группу.
     *
     * @param d Disposable для добавления
     */
    public void add(RxDisposable d) {
        disposables.add(d);
    }

    /**
     * Удаляет Disposable из группы.
     *
     * @param d Disposable для удаления
     */
    public void remove(RxDisposable d) {
        disposables.remove(d);
    }

    /**
     * Отменяет все подписки в группе.
     */
    public void dispose() {
        for (RxDisposable d : disposables) {
            d.dispose();
        }
        disposables.clear();
    }

    /**
     * Проверяет, отменены ли все подписки.
     *
     * @return true, если все подписки отменены
     */
    public boolean isDisposed() {
        return disposables.stream().allMatch(RxDisposable::isDisposed);
    }
}

