package org.rhasspy.mobile.viewmodel.settings.log

import androidx.compose.runtime.Stable
import co.touchlab.kermit.Logger
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.firebase.setCrashlyticsCollectionEnabled
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsUiEvent.Action
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.log.LogSettingsUiEvent.Change.*

@Stable
class LogSettingsViewModel(
    val viewStateCreator: LogSettingsViewStateCreator,
    private val nativeApplication: NativeApplication
) : ScreenViewModel() {

    val viewState = viewStateCreator()

    fun onEvent(event: LogSettingsUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            is SetCrashlyticsEnabled -> {
                AppSetting.isCrashlyticsEnabled.value = change.enabled
                setCrashlyticsCollectionEnabled(change.enabled)
            }

            is SetLogAudioFramesEnabled -> AppSetting.isLogAudioFramesEnabled.value = change.enabled

            is SetLogLevel -> {
                AppSetting.logLevel.value = change.logLevel
                Logger.setMinSeverity(AppSetting.logLevel.value.severity)
            }

            is SetShowLogEnabled -> AppSetting.isShowLogEnabled.value = change.enabled
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            is BackClick -> navigator.onBackPressed()
        }
    }

}