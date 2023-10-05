package org.rhasspy.mobile.viewmodel.configuration.domains.mic.audiooutputformat

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.platformspecific.features.FeatureAvailability
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.domains.mic.audiooutputformat.AudioOutputFormatConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.domains.mic.audiooutputformat.AudioOutputFormatConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

class AudioOutputFormatConfigurationViewModel(
    private val mapper: AudioOutputFormatConfigurationDataMapper,
) : ScreenViewModel() {

    private val _viewState = MutableStateFlow(
        AudioOutputFormatConfigurationViewState(
            editData = mapper(ConfigurationSetting.micDomainData.value),
            isEncodingChangeEnabled = FeatureAvailability.isAudioEncodingOutputChangeEnabled,
        )
    )
    val viewState = _viewState.readOnly

    override fun onVisible() {
        super.onVisible()
        _viewState.update {
            it.copy(editData = mapper(ConfigurationSetting.micDomainData.value))
        }
    }

    fun onEvent(event: AudioOutputFormatConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
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
        ConfigurationSetting.micDomainData.value = mapper(_viewState.value.editData)
    }

}