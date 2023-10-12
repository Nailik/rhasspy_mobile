package org.rhasspy.mobile.viewmodel.configuration.pipeline

import org.rhasspy.mobile.data.pipeline.PipelineData
import org.rhasspy.mobile.data.sounds.IndicationSoundOption
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationViewState.PipelineConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationViewState.PipelineConfigurationData.PipelineLocalConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationViewState.PipelineConfigurationData.PipelineLocalConfigurationData.IndicationSoundOptionType

class PipelineConfigurationDataMapper {

    operator fun invoke(data: PipelineData): PipelineConfigurationData {
        return PipelineConfigurationData(
            pipelineManagerOption = data.option,
            pipelineLocalConfigurationData = PipelineLocalConfigurationData(
                isSoundIndicationEnabled = data.localPipelineData.isSoundIndicationEnabled,
                soundIndicationOutputOption = data.localPipelineData.soundIndicationOutputOption,
                wakeSound = data.localPipelineData.wakeSound.option.toType(),
                errorSound = data.localPipelineData.errorSound.option.toType(),
                recordedSound = data.localPipelineData.recordedSound.option.toType(),
            ),
        )
    }

    operator fun invoke(data: PipelineConfigurationData): PipelineData {
        return with(ConfigurationSetting.pipelineData.value) {
            copy(
                option = data.pipelineManagerOption,
                localPipelineData = with(localPipelineData) {
                    copy(
                        isSoundIndicationEnabled = data.pipelineLocalConfigurationData.isSoundIndicationEnabled,
                        soundIndicationOutputOption = data.pipelineLocalConfigurationData.soundIndicationOutputOption,
                    )
                })
        }
    }
}

private fun IndicationSoundOption.toType(): IndicationSoundOptionType {
    return when (this) {
        is IndicationSoundOption.Custom -> IndicationSoundOptionType.Custom
        IndicationSoundOption.Default   -> IndicationSoundOptionType.Default
        IndicationSoundOption.Disabled  -> IndicationSoundOptionType.Disabled
    }
}