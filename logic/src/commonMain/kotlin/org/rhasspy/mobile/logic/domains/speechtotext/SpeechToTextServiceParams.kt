package org.rhasspy.mobile.logic.domains.speechtotext

import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.data.service.option.SpeechToTextOption

internal data class SpeechToTextServiceParams(
    val isAutoPauseOnMediaPlayback: Boolean,
    val speechToTextOption: SpeechToTextOption,
    val dialogManagementOption: DialogManagementOption,
    val audioRecorderChannelType: AudioFormatChannelType,
    val audioRecorderEncodingType: AudioFormatEncodingType,
    val audioRecorderSampleRateType: AudioFormatSampleRateType,
    val audioOutputChannelType: AudioFormatChannelType,
    val audioOutputEncodingType: AudioFormatEncodingType,
    val audioOutputSampleRateType: AudioFormatSampleRateType,
)