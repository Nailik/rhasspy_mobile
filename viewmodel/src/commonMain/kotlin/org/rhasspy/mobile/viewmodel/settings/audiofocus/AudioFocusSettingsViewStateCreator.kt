package org.rhasspy.mobile.viewmodel.settings.audiofocus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.settings.AppSetting

class AudioFocusSettingsViewStateCreator {

    private val updaterScope = CoroutineScope(Dispatchers.Default)

    operator fun invoke(): StateFlow<AudioFocusSettingsViewState> {
        val viewState = MutableStateFlow(getViewState())

        updaterScope.launch {
            combineStateFlow(
                AppSetting.audioFocusOption.data,
                AppSetting.isAudioFocusOnNotification.data,
                AppSetting.isAudioFocusOnSound.data,
                AppSetting.isAudioFocusOnRecord.data,
                AppSetting.isAudioFocusOnDialog.data
            ).collect {
                viewState.value = getViewState()
            }
        }

        return viewState
    }

    private fun getViewState(): AudioFocusSettingsViewState {
        return AudioFocusSettingsViewState(
            audioFocusOption = AppSetting.audioFocusOption.value,
            isAudioFocusOnNotification = AppSetting.isAudioFocusOnNotification.value,
            isAudioFocusOnSound = AppSetting.isAudioFocusOnSound.value,
            isAudioFocusOnRecord = AppSetting.isAudioFocusOnRecord.value,
            isAudioFocusOnDialog = AppSetting.isAudioFocusOnDialog.value
        )
    }

}