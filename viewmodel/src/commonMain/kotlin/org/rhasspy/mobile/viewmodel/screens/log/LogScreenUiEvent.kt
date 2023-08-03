package org.rhasspy.mobile.viewmodel.screens.log

sealed interface LogScreenUiEvent {

    sealed interface Change : LogScreenUiEvent {

        data object ToggleListAutoScroll : Change

        data object ManualListScroll : Change

    }

    sealed interface Action : LogScreenUiEvent {

        data object ShareLogFile : Action
        data object SaveLogFile : Action

    }

    sealed interface Consumed : LogScreenUiEvent {

        data object ShowSnackBar : Consumed

    }

}