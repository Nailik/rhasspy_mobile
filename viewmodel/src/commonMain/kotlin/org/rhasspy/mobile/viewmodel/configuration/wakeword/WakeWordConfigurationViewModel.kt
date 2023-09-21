package org.rhasspy.mobile.viewmodel.configuration.wakeword

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.Path
import org.koin.core.component.get
import org.koin.core.component.inject
import org.rhasspy.mobile.data.data.toIntOrNullOrConstant
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.domains.wake.IWakeDomain
import org.rhasspy.mobile.platformspecific.*
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.extensions.commonInternalFilePath
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
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.WakeWordConfigurationScreenDestination
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.WakeWordConfigurationScreenDestination.EditPorcupineLanguageScreen

@Stable
class WakeWordConfigurationViewModel(
    private val mapper: WakeWordConfigurationDataMapper,
    microphonePermission: IMicrophonePermission,
    service: IWakeDomain
) : ConfigurationViewModel(
    serviceState = service.serviceState
) {

    private val dispatcher by inject<IDispatcherProvider>()

    private val initialData get() = mapper(ConfigurationSetting.wakeDomainData.value)
    private val _viewState = MutableStateFlow(
        WakeWordConfigurationViewState(
            editData = initialData,
            porcupineWakeWordScreen = 0,
            isMicrophonePermissionRequestVisible = !microphonePermission.granted.value && (initialData.wakeWordOption == WakeWordOption.Porcupine || initialData.wakeWordOption == WakeWordOption.Udp),
        )
    )
    val viewState = _viewState.readOnly

    override fun initViewStateCreator(
        configurationViewState: MutableStateFlow<ConfigurationViewState>
    ): StateFlow<ConfigurationViewState> {
        return viewStateCreator(
            init = ::initialData,
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
            is Change           -> onChange(event)
            is Action           -> onAction(event)
            is PorcupineUiEvent -> onPorcupineAction(event)
            is UdpUiEvent       -> onUdpAction(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            when (change) {
                is SelectWakeWordOption -> it.copy(editData = with(it.editData) { copy(wakeWordOption = change.option) })
            }
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            RequestMicrophonePermission -> requireMicrophonePermission {}
            BackClick                   -> navigator.onBackPressed()
            is Navigate                 -> navigator.navigate(action.destination)
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
        ConfigurationSetting.wakeDomainData.value = mapper(_viewState.value.editData)
        filesToDelete.forEach {
            Path.commonInternalFilePath(get(), "${FolderType.PorcupineFolder}/$it").commonDelete()
        }
        filesToDelete.clear()
        newFiles.clear()
        _viewState.update { it.copy(editData = initialData) }
    }

    override fun onDiscard() {
        newFiles.forEach {
            Path.commonInternalFilePath(get(), "${FolderType.PorcupineFolder}/$it").commonDelete()
        }
        newFiles.clear()
        filesToDelete.clear()
        _viewState.update { it.copy(editData = initialData) }
    }

    override fun onBackPressed(): Boolean {
        return when (navigator.topScreen.value) {
            //do navigate sub screens back even if there are changes
            is WakeWordConfigurationScreenDestination -> false
            else                                      -> super.onBackPressed()
        }
    }

}