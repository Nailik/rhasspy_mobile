package org.rhasspy.mobile.viewmodel.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.logic.nativeutils.setLanguage
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.logic.settings.types.LanguageType

class LanguageSettingsViewModel : ViewModel() {

    //unsaved ui data
    val languageOption = AppSetting.languageType.data

    //all options
    val languageOptions = LanguageType::values

    //select log level
    fun selectLanguageOption(option: LanguageType) = setLanguage(option)

}