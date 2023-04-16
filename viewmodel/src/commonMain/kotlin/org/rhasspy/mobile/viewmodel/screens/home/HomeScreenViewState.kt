package org.rhasspy.mobile.viewmodel.screens.home

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.mapReadonlyState

@Stable
data class HomeScreenViewState(
    val isMicrophonePermissionRequired: StateFlow<Boolean>,
    val isPlayingRecording: StateFlow<Boolean>,
    val isPlayingRecordingEnabled: StateFlow<Boolean>,
    val isShowLogEnabled: StateFlow<Boolean>
) {

    companion object : KoinComponent {

        fun getInitialViewState(serviceMiddleware: ServiceMiddleware): HomeScreenViewState {
            return HomeScreenViewState(
                isMicrophonePermissionRequired = ConfigurationSetting.wakeWordOption.data
                    .mapReadonlyState { it == WakeWordOption.Porcupine || it == WakeWordOption.Udp },
                isPlayingRecording = serviceMiddleware.isPlayingRecording,
                isPlayingRecordingEnabled = serviceMiddleware.isPlayingRecordingEnabled,
                isShowLogEnabled = AppSetting.isShowLogEnabled.data
            )
        }

    }

}