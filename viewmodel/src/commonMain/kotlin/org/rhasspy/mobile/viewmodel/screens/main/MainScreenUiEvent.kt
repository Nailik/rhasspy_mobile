package org.rhasspy.mobile.viewmodel.screens.main

import org.rhasspy.mobile.viewmodel.navigation.destinations.MainScreenNavigationDestination

sealed interface MainScreenUiEvent {

    sealed interface Action : MainScreenUiEvent {

        object BackClick : Action
        data class Navigate(val destination: MainScreenNavigationDestination) : Action
        data class CrashlyticsDialogResult(val result: Boolean) : Action

    }

}