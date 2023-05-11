package org.rhasspy.mobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.Dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * can be used to set system ui colors
 */
@Composable
actual fun SetSystemColor(elevation: Dp) {
    val systemUiController = rememberSystemUiController()
    val colorScheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors
    val color = colorScheme.surfaceColorAtElevation(elevation)
    LaunchedEffect(Unit) {
        systemUiController.setSystemBarsColor(color)
        systemUiController.setNavigationBarColor(color)
        systemUiController.setStatusBarColor(color)
    }
}