package org.rhasspy.mobile.viewModels

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.TextToSpeechOptions
import org.rhasspy.mobile.services.HttpService
import org.rhasspy.mobile.services.native.NativeIndication
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings

class HomeScreenViewModel : ViewModel() {
    private val logger = Logger.withTag(this::class.simpleName!!)

    init {
        AppSettings.languageOption.value.addObserver {
            StringDesc.localeType = StringDesc.LocaleType.Custom(it.code)
        }
        AppSettings.isWakeWordLightIndication.value.addObserver {
            if (it) {
                NativeIndication.displayOverAppsPermission()
            }
        }
    }

    fun saveAndApplyChanges() {
        logger.i { "saveAndApplyChanges" }

        GlobalData.saveAllChanges()
        // ForegroundService.stopServices()
        //   ForegroundService.startServices()
    }

    fun resetChanges() {
        logger.i { "resetChanges" }

        GlobalData.resetChanges()
    }

    fun speak(text: String) {
        viewModelScope.launch {
            when (ConfigurationSettings.textToSpeechOption.data) {
                TextToSpeechOptions.RemoteHTTP -> HttpService.textToSpeech(text)
                TextToSpeechOptions.RemoteMQTT -> TODO()
                TextToSpeechOptions.Disabled -> TODO()
            }
        }
    }


    fun intentRecognition(text: String) {
        viewModelScope.launch {
            HttpService.intentRecognition(text)
        }
    }

    fun playRecording(){

    }

}