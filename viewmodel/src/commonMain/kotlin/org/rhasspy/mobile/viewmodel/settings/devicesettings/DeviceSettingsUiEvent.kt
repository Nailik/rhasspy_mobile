package org.rhasspy.mobile.viewmodel.settings.devicesettings

sealed interface DeviceSettingsUiEvent {

    sealed interface Change : DeviceSettingsUiEvent {

        data class UpdateVolume(val volume: Float) : Change
        data class SetHotWordEnabled(val enabled: Boolean) : Change
        data class SetAudioOutputEnabled(val enabled: Boolean) : Change
        data class SetIntentHandlingEnabled(val enabled: Boolean) : Change

    }

}