package org.rhasspy.mobile.android.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController

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

/**
 * can be used to set system ui colors
 */
@Composable
fun SetSystemColor(elevation: Dp) {
    val systemUiController = rememberSystemUiController()
    val colorScheme = if (isSystemInDarkTheme()) DarkThemeColors else LightThemeColors
    val color = colorScheme.surfaceColorAtElevation(elevation)
    LaunchedEffect(Unit) {
        systemUiController.setSystemBarsColor(color)
        systemUiController.setNavigationBarColor(color)
        systemUiController.setStatusBarColor(color)
    }
}

val ContentPaddingLevel1 = PaddingValues(vertical = 16.dp)