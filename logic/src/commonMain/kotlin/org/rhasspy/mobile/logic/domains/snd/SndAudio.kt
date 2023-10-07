package org.rhasspy.mobile.logic.domains.snd

sealed interface SndAudio {

    data class AudioStartEvent(
        val sampleRate: Int,
        val bitRate: Int,
        val channel: Int,
    ) : SndAudio

    class AudioChunkEvent(
        val data: ByteArray,
    ) : SndAudio

    data object AudioStopEvent : SndAudio

}