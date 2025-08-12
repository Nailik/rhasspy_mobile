package org.rhasspy.mobile.viewmodel.screens.log

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.log.LogElement
import org.rhasspy.mobile.data.resource.StableStringResource

@Stable
data class LogScreenViewState(
    val isLogAutoscroll: Boolean,
    val logList: ImmutableList<LogElement>,
    val snackBarText: StableStringResource? = null,
)