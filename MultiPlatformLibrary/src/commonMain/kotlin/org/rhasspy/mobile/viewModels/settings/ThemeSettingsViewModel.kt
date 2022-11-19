package org.rhasspy.mobile.viewModels.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.data.ThemeOptions
import org.rhasspy.mobile.settings.AppSettings

class ThemeSettingsViewModel : ViewModel() {

    //unsaved ui data
    val themeOption = AppSettings.themeOption.data

    //all options
    val themeOptions = ThemeOptions::values

    //select log level
    fun selectThemeOption(option: ThemeOptions) {
        AppSettings.themeOption.value = option
    }

}