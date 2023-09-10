package org.rhasspy.mobile.viewmodel.screens.main

sealed interface MainScreenUiEvent {

    sealed interface Action : MainScreenUiEvent {

        data object BackClick : Action
        data class CrashlyticsDialogResult(val result: Boolean) : Action
        data object CloseChangelog : Action

    }

}