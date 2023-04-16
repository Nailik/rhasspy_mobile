package org.rhasspy.mobile.viewmodel.settings.devicesettings

import org.rhasspy.mobile.logic.settings.AppSetting

data class DeviceSettingsViewState (
    val volume: Float = AppSetting.volume.value,
    val isHotWordEnabled: Boolean = AppSetting.isHotWordEnabled.value,
    val isAudioOutputEnabled: Boolean = AppSetting.isAudioOutputEnabled.value,
    val isIntentHandlingEnabled: Boolean = AppSetting.isIntentHandlingEnabled.value
)