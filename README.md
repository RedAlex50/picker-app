# PickerApp

> Приложение сборки интернет-заказов для терминала сбора данных (ТСД).

PickerApp — мобильный клиент под Android для сборщиков (пикеров) даркстора:
аутентификация по PIN, очередь нарядов, сопровождение маршрута сборки по
s-shape, сканирование ячеек и SKU, согласование замен, упаковка боксов
по температурным зонам, печать ZPL-этикеток и передача курьеру.

## Стек

| Слой | Технология |
| --- | --- |
| Мобильный клиент | Kotlin 2.0 + Jetpack Compose 1.7 |
| Локальное хранилище | SQLite 3.39 + Room 2.6 + SQLCipher |
| Сервер | Kotlin 2.0 + Spring Boot 3.3 |
| СУБД | PostgreSQL 16 |
| Сканер | Zebra DataWedge 13+ (intent API) |
| Печать | ZPL II через Print Service |
| CI | GitHub Actions |

## Архитектура

Слоистая Clean Architecture с четырьмя слоями: `presentation` (Compose-экраны
и ViewModel), `domain` (use cases и доменные модели), `data` (репозитории,
Room DAO) и `infrastructure` (адаптеры к DataWedge, REST-клиенты POS/LA,
Print Service, Android Keystore).

## Модули

См. `docs/` для PlantUML-диаграмм модели данных, активности и состояний.

## Сборка

```bash
./gradlew :android:app:assembleDebug
./gradlew :server:bootRun
```
