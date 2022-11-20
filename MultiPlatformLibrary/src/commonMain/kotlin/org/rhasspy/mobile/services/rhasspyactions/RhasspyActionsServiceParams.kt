package org.rhasspy.mobile.services.rhasspyactions

import org.rhasspy.mobile.data.*

data class RhasspyActionsServiceParams(
    val intentRecognitionOption: IntentRecognitionOptions,
    val textToSpeechOption: TextToSpeechOptions,
    val audioPlayingOption: AudioPlayingOptions,
    val speechToTextOption: SpeechToTextOptions,
    val intentHandlingOption: IntentHandlingOptions
)