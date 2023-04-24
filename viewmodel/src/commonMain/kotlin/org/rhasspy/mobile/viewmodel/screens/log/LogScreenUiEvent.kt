package org.rhasspy.mobile.viewmodel.screens.log

sealed interface LogScreenUiEvent {

    sealed interface Change : LogScreenUiEvent {

        object ToggleListAutoScroll : Change

    }

    sealed interface Navigate : LogScreenUiEvent {

        object ShareLogFile : Navigate
        object SaveLogFile : Navigate

    }

}