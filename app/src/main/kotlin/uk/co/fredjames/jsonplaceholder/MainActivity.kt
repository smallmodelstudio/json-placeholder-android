package uk.co.fredjames.jsonplaceholder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uk.co.fredjames.jsonplaceholder.core.theme.ThemeMode
import uk.co.fredjames.jsonplaceholder.core.theme.ThemeRepository
import javax.inject.Inject

/** Root entry point activity with dynamic edge-to-edge styling and theme flow consumption. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var themeRepository: ThemeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val currentThemeMode by themeRepository.themeMode.collectAsState(initial = ThemeMode.SYSTEM)

            AppShell(
                themeRepository = themeRepository,
                currentThemeMode = currentThemeMode,
                onThemeChange = { newMode ->
                    lifecycleScope.launch {
                        themeRepository.setThemeMode(newMode)
                    }
                },
            )
        }
    }
}
