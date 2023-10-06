package org.rhasspy.mobile.logic.pipeline

import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource

internal data class PlayAudioEvent(
    val data: AudioSource,
)