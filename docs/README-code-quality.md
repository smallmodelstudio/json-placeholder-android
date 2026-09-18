# Code quality

The static checks (compiler, linting, formatting) and the rules they enforce.

## Checks

`.\gradlew.bat check` runs all of them. Each also runs on its own:

| Task            | Tool                                                                                  | Config                                   |
| --------------- | ------------------------------------------------------------------------------------- | ---------------------------------------- |
| `compile*Kotlin` | The Kotlin compiler, with every warning an error                                     | `kotlin { compilerOptions }` in `app/build.gradle.kts` |
| `ktlintCheck`   | ktlint (`ktlint_official` style) plus compose-rules' ktlint ruleset                   | `.editorconfig`                          |
| `ktlintFormat`  | Same rules, applying fixes                                                            | `.editorconfig`                          |
| `lintDebug`     | Android Lint: manifest, resources, API levels, Compose and AndroidX checks            | `android { lint }` in `app/build.gradle.kts` |

In the React Native app, Metro strips types without checking them, so a separate
`typecheck` is the only thing that catches a type error. Here the compiler *is* the
build: a type error, or any warning, stops `assembleDebug` as well as `check`.

## Kotlin compiler

`allWarningsAsErrors` is on. That includes deprecations: calling a deprecated API
fails the build straight away, so a migration happens when the library deprecates
something, not when it finally removes it.

Most of the strict TypeScript flags the React Native app turns on are built into
Kotlin:

| TypeScript flag              | Kotlin                                                                              |
| ---------------------------- | ----------------------------------------------------------------------------------- |
| `strict` (`strictNullChecks`) | Null safety is part of the type system: `String` can't be null, `String?` can       |
| `noUncheckedIndexedAccess`   | `list[0]` throws on an empty list; use `list.getOrNull(0)` or `firstOrNull()` to get a nullable |
| `noImplicitOverride`         | `override` is always required                                                        |
| `noFallthroughCasesInSwitch` | `when` never falls through; a `when` over a sealed type must be exhaustive          |
| `noUnusedLocals`             | A warning, so an error here; name an unused lambda parameter `_`                     |

## ktlint

`.editorconfig` sets the style for `*.kt` and `*.kts`:

- `ktlint_code_style = ktlint_official`, which keeps trailing commas (as Prettier does
  in the React Native app) and puts each parameter on its own line when a signature wraps.
- `max_line_length = 120`.
- `ktlint_function_naming_ignore_when_annotated_with = Composable`, since composables
  are PascalCase by convention.

The compose-rules ruleset (`io.nlopez.compose.rules:ktlint`, added through the
`ktlintRuleset` configuration) checks Compose conventions: for example, a public
composable that emits UI takes a `modifier: Modifier = Modifier` parameter, applied to
its root.

Detekt (complexity and code-smell checks) is deferred until its 2.0 release is stable;
see `docs/README-plan.md`.

## Android Lint

`warningsAsErrors` and `abortOnError` are on. Three checks are disabled:
`GradleDependency`, `NewerVersionAvailable` and `AndroidGradlePluginVersion`. They flag
newer library versions, which would turn a green build red whenever a library publishes
a release; version bumps are a deliberate change to the version catalog instead.

## Dependencies

Every dependency and plugin version lives in `gradle/libs.versions.toml` and is
referenced as `libs.…` from the build scripts. Never put a version inline in a build
script. Compose libraries take their versions from the Compose BOM
(`androidx-compose-bom`), so their catalog entries have no version of their own.

## Naming and layout

- One top-level class per file, named after it (`AppConfig.kt`). Files holding only
  composables are named after the main one (`AppShell.kt`).
- Packages follow `docs/README-plan.md`'s repository layout, under
  `uk.co.fredjames.jsonplaceholder`. Sources live in `src/<sourceSet>/kotlin/`.
- Classes and composables are PascalCase; functions and properties camelCase;
  constants `UPPER_SNAKE_CASE`.
- User-facing text goes in `res/values/strings.xml` and is read with `stringResource`,
  never hard-coded in a composable.
