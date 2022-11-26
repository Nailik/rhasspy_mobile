package org.rhasspy.mobile.services.localaudio

import org.rhasspy.mobile.data.AudioOutputOptions
import org.rhasspy.mobile.settings.ConfigurationSettings

data class LocalAudioServiceParams(
    val audioOutputOption: AudioOutputOptions = ConfigurationSettings.audioOutputOption.value
)