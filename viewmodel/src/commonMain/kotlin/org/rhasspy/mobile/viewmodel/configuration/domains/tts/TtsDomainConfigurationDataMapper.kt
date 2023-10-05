package org.rhasspy.mobile.viewmodel.configuration.domains.tts

import org.rhasspy.mobile.data.data.toIntOrZero
import org.rhasspy.mobile.data.domain.TtsDomainData
import org.rhasspy.mobile.viewmodel.configuration.domains.tts.TtsDomainConfigurationViewState.TtsDomainConfigurationData
import kotlin.time.Duration.Companion.seconds

class TtsDomainConfigurationDataMapper {

    operator fun invoke(data: TtsDomainData): TtsDomainConfigurationData {
        return TtsDomainConfigurationData(
            ttsDomainOption = data.option,
            rhasspy2HermesMqttTimeout = data.rhasspy2HermesMqttTimeout.inWholeSeconds.toString(),
        )
    }

    operator fun invoke(data: TtsDomainConfigurationData): TtsDomainData {
        return TtsDomainData(
            option = data.ttsDomainOption,
            rhasspy2HermesMqttTimeout = data.rhasspy2HermesMqttTimeout.toIntOrZero().seconds,
        )
    }

}