package org.rhasspy.mobile.viewmodel.configuration.audioinput.audioinputformat

import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType

sealed interface AudioInputFormatConfigurationUiEvent {


    sealed interface Change : AudioInputFormatConfigurationUiEvent {

        data class SelectInputFormatChannelType(val value: AudioFormatChannelType) : Change
        data class SelectInputFormatEncodingType(val value: AudioFormatEncodingType) : Change
        data class SelectInputFormatSampleRateType(val value: AudioFormatSampleRateType) : Change

    }

    sealed interface Click : AudioInputFormatConfigurationUiEvent {

        data object BackClick : Click

    }


}