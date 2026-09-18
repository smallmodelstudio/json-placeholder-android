# JSON Placeholder Android

A native Android app, in Kotlin and Jetpack Compose, that reads the
[JSONPlaceholder proxy API](https://github.com/smallmodelstudio/json-placeholder-api)
and shows posts, people, albums and photos.

It is the same app as the React Native
[json-placeholder-mobile](https://github.com/smallmodelstudio/json-placeholder-mobile),
built the idiomatic Android way. The repo is a sandbox for learning native Android and
comparing it with React Native: [the comparison](docs/README-comparison.md) grows with
each phase. It is decoupled from the API and talks to it only over HTTP.

**Stack:** Kotlin 2.4 · AGP 9.4 · Jetpack Compose (BOM 2026.09) · Material 3 ·
JUnit + Robolectric · ktlint + Android Lint · Kover

## Status

Phase 0 (Environment and scaffold) is done: the app builds, passes every check and runs
on an emulator against the API. Phase 1 (Theme and app shell) is next. See the
[build plan](docs/README-plan.md) for the phase-by-phase detail.

## Quick start

Open the folder in Android Studio and press **Run**, or from PowerShell:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat installDebug
```

See [Getting started](docs/README-getting-started.md) for configuration, reaching the
API from an emulator, and running Gradle from WSL.

## Repository map

| Path                                                   | Contents                                              | Docs                                                  |
| ------------------------------------------------------ | ----------------------------------------------------- | ----------------------------------------------------- |
| `settings.gradle.kts`, `build.gradle.kts`, `gradle/`   | Gradle settings, plugin declarations, version catalog, wrapper | [Code quality](docs/README-code-quality.md)   |
| `gradle.properties`                                    | Build settings and app configuration                  | [Getting started](docs/README-getting-started.md)     |
| `app/build.gradle.kts`                                 | The app module: SDK levels, build types, lint, tests  | [Getting started](docs/README-getting-started.md)     |
| `app/src/main/kotlin/…/core/config/`                   | Configuration parsing and validation                  | [Getting started](docs/README-getting-started.md)     |
| `app/src/main/kotlin/…/`                               | The app: activity, application, composables           | [Build plan](docs/README-plan.md)                     |
| `app/src/main/res/`, `app/src/debug/res/`              | Strings, theme, icon, network security config         | [Getting started](docs/README-getting-started.md)     |
| `app/src/test/kotlin/`                                 | Unit and Robolectric Compose tests                    | [Testing](docs/README-testing.md)                     |
| `.editorconfig`                                        | Formatting and ktlint rules                           | [Code quality](docs/README-code-quality.md)           |
| `docs/README-comparison.md`                            | Kotlin and React Native compared, phase by phase      | —                                                     |
