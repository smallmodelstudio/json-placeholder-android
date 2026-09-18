# Build plan

The plan for building the app, phase by phase. Tick items off as they land, and
delete this file once every phase is done (topic docs then describe the app).

## Goal

A native Android app, in Kotlin and Jetpack Compose, that reads the
[JSONPlaceholder proxy API](https://github.com/smallmodelstudio/json-placeholder-api)
and shows the data in a polished UI. The app is read-only.

It is the same app as the
[React Native app](https://github.com/smallmodelstudio/json-placeholder-mobile):
the same screens, the same API contract rules and the same principles, built the
idiomatic Android way. The repo is a learning exercise, and a way to compare the
two approaches, so structure, tooling, tests and the written comparison
(`docs/README-comparison.md`) count as much as the features.

## Principles

- **Decoupled from the API and from the React Native app.** The app shares no
  code, packages or repo with either. The only contract is HTTP, described by a
  committed OpenAPI snapshot. No test or CI step needs the API to be running.
- **Types come from the contract.** Models are generated from the snapshot,
  never written by hand.
- **One boundary.** Screens never see raw HTTP. The data layer removes the
  envelope and turns every failure into a typed error.
- **Every screen has four states:** loading, empty, error and success, with the
  same primary/secondary data rules as the React Native app.
- **Docs describe the current state.** Topic docs live in `docs/README-<topic>.md`
  and are updated in the same change as the code.
- **Every phase adds to the comparison.** `docs/README-comparison.md` gains a
  section per phase, written while the difference is fresh.

## Stack

| Area                 | Choice                                                                        | React Native equivalent                        |
| -------------------- | ----------------------------------------------------------------------------- | ---------------------------------------------- |
| Language             | Kotlin 2.4, `allWarningsAsErrors`                                             | TypeScript, strict flags                       |
| Build                | Gradle (Kotlin DSL), version catalog, AGP 9 with built-in Kotlin              | Expo, Metro, EAS                               |
| UI                   | Jetpack Compose, Material 3                                                   | React Native Paper (Material 3)                |
| Navigation           | Navigation Compose, `@Serializable` type-safe routes                          | Expo Router, typed routes                      |
| State                | `ViewModel` + `StateFlow<UiState>`                                            | Hooks over TanStack Query                      |
| Data                 | One repository per resource, with an in-memory cache and retry policy        | TanStack Query's cache                         |
| Dependency injection | Hilt                                                                          | Module singletons and React context            |
| HTTP                 | Retrofit + OkHttp + kotlinx.serialization                                     | `openapi-fetch`                                |
| Contract types       | `openapi-generator` Gradle plugin                                             | `openapi-typescript`                           |
| Lists and images     | Lazy layouts, `HorizontalPager`, Coil                                         | FlashList, `expo-image`                        |
| Config               | Gradle property → `BuildConfig`, validated at startup                         | `EXPO_PUBLIC_API_URL`, validated with zod      |
| Lint/format          | ktlint (`ktlint_official`) + compose-rules, Android Lint                      | ESLint, Prettier, `tsc`                        |
| Unit/component tests | JUnit 4, Robolectric, Compose UI tests; coroutines-test and Turbine to come  | Jest, React Native Testing Library             |
| API mocking          | OkHttp `MockWebServer`                                                        | MSW                                            |
| E2E tests            | Maestro (the same flows as the React Native app)                              | Maestro                                        |
| Coverage             | Kover                                                                         | Jest `coverageThreshold`                       |
| CI                   | Harness pipeline; release APK as a build artifact                             | Harness pipeline; EAS Build                    |

## Decisions

| Decision               | Choice                                                                                                                                                                                                                                  |
| ---------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Where the repo lives   | The Windows filesystem (`C:\Users\frede\code`). Android Studio and Gradle over `\\wsl$` are slow and file-watching is unreliable. The API keeps running in WSL, and the emulator reaches it through `10.0.2.2`                         |
| Dependency injection   | Hilt: Google's recommended DI for Android and what most real codebases use. Manual DI would be simpler, but less representative                                                                                                        |
| Caching                | An in-memory cache in each repository (stale time, shared in-flight requests, retry with backoff), built by hand. It keeps behaviour the same as the React Native app, and shows what TanStack Query was doing. Room is a stretch goal |
| Detekt                 | Deferred. Its only Kotlin 2.x-compatible line (2.0) is still alpha, and compose-rules' detekt ruleset needs that alpha. Compose rules run through ktlint instead; add detekt once 2.0 is stable                                       |
| Module structure       | One `:app` module, split by package. Splitting into Gradle modules is a stretch goal                                                                                                                                                  |

## The API contract

The API's facts that shape the app, the same as the React Native app's. All of
them come from its OpenAPI document and response behaviour, not its source code.

| API behaviour                                                               | What the app does                                                                                                              |
| --------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------ |
| Success responses are `{ data, meta }`                                      | The data layer returns `data` only                                                                                             |
| Errors are `{ statusCode, message, error, path, timestamp, correlationId }` | Parsed into an `ApiError`; unknown shapes and network failures become an `ApiError` with status 0                              |
| Accepts and echoes `x-correlation-id`                                       | An OkHttp interceptor sends a generated ID per request; error states show it as "Ref: …"                                       |
| Rate limits per IP (429)                                                    | Retry with backoff on 429, 5xx and network errors; never retry other 4xx                                                       |
| Lists are not paginated (`/photos` is 5,000 items)                          | Prefer nested routes (`/albums/:id/photos`); lazy layouts for every list                                                       |
| Unknown query params return 400                                             | Only send params the contract declares                                                                                         |
| Photo URLs point at `via.placeholder.com`, which no longer serves images    | Render a coloured tile from the hex colour in the URL, used as the image placeholder and fallback                              |
| The snapshot describes each endpoint's envelope inline                      | Generated per-endpoint wrapper names are unwieldy (`PostsControllerFindAll200Response`); Phase 2 spikes models-only generation |

## Screens

The same as the React Native app:

```text
(tabs)
  Posts     list of cards (title, excerpt, author)
            → post detail: body, author, comments
  People    directory with search
            → profile: contact, company
              segmented: Posts | Albums | Todos
  Albums    grid of albums (colour tile, title)
            → photo grid (colour tiles, count in the header)
              → full-screen viewer (swipe, pinch to zoom)
  Settings  theme (system, light, dark), API URL, app version
```

Across screens: skeleton loaders instead of spinners, pull-to-refresh on every
list, and an error state with a retry button and the correlation ID.

## Repository layout

```text
app/src/main/kotlin/uk/co/fredjames/jsonplaceholder/
  MainActivity.kt, JsonPlaceholderApplication.kt
  navigation/                 type-safe routes, NavHost, bottom bar
  core/config/                AppConfig: BuildConfig parsing and validation
  core/network/               Retrofit service, interceptors, Envelope<T>, ApiError
  core/data/                  one repository per resource, cache and retry policy
  core/ui/                    shared composables: ScreenScaffold, ErrorState, Skeleton, ColourTile
  core/theme/                 Material 3 schemes, typography, theme mode
  feature/<feature>/          ViewModels, screens and cards
app/src/test/kotlin/          JVM unit tests and Robolectric Compose tests, mirroring main's packages
app/src/debug/                debug-only resources (network security config)
openapi/                      contract snapshot
.maestro/                     e2e flows
docs/README-<topic>.md        topic docs
README.md                     overview and docs index
CLAUDE.md                     workflow rules
```

Rules:

- A feature never imports from another feature. Shared code moves to `core/`.
- `core/ui/` has no data dependencies.
- Repositories are shared per resource (`UserRepository`), not per feature. This
  is where the app deliberately departs from the React Native app, whose
  features each define their own hooks over TanStack Query's shared cache. See
  `docs/README-comparison.md`.

## Phases

Each phase ends with a working app, passing checks, updated docs and a new
section in `docs/README-comparison.md`.

### Phase 0: Environment and scaffold

- [x] Project on the Windows filesystem, opened in Android Studio 2026.1
- [x] Gradle wrapper (9.7.1, checksum-pinned), Kotlin DSL, version catalog, AGP 9.4 with built-in Kotlin 2.4
- [x] Kotlin `allWarningsAsErrors`; ktlint with compose-rules; Android Lint with `warningsAsErrors`
- [x] JUnit, Robolectric and Compose UI tests, with passing tests; Kover reports
- [x] `AppConfig`: API URL from a Gradle property, inlined into `BuildConfig`, validated at startup
- [x] Debug-only network security config allowing plain HTTP to the local API
- [x] App running on the `Pixel_8` emulator (API 37), which reaches the API in WSL through `10.0.2.2`
- [x] `README.md`, `CLAUDE.md`, `docs/README-getting-started.md`, `docs/README-code-quality.md`,
      `docs/README-testing.md`, `docs/README-comparison.md`

Learn: Gradle, AGP, the manifest, build variants, Android Studio compared with the Expo toolchain.

### Phase 1: Theme and app shell

- [ ] Material 3 light and dark schemes, Inter from `res/font`; decide on dynamic colour (Material You)
- [ ] `ThemeMode` (system, light, dark), persisted with DataStore
- [ ] Hilt; `AppConfig` provided through DI instead of the `Application` field
- [ ] Type-safe Navigation Compose, a four-tab `NavigationBar` with a back stack per tab
- [ ] Shared composables (`ScreenScaffold`, `ErrorState`, `EmptyState`, `Skeleton`, `ColourTile`), each with `@Preview`s
- [ ] `docs/README-ui.md`

Learn: recomposition, `remember`, state hoisting, Previews compared with Fast Refresh.

### Phase 2: API layer

- [ ] `openapi/proxy.json` snapshot and an `apiSync` Gradle task
- [ ] Spike: generate models only, with a hand-written `Envelope<T>` and Retrofit interface, or the full client
- [ ] OkHttp + Retrofit + kotlinx.serialization; correlation ID interceptor; logging in debug only
- [ ] `ApiError` with `isRetryable`; an `apiCall { }` wrapper (the `unwrap` equivalent)
- [ ] Repositories: 60s stale time, shared in-flight requests, retry with backoff capped at 30s, refresh on app foreground
- [ ] `MockWebServer` dispatcher and fixtures; unit tests for `ApiError`, envelope parsing and retry (virtual time)
- [ ] `docs/README-api.md`

Learn: coroutines, `Flow`, structured concurrency, DI, and how much TanStack Query was doing.

### Phase 3: Posts (first vertical slice)

- [ ] `UiState` sealed interface; ViewModels that `combine` a primary flow with secondary ones
- [ ] Posts list with `LazyColumn`, `PullToRefreshBox`, skeletons, empty and error states
- [ ] Post detail with author (secondary) and comments (their own four states)
- [ ] ViewModel tests (Turbine) and Compose UI tests for each state
- [ ] `docs/README-architecture.md` describing the feature pattern

Learn: unidirectional data flow, ViewModels surviving configuration changes.

### Phase 4: People

- [ ] Directory with search (`debounce` in the ViewModel, query kept in `SavedStateHandle`)
- [ ] Profile with segmented Posts, Albums and Todos, each loaded on demand (`flatMapLatest`)
- [ ] Prefetch a profile when its row is pressed

Learn: Flow operators, process death.

### Phase 5: Albums and photos

- [ ] Album grid and photo grid with colour-tile fallbacks (Coil on top)
- [ ] Full-screen viewer: `HorizontalPager`, pinch and double-tap to zoom, paging disabled while zoomed
- [ ] List performance check on a low-end emulator profile, for both apps

Learn: lazy layouts (`key`, `contentType`), Compose gesture APIs, Coil.

### Phase 6: Polish

- [ ] State-change and screen transitions, list item animations
- [ ] Haptics on key interactions
- [ ] Offline banner (`ConnectivityManager` as a `callbackFlow`)
- [ ] App icon (Image Asset Studio) and the SplashScreen API
- [ ] Accessibility pass with TalkBack

Learn: what separates "works" from "feels good".

### Phase 7: Quality gates

- [ ] Maestro flows ported from the React Native app, and run
- [ ] Kover coverage thresholds
- [ ] Harness pipeline: `check` and `assembleRelease`
- [ ] R8-minified, signed release APK as a CI artifact
- [ ] `docs/README-ci.md`

Learn: CI/CD for native Android.

### Phase 8: Comparison and stretch goals

- [ ] Measure both apps: APK size, cold start, scroll jank, clean and incremental build time, test time, dependency count
- [ ] Finish `docs/README-comparison.md`
- [ ] Stretch: Room-backed offline-first data layer
- [ ] Stretch: the "new post" form with optimistic updates, in both apps
- [ ] Stretch: split into Gradle modules, so the feature-isolation rule is enforced by the compiler

## Out of scope

- Authentication, since the API has none
- iOS, which the React Native app covers
