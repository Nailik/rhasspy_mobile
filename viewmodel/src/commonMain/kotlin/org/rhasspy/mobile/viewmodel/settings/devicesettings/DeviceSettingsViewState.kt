package org.rhasspy.mobile.viewmodel.settings.devicesettings

import androidx.compose.runtime.Stable

@Stable
data class DeviceSettingsViewState(
    val isMqttApiDeviceChangeEnabled: Boolean,
    val isHttpApiDeviceChangeEnabled: Boolean,
    val volume: Float,
    val isHotWordEnabled: Boolean,
    val isAudioOutputEnabled: Boolean,
    val isIntentHandlingEnabled: Boolean
)