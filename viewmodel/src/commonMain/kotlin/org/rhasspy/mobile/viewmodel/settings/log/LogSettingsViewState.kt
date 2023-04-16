package org.rhasspy.mobile.viewmodel.settings.log

import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.logic.logger.LogLevel
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.toImmutableList

data class LogSettingsViewState(
    val logLevel: LogLevel = AppSetting.logLevel.value,
    val isCrashlyticsEnabled: Boolean = AppSetting.isCrashlyticsEnabled.value,
    val isShowLogEnabled: Boolean = AppSetting.isShowLogEnabled.value,
    val isLogAudioFramesEnabled: Boolean = AppSetting.isLogAudioFramesEnabled.value
) {

    val logLevelOptions: ImmutableList<LogLevel> get() = LogLevel.values().toImmutableList()

}