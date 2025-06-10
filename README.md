# Java HTTP Server & JSON Parser

Общий репозиторий лабораторных работ по разработке многопоточного сервера и парсинга JSON на Java.

## Лабораторные работы

### 1️⃣ JSON Parser

Java-библиотека для сериализации и десериализации объектов в формат JSON без сторонних зависимостей.

- Поддержка примитивов, строк, коллекций, вложенных объектов
- Использует Reflection API
- Промежуточное представление:
  - `Map<String, Object>` для JSON Object
  - `List<Object>` для JSON Array

➡️ Подробнее: [`JsonParser/README.md`](json-parser/README.md)

### 2️⃣ Simple Java HTTP Server

Простой HTTP-сервер с поддержкой базовых HTTP-методов и настраиваемым пулом потоков.

- Поддерживаемые методы: `GET`, `POST`, `PUT`, `PATCH`, `DELETE`
- Настраиваемый пул потоков: виртуальные или классические
- Простая маршрутизация и парсинг HTTP-запросов
- Примеры API-запросов (curl/PowerShell)

➡️ Подробнее: [`http-server/README.md`](http-server/README.md)

### 3️⃣ Нагрузочное тестирование HTTP-сервера

Нагрузочное тестирование HTTP-сервера с использованием Apache JMeter.

- Два типа POST-запросов:
  - Сохранение JSON в файл + чтение
  - Вычисления над данными и возврат JSON-ответа
- Возможность выбора между:
  - собственной реализацией `JsonParser` и библиотекой `Jackson`
  - виртуальными и классическими потоками
- Инструкция по созданию тестов в JMeter и анализ результатов

➡️ Подробнее: [`load-testing/README.md`](load-testing/README.md)

## Требования

- Java 21+
- Maven 3.9+
- Apache JMeter 5.6+ (для лабораторной №3)

## Сборка проектов

```bash

cd JsonParser
mvn clean install

cd ../HttpServer
mvn clean install

cd ../LoadTesting
mvn clean install

```

## Запуск `Main`-класса

```bash
mvn exec:java
```
