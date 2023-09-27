package org.rhasspy.mobile.logic.domains.vad

sealed interface VadEvent {

    data object VoiceStart : VadEvent

    sealed interface VoiceEnd : VadEvent {
        data object VoiceStopped : VoiceEnd
        data object VoiceTimeout : VoiceEnd
    }

}