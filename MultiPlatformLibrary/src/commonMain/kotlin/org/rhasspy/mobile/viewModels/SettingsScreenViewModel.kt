package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.data.LanguageOptions
import org.rhasspy.mobile.data.ThemeOptions

class SettingsScreenViewModel : ViewModel()  {

    var languageOption = MutableLiveData(LanguageOptions.English)
    var themeOption = MutableLiveData(ThemeOptions.System)

    var automaticSilenceDetection = MutableLiveData(false)

    var isBackgroundWakeWordDetection = MutableLiveData(false)
    var isBackgroundWakeWordDetectionTurnOnDisplay = MutableLiveData(false)

    var isWakeWordSoundIndication = MutableLiveData(false)
    var isWakeWordLightIndication = MutableLiveData(false)

    var isShowLog = MutableLiveData(false)

}