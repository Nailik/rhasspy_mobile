package org.rhasspy.mobile.logic.pipeline

import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource

sealed interface PipelineEvent {

    data class StartEvent(
        val sessionId: String?,
        val wakeWord: String?
    ) : PipelineEvent

    data class PlayAudioEvent(
        val data: AudioSource,
    ) : PipelineEvent

}