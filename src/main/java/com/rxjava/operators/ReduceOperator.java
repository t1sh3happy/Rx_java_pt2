// src/main/java/com/rxjavawork/operators/ReduceOperator.java
package com.rxjava.operators;

import com.rxjava.core.RxObservable;
import com.rxjava.core.RxObserver;
import com.rxjava.core.RxDisposable;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

/**
 * Оператор reduce: аккумуляция элементов в одно итоговое значение.
 */
public class ReduceOperator {
    public static <T> RxObservable<T> apply(
            RxObservable<T> source,
            BiFunction<? super T, ? super T, ? extends T> accumulator
    ) {
        return RxObservable.create(observer -> {
            AtomicReference<T> acc = new AtomicReference<>();
            RxDisposable disp = source.subscribe(new RxObserver<T>() {
                @Override
                public void onNext(T item) {
                    if (acc.get() == null) {
                        acc.set(item);
                    } else {
                        acc.set(accumulator.apply(acc.get(), item));
                    }
                }
                @Override
                public void onError(Throwable t) {
                    observer.onError(t);
                }
                @Override
                public void onComplete() {
                    T result = acc.get();
                    if (result != null) {
                        observer.onNext(result);
                    }
                    observer.onComplete();
                }
            });
        });
    }
}

