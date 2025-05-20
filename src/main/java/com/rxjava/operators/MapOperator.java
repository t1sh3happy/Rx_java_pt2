package com.rxjava.operators;

import com.rxjava.core.RxObservable;
import com.rxjava.core.RxObserver;
import com.rxjava.core.RxDisposable;

import java.util.function.Function;

/**
 * Оператор map: применяет функцию к каждому элементу потока.
 */
public class MapOperator {
    public static <T, R> RxObservable<R> apply(
            RxObservable<T> source,
            Function<? super T, ? extends R> mapper
    ) {
        return RxObservable.create(observer -> {
            RxDisposable disp = source.subscribe(new RxObserver<T>() {
                @Override
                public void onNext(T item) {
                    observer.onNext(mapper.apply(item));
                }
                @Override
                public void onError(Throwable t) {
                    observer.onError(t);
                }
                @Override
                public void onComplete() {
                    observer.onComplete();
                }
            });
        });
    }
}

