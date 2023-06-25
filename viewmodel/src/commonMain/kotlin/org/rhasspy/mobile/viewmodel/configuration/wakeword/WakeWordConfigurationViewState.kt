package org.rhasspy.mobile.viewmodel.configuration.wakeword

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.WakeWordConfigurationScreenDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.porcupine.PorcupineKeywordConfigurationScreenDestination

@Stable
data class WakeWordConfigurationViewState internal constructor(
    val editData: WakeWordConfigurationData,
    val screen: WakeWordConfigurationScreenDestination,
    val porcupineWakeWordScreen: PorcupineKeywordConfigurationScreenDestination,
    val isMicrophonePermissionRequestVisible: Boolean
) {

    @Stable
    data class PorcupineCustomKeywordViewState(
        val keyword: PorcupineCustomKeyword,
        val deleted: Boolean = false
    )

    @Stable
    data class WakeWordConfigurationData internal constructor(
        val wakeWordOption: WakeWordOption = ConfigurationSetting.wakeWordOption.value,
        val wakeWordPorcupineConfigurationData: WakeWordPorcupineConfigurationData = WakeWordPorcupineConfigurationData(),
        val wakeWordUdpConfigurationData: WakeWordUdpConfigurationData = WakeWordUdpConfigurationData(),
    ) {
        val wakeWordOptions: ImmutableList<WakeWordOption> = WakeWordOption.values().toImmutableList()

        @Stable
        data class WakeWordPorcupineConfigurationData internal constructor(
            val accessToken: String = ConfigurationSetting.wakeWordPorcupineAccessToken.value,
            val porcupineLanguage: PorcupineLanguageOption = ConfigurationSetting.wakeWordPorcupineLanguage.value,
            val defaultOptions: ImmutableList<PorcupineDefaultKeyword> = ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value,
            val customOptions: ImmutableList<PorcupineCustomKeyword> = ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value,
            val deletedCustomOptions: ImmutableList<PorcupineCustomKeyword> = persistentListOf(),
        ) {

            val languageOptions: ImmutableList<PorcupineLanguageOption> = PorcupineLanguageOption.values().toImmutableList()

            val customOptionsUi: ImmutableList<PorcupineCustomKeywordViewState> =
                customOptions.map {
                    PorcupineCustomKeywordViewState(
                        keyword = it,
                        deleted = deletedCustomOptions.contains(it)
                    )
                }.toImmutableList()

            val defaultOptionsUi: ImmutableList<PorcupineDefaultKeyword>
                get() =
                    defaultOptions.filter { it.option.language == porcupineLanguage }.toImmutableList()

            val keywordCount: Int get() = defaultOptionsUi.count { it.isEnabled } + customOptionsUi.count { it.keyword.isEnabled }

        }

        @Stable
        data class WakeWordUdpConfigurationData internal constructor(
            val outputHost: String = ConfigurationSetting.wakeWordUdpOutputHost.value,
            val outputPort: Int? = ConfigurationSetting.wakeWordUdpOutputPort.value
        ) {

            val outputPortText: String = outputPort.toString()

        }

    }

    //val isMicrophonePermissionRequestVisible: Boolean =
    // override val isTestingEnabled: Boolean get() = wakeWordOption != WakeWordOption.Disabled

}