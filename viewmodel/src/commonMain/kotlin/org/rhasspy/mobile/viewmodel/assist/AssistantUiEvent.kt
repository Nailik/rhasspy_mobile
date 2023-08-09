package org.rhasspy.mobile.viewmodel.assist

sealed interface AssistantUiEvent {

    data object Activate: AssistantUiEvent

}