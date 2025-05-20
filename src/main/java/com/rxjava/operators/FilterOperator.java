package com.rxjava.operators;

import com.rxjava.core.RxObservable;
import com.rxjava.core.RxObserver;
import com.rxjava.core.RxDisposable;

import java.util.function.Predicate;

/**
 * Оператор filter: пропускает только те элементы, которые удовлетворяют предикату.
 */
public class FilterOperator {
    public static <T> RxObservable<T> apply(
            RxObservable<T> source,
            Predicate<? super T> predicate
    ) {
        return RxObservable.create(observer -> {
            RxDisposable disp = source.subscribe(new RxObserver<T>() {
                @Override
                public void onNext(T item) {
                    if (predicate.test(item)) {
                        observer.onNext(item);
                    }
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

