package org.rhasspy.mobile.logic.services.localaudio

import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.logic.settings.ConfigurationSetting

data class LocalAudioServiceParams(
    val audioOutputOption: AudioOutputOption = ConfigurationSetting.audioOutputOption.value
)