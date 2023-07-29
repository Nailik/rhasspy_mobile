package org.rhasspy.mobile.logic.services.texttospeech

import org.rhasspy.mobile.data.service.option.TextToSpeechOption

internal data class TextToSpeechServiceParams(
    val siteId: String,
    val textToSpeechOption: TextToSpeechOption
)