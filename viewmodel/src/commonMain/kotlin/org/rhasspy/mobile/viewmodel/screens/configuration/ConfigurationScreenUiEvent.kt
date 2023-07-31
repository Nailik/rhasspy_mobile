package org.rhasspy.mobile.viewmodel.screens.configuration

import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConfigurationScreenNavigationDestination

sealed interface ConfigurationScreenUiEvent {

    sealed interface Change : ConfigurationScreenUiEvent {

        class SiteIdChange(val text: String) : Change

    }

    sealed interface Action : ConfigurationScreenUiEvent {

        data object ScrollToErrorClick : Action
        data object BackClick : Action
        data class Navigate(val destination: ConfigurationScreenNavigationDestination) : Action

    }

    sealed interface Consumed : ConfigurationScreenUiEvent {

        data object ScrollToError : Consumed

    }

}