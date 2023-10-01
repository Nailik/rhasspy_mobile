package org.rhasspy.mobile.viewmodel.configuration.audioinput.audiooutputformat

import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType

sealed interface AudioOutputFormatConfigurationUiEvent {

    sealed interface Change : AudioOutputFormatConfigurationUiEvent {

        data class SelectOutputFormatChannelType(val value: AudioFormatChannelType) : Change
        data class SelectOutputFormatEncodingType(val value: AudioFormatEncodingType) : Change
        data class SelectOutputFormatSampleRateType(val value: AudioFormatSampleRateType) : Change
    }

}