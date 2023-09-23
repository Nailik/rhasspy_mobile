package org.rhasspy.mobile.logic.domains.vad

sealed interface VadEvent {

    data object VoiceStart : VadEvent

    data object VoiceStopped : VadEvent

}