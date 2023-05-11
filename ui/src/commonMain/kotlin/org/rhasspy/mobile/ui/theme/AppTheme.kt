package org.rhasspy.mobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/**
 * current App Theme
 */
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val colorScheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors
    SetSystemColor(0.dp)
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

val ContentPaddingLevel1 = PaddingValues(vertical = 16.dp)