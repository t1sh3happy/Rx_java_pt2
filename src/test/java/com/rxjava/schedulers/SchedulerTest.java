package com.rxjava.schedulers;

import com.rxjava.core.RxObservable;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class SchedulerTest {

    @Test
    void ioScheduler_shouldExecuteOnSeparateThread() throws InterruptedException {
        // Подготовка
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> executionThreadName = new AtomicReference<>();

        // Действие: запускаем задачу в IO-планировщике
        RxObservable.just("тест")
                .subscribeOn(new RxIOScheduler())
                .subscribe(item -> {
                    executionThreadName.set(Thread.currentThread().getName());
                    latch.countDown();
                });

        // Ожидаем завершения асинхронной операции
        assertTrue(latch.await(1, TimeUnit.SECONDS), "Таймаут ожидания выполнения");

        // Проверка
        assertNotNull(executionThreadName.get(), "Не записалось имя потока");
        assertNotEquals(Thread.currentThread().getName(), executionThreadName.get(),
                "Должен выполняться в другом потоке");
        assertTrue(executionThreadName.get().contains("pool"),
                "Должен выполняться в пуле потоков");
    }

    @Test
    void singleScheduler_shouldExecuteSequentially() throws InterruptedException {
        // Подготовка
        CountDownLatch latch = new CountDownLatch(2);
        AtomicReference<String> firstThreadName = new AtomicReference<>();
        AtomicReference<String> secondThreadName = new AtomicReference<>();

        // Действие: запускаем две задачи в SingleScheduler
        RxObservable.just("первый")
                .observeOn(new RxSingleScheduler())
                .subscribe(item -> {
                    firstThreadName.set(Thread.currentThread().getName());
                    latch.countDown();
                });

        RxObservable.just("второй")
                .observeOn(new RxSingleScheduler())
                .subscribe(item -> {
                    secondThreadName.set(Thread.currentThread().getName());
                    latch.countDown();
                });

        // Ожидаем завершения обеих задач
        assertTrue(latch.await(1, TimeUnit.SECONDS), "Таймаут ожидания выполнения");

        // Проверка
        assertNotNull(firstThreadName.get(), "Не записалось имя первого потока");
        assertNotNull(secondThreadName.get(), "Не записалось имя второго потока");
        assertEquals(firstThreadName.get(), secondThreadName.get(),
                "Обе задачи должны выполняться в одном потоке");
    }

    @Test
    void computationScheduler_shouldUseMultipleThreads() throws InterruptedException {
        // Подготовка
        CountDownLatch latch = new CountDownLatch(2);
        AtomicReference<String> firstThreadName = new AtomicReference<>();
        AtomicReference<String> secondThreadName = new AtomicReference<>();

        // Действие: запускаем две задачи в ComputationScheduler
        RxObservable.just(1)
                .subscribeOn(new RxComputationScheduler())
                .subscribe(item -> {
                    firstThreadName.set(Thread.currentThread().getName());
                    latch.countDown();
                });

        RxObservable.just(2)
                .subscribeOn(new RxComputationScheduler())
                .subscribe(item -> {
                    secondThreadName.set(Thread.currentThread().getName());
                    latch.countDown();
                });

        // Ожидаем завершения
        assertTrue(latch.await(1, TimeUnit.SECONDS), "Таймаут ожидания выполнения");

        // Проверка
        assertNotNull(firstThreadName.get(), "Не записалось имя первого потока");
        assertNotNull(secondThreadName.get(), "Не записалось имя второго потока");
        assertNotEquals(firstThreadName.get(), secondThreadName.get(),
                "Задачи должны выполняться в разных потоках пула");
    }

    @Test
    void observeOn_shouldSwitchThreadForEvents() throws InterruptedException {
        // Подготовка
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> emitThreadName = new AtomicReference<>();
        AtomicReference<String> receiveThreadName = new AtomicReference<>();

        // Действие: эмитируем в одном потоке, получаем в другом
        RxObservable.create(observer -> {
                    emitThreadName.set(Thread.currentThread().getName());
                    observer.onNext("данные");
                    observer.onComplete();
                    latch.countDown();
                })
                .observeOn(new RxSingleScheduler())
                .subscribe(item -> {
                    receiveThreadName.set(Thread.currentThread().getName());
                });

        // Ожидаем завершения
        assertTrue(latch.await(1, TimeUnit.SECONDS), "Таймаут ожидания выполнения");

        // Проверка
        assertNotNull(emitThreadName.get(), "Не записалось имя потока эмиттера");
        assertNotNull(receiveThreadName.get(), "Не записалось имя потока получателя");
        assertNotEquals(emitThreadName.get(), receiveThreadName.get(),
                "Эмиссия и обработка должны быть в разных потоках");
    }
}
