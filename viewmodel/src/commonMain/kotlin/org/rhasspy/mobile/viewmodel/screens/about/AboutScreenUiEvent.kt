package org.rhasspy.mobile.viewmodel.screens.about

sealed interface AboutScreenUiEvent {

    sealed interface Navigate : AboutScreenUiEvent{

        object OpenSourceCode : Navigate

    }

}