package com.rxjava.operators;

import com.rxjava.core.RxObservable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class OperatorTest {

    @Test
    void mapOperator_shouldTransformItems() {
        // Подготовка: создаем исходный поток с числами 1, 2, 3
        RxObservable<Integer> source = RxObservable.just(1, 2, 3);
        List<Integer> result = new ArrayList<>();

        // Действие: применяем оператор map для умножения каждого элемента на 2
        RxObservable<Integer> mapped = MapOperator.apply(source, x -> x * 2);
        mapped.subscribe(result::add);

        // Проверка: убеждаемся, что все элементы были преобразованы правильно
        assertEquals(List.of(2, 4, 6), result);
    }

    @Test
    void filterOperator_shouldFilterItems() {
        // Подготовка: создаем поток с числами от 10 до 50
        RxObservable<Integer> source = RxObservable.just(10, 20, 30, 40, 50);
        List<Integer> result = new ArrayList<>();

        // Действие: фильтруем элементы, оставляя только больше 25
        RxObservable<Integer> filtered = FilterOperator.apply(source, x -> x > 25);
        filtered.subscribe(result::add);

        // Проверка: убеждаемся, что отфильтрованы правильные элементы
        assertEquals(List.of(30, 40, 50), result);
    }

    @Test
    void mapAndFilter_shouldWorkTogether() {
        // Подготовка: исходный поток чисел
        RxObservable<Integer> source = RxObservable.just(1, 2, 3, 4, 5);
        List<Integer> result = new ArrayList<>();

        // Действие: сначала умножаем на 10, затем фильтруем кратные 20
        RxObservable<Integer> processed = FilterOperator.apply(
                MapOperator.apply(source, x -> x * 10),
                x -> x % 20 == 0
        );
        processed.subscribe(result::add);

        // Проверка: убеждаемся в правильности совместной работы операторов
        assertEquals(List.of(20, 40), result);
    }

    @Test
    void reduceOperator_shouldAggregateValues() {
        // Подготовка: поток чисел для суммирования
        RxObservable<Integer> source = RxObservable.just(1, 2, 3, 4);
        List<Integer> result = new ArrayList<>();

        // Действие: применяем оператор reduce для суммирования
        RxObservable<Integer> reduced = ReduceOperator.apply(source, (acc, x) -> acc + x);
        reduced.subscribe(result::add);

        // Проверка: убеждаемся, что сумма вычислена верно
        assertEquals(1, result.size());
        assertEquals(10, result.get(0));
    }

    @Test
    void mergeOperator_shouldCombineMultipleStreams() {
        // Подготовка: два разных потока данных
        RxObservable<String> stream1 = RxObservable.just("A", "B");
        RxObservable<String> stream2 = RxObservable.just("1", "2");
        List<String> result = new ArrayList<>();

        // Действие: объединяем потоки с помощью merge
        RxObservable<String> merged = MergeOperator.apply(stream1, stream2);
        merged.subscribe(result::add);

        // Проверка: все элементы из обоих потоков присутствуют
        assertEquals(4, result.size());
        assertTrue(result.containsAll(List.of("A", "B", "1", "2")));
    }

    @Test
    void concatOperator_shouldPreserveOrder() {
        // Подготовка: два потока для последовательного соединения
        RxObservable<String> first = RxObservable.just("X", "Y");
        RxObservable<String> second = RxObservable.just("Z");
        List<String> result = new ArrayList<>();

        // Действие: соединяем потоки с сохранением порядка
        RxObservable<String> concatenated = ConcatOperator.apply(first, second);
        concatenated.subscribe(result::add);

        // Проверка: порядок элементов соответствует ожидаемому
        assertEquals(List.of("X", "Y", "Z"), result);
    }

    @Test
    void flatMapOperator_shouldFlattenStreams() {
        // Подготовка: исходный поток чисел
        RxObservable<Integer> source = RxObservable.just(1, 2, 3);
        List<Integer> result = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(); // Счетчик элементов

        // Действие: для каждого элемента создаем новый поток (x, x*10)
        RxObservable<Integer> flatMapped = FlatMapOperator.apply(
                source,
                x -> RxObservable.just(x, x * 10)
        );
        flatMapped.subscribe(
                item -> {
                    result.add(item);
                    counter.incrementAndGet();
                }
        );

        // Проверка: все сгенерированные элементы присутствуют
        assertEquals(6, counter.get());
        assertTrue(result.containsAll(List.of(1, 10, 2, 20, 3, 30)));
    }

    @Test
    void emptyStream_shouldWorkWithOperators() {
        // Подготовка: создаем пустой поток
        RxObservable<Integer> empty = RxObservable.create(o -> o.onComplete());
        List<Integer> result = new ArrayList<>();

        // Действие: применяем цепочку операторов к пустому потоку
        RxObservable<Integer> processed = MapOperator.apply(
                FilterOperator.apply(empty, x -> true),
                x -> x * 2
        );
        processed.subscribe(result::add);

        // Проверка: результат должен быть пустым
        assertTrue(result.isEmpty());
    }
}
