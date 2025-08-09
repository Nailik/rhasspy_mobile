package org.rhasspy.mobile.viewmodel.settings.log

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.log.LogLevel

@Stable
data class LogSettingsViewState(
    val logLevel: LogLevel,
    val isCrashlyticsEnabled: Boolean,
    val isShowLogEnabled: Boolean,
    val isLogAudioFramesEnabled: Boolean,
) {

    val logLevelOptions: ImmutableList<LogLevel> = LogLevel.entries.toImmutableList()

}