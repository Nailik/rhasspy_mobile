package org.rhasspy.mobile.viewmodel.configuration.pipeline.indicationsound

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.service.option.AudioOutputOption

@Stable
data class AudioPlayerViewState internal constructor(
    val isAudioPlaying: Boolean,
    val audioOutputOption: AudioOutputOption,
    val isNoSoundInformationBoxVisible: Boolean,
)