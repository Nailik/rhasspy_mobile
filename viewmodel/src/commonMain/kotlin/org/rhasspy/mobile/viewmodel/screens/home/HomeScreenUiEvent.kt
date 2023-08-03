package org.rhasspy.mobile.viewmodel.screens.home

sealed interface HomeScreenUiEvent {

    sealed interface Action : HomeScreenUiEvent {

        data object MicrophoneFabClick : Action
        data object TogglePlayRecording : Action

    }

}