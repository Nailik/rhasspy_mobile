package org.rhasspy.mobile.viewmodel.configuration.pipeline

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationUiEvent.*
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationUiEvent.Action.Navigate
import org.rhasspy.mobile.viewmodel.configuration.pipeline.PipelineConfigurationUiEvent.Change.SelectPipelineOption
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

/**
 * ViewModel for Dialog Management Configuration
 *
 * Current Option
 * all Options as list
 */
@Stable
class PipelineConfigurationViewModel(
    private val mapper: PipelineConfigurationDataMapper,
) : ScreenViewModel() {

    private val _viewState = MutableStateFlow(PipelineConfigurationViewState(mapper(ConfigurationSetting.pipelineData.value)))
    val viewState = _viewState.readOnly

    fun onEvent(event: PipelineConfigurationUiEvent) {
        when (event) {
            is Change               -> onChange(event)
            is Action               -> onAction(event)
            is PipelineLocalUiEvent -> onPipelineLocalEvent(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SelectPipelineOption -> copy(pipelineManagerOption = change.option)
                }
            })
        }
        ConfigurationSetting.pipelineData.value = mapper(_viewState.value.editData)
    }

    private fun onAction(action: Action) {
        when (action) {
            is Navigate -> navigator.navigate(action.destination)
        }
    }

    private fun onPipelineLocalEvent(event: PipelineLocalUiEvent) {
        when (event) {
            is PipelineLocalUiEvent.Change -> onPipelineLocalChange(event)
        }
    }

    private fun onPipelineLocalChange(change: PipelineLocalUiEvent.Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                copy(pipelineLocalConfigurationData = with(pipelineLocalConfigurationData) {
                    when (change) {
                        is PipelineLocalUiEvent.Change.SelectSoundIndicationOutputOption -> copy(soundIndicationOutputOption = change.option)
                        is PipelineLocalUiEvent.Change.SetSoundIndicationEnabled         -> copy(isSoundIndicationEnabled = change.enabled)
                    }
                })
            })
        }
        ConfigurationSetting.pipelineData.value = mapper(_viewState.value.editData)
    }

    override fun onVisible() {
        super.onVisible()
        _viewState.value = PipelineConfigurationViewState(mapper(ConfigurationSetting.pipelineData.value))
    }

}