package org.rhasspy.mobile.platformspecific.porcupine

import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption

actual class PorcupineWakeWordClient actual constructor(
    private val wakeWordPorcupineAccessToken: String,
    private val wakeWordPorcupineKeywordDefaultOptions: List<PorcupineDefaultKeyword>,
    private val wakeWordPorcupineKeywordCustomOptions: List<PorcupineCustomKeyword>,
    private val wakeWordPorcupineLanguage: PorcupineLanguageOption,
) {

    /**
     * start wake word detected
     */
    actual fun initialize(): Exception? {
        //TODO #516
        return null
    }

    actual fun audioFrame(
        sampleRate: AudioFormatSampleRateType,
        encoding: AudioFormatEncodingType,
        channel: AudioFormatChannelType,
        data: ByteArray,
    ): String? {
        //TODO #516
        return null
    }

    actual fun close() {
        //TODO #516
    }


}