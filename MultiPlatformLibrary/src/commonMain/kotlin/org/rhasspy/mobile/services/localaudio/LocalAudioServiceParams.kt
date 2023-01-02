package org.rhasspy.mobile.services.localaudio

import org.rhasspy.mobile.settings.option.AudioOutputOption
import org.rhasspy.mobile.settings.ConfigurationSetting

data class LocalAudioServiceParams(
    val audioOutputOption: AudioOutputOption = ConfigurationSetting.audioOutputOption.value
)