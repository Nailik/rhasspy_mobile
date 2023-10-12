package org.rhasspy.mobile.viewmodel.configuration.pipeline

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.PipelineManagerOption
import org.rhasspy.mobile.resources.MR

@Stable
data class PipelineConfigurationViewState internal constructor(
    val editData: PipelineConfigurationData
) {

    @Stable
    data class PipelineConfigurationData internal constructor(
        val pipelineManagerOption: PipelineManagerOption,
        val pipelineLocalConfigurationData: PipelineLocalConfigurationData
    ) {

        @Stable
        data class PipelineLocalConfigurationData internal constructor(
            val isSoundIndicationEnabled: Boolean,
            val soundIndicationOutputOption: AudioOutputOption,
            val wakeSound: IndicationSoundOptionType,
            val errorSound: IndicationSoundOptionType,
            val recordedSound: IndicationSoundOptionType,
        ) {

            val audioOutputOptionList: ImmutableList<AudioOutputOption> = AudioOutputOption.entries.toImmutableList()

            enum class IndicationSoundOptionType(val title: StableStringResource) {
                Custom(MR.strings.textCustom.stable),
                Disabled(MR.strings.disabled.stable),
                Default(MR.strings.defaultText.stable),
            }

        }

        val pipelineManagerOptionList = PipelineManagerOption.entries.toImmutableList()

    }

}


@Stable
sealed interface IndicationSoundOption {

    data class Custom(val file: String) : IndicationSoundOption

    data object Disabled : IndicationSoundOption

    data object Default : IndicationSoundOption

}