package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.combineState
import org.rhasspy.mobile.logic.StateMachine
import org.rhasspy.mobile.logic.StateMachine.manualIntentRecognition
import org.rhasspy.mobile.nativeutils.MicrophonePermission
import org.rhasspy.mobile.nativeutils.OverlayPermission
import org.rhasspy.mobile.services.recording.RecordingService
import org.rhasspy.mobile.settings.AppSettings

class HomeScreenViewModel : ViewModel(), KoinComponent {


    val isRecording = get<RecordingService>().isRecording
    val isOkState: StateFlow<Boolean> = MicrophonePermission.granted

    val isMicrophonePermissionRequestNotRequired: StateFlow<Boolean> = MicrophonePermission.granted

    private val isCurrentOverlayPermissionRequestRequired: StateFlow<Boolean> =
        combineState(OverlayPermission.granted, AppSettings.isWakeWordLightIndicationEnabled.data) { a: Boolean, b: Boolean ->
            (a && !b && AppSettings.isWakeWordLightIndicationEnabled.value)
        }

    val isOverlayPermissionRequestRequired: StateFlow<Boolean> get() = isCurrentOverlayPermissionRequestRequired


    val isShowLogEnabled: StateFlow<Boolean> get() = AppSettings.isShowLogEnabled.data

    fun toggleSession() = StateMachine.toggleSessionManually()

    fun togglePlayRecording() = StateMachine.togglePlayRecording()

    fun intentRecognition(text: String) = manualIntentRecognition(text)

    fun speakText(text: String) {

    }
}