package org.rhasspy.mobile.logic.services.wakeword

import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderSampleRateType
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.data.service.option.WakeWordOption

internal data class WakeWordServiceParams(
    val isEnabled: Boolean,
    val audioRecorderSampleRateType: AudioRecorderSampleRateType,
    val audioRecorderChannelType: AudioRecorderChannelType,
    val audioRecorderEncodingType: AudioRecorderEncodingType,
    val isUseCustomRecorder: Boolean,
    val wakeWordOption: WakeWordOption,
    val wakeWordPorcupineAccessToken: String,
    val wakeWordPorcupineKeywordDefaultOptions: ImmutableList<PorcupineDefaultKeyword>,
    val wakeWordPorcupineKeywordCustomOptions: ImmutableList<PorcupineCustomKeyword>,
    val wakeWordPorcupineLanguage: PorcupineLanguageOption,
    val wakeWordUdpOutputHost: String,
    val wakeWordUdpOutputPort: Int
)