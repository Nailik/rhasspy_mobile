package org.rhasspy.mobile.services.audioplaying

import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.option.AudioPlayingOption

data class AudioPlayingServiceParams(
    val audioPlayingOption: AudioPlayingOption = ConfigurationSetting.audioPlayingOption.value
)