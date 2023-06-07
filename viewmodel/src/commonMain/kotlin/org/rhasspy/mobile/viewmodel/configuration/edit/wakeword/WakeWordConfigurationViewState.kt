package org.rhasspy.mobile.viewmodel.configuration.edit.wakeword

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.platformspecific.toIntOrZero
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewState

@Stable
data class WakeWordConfigurationViewState internal constructor(
    val wakeWordOption: WakeWordOption = ConfigurationSetting.wakeWordOption.value,
    val wakeWordPorcupineViewState: PorcupineViewState = PorcupineViewState(),
    val wakeWordUdpViewState: UdpViewState = UdpViewState(),
    val snackBarText: StableStringResource? = null,
    val isMicrophonePermissionEnabled: Boolean,
    val isMicrophonePermissionRequestVisible: Boolean = !isMicrophonePermissionEnabled && (wakeWordOption == WakeWordOption.Porcupine || wakeWordOption == WakeWordOption.Udp)
) : IConfigurationEditViewState() {

    val wakeWordOptions: ImmutableList<WakeWordOption> = WakeWordOption.values().toImmutableList()


    override val isTestingEnabled: Boolean get() = wakeWordOption != WakeWordOption.Disabled


    @Stable
    data class PorcupineViewState internal constructor(
        val accessToken: String = ConfigurationSetting.wakeWordPorcupineAccessToken.value,
        val porcupineLanguage: PorcupineLanguageOption = ConfigurationSetting.wakeWordPorcupineLanguage.value,
        val defaultOptions: ImmutableList<PorcupineDefaultKeyword> = ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value,
        val customOptionsUi: ImmutableList<PorcupineCustomKeywordViewState> = ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value.map { PorcupineCustomKeywordViewState(it) }
            .toImmutableList()
    ) {
        val languageOptions: ImmutableList<PorcupineLanguageOption> = PorcupineLanguageOption.values().toImmutableList()

        val defaultOptionsUi: ImmutableList<PorcupineDefaultKeyword> get() = defaultOptions.filter { it.option.language == porcupineLanguage }.toImmutableList()

        val keywordCount: Int get() = defaultOptionsUi.count { it.isEnabled } + customOptionsUi.count { it.keyword.isEnabled }

        @Stable
        data class PorcupineCustomKeywordViewState(
            val keyword: PorcupineCustomKeyword,
            val deleted: Boolean = false
        )
    }

    @Stable
    data class UdpViewState internal constructor(
        val outputHost: String = ConfigurationSetting.wakeWordUdpOutputHost.value,
        val outputPortText: String = ConfigurationSetting.wakeWordUdpOutputPort.value.toString()
    ) {
        val outputPort: Int get() = outputPortText.toIntOrZero()
    }

}