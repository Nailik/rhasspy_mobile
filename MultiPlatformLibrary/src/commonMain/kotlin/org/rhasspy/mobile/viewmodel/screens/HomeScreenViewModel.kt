package org.rhasspy.mobile.viewmodel.screens

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.middleware.Action
import org.rhasspy.mobile.middleware.ServiceMiddleware
import org.rhasspy.mobile.middleware.ServiceState
import org.rhasspy.mobile.nativeutils.MicrophonePermission
import org.rhasspy.mobile.services.dialog.DialogManagerService
import org.rhasspy.mobile.services.dialog.DialogManagerServiceState
import org.rhasspy.mobile.settings.AppSetting

class HomeScreenViewModel : ViewModel(), KoinComponent {

    private val dialogManagerServiceState = get<DialogManagerService>().currentDialogState
    private val serviceMiddleware by inject<ServiceMiddleware>()

    val isActionEnabled = dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.Idle || it == DialogManagerServiceState.AwaitingWakeWord }
    val isShowBorder = dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.AwaitingWakeWord }
    val isPlayingRecording = dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.PlayingAudio }
    val isShowMicOn: StateFlow<Boolean> = MicrophonePermission.granted
    val isRecording = dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.RecordingIntent }

    val isPlayingRecordingEnabled = isActionEnabled
    val isShowLogEnabled = AppSetting.isShowLogEnabled.data

    val serviceState = MutableStateFlow<ServiceState>(ServiceState.Pending)

    fun toggleSession() = serviceMiddleware.userSessionClick()

    fun togglePlayRecording() = serviceMiddleware.action(Action.PlayStopRecording)

}