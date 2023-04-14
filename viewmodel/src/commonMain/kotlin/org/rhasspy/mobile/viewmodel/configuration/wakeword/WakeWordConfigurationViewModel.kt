package org.rhasspy.mobile.viewmodel.configuration.wakeword

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.Path
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.logic.openLink
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.logic.services.wakeword.WakeWordServiceParams
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath
import org.rhasspy.mobile.platformspecific.file.FileUtils
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.Change
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.Change.SelectWakeWordOption
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.Navigate
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.Navigate.MicrophonePermissionAllowed
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction.Change.AddPorcupineKeywordCustom
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction.Change.ClickPorcupineKeywordCustom
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction.Change.ClickPorcupineKeywordDefault
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction.Change.DeletePorcupineKeywordCustom
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction.Change.SelectWakeWordPorcupineLanguage
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction.Change.TogglePorcupineKeywordCustom
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction.Change.TogglePorcupineKeywordDefault
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction.Change.UndoCustomKeywordDeleted
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction.Change.UpdateWakeWordPorcupineAccessToken
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction.Change.UpdateWakeWordPorcupineKeywordCustomSensitivity
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction.Change.UpdateWakeWordPorcupineKeywordDefaultSensitivity
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction.Navigate.AddCustomPorcupineKeyword
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction.Navigate.DownloadCustomPorcupineKeyword
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.PorcupineUiAction.Navigate.OpenPicoVoiceConsole
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.UdpUiAction
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.UdpUiAction.Change.UpdateUdpOutputHost
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiAction.UdpUiAction.Change.UpdateUdpOutputPort

@Stable
class WakeWordConfigurationViewModel(
    service: WakeWordService,
    testRunner: WakeWordConfigurationTest
) : IConfigurationViewModel<WakeWordConfigurationTest, WakeWordConfigurationViewState>(
    service = service,
    testRunner = testRunner,
    initialViewState = ::WakeWordConfigurationViewState
) {

    fun onAction(action: WakeWordConfigurationUiAction) {
        when (action) {
            is Change -> onChange(action)
            is Navigate -> onNavigate(action)
            is PorcupineUiAction -> onPorcupineAction(action)
            is UdpUiAction -> onUdpAction(action)
        }
    }

    private fun onChange(change: Change) {
        contentViewState.update {
            when(change) {
                is SelectWakeWordOption -> it.copy(wakeWordOption = change.option)
            }
        }
    }

    private fun onNavigate(navigate: Navigate) {
        when (navigate) {
            MicrophonePermissionAllowed ->
                if (!contentViewState.value.hasUnsavedChanges) {
                    save()
                }
        }
    }

    private fun onPorcupineAction(action: PorcupineUiAction) {
        when(action) {
            is PorcupineUiAction.Change -> onPorcupineChange(action)
            is PorcupineUiAction.Navigate -> onPorcupineNavigate(action)
        }
    }

    private fun onPorcupineChange(change: PorcupineUiAction.Change) {
        contentViewState.update { contentViewState ->
            val it = contentViewState.wakeWordPorcupineViewState
            contentViewState.copy(
                wakeWordPorcupineViewState =
                when (change) {
                    is UpdateWakeWordPorcupineAccessToken -> it.copy(accessToken = change.value)
                    is ClickPorcupineKeywordCustom ->
                        it.copy(customOptionsUi = it.customOptionsUi.toMutableList()
                            .also {
                                it[change.index] = it[change.index].let { item ->
                                    item.copy(keyword = item.keyword.copy(isEnabled = !item.keyword.isEnabled))
                                }
                            }
                            .toImmutableList())

                    is ClickPorcupineKeywordDefault ->
                        it.copy(defaultOptions = it.defaultOptions.toMutableList()
                            .also { it[change.index] = it[change.index].copy(isEnabled = !it[change.index].isEnabled) }
                            .toImmutableList())

                    is AddPorcupineKeywordCustom -> it.copy(customOptionsUi = it.customOptionsUi.toMutableList()
                        .apply { add(PorcupineCustomKeywordUi(PorcupineCustomKeyword(change.path.name, true, 0.5f)))}
                        .toImmutableList())
                    is DeletePorcupineKeywordCustom -> it.copy(customOptionsUi = it.customOptionsUi.toMutableList()
                        .also { it[change.index] = it[change.index].copy(deleted = true) }
                        .toImmutableList())
                    is SelectWakeWordPorcupineLanguage -> it.copy(porcupineLanguage = change.option)
                    is TogglePorcupineKeywordCustom ->
                        it.copy(customOptionsUi = it.customOptionsUi.toMutableList()
                            .also {
                                it[change.index] = it[change.index].let { item ->
                                    item.copy(keyword = item.keyword.copy(isEnabled = change.value))
                                }
                            }
                            .toImmutableList())

                    is TogglePorcupineKeywordDefault ->
                        it.copy(defaultOptions = it.defaultOptions.toMutableList()
                            .also { it[change.index] = it[change.index].copy(isEnabled = change.value) }
                            .toImmutableList())

                    is UndoCustomKeywordDeleted -> it.copy(customOptionsUi = it.customOptionsUi.toMutableList()
                        .also { it[change.index] = it[change.index].copy(deleted = false) }
                        .toImmutableList())
                    is UpdateWakeWordPorcupineKeywordCustomSensitivity ->
                        it.copy(customOptionsUi = it.customOptionsUi.toMutableList()
                            .also {
                                it[change.index] = it[change.index].let { item ->
                                    item.copy(keyword = item.keyword.copy(sensitivity = change.value))
                                }
                            }
                            .toImmutableList())

                    is UpdateWakeWordPorcupineKeywordDefaultSensitivity ->
                        it.copy(defaultOptions = it.defaultOptions.toMutableList()
                            .also { it[change.index] = it[change.index].copy(sensitivity = change.value) }
                            .toImmutableList())
                }
            )
        }
    }

    private fun onPorcupineNavigate(navigate: PorcupineUiAction.Navigate) {
        when(navigate) {
            AddCustomPorcupineKeyword -> addCustomPorcupineKeyword()
            DownloadCustomPorcupineKeyword -> openLink("https://console.picovoice.ai/ppn")
            OpenPicoVoiceConsole -> openLink("https://console.picovoice.ai")
        }
    }

    //for custom wake word
    private val newFiles = mutableListOf<Path>()
    private val filesToDelete = mutableListOf<Path>()

    private fun addCustomPorcupineKeyword() {
        viewModelScope.launch {
            FileUtils.selectFile(FolderType.PorcupineFolder)?.also { path ->
                newFiles.add(path)
                onPorcupineChange(AddPorcupineKeywordCustom(path))
            }
        }
    }

    private fun onUdpAction(action: UdpUiAction) {
        when(action) {
            is UdpUiAction.Change -> onUdpChange(action)
        }
    }

    private fun onUdpChange(change: UdpUiAction.Change) {
        contentViewState.update { contentViewState ->
            val it = contentViewState.wakeWordUdpViewState
            contentViewState.copy(
                wakeWordUdpViewState =
                when (change) {
                    is UpdateUdpOutputHost -> it.copy(outputHost = change.value)
                    is UpdateUdpOutputPort -> it.copy(outputPortText = change.value)
                }
            )
        }
    }


    override fun onSave() {
        ConfigurationSetting.wakeWordOption.value = data.wakeWordOption
        ConfigurationSetting.wakeWordPorcupineAccessToken.value = data.wakeWordPorcupineViewState.accessToken
        ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value = data.wakeWordPorcupineViewState.defaultOptions
        ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value = data.wakeWordPorcupineViewState.customOptionsUi
            .filter { !it.deleted }.map { it.keyword }
            .toImmutableList()
        ConfigurationSetting.wakeWordPorcupineLanguage.value = data.wakeWordPorcupineViewState.porcupineLanguage
        ConfigurationSetting.wakeWordUdpOutputHost.value = data.wakeWordUdpViewState.outputHost
        ConfigurationSetting.wakeWordUdpOutputPort.value = data.wakeWordUdpViewState.outputPort

        filesToDelete.forEach {
            Path.commonInternalPath(get(),"${FolderType.PorcupineFolder}/$it").commonDelete()
        }
        filesToDelete.clear()
        newFiles.clear()
    }

    override fun onDiscard() {
        newFiles.forEach {
            Path.commonInternalPath(get(),"${FolderType.PorcupineFolder}/$it").commonDelete()
        }
        newFiles.clear()
        filesToDelete.clear()
    }

    override fun initializeTestParams() {
        get<WakeWordServiceParams> {
            parametersOf(
                WakeWordServiceParams(
                    wakeWordOption = data.wakeWordOption,
                    wakeWordPorcupineAccessToken = data.wakeWordPorcupineViewState.accessToken,
                    wakeWordPorcupineKeywordDefaultOptions = data.wakeWordPorcupineViewState.defaultOptions,
                    wakeWordPorcupineKeywordCustomOptions = data.wakeWordPorcupineViewState.customOptions,
                    wakeWordPorcupineLanguage = data.wakeWordPorcupineViewState.porcupineLanguage,
                    wakeWordUdpOutputHost = data.wakeWordUdpViewState.outputHost,
                    wakeWordUdpOutputPort = data.wakeWordUdpViewState.outputPort
                )
            )
        }
    }

    fun startWakeWordDetection() = testRunner.startWakeWordDetection()

}