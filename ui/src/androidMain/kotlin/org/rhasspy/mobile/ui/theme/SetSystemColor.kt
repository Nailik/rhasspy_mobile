package org.rhasspy.mobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.rhasspy.mobile.resources.DarkThemeColors
import org.rhasspy.mobile.resources.LightThemeColors

/**
 * can be used to set system ui colors
 */
@Composable
actual fun SetSystemColor(elevation: Dp) {
    val colorScheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors
    SetSystemColor(colorScheme.surfaceColorAtElevation(elevation))
}

@Composable
actual fun SetSystemColor(color: Color) {
    val systemUiController = rememberSystemUiController()
    LaunchedEffect(Unit) {
        systemUiController.setSystemBarsColor(color)
        systemUiController.setNavigationBarColor(color)
        systemUiController.setStatusBarColor(color)
    }
}