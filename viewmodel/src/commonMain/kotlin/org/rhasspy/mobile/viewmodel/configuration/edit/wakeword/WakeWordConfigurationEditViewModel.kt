package org.rhasspy.mobile.viewmodel.configuration.edit.wakeword

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import okio.Path
import org.koin.core.component.get
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission
import org.rhasspy.mobile.platformspecific.toIntOrZero
import org.rhasspy.mobile.platformspecific.updateList
import org.rhasspy.mobile.platformspecific.updateListItem
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewStateCreator
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.*
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.Change.SelectWakeWordOption
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputHost
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputPort
import org.rhasspy.mobile.viewmodel.configuration.edit.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.WakeWordConfigurationScreenDestination.*
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.porcupine.PorcupineKeywordConfigurationScreenDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.porcupine.PorcupineKeywordConfigurationScreenDestination.CustomKeywordScreen
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.porcupine.PorcupineKeywordConfigurationScreenDestination.DefaultKeywordScreen

@Stable
class WakeWordConfigurationEditViewModel(
    microphonePermission: MicrophonePermission,
    service: WakeWordService,
    private val viewStateCreator: ConfigurationEditViewStateCreator
) : IConfigurationEditViewModel(
    service = service
) {

    private val initialConfigurationData = WakeWordConfigurationData()

    private val _editData = MutableStateFlow(initialConfigurationData)
    private val _viewState = MutableStateFlow(
        WakeWordConfigurationViewState(
            editData = initialConfigurationData,
            screen = navigator.topScreen(EditScreen).value,
            porcupineWakeWordScreen = navigator.topScreen(DefaultKeywordScreen).value,
            isMicrophonePermissionRequestVisible = !microphonePermission.granted.value && (_editData.value.wakeWordOption == WakeWordOption.Porcupine || _editData.value.wakeWordOption == WakeWordOption.Udp),
        )
    )

    val viewState = combineState(
        _viewState,
        _editData,
        navigator.topScreen(EditScreen),
        navigator.topScreen(DefaultKeywordScreen),
        microphonePermission.granted
    ) { viewState, editData, screen, porcupineWakeWordScreen, isMicrophonePermissionGranted ->
        viewState.copy(
            editData = editData,
            screen = screen,
            porcupineWakeWordScreen = porcupineWakeWordScreen,
            isMicrophonePermissionRequestVisible = !isMicrophonePermissionGranted && (editData.wakeWordOption == WakeWordOption.Porcupine || editData.wakeWordOption == WakeWordOption.Udp)
        )
    }

    override fun initViewStateCreator(
        configurationEditViewState: MutableStateFlow<ConfigurationEditViewState>
    ): StateFlow<ConfigurationEditViewState> {
        return viewStateCreator(
            init = ::WakeWordConfigurationData,
            editData = _editData,
            configurationEditViewState = configurationEditViewState
        )
    }

    fun onEvent(event: WakeWordConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
            is PorcupineUiEvent -> onPorcupineAction(event)
            is UdpUiEvent -> onUdpAction(event)
        }
    }

    private fun onChange(change: Change) {
        _editData.update {
            when (change) {
                is SelectWakeWordOption -> it.copy(wakeWordOption = change.option)
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            RequestMicrophonePermission -> requireMicrophonePermission {}
            BackClick -> navigator.onBackPressed()
            is Navigate -> {
                navigator.navigate(action.destination)

                if (action.destination == EditPorcupineWakeWordScreen) {
                    navigator.navigate(DefaultKeywordScreen)
                }
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
        _editData.update {
            it.copy(wakeWordPorcupineConfigurationData = it.wakeWordPorcupineConfigurationData.let { data ->
                when (change) {
                    is UpdateWakeWordPorcupineAccessToken -> data.copy(accessToken = change.value)
                    is ClickPorcupineKeywordCustom -> data.copy(customOptions = data.customOptions.updateListItem(change.item) { copy(isEnabled = !isEnabled) })
                    is ClickPorcupineKeywordDefault -> data.copy(defaultOptions = data.defaultOptions.updateListItem(change.item) { copy(isEnabled = !isEnabled) })
                    is DeletePorcupineKeywordCustom -> data.copy(customOptions = data.customOptions.updateList { add(change.item) })
                    is SelectWakeWordPorcupineLanguage -> data.copy(porcupineLanguage = change.option)
                    is SetPorcupineKeywordCustom -> data.copy(customOptions = data.customOptions.updateListItem(change.item) { copy(isEnabled = change.value) })
                    is SetPorcupineKeywordDefault -> data.copy(defaultOptions = data.defaultOptions.updateListItem(change.item) { copy(isEnabled = change.value) })
                    is UndoCustomKeywordDeleted -> data.copy(deletedCustomOptions = data.deletedCustomOptions.updateList { remove(change.item) })
                    is UpdateWakeWordPorcupineKeywordCustomSensitivity -> data.copy(customOptions = data.customOptions.updateList(change.index) { copy(sensitivity = change.value) })
                    is UpdateWakeWordPorcupineKeywordDefaultSensitivity -> data.copy(defaultOptions = data.defaultOptions.updateListItem(change.item) { copy(sensitivity = change.value) })
                    is AddPorcupineKeywordCustom -> data.copy(customOptions = data.customOptions.updateList {
                        add(PorcupineCustomKeyword(fileName = change.path.name, isEnabled = true, sensitivity = 0.5f))
                    })
                }
            })
        }
    }

    private fun onPorcupineAction(action: PorcupineUiEvent.Action) {
        when (action) {
            AddCustomPorcupineKeyword -> addCustomPorcupineKeyword()
            DownloadCustomPorcupineKeyword -> openLink(LinkType.PicoVoiceCustomWakeWord)
            OpenPicoVoiceConsole -> openLink(LinkType.PicoVoiceConsole)
            PorcupineUiEvent.Action.BackClick -> navigator.onBackPressed()
            PorcupineLanguageClick -> navigator.navigate(EditPorcupineLanguageScreen)
            is PageClick -> navigator.replace<PorcupineKeywordConfigurationScreenDestination>(action.screen)
        }
    }

    //for custom wake word
    private val newFiles = mutableListOf<Path>()
    private val filesToDelete = mutableListOf<Path>()

    private fun addCustomPorcupineKeyword() {
        selectFile(FolderType.PorcupineFolder) { path ->
            newFiles.add(path)
            onPorcupineChange(AddPorcupineKeywordCustom(path))
        }
    }

    private fun onUdpAction(action: UdpUiEvent) {
        when (action) {
            is UdpUiEvent.Change -> onUdpChange(action)
        }
    }

    private fun onUdpChange(change: UdpUiEvent.Change) {
        _editData.update {
            it.copy(wakeWordUdpConfigurationData = it.wakeWordUdpConfigurationData.let { data ->
                when (change) {
                    is UpdateUdpOutputHost -> data.copy(outputHost = change.value)
                    is UpdateUdpOutputPort -> data.copy(outputPort = change.value.toIntOrZero())
                }
            })
        }
    }


    override fun onSave() {
        with(_editData.value) {
            ConfigurationSetting.wakeWordOption.value = wakeWordOption

            with(wakeWordPorcupineConfigurationData) {
                ConfigurationSetting.wakeWordPorcupineAccessToken.value = accessToken
                ConfigurationSetting.wakeWordPorcupineLanguage.value = porcupineLanguage
                ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value = defaultOptions
                ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value = customOptions.updateList {
                    removeAll(deletedCustomOptions)
                }
            }

            with(wakeWordUdpConfigurationData) {
                ConfigurationSetting.wakeWordUdpOutputHost.value = outputHost
                ConfigurationSetting.wakeWordUdpOutputPort.value = outputPort.toIntOrZero()
            }
        }

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

    override fun onBackPressed(): Boolean {
        return if (viewState.value.screen == EditPorcupineWakeWordScreen && viewState.value.porcupineWakeWordScreen == DefaultKeywordScreen) {
            //pop backstack to remove DefaultKeywordScreen
            navigator.popBackStack()
            //pop backstack to remove EditPorcupineWakeWordScreen and go to keyword edit
            navigator.popBackStack()
            //was handled
            true
        } else if (viewState.value.screen == EditPorcupineWakeWordScreen && viewState.value.porcupineWakeWordScreen == CustomKeywordScreen) {
            //navigate to DefaultKeywordScreen
            navigator.replace<PorcupineKeywordConfigurationScreenDestination>(DefaultKeywordScreen)
            //was handled
            true
        } else if (viewState.value.screen == EditScreen) {
            super.onBackPressed()
        } else {
            //close porcupine language or keyword screen
            false
        }
    }

}