package org.rhasspy.mobile.logic.services.dialog

data class SessionData(
    val sessionId: String,
    val sendAudioCaptured: Boolean,
    val wakeWord: String?,
    val recognizedText: String?
)