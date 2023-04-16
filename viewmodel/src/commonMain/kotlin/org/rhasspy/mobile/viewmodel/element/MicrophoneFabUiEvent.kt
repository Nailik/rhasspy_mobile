package org.rhasspy.mobile.viewmodel.element

sealed interface MicrophoneFabUiEvent {

    sealed interface Action: MicrophoneFabUiEvent {

        object UserSessionClick: Action

    }

}