package org.rhasspy.mobile.viewmodel.configuration.wakeword

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.Path
import org.koin.core.component.get
import org.koin.core.component.inject
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.services.wakeword.IWakeWordService
import org.rhasspy.mobile.platformspecific.*
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.extensions.commonInternalPath
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Change.SelectWakeWordOption
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputHost
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputPort
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.PorcupineKeywordConfigurationScreenDestination
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.PorcupineKeywordConfigurationScreenDestination.CustomKeywordScreen
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.PorcupineKeywordConfigurationScreenDestination.DefaultKeywordScreen
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.SettingsScreenDestination.AudioRecorderSettings
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.WakeWordConfigurationScreenDestination
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.WakeWordConfigurationScreenDestination.EditPorcupineLanguageScreen
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.WakeWordConfigurationScreenDestination.OverviewScreen
import org.rhasspy.mobile.viewmodel.navigation.topScreen

@Stable
class WakeWordConfigurationViewModel(
    microphonePermission: IMicrophonePermission,
    service: IWakeWordService
) : ConfigurationViewModel(
    service = service
) {

    private val dispatcher by inject<IDispatcherProvider>()

    private val initialData = WakeWordConfigurationData()
    private val _viewState = MutableStateFlow(
        WakeWordConfigurationViewState(
            editData = initialData,
            screen = navigator.topScreen(OverviewScreen).value,
            porcupineWakeWordScreen = navigator.topScreen(DefaultKeywordScreen).value,
            isMicrophonePermissionRequestVisible = !microphonePermission.granted.value && (initialData.wakeWordOption == WakeWordOption.Porcupine || initialData.wakeWordOption == WakeWordOption.Udp),
        )
    )
    val viewState = _viewState.readOnly

    override fun initViewStateCreator(
        configurationViewState: MutableStateFlow<ConfigurationViewState>
    ): StateFlow<ConfigurationViewState> {
        return viewStateCreator(
            init = ::WakeWordConfigurationData,
            viewState = viewState,
            configurationViewState = configurationViewState
        )
    }

    init {
        viewModelScope.launch(dispatcher.IO) {
            combineStateFlow(
                navigator.topScreen(OverviewScreen),
                navigator.topScreen(DefaultKeywordScreen),
                microphonePermission.granted
            ).collect { data ->
                val screen: WakeWordConfigurationScreenDestination =
                    data[0] as WakeWordConfigurationScreenDestination
                val porcupineWakeWordScreen: PorcupineKeywordConfigurationScreenDestination =
                    data[1] as PorcupineKeywordConfigurationScreenDestination
                val isMicrophonePermissionGranted = data[2] as Boolean

                _viewState.update {
                    it.copy(
                        screen = screen,
                        porcupineWakeWordScreen = porcupineWakeWordScreen,
                        isMicrophonePermissionRequestVisible = !isMicrophonePermissionGranted && (it.editData.wakeWordOption == WakeWordOption.Porcupine || it.editData.wakeWordOption == WakeWordOption.Udp)
                    )
                }
            }


        }
    }

    fun onEvent(event: WakeWordConfigurationUiEvent) {
        when (event) {
            is Change           -> onChange(event)
            is Action           -> onAction(event)
            is PorcupineUiEvent -> onPorcupineAction(event)
            is UdpUiEvent       -> onUdpAction(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SelectWakeWordOption -> copy(wakeWordOption = change.option)
                }
            })
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            RequestMicrophonePermission -> requireMicrophonePermission {}
            BackClick                   -> navigator.onBackPressed()
            is Navigate                 -> {
                navigator.navigate(action.destination)
            }

            OpenAudioRecorderSettings   -> navigator.navigate(AudioRecorderSettings)
        }
    }

    private fun onPorcupineAction(action: PorcupineUiEvent) {
        when (action) {
            is PorcupineUiEvent.Change -> onPorcupineChange(action)
            is PorcupineUiEvent.Action -> onPorcupineAction(action)
        }
    }

    private fun onPorcupineChange(change: PorcupineUiEvent.Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                copy(wakeWordPorcupineConfigurationData = with(wakeWordPorcupineConfigurationData) {
                    when (change) {
                        is UpdateWakeWordPorcupineAccessToken               -> copy(accessToken = change.value)
                        is SetPorcupineAudioRecorderSettings                -> copy(isUseAudioRecorderSettings = change.enabled)
                        is ClickPorcupineKeywordCustom                      -> copy(customOptions = customOptions.updateListItem(change.item) { copy(isEnabled = !isEnabled) })
                        is ClickPorcupineKeywordDefault                     -> copy(defaultOptions = defaultOptions.updateListItem(change.item) { copy(isEnabled = !isEnabled) })
                        is DeletePorcupineKeywordCustom                     -> copy(deletedCustomOptions = deletedCustomOptions.updateList { add(change.item) })
                        is SelectWakeWordPorcupineLanguage                  -> copy(porcupineLanguage = change.option)
                        is SetPorcupineKeywordCustom                        -> copy(customOptions = customOptions.updateListItem(change.item) { copy(isEnabled = change.value) })
                        is SetPorcupineKeywordDefault                       -> copy(defaultOptions = defaultOptions.updateListItem(change.item) { copy(isEnabled = change.value) })
                        is UndoCustomKeywordDeleted                         -> copy(deletedCustomOptions = deletedCustomOptions.updateList { remove(change.item) })
                        is UpdateWakeWordPorcupineKeywordCustomSensitivity  -> copy(customOptions = customOptions.updateListItem(change.item) { copy(sensitivity = change.value) })
                        is UpdateWakeWordPorcupineKeywordDefaultSensitivity -> copy(defaultOptions = defaultOptions.updateListItem(change.item) { copy(sensitivity = change.value) })
                        is AddPorcupineKeywordCustom                        -> copy(customOptions = customOptions.updateList {
                            add(
                                PorcupineCustomKeyword(
                                    fileName = change.path.name,
                                    isEnabled = true,
                                    sensitivity = 0.5f
                                )
                            )
                        })
                    }
                })
            })
        }
    }

    private fun onPorcupineAction(action: PorcupineUiEvent.Action) {
        when (action) {
            AddCustomPorcupineKeyword         -> addCustomPorcupineKeyword()
            DownloadCustomPorcupineKeyword    -> openLink(LinkType.PicoVoiceCustomWakeWord)
            OpenPicoVoiceConsole              -> openLink(LinkType.PicoVoiceConsole)
            PorcupineUiEvent.Action.BackClick -> navigator.onBackPressed()
            PorcupineLanguageClick            -> navigator.navigate(EditPorcupineLanguageScreen)
            is PageClick                      -> _viewState.update { it.copy(porcupineWakeWordScreen = action.screen) }
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
        _viewState.update {
            it.copy(editData = with(it.editData) {
                copy(wakeWordUdpConfigurationData = wakeWordUdpConfigurationData.let { data ->
                    when (change) {
                        is UpdateUdpOutputHost -> data.copy(outputHost = change.value)
                        is UpdateUdpOutputPort -> data.copy(outputPort = change.value.toIntOrNullOrConstant())
                    }
                })
            })
        }
    }


    override fun onSave() {
        with(_viewState.value.editData) {
            ConfigurationSetting.wakeWordOption.value = wakeWordOption

            with(wakeWordPorcupineConfigurationData) {
                ConfigurationSetting.wakeWordPorcupineAudioRecorderSettings.value = isUseAudioRecorderSettings
                ConfigurationSetting.wakeWordPorcupineAccessToken.value = accessToken
                ConfigurationSetting.wakeWordPorcupineLanguage.value = porcupineLanguage
                ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value = defaultOptions
                ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value = customOptions.updateList { removeAll(deletedCustomOptions) }
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
        _viewState.update { it.copy(editData = WakeWordConfigurationData()) }
    }

    override fun onDiscard() {
        newFiles.forEach {
            Path.commonInternalPath(get(), "${FolderType.PorcupineFolder}/$it").commonDelete()
        }
        newFiles.clear()
        filesToDelete.clear()
        _viewState.update { it.copy(editData = WakeWordConfigurationData()) }
    }

    override fun onBackPressed(): Boolean {
        return if (viewState.value.screen == OverviewScreen) {
            super.onBackPressed()
        } else if (viewState.value.porcupineWakeWordScreen == DefaultKeywordScreen) {
            //pop backstack to remove DefaultKeywordScreen
            navigator.popBackStack()
            //was handled
            true
        } else if (viewState.value.porcupineWakeWordScreen == CustomKeywordScreen) {
            //navigate to DefaultKeywordScreen
            navigator.replace(
                PorcupineKeywordConfigurationScreenDestination::class,
                DefaultKeywordScreen
            )
            //was handled
            true
        } else {
            //close porcupine language or keyword screen
            false
        }
    }

}