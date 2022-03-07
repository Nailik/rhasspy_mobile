package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import org.rhasspy.mobile.data.LanguageOptions
import org.rhasspy.mobile.data.ThemeOptions

object SettingsData {

    val languageOption = MutableLiveData(LanguageOptions.English)
    val themeOption = MutableLiveData(ThemeOptions.System)

    val automaticSilenceDetection = MutableLiveData(false)

    val isBackgroundWakeWordDetection = MutableLiveData(false)
    val isBackgroundWakeWordDetectionTurnOnDisplay = MutableLiveData(false)

    val isWakeWordSoundIndication = MutableLiveData(false)
    val isWakeWordLightIndication = MutableLiveData(false)

    val isShowLog = MutableLiveData(false)

}