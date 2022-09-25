package org.rhasspy.mobile.android.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.rhasspy.mobile.data.ThemeOptions
import org.rhasspy.mobile.settings.AppSettings

@Composable
fun getIsDarkTheme(): Boolean {
    val themeOption = AppSettings.themeOption.data.collectAsState().value
    return (isSystemInDarkTheme() && themeOption == ThemeOptions.System) || themeOption == ThemeOptions.Dark
}

@Composable
fun AppTheme(systemUiTheme: Boolean, content: @Composable () -> Unit) {

    val isDarkTheme = getIsDarkTheme()
    val colorScheme = if (isDarkTheme) DarkThemeColors else LightThemeColors

    if (systemUiTheme) {
        //may be used inside overlay and then the context is not an activity
        val systemUiController = rememberSystemUiController()
        systemUiController.setSystemBarsColor(colorScheme.background, darkIcons = !isDarkTheme)
        systemUiController.setNavigationBarColor(colorScheme.background, darkIcons = !isDarkTheme)
        systemUiController.setStatusBarColor(colorScheme.background, darkIcons = !isDarkTheme)
    }

    MaterialTheme(colorScheme = colorScheme, content = content)
}
