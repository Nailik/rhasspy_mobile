package org.rhasspy.mobile.viewmodel.configuration.wakeword

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.domain.WakeDomainData
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData.WakeWordPorcupineConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData.WakeWordUdpConfigurationData

class WakeWordConfigurationDataMapper {

    operator fun invoke(data: WakeDomainData): WakeWordConfigurationData {
        return WakeWordConfigurationData(
            wakeWordOption = data.wakeWordOption,
            wakeWordPorcupineConfigurationData = WakeWordPorcupineConfigurationData(
                accessToken = data.wakeWordPorcupineAccessToken,
                porcupineLanguage = data.wakeWordPorcupineLanguage,
                defaultOptions = data.wakeWordPorcupineKeywordDefaultOptions.toImmutableList(),
                customOptions = data.wakeWordPorcupineKeywordCustomOptions.toImmutableList(),
                deletedCustomOptions = persistentListOf()
            ),
            wakeWordUdpConfigurationData = WakeWordUdpConfigurationData(
                outputHost = data.wakeWordUdpOutputHost,
                outputPort = data.wakeWordUdpOutputPort,
            )
        )
    }

    operator fun invoke(data: WakeWordConfigurationData): WakeDomainData {
        return WakeDomainData(
            wakeWordOption = data.wakeWordOption,
            wakeWordPorcupineAccessToken = data.wakeWordPorcupineConfigurationData.accessToken,
            wakeWordPorcupineLanguage = data.wakeWordPorcupineConfigurationData.porcupineLanguage,
            wakeWordPorcupineKeywordDefaultOptions = data.wakeWordPorcupineConfigurationData.defaultOptions,
            wakeWordPorcupineKeywordCustomOptions = data.wakeWordPorcupineConfigurationData.customOptions,
            wakeWordUdpOutputHost = data.wakeWordUdpConfigurationData.outputHost,
            wakeWordUdpOutputPort = data.wakeWordUdpConfigurationData.outputPort,
        )
    }

}