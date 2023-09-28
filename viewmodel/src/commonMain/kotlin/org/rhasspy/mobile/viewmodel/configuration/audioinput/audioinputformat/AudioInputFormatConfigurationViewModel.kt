package org.rhasspy.mobile.viewmodel.configuration.audioinput.audioinputformat

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.service.ConnectionState
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audioinputformat.AudioInputFormatConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audioinputformat.AudioInputFormatConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audioinputformat.AudioInputFormatConfigurationUiEvent.Click
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audioinputformat.AudioInputFormatConfigurationUiEvent.Click.BackClick

class AudioInputFormatConfigurationViewModel(
    private val mapper: AudioInputFormatConfigurationDataMapper,
) : ConfigurationViewModel(
    connectionState = MutableStateFlow(ConnectionState.Success) //TODO
) {

    private val initialData get() = mapper(ConfigurationSetting.micDomainData.value)
    private val _viewState = MutableStateFlow(AudioInputFormatConfigurationViewState(initialData))
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

    fun onEvent(event: AudioInputFormatConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Click  -> onClick(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SelectInputFormatChannelType    -> copy(audioInputChannel = change.value)
                    is SelectInputFormatEncodingType   -> copy(audioInputEncoding = change.value)
                    is SelectInputFormatSampleRateType -> copy(audioInputSampleRate = change.value)
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
        ConfigurationSetting.micDomainData.value = mapper(_viewState.value.editData)
        _viewState.update { it.copy(editData = initialData) }
    }


}