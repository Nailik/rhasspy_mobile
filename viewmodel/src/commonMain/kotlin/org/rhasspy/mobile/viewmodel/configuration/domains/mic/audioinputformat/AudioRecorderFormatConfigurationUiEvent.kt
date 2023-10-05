package org.rhasspy.mobile.viewmodel.configuration.domains.mic.audioinputformat

import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType

sealed interface AudioRecorderFormatConfigurationUiEvent {


    sealed interface Change : AudioRecorderFormatConfigurationUiEvent {

        data class SelectRecorderFormatChannelType(val value: AudioFormatChannelType) : Change
        data class SelectRecorderFormatEncodingType(val value: AudioFormatEncodingType) : Change
        data class SelectRecorderFormatSampleRateType(val value: AudioFormatSampleRateType) : Change

    }


}