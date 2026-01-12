# Система управления бронированием отелей (Microservices Architecture)
## Описание проекта
Микросервисное приложение для управления отелями и бронированиями, реализующее распределенную транзакцию с компенсирующими действиями. Система обеспечивает равномерное распределение нагрузки по номерам и идемпотентную обработку запросов.

## Архитектура
### Компоненты системы
- Eureka Server (порт 8761) - Service Discovery сервер
- API Gateway (порт 8080) - Единая точка входа с маршрутизацией и передачей JWT
- Booking Service (порт 8081) - Управление бронированиями и пользователями
- Hotel Management Service (порт 8082) - Управление отелями и номерами

## Технологический стек
- Java 17+
- Spring Boot 3.5.x
- Spring Cloud (совместимый релиз-трейн)
- Spring Data JPA + H2 (in-memory базы данных)
- Spring Security + JWT
- Spring Cloud Gateway (API Gateway)
- Spring Cloud Netflix Eureka (Service Discovery)
- Lombok, MapStruct (для маппинга DTO)

## Запуск приложения
### Предварительные требования
- JDK 17 или выше
- Maven 3.6+
- Любая IDE (IntelliJ IDEA, Eclipse, VS Code)

## Порядок запуска сервисов
1. Запуск Eureka Server
cd eureka-server
mvn spring-boot:run

2. Запуск Hotel Management Service
cd hotel-service
mvn spring-boot:run

3. Запуск Booking Service
cd booking-service
mvn spring-boot:run

4. Запуск API Gateway
cd api-gateway
mvn spring-boot:run
Важно: Сервисы должны запускаться в указанном порядке для корректной регистрации в Eureka.

## Безопасность
### Аутентификация и авторизация
- JWT токены (срок жизни: 1 час)
- Роли: USER, ADMIN
- Каждый микросервис самостоятельно проверяет JWT как Resource Server

## Пример получения токена
### Регистрация
POST /api/bookings/user/register
{
  "username": "user@example.com",
  "password": "password123",
  "role": "USER"
}

### Авторизация
POST /api/bookings/user/auth
{
  "username": "user@example.com",
  "password": "password123"
}

## API Endpoints
### API Gateway (порт 8080)
Все запросы проходят через Gateway с префиксом /api.

### Booking Service
- POST	/api/bookings/booking	USER	Создание бронирования
- GET	/api/bookings/bookings	USER	История бронирований
- GET	/api/bookings/booking/{id}	USER	Получение бронирования
- DELETE	/api/bookings/booking/{id}	USER	Отмена бронирования
- POST	/api/bookings/user/register	PUBLIC	Регистрация
- POST	/api/bookings/user/auth	PUBLIC	Авторизация
- POST	/api/bookings/user	ADMIN	Создание пользовател
- PATCH	/api/bookings/user	ADMIN	Обновление пользователя
- DELETE	/api/bookings/user	ADMIN	Удаление пользователя

### Hotel Management Service
- GET	/api/hotels/hotels	USER	Список отелей
- POST	/api/hotels/hotels	ADMIN	Добавление отеля
- GET	/api/hotels/rooms	USER	Свободные номера
- GET	/api/hotels/rooms/recommend	USER	Рекомендованные номера
- POST	/api/hotels/rooms	ADMIN	Добавление номера
- POST	/api/hotels/rooms/{id}/confirm-availability	INTERNAL	Подтверждение доступности
- POST	/api/hotels/rooms/{id}/release	INTERNAL	Снятие блокировки

## Структура баз данных
Booking Service (H2 in-memory)
### Таблица users:
- id (PK)
- username (UNIQUE)
- password (зашифрованный)
- role (USER/ADMIN)

### Таблица bookings:
- id (PK)
- user_id (FK)
- room_id
- start_date
- end_date
- status (PENDING/CONFIRMED/CANCELLED)
- created_at
- correlation_id (для идемпотентности)

## Hotel Management Service (H2 in-memory)
### Таблица hotels:
- id (PK)
- name
- address

### Таблица rooms:
- id (PK)
- hotel_id (FK)
- number (номер комнаты)
- available (доступность для бронирования)
- times_booked (счетчик бронирований для равномерной загрузки)

## Алгоритмы и особенности реализации
Равномерное распределение нагрузки
### При автоподборе номера (autoSelect: true):
- Фильтрация свободных номеров на запрашиваемые даты
- Сортировка по возрастанию times_booked
- При равенстве счетчиков - сортировка по ID
- Выбор номера с минимальной загрузкой

### Идемпотентность
- Все внешние запросы используют correlation_id
- Повторная обработка одинакового запроса предотвращает дублирование
- Хранение истории обработанных correlation_id для проверки
- Обработка ошибок и тайм-ауты
- Настройка тайм-аутов для межсервисного взаимодействия
- Экспоненциальная backoff стратегия для повторов
- Компенсирующие транзакции при ошибках
- Логирование всех шагов с correlation_id для трассировки

## Мониторинг
### Eureka Dashboard
- URL: http://localhost:8761
- Просмотр зарегистрированных сервисов

## H2 Console
### Booking Service: http://localhost:8081/h2-console
- JDBC URL: jdbc:h2:mem:bookingdb
- User: sa
- Password: password

## Hotel Service: http://localhost:8082/h2-console
- JDBC URL: jdbc:h2:mem:hoteldb
- User: sa
- Password: password

## Конфигурация
### Настройки по умолчанию
Сервис	Порт	База данных
Eureka	8761	-
Gateway	8080	-
Booking	8081	bookingdb
Hotel	8082	hoteldb

###Конфигурационные файлы
application.yml - общие настройки
