package org.rhasspy.mobile.viewmodel.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.StringDesc
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.types.LanguageType

class LanguageSettingsViewModel : ViewModel() {

    //unsaved ui data
    val languageOption = AppSetting.languageType.data

    //all options
    val languageOptions = LanguageType::values

    //select log level
    fun selectLanguageOption(option: LanguageType) {
        StringDesc.localeType = StringDesc.LocaleType.Custom(option.code)
        AppSetting.languageType.value = option
    }

}