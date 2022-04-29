package org.rhasspy.mobile.services.logic

/**
 * data of session
 * uniqueId
 * keyword that started the session
 * if a intent was recognized
 * if the audio should be send
 */
data class Session(val sessionId: String, val keyword: String, var isIntentRecognized: Boolean = false, var isSendAudioCaptured: Boolean = false)