package org.rhasspy.mobile.logic.domains.texttospeech

import org.rhasspy.mobile.data.service.option.TextToSpeechOption

internal data class TextToSpeechServiceParams(
    val siteId: String,
    val textToSpeechOption: TextToSpeechOption,
    val httpConnectionId: Long?
)