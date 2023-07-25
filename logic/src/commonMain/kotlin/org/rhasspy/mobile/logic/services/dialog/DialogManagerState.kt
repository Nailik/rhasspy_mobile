package org.rhasspy.mobile.logic.services.dialog

import kotlinx.coroutines.Job

//transition map defines from with state to which state the transition is happening for a specific action


//source and dialog management option also plays a role
//best is probably to build 3 different dialog management options that have a common api
//(just onaction)

sealed interface DialogManagerState {

    val sessionData: SessionData?

    data class IdleState(override val sessionData: SessionData? = null) : DialogManagerState
    data class RecordingIntentState(
        override val sessionData: SessionData,
        val timeoutJob: Job,
    ) : DialogManagerState

    data class TranscribingIntentState(
        override val sessionData: SessionData,
        val timeoutJob: Job,
    ) : DialogManagerState

    data class RecognizingIntentState(
        override val sessionData: SessionData,
        val timeoutJob: Job,
    ) : DialogManagerState

    data class PlayingAudioState(override val sessionData: SessionData? = null) : DialogManagerState

}