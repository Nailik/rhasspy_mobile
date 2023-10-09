package org.rhasspy.mobile.data.pipeline

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.PipelineManagerOption
import org.rhasspy.mobile.data.sounds.SoundOption

@Serializable
data class PipelineData(
    val option: PipelineManagerOption,
    val localPipelineData: LocalPipelineData,
) {

    @Serializable
    data class LocalPipelineData(
        val isSoundIndicationEnabled: Boolean,
        val soundIndicationOutputOption: AudioOutputOption,
        val wakeSound: IndicationSoundOption,
        val errorSound: IndicationSoundOption,
        val recordedSound: IndicationSoundOption
    ) {

        @Serializable
        data class IndicationSoundOption(
            val volume: Float,
            val option: SoundOption,
        )

    }

}