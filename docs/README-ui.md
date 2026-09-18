# UI Architecture & Styling Strategy

This document details the layout, design system implementation, navigation architecture, and custom composable building blocks established for the UI stack.

## Architecture and Styling Details

### 1. Theme and Design System (Material 3)
- Built on top of **Material Design 3**, defining light and dark color schemes cleanly under `uk.co.fredjames.jsonplaceholder.core.theme`.
- **Dynamic Color (Material You)**: Automatically supported on Android 12+ (`Build.VERSION_CODES.S`) via `dynamicLightColorScheme` and `dynamicDarkColorScheme`. Falls back safely to explicit Material 3 purple/lavender brand schemes on older API levels.
- **Inter Font Integration**: Loaded asynchronously at runtime using the standard Google Fonts downloadable font provider API (`androidx.compose.ui:ui-text-google-fonts`), reducing total bundled APK file size while guaranteeing typography rendering.

### 2. Preference State Persistence
- **ThemeMode**: Managed using an explicit enum (`SYSTEM`, `LIGHT`, `DARK`).
- **DataStore Storage**: Preferences are persisted synchronously using **DataStore Preferences**, which avoids disk block issues and flows updates back reactively using Kotlin Coroutines `Flow`.
- **Hilt Injection**: Provided via dependencies injected by Hilt to fully separate state concerns from standard platform Activities.

### 3. Navigation Scheme (Type-Safe Navigation Compose)
- Enforces `@Serializable` type-safe routes under `uk.co.fredjames.jsonplaceholder.navigation`.
- Implements a modern tabbed layout structure utilizing `NavigationBar` and `NavigationBarItem`.
- Implements correct state preservation across tab state entries with dedicated parameters such as `saveState = true`, `launchSingleTop = true`, and `restoreState = true`.

### 4. Custom Architectural Components (`core/ui`)
- **ColourTile**: Safely parses a literal hex code string representation into an alpha-enabled `Color` box container.
- **ScreenScaffold**: Reusable high-level component standardizing `Scaffold` container wrapping and automated edge padding context delivery.
- **Skeleton**: Pure Compose shimmer animation simulating list-like loading states with linear transformations.
- **ErrorState**: Full surface error presenter hosting description metadata fields, optional retry callback invocation hooks, and trace IDs (`Ref: ...`).
- **EmptyState**: Lightweight layout providing semantic feedback whenever backend responses yield zero items.
