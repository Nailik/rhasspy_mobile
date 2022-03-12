package org.rhasspy.mobile.viewModels

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.StringDesc
import org.rhasspy.mobile.services.RecordingService
import org.rhasspy.mobile.services.ServiceInterface
import org.rhasspy.mobile.services.native.AudioPlayer
import org.rhasspy.mobile.settings.AppSettings

class HomeScreenViewModel : ViewModel() {
    private val logger = Logger.withTag(this::class.simpleName!!)

    init {
        AppSettings.languageOption.value.addObserver {
            StringDesc.localeType = StringDesc.LocaleType.Custom(it.code)
        }
    }

    val isRecording = RecordingService.status

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

    fun textToSpeak(text: String) {
        ServiceInterface.textToSpeak(text)
    }

    fun intentRecognition(text: String) {
        ServiceInterface.intentRecognition(text)
    }

    fun toggleRecording() {
        ServiceInterface.toggleRecording()
    }

    fun playRecording() {
        AudioPlayer.playRecording(RecordingService.getLatestRecording())
    }

}