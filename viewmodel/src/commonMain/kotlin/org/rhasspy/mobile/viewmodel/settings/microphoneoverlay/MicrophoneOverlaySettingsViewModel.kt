package org.rhasspy.mobile.viewmodel.settings.microphoneoverlay

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsUiEvent.Action
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsUiEvent.Change.SelectMicrophoneOverlaySizeOption
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsUiEvent.Change.SetMicrophoneOverlayWhileAppEnabled

@Stable
class MicrophoneOverlaySettingsViewModel(
    viewStateCreator: MicrophoneOverlaySettingsViewStateCreator
) : KViewModel() {

    val viewState = viewStateCreator()

    fun onEvent(event: MicrophoneOverlaySettingsUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            is SelectMicrophoneOverlaySizeOption ->
                if (change.option != MicrophoneOverlaySizeOption.Disabled) {
                    requireOverlayPermission {
                        AppSetting.microphoneOverlaySizeOption.value = change.option
                    }
                } else {
                    AppSetting.microphoneOverlaySizeOption.value = change.option
                }

            is SetMicrophoneOverlayWhileAppEnabled -> AppSetting.isMicrophoneOverlayWhileAppEnabled.value = change.enabled
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            is BackClick -> navigator.onBackPressed()
        }
    }

}