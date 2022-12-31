package org.rhasspy.mobile.viewmodel.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.StringDesc
import org.rhasspy.mobile.data.LanguageOptions
import org.rhasspy.mobile.settings.AppSettings

class LanguageSettingsViewModel : ViewModel() {

    //unsaved ui data
    val languageOption = AppSettings.languageOption.data

    //all options
    val languageOptions = LanguageOptions::values

    //select log level
    fun selectLanguageOption(option: LanguageOptions) {
        StringDesc.localeType = StringDesc.LocaleType.Custom(option.code)
        AppSettings.languageOption.value = option
    }

}