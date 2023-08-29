package org.rhasspy.mobile.viewmodel.configuration.wakeword

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.Path
import org.koin.core.component.get
import org.koin.core.component.inject
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.domains.wakeword.IWakeWordService
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
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.AudioOutputFormatUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.AudioRecorderFormatUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.Change.SelectWakeWordOption
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.PorcupineUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputHost
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputPort
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.WakeWordConfigurationScreenDestination.*

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
            porcupineWakeWordScreen = 0,
            isMicrophonePermissionRequestVisible = !microphonePermission.granted.value && (initialData.wakeWordOption == WakeWordOption.Porcupine || initialData.wakeWordOption == WakeWordOption.Udp),
            isRecorderEncodingChangeEnabled = initialData.wakeWordOption != WakeWordOption.Porcupine, //TODO #408
            isOutputEncodingChangeEnabled = false //TODO #408
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
                microphonePermission.granted,
                viewState
            ).collect {
                _viewState.update {
                    it.copy(
                        isMicrophonePermissionRequestVisible = !microphonePermission.granted.value
                                && (it.editData.wakeWordOption == WakeWordOption.Porcupine || it.editData.wakeWordOption == WakeWordOption.Udp),
                    )
                }
            }
        }
    }

    fun onEvent(event: WakeWordConfigurationUiEvent) {
        when (event) {
            is Change                     -> onChange(event)
            is Action                     -> onAction(event)
            is PorcupineUiEvent           -> onPorcupineAction(event)
            is UdpUiEvent                 -> onUdpAction(event)
            is AudioOutputFormatUiEvent   -> onAudioOutputFormatEvent(event)
            is AudioRecorderFormatUiEvent -> onAudioRecorderFormatEvent(event)
        }
    }

    private fun onAudioRecorderFormatEvent(event: AudioRecorderFormatUiEvent) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                copy(
                    wakeWordAudioRecorderData =
                    when (event) {
                        is SelectAudioRecorderChannelType    -> wakeWordAudioRecorderData.copy(audioRecorderChannelType = event.value)
                        is SelectAudioRecorderEncodingType   -> wakeWordAudioRecorderData.copy(audioRecorderEncodingType = event.value)
                        is SelectAudioRecorderSampleRateType -> wakeWordAudioRecorderData.copy(audioRecorderSampleRateType = event.value)
                    },
                    //TODO #408
                    wakeWordAudioOutputData = when (event) {
                        is SelectAudioRecorderEncodingType -> wakeWordAudioOutputData.copy(audioOutputEncodingType = event.value)
                        else                               -> wakeWordAudioOutputData
                    }
                )
            })
        }
    }

    private fun onAudioOutputFormatEvent(event: AudioOutputFormatUiEvent) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                copy(wakeWordAudioOutputData = wakeWordAudioOutputData.let { data ->
                    when (event) {
                        is SelectAudioOutputChannelType    -> data.copy(audioOutputChannelType = event.value)
                        is SelectAudioOutputEncodingType   -> data.copy(audioOutputEncodingType = event.value)
                        is SelectAudioOutputSampleRateType -> data.copy(audioOutputSampleRateType = event.value)
                    }
                })
            })
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            when (change) {
                is SelectWakeWordOption -> {
                    it.copy(
                        isRecorderEncodingChangeEnabled = change.option != WakeWordOption.Porcupine,
                        editData = with(it.editData) {
                            copy(
                                wakeWordOption = change.option,
                                wakeWordAudioRecorderData = wakeWordAudioRecorderData.copy( //TODO #408
                                    audioRecorderEncodingType = if (change.option == WakeWordOption.Porcupine) AudioFormatEncodingType.porcupine else wakeWordAudioRecorderData.audioRecorderEncodingType
                                )
                            )
                        }
                    )
                }
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            RequestMicrophonePermission -> requireMicrophonePermission {}
            BackClick                   -> navigator.onBackPressed()
            is Navigate                 -> navigator.navigate(action.destination)
            OpenAudioOutputFormat       -> navigator.navigate(AudioOutputFormatScreen)
            OpenAudioRecorderFormat     -> navigator.navigate(AudioRecorderFormatScreen)
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
                                    sensitivity = 0.5
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
                ConfigurationSetting.wakeWordPorcupineAccessToken.value = accessToken
                ConfigurationSetting.wakeWordPorcupineLanguage.value = porcupineLanguage
                ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value = defaultOptions
                ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value = customOptions.updateList { removeAll(deletedCustomOptions) }
            }

            with(wakeWordUdpConfigurationData) {
                ConfigurationSetting.wakeWordUdpOutputHost.value = outputHost
                ConfigurationSetting.wakeWordUdpOutputPort.value = outputPort.toIntOrZero()
            }

            with(wakeWordAudioRecorderData) {
                ConfigurationSetting.wakeWordAudioRecorderChannel.value = audioRecorderChannelType
                ConfigurationSetting.wakeWordAudioRecorderEncoding.value = audioRecorderEncodingType
                ConfigurationSetting.wakeWordAudioRecorderSampleRate.value = audioRecorderSampleRateType
            }

            with(wakeWordAudioOutputData) {
                ConfigurationSetting.wakeWordAudioOutputChannel.value = audioOutputChannelType
                ConfigurationSetting.wakeWordAudioOutputEncoding.value = audioOutputEncodingType
                ConfigurationSetting.wakeWordAudioOutputSampleRate.value = audioOutputSampleRateType
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

}