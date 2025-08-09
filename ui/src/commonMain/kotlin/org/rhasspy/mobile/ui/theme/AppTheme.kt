package org.rhasspy.mobile.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.rhasspy.mobile.data.theme.ThemeType
import org.rhasspy.mobile.resources.DarkThemeColors
import org.rhasspy.mobile.resources.LightThemeColors
import org.rhasspy.mobile.settings.AppSetting

/**
 * current App Theme
 */
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val colorScheme by getColorScheme()
    MaterialTheme(
        colorScheme = colorScheme,
        content = {
            Box(
                Modifier
                    .background(colorScheme.surfaceColorAtElevation(0.dp))
                    .padding(WindowInsets.systemBars.asPaddingValues()),
            ) {
                content()
            }
        }
    )
}

@Composable
fun getColorScheme(): State<ColorScheme> {
    val theme by AppSetting.themeType.data.collectAsState()
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val colorScheme = derivedStateOf {
        when (theme) {
            ThemeType.System -> if (isSystemInDarkTheme) DarkThemeColors else LightThemeColors
            ThemeType.Light -> LightThemeColors
            ThemeType.Dark -> DarkThemeColors
        }
    }
    return colorScheme
}

val ContentPaddingLevel1 = PaddingValues(vertical = 16.dp)