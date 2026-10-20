# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Язык общения

Всегда отвечать пользователю на русском языке.

## О проекте

Desktop-приложения (Kotlin + Compose Multiplatform for Desktop) для измерения времени сложной сенсомоторной реакции водителя АТС на зрительный раздражитель. Разработано для СибАДИ. Состоит из клиентской части (прохождение тестов) и части администратора (управление пользователями и тестовыми сессиями).

Стек: Gradle 9.4.1, Kotlin 2.4.21, Compose Multiplatform 1.12.2 (material3 1.12.0-alpha03 — официальная пара для этого релиза), PreCompose 1.6.2 (навигация + ViewModel), SQLDelight 2.4.1 (SQLite через JDBC), Apache POI (выгрузка в Excel), kotlinx.serialization.

Каждый модуль с `id("org.jetbrains.compose")` обязан также применять `kotlin("plugin.compose")` (компилятор Compose в Kotlin 2.x). Иконки (`Icons.*`) не приходят транзитивно — нужен `globalVersions.material.icons.core`. Версии плагинов Kotlin/Compose берутся из корневого `gradle.properties`; вложенные `settings.gradle.kts`/`gradle.properties` в модулях — остатки, при сборке из корня не используются.

## Команды

Все зависимости объявлены в version catalog `gradle/globalVersions.versions.toml` (в build-скриптах обращаться через `globalVersions.*`, а не `libs.*`). Версии Kotlin/Compose для плагинов задаются в `gradle.properties`.

```bash
# Запуск приложения (модуль-приложение из desktop-application/)
./gradlew :desktop-application:offline-application-hybrid:run
./gradlew :desktop-application:ethernet-application-reaction-test:run
./gradlew :desktop-application:ethernet-application-admin:run
./gradlew :desktop-application:generate-code-application:run

# Сборка всего проекта
./gradlew build

# Нативный дистрибутив под текущую ОС (exe/msi/dmg/deb…)
./gradlew :desktop-application:offline-application-hybrid:packageDistributionForCurrentOS

# Перегенерация кода SQLDelight после правки .sq-файлов
./gradlew :client-core:local-store-module:generateMainDatabaseInterface
```

Тестов в проекте сейчас нет (каталогов `src/test` нет).

## Архитектура

Многомодульный Gradle-проект из трёх групп (список — в `settings.gradle.kts`):

- **`desktop-application/*`** — исполняемые приложения (точка входа `presentation/Main.kt`, у генератора кода — `Main.kt`):
  - `offline-application-hybrid` — основной «гибрид»: и тесты, и управление пользователями на одной машине.
  - `ethernet-application-reaction-test` / `ethernet-application-admin` — раздельные клиент и админка. Их `presentation/{home,settings,main}` почти дублируют гибрид, отличаются набором кнопок и маршрутов на HomePage. Изменения в общих экранах этих пакетов обычно нужно повторять во всех трёх приложениях.
  - `generate-code-application` — утилита генерации секретного ключа и уникального номера (для `config.json`).
- **`client-core/*`** — данные и бизнес-логика без UI:
  - `local-store-module` — всё хранилище: SQLDelight-БД (`src/main/sqldelight/ru/plumsoftware/{users,sessions}/*.sq`, сгенерированный пакет `ru.plumsoftware`, класс `Database`), настройки в JSON, запись результатов в Excel. Слои `domain/repository` (интерфейсы) → `data/repository` (`*Impl`) → `domain/usecase` (по одному use case на операцию) → `domain/storage` (фасады `UserStorage`, `SessionStorage`, `SettingsStorage`, `WorkbookStorage`, которые и передаются в UI).
  - `hash-module` — хэширование паролей; `cryptography-module` — симметричное шифрование персональных данных пользователя в БД (ФИО, пол хранятся зашифрованными, расшифровываются в `UserRepositoryImpl`).
  - `config-module` — чтение `app\config.json` (относительно рабочей директории) с ключом/алгоритмом шифрования и данными о программе. Без этого файла `CryptographyRepositoryImpl` бросает исключение при создании, т.е. приложение с пользователями не запустится. Файл в `.gitignore`; шаблон — `docs/config.example.json`, формат и требования к длине ключа описаны в `README.md`.
- **`ui/*`**:
  - `ui/core/theme-ui-module` — тема (`AppTheme`, синяя палитра M3 в `theme/Colors.kt`, схемы строятся через `lightColorScheme`/`darkColorScheme`), каркас экранов-форм `FormScreen`/`FormSection`/`FormMessage` и поля `AuthTextField`/`AuthSpinnerField` (цвета полей — `formTextFieldColors()`), общие компоненты (включая элементы тестов: светофор, дорога, педали), модели тестов (`model/tests/*`, наследники `ReactionTest`) и константы маршрутов `other/extension/route/DesktopRouting`.
  - `ui/screens/*-ui-module` — отдельные экраны (авторизация, пользователи, профиль пользователя с графиками, меню тестов, сами тесты и т.д.).

### Ручной DI и навигация

DI-фреймворка нет. В `Main.kt` каждого приложения вручную создаются `*RepositoryImpl` → use case'ы → `*Storage`, затем внутри `PreComposeApp` — `NavHost` со `scene(route = DesktopRouting.xxx)`. Экраны получают `navigator` и нужные `*Storage` параметрами. Общее состояние (настройки, выбранный пользователь, `TestDTO` текущего теста) хранится в `MainViewModel` приложения и пробрасывается в экраны через колбэк `block`.

Новый экран: создать модуль в `ui/screens`, добавить `include` в `settings.gradle.kts`, зависимость `implementation(project(...))` в build-скрипты приложений, маршрут в `DesktopRouting` и `scene` в `Main.kt` нужных приложений.

### Паттерн экрана (MVI-подобный)

Каждый экран: `XxxPage.kt` (Composable) + `viewmodel/XxxViewModel.kt` (PreCompose `ViewModel`) + `store/{State,Event,Effect}.kt` (иногда `Output`/`Label`). ViewModel держит `state: MutableStateFlow<State>`, принимает `onEvent(event)`, одноразовые действия (навигация, назад) отправляет в `effect: MutableSharedFlow<Effect>`, которые Composable собирает в `LaunchedEffect` и вызывает `navigator`. Новые тесты реакции добавляются как модель в `theme-ui-module/model/tests`, экран в `tests-ui-module/tests/<name>/` и запись в списке `TestMenuViewModel`.

### Хранилище и режимы

- Пути жёстко ориентированы на Windows: данные и `settings.json` лежат в `C:\Users\<user>\AppData\Local\Reaction test\` (`GeneralConstants`, `DatabaseConstants`). На macOS/Linux пути работают некорректно.
- `Mode.SINGLE` — локальные SQLite-БД (`user_database.db`, `sessions_database.db`); `Mode.ETHERNET` — БД на сетевом диске `<диск>:\Default\AppData\Roaming\Reaction test\` (диск берётся из `Settings.networkDrive`).
- Результаты тестов пишутся и в БД сессий, и в Excel-файл (`WorkbookRepositoryImpl`, формат из `Settings.dataFormats`).
- Таблицы создаются не автоматически по схеме, а явным запросом `create()` из `.sq`-файла при инициализации репозитория (например, в `UserRepositoryImpl`).
