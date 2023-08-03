package org.rhasspy.mobile.viewmodel.overlay.microphone

sealed interface MicrophoneOverlayUiEvent {

    sealed interface Change : MicrophoneOverlayUiEvent {

        data class UpdateMicrophoneOverlayPosition(val offsetX: Float, val offsetY: Float) : Change

    }

    sealed interface Action : MicrophoneOverlayUiEvent {

        data object ToggleUserSession : Action

    }

}