package uk.co.fredjames.jsonplaceholder.core.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import uk.co.fredjames.jsonplaceholder.R

private val LightColorScheme =
    lightColorScheme(
        primary = Color(0xFF6750A4),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFEADDFF),
        onPrimaryContainer = Color(0xFF21005D),
        secondary = Color(0xFF625B71),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFE8DEF8),
        onSecondaryContainer = Color(0xFF1D192B),
        background = Color(0xFFFEF7FF),
        onBackground = Color(0xFF1D1B20),
        surface = Color(0xFFFEF7FF),
        onSurface = Color(0xFF1D1B20),
        surfaceVariant = Color(0xFFE7E0EC),
        onSurfaceVariant = Color(0xFF49454F),
        outline = Color(0xFF79747E),
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = Color(0xFFD0BCFF),
        onPrimary = Color(0xFF381E72),
        primaryContainer = Color(0xFF4F378B),
        onPrimaryContainer = Color(0xFFEADDFF),
        secondary = Color(0xFFCCC2DC),
        onSecondary = Color(0xFF332D41),
        secondaryContainer = Color(0xFF4A4458),
        onSecondaryContainer = Color(0xFFE8DEF8),
        background = Color(0xFF141218),
        onBackground = Color(0xFFE6E1E5),
        surface = Color(0xFF141218),
        onSurface = Color(0xFFE6E1E5),
        surfaceVariant = Color(0xFF49454F),
        onSurfaceVariant = Color(0xFFCCC2DC),
        outline = Color(0xFF938F99),
    )

private val provider =
    GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.app_google_android_gms_fonts_certs,
    )

private val fontName = GoogleFont("Inter")

private val InterFontFamily =
    FontFamily(
        Font(googleFont = fontName, fontProvider = provider),
    )

private val AppTypography =
    Typography(
        displayLarge = TextStyle(fontFamily = InterFontFamily),
        displayMedium = TextStyle(fontFamily = InterFontFamily),
        displaySmall = TextStyle(fontFamily = InterFontFamily),
        headlineLarge = TextStyle(fontFamily = InterFontFamily),
        headlineMedium = TextStyle(fontFamily = InterFontFamily),
        headlineSmall = TextStyle(fontFamily = InterFontFamily),
        titleLarge = TextStyle(fontFamily = InterFontFamily),
        titleMedium = TextStyle(fontFamily = InterFontFamily),
        titleSmall = TextStyle(fontFamily = InterFontFamily),
        bodyLarge = TextStyle(fontFamily = InterFontFamily),
        bodyMedium = TextStyle(fontFamily = InterFontFamily),
        bodySmall = TextStyle(fontFamily = InterFontFamily),
        labelLarge = TextStyle(fontFamily = InterFontFamily),
        labelMedium = TextStyle(fontFamily = InterFontFamily),
        labelSmall = TextStyle(fontFamily = InterFontFamily),
    )

/** The app's core Material 3 theme. Supports dynamic colors on Android 12+. */
@Composable
fun JsonPlaceholderTheme(
    themeMode: ThemeMode,
    content: @Composable () -> Unit,
) {
    val darkTheme =
        when (themeMode) {
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
        }

    val colorScheme =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> {
                DarkColorScheme
            }

            else -> {
                LightColorScheme
            }
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}
