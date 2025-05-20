package com.rxjava.core;

/**
 * Функциональный интерфейс, описывающий логику эмиссии элементов.
 *
 * @param <T> тип элементов
 */
@FunctionalInterface
public interface RxOnSubscribe<T> {
    /**
     * Метод, вызывающий при подписке для передачи элементов наблюдателю.
     *
     * @param observer целевой наблюдатель
     */
    void subscribe(RxObserver<? super T> observer);
}

