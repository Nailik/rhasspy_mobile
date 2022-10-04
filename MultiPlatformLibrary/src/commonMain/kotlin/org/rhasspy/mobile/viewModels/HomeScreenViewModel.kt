package org.rhasspy.mobile.viewModels

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.combineState
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


    val isMicrophonePermissionRequestNotRequired: StateFlow<Boolean> = MicrophonePermission.granted

    private val isCurrentOverlayPermissionRequestRequired: StateFlow<Boolean> =
        combineState(OverlayPermission.granted, AppSettings.isWakeWordLightIndicationEnabled.data) { a: Boolean, b: Boolean ->
            (a && !b && AppSettings.isWakeWordLightIndicationEnabled.value)
        }

    val isOverlayPermissionRequestRequired: StateFlow<Boolean> get() = isCurrentOverlayPermissionRequestRequired

    init {
        logger.v { "init" }

        viewModelScope.launch {
            AppSettings.languageOption.data.collect {
                StringDesc.localeType = StringDesc.LocaleType.Custom(it.code)
            }
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