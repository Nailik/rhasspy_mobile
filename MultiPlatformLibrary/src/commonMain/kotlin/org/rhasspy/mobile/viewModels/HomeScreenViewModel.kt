package org.rhasspy.mobile.viewModels

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MediatorLiveData
import dev.icerock.moko.mvvm.livedata.map
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.StringDesc
import org.rhasspy.mobile.logger.FileLogger
import org.rhasspy.mobile.nativeutils.MicrophonePermission
import org.rhasspy.mobile.nativeutils.OverlayPermission
import org.rhasspy.mobile.services.RhasspyActions
import org.rhasspy.mobile.services.logic.StateMachine
import org.rhasspy.mobile.settings.AppSettings

class HomeScreenViewModel : ViewModel() {
    private val logger = Logger.withTag("HomeScreenViewModel")

    private val isCurrentOverlayPermissionRequestRequired = MediatorLiveData(false)

    val isMicrophonePermissionRequestRequired: LiveData<Boolean> = MicrophonePermission.granted.map { !it }
    val isOverlayPermissionRequestRequired: LiveData<Boolean> = isCurrentOverlayPermissionRequestRequired.readOnly()

    init {
        logger.v { "init" }

        AppSettings.languageOption.value.addObserver {
            StringDesc.localeType = StringDesc.LocaleType.Custom(it.code)
        }

        isCurrentOverlayPermissionRequestRequired.addSource(OverlayPermission.granted) {
            isCurrentOverlayPermissionRequestRequired.value = (!it && AppSettings.isWakeWordLightIndication.data)
        }
        isCurrentOverlayPermissionRequestRequired.addSource(AppSettings.isWakeWordLightIndication.value) {
            isCurrentOverlayPermissionRequestRequired.value = (it && !OverlayPermission.granted.value)
        }
    }

    val isRecording = RhasspyActions.sessionRunning
    val isPlayingRecording = RhasspyActions.isPlayingRecording

    fun toggleSession() = RhasspyActions.toggleSession()

    fun playRecording() = StateMachine.playRecording()

    fun intentRecognition(text: String) = RhasspyActions.recognizeIntent(text)

    fun speakText(text: String) = RhasspyActions.say(text)

    fun saveAndApplyChanges() = RhasspyActions.saveAndApplyChanges()

    fun resetChanges() = RhasspyActions.resetChanges()

    fun shareLogFile() = FileLogger.shareLogFile()

    fun saveLogFile() = FileLogger.saveLogFile()


}