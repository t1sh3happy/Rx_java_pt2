package com.rxjava.core;

import com.rxjava.schedulers.RxScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Основной класс реактивного потока.
 *
 * @param <T> тип элементов
 */
public class RxObservable<T> {
    private static final Logger log = LoggerFactory.getLogger(RxObservable.class);

    private final RxOnSubscribe<T> source;

    private RxObservable(RxOnSubscribe<T> source) {
        this.source = source;
    }

    /**
     * Фабричный метод для создания холодного Observable.
     *
     * @param source логика эмиссии элементов
     * @param <T>    тип элементов
     * @return новый RxObservable
     */
    public static <T> RxObservable<T> create(RxOnSubscribe<T> source) {
        log.debug("Создание RxObservable via create()");
        return new RxObservable<>(source);
    }

    /**
     * Фабричный метод для единичного эмиттера.
     *
     * @param item элемент
     * @param <T>  тип элемента
     * @return Observable, эмитирующий один элемент и завершающийся
     */
    public static <T> RxObservable<T> just(T item) {
        return create(observer -> {
            observer.onNext(item);
            observer.onComplete();
        });
    }

    /**
     * Создаёт Observable, который эмитит переданные элементы и сразу завершает поток.
     *
     * @param items элементы для эмиссии
     * @param <T>   тип элементов
     * @return новый RxObservable
     */
    @SafeVarargs
    public static <T> RxObservable<T> just(T... items) {
        return create(observer -> {
            Arrays.stream(items).forEach(observer::onNext);
            observer.onComplete();
        });
    }

    /**
     * Подписка с полным набором обработчиков.
     *
     * @param onNext     действие при новом элементе
     * @param onError    действие при ошибке
     * @param onComplete действие при завершении
     * @return RxDisposable для отмены подписки
     */
    public RxDisposable subscribe(
            Consumer<? super T> onNext,
            Consumer<Throwable> onError,
            Runnable onComplete
    ) {
        RxObserver<T> obs = new RxObserver<T>() {
            @Override public void onNext(T item)     { onNext.accept(item); }
            @Override public void onError(Throwable t) { onError.accept(t); }
            @Override public void onComplete()       { onComplete.run(); }
        };
        return subscribe(obs);
    }

    /**
     * Подписка с обработчиком onNext.
     *
     * @param onNext действие при новом элементе
     * @return RxDisposable для отмены подписки
     */
    public RxDisposable subscribe(Consumer<? super T> onNext) {
        return subscribe(onNext, Throwable::printStackTrace, () -> {});
    }

    /**
     * Базовый subscribe, возвращает Disposable.
     *
     * @param observer наблюдатель
     * @return RxDisposable для отмены подписки
     */
    public RxDisposable subscribe(RxObserver<? super T> observer) {
        log.debug("Новая подписка на RxObservable");
        RxDisposable disposable = new RxDisposable();
        try {
            source.subscribe(new RxObserver<T>() {
                @Override
                public void onNext(T item) {
                    if (!disposable.isDisposed()) {
                        observer.onNext(item);
                    }
                }
                @Override
                public void onError(Throwable t) {
                    if (!disposable.isDisposed()) {
                        observer.onError(t);
                    }
                }
                @Override
                public void onComplete() {
                    if (!disposable.isDisposed()) {
                        observer.onComplete();
                    }
                }
            });
        } catch (Throwable t) {
            observer.onError(t);
        }
        return disposable;
    }

    /**
     * Подписка выполняется в указанном планировщике.
     *
     * @param scheduler планировщик для вызова source.subscribe
     * @return новый Observable, подписка которого отложена на scheduler
     */
    public RxObservable<T> subscribeOn(RxScheduler scheduler) {
        return RxObservable.create(observer ->
                scheduler.schedule(() -> this.subscribe(observer))
        );
    }

    /**
     * Эмиссия onNext/onError/onComplete происходит в указанном планировщике.
     *
     * @param scheduler планировщик для обработки событий
     * @return новый Observable, события которого переключаются на scheduler
     */
    public RxObservable<T> observeOn(RxScheduler scheduler) {
        return RxObservable.create(observer ->
                this.subscribe(new RxObserver<T>() {
                    @Override
                    public void onNext(T item) {
                        scheduler.schedule(() -> observer.onNext(item));
                    }
                    @Override
                    public void onError(Throwable t) {
                        scheduler.schedule(() -> observer.onError(t));
                    }
                    @Override
                    public void onComplete() {
                        scheduler.schedule(observer::onComplete);
                    }
                })
        );
    }
}