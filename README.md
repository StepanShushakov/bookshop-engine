# Bookshop Engine

Bookshop Engine – это движок для управления интернет-магазином книг. Данный проект предоставляет возможности для работы с каталогом книг, профилем пользователя, корзиной покупок, жанрами, авторами, и многим другим. В проект включены абстрактные данные, позволяющие протестировать функциональность движка и увидеть примеры его работы.

## Возможности движка

- **Управление каталогом книг**: просмотр, поиск и фильтрация книг.
- **Работа с корзиной**: добавление книг в корзину и оформление заказа.
- **Профиль пользователя**: управление персональной информацией.
- **Авторизация и регистрация**: поддержка создания учетной записи и входа в систему.
- **Жанры и авторы**: организация книг по жанрам, отображение информации об авторах.
- **API для взаимодействия с книгами**: возможность интеграции с другими сервисами для получения данных о книгах.
- **Платежи**: функционал для проведения транзакций.
- **Административные функции**: управление каталогом, профилями и контентом.

## Данные

В проект загружены абстрактные данные для примера работы. Они добавлены через механизм миграций с помощью **Liquibase**, который также управляет созданием всех необходимых таблиц в базе данных. Данные носят демонстрационный характер и используются для тестирования движка.

## Использованные технологии

- **Java 17** – основной язык программирования проекта.
- **Spring Boot** – фреймворк для разработки бэкенда.
- **Maven** – инструмент для управления зависимостями и сборки проекта.
- **Docker** – для контейнеризации, используется `docker-compose` для разворачивания базы данных и связанных служб.
- **Thymeleaf** – шаблонизатор для генерации HTML-страниц, используемых в интерфейсе приложения.
- **Liquibase** – управление миграциями базы данных и загрузка демонстрационных данных.
- **PostgreSQL** – реляционная база данных для хранения информации о книгах, пользователях и транзакциях.
- **Selenium** – тестирование интерфейса и функциональных возможностей.
- **JUnit** – для написания и запуска модульных тестов.
- **Spring Boot Actuator** – предоставляет информацию о состоянии приложения и метрики через `/actuator`.
- **Spring Boot Admin** – инструмент для мониторинга, используемый для отслеживания состояния приложения.

## Быстрый старт

1. Склонируйте репозиторий:
   ```bash
   git clone https://github.com/StepanShushakov/bookshop-engine.git
2. Запустите базу данных и связанные сервисы в Docker:
   ```bash
   docker-compose up -d
3. Соберите проект с помощью Maven:
   ```bash
   mvn clean install
4. Запустите проект:
   ```bash
   mvn spring-boot:run
Проект будет доступен по адресу http://localhost:8085.

## Администрирование

- **Actuator** предоставляет доступ к метрикам, статусу системы и другим диагностическим данным через эндпоинты `/actuator`.
- **Spring Boot Admin** позволяет отслеживать состояние и логи приложения. Клиент Spring Boot Admin настроен для подключения к серверу мониторинга.

## Структура проекта

- `src/main/java/com/example/mybookshopapp/` – исходный код проекта
- `src/test/` – тесты, включая данные для тестирования
- `docker/` – конфигурации Docker и инициализация базы данных