package org.rhasspy.mobile.viewmodel.configuration.mic

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.data.toIntOrZero
import org.rhasspy.mobile.logic.pipeline.IPipelineManager
import org.rhasspy.mobile.platformspecific.features.FeatureAvailability
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.mic.MicDomainConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.mic.MicDomainConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.mic.MicDomainConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.mic.MicDomainConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.AudioInputDomainScreenDestination.AudioInputFormatScreen
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.AudioInputDomainScreenDestination.AudioOutputFormatScreen
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
class MicDomainConfigurationViewModel(
    pipelineManager: IPipelineManager,
    private val mapper: MicDomainConfigurationDataMapper,
) : ScreenViewModel() {

    private val _viewState = MutableStateFlow(
        MicDomainConfigurationViewState(
            editData = mapper(ConfigurationSetting.micDomainData.value),
            micDomainStateFlow = pipelineManager.micDomainStateFlow,
            domainStateFlow = pipelineManager.micDomainStateFlow.mapReadonlyState { it.asDomainState() },
            isPauseRecordingOnMediaPlaybackEnabled = FeatureAvailability.isPauseRecordingOnPlaybackFeatureEnabled,
        )
    )
    val viewState = _viewState.readOnly

    override fun onVisible() {
        super.onVisible()
        _viewState.update {
            it.copy(editData = mapper(ConfigurationSetting.micDomainData.value))
        }
    }

    fun onEvent(event: MicDomainConfigurationUiEvent) {
        when (event) {
            is Action -> onAction(event)
            is Change -> onChange(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            OpenInputFormatConfigurationScreen  -> navigator.navigate(AudioInputFormatScreen)
            OpenOutputFormatConfigurationScreen -> navigator.navigate(AudioOutputFormatScreen)
            RequestMicrophonePermission         -> requireMicrophonePermission { }
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is SetUseLoudnessEnhancer     -> copy(isUseLoudnessEnhancer = change.value)
                    is SetUsePauseOnMediaPlayback -> copy(isPauseRecordingOnMediaPlayback = change.value)
                    is UpdateGain                 -> copy(gainControl = change.value.toIntOrZero())
                }
            })
        }
        ConfigurationSetting.micDomainData.value = mapper(_viewState.value.editData)
    }

}