package org.rhasspy.mobile.viewmodel.overlay.microphone

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabUiEvent.Action.UserSessionClick
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabViewModel
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayUiEvent.Action
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayUiEvent.Action.ToggleUserSession
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayUiEvent.Change
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayUiEvent.Change.UpdateMicrophoneOverlayPosition
import kotlin.math.roundToInt

@Stable
class MicrophoneOverlayViewModel(
    private val nativeApplication: NativeApplication,
    private val microphoneFabViewModel: MicrophoneFabViewModel,
    viewStateCreator: MicrophoneOverlayViewStateCreator
) : ViewModel(), KoinComponent {

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
                val newPositionX = (AppSetting.microphoneOverlayPositionX.value + change.offsetX).roundToInt()
                val newPositionY = (AppSetting.microphoneOverlayPositionY.value + change.offsetY).roundToInt()
                AppSetting.microphoneOverlayPositionX.value = newPositionX
                AppSetting.microphoneOverlayPositionY.value = newPositionY
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            ToggleUserSession ->
                if (MicrophonePermission.granted.value) {
                    microphoneFabViewModel.onEvent(UserSessionClick)
                } else {
                    nativeApplication.startRecordingAction()
                }
        }
    }

}