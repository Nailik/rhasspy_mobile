package org.rhasspy.mobile.viewmodel.configuration.wakeword

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.Path
import org.koin.core.component.get
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath
import org.rhasspy.mobile.platformspecific.file.FileUtils
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission
import org.rhasspy.mobile.platformspecific.updateList
import org.rhasspy.mobile.platformspecific.updateListItem
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Action.MicrophonePermissionAllowed
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Action.TestStartWakeWord
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Change.SelectWakeWordOption
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Consumed.ShowSnackBar
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputHost
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputPort
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.PorcupineViewState.PorcupineCustomKeywordViewState
import org.rhasspy.mobile.viewmodel.utils.OpenLinkUtils

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
            is Consumed -> onConsumed(event)
            is PorcupineUiEvent -> onPorcupineAction(event)
            is UdpUiEvent -> onUdpAction(event)
        }
    }

    private fun onChange(change: Change) {
        contentViewState.update {
            when (change) {
                is SelectWakeWordOption -> it.copy(
                    wakeWordOption = change.option,
                    isMicrophonePermissionRequestVisible = !MicrophonePermission.granted.value && (change.option == WakeWordOption.Porcupine || change.option == WakeWordOption.Udp)
                )
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            MicrophonePermissionAllowed -> {
                contentViewState.update { it.copy(isMicrophonePermissionRequestVisible = false) }
                if (!viewState.value.hasUnsavedChanges) {
                    onAction(Save)
                }
            }

            TestStartWakeWord -> startWakeWordDetection()
        }
    }

    private fun onConsumed(consumed: Consumed) {
        contentViewState.update {
            when (consumed) {
                is ShowSnackBar -> it.copy(snackBarText = null)
            }
        }
    }

    private fun onPorcupineAction(action: PorcupineUiEvent) {
        when (action) {
            is PorcupineUiEvent.Change -> onPorcupineChange(action)
            is PorcupineUiEvent.Action -> onPorcupineNavigate(action)
        }
    }

    private fun onPorcupineChange(change: PorcupineUiEvent.Change) {
        contentViewState.update { viewStateFlow ->
            viewStateFlow.copy(wakeWordPorcupineViewState = viewStateFlow.wakeWordPorcupineViewState.let {
                when (change) {
                    is UpdateWakeWordPorcupineAccessToken -> it.copy(accessToken = change.value)
                    is ClickPorcupineKeywordCustom -> it.copy(customOptionsUi = it.customOptionsUi.updateList(change.index) { copy(keyword = keyword.copy(isEnabled = !keyword.isEnabled)) })
                    is ClickPorcupineKeywordDefault -> it.copy(defaultOptions = it.defaultOptions.updateListItem(change.item) { copy(isEnabled = !isEnabled) })
                    is DeletePorcupineKeywordCustom -> it.copy(customOptionsUi = it.customOptionsUi.updateList(change.index) { copy(deleted = true) })
                    is SelectWakeWordPorcupineLanguage -> it.copy(porcupineLanguage = change.option)
                    is SetPorcupineKeywordCustom -> it.copy(customOptionsUi = it.customOptionsUi.updateList(change.index) { copy(keyword = keyword.copy(isEnabled = change.value)) })
                    is SetPorcupineKeywordDefault -> it.copy(defaultOptions = it.defaultOptions.updateListItem(change.item) { copy(isEnabled = change.value) })
                    is UndoCustomKeywordDeleted -> it.copy(customOptionsUi = it.customOptionsUi.updateList(change.index) { copy(deleted = false) })
                    is UpdateWakeWordPorcupineKeywordCustomSensitivity -> it.copy(customOptionsUi = it.customOptionsUi.updateList(change.index) { copy(keyword = keyword.copy(sensitivity = change.value)) })
                    is UpdateWakeWordPorcupineKeywordDefaultSensitivity -> it.copy(defaultOptions = it.defaultOptions.updateListItem(change.item) { copy(sensitivity = change.value) })
                    is AddPorcupineKeywordCustom ->
                        it.copy(customOptionsUi = it.customOptionsUi.updateList {
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
            DownloadCustomPorcupineKeyword -> {
                if (!OpenLinkUtils.openLink(LinkType.PicoVoiceCustomWakeWord)) {
                    contentViewState.update {
                        it.copy(snackBarText = MR.strings.linkOpenFailed.stable)
                    }
                }
            }

            OpenPicoVoiceConsole -> {
                if (!OpenLinkUtils.openLink(LinkType.PicoVoiceConsole)) {
                    contentViewState.update {
                        it.copy(snackBarText = MR.strings.linkOpenFailed.stable)
                    }
                }
            }
        }
    }

    //for custom wake word
    private val newFiles = mutableListOf<Path>()
    private val filesToDelete = mutableListOf<Path>()

    private fun addCustomPorcupineKeyword() {
        viewModelScope.launch(Dispatchers.Default) {
            FileUtils.selectFile(FolderType.PorcupineFolder)?.also { path ->
                newFiles.add(path)
                onPorcupineChange(AddPorcupineKeywordCustom(path))
            } ?: run {
                contentViewState.update {
                    it.copy(snackBarText = MR.strings.selectFileFailed.stable)
                }
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

    private fun startWakeWordDetection() {
        get<WakeWordService>().startDetection()
    }

}