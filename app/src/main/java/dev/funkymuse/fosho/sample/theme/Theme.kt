package dev.funkymuse.fosho.sample.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple200,
    secondary = Purple700,
    tertiary = Teal200
)

private val LightColorScheme = lightColorScheme(
    primary = Purple500,
    secondary = Purple700,
    tertiary = Teal200
)

@Composable
fun FoShoLibTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
        shapes = Shapes,
        typography = Typography
    )
}

@Composable
fun FoShoLibThemeSurface(content: @Composable () -> Unit) {
    FoShoLibTheme {
        Surface(content = content)
    }
}