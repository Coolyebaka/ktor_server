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

```bash
./gradlew :app:run
```

Проверка health endpoint:

```bash
curl http://localhost:8080/health
```

Ожидаемый ответ:

```json
{"status":"ok"}
```

## Проверка

```bash
./gradlew :app:test
```
