package uk.co.fredjames.jsonplaceholder

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppShellTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `shows the app name and the configured API URL`() {
        composeRule.setContent { AppShell(apiUrl = "http://api.test") }

        composeRule.onNodeWithText("JSON Placeholder").assertIsDisplayed()
        composeRule.onNodeWithText("API: http://api.test").assertIsDisplayed()
    }
}
