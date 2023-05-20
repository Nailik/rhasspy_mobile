package org.rhasspy.mobile.viewmodel.configuration

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.log.LogElement

@Stable
data class ConfigurationTestViewState internal constructor(
    val isListFiltered: Boolean,
    val isListAutoscroll: Boolean,
    val logEvents: StateFlow<ImmutableList<LogElement>>
)