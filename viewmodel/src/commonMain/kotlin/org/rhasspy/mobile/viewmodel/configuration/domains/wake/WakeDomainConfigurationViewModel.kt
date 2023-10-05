package org.rhasspy.mobile.viewmodel.configuration.domains.wake

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import okio.Path
import org.koin.core.component.get
import org.rhasspy.mobile.data.data.takeInt
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.logic.pipeline.IPipelineManager
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.extensions.commonInternalFilePath
import org.rhasspy.mobile.platformspecific.file.FolderType
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.updateList
import org.rhasspy.mobile.platformspecific.updateListItem
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.domains.wake.WakeDomainConfigurationUiEvent.*
import org.rhasspy.mobile.viewmodel.configuration.domains.wake.WakeDomainConfigurationUiEvent.Action.Navigate
import org.rhasspy.mobile.viewmodel.configuration.domains.wake.WakeDomainConfigurationUiEvent.Change.SelectWakeDomainOption
import org.rhasspy.mobile.viewmodel.configuration.domains.wake.WakeDomainConfigurationUiEvent.PorcupineUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.domains.wake.WakeDomainConfigurationUiEvent.PorcupineUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.domains.wake.WakeDomainConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputHost
import org.rhasspy.mobile.viewmodel.configuration.domains.wake.WakeDomainConfigurationUiEvent.UdpUiEvent.Change.UpdateUdpOutputPort
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.WakeWordConfigurationScreenDestination.EditPorcupineLanguageScreen
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
class WakeDomainConfigurationViewModel(
    pipelineManager: IPipelineManager,
    private val mapper: WakeDomainConfigurationDataMapper,
) : ScreenViewModel() {

    private val initialData = mapper(ConfigurationSetting.wakeDomainData.value)
    private val _viewState = MutableStateFlow(
        WakeDomainConfigurationViewState(
            editData = initialData,
            domainStateFlow = pipelineManager.wakeDomainStateFlow,
            porcupineWakeWordScreen = 0,
        )
    )
    val viewState = _viewState.readOnly

    fun onEvent(event: WakeDomainConfigurationUiEvent) {
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
                is SelectWakeDomainOption -> it.copy(editData = with(it.editData) { copy(wakeDomainOption = change.option) })
            }
        }
        ConfigurationSetting.wakeDomainData.value = mapper(_viewState.value.editData)
    }

    private fun onAction(action: Action) {
        when (action) {
            is Navigate -> navigator.navigate(action.destination)
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
                        is UpdateWakeDomainPorcupineAccessToken               -> copy(accessToken = change.value)
                        is ClickPorcupineKeywordCustom                        -> copy(customOptions = customOptions.updateListItem(change.item) { copy(isEnabled = !isEnabled) })
                        is ClickPorcupineKeywordDefault                       -> copy(defaultOptions = defaultOptions.updateListItem(change.item) { copy(isEnabled = !isEnabled) })
                        is DeletePorcupineKeywordCustom                       -> {
                            Path.commonInternalFilePath(get(), "${FolderType.PorcupineFolder}/$it").commonDelete()
                            copy(customOptions = customOptions.updateList { remove(change.item) })
                        }

                        is SelectWakeDomainPorcupineLanguage                  -> copy(porcupineLanguage = change.option)
                        is SetPorcupineKeywordCustom                          -> copy(customOptions = customOptions.updateListItem(change.item) { copy(isEnabled = change.value) })
                        is SetPorcupineKeywordDefault                         -> copy(defaultOptions = defaultOptions.updateListItem(change.item) { copy(isEnabled = change.value) })
                        is UpdateWakeDomainPorcupineKeywordCustomSensitivity  -> copy(customOptions = customOptions.updateListItem(change.item) { copy(sensitivity = change.value) })
                        is UpdateWakeDomainPorcupineKeywordDefaultSensitivity -> copy(defaultOptions = defaultOptions.updateListItem(change.item) { copy(sensitivity = change.value) })
                        is AddPorcupineKeywordCustom                          -> copy(customOptions = customOptions.updateList {
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
        ConfigurationSetting.wakeDomainData.value = mapper(_viewState.value.editData)
    }

    private fun onPorcupineAction(action: PorcupineUiEvent.Action) {
        when (action) {
            AddCustomPorcupineKeyword      -> addCustomPorcupineKeyword()
            DownloadCustomPorcupineKeyword -> openLink(LinkType.PicoVoiceCustomWakeWord)
            OpenPicoVoiceConsole           -> openLink(LinkType.PicoVoiceConsole)
            PorcupineLanguageClick         -> navigator.navigate(EditPorcupineLanguageScreen)
            is PageClick                   -> _viewState.update { it.copy(porcupineWakeWordScreen = action.screen) }
        }
    }

    private fun addCustomPorcupineKeyword() {
        selectFile(FolderType.PorcupineFolder) { path ->
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
                        is UpdateUdpOutputPort -> data.copy(outputPort = change.value.takeInt())
                    }
                })
            })
        }
        ConfigurationSetting.wakeDomainData.value = mapper(_viewState.value.editData)
    }

}