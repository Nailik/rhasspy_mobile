package org.rhasspy.mobile.viewmodel.configuration.wakeword

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.StateFlow
import okio.Path
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.ServiceStateHeaderViewState
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewModel.PorcupineCustomKeywordUi

@Stable
data class WakeWordConfigurationViewState(
    val wakeWordOption: WakeWordOption,
    val wakeWordPorcupineAccessToken: String,
    val wakeWordPorcupineKeywordDefaultOptions: ImmutableSet<PorcupineDefaultKeyword>,
    val wakeWordPorcupineKeywordCustomOptions: ImmutableList<PorcupineCustomKeywordUi>,
    val wakeWordPorcupineKeywordCustomOptionsNormal: ImmutableSet<PorcupineCustomKeyword>,
    val wakeWordPorcupineLanguage: PorcupineLanguageOption,
    val wakeWordUdpOutputHost: String,
    val wakeWordUdpOutputPort: Int,
    val wakeWordUdpOutputPortText: String
): IConfigurationContentViewState() {

    companion object {
        fun getInitial() = WakeWordConfigurationViewState(
            wakeWordOption = ConfigurationSetting.wakeWordOption.value,
            wakeWordPorcupineAccessToken = ConfigurationSetting.wakeWordPorcupineAccessToken.value,
            wakeWordPorcupineKeywordDefaultOptions = ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value,
            wakeWordPorcupineKeywordCustomOptions = ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value
                .map { PorcupineCustomKeywordUi(it) }.toImmutableList(),
            wakeWordPorcupineKeywordCustomOptionsNormal = ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value,
            wakeWordPorcupineLanguage = ConfigurationSetting.wakeWordPorcupineLanguage.value,
            wakeWordUdpOutputHost = ConfigurationSetting.wakeWordUdpOutputHost.value,
            wakeWordUdpOutputPort = ConfigurationSetting.wakeWordUdpOutputPort.value,
            wakeWordUdpOutputPortText = ConfigurationSetting.wakeWordUdpOutputPort.value.toString()
        )
    }

    override fun getEditViewState(serviceViewState: StateFlow<ServiceStateHeaderViewState>): IConfigurationEditViewState {
        return IConfigurationEditViewState(
            hasUnsavedChanges = !(wakeWordOption == ConfigurationSetting.wakeWordOption.value &&
            wakeWordPorcupineAccessToken == ConfigurationSetting.wakeWordPorcupineAccessToken.value &&
            wakeWordPorcupineKeywordDefaultOptions == ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value &&
            wakeWordPorcupineKeywordCustomOptionsNormal == ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value &&
            wakeWordPorcupineLanguage == ConfigurationSetting.wakeWordPorcupineLanguage.value &&
            wakeWordUdpOutputHost == ConfigurationSetting.wakeWordUdpOutputHost.value &&
            wakeWordUdpOutputPort == ConfigurationSetting.wakeWordUdpOutputPort.value),
            isTestingEnabled = wakeWordOption != WakeWordOption.Disabled,
            serviceViewState = serviceViewState
        )
    }

    private val newFiles = mutableListOf<String>()
    private val filesToDelete = mutableListOf<String>()

    override fun save() {
        ConfigurationSetting.wakeWordOption.value = wakeWordOption
        ConfigurationSetting.wakeWordPorcupineAccessToken.value = wakeWordPorcupineAccessToken
        ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value = wakeWordPorcupineKeywordDefaultOptions
        ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value = wakeWordPorcupineKeywordCustomOptions
            .filter { !it.deleted }.map { it.keyword }
            .toImmutableSet()
        ConfigurationSetting.wakeWordPorcupineLanguage.value = wakeWordPorcupineLanguage
        ConfigurationSetting.wakeWordUdpOutputHost.value = wakeWordUdpOutputHost
        ConfigurationSetting.wakeWordUdpOutputPort.value = wakeWordUdpOutputPort

        filesToDelete.forEach {
            Path.commonInternalPath(get(),"${FolderType.PorcupineFolder}/$it").commonDelete()
        }
        filesToDelete.clear()
        newFiles.clear()
    }

    override fun discard() {
        newFiles.forEach {
            Path.commonInternalPath(get(),"${FolderType.PorcupineFolder}/$it").commonDelete()
        }
        filesToDelete.clear()
    }

}