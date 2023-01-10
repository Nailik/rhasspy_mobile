package org.rhasspy.mobile.viewmodel.screens

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.middleware.Action
import org.rhasspy.mobile.middleware.ServiceMiddleware
import org.rhasspy.mobile.services.dialog.DialogManagerService
import org.rhasspy.mobile.services.dialog.DialogManagerServiceState
import org.rhasspy.mobile.settings.AppSetting

class HomeScreenViewModel : ViewModel(), KoinComponent {

    private val dialogManagerServiceState = get<DialogManagerService>().currentDialogState
    private val serviceMiddleware by inject<ServiceMiddleware>()

    val isPlayingRecording =
        dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.PlayingAudio }
    val isPlayingRecordingEnabled =
        dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.Idle || it == DialogManagerServiceState.AwaitingWakeWord }
    val isShowLogEnabled = AppSetting.isShowLogEnabled.data

    fun togglePlayRecording() = serviceMiddleware.action(Action.PlayStopRecording)

}