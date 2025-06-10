# Simple Java HTTP Server

Легковесный HTTP-сервер на Java с поддержкой основных методов HTTP и настраиваемым пулом потоков.

## Особенности

- Поддерживаемые HTTP-методы: `GET`, `POST`, `PUT`, `PATCH`, `DELETE`
- Маршрутизация запросов с помощью лямбда-обработчиков
- Настраиваемый пул потоков (виртуальные или обычные)
- Базовый парсинг HTTP-заголовков и тела запроса

## Структура проекта

### Основные компоненты системы

#### 1. Класс `Main` (Точка входа)
- **Назначение**: Инициализация сервера и настройка маршрутов
- **Функционал**:
    - Создание экземпляра HTTP-сервера
    - Регистрация обработчиков для различных HTTP-методов
    - Настройка пула потоков
    - Запуск и поддержание работы сервера

#### 2. Класс `HttpServer` (Ядро сервера)
- **Назначение**: Управление жизненным циклом сервера и обработка подключений
- **Ключевые методы**:
    - `start()`: Запуск сервера и начало прослушивания порта
    - `stop()`: Остановка сервера
    - `addRoute()`: Регистрация обработчиков маршрутов
    - `handleConnection()`: Обработка входящих подключений
    - `parseRequest()`: Парсинг сырых HTTP-запросов

#### 3. Класс `HttpRequest` (Представление запроса)
- **Назначение**: Хранение и предоставление данных о входящем запросе
- **Свойства**:
    - HTTP-метод (GET/POST и т.д.)
    - Путь запроса
    - Заголовки
    - Тело запроса
- **Методы доступа**: Геттеры и сеттеры для всех свойств

#### 4. Класс `HttpResponse` (Представление ответа)
- **Назначение**: Формирование и отправка HTTP-ответов
- **Ключевые методы**:
    - `setStatus()`: Установка кода статуса ответа
    - `setHeader()`: Добавление HTTP-заголовков
    - `setBody()`: Установка тела ответа
    - `send()`: Отправка сформированного ответа клиенту

#### 5. Интерфейс `Handler` (Функциональный интерфейс)
- **Назначение**: Определение контракта для обработчиков запросов
- **Метод**:
    - `handle()`: Основной метод обработки запроса

#### 6. Класс `Router` (Маршрутизатор)
- **Назначение**: Управление маршрутами и поиск обработчиков
- **Функционал**:
    - Хранение зарегистрированных маршрутов
    - Быстрый поиск обработчика по методу и пути
    - Внутренний класс `RouteKey`: Ключ для хранения маршрутов

#### 7. Класс `ThreadPool` (Управление потоками)
- **Назначение**: Создание и управление пулом потоков
- **Особенности**:
    - Поддержка виртуальных потоков (JDK 21+)
    - Возможность выбора между фиксированным пулом и виртуальными потоками

#### 8. Перечисление `HttpStatus` (HTTP-статусы)
- **Назначение**: Хранение кодов статусов и их текстовых описаний
- **Поддерживаемые статусы**:
    - 200 OK
    - 404 Not Found

### Взаимодействие компонентов

1. Сервер (`HttpServer`) принимает новое подключение
2. Пул потоков (`ThreadPool`) назначает поток для обработки
3. Парсер преобразует сырые данные в `HttpRequest`
4. Маршрутизатор (`Router`) находит подходящий `Handler`
5. Обработчик формирует `HttpResponse`
6. Ответ отправляется клиенту через канал


## Запуск сервера

1. Сборка проекта.
   В корне проекта выполните:
   ```bash
   mvn clean compile
   ```
   
2. Запуск сервера через плагин exec:
    ```bash
   mvn exec:java
   ```

## Маршруты API
| Метод   | Путь       | Описание                                  | Код ответа |
|:--------|:-----------|:------------------------------------------|:-----------|
| `GET`   | `/hello`   | Пауза 10 секунд, затем «Hello from Server» | `200`      |
| `POST`  | `/data`    | Возвращает получённое тело                | `201`      |
| `PUT`   | `/update`  | Обновление с телом                        | `200`      |
| `PATCH` | `/patch`   | Частичное обновление с телом              | `200`      |
| `DELETE`| `/delete`  | Удаление (пустой ответ)                   | `204`      |

## Примеры запросов

Ниже примеры отправки запросов к серверу через `curl` (Linux/macOS) и `Invoke-WebRequest` (Windows PowerShell).

### GET `/hello`

**Linux (curl):**
```bash
curl http://localhost/hello
```

**Windows PowerShell:**
```bash
Invoke-WebRequest -Uri http://localhost/hello -Method GET
```

### POST `/data`
**Linux (curl):**
```bash
curl -X POST http://localhost/data \
     -H "Content-Type: text/plain" \
     -d "some data here"
```

**Windows PowerShell:**

```bash
Invoke-WebRequest -Uri http://localhost/data `
  -Method POST `
  -Headers @{ 'Content-Type' = 'text/plain' } `
  -Body 'some data here'
```

### PUT `/update`
**Linux (curl):**

```bash
curl -X PUT http://localhost/update \
     -H "Content-Type: application/json" \
     -d '{"id":1,"name":"Alice"}'
```

**Windows PowerShell:**
```bash
Invoke-WebRequest -Uri http://localhost/update `
  -Method PUT `
  -Headers @{ 'Content-Type' = 'application/json' } `
  -Body '{"id":1,"name":"Alice"}'
```

### PATCH `/patch`
**Linux (curl):**

```bash
curl -X PATCH http://localhost/patch \
     -H "Content-Type: application/json" \
     -d '{"name":"Alexey"}'
```

**Windows PowerShell:**
```bash
Invoke-WebRequest -Uri http://localhost/patch `
  -Method PATCH `
  -Headers @{ 'Content-Type' = 'application/json' } `
  -Body '{"name":"Alexey"}'
```


### DELETE `/delete`
**Linux (curl):**

```bash
curl -X DELETE http://localhost/delete -v
```

**Windows PowerShell:**
```bash
Invoke-WebRequest -Uri http://localhost/delete -Method DELETE -Verbose
```
