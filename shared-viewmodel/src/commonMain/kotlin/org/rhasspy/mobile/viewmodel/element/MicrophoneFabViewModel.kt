package org.rhasspy.mobile.viewmodel.element

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.logic.combineState
import org.rhasspy.mobile.logic.getSafe
import org.rhasspy.mobile.logic.mapReadonlyState
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.nativeutils.MicrophonePermission
import org.rhasspy.mobile.logic.nativeutils.NativeApplication
import org.rhasspy.mobile.logic.readOnly
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerServiceState
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService

class MicrophoneFabViewModel : ViewModel(), KoinComponent {

    private val dialogManagerServiceState
        get() = getSafe<DialogManagerService>()?.currentDialogState ?: MutableStateFlow(
            DialogManagerServiceState.Idle
        ).readOnly
    val isShowBorder
        get() = getSafe<WakeWordService>()?.isRecording ?: MutableStateFlow(false)
    val isShowMicOn: StateFlow<Boolean> = MicrophonePermission.granted
    val isRecording get() = dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.RecordingIntent }
    val isActionEnabled
        get() = dialogManagerServiceState.mapReadonlyState {
            it == DialogManagerServiceState.Idle ||
                    it == DialogManagerServiceState.AwaitingWakeWord ||
                    it == DialogManagerServiceState.RecordingIntent
        }

    init {
        viewModelScope.launch {
            combineState(isShowBorder, isShowMicOn, isRecording, isActionEnabled) { _, _, _, _ ->
                listOf(isShowBorder, isShowMicOn, isRecording, isActionEnabled)
            }.collect {
                viewModelScope.launch {
                    get<NativeApplication>().updateWidgetNative()
                }
            }
        }
    }

    fun onClick() {
        getSafe<ServiceMiddleware>()?.userSessionClick()
    }

}