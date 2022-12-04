package org.rhasspy.mobile.android.navigation

/**
 * screens in the bottom bar
 */
enum class BottomBarScreens {
    HomeScreen,
    ConfigurationScreen,
    SettingsScreen,
    LogScreen;

    fun appendOptionalParameter(parameter: NavigationParams, data: Any): String {
        return "${this.name}?${parameter.name}=$data"
    }
}
