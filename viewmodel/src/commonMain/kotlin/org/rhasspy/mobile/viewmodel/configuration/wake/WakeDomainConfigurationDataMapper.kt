package org.rhasspy.mobile.viewmodel.configuration.wake

import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.data.toIntOrZero
import org.rhasspy.mobile.data.domain.WakeDomainData
import org.rhasspy.mobile.viewmodel.configuration.wake.WakeDomainConfigurationViewState.WakeDomainConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.wake.WakeDomainConfigurationViewState.WakeDomainConfigurationData.WakeWordPorcupineConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.wake.WakeDomainConfigurationViewState.WakeDomainConfigurationData.WakeWordUdpConfigurationData

class WakeDomainConfigurationDataMapper {

    operator fun invoke(data: WakeDomainData): WakeDomainConfigurationData {
        return WakeDomainConfigurationData(
            wakeDomainOption = data.wakeDomainOption,
            wakeWordPorcupineConfigurationData = WakeWordPorcupineConfigurationData(
                accessToken = data.wakeWordPorcupineAccessToken,
                porcupineLanguage = data.wakeWordPorcupineLanguage,
                defaultOptions = data.wakeWordPorcupineKeywordDefaultOptions.toImmutableList(),
                customOptions = data.wakeWordPorcupineKeywordCustomOptions.toImmutableList(),
            ),
            wakeWordUdpConfigurationData = WakeWordUdpConfigurationData(
                outputHost = data.wakeWordUdpOutputHost,
                outputPort = data.wakeWordUdpOutputPort,
            )
        )
    }

    operator fun invoke(data: WakeDomainConfigurationData): WakeDomainData {
        return WakeDomainData(
            wakeDomainOption = data.wakeDomainOption,
            wakeWordPorcupineAccessToken = data.wakeWordPorcupineConfigurationData.accessToken,
            wakeWordPorcupineLanguage = data.wakeWordPorcupineConfigurationData.porcupineLanguage,
            wakeWordPorcupineKeywordDefaultOptions = data.wakeWordPorcupineConfigurationData.defaultOptions,
            wakeWordPorcupineKeywordCustomOptions = data.wakeWordPorcupineConfigurationData.customOptions,
            wakeWordUdpOutputHost = data.wakeWordUdpConfigurationData.outputHost,
            wakeWordUdpOutputPort = data.wakeWordUdpConfigurationData.outputPort.toIntOrZero(),
        )
    }

}