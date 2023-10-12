package org.rhasspy.mobile.data.pipeline

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.PipelineManagerOption
import org.rhasspy.mobile.data.sounds.IndicationSoundOption
import org.rhasspy.mobile.data.sounds.IndicationSoundType

@Serializable
data class PipelineData(
    val option: PipelineManagerOption,
    val localPipelineData: LocalPipelineData,
) {

    @Serializable
    data class LocalPipelineData(
        val isSoundIndicationEnabled: Boolean,
        val soundIndicationOutputOption: AudioOutputOption,
        val wakeSound: IndicationSoundOptionData,
        val errorSound: IndicationSoundOptionData,
        val recordedSound: IndicationSoundOptionData
    ) {

        @Serializable
        data class IndicationSoundOptionData(
            val type: IndicationSoundType,
            val volume: Float,
            val option: IndicationSoundOption,
        )

    }

}