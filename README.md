КАСТОМНАЯ РЕАЛИЗАЦИЯ РЕАКТИВНОГО ПРОГРАММИРОВАНИЯ НА JAVA

Этот проект представляет собой учебную реализацию основных концепций реактивного программирования, аналогичных RxJava. Он демонстрирует работу реактивных потоков данных с поддержкой многопоточности и операторов преобразования.

Ключевые возможности

- Реактивные потоки: Реализация паттерна Observable-Observer.
- Операторы преобразования: Поддержка map, filter, flatMap и других операторов.
- Многопоточность: Поддержка различных стратегий планирования.
- Управление ресурсами: Механизмы отмены подписок.

Технологии

- Java 17+
- Maven (для сборки)
- SLF4J + Log4j (для логирования)
- JUnit 5 (для тестирования)

Установка и запуск

1. Клонируйте репозиторий:
git clone https://github.com/t1sh3happy/Rxjavapt2.git
    cd RxJavaWork


2. Соберите проект и запустите тесты:
mvn clean test


3. Запустите демонстрацию:
mvn exec:java -Dexec.mainClass="com.rxjavawork.Main"


Структура проекта
rxjavawork/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── rxjavawork/
│   │   │           ├── core/
│   │   │           │   ├── RxObservable.java
│   │   │           │   ├── RxObserver.java
│   │   │           │   ├── RxOnSubscribe.java
│   │   │           │   ├── RxDisposable.java
│   │   │           │   └── RxCompositeDisposable.java
│   │   │           ├── operators/
│   │   │           │   ├── MapOperator.java
│   │   │           │   ├── FilterOperator.java
│   │   │           │   ├── FlatMapOperator.java
│   │   │           │   ├── MergeOperator.java
│   │   │           │   ├── ConcatOperator.java
│   │   │           │   └── ReduceOperator.java
│   │   │           ├── schedulers/
│   │   │           │   ├── RxScheduler.java
│   │   │           │   ├── RxIOScheduler.java
│   │   │           │   ├── RxComputationScheduler.java
│   │   │           │   └── RxSingleScheduler.java
│   │   │           └── Main.java
│   │   └── resources/
│   │       └── log4j.properties
│   └── test/
│       └── java/
│           └── com/
│               └── rxjavawork/
│                   ├── core/
│                   │   └── RxObservableTest.java
│                   ├── operators/
│                   │   └── OperatorTest.java
│                   └── schedulers/
│                       └── SchedulerTest.java


Основные компоненты

RxObservable

Источник данных с фабричными методами:

- create() - создание кастомного Observable.
- just() - создание из одного или нескольких значений.

Операторы

- Преобразование: MapOperator, FilterOperator.
- Комбинирование: FlatMapOperator, MergeOperator, ConcatOperator.
- Агрегация: ReduceOperator.

Планировщики

- RxIOScheduler - для I/O операций (cached thread pool).
- RxComputationScheduler - для вычислений (fixed thread pool).
- RxSingleScheduler - для последовательного выполнения (single thread).

Примеры использования

Базовый пример
RxObservable.just(1, 2, 3, 4, 5)
    .subscribe(
        i -> System.out.println("Получено: " + i),
        Throwable::printStackTrace,
        () -> System.out.println("Завершено")
    );


Цепочка операторов
MapOperator.apply(
    FilterOperator.apply(
        RxObservable.just(10, 20, 30, 40),
        x -> x > 20
    ),
    x -> x * 2
).subscribe(System.out::println);


Работа с планировщиками
RxObservable.just("data")
    .subscribeOn(new RxIOScheduler())
    .observeOn(new RxSingleScheduler())
    .subscribe(
        item -> System.out.println(Thread.currentThread().getName()),
        Throwable::printStackTrace
    );


Тестирование

Проект включает в себя комплекс тестов:
- Базовые сценарии работы Observable
- Проверка всех операторов
- Тесты многопоточности
- Проверка обработки ошибок
