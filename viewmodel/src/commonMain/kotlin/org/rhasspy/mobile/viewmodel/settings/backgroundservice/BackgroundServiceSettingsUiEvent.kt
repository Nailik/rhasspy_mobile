package org.rhasspy.mobile.viewmodel.settings.backgroundservice

sealed interface BackgroundServiceSettingsUiEvent {

    sealed interface Change : BackgroundServiceSettingsUiEvent {

        data class SetBackgroundServiceSettingsEnabled(val enabled: Boolean) : Change

    }

    sealed interface Action : BackgroundServiceSettingsUiEvent {

        object DisableBatteryOptimization : Action
        object BackClick : Action

    }

    sealed interface Consumed : BackgroundServiceSettingsUiEvent {

        object ShowSnackBar : Consumed

    }

}