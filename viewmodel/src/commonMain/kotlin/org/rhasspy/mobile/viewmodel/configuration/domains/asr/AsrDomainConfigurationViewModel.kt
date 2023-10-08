package org.rhasspy.mobile.viewmodel.configuration.domains.asr

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.data.takeInt
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.domains.asr.AsrDomainConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.domains.asr.AsrDomainConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
class AsrDomainConfigurationViewModel(
    private val mapper: AsrDomainConfigurationDataMapper,
) : ScreenViewModel() {

    private val _viewState = MutableStateFlow(AsrDomainConfigurationViewState(mapper(ConfigurationSetting.asrDomainData.value)))
    val viewState = _viewState.readOnly

    fun onEvent(event: AsrDomainConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SelectAsrOptionDomain               -> copy(asrDomainOption = change.option)
                    is SetUseAsrMqttSilenceDetectionDomain -> copy(isUseSpeechToTextMqttSilenceDetection = change.enabled)
                    is UpdateMqttResultTimeout             -> copy(mqttResultTimeout = change.timeout.takeInt())
                }
            })
        }
        ConfigurationSetting.asrDomainData.value = mapper(_viewState.value.editData)
    }

    override fun onVisible() {
        _viewState.value = AsrDomainConfigurationViewState(mapper(ConfigurationSetting.asrDomainData.value))
        super.onVisible()
    }
}