package org.rhasspy.mobile.resources

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * all used colors
 */
private val md_theme_light_primary = Color(0xFF6750A4)
private val md_theme_light_onPrimary = Color(0xFFFFFFFF)
private val md_theme_light_primaryContainer = Color(0xFFEADDFF)
private val md_theme_light_onPrimaryContainer = Color(0xFF21005D)
private val md_theme_light_secondary = Color(0xFF625B71)
private val md_theme_light_onSecondary = Color(0xFFFFFFFF)
private val md_theme_light_secondaryContainer = Color(0xFFE8DEF8)
private val md_theme_light_onSecondaryContainer = Color(0xFF1D192B)
private val md_theme_light_tertiary = Color(0xFF7D5260)
private val md_theme_light_onTertiary = Color(0xFFFFFFFF)
private val md_theme_light_tertiaryContainer = Color(0xFFFFD8E4)
private val md_theme_light_onTertiaryContainer = Color(0xFF31111D)
private val md_theme_light_error = Color(0xFFF9DEDC)
private val md_theme_light_errorContainer = Color(0xFFFA473F)
private val md_theme_light_onError = Color(0xFF410E0B)
private val md_theme_light_onErrorContainer = Color(0xFFFFFFFF)
private val md_theme_light_background = Color(0xFFFFFBFE)
private val md_theme_light_onBackground = Color(0xFF1C1B1F)
private val md_theme_light_surface = Color(0xFFFFFBFE)
private val md_theme_light_onSurface = Color(0xFF1C1B1F)
private val md_theme_light_surfaceVariant = Color(0xFFE7E0EC)
private val md_theme_light_onSurfaceVariant = Color(0xFF49454F)
private val md_theme_light_outline = Color(0xFF79747E)
private val md_theme_light_inverseOnSurface = Color(0xFFF4EFF4)
private val md_theme_light_inverseSurface = Color(0xFF313033)
private val md_theme_light_inversePrimary = Color(0xFFD0BCFF)

private val md_theme_dark_primary = Color(0xFFD0BCFF)
private val md_theme_dark_onPrimary = Color(0xFF381E72)
private val md_theme_dark_primaryContainer = Color(0xFF4F378B)
private val md_theme_dark_onPrimaryContainer = Color(0xFFEADDFF)
private val md_theme_dark_secondary = Color(0xFFCCC2DC)
private val md_theme_dark_onSecondary = Color(0xFF332D41)
private val md_theme_dark_secondaryContainer = Color(0xFF4A4458)
private val md_theme_dark_onSecondaryContainer = Color(0xFFE8DEF8)
private val md_theme_dark_tertiary = Color(0xFFEFB8C8)
private val md_theme_dark_onTertiary = Color(0xFF492532)
private val md_theme_dark_tertiaryContainer = Color(0xFF633B48)
private val md_theme_dark_onTertiaryContainer = Color(0xFFFFD8E4)
private val md_theme_dark_error = Color(0xFFF2B8B5)
private val md_theme_dark_errorContainer = Color(0xFF8C1D18)
private val md_theme_dark_onError = Color(0xFF601410)
private val md_theme_dark_onErrorContainer = Color(0xFFF9DEDC)
private val md_theme_dark_background = Color(0xFF1C1B1F)
private val md_theme_dark_onBackground = Color(0xFFE6E1E5)
private val md_theme_dark_surface = Color(0xFF1C1B1F)
private val md_theme_dark_onSurface = Color(0xFFE6E1E5)
private val md_theme_dark_surfaceVariant = Color(0xFF49454F)
private val md_theme_dark_onSurfaceVariant = Color(0xFFCAC4D0)
private val md_theme_dark_outline = Color(0xFF938F99)
private val md_theme_dark_inverseOnSurface = Color(0xFF1C1B1F)
private val md_theme_dark_inverseSurface = Color(0xFFE6E1E5)
private val md_theme_dark_inversePrimary = Color(0xFF6750A4)

private val color_warn_dark = Color(0xDCFDCE3E)
private val color_warn_light = Color(0xFFFCBE7E)

private val on_color_warn_dark = Color(0xFF363438)
private val on_color_warn_light = Color(0xFF363438)

private val assistant_color_one_any = Color(0xFF2196F3)
private val assistant_color_two_any = Color(0xFFF44336)
private val assistant_color_three_any = Color(0xFFFFEB3B)
private val assistant_color_four_any = Color(0xFF4CAF50)

private val color_verbose_any = Color(0xFF00BCD4)
private val color_debug_any = Color(0xFF2196F3)
private val color_info_any = Color(0xFFCDDC39)
private val color_warn_any = Color(0xFFFF9800)
private val color_error_any = Color(0xFFF44336)
private val color_assert_any = Color(0xFF673AB7)

private val color_http_any = Color(0xFF2196F3)
private val color_local_any = Color(0xFFF44336)
private val color_mqtt_any = Color(0xFFFFEB3B)
private val color_home_assistant_any = Color(0xFFCDDC39)
private val color_webserver_any = Color(0xFFFF9800)
private val color_user_any = Color(0xFF673AB7)

val ColorScheme.warn: Color
    @Composable
    get() = if (isSystemInDarkTheme()) color_warn_dark else color_warn_light

val ColorScheme.on_color_warn: Color
    @Composable
    get() = if (isSystemInDarkTheme()) on_color_warn_dark else on_color_warn_light

val ColorScheme.assistant_color_one: Color
    get() = assistant_color_one_any
val ColorScheme.assistant_color_two: Color
    get() = assistant_color_two_any
val ColorScheme.assistant_color_three: Color
    get() = assistant_color_three_any
val ColorScheme.assistant_color_four: Color
    get() = assistant_color_four_any

val ColorScheme.color_verbose: Color
    get() = color_verbose_any
val ColorScheme.color_debug: Color
    get() = color_debug_any
val ColorScheme.color_info: Color
    get() = color_info_any
val ColorScheme.color_warn: Color
    get() = color_warn_any
val ColorScheme.color_error: Color
    get() = color_error_any
val ColorScheme.color_assert: Color
    get() = color_assert_any

val ColorScheme.color_http: Color
    get() = color_http_any
val ColorScheme.color_local: Color
    get() = color_local_any
val ColorScheme.color_mqtt: Color
    get() = color_mqtt_any
val ColorScheme.color_home_assistant: Color
    get() = color_home_assistant_any
val ColorScheme.color_webserver: Color
    get() = color_webserver_any
val ColorScheme.color_user: Color
    get() = color_user_any

/**
 * colors for light theme
 */
val LightThemeColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
)

/**
 * colors for dark theme
 */
val DarkThemeColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
)