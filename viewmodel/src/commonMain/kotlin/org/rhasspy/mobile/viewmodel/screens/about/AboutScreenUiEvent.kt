package org.rhasspy.mobile.viewmodel.screens.about

import org.rhasspy.mobile.data.libraries.StableLibrary

sealed interface AboutScreenUiEvent {

    sealed interface Action : AboutScreenUiEvent {

        object OpenSourceCode : Action
        object BackClick : Action

    }

    sealed interface Change : AboutScreenUiEvent {

        object OpenDataPrivacy : Change
        object CloseDataPrivacy : Change
        object OpenChangelog : Change
        object CloseChangelog : Change
        data class OpenLibrary(val library: StableLibrary) : Change
        object CloseLibrary : Change

    }

    sealed interface Consumed : AboutScreenUiEvent {

        object ShowSnackBar : Consumed

    }

}