package uk.co.fredjames.jsonplaceholder.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import uk.co.fredjames.jsonplaceholder.core.theme.ThemeMode
import uk.co.fredjames.jsonplaceholder.core.theme.ThemeRepository
import uk.co.fredjames.jsonplaceholder.core.ui.ScreenScaffold

/** Core app shell wrapping four-tab NavigationBar and its child NavHost subgraphs. */
@Composable
fun NavigationShell(
    themeRepository: ThemeRepository,
    currentThemeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val tabs =
        listOf(
            TabInfo("Posts", NavigationRoute.Posts),
            TabInfo("People", NavigationRoute.People),
            TabInfo("Albums", NavigationRoute.Albums),
            TabInfo("Settings", NavigationRoute.Settings),
        )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    val isSelected = currentDestination?.route?.contains(tab.route::class.qualifiedName ?: "") == true
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text(text = tab.title.take(1)) },
                        label = { Text(text = tab.title) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationRoute.Posts,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable<NavigationRoute.Posts> {
                ScreenScaffold(title = "Posts") {
                    PlaceholderContent(name = "Posts Subgraph Screen")
                }
            }
            composable<NavigationRoute.People> {
                ScreenScaffold(title = "People") {
                    PlaceholderContent(name = "People Subgraph Screen")
                }
            }
            composable<NavigationRoute.Albums> {
                ScreenScaffold(title = "Albums") {
                    PlaceholderContent(name = "Albums Subgraph Screen")
                }
            }
            composable<NavigationRoute.Settings> {
                ScreenScaffold(title = "Settings") {
                    SettingsContent(
                        currentThemeMode = currentThemeMode,
                        onThemeChange = onThemeChange,
                    )
                }
            }
        }
    }
}

private data class TabInfo(
    val title: String,
    val route: NavigationRoute,
)

@Composable
private fun PlaceholderContent(name: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = name)
    }
}

@Composable
private fun SettingsContent(
    currentThemeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Theme Selector Selection", style = MaterialTheme.typography.titleMedium)
        Text(text = "Current mode: $currentThemeMode", style = MaterialTheme.typography.bodyMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
            ThemeMode.entries.forEach { mode ->
                Button(
                    onClick = { onThemeChange(mode) },
                    enabled = currentThemeMode != mode,
                ) {
                    Text(text = mode.name)
                }
            }
        }
    }
}
