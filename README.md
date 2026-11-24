# Millifin

Millifin is an Android app that teaches French-speaking Muslim beginners how money works, and how to manage it according to Islamic finance principles.

It works offline, needs no account, and keeps personal data on the phone.

## Features

- **10 short lessons** in plain French, from "What is money?" to halal investment tools. Each lesson cites its sources.
- **Quizzes** after every lesson. The chosen answer is outlined in green when it is right and in red when it is wrong, with an explanation. A lesson is completed at 60 %.
- **Progress tracking**: lesson status (to do, in progress, done) and an overall progress bar on the home screen.
- **Glossary** of Islamic finance terms with a search that ignores case and accents.
- **Simple budget**: incomes and expenses by category, monthly summary and amount left to save, in CAD, EUR or XOF.
- **First-launch disclaimer**: Millifin is educational content, not a religious ruling or financial advice.

## Tech stack

| Area | Choice |
|---|---|
| Language and UI | Kotlin, Jetpack Compose, Material 3 |
| Architecture | MVVM, unidirectional data flow (`StateFlow` UI states) |
| Dependency injection | Hilt |
| Local storage | Room (content, progress, budget), DataStore (settings) |
| Network | Retrofit, kotlinx.serialization |
| Navigation | Navigation Compose with type-safe routes |
| Tests | JUnit 4, kotlinx-coroutines-test, hand-written fakes |
| CI | GitHub Actions: lint, unit tests and debug build on every pull request |

## Architecture

The app follows the C4 model and an MVVM structure.

```
User ──> Compose screens ──> ViewModels ──> Repositories ──> Room / DataStore / Retrofit
```

Packages are grouped by responsibility:

```
com.adamdrabo.millifin
├── core       database, DI modules, navigation, theme, money formatting, shared UI
├── content    lessons, quiz and glossary data (DTOs, Room entities, repository)
├── progress   lesson progress (Room table, repository)
├── budget     transactions, monthly summary, budget screens
├── settings   currency choice and onboarding
├── glossary   glossary screen
└── home       home screen
```

### Offline-first content

Lesson content lives in `app/src/main/assets/content.json`.

1. On first launch, the bundled file is saved into Room, so the app works without a connection.
2. At every launch, the app downloads the same file from the `main` branch on GitHub.
3. The local copy is replaced only when the downloaded `version` is higher. Network errors keep the existing content.

To publish new lessons without releasing a new app version, edit `content.json`, increase `version`, and merge it into `main`.

Progress is stored in a separate table with no foreign key to lessons, so a content update never erases it.

### Money

Amounts are stored as `Long` in minor units (cents), never as `Double`, to avoid rounding errors. XOF has no decimals. Each transaction keeps its own currency, and the monthly summary only counts the active one.

## Run the project

1. Open the project in Android Studio.
2. Sync Gradle.
3. Run the `app` configuration on an emulator or a device (Android 8.0, API 26, or newer).

Run the unit tests:

```
./gradlew testDebugUnitTest
```

## Branches

- `main`: production.
- `preprod`: integration branch.
- `dev/feature/*`: one branch per feature, created from `preprod`, merged with a pull request once the CI is green.

## Disclaimer

Millifin is an educational tool. It is not a religious ruling (fatwa) and not personalized financial advice. Religious topics present the main opinions and their differences; for a personal decision, consult a qualified person.
