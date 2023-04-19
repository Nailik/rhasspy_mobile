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
import org.rhasspy.mobile.platformspecific.updateList
import org.rhasspy.mobile.platformspecific.updateViewState
import org.rhasspy.mobile.platformspecific.updateViewStateFlow
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Action.MicrophonePermissionAllowed
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Action.TestStartWakeWord
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Change.SelectWakeWordOption
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputHost
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputPort
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.PorcupineViewState.PorcupineCustomKeywordViewState

@Stable
class WakeWordConfigurationViewModel(
    service: WakeWordService
) : IConfigurationViewModel<WakeWordConfigurationViewState>(
    service = service,
    initialViewState = ::WakeWordConfigurationViewState
) {

    fun onEvent(event: WakeWordConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
            is PorcupineUiEvent -> onPorcupineAction(event)
            is UdpUiEvent -> onUdpAction(event)
        }
    }

    private fun onChange(change: Change) {
        contentViewState.update {
            when (change) {
                is SelectWakeWordOption -> it.copy(wakeWordOption = change.option)
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            MicrophonePermissionAllowed -> if (!contentViewState.value.hasUnsavedChanges) {
                save()
            }

            TestStartWakeWord -> startWakeWordDetection()
        }
    }

    private fun onPorcupineAction(action: PorcupineUiEvent) {
        when (action) {
            is PorcupineUiEvent.Change -> onPorcupineChange(action)
            is PorcupineUiEvent.Action -> onPorcupineNavigate(action)
        }
    }

    private fun onPorcupineChange(change: PorcupineUiEvent.Change) {
        contentViewState.updateViewStateFlow {
            copy(wakeWordPorcupineViewState = wakeWordPorcupineViewState.updateViewState {
                when (change) {
                    is UpdateWakeWordPorcupineAccessToken -> copy(accessToken = change.value)
                    is ClickPorcupineKeywordCustom -> copy(customOptionsUi = customOptionsUi.updateList(change.index) { copy(keyword = keyword.copy(isEnabled = !keyword.isEnabled)) })
                    is ClickPorcupineKeywordDefault -> copy(defaultOptions = defaultOptions.updateList(change.index) { copy(isEnabled = !isEnabled) })
                    is DeletePorcupineKeywordCustom -> copy(customOptionsUi = customOptionsUi.updateList(change.index) { copy(deleted = true) })
                    is SelectWakeWordPorcupineLanguage -> copy(porcupineLanguage = change.option)
                    is SetPorcupineKeywordCustom -> copy(customOptionsUi = customOptionsUi.updateList(change.index) { copy(keyword = keyword.copy(isEnabled = change.value)) })
                    is SetPorcupineKeywordDefault -> copy(defaultOptions = defaultOptions.updateList(change.index) { copy(isEnabled = change.value) })
                    is UndoCustomKeywordDeleted -> copy(customOptionsUi = customOptionsUi.updateList(change.index) { copy(deleted = false) })
                    is UpdateWakeWordPorcupineKeywordCustomSensitivity -> copy(customOptionsUi = customOptionsUi.updateList(change.index) { copy(keyword = keyword.copy(sensitivity = change.value)) })
                    is UpdateWakeWordPorcupineKeywordDefaultSensitivity -> copy(defaultOptions = defaultOptions.updateList(change.index) { copy(sensitivity = change.value) })
                    is AddPorcupineKeywordCustom ->
                        copy(customOptionsUi = customOptionsUi.updateList {
                            add(
                                PorcupineCustomKeywordViewState(
                                    PorcupineCustomKeyword(
                                        fileName = change.path.name,
                                        isEnabled = true,
                                        sensitivity = 0.5f
                                    )
                                )
                            )
                        })
                }
            })
        }
    }

    private fun onPorcupineNavigate(action: PorcupineUiEvent.Action) {
        when (action) {
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

    private fun onUdpAction(action: UdpUiEvent) {
        when (action) {
            is UdpUiEvent.Change -> onUdpChange(action)
        }
    }

    private fun onUdpChange(change: UdpUiEvent.Change) {
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
            Path.commonInternalPath(get(), "${FolderType.PorcupineFolder}/$it").commonDelete()
        }
        filesToDelete.clear()
        newFiles.clear()
    }

    override fun onDiscard() {
        newFiles.forEach {
            Path.commonInternalPath(get(), "${FolderType.PorcupineFolder}/$it").commonDelete()
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

    private fun startWakeWordDetection() {
        get<WakeWordService>().startDetection()
    }

}