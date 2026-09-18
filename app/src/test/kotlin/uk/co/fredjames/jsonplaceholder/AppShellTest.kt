package uk.co.fredjames.jsonplaceholder

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uk.co.fredjames.jsonplaceholder.core.theme.ThemeMode
import uk.co.fredjames.jsonplaceholder.core.theme.ThemeRepository

@RunWith(AndroidJUnit4::class)
class AppShellTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `shows navigation tab text when content loads`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = ThemeRepository(context)
        composeRule.setContent {
            AppShell(
                themeRepository = repo,
                currentThemeMode = ThemeMode.SYSTEM,
                onThemeChange = {},
            )
        }

        composeRule.onAllNodesWithText("Posts")[0].assertIsDisplayed()
        composeRule.onAllNodesWithText("Settings")[0].assertIsDisplayed()
    }
}
