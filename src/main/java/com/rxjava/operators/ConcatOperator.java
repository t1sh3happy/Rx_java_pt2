// src/main/java/com/rxjavawork/operators/ConcatOperator.java
package com.rxjava.operators;

import com.rxjava.core.RxObservable;
import com.rxjava.core.RxObserver;

/**
 * Оператор concat: последовательная конкатенация двух Observable.
 */
public class ConcatOperator {
    public static <T> RxObservable<T> apply(
            RxObservable<? extends T> first,
            RxObservable<? extends T> second
    ) {
        return RxObservable.create(observer -> {
            first.subscribe(new RxObserver<T>() {
                @Override public void onNext(T item) { observer.onNext(item); }
                @Override public void onError(Throwable t) { observer.onError(t); }
                @Override public void onComplete() {
                    second.subscribe(observer);
                }
            });
        });
    }
}

