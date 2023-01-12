package org.rhasspy.mobile.android.navigation

/**
 * screens in the bottom bar
 */
enum class BottomBarScreenType(val route: String) {
    HomeScreen("BottomBarScreenType_HomeScreen"),
    ConfigurationScreen("BottomBarScreenType_ConfigurationScreen"),
    SettingsScreen("BottomBarScreenType_SettingsScreen"),
    LogScreen("BottomBarScreenType_LogScreen");

    fun appendOptionalParameter(parameter: NavigationParams, data: Any): String {
        return "${this.route}?${parameter.name}=$data"
    }
}
