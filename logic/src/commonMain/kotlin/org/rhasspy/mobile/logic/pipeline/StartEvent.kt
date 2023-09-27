package org.rhasspy.mobile.logic.pipeline

data class StartEvent(
    val sessionId: String?,
    val wakeWord: String?
)