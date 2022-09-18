package org.rhasspy.mobile.android.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.rhasspy.mobile.android.utils.observe
import org.rhasspy.mobile.android.utils.toColors
import org.rhasspy.mobile.data.ThemeOptions
import org.rhasspy.mobile.settings.AppSettings

@Composable
fun getIsDarkTheme(): Boolean {
    val themeOption = AppSettings.themeOption.observe()
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

    androidx.compose.material.MaterialTheme(
        colors = colorScheme.toColors(isLight = !isDarkTheme),
        typography = MaterialTheme.typography.toOldTypography()
    ) {
        MaterialTheme(colorScheme = colorScheme, content = content)
    }
}

private fun Typography.toOldTypography(): androidx.compose.material.Typography {
    return androidx.compose.material.Typography(
        h1 = this.displayLarge,
        h2 = this.displayMedium,
        h3 = this.displaySmall,
        h4 = this.headlineLarge,
        h5 = this.headlineMedium,
        h6 = this.headlineSmall,
        subtitle1 = this.titleLarge,
        subtitle2 = this.titleSmall,
        body1 = this.bodyLarge,
        body2 = this.bodySmall,
        button = this.labelLarge,
        caption = this.labelMedium,
        overline = this.labelSmall,
    )
}