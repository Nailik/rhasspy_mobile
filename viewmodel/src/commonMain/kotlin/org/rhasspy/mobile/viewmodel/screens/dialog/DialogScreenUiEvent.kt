package org.rhasspy.mobile.viewmodel.screens.dialog

sealed interface DialogScreenUiEvent {

    sealed interface Change : DialogScreenUiEvent {

        data object ToggleListAutoScroll : Change
        data object ManualListScroll : Change
        data object ClearHistory : Change

    }

}