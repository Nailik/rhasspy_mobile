package org.rhasspy.mobile.viewmodel.configuration.asr

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.data.toIntOrZero
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.asr.AsrConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.asr.AsrConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import kotlin.time.Duration.Companion.seconds

@Stable
class AsrConfigurationViewModel(
    private val mapper: AsrConfigurationDataMapper,
) : ScreenViewModel() {

    private val _viewState = MutableStateFlow(AsrConfigurationViewState(mapper(ConfigurationSetting.asrDomainData.value)))
    val viewState = _viewState.readOnly

    fun onEvent(event: AsrConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SelectAsrOption               -> copy(asrDomainOption = change.option)
                    is SetUseAsrMqttSilenceDetection -> copy(isUseSpeechToTextMqttSilenceDetection = change.enabled)
                    is UpdateMqttResultTimeout       -> copy(mqttResultTimeout = change.timeout.toIntOrZero().seconds)
                    is UpdateVoiceTimeout            -> copy(voiceTimeout = change.timeout.toIntOrZero().seconds)
                }
            })
        }
        ConfigurationSetting.asrDomainData.value = mapper(_viewState.value.editData)
    }
}