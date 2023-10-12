package org.rhasspy.mobile.viewmodel.configuration.pipeline.indicationsound

import org.rhasspy.mobile.data.pipeline.PipelineData
import org.rhasspy.mobile.data.pipeline.PipelineData.LocalPipelineData.IndicationSoundOptionData
import org.rhasspy.mobile.data.sounds.IndicationSoundType
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.pipeline.indicationsound.IndicationSoundConfigurationViewState.IndicationSoundOptionConfigurationData

class IndicationSoundConfigurationDataMapper {

    operator fun invoke(indicationSoundType: IndicationSoundType, data: PipelineData): IndicationSoundOptionConfigurationData {
        val indicationSoundOptionData = when (indicationSoundType) {
            IndicationSoundType.Error    -> data.localPipelineData.errorSound
            IndicationSoundType.Recorded -> data.localPipelineData.recordedSound
            IndicationSoundType.Wake     -> data.localPipelineData.wakeSound
        }
        return IndicationSoundOptionConfigurationData(
            volume = indicationSoundOptionData.volume,
            option = indicationSoundOptionData.option
        )
    }

    operator fun invoke(indicationSoundType: IndicationSoundType, data: IndicationSoundOptionConfigurationData): PipelineData {
        val indicationSoundData = IndicationSoundOptionData(
            volume = data.volume,
            option = data.option,
            type = indicationSoundType,
        )

        return with(ConfigurationSetting.pipelineData.value) {
            copy(localPipelineData = with(localPipelineData) {
                when (indicationSoundType) {
                    IndicationSoundType.Error    -> copy(errorSound = indicationSoundData)
                    IndicationSoundType.Recorded -> copy(recordedSound = indicationSoundData)
                    IndicationSoundType.Wake     -> copy(wakeSound = indicationSoundData)
                }
            })
        }
    }

}