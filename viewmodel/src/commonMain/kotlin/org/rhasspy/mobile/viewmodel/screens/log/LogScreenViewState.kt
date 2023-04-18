package org.rhasspy.mobile.viewmodel.screens.log

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.rhasspy.mobile.data.log.LogElement
import org.rhasspy.mobile.logic.settings.AppSetting

data class LogScreenViewState internal constructor(
    val isLogAutoscroll: Boolean = AppSetting.isLogAutoscroll.value,
    val logList: ImmutableList<LogElement> = persistentListOf()
)