package org.rhasspy.mobile.viewmodel.overlay.microphone

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.microphone.MicrophoneFabUiEvent.Action.MicrophoneFabClick
import org.rhasspy.mobile.viewmodel.microphone.MicrophoneFabViewModel
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayUiEvent.Action
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayUiEvent.Action.ToggleUserSession
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayUiEvent.Change
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayUiEvent.Change.UpdateMicrophoneOverlayPosition
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
class MicrophoneOverlayViewModel(
    private val nativeApplication: NativeApplication,
    private val microphoneFabViewModel: MicrophoneFabViewModel,
    viewStateCreator: MicrophoneOverlayViewStateCreator
) : ScreenViewModel() {

    val viewState: StateFlow<MicrophoneOverlayViewState> = viewStateCreator()

    fun onEvent(event: MicrophoneOverlayUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            is UpdateMicrophoneOverlayPosition -> {
                val newPositionX = (AppSetting.microphoneOverlayPositionX.value + change.offsetX).toInt()
                val newPositionY = (AppSetting.microphoneOverlayPositionY.value + change.offsetY).toInt()
                AppSetting.microphoneOverlayPositionX.value = newPositionX
                AppSetting.microphoneOverlayPositionY.value = newPositionY
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            ToggleUserSession ->
                if (microphonePermission.granted.value) {
                    microphoneFabViewModel.onEvent(MicrophoneFabClick)
                } else {
                    nativeApplication.startRecordingAction()
                }
        }
    }

}