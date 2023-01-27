package org.rhasspy.mobile.logic.services.localaudio

import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.logic.settings.option.AudioOutputOption

data class LocalAudioServiceParams(
    val audioOutputOption: AudioOutputOption = ConfigurationSetting.audioOutputOption.value
)