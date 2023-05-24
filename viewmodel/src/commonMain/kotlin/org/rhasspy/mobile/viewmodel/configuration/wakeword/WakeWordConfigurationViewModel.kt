package org.rhasspy.mobile.viewmodel.configuration.wakeword

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import okio.Path
import org.koin.core.component.get
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
import org.rhasspy.mobile.platformspecific.utils.OpenLinkUtils
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Change.SelectWakeWordOption
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Consumed.ShowSnackBar
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputHost
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputPort
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.PorcupineViewState.PorcupineCustomKeywordViewState
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.WakeWordConfigurationScreenDestination.*
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.porcupine.PorcupineKeywordConfigurationScreenDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.porcupine.PorcupineKeywordConfigurationScreenDestination.CustomKeywordScreen
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.porcupine.PorcupineKeywordConfigurationScreenDestination.DefaultKeywordScreen

@Stable
class WakeWordConfigurationViewModel(
    microphonePermission: MicrophonePermission,
    service: WakeWordService
) : IConfigurationViewModel<WakeWordConfigurationViewState>(
    service = service,
    initialViewState = { WakeWordConfigurationViewState(isMicrophonePermissionEnabled = microphonePermission.granted.value) },
    testPageDestination = TestScreen
) {

    val screen = navigator.topScreen(EditScreen)
    val porcupineScreen = navigator.topScreen(DefaultKeywordScreen)

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
        updateViewState {
            when (change) {
                is SelectWakeWordOption -> it.copy(
                    wakeWordOption = change.option,
                    isMicrophonePermissionRequestVisible = !microphonePermission.granted.value && (change.option == WakeWordOption.Porcupine || change.option == WakeWordOption.Udp)
                )
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            RequestMicrophonePermission -> requireMicrophonePermission {
                updateViewState { it.copy(isMicrophonePermissionRequestVisible = false) }
                if (!viewState.value.hasUnsavedChanges) {
                    onAction(Save)
                }
            }

            TestStartWakeWord -> requireMicrophonePermission(::startWakeWordDetection)
            BackClick -> navigator.onBackPressed()
            is Navigate -> {
                navigator.navigate(action.destination)

                if (action.destination == EditPorcupineWakeWordScreen) {
                    navigator.navigate(DefaultKeywordScreen)
                }
            }
        }
    }

    private fun onConsumed(consumed: Consumed) {
        updateViewState {
            when (consumed) {
                is ShowSnackBar -> it.copy(snackBarText = null)
            }
        }
    }

    private fun onPorcupineAction(action: PorcupineUiEvent) {
        when (action) {
            is PorcupineUiEvent.Change -> onPorcupineChange(action)
            is PorcupineUiEvent.Action -> onPorcupineAction(action)
        }
    }

    private fun onPorcupineChange(change: PorcupineUiEvent.Change) {
        updateViewState { viewStateFlow ->
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
                    is AddPorcupineKeywordCustom -> it.copy(customOptionsUi = it.customOptionsUi.updateList {
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

    private fun onPorcupineAction(action: PorcupineUiEvent.Action) {
        when (action) {
            AddCustomPorcupineKeyword -> addCustomPorcupineKeyword()
            DownloadCustomPorcupineKeyword -> {
                if (!get<OpenLinkUtils>().openLink(LinkType.PicoVoiceCustomWakeWord)) {
                    updateViewState {
                        it.copy(snackBarText = MR.strings.linkOpenFailed.stable)
                    }
                }
            }

            OpenPicoVoiceConsole -> {
                if (!get<OpenLinkUtils>().openLink(LinkType.PicoVoiceConsole)) {
                    updateViewState {
                        it.copy(snackBarText = MR.strings.linkOpenFailed.stable)
                    }
                }
            }

            PorcupineUiEvent.Action.BackClick -> navigator.onBackPressed()
            PorcupineLanguageClick -> navigator.navigate(EditPorcupineLanguageScreen)
            is PageClick -> navigator.replace<PorcupineKeywordConfigurationScreenDestination>(action.screen)
        }
    }

    //for custom wake word
    private val newFiles = mutableListOf<Path>()
    private val filesToDelete = mutableListOf<Path>()

    private fun addCustomPorcupineKeyword() {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.selectFile(FolderType.PorcupineFolder)?.also { path ->
                newFiles.add(path)
                onPorcupineChange(AddPorcupineKeywordCustom(path))
            } ?: run {
                updateViewState {
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
        updateViewState { contentViewState ->
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

    override fun onBackPressed(): Boolean {
        return if (screen.value == EditPorcupineWakeWordScreen && porcupineScreen.value == DefaultKeywordScreen) {
            //pop backstack to remove DefaultKeywordScreen
            navigator.popBackStack()
            //pop backstack to remove EditPorcupineWakeWordScreen and go to keyword edit
            navigator.popBackStack()
            //was handled
            true
        } else if (screen.value == EditPorcupineWakeWordScreen && porcupineScreen.value == CustomKeywordScreen) {
            //navigate to DefaultKeywordScreen
            navigator.replace<PorcupineKeywordConfigurationScreenDestination>(DefaultKeywordScreen)
            //was handled
            true
        } else if (screen.value == EditScreen || screen.value == TestScreen) {
            super.onBackPressed()
        } else {
            //close porcupine language or keyword screen
            false
        }
    }

}