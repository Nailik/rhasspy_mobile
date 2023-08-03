package org.rhasspy.mobile.viewmodel.microphone

sealed interface MicrophoneFabUiEvent {

    sealed interface Action : MicrophoneFabUiEvent {

        object MicrophoneFabClick : Action

    }

}