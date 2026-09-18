package uk.co.fredjames.jsonplaceholder

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.co.fredjames.jsonplaceholder.core.theme.JsonPlaceholderTheme
import uk.co.fredjames.jsonplaceholder.core.theme.ThemeMode
import uk.co.fredjames.jsonplaceholder.core.theme.ThemeRepository
import uk.co.fredjames.jsonplaceholder.navigation.NavigationShell

/** The app's root composable coordinating theme state management and child screens. */
@Composable
fun AppShell(
    themeRepository: ThemeRepository,
    onThemeChange: (ThemeMode) -> Unit,
    currentThemeMode: ThemeMode,
    modifier: Modifier = Modifier,
) {
    JsonPlaceholderTheme(themeMode = currentThemeMode) {
        NavigationShell(
            themeRepository = themeRepository,
            currentThemeMode = currentThemeMode,
            onThemeChange = onThemeChange,
            modifier = modifier,
        )
    }
}
