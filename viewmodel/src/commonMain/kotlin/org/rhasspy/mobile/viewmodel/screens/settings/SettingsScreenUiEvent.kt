package org.rhasspy.mobile.viewmodel.screens.settings

import org.rhasspy.mobile.viewmodel.navigation.destinations.SettingsScreenDestination

sealed interface SettingsScreenUiEvent {

    sealed interface Action : SettingsScreenUiEvent {

        object BackClick : Action
        data class Navigate(val destination: SettingsScreenDestination) : Action

    }

}