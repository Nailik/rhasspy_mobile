package org.rhasspy.mobile.viewmodel.configuration.handle

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.handle.HandleDomainConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.handle.HandleDomainConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
class HandleDomainConfigurationViewModel(
    private val mapper: HandleDomainConfigurationDataMapper,
) : ScreenViewModel() {

    private val _viewState = MutableStateFlow(HandleDomainConfigurationViewState(mapper(ConfigurationSetting.handleDomainData.value)))
    val viewState = _viewState.readOnly

    fun onEvent(event: HandleDomainConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SelectHandleDomainHomeAssistantOption       -> copy(intentHandlingHomeAssistantOption = change.option)
                    is SelectHandleDomainOption                    -> copy(handleDomainOption = change.option)
                    is UpdateHandleDomainHomeAssistantEventTimeout -> copy(homeAssistantEventTimeout = change.timeout)
                }
            })
        }
        ConfigurationSetting.handleDomainData.value = mapper(_viewState.value.editData)
    }

}