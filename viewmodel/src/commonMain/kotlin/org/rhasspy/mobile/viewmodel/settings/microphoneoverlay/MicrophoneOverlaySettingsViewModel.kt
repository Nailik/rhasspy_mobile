package org.rhasspy.mobile.viewmodel.settings.microphoneoverlay

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.updateViewStateFlow
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsUiEvent.Change.SetMicrophoneOverlaySizeOption
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsUiEvent.Change.SetMicrophoneOverlayWhileAppEnabled

@Stable
class MicrophoneOverlaySettingsViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(MicrophoneOverlaySettingsViewState())
    val viewState = _viewState.readOnly

    fun onEvent(event: MicrophoneOverlaySettingsUiEvent) {
        when (event) {
            is Change -> onChange(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.updateViewStateFlow {
            when (change) {
                is SetMicrophoneOverlaySizeOption -> {
                    AppSetting.microphoneOverlaySizeOption.value = change.option
                    copy(microphoneOverlaySizeOption = change.option)
                }

                is SetMicrophoneOverlayWhileAppEnabled -> {
                    AppSetting.isMicrophoneOverlayWhileAppEnabled.value = change.enabled
                    copy(isMicrophoneOverlayWhileAppEnabled = change.enabled)
                }
            }
        }
    }

}