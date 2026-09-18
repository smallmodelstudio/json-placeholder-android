# CLAUDE.md

## Project Context

- **Overview and docs index:** `README.md`
- **Topic docs:** `docs/README-<topic>.md`
- **Build plan and progress:** `docs/README-plan.md`

## Workflow Rules

1. Before starting work, read the docs for the area you're changing.
2. AGP, Kotlin and Compose change quickly. Check the versions in
   `gradle/libs.versions.toml` and read the docs for those versions before writing
   build or Compose code. Add every dependency through the version catalog.
3. With `allWarningsAsErrors` on, a deprecated API fails the build. Migrate to the
   replacement; don't suppress the warning.
4. The app is decoupled from the API and from the React Native app. Never import
   from, or depend on, either repo; the only contract is HTTP.
5. Documentation lives only in `docs/`, plus the root `README.md` index. Don't add
   Markdown files anywhere else.
6. When a change alters behaviour, commands or configuration covered in `docs/`,
   update that doc in the same change, and add what it teaches to
   `docs/README-comparison.md`. Docs describe the current state, not its history.
7. Before finishing a change, run `check` (see below). It runs ktlint, Android Lint
   and the unit tests.

## Running Gradle from WSL

The repo is on the Windows filesystem, and WSL has no JDK or Linux Android SDK.
Run the Windows wrapper through interop, with Android Studio's JDK:

```bash
cd /mnt/c/Users/frede/code/json-placeholder-android
JAVA_HOME='C:\Program Files\Android\Android Studio\jbr' WSLENV=JAVA_HOME cmd.exe /c gradlew.bat check
```

`adb` is `/mnt/c/Users/frede/AppData/Local/Android/Sdk/platform-tools/adb.exe`.
