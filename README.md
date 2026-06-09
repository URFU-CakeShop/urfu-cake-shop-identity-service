# Identity Service

Сервис отвечает за аутентификацию и авторизацию пользователей в Cake Shop.

<img width="1249" height="1028" alt="image" src="https://github.com/user-attachments/assets/25adf6d8-a2df-4d16-bf4c-5e55ce8879dd" />

## Функции

- регистрация пользователя (email, пароль, профиль, адрес)
- вход по email и паролю
- роли USER и ADMIN
- регистрация в Consul
- Swagger UI и Actuator (health, prometheus)

## Запуск

**Нужно:** Java 17, Docker.

```bash
docker compose up -d
./gradlew bootRun
```

Windows:

```powershell
docker compose up -d
.\gradlew.bat bootRun
```

Сервис: http://localhost:8080  
Swagger: http://localhost:8080/swagger-ui.html  
Health: http://localhost:8080/actuator/health  
Consul: http://localhost:8500

`docker compose` поднимает PostgreSQL (`5432`, БД `identity_db`) и Consul (`8500`).

## API

### POST `/api/auth/register`

```json
{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "Иван",
  "lastName": "Иванов",
  "city": "Екатеринбург",
  "street": "Ленина",
  "house": "1",
  "hasSugar": false
}
```

Обязательны `email` и `password` (от 8 символов). Ответ: `201 Created`.

### POST `/api/auth/login`

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

Ответ: `200 OK` или `401 Unauthorized`.

Ошибки:

```json
{
  "success": false,
  "message": "текст ошибки"
}
```

## Конфигурация

Файл `src/main/resources/application.yml`:

| Параметр | По умолчанию |
|----------|--------------|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/identity_db` |
| `spring.datasource.username` | `postgres` |
| `spring.datasource.password` | `postgres` |
| `spring.cloud.consul.host` | `localhost` |

## Сборка

```bash
./gradlew build
./gradlew test
docker build -t cake-shop-identity-service .
```

Миграции БД: `src/main/resources/db/changelog/`.
