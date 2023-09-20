package org.rhasspy.mobile.logic.pipeline

import kotlinx.coroutines.Job

//transition map defines from with state to which state the transition is happening for a specific action


//source and dialog management option also plays a role
//best is probably to build 3 different dialog management options that have a common api
//(just on action)

sealed interface PipelineState {

    sealed interface SessionState : PipelineState {

        val sessionData: SessionData
        val timeoutJob: Job

        data class AsrState(
            override val sessionData: SessionData,
            override val timeoutJob: Job,
        ) : SessionState

        data class IntentState(
            override val sessionData: SessionData,
            override val timeoutJob: Job,
        ) : SessionState

        data class HandleState(
            override val sessionData: SessionData,
            override val timeoutJob: Job
        ) : SessionState

    }

    data object IdleState : PipelineState

    data object SndState : PipelineState
    data object TtsState : PipelineState

}