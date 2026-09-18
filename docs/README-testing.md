# Testing

How the tests run, and how to add one.

## Setup

| Kind      | Tool                                    | Location                        | Run                              |
| --------- | --------------------------------------- | ------------------------------- | -------------------------------- |
| Unit      | JUnit 4 on the JVM                      | `app/src/test/kotlin/`          | `.\gradlew.bat testDebugUnitTest` |
| Component | Compose UI tests, run by Robolectric    | `app/src/test/kotlin/`          | `.\gradlew.bat testDebugUnitTest` |
| E2E       | Maestro (Phase 7)                       | `.maestro/*.yaml`               | —                                |

Tests mirror the package of the code they test (`core/config/AppConfigTest.kt` tests
`core/config/AppConfig.kt`). `check` runs them all.

Both unit and component tests run on the JVM, with no emulator. Robolectric plays the
role `jest-expo`'s mocks play in the React Native app, but instead of mocking the
platform it runs the real Android framework code (downloaded as `android-all` jars on
the first run) inside the JVM. There are no instrumented tests (`app/src/androidTest/`):
Robolectric covers component tests, and Maestro will cover the real device.

## Coverage

Kover measures coverage for the unit tests. `.\gradlew.bat koverLog` prints line
coverage; `.\gradlew.bat koverHtmlReportDebug` writes a report to
`app/build/reports/kover/`. `BuildConfig`, the compiler-generated
`ComposableSingletons` classes and `@Preview` functions are excluded. There is no
threshold yet; one lands in Phase 7.

## Rules

- **Name tests as sentences**, in backticks: ``fun `strips trailing slashes`()``. The
  JVM allows spaces in method names; Android's runtime only does from API 30, so an
  instrumented test named this way wouldn't run on the app's minSdk.
- **Component tests use Robolectric's runner** (`@RunWith(AndroidJUnit4::class)`) and
  `androidx.compose.ui.test.junit4.v2.createComposeRule`. The original `createComposeRule`
  is deprecated, so it fails the build (see `docs/README-code-quality.md`). The v2 rule
  queues coroutines like a real dispatcher rather than running them immediately.
- **Test a composable, not the activity.** `composeRule.setContent { … }` renders the
  composable under test with the inputs the test chooses, the way
  `renderWithProviders` does in the React Native app.
- **Query like a user.** Prefer `onNodeWithText` and `onNodeWithContentDescription` over
  `onNodeWithTag`, and assert with `assertIsDisplayed()`.
- **Config is passed in, not read.** Tests call `AppConfig.parse(value)` directly and
  pass values like `http://api.test` to composables; nothing depends on
  `gradle.properties`.

## Why the test setup looks like this

Environment quirks worth knowing before touching the test configuration in
`app/build.gradle.kts`:

- **`unitTests.isIncludeAndroidResources = true`** gives Robolectric the merged
  resources and manifest, so `stringResource` and themes work in tests.
- **`ui-test-manifest` is a `debugImplementation` dependency.** It declares the empty
  `ComponentActivity` that `createComposeRule()` hosts content in.
- **The test JVM gets `--add-opens=java.base/jdk.internal.access=ALL-UNNAMED`.**
  Robolectric reaches into JDK internals when it fakes an API 36+ device, and newer JDKs
  (Android Studio's bundled JBR is 25) block that. Without it, every Robolectric test
  fails with "Failed to interact with raw FileDescriptor internals".
  `--enable-native-access=ALL-UNNAMED` silences a related warning about Robolectric
  loading its native graphics runtime.
- **Espresso is pinned to 3.7.0.** Compose's test library depends on Espresso 3.5,
  which calls `InputManager.getInstance()`, a method removed in API 36+. With 3.5, every
  interaction fails with a `NoSuchMethodException`.
