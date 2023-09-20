package org.rhasspy.mobile.platformspecific.porcupine

import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption

/**
 * Listens to WakeWord with Porcupine
 *
 * start listening to wake words
 * requires internet to activate porcupine the very first time
 */
expect class PorcupineWakeWordClient(
    wakeWordPorcupineAccessToken: String,
    wakeWordPorcupineKeywordDefaultOptions: List<PorcupineDefaultKeyword>,
    wakeWordPorcupineKeywordCustomOptions: List<PorcupineCustomKeyword>,
    wakeWordPorcupineLanguage: PorcupineLanguageOption,
    onKeywordDetected: (hotWord: String) -> Unit,
) {

    /**
     * start wake word detected
     */
    fun initialize(): Exception?

    fun audioFrame(
        sampleRate: AudioFormatSampleRateType,
        encoding: AudioFormatEncodingType,
        channel: AudioFormatChannelType,
        data: ByteArray,
    )

    fun close()

}