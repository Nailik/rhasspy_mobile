package org.rhasspy.mobile.viewmodel.settings.backgroundservice

import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsUiEvent

sealed interface BackgroundServiceUiEvent {

    sealed interface Navigate : BackgroundServiceUiEvent {
        object BackClick: Navigate
    }


    sealed interface Change : BackgroundServiceUiEvent {

        data class SetBackgroundServiceEnabled(val enabled: Boolean) : Change

    }

    sealed interface Action : BackgroundServiceUiEvent {

        object DisableBatteryOptimization : Action

    }

    sealed interface Consumed : BackgroundServiceUiEvent {

        object ShowSnackBar : Consumed

    }

}