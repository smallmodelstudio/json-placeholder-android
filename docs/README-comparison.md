# Kotlin and React Native compared

What building the same app twice shows about the two approaches. A section per phase,
written as each one lands. The React Native app is
[json-placeholder-mobile](https://github.com/smallmodelstudio/json-placeholder-mobile).

## Phase 0: Toolchain and scaffold

| Concern                  | React Native (Expo)                                                                  | Android (Kotlin)                                                                                          |
| ------------------------ | ------------------------------------------------------------------------------------ | --------------------------------------------------------------------------------------------------------- |
| Project creation         | `create-expo-app`; `android/` generated and gitignored                               | Android Studio's template, or by hand; every build file is committed and edited directly                  |
| Build tool               | Metro bundles JS; Gradle builds the native shell (hidden)                            | Gradle builds everything; its configuration is Kotlin code (`*.gradle.kts`)                               |
| Dependency versions      | `package.json`; `npx expo install` picks SDK-compatible versions                     | `gradle/libs.versions.toml`; the Compose BOM plays the curated-set role for Compose libraries only        |
| Toolchain pinning        | `.nvmrc` (Node), Expo SDK version                                                    | Gradle wrapper (version plus distribution checksum), AGP version, JDK from Android Studio                 |
| Dev loop                 | `npm start`, Fast Refresh on save                                                    | Build and install (`installDebug`); Compose Previews in the IDE for single composables                    |
| Type checking            | Separate `tsc` step; Metro strips types, so a type error still runs                 | The compiler is the build; with `allWarningsAsErrors`, even a deprecation stops it                        |
| Lint and format          | ESLint + Prettier                                                                    | ktlint (format and style, plus Compose rules) + Android Lint (manifest, resources, API levels)            |
| Config                   | `EXPO_PUBLIC_*` in `.env`, inlined by Metro, parsed with zod at startup              | Gradle property inlined into `BuildConfig`, parsed by `AppConfig.parse` at startup                        |
| Tests                    | Jest in Node; `jest-expo` mocks the native side                                      | JUnit on the JVM; Robolectric runs real Android framework code                                            |
| Plain HTTP to a dev API  | Allowed in debug builds by the React Native template                                 | Blocked by default; a debug-only network security config allows named hosts                               |

### What stood out

- **Less is generated for you, and all of it is visible.** Expo hides the native
  project; here `AndroidManifest.xml`, the launcher icon, backup rules and the network
  security config are all files in the repo. It's more to write, but nothing is magic.
  One example: minSdk 24 predates adaptive icons, so the icon needs an API 26+ version
  (`mipmap-anydpi-v26`) and a fallback. Expo's config plugins handle that silently.
- **The compiler is a stricter gate.** In the React Native app, `typecheck` is a step you
  can forget. Here a deprecated test API (`createComposeRule`) failed the build on the
  first run, so the migration happened immediately.
- **Both test setups need environment workarounds, of different kinds.** The React
  Native app's list (`docs/README-api.md` there) is mostly about Jest not being a real
  device: Expo's fetch, `expo-crypto` and FlashList's layout all need stand-ins. Here,
  Robolectric runs the *real* framework, so the problems are version skew instead: the
  newest JDK blocking Robolectric's reflection, and an old Espresso calling an Android
  API that no longer exists. See `docs/README-testing.md`.
- **Reaching a local API is simpler.** The emulator's `10.0.2.2` alias reaches the
  Windows host, and WSL2 forwards that to the API, so the Kotlin app needs no
  `adb reverse` (which the React Native app's docs recommend, and which has to be rerun
  after every emulator restart). The same alias would work for the React Native app too.
- **Build times.** The first build took about 2 minutes, mostly downloading AGP, Kotlin
  and Compose. Incremental `check` runs take 7–30 seconds. That is slower than Fast
  Refresh for UI iteration, which is why Previews matter.
- **Cold start.** The debug build cold-starts in about 1.1s on the API 37 `Pixel_8`
  emulator (`am start -W`). Debug builds aren't representative, so the real comparison
  waits for release builds of both apps in Phase 8.
