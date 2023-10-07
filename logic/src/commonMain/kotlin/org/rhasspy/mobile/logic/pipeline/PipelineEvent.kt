package org.rhasspy.mobile.logic.pipeline

import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource

sealed interface PipelineEvent {

    data class PlayAudioEvent(
        val data: AudioSource,
    ) : PipelineEvent

}