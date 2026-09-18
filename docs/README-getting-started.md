# Getting started

How to install, configure and run the app locally.

## Prerequisites

| Tool                                                | Version                           | Needed for                                        |
| --------------------------------------------------- | --------------------------------- | ------------------------------------------------- |
| Android Studio                                      | 2026.1 or later                   | Everything: its bundled JDK (JBR) runs Gradle     |
| Android SDK platform 37 and build-tools 36          | Via Android Studio's SDK Manager  | Building                                          |
| An emulator (AVD) or a device with USB debugging    | Any; developed on a `Pixel_8`, API 37 | Running the app                               |
| The proxy API                                       | Any version matching the contract | Real data (not needed for tests)                  |

Gradle itself isn't a prerequisite: the wrapper (`gradlew.bat` / `gradlew`) downloads
the pinned version and checks it against the checksum in
`gradle/wrapper/gradle-wrapper.properties`.

## Run

Open the repo folder in Android Studio, let it sync, pick a device and press **Run**.
Android Studio writes `local.properties` (the SDK path, gitignored) on first sync.

From a terminal, Gradle needs a JDK. Point `JAVA_HOME` at the one bundled with
Android Studio; setting it as a user environment variable once saves repeating it:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat installDebug
```

Unlike Expo, there's no dev server: `installDebug` builds an APK and installs it. Change
code, then run again. Compose `@Preview`s in Android Studio give quick feedback on a
single composable without a device.

### From WSL

The repo lives on the Windows filesystem, so WSL tools see it under
`/mnt/c/Users/frede/code/json-placeholder-android`. WSL has no JDK or Linux Android
SDK, so run the Windows Gradle through interop, passing `JAVA_HOME` with `WSLENV`:

```bash
cd /mnt/c/Users/frede/code/json-placeholder-android
JAVA_HOME='C:\Program Files\Android\Android Studio\jbr' WSLENV=JAVA_HOME cmd.exe /c gradlew.bat check
```

`adb` is `/mnt/c/Users/frede/AppData/Local/Android/Sdk/platform-tools/adb.exe`.

## Configuration

Settings are Gradle properties, which the build inlines into `BuildConfig`. They are
visible to anyone with the APK, so never put secrets in them.

| Where                                                   | Committed | Purpose                             |
| ------------------------------------------------------- | --------- | ----------------------------------- |
| `gradle.properties`                                     | Yes       | Shared defaults                     |
| `%USERPROFILE%\.gradle\gradle.properties`               | No        | Your machine's overrides            |
| `-P<name>=<value>` on the command line                  | No        | One-off overrides                   |

Later rows win over earlier ones.

| Property                 | Default                 | Controls                                         |
| ------------------------ | ----------------------- | ------------------------------------------------ |
| `jsonplaceholder.apiUrl` | `http://10.0.2.2:3000`  | Base URL of the proxy API; must be http or https |

The build fails if a property is missing. `AppConfig.parse`
(`core/config/AppConfig.kt`) validates the values when the app starts, in
`JsonPlaceholderApplication.onCreate`: an invalid value crashes the app at launch with
a message naming the property, rather than failing on the first request.

To add a property:

1. Add it to `gradle.properties`.
2. Read it in `app/build.gradle.kts` and pass it on with `buildConfigField`.
3. Add it to `AppConfig` and validate it in `AppConfig.parse`, with a test in `AppConfigTest`.

## Reaching the API from an emulator (WSL2)

The API runs inside WSL, and the emulator runs on Windows. Inside an emulator,
`localhost` is the emulator itself, but `10.0.2.2` is an alias for the Windows host's
`localhost`, and WSL2 forwards Windows `localhost` to Linux by default. So the default
`http://10.0.2.2:3000` reaches the API with no setup, and keeps working after the
emulator restarts.

| Option                       | How                                                                                            |
| ---------------------------- | ---------------------------------------------------------------------------------------------- |
| `10.0.2.2` (default)         | Nothing to do. Emulator only; a physical device can't use it                                   |
| `adb reverse`                | `adb reverse tcp:3000 tcp:3000`, then set the URL to `http://localhost:3000`. Rerun after each emulator restart; also works for USB devices |
| A deployed API               | Set `jsonplaceholder.apiUrl` to its https URL                                                  |

Android blocks plain HTTP by default. `src/debug/res/xml/network_security_config.xml`
allows it for `10.0.2.2` and `localhost` in debug builds only; release builds allow
HTTPS only.

To check the emulator can reach the API without the app:

```bash
adb shell '(printf "GET /health/live HTTP/1.0\r\n\r\n"; sleep 2) | nc 10.0.2.2 3000'
```

## Gradle tasks

Run as `.\gradlew.bat <task>` (Windows) or `./gradlew <task>` (macOS/Linux).

| Task                    | Does                                                                 |
| ----------------------- | -------------------------------------------------------------------- |
| `check`                 | Every check: ktlint, Android Lint and the unit tests. Run before finishing a change |
| `assembleDebug`         | Build a debug APK into `app/build/outputs/apk/debug/`                |
| `installDebug`          | Build and install the debug APK on the connected device              |
| `ktlintCheck`           | ktlint over Kotlin sources and build scripts                         |
| `ktlintFormat`          | ktlint, applying fixes                                               |
| `lintDebug`             | Android Lint; report in `app/build/reports/`                         |
| `testDebugUnitTest`     | Unit and Robolectric tests                                           |
| `koverHtmlReportDebug`  | Coverage report in `app/build/reports/kover/`                        |
| `koverLog`              | Print line coverage to the console                                   |
