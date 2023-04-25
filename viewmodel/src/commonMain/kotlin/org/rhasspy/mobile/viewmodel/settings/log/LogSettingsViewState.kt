package org.rhasspy.mobile.viewmodel.settings.log

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.log.LogLevel
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.settings.AppSetting

@Stable
data class LogSettingsViewState internal constructor(
    val logLevel: LogLevel = AppSetting.logLevel.value,
    val isCrashlyticsEnabled: Boolean = AppSetting.isCrashlyticsEnabled.value,
    val isShowLogEnabled: Boolean = AppSetting.isShowLogEnabled.value,
    val isLogAudioFramesEnabled: Boolean = AppSetting.isLogAudioFramesEnabled.value
) {

    val logLevelOptions: ImmutableList<LogLevel> = LogLevel.values().toImmutableList()

}