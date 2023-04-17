package org.rhasspy.mobile.viewmodel.settings.backgroundservice

sealed interface BackgroundServiceUiEvent {


    sealed interface Change : BackgroundServiceUiEvent {

        data class SetBackgroundServiceEnabled(val enabled: Boolean) : Change

    }

    sealed interface Action: BackgroundServiceUiEvent {
        object DisableBatteryOptimization: Action
    }

}