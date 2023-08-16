package org.rhasspy.mobile.logic.services.wakeword

import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.data.service.option.WakeWordOption

internal data class WakeWordServiceParams(
    val isMicrophonePermissionEnabled: Boolean,
    val isEnabled: Boolean,
    val isAutoPauseOnMediaPlayback: Boolean,
    val audioRecorderSampleRateType: AudioFormatSampleRateType,
    val audioRecorderChannelType: AudioFormatChannelType,
    val audioRecorderEncodingType: AudioFormatEncodingType,
    val audioOutputSampleRateType: AudioFormatSampleRateType,
    val audioOutputChannelType: AudioFormatChannelType,
    val audioOutputEncodingType: AudioFormatEncodingType,
    val wakeWordOption: WakeWordOption,
    val wakeWordPorcupineAccessToken: String,
    val wakeWordPorcupineKeywordDefaultOptions: ImmutableList<PorcupineDefaultKeyword>,
    val wakeWordPorcupineKeywordCustomOptions: ImmutableList<PorcupineCustomKeyword>,
    val wakeWordPorcupineLanguage: PorcupineLanguageOption,
    val wakeWordUdpOutputHost: String,
    val wakeWordUdpOutputPort: Int
)