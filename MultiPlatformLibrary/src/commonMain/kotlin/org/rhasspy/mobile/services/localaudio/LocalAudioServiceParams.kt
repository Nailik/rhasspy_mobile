package org.rhasspy.mobile.services.localaudio

import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.option.AudioOutputOption

data class LocalAudioServiceParams(
    val audioOutputOption: AudioOutputOption = ConfigurationSetting.audioOutputOption.value
)