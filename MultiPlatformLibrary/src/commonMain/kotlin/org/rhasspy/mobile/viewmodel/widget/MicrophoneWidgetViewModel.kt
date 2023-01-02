package org.rhasspy.mobile.viewmodel.widget

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.*
import org.rhasspy.mobile.koin.getSafe
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.nativeutils.MicrophonePermission
import org.rhasspy.mobile.services.dialog.DialogManagerService
import org.rhasspy.mobile.services.dialog.DialogManagerServiceState

class MicrophoneWidgetViewModel : ViewModel(), KoinComponent {

    private val dialogManagerServiceState
        get() = getSafe<DialogManagerService>()?.currentDialogState ?: MutableStateFlow(
            DialogManagerServiceState.Idle
        ).readOnly
    val isShowBorder =
        MutableStateFlow(true) // dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.AwaitingHotWord }
    val isShowMicOn: StateFlow<Boolean> = MicrophonePermission.granted
    val isRecording =
        MutableStateFlow(false) // dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.RecordingIntent }
    val isActionEnabled = dialogManagerServiceState
        .mapReadonlyState { it == DialogManagerServiceState.Idle || it == DialogManagerServiceState.AwaitingWakeWord }

    init {
        viewModelScope.launch {
            combineState(isShowBorder, isShowMicOn, isRecording, isActionEnabled) { _, _, _, _ ->
                viewModelScope.launch {
                    Application.nativeInstance.updateWidgetNative()
                }
            }.collect {

            }
        }
    }

    fun onTapWidget() {
        getSafe<IServiceMiddleware>()?.userSessionClick()
    }

}