package org.rhasspy.mobile.platformspecific.porcupine

import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderSampleRateType
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
    isUseCustomRecorder: Boolean,
    audioRecorderSampleRateType: AudioRecorderSampleRateType,
    audioRecorderChannelType: AudioRecorderChannelType,
    audioRecorderEncodingType: AudioRecorderEncodingType,
    wakeWordPorcupineAccessToken: String,
    wakeWordPorcupineKeywordDefaultOptions: ImmutableList<PorcupineDefaultKeyword>,
    wakeWordPorcupineKeywordCustomOptions: ImmutableList<PorcupineCustomKeyword>,
    wakeWordPorcupineLanguage: PorcupineLanguageOption,
    onKeywordDetected: (hotWord: String) -> Unit,
    onError: (Exception) -> Unit
) {

    val isInitialized: Boolean

    /**
     * create porcupine client
     */
    fun initialize(): Exception?

    /**
     * start wake word detected
     */
    fun start(): Exception?

    /**
     * stop wake word detected
     */
    fun stop()

    fun close()

}