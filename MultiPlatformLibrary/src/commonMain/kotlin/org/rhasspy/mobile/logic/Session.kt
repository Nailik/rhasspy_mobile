package org.rhasspy.mobile.logic

/**
 * data of session
 * uniqueId
 * keyword that started the session
 * if a intent was recognized
 * if the audio should be send
 */
data class Session(val sessionId: String,
                   val keyword: String,
                   var isIntentRecognized: Boolean = false,
                   var isSendAudioCaptured: Boolean = false,
                   var currentRecording: MutableList<Byte> = mutableListOf(),
                   var mqttSpeechToTextSessionId: String? = null
)