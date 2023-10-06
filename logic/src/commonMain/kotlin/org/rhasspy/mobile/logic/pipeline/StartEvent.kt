package org.rhasspy.mobile.logic.pipeline

internal data class StartEvent(
    val sessionId: String?,
    val wakeWord: String?
)