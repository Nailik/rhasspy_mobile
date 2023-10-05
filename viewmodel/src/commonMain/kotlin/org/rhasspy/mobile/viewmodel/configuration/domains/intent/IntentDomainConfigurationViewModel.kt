package org.rhasspy.mobile.viewmodel.configuration.domains.intent

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.data.takeInt
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.domains.intent.IntentDomainConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.domains.intent.IntentDomainConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
class IntentDomainConfigurationViewModel(
    private val mapper: IntentDomainConfigurationDataMapper,
) : ScreenViewModel() {

    private val _viewState = MutableStateFlow(IntentDomainConfigurationViewState(mapper(ConfigurationSetting.intentDomainData.value)))
    val viewState = _viewState.readOnly

    fun onEvent(event: IntentDomainConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SelectIntentDomainOption -> copy(intentDomainOption = change.option)
                    is SetRhasspy2HttpIntentIntentHandlingEnabled -> copy(isRhasspy2HermesHttpIntentHandleWithRecognition = change.enabled)
                    is UpdateRhasspy2HttpIntentHandlingTimeout    -> copy(rhasspy2HermesHttpIntentHandlingTimeout = change.timeout.takeInt())
                    is UpdateVoiceTimeout                         -> copy(timeout = change.timeout.takeInt())
                }
            })
        }
        ConfigurationSetting.intentDomainData.value = mapper(_viewState.value.editData)
    }

    override fun onVisible() {
        _viewState.value = IntentDomainConfigurationViewState(mapper(ConfigurationSetting.intentDomainData.value))
        super.onVisible()
    }

}