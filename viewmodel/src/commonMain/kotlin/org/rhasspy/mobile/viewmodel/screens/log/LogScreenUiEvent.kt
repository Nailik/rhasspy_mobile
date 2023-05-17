package org.rhasspy.mobile.viewmodel.screens.log

sealed interface LogScreenUiEvent {

    sealed interface Change : LogScreenUiEvent {

        object ToggleListAutoScroll : Change

    }

    sealed interface Action : LogScreenUiEvent {

        object ShareLogFile : Action
        object SaveLogFile : Action

    }

    sealed interface Consumed : LogScreenUiEvent {

        object ShowSnackBar : Consumed

    }

}