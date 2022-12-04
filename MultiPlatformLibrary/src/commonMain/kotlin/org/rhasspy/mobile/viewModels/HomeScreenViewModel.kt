package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.logic.StateMachine
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.nativeutils.MicrophonePermission
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.dialogManager.DialogManagerServiceState
import org.rhasspy.mobile.services.dialogManager.IDialogManagerService
import org.rhasspy.mobile.settings.AppSettings

class HomeScreenViewModel : ViewModel(), KoinComponent {

    private val dialogManagerServiceState = get<IDialogManagerService>().currentState

    val isActionEnabled =
        dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.Idle || it == DialogManagerServiceState.AwaitingHotWord }
    val isPlayingRecordingEnabled = isActionEnabled

    val isHotWordRecording = dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.AwaitingHotWord }
    val isRecording = dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.RecordingIntent }
    val isPlayingRecording = dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.PlayingAudio }

    val isMicrophonePermissionGranted: StateFlow<Boolean> = MicrophonePermission.granted
    val isHasError = MutableStateFlow(true).readOnly

    val isShowLogEnabled = AppSettings.isShowLogEnabled.data

    fun toggleSession() = StateMachine.toggleSessionManually()

    fun togglePlayRecording() = StateMachine.togglePlayRecording()

}