package org.rhasspy.mobile.viewmodel.screens.home

sealed interface HomeScreenUiEvent {

    sealed interface Action : HomeScreenUiEvent {

        object TogglePlayRecording : Action

    }

}