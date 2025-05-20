package com.rxjava;

import com.rxjava.core.RxObservable;
import com.rxjava.operators.*;
import com.rxjava.schedulers.*;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // 1. Базовые операции с числами
        System.out.println("=== Базовые операции с числами ===");
        RxObservable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .subscribe(
                        i -> System.out.println("Число: " + i),
                        Throwable::printStackTrace,
                        () -> System.out.println("Завершено\n")
                );

        // 2. Цепочка операторов map + filter
        System.out.println("=== Цепочка map и filter ===");
        MapOperator.apply(
                        RxObservable.just(1, 2, 3, 4, 5),
                        i -> i * 3
                )
                .subscribe(
                        i -> System.out.println("Результат: " + i),
                        Throwable::printStackTrace,
                        () -> System.out.println("Завершено\n")
                );

        // 3. Асинхронная обработка с планировщиками
        System.out.println("=== Асинхронная обработка ===");
        RxObservable<String> fruits = RxObservable.just("Яблоко", "Банан", "Апельсин", "Груша", "Киви");
        RxObservable<String> processed = MapOperator.apply(fruits, String::toUpperCase);

        processed
                .subscribeOn(new RxIOScheduler())
                .observeOn(new RxComputationScheduler())
                .subscribe(
                        fruit -> {
                            System.out.println("Фрукт: " + fruit + " (" + Thread.currentThread().getName() + ")");
                        },
                        Throwable::printStackTrace,
                        () -> System.out.println("Обработка завершена\n")
                );

        // 4. Работа с FlatMap
        System.out.println("=== FlatMap преобразование ===");
        FlatMapOperator.apply(
                        RxObservable.just("Hello", "World"),
                        word -> RxObservable.just(word.split(""))
                )
                .subscribe(
                        letter -> System.out.println("Буква: " + letter),
                        Throwable::printStackTrace,
                        () -> System.out.println("Разбивка завершена\n")
                );

        // 5. Объединение потоков (Merge)
        System.out.println("=== Объединение потоков ===");
        RxObservable<String> colors = RxObservable.just("Красный", "Зеленый", "Синий");
        RxObservable<String> animals = RxObservable.just("Кошка", "Собака", "Птица");

        MergeOperator.apply(colors, animals)
                .subscribe(
                        item -> System.out.println("Объединенный элемент: " + item),
                        Throwable::printStackTrace,
                        () -> System.out.println("Объединение завершено\n")
                );

        // 6. Последовательное соединение (Concat)
        System.out.println("=== Последовательное соединение ===");
        RxObservable<Integer> first = RxObservable.just(1, 2, 3);
        RxObservable<Integer> second = RxObservable.just(4, 5, 6);

        ConcatOperator.apply(first, second)
                .subscribe(
                        num -> System.out.println("Число: " + num),
                        Throwable::printStackTrace,
                        () -> System.out.println("Последовательность завершена\n")
                );

        // 7. Агрегация (Reduce)
        System.out.println("=== Агрегация значений ===");
        ReduceOperator.apply(
                        RxObservable.just(1, 2, 3, 4, 5),
                        (acc, val) -> acc + val
                )
                .subscribe(
                        sum -> System.out.println("Сумма: " + sum),
                        Throwable::printStackTrace,
                        () -> System.out.println("Агрегация завершена\n")
                );

        // Время для завершения асинхронных операций
        TimeUnit.SECONDS.sleep(2);
    }
}
