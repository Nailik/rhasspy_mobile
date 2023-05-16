package org.rhasspy.mobile.viewmodel.screens.main

sealed interface MainScreenUiEvent {

    sealed interface Action : MainScreenUiEvent {

        object BackClick : Action

    }

    sealed interface Navigate : MainScreenUiEvent {

        object BottomBarHomeClick : Navigate
        object BottomBarConfigurationClick : Navigate
        object BottomBarSettingsClick : Navigate
        object BottomBarLogClick : Navigate

    }

}