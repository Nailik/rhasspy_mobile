package org.rhasspy.mobile.viewModels.screens

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.logic.StateMachine
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.middleware.EventState
import org.rhasspy.mobile.nativeutils.MicrophonePermission
import org.rhasspy.mobile.services.dialogManager.DialogManagerServiceState
import org.rhasspy.mobile.services.dialogManager.IDialogManagerService
import org.rhasspy.mobile.settings.AppSettings

class HomeScreenViewModel : ViewModel(), KoinComponent {

    private val dialogManagerServiceState = get<IDialogManagerService>().currentDialogState

    val isActionEnabled = dialogManagerServiceState
        .mapReadonlyState { it == DialogManagerServiceState.Idle || it == DialogManagerServiceState.AwaitingHotWord }
    val isShowBorder = dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.AwaitingHotWord }
    val isPlayingRecording = dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.PlayingAudio }
    val isShowMicOn: StateFlow<Boolean> = MicrophonePermission.granted
    val isRecording =  dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.RecordingIntent }

    val isPlayingRecordingEnabled = isActionEnabled
    val isShowLogEnabled = AppSettings.isShowLogEnabled.data

    val serviceState = MutableStateFlow<EventState>(EventState.Pending)

    fun toggleSession() = StateMachine.toggleSessionManually()

    fun togglePlayRecording() = StateMachine.togglePlayRecording()

}