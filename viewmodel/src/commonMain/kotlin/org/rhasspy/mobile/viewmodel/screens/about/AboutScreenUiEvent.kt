package org.rhasspy.mobile.viewmodel.screens.about

sealed interface AboutScreenUiEvent {

    sealed interface Action : AboutScreenUiEvent{

        object OpenSourceCode : Action

    }

}