package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.combineState
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.nativeutils.MicrophonePermission
import org.rhasspy.mobile.services.dialogManager.DialogManagerServiceState
import org.rhasspy.mobile.services.dialogManager.IDialogManagerService

class MicrophoneWidgetViewModel : ViewModel(), KoinComponent {

    private val dialogManagerServiceState = get<IDialogManagerService>().currentDialogState
    val isShowBorder = MutableStateFlow(true) // dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.AwaitingHotWord }
    val isShowMicOn: StateFlow<Boolean> = MicrophonePermission.granted
    val isRecording = MutableStateFlow(false) // dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.RecordingIntent }
    val isActionEnabled = dialogManagerServiceState
        .mapReadonlyState { it == DialogManagerServiceState.Idle || it == DialogManagerServiceState.AwaitingHotWord }

    init {
        viewModelScope.launch {
            combineState(isShowBorder, isShowMicOn, isRecording, isActionEnabled) { a, b, c, d ->
                viewModelScope.launch {
                    Application.Instance.updateWidgetNative()
                }
            }.collect {

            }
        }
    }

    fun onTapWidget() {
        //TODO open app if no permission
        isRecording.value = !isRecording.value
    }

}