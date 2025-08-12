package org.rhasspy.mobile.logic.services.dialog

import kotlinx.coroutines.Job

//transition map defines from with state to which state the transition is happening for a specific action

//source and dialog management option also plays a role
//best is probably to build 3 different dialog management options that have a common api
//(just on action)

sealed interface DialogManagerState {

    sealed interface SessionState : DialogManagerState {

        val sessionData: SessionData
        val timeoutJob: Job

        data class RecordingIntentState(
            override val sessionData: SessionData,
            override val timeoutJob: Job,
        ) : SessionState

        data class TranscribingIntentState(
            override val sessionData: SessionData,
            override val timeoutJob: Job,
        ) : SessionState

        data class RecognizingIntentState(
            override val sessionData: SessionData,
            override val timeoutJob: Job,
        ) : SessionState

    }

    data object IdleState : DialogManagerState

    data object PlayingAudioState : DialogManagerState

}