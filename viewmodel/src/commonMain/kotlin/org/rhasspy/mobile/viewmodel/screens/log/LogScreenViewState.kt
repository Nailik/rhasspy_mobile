package org.rhasspy.mobile.viewmodel.screens.log

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.log.LogElement

@Stable
data class LogScreenViewState internal constructor(
    val isLogAutoscroll: Boolean,
    val logList: ImmutableList<LogElement>
)