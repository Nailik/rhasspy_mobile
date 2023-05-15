package org.rhasspy.mobile.viewmodel.screens.main

sealed interface MainScreenUiEvent {

    sealed interface Action : MainScreenUiEvent {

        object BottomBarHomeClick : Action

        object BottomBarConfigurationClick : Action

        object BottomBarSettingsClick : Action

        object BottomBarLogClick : Action

        object BackClick: Action

    }

}