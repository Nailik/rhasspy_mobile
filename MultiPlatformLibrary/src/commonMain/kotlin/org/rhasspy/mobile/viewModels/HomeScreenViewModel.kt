package org.rhasspy.mobile.viewModels

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MediatorLiveData
import dev.icerock.moko.mvvm.livedata.map
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.StringDesc
import org.rhasspy.mobile.logger.FileLogger
import org.rhasspy.mobile.logic.StateMachine
import org.rhasspy.mobile.logic.StateMachine.manualIntentRecognition
import org.rhasspy.mobile.nativeutils.MicrophonePermission
import org.rhasspy.mobile.nativeutils.OverlayPermission
import org.rhasspy.mobile.services.RhasspyActions
import org.rhasspy.mobile.services.ServiceInterface
import org.rhasspy.mobile.settings.AppSettings

class HomeScreenViewModel : ViewModel() {
    private val logger = Logger.withTag("HomeScreenViewModel")

    private val isCurrentOverlayPermissionRequestRequired = MediatorLiveData(false)

    val isMicrophonePermissionRequestRequired: LiveData<Boolean> = MicrophonePermission.granted.map { !it }
    val isOverlayPermissionRequestRequired: LiveData<Boolean> = isCurrentOverlayPermissionRequestRequired.readOnly()

    init {
        logger.v { "init" }

        AppSettings.languageOption.data.observe {
            StringDesc.localeType = StringDesc.LocaleType.Custom(it.code)
        }

        isCurrentOverlayPermissionRequestRequired.addSource(OverlayPermission.granted) {
            isCurrentOverlayPermissionRequestRequired.value = (!it && AppSettings.isWakeWordLightIndication.value)
        }
        isCurrentOverlayPermissionRequestRequired.addSource(AppSettings.isWakeWordLightIndication.data.toLiveData()) {
            isCurrentOverlayPermissionRequestRequired.value = (it && !OverlayPermission.granted.value)
        }
    }

    val currentState = StateMachine.currentState
    val currentServiceState = ServiceInterface.currentState

    fun toggleSession() = StateMachine.toggleSessionManually()

    fun togglePlayRecording() = StateMachine.togglePlayRecording()

    fun intentRecognition(text: String) = manualIntentRecognition(text)

    fun speakText(text: String) = RhasspyActions.say(text)

    fun saveAndApplyChanges() = ServiceInterface.saveAndApplyChanges()

    fun resetChanges() = ServiceInterface.resetChanges()

    fun shareLogFile() = FileLogger.shareLogFile()

    fun saveLogFile() = FileLogger.saveLogFile()
}