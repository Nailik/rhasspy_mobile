package org.rhasspy.mobile.viewmodel.configuration.wakeword

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.data.toStringOrEmpty
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class WakeWordConfigurationViewState internal constructor(
    override val editData: WakeWordConfigurationData,
    val porcupineWakeWordScreen: Int,
    val isMicrophonePermissionRequestVisible: Boolean,
) : IConfigurationViewState {

    @Stable
    data class PorcupineCustomKeywordViewState internal constructor(
        val keyword: PorcupineCustomKeyword,
        val deleted: Boolean = false,
    )

    @Stable
    data class WakeWordConfigurationData internal constructor(
        val wakeWordOption: WakeWordOption,
        val wakeWordPorcupineConfigurationData: WakeWordPorcupineConfigurationData,
        val wakeWordUdpConfigurationData: WakeWordUdpConfigurationData,
    ) : IConfigurationData {

        val wakeWordOptions: ImmutableList<WakeWordOption> = WakeWordOption.entries.toTypedArray().toImmutableList()

        @Stable
        data class WakeWordPorcupineConfigurationData internal constructor(
            val accessToken: String,
            val porcupineLanguage: PorcupineLanguageOption,
            val defaultOptions: ImmutableList<PorcupineDefaultKeyword>,
            val customOptions: ImmutableList<PorcupineCustomKeyword>,
            val deletedCustomOptions: ImmutableList<PorcupineCustomKeyword>,
        ) {

            val languageOptions: ImmutableList<PorcupineLanguageOption> = PorcupineLanguageOption.entries.toTypedArray().toImmutableList()

            val customOptionsUi: ImmutableList<PorcupineCustomKeywordViewState> =
                customOptions.map {
                    PorcupineCustomKeywordViewState(
                        keyword = it,
                        deleted = deletedCustomOptions.contains(it)
                    )
                }.toImmutableList()

            val defaultOptionsUi: ImmutableList<PorcupineDefaultKeyword>
                get() = defaultOptions.filter { it.option.language == porcupineLanguage }.toImmutableList()

            val keywordCount: Int get() = defaultOptionsUi.count { it.isEnabled } + customOptionsUi.count { it.keyword.isEnabled }

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