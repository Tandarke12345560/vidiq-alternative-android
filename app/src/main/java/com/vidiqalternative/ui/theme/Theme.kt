package com.vidiqalternative.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF6200EE),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFE8DEF8),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF21005D),
    secondary = androidx.compose.ui.graphics.Color(0xFF03DAC5),
    onSecondary = androidx.compose.ui.graphics.Color.Black,
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFCAF5F0),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF002020),
    background = androidx.compose.ui.graphics.Color(0xFFFFFBFE),
    onBackground = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    surface = androidx.compose.ui.graphics.Color(0xFFFFFBFE),
    onSurface = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFE7E0EC),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF49454F),
    error = androidx.compose.ui.graphics.Color(0xFFB3261E),
    onError = androidx.compose.ui.graphics.Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFBB86FC),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF381E72),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF4F378B),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFEADDFF),
    secondary = androidx.compose.ui.graphics.Color(0xFF03DAC5),
    onSecondary = androidx.compose.ui.graphics.Color(0xFF003731),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF005048),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF70F5ED),
    background = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    onBackground = androidx.compose.ui.graphics.Color(0xFFE6E1E5),
    surface = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    onSurface = androidx.compose.ui.graphics.Color(0xFFE6E1E5),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF49454F),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFCAC4D0),
    error = androidx.compose.ui.graphics.Color(0xFFF2B8B5),
    onError = androidx.compose.ui.graphics.Color(0xFF601410)
)

@Composable
fun VidIQTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
