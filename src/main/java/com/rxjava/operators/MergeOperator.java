package com.rxjava.operators;

import com.rxjava.core.RxCompositeDisposable;
import com.rxjava.core.RxDisposable;
import com.rxjava.core.RxObservable;
import com.rxjava.core.RxObserver;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Оператор merge: параллельно сливает несколько Observable в один поток.
 */
public class MergeOperator {

    /**
     * @param sources массив Observable-источников
     * @param <T>     тип элементов
     * @return новый RxObservable<T>, эмитирующий все элементы sources
     */
    @SafeVarargs
    public static <T> RxObservable<T> apply(RxObservable<? extends T>... sources) {
        return RxObservable.create(observer -> {
            RxCompositeDisposable composite = new RxCompositeDisposable();
            AtomicInteger remaining = new AtomicInteger(sources.length);
            ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

            for (RxObservable<? extends T> src : sources) {
                RxDisposable disp = src.subscribe(new RxObserver<T>() {
                    @Override
                    public void onNext(T item) {
                        observer.onNext(item);
                    }

                    @Override
                    public void onError(Throwable t) {
                        errors.add(t);
                        completeIfDone();
                    }

                    @Override
                    public void onComplete() {
                        completeIfDone();
                    }

                    private void completeIfDone() {
                        if (remaining.decrementAndGet() == 0) {
                            Throwable err = errors.poll();
                            if (err != null) {
                                observer.onError(err);
                            } else {
                                observer.onComplete();
                            }
                            composite.dispose();
                        }
                    }
                });
                composite.add(disp);
            }
        });
    }
}
