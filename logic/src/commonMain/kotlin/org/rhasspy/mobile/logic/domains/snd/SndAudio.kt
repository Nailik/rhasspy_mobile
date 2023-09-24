package org.rhasspy.mobile.logic.domains.snd

import kotlinx.datetime.Instant
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.logic.pipeline.PipelineEvent

sealed interface SndAudio {

    data object AudioStartEvent : SndAudio

    class AudioChunkEvent(
        val sampleRate: Int,
        val bitRate: Int,
        val channel: Int,
        val data: ByteArray,
    ) : SndAudio

    data object AudioStopEvent : SndAudio

}