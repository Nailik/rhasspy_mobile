package org.rhasspy.mobile.viewmodel.settings.log

import org.rhasspy.mobile.logic.logger.LogLevel

sealed interface LogSettingsUiEvent {

    sealed interface Change: LogSettingsUiEvent{
        data class SetLogLevel(val logLevel: LogLevel): Change
        data class SetCrashlyticsEnabled(val enabled: Boolean): Change
        data class SetShowLogEnabled(val enabled: Boolean): Change
        data class SetLogAudioFramesEnabled(val enabled: Boolean): Change
    }

}