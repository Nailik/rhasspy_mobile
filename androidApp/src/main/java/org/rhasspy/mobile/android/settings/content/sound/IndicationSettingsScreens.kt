package org.rhasspy.mobile.android.settings.content.sound

/**
 * holds enum for all indication settings screens
 */
enum class IndicationSettingsScreens(val route: String) {
    Overview("IndicationSettingsScreens_Overview"),
    WakeIndicationSound("IndicationSettingsScreens_WakeIndicationSound"),
    RecordedIndicationSound("IndicationSettingsScreens_RecordedIndicationSound"),
    ErrorIndicationSound("IndicationSettingsScreens_ErrorIndicationSound")
}