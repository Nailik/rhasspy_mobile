package org.rhasspy.mobile.viewmodel.configuration.audioinput.audiooutputformat

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audiooutputformat.AudioOutputFormatConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audiooutputformat.AudioOutputFormatConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audiooutputformat.AudioOutputFormatConfigurationUiEvent.Click
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audiooutputformat.AudioOutputFormatConfigurationUiEvent.Click.BackClick

class AudioOutputFormatConfigurationViewModel(
    private val mapper: AudioOutputFormatConfigurationDataMapper,
) : ConfigurationViewModel(
    serviceState = MutableStateFlow(ServiceState.Success) //TODO
) {

    private val initialData get() = mapper(ConfigurationSetting.audioInputDomainData.value)
    private val _viewState = MutableStateFlow(AudioOutputFormatConfigurationViewState(initialData))
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

    fun onEvent(event: AudioOutputFormatConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Click  -> onClick(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SelectOutputFormatChannelType    -> copy(audioOutputChannel = change.value)
                    is SelectOutputFormatEncodingType   -> copy(audioOutputEncoding = change.value)
                    is SelectOutputFormatSampleRateType -> copy(audioOutputSampleRate = change.value)
                }
            })
        }

    }

    private fun onClick(click: Click) {
        when (click) {
            BackClick -> navigator.onBackPressed()
        }
    }

    override fun onDiscard() {
        _viewState.update { it.copy(editData = initialData) }
    }

    override fun onSave() {
        ConfigurationSetting.audioInputDomainData.value = mapper(_viewState.value.editData)
        _viewState.update { it.copy(editData = initialData) }
    }


}