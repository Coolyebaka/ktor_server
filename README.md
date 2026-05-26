# Hunter Diary Ktor Backend

Курсовой проект "Дневник охотника".

## Стек

- Kotlin
- Ktor Server
- Koin
- kotlinx.serialization
- Google Cloud Firestore Java client
- Gradle Kotlin DSL

## Запуск

Для auth endpoints нужны JWT secret и Firestore настройки:

```bash
export JWT_SECRET="change-me-to-a-long-random-secret"
export FIRESTORE_PROJECT_ID="your-google-cloud-project-id"
export FIRESTORE_CREDENTIALS_PATH="firebase-service-account.json"
./gradlew :app:run
```

Без `JWT_SECRET` приложение стартует, но выпуск токенов недоступен.

Проверка health endpoint:

```bash
curl http://localhost:8080/health
```

Ожидаемый ответ:

```json
{"status":"ok"}
```

## Auth API

Регистрация:

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"hunter@example.com","password":"secret-password"}'
```

Авторизация:

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"hunter@example.com","password":"secret-password"}'
```

Формат успешного ответа:

```json
{
  "token": "...",
  "user": {
    "id": "...",
    "email": "hunter@example.com"
  }
}
```

Формат ошибки:

```json
{
  "code": "...",
  "message": "..."
}
```

## Проверка

```bash
./gradlew :app:build
```
