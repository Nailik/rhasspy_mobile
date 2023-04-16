package org.rhasspy.mobile.viewmodel.settings.language

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.data.language.LanguageType
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.language.setLanguage
import org.rhasspy.mobile.platformspecific.toImmutableList

class LanguageSettingsViewModel : ViewModel() {

    //unsaved ui data
    val languageOption = AppSetting.languageType.data

    //all options
    val languageOptions = LanguageType.values().toImmutableList()

    //select log level
    fun selectLanguageOption(option: LanguageType) {
        AppSetting.languageType.value = option
        setLanguage(option)
    }

}