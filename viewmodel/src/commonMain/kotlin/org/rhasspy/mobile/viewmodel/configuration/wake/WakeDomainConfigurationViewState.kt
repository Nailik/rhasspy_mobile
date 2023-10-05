package org.rhasspy.mobile.viewmodel.configuration.wake

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.data.toStringOrEmpty
import org.rhasspy.mobile.data.domain.DomainState
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.data.service.option.WakeDomainOption
import org.rhasspy.mobile.platformspecific.toImmutableList

@Stable
data class WakeDomainConfigurationViewState internal constructor(
    val editData: WakeDomainConfigurationData,
    val domainStateFlow: StateFlow<DomainState>,
    val porcupineWakeWordScreen: Int,
) {

    @Stable
    data class WakeDomainConfigurationData internal constructor(
        val wakeDomainOption: WakeDomainOption,
        val wakeWordPorcupineConfigurationData: WakeWordPorcupineConfigurationData,
        val wakeWordUdpConfigurationData: WakeWordUdpConfigurationData,
    ) {

        val wakeDomainOptions: ImmutableList<WakeDomainOption> = WakeDomainOption.entries.toTypedArray().toImmutableList()

        @Stable
        data class WakeWordPorcupineConfigurationData internal constructor(
            val accessToken: String,
            val porcupineLanguage: PorcupineLanguageOption,
            val defaultOptions: ImmutableList<PorcupineDefaultKeyword>,
            val customOptions: ImmutableList<PorcupineCustomKeyword>,
        ) {

            val languageOptions: ImmutableList<PorcupineLanguageOption> = PorcupineLanguageOption.entries.toTypedArray().toImmutableList()

            val defaultOptionsUi: ImmutableList<PorcupineDefaultKeyword>
                get() = defaultOptions.filter { it.option.language == porcupineLanguage }.toImmutableList()

            val keywordCount: Int get() = defaultOptionsUi.count { it.isEnabled } + customOptions.count { it.isEnabled }

        }

        @Stable
        data class WakeWordUdpConfigurationData internal constructor(
            val outputHost: String,
            val outputPort: Int?,
        ) {

            val outputPortText: String = outputPort.toStringOrEmpty()

        }

    }

}