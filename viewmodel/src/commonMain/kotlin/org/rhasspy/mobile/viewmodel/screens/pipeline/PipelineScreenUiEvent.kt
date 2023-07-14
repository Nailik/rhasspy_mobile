package org.rhasspy.mobile.viewmodel.screens.pipeline

sealed interface PipelineScreenUiEvent {

    sealed interface Action : PipelineScreenUiEvent {

        data object BackClick : Action

        data object StartSession : Action

        data object StopRecording : Action

    }

}