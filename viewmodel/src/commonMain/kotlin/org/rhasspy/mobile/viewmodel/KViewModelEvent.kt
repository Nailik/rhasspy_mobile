package org.rhasspy.mobile.viewmodel

sealed interface KViewModelEvent {

    sealed interface Action : KViewModelEvent {

        object RequestMicrophonePermission : Action
        object RequestMicrophonePermissionRedirect : Action
        data class MicrophonePermissionDialogResult(val confirm: Boolean) : Action
        object RequestOverlayPermission : Action
        data class OverlayPermissionDialogResult(val confirm: Boolean) : Action

    }

    sealed interface Consumed : KViewModelEvent {

        object ConsumedMicrophonePermissionSnackBar : Consumed
        object ConsumedOverlayPermissionSnackBar : Consumed

    }

}