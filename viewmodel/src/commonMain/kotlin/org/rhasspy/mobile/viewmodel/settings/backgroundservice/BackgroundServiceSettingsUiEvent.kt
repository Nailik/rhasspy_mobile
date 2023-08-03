package org.rhasspy.mobile.viewmodel.settings.backgroundservice

sealed interface BackgroundServiceSettingsUiEvent {

    sealed interface Change : BackgroundServiceSettingsUiEvent {

        data class SetBackgroundServiceSettingsEnabled(val enabled: Boolean) : Change

    }

    sealed interface Action : BackgroundServiceSettingsUiEvent {

        data object DisableBatteryOptimization : Action
        data object BackClick : Action

    }

    sealed interface Consumed : BackgroundServiceSettingsUiEvent {

        data object ShowSnackBar : Consumed

    }

}