package org.rhasspy.mobile.logic.services.dialog

import androidx.compose.runtime.Stable

@Stable
data class SessionData(
    val sessionId: String,
    val sendAudioCaptured: Boolean,
    val wakeWord: String?,
    val recognizedText: String?
)