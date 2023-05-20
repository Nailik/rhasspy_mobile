package org.rhasspy.mobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * HTML text to correctly display html
 */
@Composable
actual fun SetSystemColor(elevation: Dp) {
    val colorScheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors
    SetSystemColor(colorScheme.surfaceColorAtElevation(elevation))
}

@Composable
actual fun SetSystemColor(color: Color) {
    //TODO("Not yet implemented")
}