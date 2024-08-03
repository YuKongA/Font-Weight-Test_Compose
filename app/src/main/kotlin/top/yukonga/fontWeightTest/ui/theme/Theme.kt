package top.yukonga.fontWeightTest.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun AppTheme(
    colorMode: Int = 0,
    content: @Composable () -> Unit
) {
    return MaterialTheme(
        colorScheme = getColorScheme(colorMode),
        content = content
    )
}

@Composable
fun getColorScheme(colorMode: Int): ColorScheme {
    val context = LocalContext.current
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            when (colorMode) {
                1 -> dynamicLightColorScheme(context)
                2 -> dynamicDarkColorScheme(context)
                else -> if (isSystemInDarkTheme()) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
        }

        else -> {
            when (colorMode) {
                1 -> lightColorScheme()
                2 -> darkColorScheme()
                else -> if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
            }
        }
    }
}