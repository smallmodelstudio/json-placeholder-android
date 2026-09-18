package uk.co.fredjames.jsonplaceholder.core.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import uk.co.fredjames.jsonplaceholder.core.theme.JsonPlaceholderTheme
import uk.co.fredjames.jsonplaceholder.core.theme.ThemeMode

/** A colored tile box rendering a hex color string (e.g., "#abcdef"). */
@Composable
fun ColourTile(
    hexColor: String,
    modifier: Modifier = Modifier,
) {
    val color =
        try {
            Color(hexColor.toColorInt())
        } catch (_: IllegalArgumentException) {
            Color.Gray
        }
    Box(
        modifier =
            modifier
                .size(100.dp)
                .background(color, shape = MaterialTheme.shapes.medium),
    )
}

/** Renders a skeleton shimmer placeholder row loader. */
@Composable
fun Skeleton(modifier: Modifier = Modifier) {
    val shimmerColors =
        listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        )
    val transition = rememberInfiniteTransition(label = "SkeletonShimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        label = "ShimmerTranslation",
    )
    val brush =
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnim, y = translateAnim),
        )
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(20.dp).background(brush))
        Box(modifier = Modifier.fillMaxWidth(0.7f).height(14.dp).background(brush))
    }
}

/** Error state composable showing message, retry button, and a correlation correlation ID. */
@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    correlationId: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Ref: $correlationId",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(text = "Retry")
        }
    }
}

/** Empty state composable showing an empty informational message. */
@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** ScreenScaffold provides standard top bar and layout structure wrapping pages. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenScaffold(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
            )
        },
        content = content,
    )
}

@Preview(showBackground = true)
@Composable
private fun ColourTilePreview() {
    JsonPlaceholderTheme(themeMode = ThemeMode.SYSTEM) {
        ColourTile(hexColor = "#6750A4")
    }
}

@Preview(showBackground = true)
@Composable
private fun SkeletonPreview() {
    JsonPlaceholderTheme(themeMode = ThemeMode.SYSTEM) {
        Skeleton()
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorStatePreview() {
    JsonPlaceholderTheme(themeMode = ThemeMode.SYSTEM) {
        ErrorState(
            message = "Failed to load posts",
            onRetry = {},
            correlationId = "ERR-404-XYZ",
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    JsonPlaceholderTheme(themeMode = ThemeMode.SYSTEM) {
        EmptyState(message = "No posts found")
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreenScaffoldPreview() {
    JsonPlaceholderTheme(themeMode = ThemeMode.SYSTEM) {
        ScreenScaffold(title = "Posts") { padding ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
