package org.rhasspy.mobile.viewmodel.configuration.wakeword

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.platformspecific.toIntOrZero
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationEditViewState

@Stable
data class WakeWordConfigurationViewState internal constructor(
    val wakeWordOption: WakeWordOption = ConfigurationSetting.wakeWordOption.value,
    val wakeWordPorcupineViewState: PorcupineViewState = PorcupineViewState(),
    val wakeWordUdpViewState: UdpViewState = UdpViewState(),
) : IConfigurationEditViewState() {

    val wakeWordOptions: ImmutableList<WakeWordOption> = WakeWordOption.values().toImmutableList()
    val isMicrophonePermissionRequestVisible: Boolean
        get() =
            !MicrophonePermission.granted.value && (wakeWordOption == WakeWordOption.Porcupine || wakeWordOption == WakeWordOption.Udp)

    override val hasUnsavedChanges: Boolean get() = this != WakeWordConfigurationViewState()
    override val isTestingEnabled: Boolean get() = wakeWordOption != WakeWordOption.Disabled


    @Stable
    data class PorcupineViewState internal constructor(
        val accessToken: String = ConfigurationSetting.wakeWordPorcupineAccessToken.value,
        val defaultOptions: ImmutableList<PorcupineDefaultKeyword> = ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value,
        val customOptionsUi: ImmutableList<PorcupineCustomKeywordViewState> = ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value.map { PorcupineCustomKeywordViewState(it) }
            .toImmutableList(),
        val porcupineLanguage: PorcupineLanguageOption = ConfigurationSetting.wakeWordPorcupineLanguage.value
    ) {
        val languageOptions: ImmutableList<PorcupineLanguageOption> = PorcupineLanguageOption.values().toImmutableList()
        val customOptions: ImmutableList<PorcupineCustomKeyword> get() = customOptionsUi.filter { !it.deleted }.map { it.keyword }.toImmutableList()
        val keywordCount: Int get() = defaultOptions.count { it.isEnabled } + customOptionsUi.count { it.keyword.isEnabled }

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