# north-star-client

Android-клиент (Kotlin, Compose) для North Star: администратор, диспетчер, водитель.

## Сервер

Нужен запущенный [north_star_server_kursovaya](https://github.com/pressf1om/north_star_server_kursovaya): PostgreSQL на порту `5434`, API на `8080`.

В эмуляторе базовый URL по умолчанию: `http://10.0.2.2:8080` (`BuildConfig.SERVER_BASE_URL`).

## Демо-вход (миграции V2–V4 на сервере)

Пароль для всех: **`admin123`**

| Логин | Роль |
| --- | --- |
| `admin` | Администратор |
| `dispatcher` | Диспетчер |
| `petrov` | Водитель, рейс на `У123КЕ777` |
| `sidorov` | Водитель, рейс на `К456МН799` |
| `kozlov` | Водитель, без текущей заявки |

На парке у карточки с заявкой — **«Подробнее»**. В «Люди» — выдача и смена роли (кнопка на карточке).

## Сборка

```bash
.\gradlew.bat :androidApp:assembleDebug
```
