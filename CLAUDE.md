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
  - `config-module` — параметры шифрования и данные о программе. Ключ берёт `CryptographyKeyStore`: сначала `app\config.json` (относительно рабочей директории, только для переноса ключа между машинами), затем сохранённый в папке данных (`ConfigPaths.appDataFolder`) как `key-{local,network}.bin` (на Windows защищён DPAPI через JNA), иначе генерирует новый — только если `UserRepositoryImpl` подтвердил, что базы пустые. `SessionRepositoryImpl_` ключ не генерирует, поэтому `UserRepositoryImpl` должен создаваться раньше. Нет ключа — `MissingCryptographyKeyException`; экспорт — `CryptographyKeyStore.export` (кнопка в настройках админки и гибрида). Репозитории в `Main.kt` создаются в `remember` (один раз на диск / по «Повторить»), ошибка показывается `StorageErrorDialog`, а не роняет приложение. Название и версия для «О программе» — ресурс `program.properties`, который генерирует задача `generateProgramInfo` в build-скрипте каждого приложения; версия одна на все приложения — `app.version` в `gradle.properties`. Формат `config.json` — в `README.md`.
- **`ui/*`**:
  - `ui/core/theme-ui-module` — тема (`AppTheme`, палитра в стиле VK (акцент #0077FF) в `theme/Colors.kt`, схемы строятся через `lightColorScheme`/`darkColorScheme`), каркас экранов-форм `FormScreen`/`FormSection`/`FormMessage` и поля `AuthTextField`/`AuthSpinnerField` (цвета полей — `formTextFieldColors()`), общие компоненты (включая элементы тестов: светофор, дорога, педали), модели тестов (`model/tests/*`, наследники `ReactionTest`) и константы маршрутов `other/extension/route/DesktopRouting`.
  - `ui/screens/*-ui-module` — отдельные экраны (авторизация, пользователи, профиль пользователя с графиками, меню тестов, сами тесты и т.д.).

### Ручной DI и навигация

DI-фреймворка нет. В `Main.kt` каждого приложения вручную создаются `*RepositoryImpl` → use case'ы → `*Storage`, затем внутри `PreComposeApp` — `NavHost` со `scene(route = DesktopRouting.xxx)`. Экраны получают `navigator` и нужные `*Storage` параметрами. Общее состояние (настройки, выбранный пользователь, `TestDTO` текущего теста) хранится в `MainViewModel` приложения и пробрасывается в экраны через колбэк `block`.

Новый экран: создать модуль в `ui/screens`, добавить `include` в `settings.gradle.kts`, зависимость `implementation(project(...))` в build-скрипты приложений, маршрут в `DesktopRouting` и `scene` в `Main.kt` нужных приложений.

### Паттерн экрана (MVI-подобный)

Каждый экран: `XxxPage.kt` (Composable) + `viewmodel/XxxViewModel.kt` (PreCompose `ViewModel`) + `store/{State,Event,Effect}.kt` (иногда `Output`/`Label`). ViewModel держит `state: MutableStateFlow<State>`, принимает `onEvent(event)`, одноразовые действия (навигация, назад) отправляет в `effect: MutableSharedFlow<Effect>`, которые Composable собирает в `LaunchedEffect` и вызывает `navigator`. Новые тесты реакции добавляются как модель в `theme-ui-module/model/tests` (с текстом `instruction` для окна перед запуском `TestStartDialog` в меню тестов; там же выбираются количество попыток и интервал), экран в `tests-ui-module/tests/<name>/` и запись в списке `TestMenuViewModel`.

### Тесты реакции

Общая логика — `tests-ui-module/.../tests/common/ReactionTestCore.kt` (фазы `TestPhase`, учёт попыток `TrialResults`, момент сигнала `StimulusClock`, сохранение `saveTestResult`); статистика — `ReactionStatistics` в `local-store-module`. Время сигнала — первая отрисовка стимула (`Modifier.reportFirstDraw`), время реакции — нажатие (`Modifier.onPressTimestamp`), обе отметки через `System.nanoTime()`. Не использовать `Button.onClick` для регистрации реакции: он срабатывает на отпускании. Размеры сцен тестов считаются от размеров окна (`BoxWithConstraints`), анимация движения дороги считается от времени кадра (`withFrameNanos`), а не шагом в пикселях. Тесты 4–7 (РДО, звуковой сигнал, PVT) используют общую дорожную сцену `RoadView` (светофор, впереди идущая машина, подъезд к стоп-линии); звук — `HornPlayer` (момент начала по позиции воспроизведения). Показатели, специфичные для теста, — `TestMetric` (таблица `SessionMetrics`, лист Excel «Показатели»); id тестов — в `model/tests`, общий список — `allReactionTests`. Ответ с клавиатуры: клавиши — `ResponseKeys` в `model/tests`, приём — `onResponseKey` у `TestScreenScaffold` (`keyboardResponses`); устройство — `preTestState.inputDevice`, выбирается в `TestStartDialog`, в режиме клавиатуры мышь игнорируется и наоборот. Перед зачётной частью — `PRACTICE_TRIALS` тренировочных попыток (`State.isPractice`, фаза `PRACTICE_FINISHED`, событие `StartMainTest`); они не сохраняются. Каждая попытка пишется в `TrialResults.trials` (лист Excel «Попытки»); сведения о рабочем месте — `EnvironmentCollector` (при изменении методики увеличивать `METHOD_VERSION`). Методика, типы ошибок и состав данных описаны в `README.md`.

### Хранилище и режимы

- Данные, `settings.json` и ключ шифрования лежат в папке ОС (`GeneralConstants.APP_DATA_FOLDER`, в config-module — `ConfigPaths`): Windows — `C:\Users\<user>\AppData\Local\Reaction test\` (как в прежних версиях), macOS — `~/Library/Application Support/Reaction test/`, Linux — `~/.local/share/Reaction test/`. Excel по умолчанию — `~/Documents/Reaction test/`; путь вида `C:\...` из настроек на не-Windows сбрасывается на него. Пути собирать через `File(parent, child)`, не через `\\`. Сетевой режим — только Windows.
- `Mode.SINGLE` — локальные SQLite-БД (`user_database.db`, `sessions_database.db`); `Mode.ETHERNET` — БД на сетевом диске `<диск>:\Default\AppData\Roaming\Reaction test\` (диск берётся из `Settings.networkDrive`).
- Результаты тестов пишутся и в БД сессий, и в Excel-файл (`WorkbookRepositoryImpl`, формат из `Settings.dataFormats`).
- Таблицы создаются не автоматически по схеме, а явным запросом `create()` из `.sq`-файла при инициализации репозитория (например, в `UserRepositoryImpl`).
