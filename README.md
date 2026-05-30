# Hunter Diary Ktor Backend

Курсовой проект "Дневник охотника".

## Стек

- Kotlin
- Ktor Server
- Koin
- kotlinx.serialization
- Google Cloud Firestore Java client
- Gradle Kotlin DSL

## Настройка

### Адрес слушателя

```bash
export HOST="localhost"
export PORT="8080"
```

`HOST` по умолчанию `localhost`, `PORT` по умолчанию `8080`.

### Аутентификация

```bash
export JWT_SECRET="change-me-to-a-long-random-secret"
export FIRESTORE_CREDENTIALS_PATH="/absolute/path/to/firebase-adminsdk-key.json"
```

Без `JWT_SECRET` и `FIRESTORE_CREDENTIALS_PATH` приложение стартует, но выпуск токенов и запросы к бд недоступны.

## Сборка и запуск

```bash
./gradlew :app:build
```

```bash
./gradlew run
```

### Fat jar

Сборка исполняемого jar со всеми runtime-зависимостями через Ktor Gradle plugin:

```bash
./gradlew :app:buildFatJar
```

Файл будет создан здесь:

```text
app/build/libs/app-all.jar
```

Запуск:

```bash
java -jar app/build/libs/app-all.jar
```
