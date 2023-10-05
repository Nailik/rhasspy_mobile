package org.rhasspy.mobile.viewmodel.configuration.domains.tts

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.data.takeInt
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.domains.tts.TtsDomainConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.domains.tts.TtsDomainConfigurationUiEvent.Change.SelectTtsDomainOption
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
class TtsDomainConfigurationViewModel(
    private val mapper: TtsDomainConfigurationDataMapper,
) : ScreenViewModel() {

    private val _viewState = MutableStateFlow(TtsDomainConfigurationViewState(mapper(ConfigurationSetting.ttsDomainData.value)))
    val viewState = _viewState.readOnly

    fun onEvent(event: TtsDomainConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SelectTtsDomainOption                  -> copy(ttsDomainOption = change.option)
                    is Change.UpdateRhasspy2HermesMqttTimeout -> copy(rhasspy2HermesMqttTimeout = change.timeout.takeInt())
                }
            })
        }
        ConfigurationSetting.ttsDomainData.value = mapper(_viewState.value.editData)
    }

}