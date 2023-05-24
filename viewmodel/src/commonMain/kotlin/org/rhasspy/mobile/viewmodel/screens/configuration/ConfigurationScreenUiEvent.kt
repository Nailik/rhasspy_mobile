package org.rhasspy.mobile.viewmodel.screens.configuration

import org.rhasspy.mobile.viewmodel.navigation.destinations.ConfigurationScreenNavigationDestination

sealed interface ConfigurationScreenUiEvent {

    sealed interface Change : ConfigurationScreenUiEvent {

        class SiteIdChange(val text: String) : Change

    }

    sealed interface Action : ConfigurationScreenUiEvent {

        object ScrollToErrorClick : Action
        object BackClick : Action
        data class Navigate(val destination: ConfigurationScreenNavigationDestination) : Action

    }

    sealed interface Consumed : ConfigurationScreenUiEvent {

        object ScrollToError : Consumed

    }

}