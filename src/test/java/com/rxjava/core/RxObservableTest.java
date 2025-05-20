package com.rxjava.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class RxObservableTest {

    @Test
    void create_shouldEmitItemsAndComplete() {
        // Аранжировка
        List<String> received = new ArrayList<>();
        AtomicBoolean completed = new AtomicBoolean(false);

        // Действие
        RxObservable<String> source = RxObservable.create(observer -> {
            observer.onNext("A");
            observer.onNext("B");
            observer.onComplete();
        });

        RxDisposable disposable = source.subscribe(
                received::add,
                error -> fail("Unexpected error"),
                () -> completed.set(true)
        );

        // Проверка предаоложений
        assertFalse(disposable.isDisposed());
        assertEquals(List.of("A", "B"), received);
        assertTrue(completed.get());
    }

    @Test
    void just_shouldEmitSingleItemAndComplete() {
        // Аранжировка
        List<Integer> result = new ArrayList<>();
        AtomicBoolean completed = new AtomicBoolean(false);

        // Действие
        RxDisposable disposable = RxObservable.just(42).subscribe(
                result::add,
                error -> fail("Unexpected error"),
                () -> completed.set(true)
        );

        // Проверка предаоложений
        assertFalse(disposable.isDisposed());
        assertEquals(1, result.size());
        assertEquals(42, result.get(0));
        assertTrue(completed.get());
    }

    @Test
    void just_withVarargs_shouldEmitAllItemsAndComplete() {
        // Аранжировка
        List<String> items = new ArrayList<>();

        // Действие
        RxDisposable disposable = RxObservable.just("X", "Y", "Z").subscribe(
                items::add,
                error -> fail("Unexpected error"),
                () -> {
                }
        );

        // Проверка предаоложений
        assertFalse(disposable.isDisposed());
        assertEquals(List.of("X", "Y", "Z"), items);
    }

    @Test
    void errorHandling_shouldPropagateErrors() {
        // Аранжировка
        AtomicReference<Throwable> receivedError = new AtomicReference<>();
        RuntimeException testError = new RuntimeException("Test error");

        // Действие
        RxDisposable disposable = RxObservable.create(observer -> {
            observer.onError(testError);
        }).subscribe(
                item -> fail("Should not receive items"),
                receivedError::set,
                () -> fail("Should not complete")
        );

        // Проверка предаоложений
        assertFalse(disposable.isDisposed());
        assertNotNull(receivedError.get());
        assertEquals(testError, receivedError.get());
    }
}
