package org.rhasspy.mobile.viewmodel.screens.main

import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.MainScreenNavigationDestination

sealed interface MainScreenUiEvent {

    sealed interface Action : MainScreenUiEvent {

        data object BackClick : Action
        data class Navigate(val destination: MainScreenNavigationDestination) : Action
        data class CrashlyticsDialogResult(val result: Boolean) : Action
        data object CloseChangelog : Action

    }

}