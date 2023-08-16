package org.rhasspy.mobile.viewmodel.settings.audiofocus

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.AppSetting

class AudioFocusSettingsViewStateCreator {

    operator fun invoke(): StateFlow<AudioFocusSettingsViewState> {
        return combineStateFlow(
            AppSetting.audioFocusOption.data,
            AppSetting.isAudioFocusOnNotification.data,
            AppSetting.isAudioFocusOnSound.data,
            AppSetting.isAudioFocusOnRecord.data,
            AppSetting.isAudioFocusOnDialog.data,
            AppSetting.isPauseRecordingOnMedia.data,
        ).mapReadonlyState {
            getViewState()
        }
    }

    private fun getViewState(): AudioFocusSettingsViewState {
        return AudioFocusSettingsViewState(
            audioFocusOption = AppSetting.audioFocusOption.value,
            isAudioFocusOnNotification = AppSetting.isAudioFocusOnNotification.value,
            isAudioFocusOnSound = AppSetting.isAudioFocusOnSound.value,
            isAudioFocusOnRecord = AppSetting.isAudioFocusOnRecord.value,
            isAudioFocusOnDialog = AppSetting.isAudioFocusOnDialog.value,
            isPauseRecordingOnMedia = AppSetting.isPauseRecordingOnMedia.value,
        )
    }

}