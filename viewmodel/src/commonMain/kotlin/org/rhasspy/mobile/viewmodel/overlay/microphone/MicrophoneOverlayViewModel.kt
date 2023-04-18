package org.rhasspy.mobile.viewmodel.overlay.microphone

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.updateViewStateFlow
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabUiEvent.Action.UserSessionClick
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabViewModel
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayUiEvent.Action
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayUiEvent.Action.ToggleUserSession
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayUiEvent.Change
import org.rhasspy.mobile.viewmodel.overlay.microphone.MicrophoneOverlayUiEvent.Change.UpdateMicrophoneOverlayPosition
import kotlin.math.roundToInt

class MicrophoneOverlayViewModel(
    private val nativeApplication: NativeApplication,
    private val microphoneFabViewModel: MicrophoneFabViewModel
) : ViewModel(), KoinComponent {

    private val _viewState =
        MutableStateFlow(MicrophoneOverlayViewState(nativeApplication))
    val viewState = _viewState.readOnly

    init {
        MicrophoneOverlayViewStateUpdater(
            _viewState = _viewState,
            nativeApplication = nativeApplication
        )
    }

    fun onEvent(event: MicrophoneOverlayUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.updateViewStateFlow {
            when (change) {
                is UpdateMicrophoneOverlayPosition -> {
                    val newPositionX = (AppSetting.microphoneOverlayPositionX.value + change.offsetX).roundToInt()
                    val newPositionY = (AppSetting.microphoneOverlayPositionX.value + change.offsetY).roundToInt()
                    AppSetting.microphoneOverlayPositionX.value = newPositionX
                    AppSetting.microphoneOverlayPositionY.value = newPositionY
                    copy(
                        microphoneOverlayPositionX = newPositionX,
                        microphoneOverlayPositionY = newPositionY
                    )
                }
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

    fun updateMicrophoneOverlayPosition(offsetX: Float, offsetY: Float) {
        AppSetting.microphoneOverlayPositionX.value =
            (((AppSetting.microphoneOverlayPositionX.value + offsetX).roundToInt()))
        AppSetting.microphoneOverlayPositionY.value =
            (((AppSetting.microphoneOverlayPositionY.value + offsetY).roundToInt()))
    }

}