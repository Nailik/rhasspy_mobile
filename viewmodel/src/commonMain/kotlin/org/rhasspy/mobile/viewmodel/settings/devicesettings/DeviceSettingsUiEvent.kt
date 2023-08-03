package org.rhasspy.mobile.viewmodel.settings.devicesettings

sealed interface DeviceSettingsUiEvent {

    sealed interface Action : DeviceSettingsUiEvent {
        data object BackClick : Action
    }

    sealed interface Change : DeviceSettingsUiEvent {

        data class SetMqttApiChangesEnabled(val enabled: Boolean) : Change
        data class SetHttpApiChangesEnabled(val enabled: Boolean) : Change
        data class UpdateVolume(val volume: Float) : Change
        data class SetHotWordEnabled(val enabled: Boolean) : Change
        data class SetAudioOutputEnabled(val enabled: Boolean) : Change
        data class SetIntentHandlingEnabled(val enabled: Boolean) : Change
    }

}