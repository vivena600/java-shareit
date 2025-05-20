# java-shareit
[![Java Version](https://img.shields.io/badge/Java-17-blue.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen.svg)](https://spring.io/projects/spring-boot)

Сервис для шеринга вещей позволяет пользователям брать предметы в аренду и предлагать свои вещи в аренду другим.

### Базовый URL
`http://localhost:8080`

### Пользователи
| Метод | Путь | Описание |
|-------|------|----------|
| POST  | `/users` | Создание нового пользователя |
| GET   | `/users/{userId}` | Получение информации о пользователе |
| PATCH | `/users/{userId}` | Обновление данных пользователя |
| GET   | `/users` | Получение списка всех пользователей |

### Вещи
| Метод | Путь | Описание | Требуемые заголовки |
|-------|------|----------|---------------------|
| POST  | `/items` | Добавление новой вещи | `X-Sharer-User-Id` |
| PATCH | `/items/{itemId}` | Обновление вещи | `X-Sharer-User-Id` |
| GET   | `/items/{itemId}` | Получение информации о вещи | - |
| GET   | `/items` | Получение всех вещей пользователя | `X-Sharer-User-Id` |
| GET   | `/items/search?text={text}` | Поиск вещей | - |
