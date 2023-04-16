package org.rhasspy.mobile.viewmodel.screens.home

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.logic.middleware.Action
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.settings.AppSetting

class HomeScreenViewModel : ViewModel(), KoinComponent {

    private val serviceMiddleware by inject<ServiceMiddleware>()

    val isPlayingRecording = serviceMiddleware.isPlayingRecording
    val isPlayingRecordingEnabled = serviceMiddleware.isPlayingRecordingEnabled
    val isShowLogEnabled = AppSetting.isShowLogEnabled.data

    var isStartRecordingAction: Boolean = false
        private set

    fun togglePlayRecording() = serviceMiddleware.action(Action.PlayStopRecording)

    fun startRecordingAction(value: Boolean) {
        isStartRecordingAction = value
    }

    fun consumedStartRecordingAction() {
        isStartRecordingAction = false
    }

}