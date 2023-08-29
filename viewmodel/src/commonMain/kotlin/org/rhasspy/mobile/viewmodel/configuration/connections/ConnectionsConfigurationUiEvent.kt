package org.rhasspy.mobile.viewmodel.configuration.connections

import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConnectionScreenNavigationDestination

sealed interface ConnectionsConfigurationUiEvent {

    sealed interface Action : ConnectionsConfigurationUiEvent {

        data object BackClick : Action

        data class Navigate(val destination: ConnectionScreenNavigationDestination) : Action

    }

}