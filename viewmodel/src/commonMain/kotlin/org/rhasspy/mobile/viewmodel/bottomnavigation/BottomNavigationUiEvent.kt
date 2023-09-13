package org.rhasspy.mobile.viewmodel.bottomnavigation

import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.MainScreenNavigationDestination

sealed interface BottomNavigationUiEvent {

    sealed interface Action : BottomNavigationUiEvent {

        data class Navigate(val destination: MainScreenNavigationDestination) : Action

    }
}